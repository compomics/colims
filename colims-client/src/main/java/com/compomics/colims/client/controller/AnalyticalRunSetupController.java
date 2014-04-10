package com.compomics.colims.client.controller;

import com.compomics.colims.client.event.InstrumentChangeEvent;
import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.event.message.StorageQueuesConnectionErrorMessageEvent;
import com.compomics.colims.client.storage.QueueManager;
import com.compomics.colims.client.storage.StorageTaskProducer;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.AnalyticalRunSetupDialog;
import com.compomics.colims.core.io.DataImport;
import com.compomics.colims.core.service.InstrumentService;
import com.compomics.colims.distributed.model.StorageMetadata;
import com.compomics.colims.distributed.model.StorageTask;
import com.compomics.colims.distributed.model.enums.StorageType;
import com.compomics.colims.model.Instrument;
import com.compomics.colims.model.enums.DefaultPermission;
import com.compomics.colims.repository.AuthenticationBean;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JComboBoxBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.JmsException;
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("analyticalRunSetupController")
public class AnalyticalRunSetupController implements Controllable {

    private static final Logger LOGGER = Logger.getLogger(AnalyticalRunSetupController.class);
    private static final String METADATA_SELECTION_CARD = "metadataSelectionPanel";
    private static final String PS_DATA_IMPORT_CARD = "peptideShakerDataImportPanel";
    private static final String MAX_QUANT_DATA_IMPORT_CARD = "maxQuantDataImportPanel";
    private static final String CONFIRMATION_CARD = "confirmationPanel";

    //model       
    private BindingGroup bindingGroup;
    private ObservableList<Instrument> instrumentBindingList;
    private StorageType storageType;
    private Instrument instrument;
    //view
    private AnalyticalRunSetupDialog analyticalRunSetupDialog;
    //parent controller
    @Autowired
    private ColimsController colimsController;
    private ProjectManagementController projectManagementController;
    //child controller
    @Autowired
    private PeptideShakerDataImportController peptideShakerDataImportController;
    @Autowired
    private MaxQuantDataImportController maxQuantDataImportController;
    //services
    @Autowired
    private InstrumentService instrumentService;
    @Autowired
    private EventBus eventBus;
    @Autowired
    private AuthenticationBean authenticationBean;
    @Autowired
    private StorageTaskProducer storageTaskProducer;
    @Autowired
    private QueueManager queueManager;

    public AnalyticalRunSetupDialog getAnalyticalRunSetupDialog() {
        return analyticalRunSetupDialog;
    }

    @Override
    public void init() {
        //init view
        analyticalRunSetupDialog = new AnalyticalRunSetupDialog(colimsController.getColimsFrame(), true);

        //register to event bus
        eventBus.register(this);

        //init child controller
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
            public void actionPerformed(ActionEvent e) {
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
                            }
                            onCardSwitch();
                        } else {
                            MessageEvent messageEvent = new MessageEvent("instrument/start date selection", "Please select an instrument and a start date.", JOptionPane.INFORMATION_MESSAGE);
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
            public void actionPerformed(ActionEvent e) {
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
            public void actionPerformed(ActionEvent e) {
                String currentCardName = GuiUtils.getVisibleChildComponent(analyticalRunSetupDialog.getTopPanel());
                switch (currentCardName) {
                    case PS_DATA_IMPORT_CARD:
                        List<String> psValidationMessages = peptideShakerDataImportController.validateBeforeUnpacking();
                        if (psValidationMessages.isEmpty()) {
                            sendStorageTask(peptideShakerDataImportController.getDataImport());
                            getCardLayout().show(analyticalRunSetupDialog.getTopPanel(), CONFIRMATION_CARD);
                        } else {
                            MessageEvent messageEvent = new MessageEvent("validation failure", psValidationMessages, JOptionPane.WARNING_MESSAGE);
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
                            MessageEvent messageEvent = new MessageEvent("validation failure", maxQuantValidationMessages, JOptionPane.WARNING_MESSAGE);
                            eventBus.post(messageEvent);
                        }
                        onCardSwitch();
                        break;
                    default:
                        break;
                }
            }
        });

        analyticalRunSetupDialog.getCloseButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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

                GuiUtils.centerDialogOnComponent(colimsController.getColimsFrame(), analyticalRunSetupDialog);
                analyticalRunSetupDialog.setVisible(true);
            } else {
                eventBus.post(new StorageQueuesConnectionErrorMessageEvent(queueManager.getBrokerName(), queueManager.getBrokerUrl(), queueManager.getBrokerJmxUrl()));
            }
        } else {
            eventBus.post(new MessageEvent("authorization problem", "User " + authenticationBean.getCurrentUser().getName() + " has no rights to add a run.", JOptionPane.INFORMATION_MESSAGE));
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
        StorageMetadata storageMetadata = new StorageMetadata(getSelectedStorageType(), analyticalRunSetupDialog.getStorageDescriptionTextField().getText(), authenticationBean.getCurrentUser().getName(), analyticalRunSetupDialog.getDateTimePicker().getDate(), instrument, projectManagementController.getSelectedSample());
        StorageTask storageTask = new StorageTask(storageMetadata, dataImport);

        try {
            storageTaskProducer.sendStorageTask(storageTask);
        } catch (JmsException jmsException) {
            LOGGER.error(jmsException.getMessage(), jmsException);
            MessageEvent messageEvent = new MessageEvent("connection error", "The storage unit cannot be reached.", JOptionPane.ERROR_MESSAGE);
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
     * Show the correct info and disable/enable the right buttons when switching
     * between cards.
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
    private StorageType getSelectedStorageType() {
        StorageType selectedStorageType = null;

        //iterate over the radio buttons in the group
        for (Enumeration<AbstractButton> buttons = analyticalRunSetupDialog.getDataTypeButtonGroup().getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();

            if (button.isSelected()) {
                selectedStorageType = StorageType.getByUserFriendlyName(button.getText());
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
