package com.compomics.colims.client.controller;

import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.event.progress.ProgressEndEvent;
import com.compomics.colims.client.event.progress.ProgressStartEvent;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.MzTabExportDialog;
import com.compomics.colims.core.io.mztab.MzTabExport;
import com.compomics.colims.core.io.mztab.MzTabExporter;
import com.compomics.colims.core.io.mztab.enums.MzTabMode;
import com.compomics.colims.core.io.mztab.enums.MzTabType;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.Sample;
import com.compomics.colims.model.enums.SearchEngineType;
import com.google.common.eventbus.EventBus;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import javax.swing.AbstractButton;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
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
    private static final String THIRD_PANEL = "thirdPanel";
    private static final String LAST_PANEL = "lastPanel";
    private static final String ASSAY = "assay ";

    //model
    private MzTabExport mzTabExport = new MzTabExport();
    private final DefaultListModel<String> assaysToCVListModel = new DefaultListModel<>();
    private final DefaultMutableTreeNode studyVariableRootNode = new DefaultMutableTreeNode("Study variables");
    private final DefaultTreeModel studyVariableTreeModel = new DefaultTreeModel(studyVariableRootNode);
    private final DefaultListModel<String> assaysToRunsListModel = new DefaultListModel<>();
    private final DefaultMutableTreeNode analyticalRunRootNode = new DefaultMutableTreeNode("Analytical runs");
    private final DefaultTreeModel analyticalRunTreeModel = new DefaultTreeModel(analyticalRunRootNode);
    //view
    private MzTabExportDialog mzTabExportDialog;
    //parent controller
    @Autowired
    private MainController mainController;
    //services
    @Autowired
    private EventBus eventBus;
    @Autowired
    private MzTabExporter mzTabExporter;

    public MzTabExport getMzTabExport() {
        return mzTabExport;
    }

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
        //set analytical runs tree model
        mzTabExportDialog.getAnalyticalRunTree().setRootVisible(false);
        mzTabExportDialog.getAnalyticalRunTree().setModel(analyticalRunTreeModel);

        //set assay list models
        mzTabExportDialog.getAssaysToCVList().setModel(assaysToCVListModel);
        mzTabExportDialog.getAssaysToRunsList().setModel(assaysToRunsListModel);

        //configure export directory chooser
        mzTabExportDialog.getExportDirectoryChooser().setMultiSelectionEnabled(false);
        mzTabExportDialog.getExportDirectoryChooser().setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

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

                            //check if the number of assays has changed and if necessary, update the second panel
                            int numberOfAssays = assaysToCVListModel.getSize() + getNumberOfAssaysInTree();
                            if (numberOfAssays != (int) mzTabExportDialog.getNumberOfAssaysSpinner().getValue()) {
                                //update assay list models
                                assaysToCVListModel.clear();
                                assaysToRunsListModel.clear();
                                for (int i = 1; i <= (int) mzTabExportDialog.getNumberOfAssaysSpinner().getValue(); i++) {
                                    assaysToCVListModel.add(i - 1, ASSAY + i);
                                    assaysToRunsListModel.add(i - 1, ASSAY + i);
                                }
                                //update trees and expand
                                removeAllAssaysFromTree(studyVariableTreeModel, studyVariableRootNode, 1);
                                expandTree(mzTabExportDialog.getStudyVariableTree());
                                removeAllAssaysFromTree(analyticalRunTreeModel, analyticalRunRootNode, 2);
                                expandTree(mzTabExportDialog.getAnalyticalRunTree());
                            }

                            getCardLayout().show(mzTabExportDialog.getTopPanel(), SECOND_PANEL);
                            onCardSwitch();
                        } else {
                            MessageEvent messageEvent = new MessageEvent("Validation failure", firstPanelValidationMessages, JOptionPane.WARNING_MESSAGE);
                            eventBus.post(messageEvent);
                        }
                        break;
                    case SECOND_PANEL:
                        List<String> secondPanelValidationMessages = validateSecondPanel();
                        if (secondPanelValidationMessages.isEmpty()) {
                            updateStudyVariablesAssaysRefs();
                            getCardLayout().show(mzTabExportDialog.getTopPanel(), THIRD_PANEL);
                            onCardSwitch();
                        } else {
                            MessageEvent messageEvent = new MessageEvent("Validation failure", secondPanelValidationMessages, JOptionPane.WARNING_MESSAGE);
                            eventBus.post(messageEvent);
                        }
                        break;
                    case THIRD_PANEL:
                        updateAnalyticalRunsToExport();
                        List<String> thirdPanelValidationMessages = validateThirdPanel();
                        if (thirdPanelValidationMessages.isEmpty()) {
                            getCardLayout().show(mzTabExportDialog.getTopPanel(), LAST_PANEL);
                            onCardSwitch();
                        } else {
                            MessageEvent messageEvent = new MessageEvent("Validation failure", thirdPanelValidationMessages, JOptionPane.WARNING_MESSAGE);
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
                List<String> lastPanelValidationMessages = validateLastPanel();
                if (lastPanelValidationMessages.isEmpty()) {
                    mzTabExport.setFileName(mzTabExportDialog.getFileNameTextField().getText());
                    mzTabExport.setExportDirectory(mzTabExportDialog.getExportDirectoryChooser().getSelectedFile());
                    MzTabExporterWorker mzTabExporterWorker = new MzTabExporterWorker();
                    ProgressStartEvent progressStartEvent = new ProgressStartEvent(mainController.getMainFrame(), true, 1, "MzTab export progress. ");
                    eventBus.post(progressStartEvent);
                    mzTabExporterWorker.execute();
                } else {
                    MessageEvent messageEvent = new MessageEvent("Validation failure", lastPanelValidationMessages, JOptionPane.WARNING_MESSAGE);
                    eventBus.post(messageEvent);
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
                //check if one or more study variables have been selected
                if (areOneOrMoreNodesSelected(mzTabExportDialog.getStudyVariableTree(), 1)) {
                    for (TreePath treePath : selectionModel.getSelectionPaths()) {
                        //check for assays linked to the study variable to remove
                        DefaultMutableTreeNode studyVariable = (DefaultMutableTreeNode) treePath.getLastPathComponent();
                        Enumeration<DefaultMutableTreeNode> assays = studyVariable.children();
                        while (assays.hasMoreElements()) {
                            DefaultMutableTreeNode assay = assays.nextElement();
                            //add to assay list model
                            assaysToCVListModel.addElement((String) assay.getUserObject());
                        }

                        studyVariableTreeModel.removeNodeFromParent((DefaultMutableTreeNode) treePath.getLastPathComponent());
                    }
                } else {
                    MessageEvent messageEvent = new MessageEvent("Study variable(s) removal", "Please select one or more study variables to delete.", JOptionPane.WARNING_MESSAGE);
                    eventBus.post(messageEvent);
                }
            }
        });

        mzTabExportDialog.getAddAssaysToSVButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                List<String> selectedValuesList = mzTabExportDialog.getAssaysToCVList().getSelectedValuesList();
                if (!selectedValuesList.isEmpty() && isOneNodeSelected(mzTabExportDialog.getStudyVariableTree(), 1)) {
                    DefaultMutableTreeNode studyVariable = (DefaultMutableTreeNode) mzTabExportDialog.getStudyVariableTree().getSelectionPaths()[0].getLastPathComponent();
                    for (String assay : selectedValuesList) {
                        //remove from assay list
                        assaysToCVListModel.removeElement(assay);

                        //add to tree and expand node
                        DefaultMutableTreeNode assayTreeNode = new DefaultMutableTreeNode(assay);
                        studyVariableTreeModel.insertNodeInto(assayTreeNode, studyVariable, studyVariableTreeModel.getChildCount(studyVariable));

                        mzTabExportDialog.getStudyVariableTree().expandPath(new TreePath(studyVariable.getPath()));
                    }
                } else {
                    MessageEvent messageEvent = new MessageEvent("Assay assigment", "Please select one or more assays and a study variable to assign the assay(s) to.", JOptionPane.WARNING_MESSAGE);
                    eventBus.post(messageEvent);
                }
            }
        });

        mzTabExportDialog.getRemoveAssaysToSVButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                TreeSelectionModel selectionModel = mzTabExportDialog.getStudyVariableTree().getSelectionModel();
                //check if one or more assays have been selected
                if (areOneOrMoreNodesSelected(mzTabExportDialog.getStudyVariableTree(), 2)) {
                    for (TreePath treePath : selectionModel.getSelectionPaths()) {
                        DefaultMutableTreeNode assay = (DefaultMutableTreeNode) treePath.getLastPathComponent();

                        //add to assay list model
                        assaysToCVListModel.addElement((String) assay.getUserObject());

                        studyVariableTreeModel.removeNodeFromParent((DefaultMutableTreeNode) treePath.getLastPathComponent());
                    }
                } else {
                    MessageEvent messageEvent = new MessageEvent("Assay(s) removal", "Please select one or more assays to remove.", JOptionPane.WARNING_MESSAGE);
                    eventBus.post(messageEvent);
                }
            }
        });

        mzTabExportDialog.getAddAssaysToRunsButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                List<String> selectedValuesList = mzTabExportDialog.getAssaysToRunsList().getSelectedValuesList();
                if (!selectedValuesList.isEmpty() && isOneNodeSelected(mzTabExportDialog.getAnalyticalRunTree(), 2)) {
                    DefaultMutableTreeNode analyticalRun = (DefaultMutableTreeNode) mzTabExportDialog.getAnalyticalRunTree().getSelectionPaths()[0].getLastPathComponent();
                    for (String assay : selectedValuesList) {
                        //remove from assay list
                        assaysToRunsListModel.removeElement(assay);

                        //add to tree and expand node
                        DefaultMutableTreeNode assayTreeNode = new DefaultMutableTreeNode(assay);
                        analyticalRunTreeModel.insertNodeInto(assayTreeNode, analyticalRun, analyticalRunTreeModel.getChildCount(analyticalRun));

                        mzTabExportDialog.getAnalyticalRunTree().expandPath(new TreePath(analyticalRun.getPath()));
                    }
                } else {
                    MessageEvent messageEvent = new MessageEvent("Assay assigment", "Please select one or more assays and an analytical run to assign the assay(s) to.", JOptionPane.WARNING_MESSAGE);
                    eventBus.post(messageEvent);
                }
            }
        });

        mzTabExportDialog.getRemoveAssaysToRunsButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                TreeSelectionModel selectionModel = mzTabExportDialog.getAnalyticalRunTree().getSelectionModel();
                //check if one or more assays have been selected
                if (areOneOrMoreNodesSelected(mzTabExportDialog.getAnalyticalRunTree(), 3)) {
                    for (TreePath treePath : selectionModel.getSelectionPaths()) {
                        DefaultMutableTreeNode assay = (DefaultMutableTreeNode) treePath.getLastPathComponent();

                        //add to assay list model
                        assaysToRunsListModel.addElement((String) assay.getUserObject());

                        analyticalRunTreeModel.removeNodeFromParent((DefaultMutableTreeNode) treePath.getLastPathComponent());
                    }
                } else {
                    MessageEvent messageEvent = new MessageEvent("Assay(s) removal", "Please select one or more assays to remove.", JOptionPane.WARNING_MESSAGE);
                    eventBus.post(messageEvent);
                }
            }
        });

        mzTabExportDialog.getExportDirectoryBrowseButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                //in response to the button click, show open dialog
                int returnVal = mzTabExportDialog.getExportDirectoryChooser().showOpenDialog(mzTabExportDialog);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File exportDirectory = mzTabExportDialog.getExportDirectoryChooser().getSelectedFile();

                    //show directory path in textfield
                    mzTabExportDialog.getExportDirectoryTextField().setText(exportDirectory.getAbsolutePath());
                }
            }
        });
    }

    @Override
    public void showView() {
        //show first card
        getCardLayout().first(mzTabExportDialog.getTopPanel());
        onCardSwitch();

        //dereference MzTabExport instance but keep samples
        List<Sample> samples = mzTabExport.getSamples();
        mzTabExport = new MzTabExport();
        mzTabExport.setSamples(samples);

        //reset first panel input
        mzTabExportDialog.getDescriptionTextArea().setText("");
        mzTabExportDialog.getNumberOfAssaysSpinner().getModel().setValue(1);

        //reset tree models
        studyVariableRootNode.removeAllChildren();
        studyVariableTreeModel.reload();
        analyticalRunRootNode.removeAllChildren();
        analyticalRunTreeModel.reload();

        //reset assay lists
        assaysToCVListModel.clear();
        assaysToRunsListModel.clear();

        //build analytical run tree
        for (Sample sample : mzTabExport.getSamples()) {
            DefaultMutableTreeNode sampleNode = new DefaultMutableTreeNode(sample);
            analyticalRunRootNode.add(sampleNode);
            for (AnalyticalRun analyticalRun : sample.getAnalyticalRuns()) {
                DefaultMutableTreeNode analyticalRunNode = new DefaultMutableTreeNode(analyticalRun);
                sampleNode.add(analyticalRunNode);
            }
        }
        expandTree(mzTabExportDialog.getAnalyticalRunTree());

        //reset second panel text field
        mzTabExportDialog.getStudyVariableTextField().setText("");

        //reset last panel text fields
        mzTabExportDialog.getFileNameTextField().setText("");
        mzTabExportDialog.getExportDirectoryTextField().setText("");

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
                updateInfo("Click on \"proceed\" to link study variables to assays.");
                break;
            case SECOND_PANEL:
                mzTabExportDialog.getBackButton().setEnabled(true);
                mzTabExportDialog.getProceedButton().setEnabled(true);
                mzTabExportDialog.getFinishButton().setEnabled(false);
                //show info
                updateInfo("Click on \"proceed\" to link analytical runs to assays.");
                break;
            case THIRD_PANEL:
                mzTabExportDialog.getBackButton().setEnabled(true);
                mzTabExportDialog.getProceedButton().setEnabled(true);
                mzTabExportDialog.getFinishButton().setEnabled(false);
                //show info
                updateInfo("Click on \"proceed\" to provide a file name and export directory.");
                break;
            case LAST_PANEL:
                mzTabExportDialog.getBackButton().setEnabled(true);
                mzTabExportDialog.getProceedButton().setEnabled(false);
                mzTabExportDialog.getFinishButton().setEnabled(true);
                //show info
                updateInfo("Click on \"finish\" to finalize the export.");
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
     * Validate the user input in the second panel.
     *
     * @return the list of validation messages.
     */
    private List<String> validateSecondPanel() {
        List<String> validationMessages = new ArrayList<>();

        //check for unassignes assays
        if (!assaysToCVListModel.isEmpty()) {
            validationMessages.add("All assays must be linked to study variables.");
        }
        //check for study variables nodes with no assays
        Enumeration children = studyVariableRootNode.children();
        while (children.hasMoreElements()) {
            if (((DefaultMutableTreeNode) children.nextElement()).getChildCount() == 0) {
                validationMessages.add("All study variables must have at least one assay.");
                break;
            }
        }

        return validationMessages;
    }

    /**
     * Validate the user input in the third panel.
     *
     * @return the list of validation messages.
     */
    private List<String> validateThirdPanel() {
        List<String> validationMessages = new ArrayList<>();

        if (!assaysToRunsListModel.isEmpty()) {
            validationMessages.add("All assays must be linked to analytical runs.");
        }
        //ensure that all runs linked with assays have been searched with the same search engine
        List<AnalyticalRun> runs = mzTabExport.getRuns();
        SearchEngineType firstRunSearchEngineType = runs.get(0).getSearchAndValidationSettings().getSearchEngine().getSearchEngineType();
        for (int i = 1; i < runs.size(); i++) {
            if (!firstRunSearchEngineType.equals(runs.get(i).getSearchAndValidationSettings().getSearchEngine().getSearchEngineType())) {
                validationMessages.add("All runs linked to assays must have the same search engine");
            }
        }

        return validationMessages;
    }

    /**
     * Validate the user input in the last panel.
     *
     * @return the list of validation messages.
     */
    private List<String> validateLastPanel() {
        List<String> validationMessages = new ArrayList<>();

        if (mzTabExportDialog.getFileNameTextField().getText().isEmpty()) {
            validationMessages.add("Please provide a file name.");
        }
        if (mzTabExportDialog.getExportDirectoryTextField().getText().isEmpty()) {
            validationMessages.add("Please provide an export directory.");
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
     * Check if one and only one one with the given level is selected in the
     * tree. Returns false if nothing or another path level (assay, root path)
     * is selected.
     *
     * @param tree the JTree instance
     * @param level the level of the node (0 for root node)
     * @return the boolean result
     */
    private boolean isOneNodeSelected(JTree tree, int level) {
        boolean isOneNodeSelected = false;

        TreePath[] selectionPaths = tree.getSelectionModel().getSelectionPaths();
        //check if one and only one node is selected
        if (selectionPaths.length == 1) {
            DefaultMutableTreeNode lastPathComponent = (DefaultMutableTreeNode) selectionPaths[0].getLastPathComponent();
            //check if the level of the node is correct
            if (lastPathComponent.getLevel() == level) {
                isOneNodeSelected = true;
            }
        }

        return isOneNodeSelected;
    }

    /**
     * Check if one or more nodes are selected in the tree. Returns false if
     * nothing or another path level (study variable or assay, root path) is
     * selected.
     *
     * @param tree the JTree instance
     * @param level the level of the node(s) (0 for root node) nodes nodes
     * @return the boolean result
     */
    private boolean areOneOrMoreNodesSelected(JTree tree, int level) {
        boolean areOneOrMoreNodesSelected = true;

        TreePath[] selectionPaths = tree.getSelectionModel().getSelectionPaths();
        //check if one or more nodes are selected
        if (selectionPaths.length >= 1) {
            for (TreePath selectionPath : selectionPaths) {
                DefaultMutableTreeNode lastPathComponent = (DefaultMutableTreeNode) selectionPath.getLastPathComponent();
                //check if the level of the nodes is correct
                if (lastPathComponent.getLevel() != level) {
                    areOneOrMoreNodesSelected = false;
                    break;
                }
            }
        } else {
            areOneOrMoreNodesSelected = false;
        }

        return areOneOrMoreNodesSelected;
    }

    /**
     * Get the number of assays in the study variables tree.
     *
     * @return the number of assays
     */
    private int getNumberOfAssaysInTree() {
        int numberOfAssaysInTree = 0;

        Enumeration studyVariables = studyVariableRootNode.children();
        while (studyVariables.hasMoreElements()) {
            DefaultMutableTreeNode studyVariable = (DefaultMutableTreeNode) studyVariables.nextElement();
            numberOfAssaysInTree += studyVariable.getChildCount();
        }

        return numberOfAssaysInTree;
    }

    /**
     * Remove all assays from the given tree.
     *
     * @param treeModel the JTree model instance
     * @param rootNode the tree root node
     * @param assayParentLevel the level of the assay parent node
     */
    private void removeAllAssaysFromTree(DefaultTreeModel treeModel, DefaultMutableTreeNode rootNode, int assayParentLevel) {
        Enumeration nodes = rootNode.breadthFirstEnumeration();
        while (nodes.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) nodes.nextElement();
            if (node.getLevel() == assayParentLevel) {
                node.removeAllChildren();
            }
        }
        treeModel.reload();
    }

    /**
     * Expand all nodes in the given tree.
     *
     * @param tree the JTree instance
     */
    private void expandTree(JTree tree) {
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
    }

    /**
     * Update the study variables to assays references.
     */
    private void updateStudyVariablesAssaysRefs() {
        Map<String, int[]> studyVariablesAssaysRefs = mzTabExport.getStudyVariablesAssaysRefs();
        studyVariablesAssaysRefs.clear();

        Enumeration studyVariables = studyVariableRootNode.children();
        while (studyVariables.hasMoreElements()) {
            DefaultMutableTreeNode studyVariable = (DefaultMutableTreeNode) studyVariables.nextElement();
            Enumeration assays = studyVariable.children();
            int[] assayNumbers = new int[studyVariable.getChildCount()];
            int index = 0;
            while (assays.hasMoreElements()) {
                String assay = ((DefaultMutableTreeNode) assays.nextElement()).getUserObject().toString();
                int assayNumber = Integer.valueOf(assay.substring(assay.indexOf(" ") + 1));
                assayNumbers[index] = assayNumber;
            }
            studyVariablesAssaysRefs.put(studyVariable.toString(), assayNumbers);
        }
    }

    /**
     * Update the analytical runs to export; set the list of runs and assays
     * references in the MzTabExport instance.
     */
    private void updateAnalyticalRunsToExport() {
        List<AnalyticalRun> analyticalRuns = mzTabExport.getRuns();
        analyticalRuns.clear();
        Map<AnalyticalRun, int[]> analyticalRunAssaysRefs = mzTabExport.getAnalyticalRunsAssaysRefs();
        analyticalRunAssaysRefs.clear();

        Enumeration nodes = analyticalRunRootNode.breadthFirstEnumeration();
        while (nodes.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) nodes.nextElement();
            //the analytical runs are on node level 2
            if (node.getLevel() == 2) {
                Enumeration assays = node.children();
                int[] assayNumbers = new int[node.getChildCount()];
                int index = 0;
                while (assays.hasMoreElements()) {
                    String assay = ((DefaultMutableTreeNode) assays.nextElement()).getUserObject().toString();
                    int assayNumber = Integer.valueOf(assay.substring(assay.indexOf(" ") + 1));
                    assayNumbers[index] = assayNumber;
                    index++;
                }
                AnalyticalRun analyticalRun = (AnalyticalRun) ((DefaultMutableTreeNode) node).getUserObject();
                analyticalRuns.add(analyticalRun);
                analyticalRunAssaysRefs.put(analyticalRun, assayNumbers);
            }
        }
    }

    /**
     * MzTab exporter swing worker.
     */
    private class MzTabExporterWorker extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {

            LOGGER.info("Exporting mzTab file " + mzTabExport.getFileName() + " to directory " + mzTabExport.getExportDirectory());
            mzTabExporter.export(mzTabExport);
            LOGGER.info("Finished exporting mzTab file " + mzTabExport.getFileName());

            Thread.sleep(10000);

            return null;
        }

        @Override
        protected void done() {
            try {
                get();
            } catch (InterruptedException | ExecutionException ex) {
                LOGGER.error(ex.getMessage(), ex);
            } catch (CancellationException ex) {
                LOGGER.info("Cancelling mzTab export.");
            } finally {
                //hide progress dialog
                eventBus.post(new ProgressEndEvent());
                //hide export dialog
                mzTabExportDialog.setVisible(false);
            }
        }
    }
}
