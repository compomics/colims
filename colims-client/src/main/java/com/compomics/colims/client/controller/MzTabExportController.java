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
import com.compomics.colims.core.service.ProteinGroupQuantLabeledService;
import com.compomics.colims.core.service.QuantificationMethodService;
import com.compomics.colims.model.*;
import com.compomics.colims.model.enums.SearchEngineType;
import com.google.common.eventbus.EventBus;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

/**
 * The mzTab export view controller.
 *
 * @author Niels Hulstaert
 */
@Component("mzTabExportController")
@Lazy
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
    private final DefaultListModel<String> quantificationLabelsListModel = new DefaultListModel<>();
    private final DefaultListModel<String> assaysToCVListModel = new DefaultListModel<>();
    private final DefaultMutableTreeNode quantificationReagentRootNode = new DefaultMutableTreeNode("Quantification Reagent");
    private final DefaultTreeModel quantificationReagentTreeModel = new DefaultTreeModel(quantificationReagentRootNode);
    private final DefaultMutableTreeNode studyVariableRootNode = new DefaultMutableTreeNode("Study variables");
    private final DefaultTreeModel studyVariableTreeModel = new DefaultTreeModel(studyVariableRootNode);
    private final DefaultMutableTreeNode analyticalRunRootNode = new DefaultMutableTreeNode("Analytical runs");
    private final DefaultTreeModel analyticalRunTreeModel = new DefaultTreeModel(analyticalRunRootNode);

    /**
     * The FASTA DBs location as provided in the distributed properties file.
     */
    @Value("${fastas.path}")
    private String fastasPath = "";

    private int assayNumber = 0;
    //view
    private MzTabExportDialog mzTabExportDialog;
    //parent controller
    private final MainController mainController;
    //services
    private final EventBus eventBus;
    private final MzTabExporter mzTabExporter;
    private final ProteinGroupQuantLabeledService proteinGroupQuantLabeledService;
    private final QuantificationMethodService quantificationMethodService;

    @Autowired
    public MzTabExportController(MzTabExporter mzTabExporter, EventBus eventBus, MainController mainController,
                                 ProteinGroupQuantLabeledService proteinGroupQuantLabeledService, QuantificationMethodService quantificationMethodService) {
        this.mzTabExporter = mzTabExporter;
        this.eventBus = eventBus;
        this.mainController = mainController;
        this.proteinGroupQuantLabeledService = proteinGroupQuantLabeledService;
        this.quantificationMethodService = quantificationMethodService;
    }

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
    @PostConstruct
    public void init() {
        //init view
        mzTabExportDialog = new MzTabExportDialog(mainController.getMainFrame(), true);

        //register to event bus
        eventBus.register(this);

        //select the identification type radio button
        mzTabExportDialog.getIdentificationRadioButton().setSelected(true);
        //select the summary mode radio button
        mzTabExportDialog.getSummaryRadioButton().setSelected(true);

        //set study variables tree model
        mzTabExportDialog.getStudyVariableTree().setRootVisible(false);
        mzTabExportDialog.getStudyVariableTree().setModel(studyVariableTreeModel);
        //set analytical runs tree model
        mzTabExportDialog.getAssayWithRunsTree().setRootVisible(false);
        mzTabExportDialog.getAssayWithRunsTree().setModel(analyticalRunTreeModel);

        //set assay list models
        mzTabExportDialog.getAssaysToCVList().setModel(assaysToCVListModel);

        //set quantification labels list model
        mzTabExportDialog.getQuantificationLabelList().setModel(quantificationLabelsListModel);
        //set quantification reagent tree model
        mzTabExportDialog.getQuantificationReagentTree().setRootVisible(false);
        mzTabExportDialog.getQuantificationReagentTree().setModel(quantificationReagentTreeModel);

        //configure export directory chooser
        mzTabExportDialog.getExportDirectoryChooser().setMultiSelectionEnabled(false);
        mzTabExportDialog.getExportDirectoryChooser().setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        //add action listeners
        mzTabExportDialog.getProceedButton().addActionListener(e -> {
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
                        if (numberOfAssays != assayNumber) {
                            //update assay list models
                            assaysToCVListModel.clear();
                            for (int i = 1; i <= assayNumber; i++) {
                                assaysToCVListModel.add(i - 1, ASSAY + i);
                            }
                            //update tree and expand
                            removeAllAssaysFromTree(studyVariableTreeModel, studyVariableRootNode, 1);
                            expandTree(mzTabExportDialog.getStudyVariableTree());
                        }
                        // create quantification reagent-label map
                        for (int i = 0; i < quantificationReagentTreeModel.getChildCount(quantificationReagentRootNode); i++) {
                            mzTabExport.getQuantificationReagentLabelMatch().put(quantificationReagentRootNode.getChildAt(i).toString(), quantificationReagentRootNode.getChildAt(i).getChildAt(0).toString());
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
                        getCardLayout().show(mzTabExportDialog.getTopPanel(), LAST_PANEL);
                        onCardSwitch();
                    } else {
                        MessageEvent messageEvent = new MessageEvent("Validation failure", secondPanelValidationMessages, JOptionPane.WARNING_MESSAGE);
                        eventBus.post(messageEvent);
                    }
                    break;
                default:
                    break;
            }
        });

        mzTabExportDialog.getBackButton().addActionListener(e -> {
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
        });

        mzTabExportDialog.getFinishButton().addActionListener(e -> {
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
        });

        mzTabExportDialog.getCloseButton().addActionListener(e -> mzTabExportDialog.dispose());

        mzTabExportDialog.getAddStudyVariableButton().addActionListener(e -> {
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
        });

        mzTabExportDialog.getDeleteStudyVariableButton().addActionListener(e -> {
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
        });

        mzTabExportDialog.getAddAssaysToSVButton().addActionListener(e -> {
            List<String> selectedValuesList = mzTabExportDialog.getAssaysToCVList().getSelectedValuesList();
            if (!selectedValuesList.isEmpty() && isOneNodeSelected(mzTabExportDialog.getStudyVariableTree(), 1)) {
                DefaultMutableTreeNode studyVariable = (DefaultMutableTreeNode) mzTabExportDialog.getStudyVariableTree().getSelectionPaths()[0].getLastPathComponent();
                selectedValuesList.stream().forEach(assay -> {
                    //remove from assay list
                    assaysToCVListModel.removeElement(assay);

                    //add to tree and expand node
                    DefaultMutableTreeNode assayTreeNode = new DefaultMutableTreeNode(assay);
                    studyVariableTreeModel.insertNodeInto(assayTreeNode, studyVariable, studyVariableTreeModel.getChildCount(studyVariable));

                    mzTabExportDialog.getStudyVariableTree().expandPath(new TreePath(studyVariable.getPath()));
                });
            } else {
                MessageEvent messageEvent = new MessageEvent("Assay assigment", "Please select one or more assays and a study variable to assign the assay(s) to.", JOptionPane.WARNING_MESSAGE);
                eventBus.post(messageEvent);
            }
        });

        mzTabExportDialog.getRemoveAssaysToSVButton().addActionListener(e -> {
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
        });

        mzTabExportDialog.getMatchReagentAndLabelButton().addActionListener(e -> {
            String selectedLabel = mzTabExportDialog.getQuantificationLabelList().getSelectedValue();

            if (selectedLabel != null && isOneNodeSelected(mzTabExportDialog.getQuantificationReagentTree(), 1)) {
                DefaultMutableTreeNode quantificationReagent = (DefaultMutableTreeNode) mzTabExportDialog.getQuantificationReagentTree().getSelectionPaths()[0].getLastPathComponent();
                if (quantificationReagent.getChildCount() == 0) {
                    // remove label from list
                    quantificationLabelsListModel.removeElement(selectedLabel);
                    // add to reagent tree and expand node
                    DefaultMutableTreeNode labelTreeNode = new DefaultMutableTreeNode(selectedLabel);
                    quantificationReagentTreeModel.insertNodeInto(labelTreeNode, quantificationReagent, quantificationReagentTreeModel.getChildCount(quantificationReagent));

                    mzTabExportDialog.getQuantificationReagentTree().expandPath(new TreePath(quantificationReagent.getPath()));
                } else {
                    MessageEvent messageEvent = new MessageEvent("Label assigment", "One reagent can have only one label. (one to one)", JOptionPane.WARNING_MESSAGE);
                    eventBus.post(messageEvent);
                }

            } else {
                MessageEvent messageEvent = new MessageEvent("Label assigment", "Please select one label and one reagent to match label to.", JOptionPane.WARNING_MESSAGE);
                eventBus.post(messageEvent);
            }
        });

        mzTabExportDialog.getRemoveReagentAndLabelButton().addActionListener(e -> {
            TreeSelectionModel selectionModel = mzTabExportDialog.getQuantificationReagentTree().getSelectionModel();
            //check if one or more assays have been selected
            if (areOneOrMoreNodesSelected(mzTabExportDialog.getQuantificationReagentTree(), 2)) {
                for (TreePath treePath : selectionModel.getSelectionPaths()) {
                    DefaultMutableTreeNode label = (DefaultMutableTreeNode) treePath.getLastPathComponent();

                    //add to assay list model
                    quantificationLabelsListModel.addElement((String) label.getUserObject());

                    quantificationReagentTreeModel.removeNodeFromParent((DefaultMutableTreeNode) treePath.getLastPathComponent());
                }
                expandTree(mzTabExportDialog.getQuantificationReagentTree());
            } else {
                MessageEvent messageEvent = new MessageEvent("Label(s) removal", "Please select one or more labels to remove.", JOptionPane.WARNING_MESSAGE);
                eventBus.post(messageEvent);
            }
        });

        mzTabExportDialog.getExportDirectoryBrowseButton().addActionListener(e -> {
            //in response to the button click, show open dialog
            int returnVal = mzTabExportDialog.getExportDirectoryChooser().showOpenDialog(mzTabExportDialog);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File exportDirectory = mzTabExportDialog.getExportDirectoryChooser().getSelectedFile();

                //show directory path in textfield
                mzTabExportDialog.getExportDirectoryTextField().setText(exportDirectory.getAbsolutePath());
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

        //reset tree models
        studyVariableRootNode.removeAllChildren();
        studyVariableTreeModel.reload();
        analyticalRunRootNode.removeAllChildren();
        analyticalRunTreeModel.reload();
        quantificationReagentRootNode.removeAllChildren();
        quantificationReagentTreeModel.reload();

        //reset assay lists
        assaysToCVListModel.clear();

        //build analytical run tree
        mzTabExport.getSamples().stream().forEach(sample -> {
            DefaultMutableTreeNode sampleNode = new DefaultMutableTreeNode(sample);
            analyticalRunRootNode.add(sampleNode);
            sample.getAnalyticalRuns().stream().forEach(analyticalRun -> {
                DefaultMutableTreeNode analyticalRunNode = new DefaultMutableTreeNode(analyticalRun);
                sampleNode.add(analyticalRunNode);
            });
        });
        setAnalyticalRunsAssays();

        setQuantificationReagentsAndLabels();

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
        for (Enumeration<AbstractButton> buttons = mzTabExportDialog.getTypeButtonGroup().getElements(); buttons.hasMoreElements(); ) {
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

        if (!quantificationLabelsListModel.isEmpty()) {
            validationMessages.add("All labels should be matched to reagents");
        }

        //ensure that all runs linked with assays have been searched with the same search engine
        List<AnalyticalRun> runs = mzTabExport.getRuns();
        SearchEngineType firstRunSearchEngineType = runs.get(0).getSearchAndValidationSettings().getSearchEngine().getSearchEngineType();
        for (int i = 1; i < runs.size(); i++) {
            if (!firstRunSearchEngineType.equals(runs.get(i).getSearchAndValidationSettings().getSearchEngine().getSearchEngineType())) {
                validationMessages.add("All runs linked to assays must have the same search engine");
            }
        }
        if (runs.get(0).getQuantificationSettings() != null) {
            QuantificationMethod firstQuantificationMethodCvParam = runs.get(0).getQuantificationSettings().getQuantificationMethod();
            for (int i = 1; i < runs.size(); i++) {
                if (!firstQuantificationMethodCvParam.equals(runs.get(i).getQuantificationSettings().getQuantificationMethod())) {
                    validationMessages.add("All runs should have the same quantification method");
                }
                if (quantificationMethodService.fetchQuantificationMethodHasReagents(runs.get(i).getQuantificationSettings().getQuantificationMethod()).size() != mzTabExport.getAnalyticalRunsAssaysRefs().get(mzTabExport.getRuns().get(i)).length) {
                    validationMessages.add("Assay number should be the same with quantification reagent of the run " + runs.get(i).getName());
                }
            }
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
        for (Enumeration<AbstractButton> buttons = mzTabExportDialog.getModeButtonGroup().getElements(); buttons.hasMoreElements(); ) {
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
     * @param tree  the JTree instance
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
     * @param tree  the JTree instance
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
     * @param treeModel        the JTree model instance
     * @param rootNode         the tree root node
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
        mzTabExport.getStudyVariablesAssaysRefs().clear();

        Enumeration studyVariables = studyVariableRootNode.children();
        while (studyVariables.hasMoreElements()) {
            DefaultMutableTreeNode studyVariable = (DefaultMutableTreeNode) studyVariables.nextElement();
            Enumeration assays = studyVariable.children();
            int[] assayNumbers = new int[studyVariable.getChildCount()];
            int index = 0;
            while (assays.hasMoreElements()) {
                String assay = ((DefaultMutableTreeNode) assays.nextElement()).getUserObject().toString();
                int assayNo = Integer.valueOf(assay.substring(assay.indexOf(" ") + 1));
                assayNumbers[index] = assayNo;
                index++;
            }
            mzTabExport.getStudyVariablesAssaysRefs().put(studyVariable.toString(), assayNumbers);
        }
    }

    /**
     * Set the analyticalRunAssaysRef
     */
    private void setAnalyticalRunsAssays() {
        mzTabExport.getRuns().clear();
        mzTabExport.getAnalyticalRunsAssaysRefs().clear();

        removeAllAssaysFromTree(analyticalRunTreeModel, analyticalRunRootNode, 2);
        expandTree(mzTabExportDialog.getAssayWithRunsTree());
        assayNumber = 0;
        Enumeration nodes = analyticalRunRootNode.breadthFirstEnumeration();
        while (nodes.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) nodes.nextElement();
            //the analytical runs are on node level 2
            if (node.getLevel() == 2) {
                AnalyticalRun analyticalRun = (AnalyticalRun) node.getUserObject();
                List<QuantificationMethodHasReagent> quantificationMethodHasReagents = quantificationMethodService.fetchQuantificationMethodHasReagents(getQuantificationSettings(analyticalRun).getQuantificationMethod());
                int[] assayArray = new int[quantificationMethodHasReagents.size()];
                // label free experiments
                if (quantificationMethodHasReagents.isEmpty()) {
                    assayNumber++;
                    //add to tree and expand node
                    DefaultMutableTreeNode assayTreeNode = new DefaultMutableTreeNode(ASSAY + assayNumber);
                    analyticalRunTreeModel.insertNodeInto(assayTreeNode, node, analyticalRunTreeModel.getChildCount(node));
                    // analyticalRun.add(assayTreeNode);
                    mzTabExportDialog.getAssayWithRunsTree().expandPath(new TreePath(node.getPath()));
                }
                // labeled experiments
                for (int counter = 0; counter < quantificationMethodHasReagents.size(); counter++) {
                    assayNumber++;
                    assayArray[counter] = assayNumber;
                    //add to tree and expand node
                    DefaultMutableTreeNode assayTreeNode = new DefaultMutableTreeNode(ASSAY + assayNumber);
                    analyticalRunTreeModel.insertNodeInto(assayTreeNode, node, analyticalRunTreeModel.getChildCount(node));
                    mzTabExportDialog.getAssayWithRunsTree().expandPath(new TreePath(node.getPath()));
                }
                mzTabExport.getAnalyticalRunsAssaysRefs().put(analyticalRun, assayArray);

                mzTabExport.getRuns().add(analyticalRun);
            }
        }
    }

    /**
     * Set Quantification Reagents and Labels
     */
    private void setQuantificationReagentsAndLabels() {
        int quantLabelIndex = 0;

        List<QuantificationMethodHasReagent> quantificationMethodHasReagents = quantificationMethodService.fetchQuantificationMethodHasReagents(
                getQuantificationSettings(mzTabExport.getRuns().get(0)).getQuantificationMethod());

        if (quantificationMethodHasReagents.isEmpty()) {
            mzTabExportDialog.getQuantificationLabelList().setVisible(false);
            mzTabExportDialog.getQuantificationReagentTree().setVisible(false);
            mzTabExportDialog.getMatchReagentAndLabelButton().setVisible(false);
            mzTabExportDialog.getRemoveReagentAndLabelButton().setVisible(false);
        } else {
            mzTabExportDialog.getQuantificationLabelList().setVisible(true);
            mzTabExportDialog.getQuantificationReagentTree().setVisible(true);
            mzTabExportDialog.getMatchReagentAndLabelButton().setVisible(true);
            mzTabExportDialog.getRemoveReagentAndLabelButton().setVisible(true);
        }
        for (int counter = 0; counter < quantificationMethodHasReagents.size(); counter++) {
            quantificationLabelsListModel.add(quantLabelIndex, proteinGroupQuantLabeledService.getProteinGroupQuantLabeledForRun(mzTabExport.getRuns().get(0).getId()).get(counter).getLabel());
            DefaultMutableTreeNode quantificationReagentTreeNode = new DefaultMutableTreeNode(quantificationMethodHasReagents.get(counter).getQuantificationReagent().getName());
            quantificationReagentTreeModel.insertNodeInto(quantificationReagentTreeNode, quantificationReagentRootNode, quantificationReagentTreeModel.getChildCount(quantificationReagentRootNode));
            mzTabExportDialog.getQuantificationReagentTree().expandPath(new TreePath(quantificationReagentRootNode.getPath()));
            quantLabelIndex++;
        }

    }

    /**
     * Get the quantification settings.
     *
     * @param analyticalRun the {@link AnalyticalRun} instance
     * @return quantificationSettingsMap
     */
    private QuantificationSettings getQuantificationSettings(AnalyticalRun analyticalRun) {
        return analyticalRun.getQuantificationSettings();
    }

    /**
     * MzTab exporter swing worker.
     */
    private class MzTabExporterWorker extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {

            LOGGER.info("Exporting mzTab file " + mzTabExport.getFileName() + " to directory " + mzTabExport.getExportDirectory());
            try {
                mzTabExport.setFastaDirectory(Paths.get(fastasPath));
                mzTabExporter.export(mzTabExport);
                ProgressEndEvent progressEndEvent = new ProgressEndEvent();
                eventBus.post(progressEndEvent);
                eventBus.post(new MessageEvent("Info", "Exporting mzTab file has finished", JOptionPane.INFORMATION_MESSAGE));
                LOGGER.info("Finished exporting mzTab file " + mzTabExport.getFileName());
            } catch (Exception e) {
                eventBus.post(new MessageEvent("Error", e.getMessage(), JOptionPane.ERROR_MESSAGE));
            }

            Thread.sleep(1);

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
