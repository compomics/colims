package com.compomics.colims.client.controller.admin;

import com.compomics.colims.client.compoment.DualList;
import com.compomics.colims.client.controller.Controllable;
import com.compomics.colims.client.controller.ColimsController;
import com.compomics.colims.client.event.EntityChangeEvent;
import com.compomics.colims.client.event.admin.InstrumentChangeEvent;
import com.compomics.colims.client.event.admin.CvTermChangeEvent;
import com.compomics.colims.client.event.message.DbConstraintMessageEvent;
import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.model.TypedCvTermSummaryListModel;
import com.compomics.colims.client.model.TypedCvTermTableModel;
import com.compomics.colims.client.renderer.TypedCvTermSummaryCellRenderer;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.admin.instrument.InstrumentEditDialog;
import com.compomics.colims.client.view.admin.instrument.InstrumentManagementDialog;
import com.compomics.colims.core.service.CvTermService;
import com.compomics.colims.core.service.InstrumentService;
import com.compomics.colims.model.AuditableTypedCvTerm;
import com.compomics.colims.model.Instrument;
import com.compomics.colims.model.InstrumentCvTerm;
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
import org.jdesktop.beansbinding.BindingGroup;
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
@Component("instrumentManagementController")
public class InstrumentManagementController implements Controllable {

    //model
    private TypedCvTermSummaryListModel<InstrumentCvTerm> typedCvTermSummaryListModel;
    private ObservableList<Instrument> instrumentBindingList;
    private BindingGroup bindingGroup;
    private Instrument instrumentToEdit;
    //view
    private InstrumentManagementDialog instrumentManagementDialog;
    private InstrumentEditDialog instrumentEditDialog;
    //parent controller
    @Autowired
    private ColimsController colimsController;
    @Autowired
    private CvTermManagementController cvTermManagementController;
    //services
    @Autowired
    private InstrumentService instrumentService;
    @Autowired
    private CvTermService cvTermService;
    @Autowired
    private EventBus eventBus;

    /**
     *
     */
    public InstrumentManagementController() {
    }

    /**
     *
     * @return
     */
    public InstrumentManagementDialog getInstrumentManagementOverviewDialog() {
        return instrumentManagementDialog;
    }

    @Override
    public void init() {
        //register to event bus
        eventBus.register(this);

        //init binding
        bindingGroup = new BindingGroup();

        //init views
        initInstrumentManagementDialog();
        initInstrumentEditDialog();

        bindingGroup.bind();
    }

    @Override
    public void showView() {
        //clear selection
        instrumentManagementDialog.getInstrumentList().getSelectionModel().clearSelection();

        GuiUtils.centerDialogOnComponent(colimsController.getColimsFrame(), instrumentManagementDialog);
        instrumentManagementDialog.setVisible(true);
    }

    /**
     * Listen to a CV term change event posted by the
     * CvTermManagementController. If the InstrumentManagementDialog is visible,
     * clear the selection in the CV term summary list.
     *
     * @param cvTermChangeEvent
     */
    @Subscribe
    public void onCvTermChangeEvent(CvTermChangeEvent cvTermChangeEvent) {
        if (instrumentEditDialog.isVisible()) {
            instrumentEditDialog.getCvTermSummaryList().getSelectionModel().clearSelection();
        }
    }

    private void initInstrumentManagementDialog() {
        instrumentManagementDialog = new InstrumentManagementDialog(colimsController.getColimsFrame(), true);

        //add binding
        instrumentBindingList = ObservableCollections.observableList(instrumentService.findAll());
        JListBinding instrumentListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, instrumentBindingList, instrumentManagementDialog.getInstrumentList());
        bindingGroup.addBinding(instrumentListBinding);

        //add action listeners
        instrumentManagementDialog.getInstrumentList().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedIndex = instrumentManagementDialog.getInstrumentList().getSelectedIndex();
                    if (selectedIndex != -1 && instrumentBindingList.get(selectedIndex) != null) {
                        Instrument selectedInstrument = instrumentBindingList.get(selectedIndex);

                        //init CvTermModel
                        List<AuditableTypedCvTerm> cvTerms = new ArrayList<>();
                        if (selectedInstrument.getType() != null) {
                            cvTerms.add(selectedInstrument.getType());
                        }
                        if (selectedInstrument.getSource() != null) {
                            cvTerms.add(selectedInstrument.getSource());
                        }
                        if (selectedInstrument.getDetector() != null) {
                            cvTerms.add(selectedInstrument.getDetector());
                        }
                        for (InstrumentCvTerm analyzer : selectedInstrument.getAnalyzers()) {
                            cvTerms.add(analyzer);
                        }
                        TypedCvTermTableModel typedCvTermTableModel = new TypedCvTermTableModel(cvTerms);
                        instrumentManagementDialog.getInstrumentDetailsTable().setModel(typedCvTermTableModel);
                    } else {
                        //clear detail view
                        clearInstrumentDetailFields();
                    }
                }
            }
        });

        instrumentManagementDialog.getAddInstrumentButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                updateInstrumentEditDialog(createDefaultInstrument());

                //show dialog
                GuiUtils.centerDialogOnComponent(instrumentManagementDialog, instrumentEditDialog);
                instrumentEditDialog.setVisible(true);
            }
        });

        instrumentManagementDialog.getDeleteInstrumentButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (instrumentManagementDialog.getInstrumentList().getSelectedIndex() != -1) {
                    Instrument instrumentToDelete = getSelectedInstrument();
                    //check if the instrument is already has an id.
                    //If so, delete the instrument from the db.
                    if (instrumentToDelete.getId() != null) {
                        try {
                            instrumentService.delete(instrumentToDelete);

                            instrumentBindingList.remove(instrumentManagementDialog.getInstrumentList().getSelectedIndex());
                            instrumentManagementDialog.getInstrumentList().getSelectionModel().clearSelection();

                            eventBus.post(new InstrumentChangeEvent(EntityChangeEvent.Type.DELETED));
                        } catch (DataIntegrityViolationException dive) {
                            //check if the instrument can be deleted without breaking existing database relations,
                            //i.e. are there any constraints violations
                            if (dive.getCause() instanceof ConstraintViolationException) {
                                DbConstraintMessageEvent dbConstraintMessageEvent = new DbConstraintMessageEvent("instrument", instrumentToDelete.getName());
                                eventBus.post(dbConstraintMessageEvent);
                            } else {
                                //pass the exception
                                throw dive;
                            }
                        }
                    } else {
                        instrumentBindingList.remove(instrumentManagementDialog.getInstrumentList().getSelectedIndex());
                        instrumentManagementDialog.getInstrumentList().getSelectionModel().clearSelection();
                    }
                } else {
                    eventBus.post(new MessageEvent("Instrument selection", "Please select an instrument to delete.", JOptionPane.INFORMATION_MESSAGE));
                }
            }
        });

        instrumentManagementDialog.getEditInstrumentButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (instrumentManagementDialog.getInstrumentList().getSelectedIndex() != -1) {
                    updateInstrumentEditDialog(getSelectedInstrument());

                    //show dialog
                    GuiUtils.centerDialogOnComponent(instrumentManagementDialog, instrumentEditDialog);
                    instrumentEditDialog.setVisible(true);
                } else {
                    eventBus.post(new MessageEvent("Instrument selection", "Please select an instrument to edit.", JOptionPane.INFORMATION_MESSAGE));
                }
            }
        }
        );

        instrumentManagementDialog.getCancelInstrumentManagementButton()
                .addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        instrumentManagementDialog.dispose();
                    }
                }
                );

    }

    private void initInstrumentEditDialog() {
        instrumentEditDialog = new InstrumentEditDialog(instrumentManagementDialog, true);

        //init dual list
        instrumentEditDialog.getCvTermDualList().init(new CvTermAccessionComparator());

        //set model and renderer
        typedCvTermSummaryListModel = new TypedCvTermSummaryListModel();
        instrumentEditDialog.getCvTermSummaryList().setModel(typedCvTermSummaryListModel);
        instrumentEditDialog.getCvTermSummaryList().setCellRenderer(new TypedCvTermSummaryCellRenderer<InstrumentCvTerm>());

        //add action listeners
        instrumentEditDialog.getCvTermSummaryList().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(final ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    if (instrumentEditDialog.getCvTermSummaryList().getSelectedIndex() != -1) {
                        //get selected cvTermType from summary list
                        CvTermType selectedcvTermType = (CvTermType) instrumentEditDialog.getCvTermSummaryList().getSelectedValue();

                        //load duallist for the selected cvTermType
                        List<InstrumentCvTerm> availableCvTerms = cvTermService.findByCvTermByType(InstrumentCvTerm.class, selectedcvTermType);

                        List<InstrumentCvTerm> addedCvTerms;

                        if (typedCvTermSummaryListModel.isSingleCvTerm(selectedcvTermType)) {
                            addedCvTerms = new ArrayList<>();
                            InstrumentCvTerm instrumentCvTerm = typedCvTermSummaryListModel.getSingleCvTerms().get(selectedcvTermType);
                            //check for null value
                            if (instrumentCvTerm != null) {
                                addedCvTerms.add(instrumentCvTerm);
                            }
                            instrumentEditDialog.getCvTermDualList().populateLists(availableCvTerms, addedCvTerms, 1);
                        } else {
                            addedCvTerms = typedCvTermSummaryListModel.getMultiCvTerms().get(selectedcvTermType);
                            instrumentEditDialog.getCvTermDualList().populateLists(availableCvTerms, addedCvTerms);
                        }
                    } else {
                        instrumentEditDialog.getCvTermDualList().clear();
                    }
                }
            }
        });

        instrumentEditDialog.getCvTermDualList().addPropertyChangeListener(DualList.CHANGED, new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent evt) {
                //get selected cvTermType
                CvTermType selectedcvTermType = (CvTermType) instrumentEditDialog.getCvTermSummaryList().getSelectedValue();

                List<InstrumentCvTerm> addedItems = (List<InstrumentCvTerm>) evt.getNewValue();

                //check for property
                if (selectedcvTermType.equals(CvTermType.TYPE)) {
                    if (!addedItems.isEmpty()) {
                        InstrumentCvTerm type = addedItems.get(0);
                        instrumentToEdit.setType(type);
                        typedCvTermSummaryListModel.updateSingleCvTerm(CvTermType.TYPE, type);
                    } else {
                        instrumentToEdit.setType(null);
                        typedCvTermSummaryListModel.updateSingleCvTerm(CvTermType.TYPE, null);
                    }
                } else if (selectedcvTermType.equals(CvTermType.SOURCE)) {
                    if (!addedItems.isEmpty()) {
                        InstrumentCvTerm source = addedItems.get(0);
                        instrumentToEdit.setSource(source);
                        typedCvTermSummaryListModel.updateSingleCvTerm(CvTermType.SOURCE, source);
                    } else {
                        instrumentToEdit.setSource(null);
                        typedCvTermSummaryListModel.updateSingleCvTerm(CvTermType.SOURCE, null);
                    }
                } else if (selectedcvTermType.equals(CvTermType.DETECTOR)) {
                    if (!addedItems.isEmpty()) {
                        InstrumentCvTerm detector = addedItems.get(0);
                        instrumentToEdit.setDetector(detector);
                        typedCvTermSummaryListModel.updateSingleCvTerm(CvTermType.DETECTOR, detector);
                    } else {
                        instrumentToEdit.setDetector(null);
                        typedCvTermSummaryListModel.updateSingleCvTerm(CvTermType.DETECTOR, null);
                    }
                } else if (selectedcvTermType.equals(CvTermType.ANALYZER)) {
                    instrumentToEdit.setAnalyzers(addedItems);
                    typedCvTermSummaryListModel.updateMultiCvTerm(CvTermType.ANALYZER, addedItems);
                }

            }
        });

        instrumentEditDialog.getInstrumentSaveOrUpdateButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                //update with dialog input
                updateInstrumentToEdit();

                //validate instrument
                List<String> validationMessages = GuiUtils.validateEntity(instrumentToEdit);
                //check for a new instrument if the instrument name already exists in the db
                if (instrumentToEdit.getId() == null && isExistingInstrumentName(instrumentToEdit)) {
                    validationMessages.add(instrumentToEdit.getName() + " already exists in the database,"
                            + System.lineSeparator() + "please choose another instrument name.");
                }
                if (validationMessages.isEmpty()) {
                    int index;
                    EntityChangeEvent.Type type;
                    if (instrumentToEdit.getId() != null) {
                        instrumentService.update(instrumentToEdit);
                        index = instrumentManagementDialog.getInstrumentList().getSelectedIndex();
                        type = EntityChangeEvent.Type.UPDATED;
                    } else {
                        instrumentService.save(instrumentToEdit);
                        //add instrument to overview list
                        instrumentBindingList.add(instrumentToEdit);
                        index = instrumentBindingList.size() - 1;
                        instrumentEditDialog.getInstrumentStateInfoLabel().setText("");
                        type = EntityChangeEvent.Type.CREATED;
                    }
                    instrumentEditDialog.getInstrumentSaveOrUpdateButton().setText("update");

                    eventBus.post(new InstrumentChangeEvent(type));

                    MessageEvent messageEvent = new MessageEvent("Instrument store confirmation", "Instrument " + instrumentToEdit.getName() + " was stored successfully!", JOptionPane.INFORMATION_MESSAGE);
                    eventBus.post(messageEvent);

                    //refresh selection in instrument list in management overview dialog
                    instrumentManagementDialog.getInstrumentList().getSelectionModel().clearSelection();
                    instrumentManagementDialog.getInstrumentList().setSelectedIndex(index);
                } else {
                    MessageEvent messageEvent = new MessageEvent("Validation failure", validationMessages, JOptionPane.WARNING_MESSAGE);
                    eventBus.post(messageEvent);
                }
            }
        });

        instrumentEditDialog.getCancelInstrumentEditButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                instrumentEditDialog.dispose();
            }
        });

        instrumentEditDialog.getInstrumentCvTermsCrudButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                //check if a CV term group is selected in the CV term summary list
                if (instrumentEditDialog.getCvTermSummaryList().getSelectedIndex() != -1) {
                    //get selected cvTermType from summary list
                    CvTermType selectedcvTermType = (CvTermType) instrumentEditDialog.getCvTermSummaryList().getSelectedValue();

                    List<AuditableTypedCvTerm> cvTerms = cvTermService.findByCvTermByType(selectedcvTermType);

                    //update the CV term list
                    cvTermManagementController.updateDialog(selectedcvTermType, cvTerms);

                    cvTermManagementController.showView();
                } else {
                    eventBus.post(new MessageEvent("Instrument CV term type selection", "Please select an instrument CV term type to edit.", JOptionPane.INFORMATION_MESSAGE));
                }
            }
        });
    }

    /**
     * Check if a instrument with the given instrument name exists in the
     * database.
     *
     * @param instrument the instrument
     * @return does the instrument name exist
     */
    private boolean isExistingInstrumentName(final Instrument instrument) {
        boolean isExistingInstrumentName = true;
        Instrument foundInstrument = instrumentService.findByName(instrument.getName());
        if (foundInstrument == null) {
            isExistingInstrumentName = false;
        }

        return isExistingInstrumentName;
    }

    /**
     * Get the selected instrument in the instrument JList.
     *
     * @return the selected instrument
     */
    private Instrument getSelectedInstrument() {
        int selectedIndex = instrumentManagementDialog.getInstrumentList().getSelectedIndex();
        Instrument selectedInstrument = (selectedIndex != -1) ? instrumentBindingList.get(selectedIndex) : null;
        return selectedInstrument;
    }

    /**
     * Create a default instrument, with some default properties.
     *
     * @return the default instrument
     */
    private Instrument createDefaultInstrument() {
        Instrument defaultInstrument = new Instrument("default instrument name");
        //find instrument types
        List<InstrumentCvTerm> types = cvTermService.findByCvTermByType(InstrumentCvTerm.class, CvTermType.TYPE);
        if (!types.isEmpty()) {
            defaultInstrument.setType(types.get(0));

        }
        //find sources
        List<InstrumentCvTerm> sources = cvTermService.findByCvTermByType(InstrumentCvTerm.class, CvTermType.SOURCE);
        if (!sources.isEmpty()) {
            defaultInstrument.setSource(sources.get(0));
        }
        //find detectors
        List<InstrumentCvTerm> detectors = cvTermService.findByCvTermByType(InstrumentCvTerm.class, CvTermType.DETECTOR);

        if (!detectors.isEmpty()) {
            defaultInstrument.setDetector(detectors.get(0));
        }
        //find analyzers
        List<InstrumentCvTerm> analyzers = cvTermService.findByCvTermByType(InstrumentCvTerm.class, CvTermType.ANALYZER);

        if (!analyzers.isEmpty()) {
            List<InstrumentCvTerm> defaultAnalyzers = new ArrayList<>();
            defaultAnalyzers.add(analyzers.get(0));
            defaultInstrument.setAnalyzers(defaultAnalyzers);
        }
        return defaultInstrument;
    }

    /**
     * Update the instrumentToEdit with input from the instrumentEditDialog.
     */
    public void updateInstrumentToEdit() {
        instrumentToEdit.setName(instrumentEditDialog.getNameTextField().getText());
    }

    /**
     * Update the instrument edit dialog with the given instrument.
     *
     * @param instrument the Instrument
     */
    private void updateInstrumentEditDialog(final Instrument instrument) {
        instrumentToEdit = instrument;

        //check if the instrument has an ID.
        //If so, change the save button text and the info state label.
        if (instrumentToEdit.getId() != null) {
            instrumentEditDialog.getInstrumentSaveOrUpdateButton().setText("update");
            instrumentEditDialog.getInstrumentStateInfoLabel().setText("");
        } else {
            instrumentEditDialog.getInstrumentSaveOrUpdateButton().setText("save");
            instrumentEditDialog.getInstrumentStateInfoLabel().setText("This instrument hasn't been stored in the database.");
        }

        instrumentEditDialog.getNameTextField().setText(instrumentToEdit.getName());

        //add the single CV terms
        EnumMap<CvTermType, InstrumentCvTerm> singleCvTerms = new EnumMap<>(CvTermType.class
        );
        singleCvTerms.put(CvTermType.TYPE, instrumentToEdit.getType());
        singleCvTerms.put(CvTermType.SOURCE, instrumentToEdit.getSource());
        singleCvTerms.put(CvTermType.DETECTOR, instrumentToEdit.getDetector());

        //add the multiple CV terms
        EnumMap<CvTermType, List<InstrumentCvTerm>> multipleCvTerms = new EnumMap<>(CvTermType.class);

        multipleCvTerms.put(CvTermType.ANALYZER, instrumentToEdit.getAnalyzers());
        typedCvTermSummaryListModel.update(singleCvTerms, multipleCvTerms);

        //clear selection in CV term summary list
        instrumentEditDialog.getCvTermSummaryList()
                .getSelectionModel().clearSelection();
    }

    /**
     * Clear the instrument detail fields.
     */
    private void clearInstrumentDetailFields() {
        instrumentManagementDialog.getInstrumentDetailsTable().setModel(new TypedCvTermTableModel());
    }
}
