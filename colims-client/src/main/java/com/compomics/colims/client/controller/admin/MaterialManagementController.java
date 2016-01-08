package com.compomics.colims.client.controller.admin;

import com.compomics.colims.client.compoment.DualList;
import com.compomics.colims.client.controller.Controllable;
import com.compomics.colims.client.controller.MainController;
import com.compomics.colims.client.event.EntityChangeEvent;
import com.compomics.colims.client.event.admin.CvParamChangeEvent;
import com.compomics.colims.client.event.admin.MaterialChangeEvent;
import com.compomics.colims.client.event.message.DbConstraintMessageEvent;
import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.model.TypedCvParamSummaryListModel;
import com.compomics.colims.client.model.table.model.TypedCvParamTableModel;
import com.compomics.colims.client.renderer.TypedCvParamSummaryCellRenderer;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.admin.material.MaterialEditDialog;
import com.compomics.colims.client.view.admin.material.MaterialManagementDialog;
import com.compomics.colims.core.service.AuditableTypedCvParamService;
import com.compomics.colims.core.service.MaterialService;
import com.compomics.colims.model.Material;
import com.compomics.colims.model.MaterialCvParam;
import com.compomics.colims.model.comparator.AuditableCvParamAccessionComparator;
import com.compomics.colims.model.cv.AuditableTypedCvParam;
import com.compomics.colims.model.enums.CvParamType;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.hibernate.exception.ConstraintViolationException;
import org.jdesktop.beansbinding.*;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

/**
 * @author Niels Hulstaert
 */
@Component("materialManagementController")
@Lazy
public class MaterialManagementController implements Controllable {

    //model
    private TypedCvParamSummaryListModel<MaterialCvParam> typedCvParamSummaryListModel;
    private ObservableList<Material> materialBindingList;
    private BindingGroup bindingGroup;
    private Material materialToEdit;
    //view
    private MaterialManagementDialog materialManagementDialog;
    private MaterialEditDialog materialEditDialog;
    //parent controller
    @Autowired
    private MainController mainController;
    @Autowired
    @Lazy
    private CvParamManagementController cvParamManagementController;
    //services
    @Autowired
    private MaterialService materialService;
    @Autowired
    private AuditableTypedCvParamService cvParamService;
    @Autowired
    private EventBus eventBus;

    @Override
    @PostConstruct
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

        GuiUtils.centerDialogOnComponent(mainController.getMainFrame(), materialManagementDialog);
        materialManagementDialog.setVisible(true);
    }

    /**
     * Listen to a CV param change event posted by the
     * CvParamManagementController. If the MaterialManagementDialog is visible,
     * clear the selection in the CV param summary list.
     *
     * @param cvParamChangeEvent the CvParamChangeEvent
     */
    @Subscribe
    public void onCvParamChangeEvent(final CvParamChangeEvent cvParamChangeEvent) {
        if (materialEditDialog.isVisible()) {
            materialEditDialog.getCvParamSummaryList().getSelectionModel().clearSelection();
        }
    }

    private void initMaterialManagementDialog() {
        materialManagementDialog = new MaterialManagementDialog(mainController.getMainFrame(), true);

        //add binding
        materialBindingList = ObservableCollections.observableList(materialService.findAll());
        JListBinding materialListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, materialBindingList, materialManagementDialog.getMaterialList());
        bindingGroup.addBinding(materialListBinding);

        //add action listeners
        materialManagementDialog.getMaterialList().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedIndex = materialManagementDialog.getMaterialList().getSelectedIndex();
                if (selectedIndex != -1 && materialBindingList.get(selectedIndex) != null) {
                    Material selectedMaterial = materialBindingList.get(selectedIndex);

                    //init CvParamModel
                    List<AuditableTypedCvParam> cvParams = new ArrayList<>();
                    if (selectedMaterial.getSpecies() != null) {
                        cvParams.add(selectedMaterial.getSpecies());
                    }
                    if (selectedMaterial.getCellType() != null) {
                        cvParams.add(selectedMaterial.getCellType());
                    }
                    if (selectedMaterial.getTissue() != null) {
                        cvParams.add(selectedMaterial.getTissue());
                    }
                    if (selectedMaterial.getCompartment() != null) {
                        cvParams.add(selectedMaterial.getCompartment());
                    }
                    TypedCvParamTableModel typedCvParamTableModel = new TypedCvParamTableModel(cvParams);
                    materialManagementDialog.getMaterialDetailsTable().setModel(typedCvParamTableModel);
                } else {
                    //clear detail view
                    clearMaterialDetailFields();
                }
            }
        });

        materialManagementDialog.getAddMaterialButton().addActionListener(e -> {
            updateMaterialEditDialog(createDefaultMaterial());

            //show dialog
            GuiUtils.centerDialogOnComponent(materialManagementDialog, materialEditDialog);
            materialEditDialog.setVisible(true);
        });

        materialManagementDialog.getDeleteMaterialButton().addActionListener(e -> {
            if (materialManagementDialog.getMaterialList().getSelectedIndex() != -1) {
                Material materialToDelete = getSelectedMaterial();
                //check if the material is already has an id.
                //If so, delete the material from the db.
                if (materialToDelete.getId() != null) {
                    try {
                        materialService.remove(materialToDelete);

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
        });

        materialManagementDialog.getEditMaterialButton().addActionListener(e -> {
            if (materialManagementDialog.getMaterialList().getSelectedIndex() != -1) {
                updateMaterialEditDialog(getSelectedMaterial());
                //show dialog
                GuiUtils.centerDialogOnComponent(materialManagementDialog, materialEditDialog);
                materialEditDialog.setVisible(true);
            } else {
                eventBus.post(new MessageEvent("Material selection", "Please select a material to edit.", JOptionPane.INFORMATION_MESSAGE));
            }
        });

        materialManagementDialog.getCancelMaterialManagementButton().addActionListener(e -> materialManagementDialog.dispose());

    }

    private void initMaterialEditDialog() {
        materialEditDialog = new MaterialEditDialog(materialManagementDialog, true);

        //init dual list
        materialEditDialog.getCvParamDualList().init(new AuditableCvParamAccessionComparator());

        //add binding
        Binding materialNameBinding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, materialManagementDialog.getMaterialList(), ELProperty.create("${selectedElement.name}"), materialEditDialog.getNameTextField(), BeanProperty.create("text"), "materialNameBinding");
        bindingGroup.addBinding(materialNameBinding);

        //set model and renderer
        typedCvParamSummaryListModel = new TypedCvParamSummaryListModel();
        materialEditDialog.getCvParamSummaryList().setModel(typedCvParamSummaryListModel);
        materialEditDialog.getCvParamSummaryList().setCellRenderer(new TypedCvParamSummaryCellRenderer<>());

        //add action listeners
        materialEditDialog.getCvParamSummaryList().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                if (materialEditDialog.getCvParamSummaryList().getSelectedIndex() != -1) {
                    //get selected cvParamType from summary list
                    CvParamType selectedCvParamType = (CvParamType) materialEditDialog.getCvParamSummaryList().getSelectedValue();

                    //load duallist for the selected cvParamType
                    List<MaterialCvParam> availableCvParams = cvParamService.findByCvParamByType(MaterialCvParam.class, selectedCvParamType);

                    List<MaterialCvParam> addedCvParams;
                    //@todo for the moment, material has only single CV params,
                    //so this check is not necessary.
                    if (typedCvParamSummaryListModel.isSingleCvParam(selectedCvParamType)) {
                        addedCvParams = new ArrayList<>();
                        MaterialCvParam materialCvParam = typedCvParamSummaryListModel.getSingleCvParams().get(selectedCvParamType);
                        //check for null value
                        if (materialCvParam != null) {
                            addedCvParams.add(materialCvParam);
                        }
                        materialEditDialog.getCvParamDualList().populateLists(availableCvParams, addedCvParams, 1);
                    } else {
                        addedCvParams = typedCvParamSummaryListModel.getMultiCvParams().get(selectedCvParamType);
                        materialEditDialog.getCvParamDualList().populateLists(availableCvParams, addedCvParams);
                    }
                } else {
                    materialEditDialog.getCvParamDualList().clear();
                }
            }
        });

        materialEditDialog.getCvParamDualList().addPropertyChangeListener(DualList.CHANGED, evt -> {
            //get selected cvParamType
            CvParamType selectedcvParamType = (CvParamType) materialEditDialog.getCvParamSummaryList().getSelectedValue();

            List<MaterialCvParam> addedItems = (List<MaterialCvParam>) evt.getNewValue();

            //check for property
            switch (selectedcvParamType) {
                case SPECIES:
                    if (!addedItems.isEmpty()) {
                        MaterialCvParam species = addedItems.get(0);
                        materialToEdit.setSpecies(species);
                        typedCvParamSummaryListModel.updateSingleCvParam(CvParamType.SPECIES, species);
                    } else {
                        materialToEdit.setSpecies(null);
                        typedCvParamSummaryListModel.updateSingleCvParam(CvParamType.SPECIES, null);
                    }
                    break;
                case TISSUE:
                    if (!addedItems.isEmpty()) {
                        MaterialCvParam tissue = addedItems.get(0);
                        materialToEdit.setTissue(tissue);
                        typedCvParamSummaryListModel.updateSingleCvParam(CvParamType.TISSUE, tissue);
                    } else {
                        materialToEdit.setTissue(null);
                        typedCvParamSummaryListModel.updateSingleCvParam(CvParamType.TISSUE, null);
                    }
                    break;
                case CELL_TYPE:
                    if (!addedItems.isEmpty()) {
                        MaterialCvParam cellType = addedItems.get(0);
                        materialToEdit.setCellType(cellType);
                        typedCvParamSummaryListModel.updateSingleCvParam(CvParamType.CELL_TYPE, cellType);
                    } else {
                        materialToEdit.setCellType(null);
                        typedCvParamSummaryListModel.updateSingleCvParam(CvParamType.CELL_TYPE, null);
                    }
                    break;
                case COMPARTMENT:
                    if (!addedItems.isEmpty()) {
                        MaterialCvParam compartment = addedItems.get(0);
                        materialToEdit.setCompartment(compartment);
                        typedCvParamSummaryListModel.updateSingleCvParam(CvParamType.COMPARTMENT, compartment);
                    } else {
                        materialToEdit.setCompartment(null);
                        typedCvParamSummaryListModel.updateSingleCvParam(CvParamType.COMPARTMENT, null);
                    }
                    break;
                default:
                    break;
            }

        });

        materialEditDialog.getMaterialSaveOrUpdateButton().addActionListener(e -> {
            //update with dialog input
            updateMaterialToEdit();

            //validate material
            List<String> validationMessages = GuiUtils.validateEntity(materialToEdit);
            //check if the material name already exists in the db
            if (isExistingMaterialName(materialToEdit)) {
                validationMessages.add(materialToEdit.getName() + " already exists in the database,"
                        + System.lineSeparator() + "please choose another material name.");
            }
            if (validationMessages.isEmpty()) {
                int index;
                EntityChangeEvent.Type type;
                if (materialToEdit.getId() != null) {
                    materialToEdit = materialService.merge(materialToEdit);
                    index = materialManagementDialog.getMaterialList().getSelectedIndex();
                    type = EntityChangeEvent.Type.UPDATED;
                } else {
                    materialService.persist(materialToEdit);
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
        });

        materialEditDialog.getCancelMaterialEditButton().addActionListener(e -> {
            if (materialToEdit.getId() != null) {
                //roll back the changes
                Material rolledBackMaterial = materialService.findById(materialToEdit.getId());
                int selectedIndex = materialManagementDialog.getMaterialList().getSelectedIndex();
                materialBindingList.remove(selectedIndex);
                materialBindingList.add(selectedIndex, rolledBackMaterial);
            }

            materialEditDialog.dispose();
        });

        materialEditDialog.getMaterialCvParamsCrudButton().addActionListener(e -> {
            //check if a CV param group is selected in the CV param summary list
            if (materialEditDialog.getCvParamSummaryList().getSelectedIndex() != -1) {
                //get selected cvParamType from summary list
                CvParamType selectedcvParamType = (CvParamType) materialEditDialog.getCvParamSummaryList().getSelectedValue();

                List<AuditableTypedCvParam> cvParams = cvParamService.findByCvParamByType(selectedcvParamType);

                //update the CV param list
                cvParamManagementController.updateDialog(selectedcvParamType, cvParams);

                cvParamManagementController.showView();
            } else {
                eventBus.post(new MessageEvent("Material CV param type selection", "Please select a material CV param type to edit.", JOptionPane.INFORMATION_MESSAGE));
            }
        });
    }

    /**
     * Check if a material with the given material name exists in the database.
     *
     * @param material the material
     * @return does the material name exist
     */
    private boolean isExistingMaterialName(final Material material) {
        Long count = materialService.countByName(material);

        return count != 0;
    }

    /**
     * Get the selected material in the material JList.
     *
     * @return the selected material
     */
    private Material getSelectedMaterial() {
        int selectedIndex = materialManagementDialog.getMaterialList().getSelectedIndex();
        return (selectedIndex != -1) ? materialBindingList.get(selectedIndex) : null;
    }

    /**
     * Create a default material, with some default properties.
     *
     * @return the default material
     */
    private Material createDefaultMaterial() {
        Material defaultMaterial = new Material("default material name");

        //find species
        List<MaterialCvParam> species = cvParamService.findByCvParamByType(MaterialCvParam.class, CvParamType.SPECIES);
        if (!species.isEmpty()) {
            defaultMaterial.setSpecies(species.get(0));
        }
        //find tissues
        List<MaterialCvParam> tissues = cvParamService.findByCvParamByType(MaterialCvParam.class, CvParamType.TISSUE);
        if (!tissues.isEmpty()) {
            defaultMaterial.setTissue(tissues.get(0));
        }
        //find cell types
        List<MaterialCvParam> cellTypes = cvParamService.findByCvParamByType(MaterialCvParam.class, CvParamType.CELL_TYPE);
        if (!cellTypes.isEmpty()) {
            defaultMaterial.setCellType(cellTypes.get(0));
        }
        //find compartments
        List<MaterialCvParam> compartments = cvParamService.findByCvParamByType(MaterialCvParam.class, CvParamType.COMPARTMENT);
        if (!compartments.isEmpty()) {
            defaultMaterial.setCompartment(compartments.get(0));
        }
        return defaultMaterial;
    }

    /**
     * Update the materialToEdit with input from the materialEditDialog.
     */
    public void updateMaterialToEdit() {
        materialToEdit.setName(materialEditDialog.getNameTextField().getText());
    }

    /**
     * Update the material edit dialog with the given material.
     *
     * @param material the material
     */
    private void updateMaterialEditDialog(final Material material) {
        materialToEdit = material;

        //check if the material has an ID.
        //If so, change the save button text and the info state label.
        if (materialToEdit.getId() != null) {
            materialEditDialog.getMaterialSaveOrUpdateButton().setText("update");
            materialEditDialog.getMaterialStateInfoLabel().setText("");
        } else {
            materialEditDialog.getMaterialSaveOrUpdateButton().setText("save");
            materialEditDialog.getMaterialStateInfoLabel().setText("This material hasn't been stored in the database.");
        }

        materialEditDialog.getNameTextField().setText(materialToEdit.getName());

        //add the single CV params
        EnumMap<CvParamType, MaterialCvParam> singleCvParams = new EnumMap<>(CvParamType.class);
        singleCvParams.put(CvParamType.SPECIES, materialToEdit.getSpecies());
        singleCvParams.put(CvParamType.TISSUE, materialToEdit.getTissue());
        singleCvParams.put(CvParamType.CELL_TYPE, materialToEdit.getCellType());
        singleCvParams.put(CvParamType.COMPARTMENT, materialToEdit.getCompartment());

        //add the multiple CV params
        EnumMap<CvParamType, List<MaterialCvParam>> multipleCvParams = new EnumMap<>(CvParamType.class);
        typedCvParamSummaryListModel.update(singleCvParams, multipleCvParams);

        //clear selection in CV param summary list
        materialEditDialog.getCvParamSummaryList().getSelectionModel().clearSelection();
    }

    /**
     * Clear the material detail fields.
     */
    private void clearMaterialDetailFields() {
        materialManagementDialog.getMaterialDetailsTable().setModel(new TypedCvParamTableModel());
    }
}
