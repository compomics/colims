package com.compomics.colims.client.controller;

import com.compomics.colims.client.controller.admin.FastaDbManagementController;
import com.compomics.colims.client.distributed.QueueManager;
import com.compomics.colims.client.distributed.producer.DbTaskProducer;
import com.compomics.colims.client.event.admin.InstrumentChangeEvent;
import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.event.message.StorageQueuesConnectionErrorMessageEvent;
import com.compomics.colims.client.event.message.UnexpectedErrorMessageEvent;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.AnalyticalRunsAdditionDialog;
import com.compomics.colims.core.distributed.model.PersistDbTask;
import com.compomics.colims.core.distributed.model.PersistMetadata;
import com.compomics.colims.core.distributed.model.enums.PersistType;
import com.compomics.colims.core.io.DataImport;
import com.compomics.colims.core.service.InstrumentService;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.Instrument;
import com.compomics.colims.model.Sample;
import com.compomics.colims.model.User;
import com.compomics.colims.model.enums.DefaultPermission;
import com.compomics.colims.model.UserBean;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.apache.log4j.Logger;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JComboBoxBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

/**
 * @author Niels Hulstaert
 */
@Component("analyticalRunsAdditionController")
@Lazy
public class AnalyticalRunsAdditionController implements Controllable {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(AnalyticalRunsAdditionController.class);

    private static final String METADATA_SELECTION_CARD = "metadataSelectionPanel";
    private static final String PS_DATA_IMPORT_CARD = "peptideShakerDataImportPanel";
    private static final String MAX_QUANT_DATA_IMPORT_CARD = "maxQuantDataImportPanel";
    private static final String CONFIRMATION_CARD = "confirmationPanel";

    //model
    private BindingGroup bindingGroup;
    private ObservableList<Instrument> instrumentBindingList;
    private PersistType storageType;
    private Instrument instrument;
    //view
    private AnalyticalRunsAdditionDialog analyticalRunsAdditionDialog;
    //parent controller
    @Autowired
    private MainController mainController;
    @Autowired
    private ProjectManagementController projectManagementController;
    //child controller
    @Autowired
    @Lazy
    private PeptideShakerDataImportController peptideShakerDataImportController;
    @Autowired
    @Lazy
    private MaxQuantDataImportController maxQuantDataImportController;
    @Autowired
    @Lazy
    private FastaDbManagementController fastaDbManagementController;
    //services
    @Autowired
    private InstrumentService instrumentService;
    @Autowired
    private EventBus eventBus;
    @Autowired
    private UserBean userBean;
    @Autowired
    private DbTaskProducer storageTaskProducer;
    @Autowired
    private QueueManager queueManager;

    /**
     * Get the view of this controller.
     *
     * @return the AnalyticalRunsAdditionDialog
     */
    public AnalyticalRunsAdditionDialog getAnalyticalRunsAdditionDialog() {
        return analyticalRunsAdditionDialog;
    }

    @Override
    @PostConstruct
    public void init() {
        //init view
        analyticalRunsAdditionDialog = new AnalyticalRunsAdditionDialog(mainController.getMainFrame(), true);

        //register to event bus
        eventBus.register(this);

        //init child controller
        fastaDbManagementController.init();
        peptideShakerDataImportController.init();
        maxQuantDataImportController.init();

        //select peptideShaker radio button
        analyticalRunsAdditionDialog.getPeptideShakerRadioButton().setSelected(true);

        //set DateTimePicker format
        analyticalRunsAdditionDialog.getDateTimePicker().setFormats(new SimpleDateFormat("dd-MM-yyyy HH:mm"));
        analyticalRunsAdditionDialog.getDateTimePicker().setTimeFormat(DateFormat.getTimeInstance(DateFormat.MEDIUM));

        instrumentBindingList = ObservableCollections.observableList(instrumentService.findAll());

        //add binding
        bindingGroup = new BindingGroup();

        JComboBoxBinding instrumentComboBoxBinding = SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ_WRITE, instrumentBindingList, analyticalRunsAdditionDialog.getInstrumentComboBox());
        bindingGroup.addBinding(instrumentComboBoxBinding);

        bindingGroup.bind();

        //add action listeners
        analyticalRunsAdditionDialog.getProceedButton().addActionListener(e -> {
            String currentCardName = GuiUtils.getVisibleChildComponent(analyticalRunsAdditionDialog.getTopPanel());
            switch (currentCardName) {
                case METADATA_SELECTION_CARD:
                    instrument = getSelectedInstrument();
                    Date startDate = analyticalRunsAdditionDialog.getDateTimePicker().getDate();
                    if (instrument != null && startDate != null) {
                        storageType = getSelectedStorageType();
                        switch (storageType) {
                            case PEPTIDESHAKER:
                                getCardLayout().show(analyticalRunsAdditionDialog.getTopPanel(), PS_DATA_IMPORT_CARD);
                                break;
                            case MAX_QUANT:
                                getCardLayout().show(analyticalRunsAdditionDialog.getTopPanel(), MAX_QUANT_DATA_IMPORT_CARD);
                                break;
                            default:
                                break;
                        }
                        onCardSwitch();
                    } else {
                        MessageEvent messageEvent = new MessageEvent("Instrument/start date selection", "Please select an instrument and a start date.", JOptionPane.INFORMATION_MESSAGE);
                        eventBus.post(messageEvent);
                    }
                    break;
                default:
                    break;
            }
        });

        analyticalRunsAdditionDialog.getBackButton().addActionListener(e -> {
            String currentCardName = GuiUtils.getVisibleChildComponent(analyticalRunsAdditionDialog.getTopPanel());
            switch (currentCardName) {
                case PS_DATA_IMPORT_CARD:
                case MAX_QUANT_DATA_IMPORT_CARD:
                    getCardLayout().show(analyticalRunsAdditionDialog.getTopPanel(), METADATA_SELECTION_CARD);
                    break;
                default:
                    getCardLayout().previous(analyticalRunsAdditionDialog.getTopPanel());
                    break;
            }
            onCardSwitch();
        });

        analyticalRunsAdditionDialog.getFinishButton().addActionListener(e -> {
            String currentCardName = GuiUtils.getVisibleChildComponent(analyticalRunsAdditionDialog.getTopPanel());
            switch (currentCardName) {
                case PS_DATA_IMPORT_CARD:
                    List<String> psValidationMessages = peptideShakerDataImportController.validate();
                    if (psValidationMessages.isEmpty()) {
                        sendStorageTask(peptideShakerDataImportController.getDataImport());
                        getCardLayout().show(analyticalRunsAdditionDialog.getTopPanel(), CONFIRMATION_CARD);
                    } else {
                        MessageEvent messageEvent = new MessageEvent("Validation failure", psValidationMessages, JOptionPane.WARNING_MESSAGE);
                        eventBus.post(messageEvent);
                    }
                    onCardSwitch();
                    break;
                case MAX_QUANT_DATA_IMPORT_CARD:
                    List<String> maxQuantValidationMessages = maxQuantDataImportController.validate();
                    if (maxQuantValidationMessages.isEmpty()) {
                        sendStorageTask(maxQuantDataImportController.getDataImport());
                        getCardLayout().show(analyticalRunsAdditionDialog.getTopPanel(), CONFIRMATION_CARD);
                    } else {
                        MessageEvent messageEvent = new MessageEvent("Validation failure", maxQuantValidationMessages, JOptionPane.WARNING_MESSAGE);
                        eventBus.post(messageEvent);
                    }
                    onCardSwitch();
                    break;
                default:
                    break;
            }
        });

        analyticalRunsAdditionDialog.getCancelButton().addActionListener(e -> analyticalRunsAdditionDialog.dispose());
    }

    @Override
    public void showView() {
        //check if the user has the rights to add a run
        if (userBean.getDefaultPermissions().get(DefaultPermission.CREATE)) {
            //check connection to distributed queues
            if (queueManager.isReachable()) {
                //reset instrument selection
                if (!instrumentBindingList.isEmpty()) {
                    analyticalRunsAdditionDialog.getInstrumentComboBox().setSelectedIndex(0);
                }

                //reset input fields
                analyticalRunsAdditionDialog.getStorageDescriptionTextField().setText("");
                analyticalRunsAdditionDialog.getDateTimePicker().setDate(new Date());
                peptideShakerDataImportController.showView();
                maxQuantDataImportController.showView();

                //show first card
                getCardLayout().first(analyticalRunsAdditionDialog.getTopPanel());
                onCardSwitch();

                GuiUtils.centerDialogOnComponent(mainController.getMainFrame(), analyticalRunsAdditionDialog);
                analyticalRunsAdditionDialog.setVisible(true);
            } else {
                eventBus.post(new StorageQueuesConnectionErrorMessageEvent(queueManager.getBrokerName(), queueManager.getBrokerUrl(), queueManager.getBrokerJmxUrl()));
            }
        } else {
            eventBus.post(new MessageEvent("Authorization problem", "User " + userBean.getCurrentUser().getName() + " has no rights to add a run.", JOptionPane.INFORMATION_MESSAGE));
        }
    }

    /**
     * Listen to an InstrumentChangeEvent.
     *
     * @param instrumentChangeEvent the instrument change event
     */
    @Subscribe
    public void onInstrumentChangeEvent(InstrumentChangeEvent instrumentChangeEvent) {
        instrumentBindingList.clear();
        instrumentBindingList.addAll(instrumentService.findAll());
    }

    /**
     * Send a storage task to the queue. A message dialog is shown in case the
     * queue cannot be reached or in case of an IOException thrown by the
     * sendDbTask method.
     *
     * @param dataImport the DataImport instance with the necessary import
     * information
     */
    private void sendStorageTask(DataImport dataImport) {
        String storageDescription = analyticalRunsAdditionDialog.getStorageDescriptionTextField().getText();
        User currentUser = userBean.getCurrentUser();
        Date startDate = analyticalRunsAdditionDialog.getDateTimePicker().getDate();
        Sample sample = projectManagementController.getSelectedSample();

        PersistMetadata persistMetadata = new PersistMetadata(storageType, storageDescription, startDate, instrument.getId());
        PersistDbTask persistDbTask = new PersistDbTask(AnalyticalRun.class, sample.getId(), currentUser.getId(), persistMetadata, dataImport);

        //check connection
        if (queueManager.isReachable()) {
            try {
                storageTaskProducer.sendDbTask(persistDbTask);
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e.getCause());
                eventBus.post(new UnexpectedErrorMessageEvent(e.getMessage()));
            }
        } else {
            eventBus.post(new StorageQueuesConnectionErrorMessageEvent(queueManager.getBrokerName(), queueManager.getBrokerUrl(), queueManager.getBrokerJmxUrl()));
        }
    }

    /**
     * Get the card layout.
     *
     * @return the CardLayout
     */
    private CardLayout getCardLayout() {
        return (CardLayout) analyticalRunsAdditionDialog.getTopPanel().getLayout();
    }

    /**
     * Show the correct info and disable/enable the right buttons when switching
     * between cards.
     */
    private void onCardSwitch() {
        String currentCardName = GuiUtils.getVisibleChildComponent(analyticalRunsAdditionDialog.getTopPanel());
        switch (currentCardName) {
            case METADATA_SELECTION_CARD:
                analyticalRunsAdditionDialog.getBackButton().setEnabled(false);
                analyticalRunsAdditionDialog.getProceedButton().setEnabled(true);
                analyticalRunsAdditionDialog.getFinishButton().setEnabled(false);
                //show info
                updateInfo("Click on \"proceed\" to select the necessary input files/directories.");
                break;
            case PS_DATA_IMPORT_CARD:
                analyticalRunsAdditionDialog.getBackButton().setEnabled(true);
                analyticalRunsAdditionDialog.getProceedButton().setEnabled(false);
                analyticalRunsAdditionDialog.getFinishButton().setEnabled(true);
                //show info
                updateInfo("Click on \"finish\" to validate the input and store the run(s).");
                break;
            case MAX_QUANT_DATA_IMPORT_CARD:
                analyticalRunsAdditionDialog.getBackButton().setEnabled(true);
                analyticalRunsAdditionDialog.getProceedButton().setEnabled(false);
                analyticalRunsAdditionDialog.getFinishButton().setEnabled(true);
                //show info
                updateInfo("Click on \"finish\" to validate the input and store the run(s).");
                break;
            case CONFIRMATION_CARD:
                analyticalRunsAdditionDialog.getBackButton().setEnabled(false);
                analyticalRunsAdditionDialog.getProceedButton().setEnabled(false);
                analyticalRunsAdditionDialog.getFinishButton().setEnabled(false);
                updateInfo("");
                break;
            default:
                break;
        }
    }

    /**
     * Update the info label.
     *
     * @param message the info message
     */
    private void updateInfo(String message) {
        analyticalRunsAdditionDialog.getInfoLabel().setText(message);
    }

    /**
     * Get the selected storage type.
     *
     * @return the selected StorageType
     */
    private PersistType getSelectedStorageType() {
        PersistType selectedStorageType = null;

        //iterate over the radio buttons in the group
        for (Enumeration<AbstractButton> buttons = analyticalRunsAdditionDialog.getDataTypeButtonGroup().getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();

            if (button.isSelected()) {
                selectedStorageType = PersistType.getByUserFriendlyName(button.getText());
            }
        }

        return selectedStorageType;
    }

    /**
     * Get the selected instrument. Returns null if no instrument was selected.
     *
     * @return the selected Instrument
     */
    private Instrument getSelectedInstrument() {
        Instrument selectedInstrument = null;

        if (analyticalRunsAdditionDialog.getInstrumentComboBox().getSelectedIndex() != -1) {
            selectedInstrument = instrumentBindingList.get(analyticalRunsAdditionDialog.getInstrumentComboBox().getSelectedIndex());
        }

        return selectedInstrument;
    }

}