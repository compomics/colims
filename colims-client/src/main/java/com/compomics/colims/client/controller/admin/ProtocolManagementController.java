package com.compomics.colims.client.controller.admin;

import com.compomics.colims.client.compoment.DualList;
import com.compomics.colims.client.controller.Controllable;
import com.compomics.colims.client.controller.ColimsController;
import com.compomics.colims.client.controller.admin.CvTermManagementController;
import com.compomics.colims.client.event.CvTermChangeEvent;
import com.compomics.colims.client.event.DbConstraintMessageEvent;
import com.compomics.colims.client.event.MessageEvent;
import com.compomics.colims.client.model.CvTermSummaryListModel;
import com.compomics.colims.client.model.CvTermTableModel;
import com.compomics.colims.client.renderer.CvTermSummaryCellRenderer;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.admin.protocol.ProtocolEditDialog;
import com.compomics.colims.client.view.admin.protocol.ProtocolManagementDialog;
import com.compomics.colims.core.service.CvTermService;
import com.compomics.colims.core.service.ProtocolService;
import com.compomics.colims.model.CvTerm;
import com.compomics.colims.model.InstrumentCvTerm;
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
    private CvTermSummaryListModel<ProtocolCvTerm> cvTermSummaryListModel;
    private ObservableList<Protocol> protocolBindingList;
    private BindingGroup bindingGroup;
    //view
    private ProtocolManagementDialog protocolManagementDialog;
    private ProtocolEditDialog protocolEditDialog;
    //parent controller
    @Autowired
    private ColimsController mainController;
    @Autowired
    private CvTermManagementController cvTermManagementController;
    //services
    @Autowired
    private ProtocolService protocolService;
    @Autowired
    private CvTermService cvTermService;
    @Autowired
    private EventBus eventBus;

    public ProtocolManagementController() {
    }

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
        
        protocolManagementDialog.setVisible(true);        
    } 
    
    /**
     * Listen to a CV term change event posted by the
     * CvTermManagementController. If the ProtocolManagementDialog is visible,
     * clear the selection in the CV term summary list.
     */
    @Subscribe
    public void onCvTermChangeEvent(CvTermChangeEvent cvTermChangeEvent) {
        if (protocolEditDialog.isVisible()) {
            protocolEditDialog.getCvTermSummaryList().getSelectionModel().clearSelection();
        }
    }

    private void initProtocolManagementDialog() {
        protocolManagementDialog = new ProtocolManagementDialog(mainController.getColimsFrame(), true);

        //add binding
        protocolBindingList = ObservableCollections.observableList(protocolService.findAll());
        JListBinding protocolListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, protocolBindingList, protocolManagementDialog.getProtocolList());
        bindingGroup.addBinding(protocolListBinding);

        //add action listeners
        protocolManagementDialog.getProtocolList().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    if (protocolManagementDialog.getProtocolList().getSelectedIndex() != -1) {
                        Protocol selectedProtocol = protocolBindingList.get(protocolManagementDialog.getProtocolList().getSelectedIndex());

                        //check if the protocol has an ID.
                        //If so, change the save button text and the info state label.
                        if (selectedProtocol.getId() != null) {
                            protocolManagementDialog.getProtocolStateInfoLabel().setText("");
                        } else {
                            protocolManagementDialog.getProtocolStateInfoLabel().setText("This protocol hasn't been persisted to the database.");
                        }

                        //init CvTermModel
                        List<CvTerm> cvTerms = new ArrayList<>();
                        cvTerms.add(selectedProtocol.getReduction());
                        cvTerms.add(selectedProtocol.getEnzyme());
                        cvTerms.add(selectedProtocol.getCellBased());
                        for (ProtocolCvTerm chemicalLabeling : selectedProtocol.getChemicalLabels()) {
                            cvTerms.add(chemicalLabeling);
                        }
                        for (ProtocolCvTerm otherCvTerm : selectedProtocol.getOtherCvTerms()) {
                            cvTerms.add(otherCvTerm);
                        }
                        CvTermTableModel cvTermTableModel = new CvTermTableModel(cvTerms);
                        protocolManagementDialog.getProtocolDetailsTable().setModel(cvTermTableModel);
                    } else {
                        //clear detail view
                        clearProtocolDetailFields();
                    }
                }
            }
        });

        protocolManagementDialog.getAddProtocolButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Protocol defaultProtocol = createDefaultProtocol();
                protocolBindingList.add(defaultProtocol);
                protocolManagementDialog.getProtocolList().setSelectedIndex(protocolBindingList.size() - 1);
            }
        });

        protocolManagementDialog.getDeleteProtocolButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (protocolManagementDialog.getProtocolList().getSelectedIndex() != -1) {
                    Protocol protocolToDelete = getSelectedProtocol();
                    //check if the protocol is already has an id.
                    //If so, delete the protocol from the db.
                    if (protocolToDelete.getId() != null) {
                        try {
                            protocolService.delete(protocolToDelete);

                            protocolBindingList.remove(protocolManagementDialog.getProtocolList().getSelectedIndex());
                            protocolManagementDialog.getProtocolList().getSelectionModel().clearSelection();
                        } catch (DataIntegrityViolationException dive) {
                            //check if the protocol can be deleted without breaking existing database relations,
                            //i.e. are there any constraints violations
                            if (dive.getCause() instanceof ConstraintViolationException) {
                                DbConstraintMessageEvent dbConstraintMessageEvent = new DbConstraintMessageEvent(protocolToDelete.getName());
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
                }
            }
        });

        protocolManagementDialog.getEditProtocolButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (protocolManagementDialog.getProtocolList().getSelectedIndex() != -1) {
                    updateProtocolEditDialog(getSelectedProtocol());
                    //show dialog
                    protocolEditDialog.setLocationRelativeTo(null);
                    protocolEditDialog.setVisible(true);
                }
            }
        });

        protocolManagementDialog.getCloseProtocolManagementButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                protocolManagementDialog.dispose();
            }
        });

    }

    private void initProtocolEditDialog() {
        protocolEditDialog = new ProtocolEditDialog(mainController.getColimsFrame(), true);
        
        //init dual list
        protocolEditDialog.getCvTermDualList().init(new CvTermAccessionComparator());

        //add binding
        Binding protocolNameBinding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, protocolManagementDialog.getProtocolList(), ELProperty.create("${selectedElement.name}"), protocolEditDialog.getNameTextField(), BeanProperty.create("text"), "protocolNameBinding");
        bindingGroup.addBinding(protocolNameBinding);

        //set model and renderer
        cvTermSummaryListModel = new CvTermSummaryListModel();
        protocolEditDialog.getCvTermSummaryList().setModel(cvTermSummaryListModel);
        protocolEditDialog.getCvTermSummaryList().setCellRenderer(new CvTermSummaryCellRenderer<ProtocolCvTerm>());

        //add action listeners
        protocolEditDialog.getCvTermSummaryList().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    if (protocolEditDialog.getCvTermSummaryList().getSelectedIndex() != -1) {
                        //get selected cvTermType from summary list                        
                        CvTermType selectedcvTermType = (CvTermType) protocolEditDialog.getCvTermSummaryList().getSelectedValue();

                        //load duallist for the selected cvTermType
                        List<ProtocolCvTerm> availableCvTerms = cvTermService.findByCvTermByType(ProtocolCvTerm.class, selectedcvTermType);

                        List<ProtocolCvTerm> addedCvTerms;
                        //@todo for the moment, protocol has only single CV terms,
                        //so this check is not necessary.
                        if (cvTermSummaryListModel.isSingleCvTerm(selectedcvTermType)) {
                            addedCvTerms = new ArrayList<>();
                            ProtocolCvTerm protocolCvTerm = cvTermSummaryListModel.getSingleCvTerms().get(selectedcvTermType);
                            //check for null value
                            if (protocolCvTerm != null) {
                                addedCvTerms.add(protocolCvTerm);
                            }
                            protocolEditDialog.getCvTermDualList().populateLists(availableCvTerms, addedCvTerms, 1);
                        } else {
                            addedCvTerms = cvTermSummaryListModel.getMultiCvTerms().get(selectedcvTermType);
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
            public void propertyChange(PropertyChangeEvent evt) {
                //get selected cvTermType                        
                CvTermType selectedcvTermType = (CvTermType) protocolEditDialog.getCvTermSummaryList().getSelectedValue();

                Protocol protocol = getSelectedProtocol();
                List<ProtocolCvTerm> addedItems = protocolEditDialog.getCvTermDualList().getAddedItems();

                //check for property
                if (selectedcvTermType.equals(CvTermType.REDUCTION)) {
                    if (!addedItems.isEmpty()) {
                        ProtocolCvTerm reduction = addedItems.get(0);
                        protocol.setReduction(reduction);
                        cvTermSummaryListModel.updateSingleCvTerm(CvTermType.REDUCTION, reduction);
                    } else {
                        protocol.setReduction(null);
                        cvTermSummaryListModel.updateSingleCvTerm(CvTermType.REDUCTION, null);
                    }
                } else if (selectedcvTermType.equals(CvTermType.ENZYME)) {
                    if (!addedItems.isEmpty()) {
                        ProtocolCvTerm enzyme = addedItems.get(0);
                        protocol.setEnzyme(enzyme);
                        cvTermSummaryListModel.updateSingleCvTerm(CvTermType.ENZYME, enzyme);
                    } else {
                        protocol.setEnzyme(null);
                        cvTermSummaryListModel.updateSingleCvTerm(CvTermType.ENZYME, null);
                    }
                } else if (selectedcvTermType.equals(CvTermType.CELL_BASED)) {
                    if (!addedItems.isEmpty()) {
                        ProtocolCvTerm cellBased = addedItems.get(0);
                        protocol.setCellBased(cellBased);
                        cvTermSummaryListModel.updateSingleCvTerm(CvTermType.CELL_BASED, cellBased);
                    } else {
                        protocol.setCellBased(null);
                        cvTermSummaryListModel.updateSingleCvTerm(CvTermType.CELL_BASED, null);
                    }
                } else if (selectedcvTermType.equals(CvTermType.CHEMICAL_LABELING)) {
                    protocol.setChemicalLabels(addedItems);
                    cvTermSummaryListModel.updateMultiCvTerm(CvTermType.CHEMICAL_LABELING, addedItems);
                } else if (selectedcvTermType.equals(CvTermType.OTHER)) {
                    protocol.setOtherCvTerms(addedItems);
                    cvTermSummaryListModel.updateMultiCvTerm(CvTermType.CHEMICAL_LABELING, addedItems);
                }

            }
        });

        protocolEditDialog.getProtocolSaveOrUpdateButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Protocol selectedProtocol = getSelectedProtocol();
                //validate protocol
                List<String> validationMessages = GuiUtils.validateEntity(selectedProtocol);
                //check for a new protocol if the protocol name already exists in the db                
                if (selectedProtocol.getId() == null && isExistingProtocolName(selectedProtocol)) {
                    validationMessages.add(selectedProtocol.getName() + " already exists in the database, please choose another protocol name.");
                }
                if (validationMessages.isEmpty()) {
                    if (selectedProtocol.getId() != null) {
                        protocolService.update(selectedProtocol);
                    } else {
                        protocolService.save(selectedProtocol);
                    }
                    protocolEditDialog.getProtocolSaveOrUpdateButton().setText("update");

                    MessageEvent messageEvent = new MessageEvent("Protocol persist confirmation", "Protocol " + selectedProtocol.getName() + " was persisted successfully!", JOptionPane.INFORMATION_MESSAGE);
                    eventBus.post(messageEvent);

                    //refresh selection in protocol list in management overview dialog
                    int index = protocolManagementDialog.getProtocolList().getSelectedIndex();
                    protocolManagementDialog.getProtocolList().getSelectionModel().clearSelection();
                    protocolManagementDialog.getProtocolList().setSelectedIndex(index);
                } else {
                    MessageEvent messageEvent = new MessageEvent("Validation failure", validationMessages, JOptionPane.ERROR_MESSAGE);
                    eventBus.post(messageEvent);
                }
            }
        });

        protocolEditDialog.getCloseProtocolEditButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                protocolEditDialog.dispose();
            }
        });

        protocolEditDialog.getProtocolCvTermsCrudButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //check if a CV term group is selected in the CV term summary list
                if (protocolEditDialog.getCvTermSummaryList().getSelectedIndex() != -1) {
                    //get selected cvTermType from summary list                        
                    CvTermType selectedcvTermType = (CvTermType) protocolEditDialog.getCvTermSummaryList().getSelectedValue();

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
     * Check if a protocol with the given protocol name exists in the database.
     *
     * @param protocol the protocol
     * @return does the protocol name exist
     */
    private boolean isExistingProtocolName(Protocol protocol) {
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

    private void updateProtocolEditDialog(Protocol protocol) {
        if (protocol.getId() != null) {
            protocolEditDialog.getProtocolSaveOrUpdateButton().setText("update");
        } else {
            protocolEditDialog.getProtocolSaveOrUpdateButton().setText("save");
        }

        //add the single CV terms
        EnumMap<CvTermType, ProtocolCvTerm> singleCvTerms = new EnumMap<>(CvTermType.class);
        singleCvTerms.put(CvTermType.REDUCTION, protocol.getReduction());
        singleCvTerms.put(CvTermType.ENZYME, protocol.getEnzyme());
        singleCvTerms.put(CvTermType.CELL_BASED, protocol.getCellBased());

        //add the multiple CV terms
        EnumMap<CvTermType, List<ProtocolCvTerm>> multipleCvTerms = new EnumMap<>(CvTermType.class);
        multipleCvTerms.put(CvTermType.CHEMICAL_LABELING, protocol.getChemicalLabels());
        multipleCvTerms.put(CvTermType.OTHER, protocol.getOtherCvTerms());
        cvTermSummaryListModel.update(singleCvTerms, multipleCvTerms);

        //clear selection in CV term summary list
        protocolEditDialog.getCvTermSummaryList().getSelectionModel().clearSelection();
    }

    /**
     * Clear the protocol detail fields
     */
    private void clearProtocolDetailFields() {
        protocolManagementDialog.getProtocolStateInfoLabel().setText("");
        protocolManagementDialog.getProtocolDetailsTable().setModel(new CvTermTableModel());
    }
}
