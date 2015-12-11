package com.compomics.colims.client.controller.admin;

import com.compomics.colims.client.compoment.DualList;
import com.compomics.colims.client.controller.Controllable;
import com.compomics.colims.client.controller.MainController;
import com.compomics.colims.client.event.EntityChangeEvent;
import com.compomics.colims.client.event.admin.CvParamChangeEvent;
import com.compomics.colims.client.event.admin.InstrumentChangeEvent;
import com.compomics.colims.client.event.message.DbConstraintMessageEvent;
import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.model.TypedCvParamSummaryListModel;
import com.compomics.colims.client.model.table.model.TypedCvParamTableModel;
import com.compomics.colims.client.renderer.TypedCvParamSummaryCellRenderer;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.admin.instrument.InstrumentEditDialog;
import com.compomics.colims.client.view.admin.instrument.InstrumentManagementDialog;
import com.compomics.colims.core.service.AuditableTypedCvParamService;
import com.compomics.colims.core.service.InstrumentService;
import com.compomics.colims.model.Instrument;
import com.compomics.colims.model.InstrumentCvParam;
import com.compomics.colims.model.comparator.AuditableCvParamAccessionComparator;
import com.compomics.colims.model.cv.AuditableTypedCvParam;
import com.compomics.colims.model.enums.CvParamType;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.hibernate.exception.ConstraintViolationException;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BindingGroup;
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
@Component("instrumentManagementController")
@Lazy
public class InstrumentManagementController implements Controllable {

    //model
    private TypedCvParamSummaryListModel<InstrumentCvParam> typedCvParamSummaryListModel;
    private ObservableList<Instrument> instrumentBindingList;
    private BindingGroup bindingGroup;
    private Instrument instrumentToEdit;
    //view
    private InstrumentManagementDialog instrumentManagementDialog;
    private InstrumentEditDialog instrumentEditDialog;
    //parent controller
    @Autowired
    private MainController mainController;
    @Autowired
    @Lazy
    private CvParamManagementController cvParamManagementController;
    //services
    @Autowired
    private InstrumentService instrumentService;
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
        initInstrumentManagementDialog();
        initInstrumentEditDialog();

        bindingGroup.bind();
    }

    @Override
    public void showView() {
        //clear selection
        instrumentManagementDialog.getInstrumentList().getSelectionModel().clearSelection();

        GuiUtils.centerDialogOnComponent(mainController.getMainFrame(), instrumentManagementDialog);
        instrumentManagementDialog.setVisible(true);
    }

    /**
     * Listen to a CV param change event posted by the CvParamManagementController. If the InstrumentManagementDialog is
     * visible, clear the selection in the CV param summary list.
     *
     * @param cvParamChangeEvent the CvParamChangeEvent instance
     */
    @Subscribe
    public void onCvParamChangeEvent(CvParamChangeEvent cvParamChangeEvent) {
        if (instrumentEditDialog.isVisible()) {
            instrumentEditDialog.getCvParamSummaryList().getSelectionModel().clearSelection();
        }
    }

    private void initInstrumentManagementDialog() {
        instrumentManagementDialog = new InstrumentManagementDialog(mainController.getMainFrame(), true);

        //add binding
        instrumentBindingList = ObservableCollections.observableList(instrumentService.findAll());
        JListBinding instrumentListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, instrumentBindingList, instrumentManagementDialog.getInstrumentList());
        bindingGroup.addBinding(instrumentListBinding);

        //add action listeners
        instrumentManagementDialog.getInstrumentList().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedIndex = instrumentManagementDialog.getInstrumentList().getSelectedIndex();
                if (selectedIndex != -1 && instrumentBindingList.get(selectedIndex) != null) {
                    Instrument selectedInstrument = instrumentBindingList.get(selectedIndex);

                    //init CvParamModel
                    List<AuditableTypedCvParam> cvParams = new ArrayList<>();
                    if (selectedInstrument.getType() != null) {
                        cvParams.add(selectedInstrument.getType());
                    }
                    if (selectedInstrument.getSource() != null) {
                        cvParams.add(selectedInstrument.getSource());
                    }
                    if (selectedInstrument.getDetector() != null) {
                        cvParams.add(selectedInstrument.getDetector());
                    }
                    for (InstrumentCvParam analyzer : selectedInstrument.getAnalyzers()) {
                        cvParams.add(analyzer);
                    }
                    TypedCvParamTableModel typedCvParamTableModel = new TypedCvParamTableModel(cvParams);
                    instrumentManagementDialog.getInstrumentDetailsTable().setModel(typedCvParamTableModel);
                } else {
                    //clear detail view
                    clearInstrumentDetailFields();
                }
            }
        });

        instrumentManagementDialog.getAddInstrumentButton().addActionListener(e -> {
            updateInstrumentEditDialog(createDefaultInstrument());

            //show dialog
            GuiUtils.centerDialogOnComponent(instrumentManagementDialog, instrumentEditDialog);
            instrumentEditDialog.setVisible(true);
        });

        instrumentManagementDialog.getDeleteInstrumentButton().addActionListener(e -> {
            if (instrumentManagementDialog.getInstrumentList().getSelectedIndex() != -1) {
                Instrument instrumentToDelete = getSelectedInstrument();
                //check if the instrument already has an id.
                //If so, delete the instrument from the db.
                if (instrumentToDelete.getId() != null) {
                    try {
                        instrumentService.remove(instrumentToDelete);

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
        });

        instrumentManagementDialog.getEditInstrumentButton().addActionListener(e -> {
                    if (instrumentManagementDialog.getInstrumentList().getSelectedIndex() != -1) {
                        updateInstrumentEditDialog(getSelectedInstrument());

                        //show dialog
                        GuiUtils.centerDialogOnComponent(instrumentManagementDialog, instrumentEditDialog);
                        instrumentEditDialog.setVisible(true);
                    } else {
                        eventBus.post(new MessageEvent("Instrument selection", "Please select an instrument to edit.", JOptionPane.INFORMATION_MESSAGE));
                    }
                }
        );

        instrumentManagementDialog.getCancelInstrumentManagementButton().addActionListener(e -> instrumentManagementDialog.dispose()
        );

    }

    private void initInstrumentEditDialog() {
        instrumentEditDialog = new InstrumentEditDialog(instrumentManagementDialog, true);

        //init dual list
        instrumentEditDialog.getCvParamDualList().init(new AuditableCvParamAccessionComparator());

        //set model and renderer
        typedCvParamSummaryListModel = new TypedCvParamSummaryListModel();
        instrumentEditDialog.getCvParamSummaryList().setModel(typedCvParamSummaryListModel);
        instrumentEditDialog.getCvParamSummaryList().setCellRenderer(new TypedCvParamSummaryCellRenderer<InstrumentCvParam>());

        //add action listeners
        instrumentEditDialog.getCvParamSummaryList().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                if (instrumentEditDialog.getCvParamSummaryList().getSelectedIndex() != -1) {
                    //get selected cvParamType from summary list
                    CvParamType selectedcvParamType = (CvParamType) instrumentEditDialog.getCvParamSummaryList().getSelectedValue();

                    //load duallist for the selected cvParamType
                    List<InstrumentCvParam> availableCvParams = cvParamService.findByCvParamByType(InstrumentCvParam.class, selectedcvParamType);

                    List<InstrumentCvParam> addedCvParams;

                    if (typedCvParamSummaryListModel.isSingleCvParam(selectedcvParamType)) {
                        addedCvParams = new ArrayList<>();
                        InstrumentCvParam instrumentCvParam = typedCvParamSummaryListModel.getSingleCvParams().get(selectedcvParamType);
                        //check for null value
                        if (instrumentCvParam != null) {
                            addedCvParams.add(instrumentCvParam);
                        }
                        instrumentEditDialog.getCvParamDualList().populateLists(availableCvParams, addedCvParams, 1);
                    } else {
                        addedCvParams = typedCvParamSummaryListModel.getMultiCvParams().get(selectedcvParamType);
                        instrumentEditDialog.getCvParamDualList().populateLists(availableCvParams, addedCvParams);
                    }
                } else {
                    instrumentEditDialog.getCvParamDualList().clear();
                }
            }
        });

        instrumentEditDialog.getCvParamDualList().addPropertyChangeListener(DualList.CHANGED, evt -> {
            //get selected cvParamType
            CvParamType selectedcvParamType = (CvParamType) instrumentEditDialog.getCvParamSummaryList().getSelectedValue();

            List<InstrumentCvParam> addedItems = (List<InstrumentCvParam>) evt.getNewValue();

            //check for property
            if (selectedcvParamType.equals(CvParamType.TYPE)) {
                if (!addedItems.isEmpty()) {
                    InstrumentCvParam type = addedItems.get(0);
                    instrumentToEdit.setType(type);
                    typedCvParamSummaryListModel.updateSingleCvParam(CvParamType.TYPE, type);
                } else {
                    instrumentToEdit.setType(null);
                    typedCvParamSummaryListModel.updateSingleCvParam(CvParamType.TYPE, null);
                }
            } else if (selectedcvParamType.equals(CvParamType.SOURCE)) {
                if (!addedItems.isEmpty()) {
                    InstrumentCvParam source = addedItems.get(0);
                    instrumentToEdit.setSource(source);
                    typedCvParamSummaryListModel.updateSingleCvParam(CvParamType.SOURCE, source);
                } else {
                    instrumentToEdit.setSource(null);
                    typedCvParamSummaryListModel.updateSingleCvParam(CvParamType.SOURCE, null);
                }
            } else if (selectedcvParamType.equals(CvParamType.DETECTOR)) {
                if (!addedItems.isEmpty()) {
                    InstrumentCvParam detector = addedItems.get(0);
                    instrumentToEdit.setDetector(detector);
                    typedCvParamSummaryListModel.updateSingleCvParam(CvParamType.DETECTOR, detector);
                } else {
                    instrumentToEdit.setDetector(null);
                    typedCvParamSummaryListModel.updateSingleCvParam(CvParamType.DETECTOR, null);
                }
            } else if (selectedcvParamType.equals(CvParamType.ANALYZER)) {
                instrumentToEdit.setAnalyzers(addedItems);
                typedCvParamSummaryListModel.updateMultiCvParam(CvParamType.ANALYZER, addedItems);
            }

        });

        instrumentEditDialog.getInstrumentSaveOrUpdateButton().addActionListener(e -> {
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
                    instrumentToEdit = instrumentService.merge(instrumentToEdit);
                    index = instrumentManagementDialog.getInstrumentList().getSelectedIndex();
                    type = EntityChangeEvent.Type.UPDATED;
                } else {
                    instrumentService.persist(instrumentToEdit);
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
        });

        instrumentEditDialog.getCancelInstrumentEditButton().addActionListener(e -> {
            if (instrumentToEdit.getId() != null) {
                //roll back the changes
                Instrument rolledBackInstrument = instrumentService.findById(instrumentToEdit.getId());
                int selectedIndex = instrumentManagementDialog.getInstrumentList().getSelectedIndex();
                instrumentBindingList.remove(selectedIndex);
                instrumentBindingList.add(selectedIndex, rolledBackInstrument);
            }

            instrumentEditDialog.dispose();
        });

        instrumentEditDialog.getInstrumentCvParamsCrudButton().addActionListener(e -> {
            //check if a CV param group is selected in the CV param summary list
            if (instrumentEditDialog.getCvParamSummaryList().getSelectedIndex() != -1) {
                //get selected cvParamType from summary list
                CvParamType selectedcvParamType = (CvParamType) instrumentEditDialog.getCvParamSummaryList().getSelectedValue();

                List<AuditableTypedCvParam> cvParams = cvParamService.findByCvParamByType(selectedcvParamType);

                //update the CV param list
                cvParamManagementController.updateDialog(selectedcvParamType, cvParams);

                cvParamManagementController.showView();
            } else {
                eventBus.post(new MessageEvent("Instrument CV param type selection", "Please select an instrument CV param type to edit.", JOptionPane.INFORMATION_MESSAGE));
            }
        });
    }

    /**
     * Check if a instrument with the given instrument name exists in the database.
     *
     * @param instrument the instrument
     * @return does the instrument name exist
     */
    private boolean isExistingInstrumentName(final Instrument instrument) {
        Long count = instrumentService.countByName(instrument.getName());

        return count != 0;
    }

    /**
     * Get the selected instrument in the instrument JList.
     *
     * @return the selected instrument
     */
    private Instrument getSelectedInstrument() {
        int selectedIndex = instrumentManagementDialog.getInstrumentList().getSelectedIndex();
        return (selectedIndex != -1) ? instrumentBindingList.get(selectedIndex) : null;
    }

    /**
     * Create a default instrument, with some default properties.
     *
     * @return the default instrument
     */
    private Instrument createDefaultInstrument() {
        Instrument defaultInstrument = new Instrument("default instrument name");
        //find instrument types
        List<InstrumentCvParam> types = cvParamService.findByCvParamByType(InstrumentCvParam.class, CvParamType.TYPE);
        if (!types.isEmpty()) {
            defaultInstrument.setType(types.get(0));

        }
        //find sources
        List<InstrumentCvParam> sources = cvParamService.findByCvParamByType(InstrumentCvParam.class, CvParamType.SOURCE);
        if (!sources.isEmpty()) {
            defaultInstrument.setSource(sources.get(0));
        }
        //find detectors
        List<InstrumentCvParam> detectors = cvParamService.findByCvParamByType(InstrumentCvParam.class, CvParamType.DETECTOR);

        if (!detectors.isEmpty()) {
            defaultInstrument.setDetector(detectors.get(0));
        }
        //find analyzers
        List<InstrumentCvParam> analyzers = cvParamService.findByCvParamByType(InstrumentCvParam.class, CvParamType.ANALYZER);

        if (!analyzers.isEmpty()) {
            List<InstrumentCvParam> defaultAnalyzers = new ArrayList<>();
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

        //add the single CV params
        EnumMap<CvParamType, InstrumentCvParam> singleCvParams = new EnumMap<>(CvParamType.class
        );
        singleCvParams.put(CvParamType.TYPE, instrumentToEdit.getType());
        singleCvParams.put(CvParamType.SOURCE, instrumentToEdit.getSource());
        singleCvParams.put(CvParamType.DETECTOR, instrumentToEdit.getDetector());

        //add the multiple CV params
        EnumMap<CvParamType, List<InstrumentCvParam>> multipleCvParams = new EnumMap<>(CvParamType.class);

        multipleCvParams.put(CvParamType.ANALYZER, instrumentToEdit.getAnalyzers());
        typedCvParamSummaryListModel.update(singleCvParams, multipleCvParams);

        //clear selection in CV param summary list
        instrumentEditDialog.getCvParamSummaryList().getSelectionModel().clearSelection();
    }

    /**
     * Clear the instrument detail fields.
     */
    private void clearInstrumentDetailFields() {
        instrumentManagementDialog.getInstrumentDetailsTable().setModel(new TypedCvParamTableModel());
    }
}
