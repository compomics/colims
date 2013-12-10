package com.compomics.colims.client.controller.admin;

import com.compomics.colims.client.compoment.DualList;
import com.compomics.colims.client.controller.Controllable;
import com.compomics.colims.client.controller.ColimsController;
import com.compomics.colims.client.controller.admin.CvTermManagementController;
import com.compomics.colims.client.event.admin.CvTermChangeEvent;
import com.compomics.colims.client.event.message.DbConstraintMessageEvent;
import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.model.CvTermSummaryListModel;
import com.compomics.colims.client.model.CvTermTableModel;
import com.compomics.colims.client.renderer.CvTermSummaryCellRenderer;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.admin.material.MaterialEditDialog;
import com.compomics.colims.client.view.admin.material.MaterialManagementDialog;
import com.compomics.colims.core.service.CvTermService;
import com.compomics.colims.core.service.MaterialService;
import com.compomics.colims.model.CvTerm;
import com.compomics.colims.model.Material;
import com.compomics.colims.model.MaterialCvTerm;
import com.compomics.colims.model.comparator.CvTermAccessionComparator;
import com.compomics.colims.model.enums.CvTermType;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.hibernate.exception.ConstraintViolationException;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ELProperty;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("materialManagementController")
public class MaterialManagementController implements Controllable {

    //model      
    private CvTermSummaryListModel<MaterialCvTerm> cvTermSummaryListModel;
    private ObservableList<Material> materialBindingList;
    private BindingGroup bindingGroup;
    //view
    private MaterialManagementDialog materialManagementDialog;
    private MaterialEditDialog materialEditDialog;
    //parent controller
    @Autowired
    private ColimsController mainController;
    @Autowired
    private CvTermManagementController cvTermManagementController;
    //services
    @Autowired
    private MaterialService materialService;
    @Autowired
    private CvTermService cvTermService;
    @Autowired
    private EventBus eventBus;

    public MaterialManagementController() {
    }

    public MaterialManagementDialog getMaterialManagementOverviewDialog() {
        return materialManagementDialog;
    }    

    @Override
    public void init() {
        //register to event bus
        eventBus.register(this);

        //init binding
        bindingGroup = new BindingGroup();

        //init views     
        initMaterialManagementDialog();
        initMaterialEditDialog();

        bindingGroup.bind();
    }
    
    @Override
    public void showView() {
        //clear selection
        materialManagementDialog.getMaterialList().getSelectionModel().clearSelection();
        
        materialManagementDialog.setVisible(true);        
    } 
    
    /**
     * Listen to a CV term change event posted by the
     * CvTermManagementController. If the MaterialManagementDialog is visible,
     * clear the selection in the CV term summary list.
     */
    @Subscribe
    public void onCvTermChangeEvent(CvTermChangeEvent cvTermChangeEvent) {
        if (materialEditDialog.isVisible()) {
            materialEditDialog.getCvTermSummaryList().getSelectionModel().clearSelection();
        }
    }

    private void initMaterialManagementDialog() {
        materialManagementDialog = new MaterialManagementDialog(mainController.getColimsFrame(), true);

        //add binding
        materialBindingList = ObservableCollections.observableList(materialService.findAll());
        JListBinding materialListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, materialBindingList, materialManagementDialog.getMaterialList());
        bindingGroup.addBinding(materialListBinding);

        //add action listeners
        materialManagementDialog.getMaterialList().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    if (materialManagementDialog.getMaterialList().getSelectedIndex() != -1) {
                        Material selectedMaterial = materialBindingList.get(materialManagementDialog.getMaterialList().getSelectedIndex());

                        //check if the material has an ID.
                        //If so, change the save button text and the info state label.
                        if (selectedMaterial.getId() != null) {
                            materialManagementDialog.getMaterialStateInfoLabel().setText("");
                        } else {
                            materialManagementDialog.getMaterialStateInfoLabel().setText("This material hasn't been persisted to the database.");
                        }

                        //init CvTermModel
                        List<CvTerm> cvTerms = new ArrayList<>();
                        cvTerms.add(selectedMaterial.getSpecies());
                        cvTerms.add(selectedMaterial.getCellType());
                        cvTerms.add(selectedMaterial.getTissue());
                        cvTerms.add(selectedMaterial.getCompartment());
                        CvTermTableModel cvTermTableModel = new CvTermTableModel(cvTerms);
                        materialManagementDialog.getMaterialDetailsTable().setModel(cvTermTableModel);
                    } else {
                        //clear detail view
                        clearMaterialDetailFields();
                    }
                }
            }
        });

        materialManagementDialog.getAddMaterialButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Material defaultMaterial = createDefaultMaterial();
                materialBindingList.add(defaultMaterial);
                materialManagementDialog.getMaterialList().setSelectedIndex(materialBindingList.size() - 1);
            }
        });

        materialManagementDialog.getDeleteMaterialButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (materialManagementDialog.getMaterialList().getSelectedIndex() != -1) {
                    Material materialToDelete = getSelectedMaterial();
                    //check if the material is already has an id.
                    //If so, delete the material from the db.
                    if (materialToDelete.getId() != null) {
                        try {
                            materialService.delete(materialToDelete);

                            materialBindingList.remove(materialManagementDialog.getMaterialList().getSelectedIndex());
                            materialManagementDialog.getMaterialList().getSelectionModel().clearSelection();
                        } catch (DataIntegrityViolationException dive) {
                            //check if the material can be deleted without breaking existing database relations,
                            //i.e. are there any constraints violations
                            if (dive.getCause() instanceof ConstraintViolationException) {
                                DbConstraintMessageEvent dbConstraintMessageEvent = new DbConstraintMessageEvent("material", materialToDelete.getName());
                                eventBus.post(dbConstraintMessageEvent);
                            } else {
                                //pass the exception
                                throw dive;
                            }
                        }
                    } else {
                        materialBindingList.remove(materialManagementDialog.getMaterialList().getSelectedIndex());
                        materialManagementDialog.getMaterialList().getSelectionModel().clearSelection();
                    }
                }
            }
        });

        materialManagementDialog.getEditMaterialButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (materialManagementDialog.getMaterialList().getSelectedIndex() != -1) {
                    updateMaterialEditDialog(getSelectedMaterial());
                    //show dialog
                    materialEditDialog.setLocationRelativeTo(null);
                    materialEditDialog.setVisible(true);
                }
            }
        });

        materialManagementDialog.getCancelMaterialManagementButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                materialManagementDialog.dispose();
            }
        });

    }

    private void initMaterialEditDialog() {
        materialEditDialog = new MaterialEditDialog(mainController.getColimsFrame(), true);

        //init dual list
        materialEditDialog.getCvTermDualList().init(new CvTermAccessionComparator());
        
        //add binding
        Binding materialNameBinding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, materialManagementDialog.getMaterialList(), ELProperty.create("${selectedElement.name}"), materialEditDialog.getNameTextField(), BeanProperty.create("text"), "materialNameBinding");
        bindingGroup.addBinding(materialNameBinding);
        
        //set model and renderer
        cvTermSummaryListModel = new CvTermSummaryListModel();
        materialEditDialog.getCvTermSummaryList().setModel(cvTermSummaryListModel);
        materialEditDialog.getCvTermSummaryList().setCellRenderer(new CvTermSummaryCellRenderer<MaterialCvTerm>());

        //add action listeners
        materialEditDialog.getCvTermSummaryList().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    if (materialEditDialog.getCvTermSummaryList().getSelectedIndex() != -1) {
                        //get selected cvTermType from summary list                        
                        CvTermType selectedcvTermType = (CvTermType) materialEditDialog.getCvTermSummaryList().getSelectedValue();

                        //load duallist for the selected cvTermType
                        List<MaterialCvTerm> availableCvTerms = cvTermService.findByCvTermByType(MaterialCvTerm.class, selectedcvTermType);

                        List<MaterialCvTerm> addedCvTerms;
                        //@todo for the moment, material has only single CV terms,
                        //so this check is not necessary.
                        if (cvTermSummaryListModel.isSingleCvTerm(selectedcvTermType)) {
                            addedCvTerms = new ArrayList<>();
                            MaterialCvTerm materialCvTerm = cvTermSummaryListModel.getSingleCvTerms().get(selectedcvTermType);
                            //check for null value
                            if (materialCvTerm != null) {
                                addedCvTerms.add(materialCvTerm);
                            }
                            materialEditDialog.getCvTermDualList().populateLists(availableCvTerms, addedCvTerms, 1);
                        } else {
                            addedCvTerms = cvTermSummaryListModel.getMultiCvTerms().get(selectedcvTermType);
                            materialEditDialog.getCvTermDualList().populateLists(availableCvTerms, addedCvTerms);
                        }
                    } else {
                        materialEditDialog.getCvTermDualList().clear();
                    }
                }
            }
        });

        materialEditDialog.getCvTermDualList().addPropertyChangeListener(DualList.CHANGED, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                //get selected cvTermType                        
                CvTermType selectedcvTermType = (CvTermType) materialEditDialog.getCvTermSummaryList().getSelectedValue();

                Material material = getSelectedMaterial();
                List<MaterialCvTerm> addedItems = materialEditDialog.getCvTermDualList().getAddedItems();

                //check for property
                if (selectedcvTermType.equals(CvTermType.SPECIES)) {
                    if (!addedItems.isEmpty()) {
                        MaterialCvTerm species = addedItems.get(0);
                        material.setSpecies(species);
                        cvTermSummaryListModel.updateSingleCvTerm(CvTermType.SPECIES, species);
                    } else {
                        material.setSpecies(null);
                        cvTermSummaryListModel.updateSingleCvTerm(CvTermType.SPECIES, null);
                    }
                } else if (selectedcvTermType.equals(CvTermType.TISSUE)) {
                    if (!addedItems.isEmpty()) {
                        MaterialCvTerm tissue = addedItems.get(0);
                        material.setTissue(tissue);
                        cvTermSummaryListModel.updateSingleCvTerm(CvTermType.TISSUE, tissue);
                    } else {
                        material.setTissue(null);
                        cvTermSummaryListModel.updateSingleCvTerm(CvTermType.TISSUE, null);
                    }
                } else if (selectedcvTermType.equals(CvTermType.CELL_TYPE)) {
                    if (!addedItems.isEmpty()) {
                        MaterialCvTerm cellType = addedItems.get(0);
                        material.setCellType(cellType);
                        cvTermSummaryListModel.updateSingleCvTerm(CvTermType.CELL_TYPE, cellType);
                    } else {
                        material.setCellType(null);
                        cvTermSummaryListModel.updateSingleCvTerm(CvTermType.CELL_TYPE, null);
                    }
                } else if (selectedcvTermType.equals(CvTermType.COMPARTMENT)) {
                    if (!addedItems.isEmpty()) {
                        MaterialCvTerm compartment = addedItems.get(0);
                        material.setCompartment(compartment);
                        cvTermSummaryListModel.updateSingleCvTerm(CvTermType.COMPARTMENT, compartment);
                    } else {
                        material.setCompartment(null);
                        cvTermSummaryListModel.updateSingleCvTerm(CvTermType.COMPARTMENT, null);
                    }
                }

            }
        });

        materialEditDialog.getMaterialSaveOrUpdateButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Material selectedMaterial = getSelectedMaterial();
                //validate material
                List<String> validationMessages = GuiUtils.validateEntity(selectedMaterial);
                //check for a new material if the material name already exists in the db                
                if (selectedMaterial.getId() == null && isExistingMaterialName(selectedMaterial)) {
                    validationMessages.add(selectedMaterial.getName() + " already exists in the database,"
                            + "\n" + "please choose another material name.");
                }
                if (validationMessages.isEmpty()) {
                    if (selectedMaterial.getId() != null) {
                        materialService.update(selectedMaterial);
                    } else {
                        materialService.save(selectedMaterial);
                    }
                    materialEditDialog.getMaterialSaveOrUpdateButton().setText("update");

                    MessageEvent messageEvent = new MessageEvent("material persist confirmation", "Material " + selectedMaterial.getName() + " was persisted successfully!", JOptionPane.INFORMATION_MESSAGE);
                    eventBus.post(messageEvent);

                    //refresh selection in material list in management overview dialog
                    int index = materialManagementDialog.getMaterialList().getSelectedIndex();
                    materialManagementDialog.getMaterialList().getSelectionModel().clearSelection();
                    materialManagementDialog.getMaterialList().setSelectedIndex(index);
                } else {
                    MessageEvent messageEvent = new MessageEvent("validation failure", validationMessages, JOptionPane.WARNING_MESSAGE);
                    eventBus.post(messageEvent);
                }
            }
        });

        materialEditDialog.getCancelMaterialEditButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                materialEditDialog.dispose();
            }
        });

        materialEditDialog.getMaterialCvTermsCrudButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //check if a CV term group is selected in the CV term summary list
                if (materialEditDialog.getCvTermSummaryList().getSelectedIndex() != -1) {
                    //get selected cvTermType from summary list                        
                    CvTermType selectedcvTermType = (CvTermType) materialEditDialog.getCvTermSummaryList().getSelectedValue();

                    List<CvTerm> cvTerms = cvTermService.findByCvTermByType(selectedcvTermType);

                    //update the CV term list
                    cvTermManagementController.updateDialog(selectedcvTermType, cvTerms);

                    cvTermManagementController.getCvTermManagementDialog().setLocationRelativeTo(null);
                    cvTermManagementController.getCvTermManagementDialog().setVisible(true);
                }
            }
        });
    }

    /**
     * Check if a material with the given material name exists in the database.
     *
     * @param material the material
     * @return does the material name exist
     */
    private boolean isExistingMaterialName(Material material) {
        boolean isExistingMaterialName = true;
        Material foundMaterial = materialService.findByName(material.getName());
        if (foundMaterial == null) {
            isExistingMaterialName = false;
        }

        return isExistingMaterialName;
    }

    /**
     * Get the selected material in the material JList.
     *
     * @return the selected material
     */
    private Material getSelectedMaterial() {
        int selectedIndex = materialManagementDialog.getMaterialList().getSelectedIndex();
        Material selectedMaterial = (selectedIndex != -1) ? materialBindingList.get(selectedIndex) : null;
        return selectedMaterial;
    }

    /**
     * Create a default material, with some default properties.
     *
     * @return the default material
     */
    private Material createDefaultMaterial() {
        Material defaultMaterial = new Material("default material name");

        //find species
        List<MaterialCvTerm> species = cvTermService.findByCvTermByType(MaterialCvTerm.class, CvTermType.SPECIES);
        if (!species.isEmpty()) {
            defaultMaterial.setSpecies(species.get(0));
        }
        //find tissues
        List<MaterialCvTerm> tissues = cvTermService.findByCvTermByType(MaterialCvTerm.class, CvTermType.TISSUE);
        if (!tissues.isEmpty()) {
            defaultMaterial.setTissue(tissues.get(0));
        }
        //find cell types
        List<MaterialCvTerm> cellTypes = cvTermService.findByCvTermByType(MaterialCvTerm.class, CvTermType.CELL_TYPE);
        if (!cellTypes.isEmpty()) {
            defaultMaterial.setCellType(cellTypes.get(0));
        }
        //find compartments
        List<MaterialCvTerm> compartments = cvTermService.findByCvTermByType(MaterialCvTerm.class, CvTermType.COMPARTMENT);
        if (!compartments.isEmpty()) {
            defaultMaterial.setCompartment(compartments.get(0));
        }
        return defaultMaterial;
    }

    private void updateMaterialEditDialog(Material material) {
        if (material.getId() != null) {
            materialEditDialog.getMaterialSaveOrUpdateButton().setText("update");
        } else {
            materialEditDialog.getMaterialSaveOrUpdateButton().setText("save");
        }

        //add the single CV terms
        EnumMap<CvTermType, MaterialCvTerm> singleCvTerms = new EnumMap<>(CvTermType.class);
        singleCvTerms.put(CvTermType.SPECIES, material.getSpecies());
        singleCvTerms.put(CvTermType.TISSUE, material.getTissue());
        singleCvTerms.put(CvTermType.CELL_TYPE, material.getCellType());
        singleCvTerms.put(CvTermType.COMPARTMENT, material.getCompartment());

        //add the multiple CV terms
        EnumMap<CvTermType, List<MaterialCvTerm>> multipleCvTerms = new EnumMap<>(CvTermType.class);
        cvTermSummaryListModel.update(singleCvTerms, multipleCvTerms);
        
        //clear selection in CV term summary list
        materialEditDialog.getCvTermSummaryList().getSelectionModel().clearSelection();
    }
    
    /**
     * Clear the material detail fields
     */
    private void clearMaterialDetailFields() {
        materialManagementDialog.getMaterialStateInfoLabel().setText("");
        materialManagementDialog.getMaterialDetailsTable().setModel(new CvTermTableModel());
    }
}
