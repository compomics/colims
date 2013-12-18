package com.compomics.colims.client.controller;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.AdvancedTableModel;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.GlazedListsSwing;
import com.compomics.colims.client.model.tableformat.AnalyticalRunSimpleTableFormat;
import com.compomics.colims.client.model.tableformat.ExperimentSimpleTableFormat;
import com.compomics.colims.client.model.tableformat.ProjectSimpleTableFormat;
import com.compomics.colims.client.model.tableformat.SampleSimpleTableFormat;
import com.compomics.colims.client.model.tableformat.SpectrumTableFormat;
import com.compomics.colims.client.view.ProjectOverviewPanel;
import com.compomics.colims.core.service.AnalyticalRunService;
import com.compomics.colims.core.service.ExperimentService;
import com.compomics.colims.core.service.ProjectService;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.Experiment;
import com.compomics.colims.model.Project;
import com.compomics.colims.model.Sample;
import com.compomics.colims.model.Spectrum;
import com.compomics.colims.model.comparator.IdComparator;
import com.google.common.eventbus.EventBus;
import java.util.ArrayList;
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
@Component("projectOverviewController")
public class ProjectOverviewController implements Controllable {

    private static final Logger LOGGER = Logger.getLogger(ProjectOverviewController.class);
    //model
    private EventList<Project> projects = new BasicEventList<>();
    private AdvancedTableModel<Project> projectsTableModel;
    private DefaultEventSelectionModel<Project> projectsSelectionModel;
    private EventList<Experiment> experiments = new BasicEventList<>();
    private AdvancedTableModel<Experiment> experimentsTableModel;
    private DefaultEventSelectionModel<Experiment> experimentsSelectionModel;
    private EventList<Sample> samples = new BasicEventList<>();
    private AdvancedTableModel<Sample> samplesTableModel;
    private DefaultEventSelectionModel<Sample> samplesSelectionModel;
    private EventList<AnalyticalRun> analyticalRuns = new BasicEventList<>();
    private AdvancedTableModel<AnalyticalRun> analyticalRunsTableModel;
    private DefaultEventSelectionModel<AnalyticalRun> analyticalRunsSelectionModel;
    private EventList<Spectrum> spectra = new BasicEventList<>();
    private AdvancedTableModel<Spectrum> spectraTableModel;
    private DefaultEventSelectionModel<Spectrum> spectraSelectionModel;
    //view
    private ProjectOverviewPanel projectOverviewPanel;
    //parent controller
    @Autowired
    private ColimsController colimsController;
    //services
    @Autowired
    private ProjectService projectService;
    @Autowired
    private AnalyticalRunService analyticalRunService;
    @Autowired
    private EventBus eventBus;

    public ProjectOverviewPanel getProjectOverviewPanel() {
        return projectOverviewPanel;
    }

    public void init() {
        //register to event bus
        eventBus.register(this);

        //init view
        projectOverviewPanel = new ProjectOverviewPanel(colimsController.getColimsFrame());

        //init projects table
        projects.addAll(projectService.findAllWithEagerFetching());
        SortedList<Project> sortedProjects = new SortedList<>(projects, new IdComparator());
        projectsTableModel = GlazedListsSwing.eventTableModel(sortedProjects, new ProjectSimpleTableFormat());
        projectOverviewPanel.getProjectsTable().setModel(projectsTableModel);
        projectsSelectionModel = new DefaultEventSelectionModel<>(sortedProjects);
        projectsSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        projectOverviewPanel.getProjectsTable().setSelectionModel(projectsSelectionModel);

        //set column widths
        projectOverviewPanel.getProjectsTable().getColumnModel().getColumn(ProjectSimpleTableFormat.PROJECT_ID).setPreferredWidth(10);
        projectOverviewPanel.getProjectsTable().getColumnModel().getColumn(ProjectSimpleTableFormat.TITLE).setPreferredWidth(100);
        projectOverviewPanel.getProjectsTable().getColumnModel().getColumn(ProjectSimpleTableFormat.LABEL).setPreferredWidth(50);
        projectOverviewPanel.getProjectsTable().getColumnModel().getColumn(ProjectSimpleTableFormat.NUMBER_OF_EXPERIMENTS).setPreferredWidth(20);

        //init projects experiment table
        SortedList<Experiment> sortedExperiments = new SortedList<>(experiments, new IdComparator());
        experimentsTableModel = GlazedListsSwing.eventTableModel(sortedExperiments, new ExperimentSimpleTableFormat());
        projectOverviewPanel.getExperimentsTable().setModel(experimentsTableModel);
        experimentsSelectionModel = new DefaultEventSelectionModel<>(sortedExperiments);
        experimentsSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        projectOverviewPanel.getExperimentsTable().setSelectionModel(experimentsSelectionModel);

        //set column widths
        projectOverviewPanel.getExperimentsTable().getColumnModel().getColumn(ExperimentSimpleTableFormat.EXPERIMENT_ID).setPreferredWidth(10);
        projectOverviewPanel.getExperimentsTable().getColumnModel().getColumn(ExperimentSimpleTableFormat.TITLE).setPreferredWidth(100);
        projectOverviewPanel.getExperimentsTable().getColumnModel().getColumn(ExperimentSimpleTableFormat.NUMBER).setPreferredWidth(20);
        projectOverviewPanel.getExperimentsTable().getColumnModel().getColumn(ExperimentSimpleTableFormat.NUMBER_OF_SAMPLES).setPreferredWidth(20);

        //init experiment samples table
        SortedList<Sample> sortedSamples = new SortedList<>(samples, new IdComparator());
        samplesTableModel = GlazedListsSwing.eventTableModel(sortedSamples, new SampleSimpleTableFormat());
        projectOverviewPanel.getSamplesTable().setModel(samplesTableModel);
        samplesSelectionModel = new DefaultEventSelectionModel<>(sortedSamples);
        samplesSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        projectOverviewPanel.getSamplesTable().setSelectionModel(samplesSelectionModel);

        //set column widths
        projectOverviewPanel.getSamplesTable().getColumnModel().getColumn(SampleSimpleTableFormat.SAMPLE_ID).setPreferredWidth(10);
        projectOverviewPanel.getSamplesTable().getColumnModel().getColumn(SampleSimpleTableFormat.NAME).setPreferredWidth(100);
        projectOverviewPanel.getSamplesTable().getColumnModel().getColumn(SampleSimpleTableFormat.NUMBER_OF_RUNS).setPreferredWidth(20);

        //init sample analyticalruns table
        SortedList<AnalyticalRun> sortedAnalyticalRuns = new SortedList<>(analyticalRuns, new IdComparator());
        analyticalRunsTableModel = GlazedListsSwing.eventTableModel(sortedAnalyticalRuns, new AnalyticalRunSimpleTableFormat());
        projectOverviewPanel.getAnalyticalRunsTable().setModel(analyticalRunsTableModel);
        analyticalRunsSelectionModel = new DefaultEventSelectionModel<>(sortedAnalyticalRuns);
        analyticalRunsSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        projectOverviewPanel.getAnalyticalRunsTable().setSelectionModel(analyticalRunsSelectionModel);

        //set column widths
        projectOverviewPanel.getAnalyticalRunsTable().getColumnModel().getColumn(AnalyticalRunSimpleTableFormat.RUN_ID).setPreferredWidth(10);
        projectOverviewPanel.getAnalyticalRunsTable().getColumnModel().getColumn(AnalyticalRunSimpleTableFormat.NAME).setPreferredWidth(50);
        projectOverviewPanel.getAnalyticalRunsTable().getColumnModel().getColumn(AnalyticalRunSimpleTableFormat.START_DATE).setPreferredWidth(50);        
        projectOverviewPanel.getAnalyticalRunsTable().getColumnModel().getColumn(AnalyticalRunSimpleTableFormat.NUMBER_OF_SPECTRA).setPreferredWidth(20);

        //init sample analyticalruns table
        SortedList<Spectrum> sortedSpectra = new SortedList<>(spectra, new IdComparator());
        spectraTableModel = GlazedListsSwing.eventTableModel(sortedSpectra, new SpectrumTableFormat());
        projectOverviewPanel.getPsmTable().setModel(spectraTableModel);
        spectraSelectionModel = new DefaultEventSelectionModel<>(sortedSpectra);
        spectraSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        projectOverviewPanel.getPsmTable().setSelectionModel(spectraSelectionModel);

        //set column widths
        projectOverviewPanel.getAnalyticalRunsTable().getColumnModel().getColumn(AnalyticalRunSimpleTableFormat.RUN_ID).setPreferredWidth(10);
        projectOverviewPanel.getAnalyticalRunsTable().getColumnModel().getColumn(AnalyticalRunSimpleTableFormat.NAME).setPreferredWidth(50);
        projectOverviewPanel.getAnalyticalRunsTable().getColumnModel().getColumn(AnalyticalRunSimpleTableFormat.START_DATE).setPreferredWidth(50);        
        projectOverviewPanel.getAnalyticalRunsTable().getColumnModel().getColumn(AnalyticalRunSimpleTableFormat.NUMBER_OF_SPECTRA).setPreferredWidth(20);
        
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

        samplesSelectionModel.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent lse) {
                if (!lse.getValueIsAdjusting()) {
                    Sample selectedSample = getSelectedSample();
                    if (selectedSample != null) {
                        //fill samples table                        
                        GlazedLists.replaceAll(analyticalRuns, selectedSample.getAnalyticalRuns(), false);
                    } else {
                        GlazedLists.replaceAll(analyticalRuns, new ArrayList<AnalyticalRun>(), false);
                    }
                }
            }
        });

        analyticalRunsSelectionModel.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent lse) {
                if (!lse.getValueIsAdjusting()) {
                    AnalyticalRun selectedAnalyticalRun = getSelectedAnalyticalRun();
                    if (selectedAnalyticalRun != null) {
                        analyticalRunService.fetchSpectra(selectedAnalyticalRun);
                        //fill samples table                        
                        GlazedLists.replaceAll(spectra, selectedAnalyticalRun.getSpectrums(), false);
                    } else {
                        GlazedLists.replaceAll(spectra, new ArrayList<Spectrum>(), false);
                    }
                }
            }
        });
    }

    @Override
    public void showView() {
        //do nothing
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
     * Get the selected sample from the sample table.
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
     * Get the selected analytical run from the analytical run table.
     *
     * @return the selected analytical run, null if no analytical is selected
     */
    private AnalyticalRun getSelectedAnalyticalRun() {
        AnalyticalRun selectedAnalyticalRun = null;

        EventList<AnalyticalRun> selectedAnalyticalRuns = analyticalRunsSelectionModel.getSelected();
        if (!selectedAnalyticalRuns.isEmpty()) {
            selectedAnalyticalRun = selectedAnalyticalRuns.get(0);
        }

        return selectedAnalyticalRun;
    }
}
