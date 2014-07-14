package com.compomics.colims.client.controller.admin;

import com.compomics.colims.client.compoment.DualList;
import com.compomics.colims.client.controller.Controllable;
import com.compomics.colims.client.controller.ColimsController;
import com.compomics.colims.client.event.EntityChangeEvent;
import com.compomics.colims.client.event.admin.MaterialChangeEvent;
import com.compomics.colims.client.event.admin.CvTermChangeEvent;
import com.compomics.colims.client.event.message.DbConstraintMessageEvent;
import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.model.TypedCvTermSummaryListModel;
import com.compomics.colims.client.model.TypedCvTermTableModel;
import com.compomics.colims.client.renderer.TypedCvTermSummaryCellRenderer;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.admin.material.MaterialEditDialog;
import com.compomics.colims.client.view.admin.material.MaterialManagementDialog;
import com.compomics.colims.core.service.CvTermService;
import com.compomics.colims.core.service.MaterialService;
import com.compomics.colims.model.TypedCvTerm;
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
    private TypedCvTermSummaryListModel<MaterialCvTerm> typedCvTermSummaryListModel;
    private ObservableList<Material> materialBindingList;
    private BindingGroup bindingGroup;
    private Material materialToEdit;
    //view
    private MaterialManagementDialog materialManagementDialog;
    private MaterialEditDialog materialEditDialog;
    //parent controller
    @Autowired
    private ColimsController colimsController;
    @Autowired
    private CvTermManagementController cvTermManagementController;
    //services
    @Autowired
    private MaterialService materialService;
    @Autowired
    private CvTermService cvTermService;
    @Autowired
    private EventBus eventBus;

    /**
     *
     */
    public MaterialManagementController() {
    }

    /**
     *
     * @return
     */
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

        GuiUtils.centerDialogOnComponent(colimsController.getColimsFrame(), materialManagementDialog);
        materialManagementDialog.setVisible(true);
    }

    /**
     * Listen to a CV term change event posted by the
     * CvTermManagementController. If the MaterialManagementDialog is visible,
     * clear the selection in the CV term summary list.
     *
     * @param cvTermChangeEvent
     */
    @Subscribe
    public void onCvTermChangeEvent(CvTermChangeEvent cvTermChangeEvent) {
        if (materialEditDialog.isVisible()) {
            materialEditDialog.getCvTermSummaryList().getSelectionModel().clearSelection();
        }
    }

    private void initMaterialManagementDialog() {
        materialManagementDialog = new MaterialManagementDialog(colimsController.getColimsFrame(), true);

        //add binding
        materialBindingList = ObservableCollections.observableList(materialService.findAll());
        JListBinding materialListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, materialBindingList, materialManagementDialog.getMaterialList());
        bindingGroup.addBinding(materialListBinding);

        //add action listeners
        materialManagementDialog.getMaterialList().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedIndex = materialManagementDialog.getMaterialList().getSelectedIndex();
                    if (selectedIndex != -1 && materialBindingList.get(selectedIndex) != null) {
                        Material selectedMaterial = materialBindingList.get(selectedIndex);

                        //init CvTermModel
                        List<TypedCvTerm> cvTerms = new ArrayList<>();
                        if (selectedMaterial.getSpecies() != null) {
                            cvTerms.add(selectedMaterial.getSpecies());
                        }
                        if (selectedMaterial.getCellType() != null) {
                            cvTerms.add(selectedMaterial.getCellType());
                        }
                        if (selectedMaterial.getTissue() != null) {
                            cvTerms.add(selectedMaterial.getTissue());
                        }
                        if (selectedMaterial.getCompartment() != null) {
                            cvTerms.add(selectedMaterial.getCompartment());
                        }
                        TypedCvTermTableModel typedCvTermTableModel = new TypedCvTermTableModel(cvTerms);
                        materialManagementDialog.getMaterialDetailsTable().setModel(typedCvTermTableModel);
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
                updateMaterialEditDialog(createDefaultMaterial());

                //show dialog
                GuiUtils.centerDialogOnComponent(materialManagementDialog, materialEditDialog);
                materialEditDialog.setVisible(true);
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

                            eventBus.post(new MaterialChangeEvent(EntityChangeEvent.Type.DELETED));
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
                } else {
                    eventBus.post(new MessageEvent("Material selection", "Please select a material to delete.", JOptionPane.INFORMATION_MESSAGE));
                }
            }
        });

        materialManagementDialog.getEditMaterialButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (materialManagementDialog.getMaterialList().getSelectedIndex() != -1) {
                    updateMaterialEditDialog(getSelectedMaterial());
                    //show dialog
                    GuiUtils.centerDialogOnComponent(materialManagementDialog, materialEditDialog);
                    materialEditDialog.setVisible(true);
                } else {
                    eventBus.post(new MessageEvent("Material selection", "Please select a material to edit.", JOptionPane.INFORMATION_MESSAGE));
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
        materialEditDialog = new MaterialEditDialog(materialManagementDialog, true);

        //init dual list
        materialEditDialog.getCvTermDualList().init(new CvTermAccessionComparator());

        //add binding
        Binding materialNameBinding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, materialManagementDialog.getMaterialList(), ELProperty.create("${selectedElement.name}"), materialEditDialog.getNameTextField(), BeanProperty.create("text"), "materialNameBinding");
        bindingGroup.addBinding(materialNameBinding);

        //set model and renderer
        typedCvTermSummaryListModel = new TypedCvTermSummaryListModel();
        materialEditDialog.getCvTermSummaryList().setModel(typedCvTermSummaryListModel);
        materialEditDialog.getCvTermSummaryList().setCellRenderer(new TypedCvTermSummaryCellRenderer<MaterialCvTerm>());

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
                        if (typedCvTermSummaryListModel.isSingleCvTerm(selectedcvTermType)) {
                            addedCvTerms = new ArrayList<>();
                            MaterialCvTerm materialCvTerm = typedCvTermSummaryListModel.getSingleCvTerms().get(selectedcvTermType);
                            //check for null value
                            if (materialCvTerm != null) {
                                addedCvTerms.add(materialCvTerm);
                            }
                            materialEditDialog.getCvTermDualList().populateLists(availableCvTerms, addedCvTerms, 1);
                        } else {
                            addedCvTerms = typedCvTermSummaryListModel.getMultiCvTerms().get(selectedcvTermType);
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

                List<MaterialCvTerm> addedItems = (List<MaterialCvTerm>) evt.getNewValue();

                //check for property
                if (selectedcvTermType.equals(CvTermType.SPECIES)) {
                    if (!addedItems.isEmpty()) {
                        MaterialCvTerm species = addedItems.get(0);
                        materialToEdit.setSpecies(species);
                        typedCvTermSummaryListModel.updateSingleCvTerm(CvTermType.SPECIES, species);
                    } else {
                        materialToEdit.setSpecies(null);
                        typedCvTermSummaryListModel.updateSingleCvTerm(CvTermType.SPECIES, null);
                    }
                } else if (selectedcvTermType.equals(CvTermType.TISSUE)) {
                    if (!addedItems.isEmpty()) {
                        MaterialCvTerm tissue = addedItems.get(0);
                        materialToEdit.setTissue(tissue);
                        typedCvTermSummaryListModel.updateSingleCvTerm(CvTermType.TISSUE, tissue);
                    } else {
                        materialToEdit.setTissue(null);
                        typedCvTermSummaryListModel.updateSingleCvTerm(CvTermType.TISSUE, null);
                    }
                } else if (selectedcvTermType.equals(CvTermType.CELL_TYPE)) {
                    if (!addedItems.isEmpty()) {
                        MaterialCvTerm cellType = addedItems.get(0);
                        materialToEdit.setCellType(cellType);
                        typedCvTermSummaryListModel.updateSingleCvTerm(CvTermType.CELL_TYPE, cellType);
                    } else {
                        materialToEdit.setCellType(null);
                        typedCvTermSummaryListModel.updateSingleCvTerm(CvTermType.CELL_TYPE, null);
                    }
                } else if (selectedcvTermType.equals(CvTermType.COMPARTMENT)) {
                    if (!addedItems.isEmpty()) {
                        MaterialCvTerm compartment = addedItems.get(0);
                        materialToEdit.setCompartment(compartment);
                        typedCvTermSummaryListModel.updateSingleCvTerm(CvTermType.COMPARTMENT, compartment);
                    } else {
                        materialToEdit.setCompartment(null);
                        typedCvTermSummaryListModel.updateSingleCvTerm(CvTermType.COMPARTMENT, null);
                    }
                }

            }
        });

        materialEditDialog.getMaterialSaveOrUpdateButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //update with dialog input
                updateMaterialToEdit();

                //validate material
                List<String> validationMessages = GuiUtils.validateEntity(materialToEdit);
                //check for a new material if the material name already exists in the db                
                if (materialToEdit.getId() == null && isExistingMaterialName(materialToEdit)) {
                    validationMessages.add(materialToEdit.getName() + " already exists in the database,"
                            + "\n" + "please choose another material name.");
                }
                if (validationMessages.isEmpty()) {
                    int index;
                    EntityChangeEvent.Type type;
                    if (materialToEdit.getId() != null) {
                        materialService.update(materialToEdit);
                        index = materialManagementDialog.getMaterialList().getSelectedIndex();
                        type = EntityChangeEvent.Type.UPDATED;
                    } else {
                        materialService.save(materialToEdit);
                        //add instrument to overview list
                        materialBindingList.add(materialToEdit);
                        index = materialBindingList.size() - 1;
                        materialEditDialog.getMaterialStateInfoLabel().setText("");
                        type = EntityChangeEvent.Type.CREATED;
                    }
                    materialEditDialog.getMaterialSaveOrUpdateButton().setText("update");

                    eventBus.post(new MaterialChangeEvent(type));

                    MessageEvent messageEvent = new MessageEvent("Material store confirmation", "Material " + materialToEdit.getName() + " was stored successfully!", JOptionPane.INFORMATION_MESSAGE);
                    eventBus.post(messageEvent);

                    //refresh selection in material list in management overview dialog
                    materialManagementDialog.getMaterialList().getSelectionModel().clearSelection();
                    materialManagementDialog.getMaterialList().setSelectedIndex(index);
                } else {
                    MessageEvent messageEvent = new MessageEvent("Validation failure", validationMessages, JOptionPane.WARNING_MESSAGE);
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

                    List<TypedCvTerm> cvTerms = cvTermService.findByCvTermByType(selectedcvTermType);

                    //update the CV term list
                    cvTermManagementController.updateDialog(selectedcvTermType, cvTerms);

                    cvTermManagementController.showView();
                } else {
                    eventBus.post(new MessageEvent("Material CV term type selection", "Please select a material CV term type to edit.", JOptionPane.INFORMATION_MESSAGE));
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

    /**
     * Update the materialToEdit with input from the materialEditDialog
     */
    public void updateMaterialToEdit() {
        materialToEdit.setName(materialEditDialog.getNameTextField().getText());
    }

    /**
     * Update the material edit dialog with the given material
     *
     * @param material
     */
    private void updateMaterialEditDialog(Material material) {
        materialToEdit = material;

        //check if the material has an ID.
        //If so, change the save button text and the info state label.
        if (materialToEdit.getId() != null) {
            materialEditDialog.getMaterialSaveOrUpdateButton().setText("update");
            materialEditDialog.getMaterialStateInfoLabel().setText("");
        } else {
            materialEditDialog.getMaterialSaveOrUpdateButton().setText("save");
            materialEditDialog.getMaterialStateInfoLabel().setText("This material hasn't been stored to the database.");
        }

        materialEditDialog.getNameTextField().setText(materialToEdit.getName());

        //add the single CV terms
        EnumMap<CvTermType, MaterialCvTerm> singleCvTerms = new EnumMap<>(CvTermType.class);
        singleCvTerms.put(CvTermType.SPECIES, materialToEdit.getSpecies());
        singleCvTerms.put(CvTermType.TISSUE, materialToEdit.getTissue());
        singleCvTerms.put(CvTermType.CELL_TYPE, materialToEdit.getCellType());
        singleCvTerms.put(CvTermType.COMPARTMENT, materialToEdit.getCompartment());

        //add the multiple CV terms
        EnumMap<CvTermType, List<MaterialCvTerm>> multipleCvTerms = new EnumMap<>(CvTermType.class);
        typedCvTermSummaryListModel.update(singleCvTerms, multipleCvTerms);

        //clear selection in CV term summary list
        materialEditDialog.getCvTermSummaryList().getSelectionModel().clearSelection();
    }

    /**
     * Clear the material detail fields
     */
    private void clearMaterialDetailFields() {
        materialManagementDialog.getMaterialDetailsTable().setModel(new TypedCvTermTableModel());
    }
}
