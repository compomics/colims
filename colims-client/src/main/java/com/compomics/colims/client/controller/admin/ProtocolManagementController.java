package com.compomics.colims.client.controller.admin;

import com.compomics.colims.client.compoment.DualList;
import com.compomics.colims.client.controller.Controllable;
import com.compomics.colims.client.controller.MainController;
import com.compomics.colims.client.event.EntityChangeEvent;
import com.compomics.colims.client.event.admin.TypedCvParamChangeEvent;
import com.compomics.colims.client.event.admin.ProtocolChangeEvent;
import com.compomics.colims.client.event.message.DbConstraintMessageEvent;
import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.model.TypedCvParamSummaryListModel;
import com.compomics.colims.client.model.table.model.TypedCvParamTableModel;
import com.compomics.colims.client.renderer.TypedCvParamSummaryCellRenderer;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.admin.protocol.ProtocolEditDialog;
import com.compomics.colims.client.view.admin.protocol.ProtocolManagementDialog;
import com.compomics.colims.core.service.AuditableTypedCvParamService;
import com.compomics.colims.core.service.ProtocolService;
import com.compomics.colims.model.Protocol;
import com.compomics.colims.model.ProtocolCvParam;
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
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Niels Hulstaert
 */
@Component("protocolManagementController")
@Lazy
public class ProtocolManagementController implements Controllable {

    /**
     * The preselected ontology namespaces.
     */
    private static final List<String> PRESELECTED_ONTOLOGY_NAMESPACES = Arrays.asList("ms");

    //model
    private TypedCvParamSummaryListModel<ProtocolCvParam> typedCvParamSummaryListModel;
    private ObservableList<Protocol> protocolBindingList;
    private BindingGroup bindingGroup;
    private Protocol protocolToEdit;
    //view
    private ProtocolManagementDialog protocolManagementDialog;
    private ProtocolEditDialog protocolEditDialog;
    //parent controller
    @Autowired
    private MainController mainController;
    @Autowired
    @Lazy
    private TypedCvParamManagementController typedCvParamManagementController;
    //services
    @Autowired
    private ProtocolService protocolService;
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
        initProtocolManagementDialog();
        initProtocolEditDialog();

        bindingGroup.bind();
    }

    @Override
    public void showView() {
        //clear selection
        protocolManagementDialog.getProtocolList().getSelectionModel().clearSelection();

        GuiUtils.centerDialogOnComponent(mainController.getMainFrame(), protocolManagementDialog);
        protocolManagementDialog.setVisible(true);
    }

    /**
     * Listen to a CV param change event posted by the
     * TypedCvParamManagementController. If the ProtocolManagementDialog is
     * visible, clear the selection in the CV param summary list.
     *
     * @param cvParamChangeEvent the TypedCvParamChangeEvent
     */
    @Subscribe
    public void onCvParamChangeEvent(final TypedCvParamChangeEvent cvParamChangeEvent) {
        if (protocolEditDialog.isVisible()) {
            protocolEditDialog.getCvParamSummaryList().getSelectionModel().clearSelection();
        }
    }

    /**
     * Init the ProtocolManagementDialog.
     */
    private void initProtocolManagementDialog() {
        protocolManagementDialog = new ProtocolManagementDialog(mainController.getMainFrame(), true);

        //add binding
        protocolBindingList = ObservableCollections.observableList(protocolService.findAll());
        JListBinding protocolListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, protocolBindingList, protocolManagementDialog.getProtocolList());
        bindingGroup.addBinding(protocolListBinding);

        //add action listeners
        protocolManagementDialog.getProtocolList().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedIndex = protocolManagementDialog.getProtocolList().getSelectedIndex();
                if (selectedIndex != -1 && protocolBindingList.get(selectedIndex) != null) {
                    Protocol selectedProtocol = protocolBindingList.get(selectedIndex);

                    //init CvParamModel
                    List<AuditableTypedCvParam> cvParams = new ArrayList<>();
                    if (selectedProtocol.getReduction() != null) {
                        cvParams.add(selectedProtocol.getReduction());
                    }
                    if (selectedProtocol.getEnzyme() != null) {
                        cvParams.add(selectedProtocol.getEnzyme());
                    }
                    if (selectedProtocol.getCellBased() != null) {
                        cvParams.add(selectedProtocol.getCellBased());
                    }
                    cvParams.addAll(selectedProtocol.getChemicalLabels().stream().collect(Collectors.toList()));
                    cvParams.addAll(selectedProtocol.getOtherCvParams().stream().collect(Collectors.toList()));
                    TypedCvParamTableModel typedCvParamTableModel = new TypedCvParamTableModel(cvParams);

                    protocolManagementDialog.getProtocolDetailsTable().setModel(typedCvParamTableModel);
                } else {
                    //clear detail view
                    clearProtocolDetailFields();
                }
            }
        });

        protocolManagementDialog.getAddProtocolButton().addActionListener(e -> {
            updateProtocolEditDialog(createDefaultProtocol());

            //show dialog
            GuiUtils.centerDialogOnComponent(protocolManagementDialog, protocolEditDialog);
            protocolEditDialog.setVisible(true);
        });

        protocolManagementDialog.getDeleteProtocolButton().addActionListener(e -> {
            if (protocolManagementDialog.getProtocolList().getSelectedIndex() != -1) {
                Protocol protocolToDelete = getSelectedProtocol();
                //check if the protocol is already has an id.
                //If so, delete the protocol from the db.
                if (protocolToDelete.getId() != null) {
                    try {
                        protocolService.remove(protocolToDelete);

                        protocolBindingList.remove(protocolManagementDialog.getProtocolList().getSelectedIndex());
                        protocolManagementDialog.getProtocolList().getSelectionModel().clearSelection();

                        eventBus.post(new ProtocolChangeEvent(EntityChangeEvent.Type.DELETED));
                    } catch (DataIntegrityViolationException dive) {
                        //check if the protocol can be deleted without breaking existing database relations,
                        //i.e. are there any constraints violations
                        if (dive.getCause() instanceof ConstraintViolationException) {
                            DbConstraintMessageEvent dbConstraintMessageEvent = new DbConstraintMessageEvent("protocol", protocolToDelete.getName());
                            eventBus.post(dbConstraintMessageEvent);
                        } else {
                            //pass the exception
                            throw dive;
                        }
                    }
                } else {
                    protocolBindingList.remove(protocolManagementDialog.getProtocolList().getSelectedIndex());
                    protocolManagementDialog.getProtocolList().getSelectionModel().clearSelection();
                }
            } else {
                eventBus.post(new MessageEvent("Protocol selection", "Please select a protocol to delete.", JOptionPane.INFORMATION_MESSAGE));
            }
        });

        protocolManagementDialog.getEditProtocolButton().addActionListener(e -> {
            if (protocolManagementDialog.getProtocolList().getSelectedIndex() != -1) {
                updateProtocolEditDialog(getSelectedProtocol());
                //show dialog
                GuiUtils.centerDialogOnComponent(protocolManagementDialog, protocolEditDialog);
                protocolEditDialog.setVisible(true);
            } else {
                eventBus.post(new MessageEvent("Protocol selection", "Please select a protocol to edit.", JOptionPane.INFORMATION_MESSAGE));
            }
        });

        protocolManagementDialog.getCancelProtocolManagementButton().addActionListener(e -> protocolManagementDialog.dispose());

    }

    /**
     * Init the ProtocolEditDialog.
     */
    private void initProtocolEditDialog() {
        protocolEditDialog = new ProtocolEditDialog(protocolManagementDialog, true);

        //init dual list
        protocolEditDialog.getCvParamDualList().init(new AuditableCvParamAccessionComparator());

        //add binding
        Binding protocolNameBinding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, protocolManagementDialog.getProtocolList(), ELProperty.create("${selectedElement.name}"), protocolEditDialog.getNameTextField(), BeanProperty.create("text"), "protocolNameBinding");
        bindingGroup.addBinding(protocolNameBinding);

        //set model and renderer
        typedCvParamSummaryListModel = new TypedCvParamSummaryListModel();
        protocolEditDialog.getCvParamSummaryList().setModel(typedCvParamSummaryListModel);
        protocolEditDialog.getCvParamSummaryList().setCellRenderer(new TypedCvParamSummaryCellRenderer<>());

        //add action listeners
        protocolEditDialog.getCvParamSummaryList().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                if (protocolEditDialog.getCvParamSummaryList().getSelectedIndex() != -1) {
                    //get selected cvParamType from summary list
                    CvParamType selectedCvParamType = (CvParamType) protocolEditDialog.getCvParamSummaryList().getSelectedValue();

                    //load duallist for the selected cvParamType
                    List<ProtocolCvParam> availableCvParams = cvParamService.findByCvParamByType(ProtocolCvParam.class, selectedCvParamType);

                    List<ProtocolCvParam> addedCvParams;
                    //@todo for the moment, protocol has only single CV params,
                    //so this check is not necessary.
                    if (typedCvParamSummaryListModel.isSingleCvParam(selectedCvParamType)) {
                        addedCvParams = new ArrayList<>();
                        ProtocolCvParam protocolCvParam = typedCvParamSummaryListModel.getSingleCvParams().get(selectedCvParamType);
                        //check for null value
                        if (protocolCvParam != null) {
                            addedCvParams.add(protocolCvParam);
                        }
                        protocolEditDialog.getCvParamDualList().populateLists(availableCvParams, addedCvParams, 1);
                    } else {
                        addedCvParams = typedCvParamSummaryListModel.getMultiCvParams().get(selectedCvParamType);
                        protocolEditDialog.getCvParamDualList().populateLists(availableCvParams, addedCvParams);
                    }
                } else {
                    protocolEditDialog.getCvParamDualList().clear();
                }
            }
        });

        protocolEditDialog.getCvParamDualList().addPropertyChangeListener(DualList.CHANGED, evt -> {
            //get selected cvParamType
            CvParamType selectedCvParamType = (CvParamType) protocolEditDialog.getCvParamSummaryList().getSelectedValue();

            List<ProtocolCvParam> addedItems = (List<ProtocolCvParam>) evt.getNewValue();

            //check for property
            switch (selectedCvParamType) {
                case REDUCTION:
                    if (!addedItems.isEmpty()) {
                        ProtocolCvParam reduction = addedItems.get(0);
                        protocolToEdit.setReduction(reduction);
                        typedCvParamSummaryListModel.updateSingleCvParam(CvParamType.REDUCTION, reduction);
                    } else {
                        protocolToEdit.setReduction(null);
                        typedCvParamSummaryListModel.updateSingleCvParam(CvParamType.REDUCTION, null);
                    }
                    break;
                case ENZYME:
                    if (!addedItems.isEmpty()) {
                        ProtocolCvParam enzyme = addedItems.get(0);
                        protocolToEdit.setEnzyme(enzyme);
                        typedCvParamSummaryListModel.updateSingleCvParam(CvParamType.ENZYME, enzyme);
                    } else {
                        protocolToEdit.setEnzyme(null);
                        typedCvParamSummaryListModel.updateSingleCvParam(CvParamType.ENZYME, null);
                    }
                    break;
                case CELL_BASED:
                    if (!addedItems.isEmpty()) {
                        ProtocolCvParam cellBased = addedItems.get(0);
                        protocolToEdit.setCellBased(cellBased);
                        typedCvParamSummaryListModel.updateSingleCvParam(CvParamType.CELL_BASED, cellBased);
                    } else {
                        protocolToEdit.setCellBased(null);
                        typedCvParamSummaryListModel.updateSingleCvParam(CvParamType.CELL_BASED, null);
                    }
                    break;
                case CHEMICAL_LABELING:
                    protocolToEdit.setChemicalLabels(addedItems);
                    typedCvParamSummaryListModel.updateMultiCvParam(CvParamType.CHEMICAL_LABELING, addedItems);
                    break;
                case OTHER:
                    protocolToEdit.setOtherCvParams(addedItems);
                    typedCvParamSummaryListModel.updateMultiCvParam(CvParamType.CHEMICAL_LABELING, addedItems);
                    break;
                default:
                    break;
            }

        });

        protocolEditDialog.getProtocolSaveOrUpdateButton().addActionListener(e -> {
            //update with dialog input
            updateProtocolToEdit();

            //validate protocol
            List<String> validationMessages = GuiUtils.validateEntity(protocolToEdit);
            //check if the protocol name already exists in the db
            if (isExistingProtocolName(protocolToEdit)) {
                validationMessages.add(protocolToEdit.getName() + " already exists in the database,"
                        + System.lineSeparator() + "please choose another protocol name.");
            }
            if (validationMessages.isEmpty()) {
                int index;
                EntityChangeEvent.Type type;
                if (protocolToEdit.getId() != null) {
                    protocolToEdit = protocolService.merge(protocolToEdit);
                    index = protocolManagementDialog.getProtocolList().getSelectedIndex();
                    type = EntityChangeEvent.Type.UPDATED;
                } else {
                    protocolService.persist(protocolToEdit);
                    //add protocol to overview list
                    protocolBindingList.add(protocolToEdit);
                    index = protocolBindingList.size() - 1;
                    protocolEditDialog.getProtocolStateInfoLabel().setText("");
                    type = EntityChangeEvent.Type.CREATED;
                }
                protocolEditDialog.getProtocolSaveOrUpdateButton().setText("update");

                eventBus.post(new ProtocolChangeEvent(type));

                MessageEvent messageEvent = new MessageEvent("Protocol store confirmation", "Protocol " + protocolToEdit.getName() + " was stored successfully!", JOptionPane.INFORMATION_MESSAGE);
                eventBus.post(messageEvent);

                //refresh selection in protocol list in management overview dialog
                protocolManagementDialog.getProtocolList().getSelectionModel().clearSelection();
                protocolManagementDialog.getProtocolList().setSelectedIndex(index);
            } else {
                MessageEvent messageEvent = new MessageEvent("Validation failure", validationMessages, JOptionPane.WARNING_MESSAGE);
                eventBus.post(messageEvent);
            }
        });

        protocolEditDialog.getCancelProtocolEditButton().addActionListener(e -> {
            if (protocolToEdit.getId() != null) {
                //roll back the changes
                Protocol rolledBackProtocol = protocolService.findById(protocolToEdit.getId());
                int selectedIndex = protocolManagementDialog.getProtocolList().getSelectedIndex();
                protocolBindingList.remove(selectedIndex);
                protocolBindingList.add(selectedIndex, rolledBackProtocol);
            }

            protocolEditDialog.dispose();
        });

        protocolEditDialog.getProtocolCvParamsCrudButton().addActionListener(e -> {
            //check if a CV param group is selected in the CV param summary list
            if (protocolEditDialog.getCvParamSummaryList().getSelectedIndex() != -1) {
                //get selected cvParamType from summary list
                CvParamType selectedCvParamType = (CvParamType) protocolEditDialog.getCvParamSummaryList().getSelectedValue();

                List<AuditableTypedCvParam> cvParams = cvParamService.findByCvParamByType(selectedCvParamType);

                //update the CV param list
                typedCvParamManagementController.updateDialog(selectedCvParamType, PRESELECTED_ONTOLOGY_NAMESPACES, cvParams);

                typedCvParamManagementController.showView();
            } else {
                eventBus.post(new MessageEvent("Protocol CV param type selection", "Please select a protocol CV param type to edit.", JOptionPane.INFORMATION_MESSAGE));
            }
        });
    }

    /**
     * Check if a protocol with the given protocol name exists in the database.
     *
     * @param protocol the protocol
     * @return does the protocol name exist
     */
    private boolean isExistingProtocolName(final Protocol protocol) {
        Long count = protocolService.countByName(protocol);

        return count != 0;
    }

    /**
     * Get the selected protocol in the protocol JList.
     *
     * @return the selected protocol
     */
    private Protocol getSelectedProtocol() {
        int selectedIndex = protocolManagementDialog.getProtocolList().getSelectedIndex();
        return (selectedIndex != -1) ? protocolBindingList.get(selectedIndex) : null;
    }

    /**
     * Create a default protocol, with some default properties.
     *
     * @return the default protocol
     */
    private Protocol createDefaultProtocol() {

        return new Protocol("default protocol name");
    }

    /**
     * Update the protocolToEdit with input from the protocolEditDialog.
     */
    public void updateProtocolToEdit() {
        protocolToEdit.setName(protocolEditDialog.getNameTextField().getText());
    }

    /**
     * Update the protocol edit dialog with the given protocol.
     *
     * @param protocol the Protocol
     */
    private void updateProtocolEditDialog(final Protocol protocol) {
        protocolToEdit = protocol;

        //check if the protocol has an ID.
        //If so, change the save button text and the info state label.
        if (protocolToEdit.getId() != null) {
            protocolEditDialog.getProtocolSaveOrUpdateButton().setText("update");
            protocolEditDialog.getProtocolStateInfoLabel().setText("");
        } else {
            protocolEditDialog.getProtocolSaveOrUpdateButton().setText("save");
            protocolEditDialog.getProtocolStateInfoLabel().setText("This protocol hasn't been stored in the database.");
        }

        protocolEditDialog.getNameTextField().setText(protocolToEdit.getName());

        //add the single CV params
        EnumMap<CvParamType, ProtocolCvParam> singleCvParams = new EnumMap<>(CvParamType.class);
        singleCvParams.put(CvParamType.REDUCTION, protocolToEdit.getReduction());
        singleCvParams.put(CvParamType.ENZYME, protocolToEdit.getEnzyme());
        singleCvParams.put(CvParamType.CELL_BASED, protocolToEdit.getCellBased());

        //add the multiple CV params
        EnumMap<CvParamType, List<ProtocolCvParam>> multipleCvParams = new EnumMap<>(CvParamType.class);
        multipleCvParams.put(CvParamType.CHEMICAL_LABELING, protocolToEdit.getChemicalLabels());
        multipleCvParams.put(CvParamType.OTHER, protocolToEdit.getOtherCvParams());
        typedCvParamSummaryListModel.update(singleCvParams, multipleCvParams);

        //clear selection in CV param summary list
        protocolEditDialog.getCvParamSummaryList().getSelectionModel().clearSelection();
    }

    /**
     * Clear the protocol detail fields.
     */
    private void clearProtocolDetailFields() {
        protocolManagementDialog.getProtocolDetailsTable().setModel(new TypedCvParamTableModel());
    }
}
