package com.compomics.colims.client.controller;

import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.MzTabExportDialog;
import com.compomics.colims.core.io.mztab.MzTabExport;
import com.compomics.colims.core.io.mztab.enums.MzTabMode;
import com.compomics.colims.core.io.mztab.enums.MzTabType;
import com.google.common.eventbus.EventBus;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.apache.log4j.Logger;
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

    private static final String FIRST_PANEL = "firstPanel";
    private static final String SECOND_PANEL = "secondPanel";
    private static final String ASSAY = "assay ";

    //model
    private final MzTabExport mzTabExport = new MzTabExport();
    private final DefaultListModel<String> assayListModel = new DefaultListModel<>();
    private final DefaultMutableTreeNode studyVariableRootNode = new DefaultMutableTreeNode("Study variables");
    private final DefaultTreeModel studyVariableTreeModel = new DefaultTreeModel(studyVariableRootNode);
    //view
    private MzTabExportDialog mzTabExportDialog;
    //parent controller
    @Autowired
    private MainController mainController;
    //services
    @Autowired
    private EventBus eventBus;

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
        mzTabExportDialog = new MzTabExportDialog(mainController.getMainFrame(), true);

        //register to event bus
        eventBus.register(this);

        //select the identification type radio button
        mzTabExportDialog.getIdentificationRadioButton().setSelected(true);
        //select the summary mode radio button
        mzTabExportDialog.getSummaryRadioButton().setSelected(true);

        //set number of assays spinner model
        SpinnerModel model = new SpinnerNumberModel(1, 1, 20, 1);
        mzTabExportDialog.getNumberOfAssaysSpinner().setModel(model);

        //set study variables tree model
        mzTabExportDialog.getStudyVariableTree().setRootVisible(false);
        mzTabExportDialog.getStudyVariableTree().setModel(studyVariableTreeModel);

        //set assay list model
        mzTabExportDialog.getAssayList().setModel(assayListModel);

        //add action listeners
        mzTabExportDialog.getProceedButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                String currentCardName = GuiUtils.getVisibleChildComponent(mzTabExportDialog.getTopPanel());
                switch (currentCardName) {
                    case FIRST_PANEL:
                        List<String> firstPanelValidationMessages = validateFirstPanel();
                        if (firstPanelValidationMessages.isEmpty()) {
                            mzTabExport.setMzTabType(getSelectedMzTabType());
                            mzTabExport.setMzTabMode(getSelectedMzTabMode());
                            mzTabExport.setDescription(mzTabExportDialog.getDescriptionTextArea().getText());

                            //update assay list model
                            assayListModel.clear();
                            for (int i = 1; i <= (int) mzTabExportDialog.getNumberOfAssaysSpinner().getValue(); i++) {
                                assayListModel.add(i - 1, ASSAY + i);
                            }

                            getCardLayout().show(mzTabExportDialog.getTopPanel(), SECOND_PANEL);
                            onCardSwitch();
                        } else {
                            MessageEvent messageEvent = new MessageEvent("Validation failure", firstPanelValidationMessages, JOptionPane.WARNING_MESSAGE);
                            eventBus.post(messageEvent);
                        }
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
                    case SECOND_PANEL:
                        getCardLayout().show(mzTabExportDialog.getTopPanel(), FIRST_PANEL);
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
                    case SECOND_PANEL:
//                        if (psValidationMessages.isEmpty()) {
//                            getCardLayout().show(mzTabExportDialog.getTopPanel(), CONFIRMATION_CARD);
//                        } else {
//                            MessageEvent messageEvent = new MessageEvent("Validation failure", psValidationMessages, JOptionPane.WARNING_MESSAGE);
//                            eventBus.post(messageEvent);
//                        }
                        onCardSwitch();
                        break;
//                    case MAX_QUANT_DATA_IMPORT_CARD:
//                        if (maxQuantValidationMessages.isEmpty()) {
//                            getCardLayout().show(mzTabExportDialog.getTopPanel(), CONFIRMATION_CARD);
//                        } else {
//                            MessageEvent messageEvent = new MessageEvent("Validation failure", maxQuantValidationMessages, JOptionPane.WARNING_MESSAGE);
//                            eventBus.post(messageEvent);
//                        }
//                        onCardSwitch();
//                        break;
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

        mzTabExportDialog.getAddStudyVariableButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!mzTabExportDialog.getStudyVariableTextField().getText().isEmpty()) {
                    DefaultMutableTreeNode studyVariable = new DefaultMutableTreeNode(mzTabExportDialog.getStudyVariableTextField().getText());
                    studyVariableTreeModel.insertNodeInto(studyVariable, studyVariableRootNode, studyVariableTreeModel.getChildCount(studyVariableRootNode));

                    //expand tree with invisible root node trick
                    mzTabExportDialog.getStudyVariableTree().expandPath(new TreePath(studyVariableRootNode.getPath()));

                    //reset text field
                    mzTabExportDialog.getStudyVariableTextField().setText("");
                } else {
                    MessageEvent messageEvent = new MessageEvent("Study variable addition", "Please provide a non-empty study variable description.", JOptionPane.WARNING_MESSAGE);
                    eventBus.post(messageEvent);
                }
            }
        });

        mzTabExportDialog.getDeleteStudyVariableButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                TreeSelectionModel selectionModel = mzTabExportDialog.getStudyVariableTree().getSelectionModel();
                if (selectionModel.getSelectionPaths().length != 0 && !selectionModel.getSelectionPaths()[0].getLastPathComponent().equals(studyVariableRootNode)) {
                    for (TreePath treePath : selectionModel.getSelectionPaths()) {
                        studyVariableTreeModel.removeNodeFromParent((DefaultMutableTreeNode) treePath.getLastPathComponent());
                    }
                } else {
                    MessageEvent messageEvent = new MessageEvent("Study variable(s) removal", "Please select one or more study variables to delete.", JOptionPane.WARNING_MESSAGE);
                    eventBus.post(messageEvent);
                }
            }
        });

        mzTabExportDialog.getAddAssayButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                List selectedValuesList = mzTabExportDialog.getAssayList().getSelectedValuesList();
                if (!selectedValuesList.isEmpty() && isStudyVariableSelected()) {

                } else {
                    MessageEvent messageEvent = new MessageEvent("Assay assigment", "Please select one or more assays and a study variable to assign the assay(s) to.", JOptionPane.WARNING_MESSAGE);
                    eventBus.post(messageEvent);
                }
            }
        });
    }

    @Override
    public void showView() {
        //show first card
        getCardLayout().first(mzTabExportDialog.getTopPanel());
        onCardSwitch();

        //reset tree model
        studyVariableRootNode.removeAllChildren();
        studyVariableTreeModel.reload();

        //reset assay list
        assayListModel.clear();

        GuiUtils.centerDialogOnComponent(mainController.getMainFrame(), mzTabExportDialog);
        mzTabExportDialog.setVisible(true);
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
            case FIRST_PANEL:
                mzTabExportDialog.getBackButton().setEnabled(false);
                mzTabExportDialog.getProceedButton().setEnabled(true);
                mzTabExportDialog.getFinishButton().setEnabled(false);
                //show info
                updateInfo("Click on \"proceed\" to provide one or more study variables.");
                break;
            case SECOND_PANEL:
                mzTabExportDialog.getBackButton().setEnabled(true);
                mzTabExportDialog.getProceedButton().setEnabled(true);
                mzTabExportDialog.getFinishButton().setEnabled(false);
                //show info
                updateInfo("Click on \"finish\" to validate the input and store the run(s).");
                break;
//            case MAX_QUANT_DATA_IMPORT_CARD:
//                mzTabExportDialog.getBackButton().setEnabled(true);
//                mzTabExportDialog.getProceedButton().setEnabled(false);
//                mzTabExportDialog.getFinishButton().setEnabled(true);
//                //show info
//                updateInfo("Click on \"finish\" to validate the input and store the run(s).");
//                break;
//            case CONFIRMATION_CARD:
//                mzTabExportDialog.getBackButton().setEnabled(false);
//                mzTabExportDialog.getProceedButton().setEnabled(false);
//                mzTabExportDialog.getFinishButton().setEnabled(false);
//                updateInfo("");
//                break;
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
     * Validate the user input in the first panel.
     *
     * @return the list of validation messages.
     */
    private List<String> validateFirstPanel() {
        List<String> validationMessages = new ArrayList<>();

        if (mzTabExportDialog.getDescriptionTextArea().getText().isEmpty()) {
            validationMessages.add("Please provide a description.");
        }

        return validationMessages;
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

    /**
     * Check if one and only one study variable is selected in the tree. Returns
     * false if nothing or another path level (assay, root path) is selected.
     *
     * @return the boolean result
     */
    private boolean isStudyVariableSelected() {
        boolean isStudyVariableSelected = false;

        TreePath[] selectionPaths = mzTabExportDialog.getStudyVariableTree().getSelectionModel().getSelectionPaths();
        //check if one and only one node is selected
        if (selectionPaths.length == 1) {
            DefaultMutableTreeNode lastPathComponent = (DefaultMutableTreeNode) selectionPaths[0].getLastPathComponent();
            //check if the parent node is the root node
            if (lastPathComponent.getParent().equals(studyVariableRootNode)) {
                isStudyVariableSelected = true;
            }
        }

        return isStudyVariableSelected;
    }

}
