package com.compomics.colims.client.controller;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.AdvancedTableModel;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.GlazedListsSwing;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.model.tableformat.ExperimentSimpleTableFormat;
import com.compomics.colims.client.model.tableformat.ProjectSimpleTableFormat;
import com.compomics.colims.client.model.tableformat.SampleSimpleTableFormat;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.AnalyticalRunSetupDialog;
import com.compomics.colims.distributed.storage.enums.StorageType;
import com.compomics.colims.model.Experiment;
import com.compomics.colims.model.Project;
import com.compomics.colims.model.Sample;
import com.compomics.colims.model.comparator.IdComparator;
import com.google.common.eventbus.EventBus;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.swing.AbstractButton;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("analyticalRunSetupController")
public class AnalyticalRunSetupController implements Controllable {

    private static final Logger LOGGER = Logger.getLogger(AnalyticalRunSetupController.class);
    private static final String SAMPLE_SELECTION_CARD = "sampleSelectionPanel";
    private static final String DATA_TYPE_SELECTION_CARD = "dataTypeSelectionPanel";

    //model
    private AdvancedTableModel<Project> projectsTableModel;
    private DefaultEventSelectionModel<Project> projectsSelectionModel;
    private EventList<Experiment> experiments = new BasicEventList<>();
    private AdvancedTableModel<Experiment> experimentsTableModel;
    private DefaultEventSelectionModel<Experiment> experimentsSelectionModel;
    private EventList<Sample> samples = new BasicEventList<>();
    private AdvancedTableModel<Sample> samplesTableModel;
    private DefaultEventSelectionModel<Sample> samplesSelectionModel;
    //view
    private AnalyticalRunSetupDialog analyticalRunSetupDialog;
    //parent controller
    @Autowired
    private ColimsController colimsController;
    //services
    @Autowired
    private EventBus eventBus;

    public AnalyticalRunSetupDialog getAnalyticalRunSetupDialog() {
        return analyticalRunSetupDialog;
    }

    @Override
    public void init() {
        //init view
        analyticalRunSetupDialog = new AnalyticalRunSetupDialog(colimsController.getColimsFrame(), true);

        //register to event bus
        eventBus.register(this);

        //select peptideShaker radio button
        analyticalRunSetupDialog.getPeptideShakerRadioButton().setSelected(true);

        //init projects table
        SortedList<Project> sortedProjects = new SortedList<>(colimsController.getProjects(), new IdComparator());
        projectsTableModel = GlazedListsSwing.eventTableModel(sortedProjects, new ProjectSimpleTableFormat());
        analyticalRunSetupDialog.getProjectsTable().setModel(projectsTableModel);
        projectsSelectionModel = new DefaultEventSelectionModel<>(sortedProjects);
        projectsSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        analyticalRunSetupDialog.getProjectsTable().setSelectionModel(projectsSelectionModel);

        //set column widths
        analyticalRunSetupDialog.getProjectsTable().getColumnModel().getColumn(ProjectSimpleTableFormat.PROJECT_ID).setPreferredWidth(10);
        analyticalRunSetupDialog.getProjectsTable().getColumnModel().getColumn(ProjectSimpleTableFormat.TITLE).setPreferredWidth(100);
        analyticalRunSetupDialog.getProjectsTable().getColumnModel().getColumn(ProjectSimpleTableFormat.LABEL).setPreferredWidth(50);
        analyticalRunSetupDialog.getProjectsTable().getColumnModel().getColumn(ProjectSimpleTableFormat.NUMBER_OF_EXPERIMENTS).setPreferredWidth(20);

        //set sorting
        TableComparatorChooser projectsTableSorter = TableComparatorChooser.install(
                analyticalRunSetupDialog.getProjectsTable(), sortedProjects, TableComparatorChooser.SINGLE_COLUMN);

        //init projects experiment table
        SortedList<Experiment> sortedExperiments = new SortedList<>(experiments, new IdComparator());
        experimentsTableModel = GlazedListsSwing.eventTableModel(sortedExperiments, new ExperimentSimpleTableFormat());
        analyticalRunSetupDialog.getExperimentsTable().setModel(experimentsTableModel);
        experimentsSelectionModel = new DefaultEventSelectionModel<>(sortedExperiments);
        experimentsSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        analyticalRunSetupDialog.getExperimentsTable().setSelectionModel(experimentsSelectionModel);

        //set column widths
        analyticalRunSetupDialog.getExperimentsTable().getColumnModel().getColumn(ExperimentSimpleTableFormat.EXPERIMENT_ID).setPreferredWidth(10);
        analyticalRunSetupDialog.getExperimentsTable().getColumnModel().getColumn(ExperimentSimpleTableFormat.TITLE).setPreferredWidth(100);
        analyticalRunSetupDialog.getExperimentsTable().getColumnModel().getColumn(ExperimentSimpleTableFormat.NUMBER).setPreferredWidth(20);
        analyticalRunSetupDialog.getExperimentsTable().getColumnModel().getColumn(ExperimentSimpleTableFormat.NUMBER_OF_SAMPLES).setPreferredWidth(20);

        //set sorting
        TableComparatorChooser experimentsTableSorter = TableComparatorChooser.install(
                analyticalRunSetupDialog.getExperimentsTable(), sortedExperiments, TableComparatorChooser.SINGLE_COLUMN);

        //init experiment samples table
        SortedList<Sample> sortedSamples = new SortedList<>(samples, new IdComparator());
        samplesTableModel = GlazedListsSwing.eventTableModel(sortedSamples, new SampleSimpleTableFormat());
        analyticalRunSetupDialog.getSamplesTable().setModel(samplesTableModel);
        samplesSelectionModel = new DefaultEventSelectionModel<>(sortedSamples);
        samplesSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        analyticalRunSetupDialog.getSamplesTable().setSelectionModel(samplesSelectionModel);

        //set column widths
        analyticalRunSetupDialog.getSamplesTable().getColumnModel().getColumn(SampleSimpleTableFormat.SAMPLE_ID).setPreferredWidth(10);
        analyticalRunSetupDialog.getSamplesTable().getColumnModel().getColumn(SampleSimpleTableFormat.NAME).setPreferredWidth(100);
        analyticalRunSetupDialog.getSamplesTable().getColumnModel().getColumn(SampleSimpleTableFormat.NUMBER_OF_RUNS).setPreferredWidth(20);

        //set sorting
        TableComparatorChooser samplesTableSorter = TableComparatorChooser.install(
                analyticalRunSetupDialog.getSamplesTable(), sortedSamples, TableComparatorChooser.SINGLE_COLUMN);

        //add action listeners
        projectsSelectionModel.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent lse) {
                if (!lse.getValueIsAdjusting()) {
                    Project selectedProject = getSelectedProject();
                    if (selectedProject != null) {
                        //fill experiments table                        
                        GlazedLists.replaceAll(experiments, selectedProject.getExperiments(), false);
                    } else {
                        GlazedLists.replaceAll(experiments, new ArrayList<Experiment>(), false);
                    }
                }
            }
        });

        experimentsSelectionModel.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent lse) {
                if (!lse.getValueIsAdjusting()) {
                    Experiment selectedExperiment = getSelectedExperiment();
                    if (selectedExperiment != null) {
                        //fill samples table                        
                        GlazedLists.replaceAll(samples, selectedExperiment.getSamples(), false);
                    } else {
                        GlazedLists.replaceAll(samples, new ArrayList<Sample>(), false);

                    }
                }
            }
        });

        analyticalRunSetupDialog.getBackButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getCardLayout().previous(analyticalRunSetupDialog.getTopPanel());
                onCardSwitch(GuiUtils.getCurrentCardName(analyticalRunSetupDialog.getTopPanel()));
            }
        });

        //add action listeners
        analyticalRunSetupDialog.getProceedButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String currentCardName = GuiUtils.getCurrentCardName(analyticalRunSetupDialog.getTopPanel());
                switch (currentCardName) {
                    case SAMPLE_SELECTION_CARD:
                        //check if a sample is selected
                        Sample selectedSample = getSelectedSample();
                        if (selectedSample != null) {
                            getCardLayout().next(analyticalRunSetupDialog.getTopPanel());
                            onCardSwitch(currentCardName);
                        } else {
                            MessageEvent messageEvent = new MessageEvent("sample selection", "Please select a sample.", JOptionPane.INFORMATION_MESSAGE);
                            eventBus.post(messageEvent);
                        }
                        break;
                    case DATA_TYPE_SELECTION_CARD:
                        StorageType selectedStorageType = getSelectedStorageType();
                        switch (selectedStorageType) {
                            case PEPTIDESHAKER:

                                break;
                            case MAX_QUANT:

                                break;                                                
                        }
                        onCardSwitch(currentCardName);
                        break;
                }
            }
        });

        analyticalRunSetupDialog.getFinishButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //do something
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
        //show first card
        getCardLayout().first(analyticalRunSetupDialog.getTopPanel());
        onCardSwitch(GuiUtils.getCurrentCardName(analyticalRunSetupDialog.getTopPanel()));

        //show info
        updateInfo("Choose a sample to add the run to.");

        GuiUtils.centerDialogOnComponent(colimsController.getColimsFrame(), analyticalRunSetupDialog);

        analyticalRunSetupDialog.setVisible(true);
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
    private void onCardSwitch(String cardName) {
        switch (cardName) {
            case SAMPLE_SELECTION_CARD:
                //disable back button
                analyticalRunSetupDialog.getBackButton().setEnabled(false);
                //enable proceed button
                analyticalRunSetupDialog.getProceedButton().setEnabled(true);
                //disable finish button
                analyticalRunSetupDialog.getFinishButton().setEnabled(false);
                //show info
                updateInfo("Click on proceed to import data files.");
                break;
            case DATA_TYPE_SELECTION_CARD:
                //enable back button
                analyticalRunSetupDialog.getBackButton().setEnabled(true);
                //disable proceed button
                analyticalRunSetupDialog.getProceedButton().setEnabled(false);
                //enable finish button
                analyticalRunSetupDialog.getFinishButton().setEnabled(true);
                //show info
                updateInfo("Click on \"finish\" to store this project.");
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
     * Get the selected project from the project overview table.
     *
     * @return the selected project, null if no project is selected
     */
    private Project getSelectedProject() {
        Project selectedProject = null;

        EventList<Project> selectedProjects = projectsSelectionModel.getSelected();
        if (!selectedProjects.isEmpty()) {
            selectedProject = selectedProjects.get(0);
        }

        return selectedProject;
    }

    /**
     * Get the selected experiment from the experiment table.
     *
     * @return the selected experiment, null if no experiment is selected
     */
    private Experiment getSelectedExperiment() {
        Experiment selectedExperiment = null;

        EventList<Experiment> selectedExperiments = experimentsSelectionModel.getSelected();
        if (!selectedExperiments.isEmpty()) {
            selectedExperiment = selectedExperiments.get(0);
        }

        return selectedExperiment;
    }

    /**
     * Get the selected sample from the experiment table.
     *
     * @return the selected sample, null if no sample is selected
     */
    private Sample getSelectedSample() {
        Sample selectedSample = null;

        EventList<Sample> selectedSamples = samplesSelectionModel.getSelected();
        if (!selectedSamples.isEmpty()) {
            selectedSample = selectedSamples.get(0);
        }

        return selectedSample;
    }

    /**
     * Get the selected storage type.
     *
     * @return the selected StorageType
     */
    private StorageType getSelectedStorageType() {
        StorageType storageType = null;

        //iterate over the radio buttons in the group
        for (Enumeration<AbstractButton> buttons = analyticalRunSetupDialog.getDataTypeButtonGroup().getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();

            if (button.isSelected()) {
                storageType = StorageType.getByUserFriendlyName(button.getText());
            }
        }

        return storageType;
    }

}
