package com.compomics.colims.client.controller.admin;

import com.compomics.colims.client.compoment.DualList;
import com.compomics.colims.client.controller.Controllable;
import com.compomics.colims.client.controller.ColimsController;
import com.compomics.colims.client.event.EntityChangeEvent;
import com.compomics.colims.client.event.admin.ProtocolChangeEvent;
import com.compomics.colims.client.event.admin.CvTermChangeEvent;
import com.compomics.colims.client.event.message.DbConstraintMessageEvent;
import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.model.TypedCvTermSummaryListModel;
import com.compomics.colims.client.model.TypedCvTermTableModel;
import com.compomics.colims.client.renderer.TypedCvTermSummaryCellRenderer;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.admin.protocol.ProtocolEditDialog;
import com.compomics.colims.client.view.admin.protocol.ProtocolManagementDialog;
import com.compomics.colims.core.service.CvTermService;
import com.compomics.colims.core.service.ProtocolService;
import com.compomics.colims.model.AuditableTypedCvTerm;
import com.compomics.colims.model.Protocol;
import com.compomics.colims.model.ProtocolCvTerm;
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
@Component("protocolManagementController")
public class ProtocolManagementController implements Controllable {

    //model
    private TypedCvTermSummaryListModel<ProtocolCvTerm> typedCvTermSummaryListModel;
    private ObservableList<Protocol> protocolBindingList;
    private BindingGroup bindingGroup;
    private Protocol protocolToEdit;
    //view
    private ProtocolManagementDialog protocolManagementDialog;
    private ProtocolEditDialog protocolEditDialog;
    //parent controller
    @Autowired
    private ColimsController colimsController;
    @Autowired
    private CvTermManagementController cvTermManagementController;
    //services
    @Autowired
    private ProtocolService protocolService;
    @Autowired
    private CvTermService cvTermService;
    @Autowired
    private EventBus eventBus;

    /**
     *
     * @return
     */
    public ProtocolManagementDialog getProtocolManagementOverviewDialog() {
        return protocolManagementDialog;
    }

    @Override
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

        GuiUtils.centerDialogOnComponent(colimsController.getColimsFrame(), protocolManagementDialog);
        protocolManagementDialog.setVisible(true);
    }

    /**
     * Listen to a CV term change event posted by the
     * CvTermManagementController. If the ProtocolManagementDialog is visible,
     * clear the selection in the CV term summary list.
     *
     * @param cvTermChangeEvent the CvTermChangeEvent
     */
    @Subscribe
    public void onCvTermChangeEvent(final CvTermChangeEvent cvTermChangeEvent) {
        if (protocolEditDialog.isVisible()) {
            protocolEditDialog.getCvTermSummaryList().getSelectionModel().clearSelection();
        }
    }

    /**
     * Init the ProtocolManagementDialog.
     */
    private void initProtocolManagementDialog() {
        protocolManagementDialog = new ProtocolManagementDialog(colimsController.getColimsFrame(), true);

        //add binding
        protocolBindingList = ObservableCollections.observableList(protocolService.findAll());
        JListBinding protocolListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, protocolBindingList, protocolManagementDialog.getProtocolList());
        bindingGroup.addBinding(protocolListBinding);

        //add action listeners
        protocolManagementDialog.getProtocolList().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(final ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedIndex = protocolManagementDialog.getProtocolList().getSelectedIndex();
                    if (selectedIndex != -1 && protocolBindingList.get(selectedIndex) != null) {
                        Protocol selectedProtocol = protocolBindingList.get(selectedIndex);

                        //init CvTermModel
                        List<AuditableTypedCvTerm> cvTerms = new ArrayList<>();
                        if (selectedProtocol.getReduction() != null) {
                            cvTerms.add(selectedProtocol.getReduction());
                        }
                        if (selectedProtocol.getEnzyme() != null) {
                            cvTerms.add(selectedProtocol.getEnzyme());
                        }
                        if (selectedProtocol.getCellBased() != null) {
                            cvTerms.add(selectedProtocol.getCellBased());
                        }
                        for (ProtocolCvTerm chemicalLabeling : selectedProtocol.getChemicalLabels()) {
                            cvTerms.add(chemicalLabeling);
                        }
                        for (ProtocolCvTerm otherCvTerm : selectedProtocol.getOtherCvTerms()) {
                            cvTerms.add(otherCvTerm);
                        }
                        TypedCvTermTableModel typedCvTermTableModel = new TypedCvTermTableModel(cvTerms);
                        protocolManagementDialog.getProtocolDetailsTable().setModel(typedCvTermTableModel);
                    } else {
                        //clear detail view
                        clearProtocolDetailFields();
                    }
                }
            }
        });

        protocolManagementDialog.getAddProtocolButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                updateProtocolEditDialog(createDefaultProtocol());

                //show dialog
                GuiUtils.centerDialogOnComponent(protocolManagementDialog, protocolEditDialog);
                protocolEditDialog.setVisible(true);
            }
        });

        protocolManagementDialog.getDeleteProtocolButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (protocolManagementDialog.getProtocolList().getSelectedIndex() != -1) {
                    Protocol protocolToDelete = getSelectedProtocol();
                    //check if the protocol is already has an id.
                    //If so, delete the protocol from the db.
                    if (protocolToDelete.getId() != null) {
                        try {
                            protocolService.delete(protocolToDelete);

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
            }
        });

        protocolManagementDialog.getEditProtocolButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (protocolManagementDialog.getProtocolList().getSelectedIndex() != -1) {
                    updateProtocolEditDialog(getSelectedProtocol());
                    //show dialog
                    GuiUtils.centerDialogOnComponent(protocolManagementDialog, protocolEditDialog);
                    protocolEditDialog.setVisible(true);
                } else {
                    eventBus.post(new MessageEvent("Protocol selection", "Please select a protocol to edit.", JOptionPane.INFORMATION_MESSAGE));
                }
            }
        });

        protocolManagementDialog.getCancelProtocolManagementButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                protocolManagementDialog.dispose();
            }
        });

    }

    /**
     * Init the ProtocolEditDialog.
     */
    private void initProtocolEditDialog() {
        protocolEditDialog = new ProtocolEditDialog(protocolManagementDialog, true);

        //init dual list
        protocolEditDialog.getCvTermDualList().init(new CvTermAccessionComparator());

        //add binding
        Binding protocolNameBinding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, protocolManagementDialog.getProtocolList(), ELProperty.create("${selectedElement.name}"), protocolEditDialog.getNameTextField(), BeanProperty.create("text"), "protocolNameBinding");
        bindingGroup.addBinding(protocolNameBinding);

        //set model and renderer
        typedCvTermSummaryListModel = new TypedCvTermSummaryListModel();
        protocolEditDialog.getCvTermSummaryList().setModel(typedCvTermSummaryListModel);
        protocolEditDialog.getCvTermSummaryList().setCellRenderer(new TypedCvTermSummaryCellRenderer<ProtocolCvTerm>());

        //add action listeners
        protocolEditDialog.getCvTermSummaryList().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(final ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    if (protocolEditDialog.getCvTermSummaryList().getSelectedIndex() != -1) {
                        //get selected cvTermType from summary list
                        CvTermType selectedcvTermType = (CvTermType) protocolEditDialog.getCvTermSummaryList().getSelectedValue();

                        //load duallist for the selected cvTermType
                        List<ProtocolCvTerm> availableCvTerms = cvTermService.findByCvTermByType(ProtocolCvTerm.class, selectedcvTermType);

                        List<ProtocolCvTerm> addedCvTerms;
                        //@todo for the moment, protocol has only single CV terms,
                        //so this check is not necessary.
                        if (typedCvTermSummaryListModel.isSingleCvTerm(selectedcvTermType)) {
                            addedCvTerms = new ArrayList<>();
                            ProtocolCvTerm protocolCvTerm = typedCvTermSummaryListModel.getSingleCvTerms().get(selectedcvTermType);
                            //check for null value
                            if (protocolCvTerm != null) {
                                addedCvTerms.add(protocolCvTerm);
                            }
                            protocolEditDialog.getCvTermDualList().populateLists(availableCvTerms, addedCvTerms, 1);
                        } else {
                            addedCvTerms = typedCvTermSummaryListModel.getMultiCvTerms().get(selectedcvTermType);
                            protocolEditDialog.getCvTermDualList().populateLists(availableCvTerms, addedCvTerms);
                        }
                    } else {
                        protocolEditDialog.getCvTermDualList().clear();
                    }
                }
            }
        });

        protocolEditDialog.getCvTermDualList().addPropertyChangeListener(DualList.CHANGED, new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent evt) {
                //get selected cvTermType
                CvTermType selectedcvTermType = (CvTermType) protocolEditDialog.getCvTermSummaryList().getSelectedValue();

                List<ProtocolCvTerm> addedItems = (List<ProtocolCvTerm>) evt.getNewValue();

                //check for property
                if (selectedcvTermType.equals(CvTermType.REDUCTION)) {
                    if (!addedItems.isEmpty()) {
                        ProtocolCvTerm reduction = addedItems.get(0);
                        protocolToEdit.setReduction(reduction);
                        typedCvTermSummaryListModel.updateSingleCvTerm(CvTermType.REDUCTION, reduction);
                    } else {
                        protocolToEdit.setReduction(null);
                        typedCvTermSummaryListModel.updateSingleCvTerm(CvTermType.REDUCTION, null);
                    }
                } else if (selectedcvTermType.equals(CvTermType.ENZYME)) {
                    if (!addedItems.isEmpty()) {
                        ProtocolCvTerm enzyme = addedItems.get(0);
                        protocolToEdit.setEnzyme(enzyme);
                        typedCvTermSummaryListModel.updateSingleCvTerm(CvTermType.ENZYME, enzyme);
                    } else {
                        protocolToEdit.setEnzyme(null);
                        typedCvTermSummaryListModel.updateSingleCvTerm(CvTermType.ENZYME, null);
                    }
                } else if (selectedcvTermType.equals(CvTermType.CELL_BASED)) {
                    if (!addedItems.isEmpty()) {
                        ProtocolCvTerm cellBased = addedItems.get(0);
                        protocolToEdit.setCellBased(cellBased);
                        typedCvTermSummaryListModel.updateSingleCvTerm(CvTermType.CELL_BASED, cellBased);
                    } else {
                        protocolToEdit.setCellBased(null);
                        typedCvTermSummaryListModel.updateSingleCvTerm(CvTermType.CELL_BASED, null);
                    }
                } else if (selectedcvTermType.equals(CvTermType.CHEMICAL_LABELING)) {
                    protocolToEdit.setChemicalLabels(addedItems);
                    typedCvTermSummaryListModel.updateMultiCvTerm(CvTermType.CHEMICAL_LABELING, addedItems);
                } else if (selectedcvTermType.equals(CvTermType.OTHER)) {
                    protocolToEdit.setOtherCvTerms(addedItems);
                    typedCvTermSummaryListModel.updateMultiCvTerm(CvTermType.CHEMICAL_LABELING, addedItems);
                }

            }
        });

        protocolEditDialog.getProtocolSaveOrUpdateButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                //update with dialog input
                updateProtocolToEdit();

                //validate protocol
                List<String> validationMessages = GuiUtils.validateEntity(protocolToEdit);
                //check for a new protocol if the protocol name already exists in the db
                if (protocolToEdit.getId() == null && isExistingProtocolName(protocolToEdit)) {
                    validationMessages.add(protocolToEdit.getName() + " already exists in the database,"
                            + System.lineSeparator() + "please choose another protocol name.");
                }
                if (validationMessages.isEmpty()) {
                    int index;
                    EntityChangeEvent.Type type;
                    if (protocolToEdit.getId() != null) {
                        protocolService.update(protocolToEdit);
                        index = protocolManagementDialog.getProtocolList().getSelectedIndex();
                        type = EntityChangeEvent.Type.UPDATED;
                    } else {
                        protocolService.save(protocolToEdit);
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
            }
        });

        protocolEditDialog.getCancelProtocolEditButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                protocolEditDialog.dispose();
            }
        });

        protocolEditDialog.getProtocolCvTermsCrudButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                //check if a CV term group is selected in the CV term summary list
                if (protocolEditDialog.getCvTermSummaryList().getSelectedIndex() != -1) {
                    //get selected cvTermType from summary list
                    CvTermType selectedcvTermType = (CvTermType) protocolEditDialog.getCvTermSummaryList().getSelectedValue();

                    List<AuditableTypedCvTerm> cvTerms = cvTermService.findByCvTermByType(selectedcvTermType);

                    //update the CV term list
                    cvTermManagementController.updateDialog(selectedcvTermType, cvTerms);

                    cvTermManagementController.showView();
                } else {
                    eventBus.post(new MessageEvent("Protocol CV term type selection", "Please select a protocol CV term type to edit.", JOptionPane.INFORMATION_MESSAGE));
                }
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
        boolean isExistingProtocolName = true;
        Protocol foundProtocol = protocolService.findByName(protocol.getName());
        if (foundProtocol == null) {
            isExistingProtocolName = false;
        }

        return isExistingProtocolName;
    }

    /**
     * Get the selected protocol in the protocol JList.
     *
     * @return the selected protocol
     */
    private Protocol getSelectedProtocol() {
        int selectedIndex = protocolManagementDialog.getProtocolList().getSelectedIndex();
        Protocol selectedProtocol = (selectedIndex != -1) ? protocolBindingList.get(selectedIndex) : null;
        return selectedProtocol;
    }

    /**
     * Create a default protocol, with some default properties.
     *
     * @return the default protocol
     */
    private Protocol createDefaultProtocol() {
        Protocol defaultProtocol = new Protocol("default protocol name");

        return defaultProtocol;
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

        //add the single CV terms
        EnumMap<CvTermType, ProtocolCvTerm> singleCvTerms = new EnumMap<>(CvTermType.class);
        singleCvTerms.put(CvTermType.REDUCTION, protocolToEdit.getReduction());
        singleCvTerms.put(CvTermType.ENZYME, protocolToEdit.getEnzyme());
        singleCvTerms.put(CvTermType.CELL_BASED, protocolToEdit.getCellBased());

        //add the multiple CV terms
        EnumMap<CvTermType, List<ProtocolCvTerm>> multipleCvTerms = new EnumMap<>(CvTermType.class);
        multipleCvTerms.put(CvTermType.CHEMICAL_LABELING, protocolToEdit.getChemicalLabels());
        multipleCvTerms.put(CvTermType.OTHER, protocolToEdit.getOtherCvTerms());
        typedCvTermSummaryListModel.update(singleCvTerms, multipleCvTerms);

        //clear selection in CV term summary list
        protocolEditDialog.getCvTermSummaryList().getSelectionModel().clearSelection();
    }

    /**
     * Clear the protocol detail fields.
     */
    private void clearProtocolDetailFields() {
        protocolManagementDialog.getProtocolDetailsTable().setModel(new TypedCvTermTableModel());
    }
}
