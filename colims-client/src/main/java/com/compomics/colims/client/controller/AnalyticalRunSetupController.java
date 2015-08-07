package com.compomics.colims.client.controller;

import com.compomics.colims.client.controller.admin.FastaDbManagementController;
import com.compomics.colims.client.distributed.QueueManager;
import com.compomics.colims.client.distributed.producer.DbTaskProducer;
import com.compomics.colims.client.event.admin.InstrumentChangeEvent;
import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.event.message.StorageQueuesConnectionErrorMessageEvent;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.AnalyticalRunSetupDialog;
import com.compomics.colims.core.io.DataImport;
import com.compomics.colims.core.service.InstrumentService;
import com.compomics.colims.distributed.model.PersistDbTask;
import com.compomics.colims.distributed.model.PersistMetadata;
import com.compomics.colims.distributed.model.enums.PersistType;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.Instrument;
import com.compomics.colims.model.Sample;
import com.compomics.colims.model.User;
import com.compomics.colims.model.enums.DefaultPermission;
import com.compomics.colims.repository.AuthenticationBean;
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
import org.springframework.jms.JmsException;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

/**
 * @author Niels Hulstaert
 */
@Component("analyticalRunSetupController")
@Lazy
public class AnalyticalRunSetupController implements Controllable {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(AnalyticalRunSetupController.class);

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
    private AnalyticalRunSetupDialog analyticalRunSetupDialog;
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
    private AuthenticationBean authenticationBean;
    @Autowired
    private DbTaskProducer storageTaskProducer;
    @Autowired
    private QueueManager queueManager;

    /**
     * Get the view of this controller.
     *
     * @return the AnalyticalRunSetupDialog
     */
    public AnalyticalRunSetupDialog getAnalyticalRunSetupDialog() {
        return analyticalRunSetupDialog;
    }

    @Override
    @PostConstruct
    public void init() {
        //init view
        analyticalRunSetupDialog = new AnalyticalRunSetupDialog(mainController.getMainFrame(), true);

        //register to event bus
        eventBus.register(this);

        //init child controller
        fastaDbManagementController.init();
        peptideShakerDataImportController.init();
        maxQuantDataImportController.init();

        //select peptideShaker radio button
        analyticalRunSetupDialog.getPeptideShakerRadioButton().setSelected(true);

        //set DateTimePicker format
        analyticalRunSetupDialog.getDateTimePicker().setFormats(new SimpleDateFormat("dd-MM-yyyy HH:mm"));
        analyticalRunSetupDialog.getDateTimePicker().setTimeFormat(DateFormat.getTimeInstance(DateFormat.MEDIUM));

        instrumentBindingList = ObservableCollections.observableList(instrumentService.findAll());

        //add binding
        bindingGroup = new BindingGroup();

        JComboBoxBinding instrumentComboBoxBinding = SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ_WRITE, instrumentBindingList, analyticalRunSetupDialog.getInstrumentComboBox());
        bindingGroup.addBinding(instrumentComboBoxBinding);

        bindingGroup.bind();

        //add action listeners
        analyticalRunSetupDialog.getProceedButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                String currentCardName = GuiUtils.getVisibleChildComponent(analyticalRunSetupDialog.getTopPanel());
                switch (currentCardName) {
                    case METADATA_SELECTION_CARD:
                        instrument = getSelectedInstrument();
                        Date startDate = analyticalRunSetupDialog.getDateTimePicker().getDate();
                        if (instrument != null && startDate != null) {
                            storageType = getSelectedStorageType();
                            switch (storageType) {
                                case PEPTIDESHAKER:
                                    getCardLayout().show(analyticalRunSetupDialog.getTopPanel(), PS_DATA_IMPORT_CARD);
                                    break;
                                case MAX_QUANT:
                                    getCardLayout().show(analyticalRunSetupDialog.getTopPanel(), MAX_QUANT_DATA_IMPORT_CARD);
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
            }
        });

        analyticalRunSetupDialog.getBackButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                String currentCardName = GuiUtils.getVisibleChildComponent(analyticalRunSetupDialog.getTopPanel());
                switch (currentCardName) {
                    case PS_DATA_IMPORT_CARD:
                    case MAX_QUANT_DATA_IMPORT_CARD:
                        getCardLayout().show(analyticalRunSetupDialog.getTopPanel(), METADATA_SELECTION_CARD);
                        break;
                    default:
                        getCardLayout().previous(analyticalRunSetupDialog.getTopPanel());
                        break;
                }
                onCardSwitch();
            }
        });

        analyticalRunSetupDialog.getFinishButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                String currentCardName = GuiUtils.getVisibleChildComponent(analyticalRunSetupDialog.getTopPanel());
                switch (currentCardName) {
                    case PS_DATA_IMPORT_CARD:
                        List<String> psValidationMessages = peptideShakerDataImportController.validate();
                        if (psValidationMessages.isEmpty()) {
                            sendStorageTask(peptideShakerDataImportController.getDataImport());
                            getCardLayout().show(analyticalRunSetupDialog.getTopPanel(), CONFIRMATION_CARD);
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
                            getCardLayout().show(analyticalRunSetupDialog.getTopPanel(), CONFIRMATION_CARD);
                        } else {
                            MessageEvent messageEvent = new MessageEvent("Validation failure", maxQuantValidationMessages, JOptionPane.WARNING_MESSAGE);
                            eventBus.post(messageEvent);
                        }
                        onCardSwitch();
                        break;
                    default:
                        break;
                }
            }
        });

        analyticalRunSetupDialog.getCancelButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                analyticalRunSetupDialog.dispose();
            }
        });
    }

    @Override
    public void showView() {
        //check if the user has the rights to add a run
        if (authenticationBean.getDefaultPermissions().get(DefaultPermission.CREATE)) {
            //check connection to distributed queues
            if (queueManager.testConnection()) {
                //reset instrument selection
                if (!instrumentBindingList.isEmpty()) {
                    analyticalRunSetupDialog.getInstrumentComboBox().setSelectedIndex(0);
                }

                //reset input fields
                analyticalRunSetupDialog.getStorageDescriptionTextField().setText("");
                analyticalRunSetupDialog.getDateTimePicker().setDate(new Date());
                peptideShakerDataImportController.showView();
                maxQuantDataImportController.showView();

                //show first card
                getCardLayout().first(analyticalRunSetupDialog.getTopPanel());
                onCardSwitch();

                GuiUtils.centerDialogOnComponent(mainController.getMainFrame(), analyticalRunSetupDialog);
                analyticalRunSetupDialog.setVisible(true);
            } else {
                eventBus.post(new StorageQueuesConnectionErrorMessageEvent(queueManager.getBrokerName(), queueManager.getBrokerUrl(), queueManager.getBrokerJmxUrl()));
            }
        } else {
            eventBus.post(new MessageEvent("Authorization problem", "User " + authenticationBean.getCurrentUser().getName() + " has no rights to add a run.", JOptionPane.INFORMATION_MESSAGE));
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

    private void sendStorageTask(DataImport dataImport) {
        String storageDescription = analyticalRunSetupDialog.getStorageDescriptionTextField().getText();
        User currentUser = authenticationBean.getCurrentUser();
        Date startDate = analyticalRunSetupDialog.getDateTimePicker().getDate();
        Sample sample = projectManagementController.getSelectedSample();

        PersistMetadata persistMetadata = new PersistMetadata(storageType, storageDescription, startDate, instrument);
        PersistDbTask persistDbTask = new PersistDbTask(AnalyticalRun.class, sample.getId(), currentUser.getId(), persistMetadata, dataImport);

        try {
            storageTaskProducer.sendDbTask(persistDbTask);
        } catch (JmsException jmsException) {
            LOGGER.error(jmsException.getMessage(), jmsException);
            eventBus.post(new MessageEvent("Connection error", "The storage unit cannot be reached.", JOptionPane.ERROR_MESSAGE));
        }
    }

    /**
     * Get the card layout.
     *
     * @return the CardLayout
     */
    private CardLayout getCardLayout() {
        return (CardLayout) analyticalRunSetupDialog.getTopPanel().getLayout();
    }

    /**
     * Show the correct info and disable/enable the right buttons when switching between cards.
     */
    private void onCardSwitch() {
        String currentCardName = GuiUtils.getVisibleChildComponent(analyticalRunSetupDialog.getTopPanel());
        switch (currentCardName) {
            case METADATA_SELECTION_CARD:
                analyticalRunSetupDialog.getBackButton().setEnabled(false);
                analyticalRunSetupDialog.getProceedButton().setEnabled(true);
                analyticalRunSetupDialog.getFinishButton().setEnabled(false);
                //show info
                updateInfo("Click on \"proceed\" to select the necessary input files/directories.");
                break;
            case PS_DATA_IMPORT_CARD:
                analyticalRunSetupDialog.getBackButton().setEnabled(true);
                analyticalRunSetupDialog.getProceedButton().setEnabled(false);
                analyticalRunSetupDialog.getFinishButton().setEnabled(true);
                //show info
                updateInfo("Click on \"finish\" to validate the input and store the run(s).");
                break;
            case MAX_QUANT_DATA_IMPORT_CARD:
                analyticalRunSetupDialog.getBackButton().setEnabled(true);
                analyticalRunSetupDialog.getProceedButton().setEnabled(false);
                analyticalRunSetupDialog.getFinishButton().setEnabled(true);
                //show info
                updateInfo("Click on \"finish\" to validate the input and store the run(s).");
                break;
            case CONFIRMATION_CARD:
                analyticalRunSetupDialog.getBackButton().setEnabled(false);
                analyticalRunSetupDialog.getProceedButton().setEnabled(false);
                analyticalRunSetupDialog.getFinishButton().setEnabled(false);
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
        analyticalRunSetupDialog.getInfoLabel().setText(message);
    }

    /**
     * Get the selected storage type.
     *
     * @return the selected StorageType
     */
    private PersistType getSelectedStorageType() {
        PersistType selectedStorageType = null;

        //iterate over the radio buttons in the group
        for (Enumeration<AbstractButton> buttons = analyticalRunSetupDialog.getDataTypeButtonGroup().getElements(); buttons.hasMoreElements(); ) {
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

        if (analyticalRunSetupDialog.getInstrumentComboBox().getSelectedIndex() != -1) {
            selectedInstrument = instrumentBindingList.get(analyticalRunSetupDialog.getInstrumentComboBox().getSelectedIndex());
        }

        return selectedInstrument;
    }

}
