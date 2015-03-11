package com.compomics.colims.client.controller;

import com.compomics.colims.client.controller.admin.FastaDbManagementController;
import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.distributed.QueueManager;
import com.compomics.colims.client.distributed.producer.DbTaskProducer;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.MzTabExportDialog;
import com.compomics.colims.core.io.mztab.enums.MzTabMode;
import com.compomics.colims.core.io.mztab.enums.MzTabType;
import com.compomics.colims.core.service.InstrumentService;
import com.compomics.colims.repository.AuthenticationBean;
import com.google.common.eventbus.EventBus;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import org.jdesktop.beansbinding.BindingGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The mzTab export view controller.
 *
 * @author Niels Hulstaert
 */
@Component("mzTabExportController")
public class MzTabExportController implements Controllable {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(MzTabExportController.class);

    private static final String TYPE_AND_MODE_SELECTION_CARD = "typeAndModeSelectionPanel";
    private static final String PS_DATA_IMPORT_CARD = "peptideShakerDataImportPanel";
    private static final String MAX_QUANT_DATA_IMPORT_CARD = "maxQuantDataImportPanel";
    private static final String CONFIRMATION_CARD = "confirmationPanel";

    //model
    private BindingGroup bindingGroup;
    private MzTabType mzTabType;
    private MzTabMode mzTabMode;
    //view
    private MzTabExportDialog mzTabExportDialog;
    //parent controller
    @Autowired
    private ColimsController colimsController;
    @Autowired
    private ProjectManagementController projectManagementController;
    //child controller
    @Autowired
    private PeptideShakerDataImportController peptideShakerDataImportController;
    @Autowired
    private MaxQuantDataImportController maxQuantDataImportController;
    @Autowired
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
     * @return the MzTabExportDialog
     */
    public MzTabExportDialog getMzTabExportDialog() {
        return mzTabExportDialog;
    }

    @Override
    public void init() {
        //init view
        mzTabExportDialog = new MzTabExportDialog(colimsController.getColimsFrame(), true);

        //register to event bus
        eventBus.register(this);

        //init child controller
        fastaDbManagementController.init();
        peptideShakerDataImportController.init();
        maxQuantDataImportController.init();

        //select the identification type radio button
        mzTabExportDialog.getIdentificationRadioButton().setSelected(true);
        //select the summary mode radio button
        mzTabExportDialog.getSummaryRadioButton().setSelected(true);

        //add binding
        bindingGroup = new BindingGroup();

        //bindings go here

        bindingGroup.bind();

        //add action listeners
        mzTabExportDialog.getProceedButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                String currentCardName = GuiUtils.getVisibleChildComponent(mzTabExportDialog.getTopPanel());
                switch (currentCardName) {
                    case TYPE_AND_MODE_SELECTION_CARD:
                        mzTabType = getSelectedMzTabType();
                        mzTabMode = getSelectedMzTabMode();
                        break;
                    default:
                        break;
                }
            }
        });

        mzTabExportDialog.getBackButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                String currentCardName = GuiUtils.getVisibleChildComponent(mzTabExportDialog.getTopPanel());
                switch (currentCardName) {
                    case PS_DATA_IMPORT_CARD:
                    case MAX_QUANT_DATA_IMPORT_CARD:
                        getCardLayout().show(mzTabExportDialog.getTopPanel(), TYPE_AND_MODE_SELECTION_CARD);
                        break;
                    default:
                        getCardLayout().previous(mzTabExportDialog.getTopPanel());
                        break;
                }
                onCardSwitch();
            }
        });

        mzTabExportDialog.getFinishButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                String currentCardName = GuiUtils.getVisibleChildComponent(mzTabExportDialog.getTopPanel());
                switch (currentCardName) {
                    case PS_DATA_IMPORT_CARD:
                        List<String> psValidationMessages = peptideShakerDataImportController.validate();
                        if (psValidationMessages.isEmpty()) {
                            getCardLayout().show(mzTabExportDialog.getTopPanel(), CONFIRMATION_CARD);
                        } else {
                            MessageEvent messageEvent = new MessageEvent("Validation failure", psValidationMessages, JOptionPane.WARNING_MESSAGE);
                            eventBus.post(messageEvent);
                        }
                        onCardSwitch();
                        break;
                    case MAX_QUANT_DATA_IMPORT_CARD:
                        List<String> maxQuantValidationMessages = maxQuantDataImportController.validate();
                        if (maxQuantValidationMessages.isEmpty()) {
                            getCardLayout().show(mzTabExportDialog.getTopPanel(), CONFIRMATION_CARD);
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

        mzTabExportDialog.getCancelButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                mzTabExportDialog.dispose();
            }
        });
    }

    @Override
    public void showView() {

    }

    /**
     * Get the card layout.
     *
     * @return the CardLayout
     */
    private CardLayout getCardLayout() {
        return (CardLayout) mzTabExportDialog.getTopPanel().getLayout();
    }

    /**
     * Show the correct info and disable/enable the right buttons when switching
     * between cards.
     */
    private void onCardSwitch() {
        String currentCardName = GuiUtils.getVisibleChildComponent(mzTabExportDialog.getTopPanel());
        switch (currentCardName) {
            case TYPE_AND_MODE_SELECTION_CARD:
                mzTabExportDialog.getBackButton().setEnabled(false);
                mzTabExportDialog.getProceedButton().setEnabled(true);
                mzTabExportDialog.getFinishButton().setEnabled(false);
                //show info
                updateInfo("Click on \"proceed\" to select the necessary input files/directories.");
                break;
            case PS_DATA_IMPORT_CARD:
                mzTabExportDialog.getBackButton().setEnabled(true);
                mzTabExportDialog.getProceedButton().setEnabled(false);
                mzTabExportDialog.getFinishButton().setEnabled(true);
                //show info
                updateInfo("Click on \"finish\" to validate the input and store the run(s).");
                break;
            case MAX_QUANT_DATA_IMPORT_CARD:
                mzTabExportDialog.getBackButton().setEnabled(true);
                mzTabExportDialog.getProceedButton().setEnabled(false);
                mzTabExportDialog.getFinishButton().setEnabled(true);
                //show info
                updateInfo("Click on \"finish\" to validate the input and store the run(s).");
                break;
            case CONFIRMATION_CARD:
                mzTabExportDialog.getBackButton().setEnabled(false);
                mzTabExportDialog.getProceedButton().setEnabled(false);
                mzTabExportDialog.getFinishButton().setEnabled(false);
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
        mzTabExportDialog.getInfoLabel().setText(message);
    }

    /**
     * Get the selected mzTab type.
     *
     * @return the selected MzTabType
     */
    private MzTabType getSelectedMzTabType() {
        MzTabType selectedMzTabType = null;

        //iterate over the radio buttons in the group
        for (Enumeration<AbstractButton> buttons = mzTabExportDialog.getTypeButtonGroup().getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();

            if (button.isSelected()) {
                selectedMzTabType = MzTabType.getByMzTabName(button.getText());
            }
        }

        return selectedMzTabType;
    }

    /**
     * Get the selected mzTab mode.
     *
     * @return the selected MzTabMode
     */
    private MzTabMode getSelectedMzTabMode() {
        MzTabMode selectedMzTabMode = null;

        //iterate over the radio buttons in the group
        for (Enumeration<AbstractButton> buttons = mzTabExportDialog.getModeButtonGroup().getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();

            if (button.isSelected()) {
                selectedMzTabMode = MzTabMode.getByMzTabName(button.getText());
            }
        }

        return selectedMzTabMode;
    }

}
