package com.compomics.colims.client.controller;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.AdvancedTableModel;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.GlazedListsSwing;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import com.compomics.colims.client.event.AnalyticalRunChangeEvent;
import com.compomics.colims.client.event.EntityChangeEvent;
import com.compomics.colims.client.event.ExperimentChangeEvent;
import com.compomics.colims.client.event.SampleChangeEvent;
import com.compomics.colims.client.model.PsmTableModel;
import com.compomics.colims.client.model.tableformat.AnalyticalRunSimpleTableFormat;
import com.compomics.colims.client.model.tableformat.ExperimentSimpleTableFormat;
import com.compomics.colims.client.model.tableformat.ProjectSimpleTableFormat;
import com.compomics.colims.client.model.tableformat.SampleSimpleTableFormat;
import com.compomics.colims.client.model.tableformat.PsmTableFormat;
import com.compomics.colims.client.view.ProjectOverviewPanel;
import com.compomics.colims.core.io.colims_to_utilities.ColimsSpectrumMapper;
import com.compomics.colims.core.io.colims_to_utilities.PsmMapper;
import com.compomics.colims.core.service.SpectrumService;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.Experiment;
import com.compomics.colims.model.Project;
import com.compomics.colims.model.Sample;
import com.compomics.colims.model.Spectrum;
import com.compomics.colims.model.comparator.IdComparator;
import com.compomics.colims.repository.SpectrumRepository;
import com.compomics.util.experiment.identification.PeptideAssumption;
import com.compomics.util.experiment.identification.SpectrumAnnotator;
import com.compomics.util.experiment.identification.matches.IonMatch;
import com.compomics.util.experiment.identification.matches.SpectrumMatch;
import com.compomics.util.experiment.identification.spectrum_annotators.PeptideSpectrumAnnotator;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.Peak;
import com.compomics.util.experiment.massspectrometry.Precursor;
import com.compomics.util.gui.spectrum.IntensityHistogram;
import com.compomics.util.gui.spectrum.MassErrorPlot;
import com.compomics.util.gui.spectrum.SequenceFragmentationPanel;
import com.compomics.util.gui.spectrum.SpectrumPanel;
import com.compomics.util.preferences.AnnotationPreferences;
import com.compomics.util.preferences.SequenceMatchingPreferences;
import com.compomics.util.preferences.SpecificAnnotationPreferences;
import com.compomics.util.preferences.UtilitiesUserPreferences;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import no.uib.jsparklines.renderers.JSparklinesIntervalChartTableCellRenderer;
import org.apache.log4j.Logger;
import org.jfree.chart.plot.PlotOrientation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The project overview view controller.
 *
 * @author Niels Hulstaert
 */
@Component("projectOverviewController")
public class ProjectOverviewController implements Controllable {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(ProjectOverviewController.class);

    private static final double INTENSITY_LEVEL = 0.75;
    //model
    private AdvancedTableModel<Project> projectsTableModel;
    private DefaultEventSelectionModel<Project> projectsSelectionModel;
    private final EventList<Experiment> experiments = new BasicEventList<>();
    private AdvancedTableModel<Experiment> experimentsTableModel;
    private DefaultEventSelectionModel<Experiment> experimentsSelectionModel;
    private final EventList<Sample> samples = new BasicEventList<>();
    private AdvancedTableModel<Sample> samplesTableModel;
    private DefaultEventSelectionModel<Sample> samplesSelectionModel;
    private final EventList<AnalyticalRun> analyticalRuns = new BasicEventList<>();
    private AdvancedTableModel<AnalyticalRun> analyticalRunsTableModel;
    private DefaultEventSelectionModel<AnalyticalRun> analyticalRunsSelectionModel;
    private final EventList<Spectrum> spectra = new BasicEventList<>();
    private PsmTableModel psmsTableModel;
    private DefaultEventSelectionModel<Spectrum> psmsSelectionModel;
    /**
     * The spectrum annotator.
     */
    private final PeptideSpectrumAnnotator spectrumAnnotator = new PeptideSpectrumAnnotator();
    /**
     * The utilities user preferences.
     */
    private final UtilitiesUserPreferences utilitiesUserPreferences = new UtilitiesUserPreferences();
    /**
     * The label with for the numbers in the jsparklines columns.
     */
    private final int labelWidth = 50;

    //view
    private ProjectOverviewPanel projectOverviewPanel;
    //parent controller
    @Autowired
    private MainController mainController;
    @Autowired
    private ProjectManagementController projectManagementController;
    //services
    @Autowired
    private SpectrumService spectrumService;
    @Autowired
    private ColimsSpectrumMapper colimsSpectrumMapper;
    @Autowired
    private PsmMapper psmMapper;
    @Autowired
    private EventBus eventBus;
    @Autowired
    private SpectrumRepository spectrumRepository;

    /**
     * Get the view of this controller.
     *
     * @return the ProjectOverviewPanel
     */
    public ProjectOverviewPanel getProjectOverviewPanel() {
        return projectOverviewPanel;
    }

    @Override
    public void init() {
        //register to event bus
        eventBus.register(this);

        //init view
        projectOverviewPanel = new ProjectOverviewPanel(mainController.getMainFrame(), this, utilitiesUserPreferences);

        //init projects table
        SortedList<Project> sortedProjects = new SortedList<>(mainController.getProjects(), new IdComparator());
        projectsTableModel = GlazedListsSwing.eventTableModel(sortedProjects, new ProjectSimpleTableFormat());
        projectOverviewPanel.getProjectsTable().setModel(projectsTableModel);
        projectsSelectionModel = new DefaultEventSelectionModel<>(sortedProjects);
        projectsSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        projectOverviewPanel.getProjectsTable().setSelectionModel(projectsSelectionModel);

        //set column widths
        projectOverviewPanel.getProjectsTable().getColumnModel().getColumn(ProjectSimpleTableFormat.PROJECT_ID).setPreferredWidth(35);
        projectOverviewPanel.getProjectsTable().getColumnModel().getColumn(ProjectSimpleTableFormat.PROJECT_ID).setMaxWidth(35);
        projectOverviewPanel.getProjectsTable().getColumnModel().getColumn(ProjectSimpleTableFormat.PROJECT_ID).setMinWidth(35);
        projectOverviewPanel.getProjectsTable().getColumnModel().getColumn(ProjectSimpleTableFormat.TITLE).setPreferredWidth(300);
        projectOverviewPanel.getProjectsTable().getColumnModel().getColumn(ProjectSimpleTableFormat.LABEL).setPreferredWidth(100);
        projectOverviewPanel.getProjectsTable().getColumnModel().getColumn(ProjectSimpleTableFormat.NUMBER_OF_EXPERIMENTS).setPreferredWidth(65);
        projectOverviewPanel.getProjectsTable().getColumnModel().getColumn(ProjectSimpleTableFormat.NUMBER_OF_EXPERIMENTS).setMaxWidth(65);
        projectOverviewPanel.getProjectsTable().getColumnModel().getColumn(ProjectSimpleTableFormat.NUMBER_OF_EXPERIMENTS).setMinWidth(65);

        //set sorting
        @SuppressWarnings("UnusedAssignment") TableComparatorChooser projectsTableSorter = TableComparatorChooser.install(
            projectOverviewPanel.getProjectsTable(), sortedProjects, TableComparatorChooser.SINGLE_COLUMN);

        //init projects experiment table
        SortedList<Experiment> sortedExperiments = new SortedList<>(experiments, new IdComparator());
        experimentsTableModel = GlazedListsSwing.eventTableModel(sortedExperiments, new ExperimentSimpleTableFormat());
        projectOverviewPanel.getExperimentsTable().setModel(experimentsTableModel);
        experimentsSelectionModel = new DefaultEventSelectionModel<>(sortedExperiments);
        experimentsSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        projectOverviewPanel.getExperimentsTable().setSelectionModel(experimentsSelectionModel);

        //set column widths
        projectOverviewPanel.getProjectsTable().getColumnModel().getColumn(ExperimentSimpleTableFormat.EXPERIMENT_ID).setPreferredWidth(35);
        projectOverviewPanel.getProjectsTable().getColumnModel().getColumn(ExperimentSimpleTableFormat.EXPERIMENT_ID).setMaxWidth(35);
        projectOverviewPanel.getProjectsTable().getColumnModel().getColumn(ExperimentSimpleTableFormat.EXPERIMENT_ID).setMinWidth(35);
        projectOverviewPanel.getExperimentsTable().getColumnModel().getColumn(ExperimentSimpleTableFormat.TITLE).setPreferredWidth(300);
        projectOverviewPanel.getExperimentsTable().getColumnModel().getColumn(ExperimentSimpleTableFormat.NUMBER).setPreferredWidth(100);
        projectOverviewPanel.getExperimentsTable().getColumnModel().getColumn(ExperimentSimpleTableFormat.NUMBER_OF_SAMPLES).setPreferredWidth(65);
        projectOverviewPanel.getExperimentsTable().getColumnModel().getColumn(ExperimentSimpleTableFormat.NUMBER_OF_SAMPLES).setMaxWidth(65);
        projectOverviewPanel.getExperimentsTable().getColumnModel().getColumn(ExperimentSimpleTableFormat.NUMBER_OF_SAMPLES).setMinWidth(65);

        //set sorting
        TableComparatorChooser experimentsTableSorter = TableComparatorChooser.install(
            projectOverviewPanel.getExperimentsTable(), sortedExperiments, TableComparatorChooser.SINGLE_COLUMN);

        //init experiment samples table
        SortedList<Sample> sortedSamples = new SortedList<>(samples, new IdComparator());
        samplesTableModel = GlazedListsSwing.eventTableModel(sortedSamples, new SampleSimpleTableFormat());
        projectOverviewPanel.getSamplesTable().setModel(samplesTableModel);
        samplesSelectionModel = new DefaultEventSelectionModel<>(sortedSamples);
        samplesSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        projectOverviewPanel.getSamplesTable().setSelectionModel(samplesSelectionModel);

        //set column widths
        projectOverviewPanel.getProjectsTable().getColumnModel().getColumn(SampleSimpleTableFormat.SAMPLE_ID).setPreferredWidth(35);
        projectOverviewPanel.getProjectsTable().getColumnModel().getColumn(SampleSimpleTableFormat.SAMPLE_ID).setMaxWidth(35);
        projectOverviewPanel.getProjectsTable().getColumnModel().getColumn(SampleSimpleTableFormat.SAMPLE_ID).setMinWidth(35);
        projectOverviewPanel.getSamplesTable().getColumnModel().getColumn(SampleSimpleTableFormat.NAME).setPreferredWidth(300);
        projectOverviewPanel.getSamplesTable().getColumnModel().getColumn(SampleSimpleTableFormat.NUMBER_OF_RUNS).setPreferredWidth(65);
        projectOverviewPanel.getSamplesTable().getColumnModel().getColumn(SampleSimpleTableFormat.NUMBER_OF_RUNS).setMaxWidth(65);
        projectOverviewPanel.getSamplesTable().getColumnModel().getColumn(SampleSimpleTableFormat.NUMBER_OF_RUNS).setMinWidth(65);

        //set sorting
        TableComparatorChooser samplesTableSorter = TableComparatorChooser.install(
            projectOverviewPanel.getSamplesTable(), sortedSamples, TableComparatorChooser.SINGLE_COLUMN);

        //init sample analyticalruns table
        SortedList<AnalyticalRun> sortedAnalyticalRuns = new SortedList<>(analyticalRuns, new IdComparator());
        analyticalRunsTableModel = GlazedListsSwing.eventTableModel(sortedAnalyticalRuns, new AnalyticalRunSimpleTableFormat());
        projectOverviewPanel.getAnalyticalRunsTable().setModel(analyticalRunsTableModel);
        analyticalRunsSelectionModel = new DefaultEventSelectionModel<>(sortedAnalyticalRuns);
        analyticalRunsSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        projectOverviewPanel.getAnalyticalRunsTable().setSelectionModel(analyticalRunsSelectionModel);

        //set column widths
        projectOverviewPanel.getAnalyticalRunsTable().getColumnModel().getColumn(AnalyticalRunSimpleTableFormat.RUN_ID).setPreferredWidth(35);
        projectOverviewPanel.getAnalyticalRunsTable().getColumnModel().getColumn(AnalyticalRunSimpleTableFormat.RUN_ID).setMaxWidth(35);
        projectOverviewPanel.getAnalyticalRunsTable().getColumnModel().getColumn(AnalyticalRunSimpleTableFormat.RUN_ID).setMinWidth(35);
        projectOverviewPanel.getAnalyticalRunsTable().getColumnModel().getColumn(AnalyticalRunSimpleTableFormat.NAME).setPreferredWidth(100);
        projectOverviewPanel.getAnalyticalRunsTable().getColumnModel().getColumn(AnalyticalRunSimpleTableFormat.START_DATE).setPreferredWidth(100);
        projectOverviewPanel.getAnalyticalRunsTable().getColumnModel().getColumn(AnalyticalRunSimpleTableFormat.NUMBER_OF_SPECTRA).setPreferredWidth(65);
        projectOverviewPanel.getAnalyticalRunsTable().getColumnModel().getColumn(AnalyticalRunSimpleTableFormat.NUMBER_OF_SPECTRA).setMaxWidth(65);
        projectOverviewPanel.getAnalyticalRunsTable().getColumnModel().getColumn(AnalyticalRunSimpleTableFormat.NUMBER_OF_SPECTRA).setMinWidth(65);

        //set sorting
        TableComparatorChooser analyticalRunsTableSorter = TableComparatorChooser.install(
            projectOverviewPanel.getAnalyticalRunsTable(), sortedAnalyticalRuns, TableComparatorChooser.SINGLE_COLUMN);

        //init sample spectra table
        SortedList<Spectrum> sortedPsms = new SortedList<>(spectra, null);
        psmsTableModel = new PsmTableModel(sortedPsms, new PsmTableFormat(), spectrumRepository);
        projectOverviewPanel.getPsmTable().setModel(psmsTableModel);
        psmsSelectionModel = new DefaultEventSelectionModel<>(sortedPsms);
        psmsSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        projectOverviewPanel.getPsmTable().setSelectionModel(psmsSelectionModel);

        //set column widths
        projectOverviewPanel.getPsmTable().getColumnModel().getColumn(PsmTableFormat.SPECTRUM_ID).setPreferredWidth(35);
        projectOverviewPanel.getPsmTable().getColumnModel().getColumn(PsmTableFormat.SPECTRUM_ID).setMaxWidth(35);
        projectOverviewPanel.getPsmTable().getColumnModel().getColumn(PsmTableFormat.SPECTRUM_ID).setMinWidth(35);
        projectOverviewPanel.getPsmTable().getColumnModel().getColumn(PsmTableFormat.PRECURSOR_CHARGE).setPreferredWidth(10);
        projectOverviewPanel.getPsmTable().getColumnModel().getColumn(PsmTableFormat.PRECURSOR_MZRATIO).setPreferredWidth(50);
        projectOverviewPanel.getPsmTable().getColumnModel().getColumn(PsmTableFormat.PRECURSOR_INTENSITY).setPreferredWidth(50);
        projectOverviewPanel.getPsmTable().getColumnModel().getColumn(PsmTableFormat.RETENTION_TIME).setPreferredWidth(50);
        projectOverviewPanel.getPsmTable().getColumnModel().getColumn(PsmTableFormat.PEPTIDE_SEQUENCE).setPreferredWidth(300);
        projectOverviewPanel.getPsmTable().getColumnModel().getColumn(PsmTableFormat.PSM_CONFIDENCE).setPreferredWidth(50);
        projectOverviewPanel.getPsmTable().getColumnModel().getColumn(PsmTableFormat.PROTEIN_ACCESSIONS).setPreferredWidth(300);

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
                        //fill runs table
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
                mainController.getMainFrame().setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));

                AnalyticalRun selectedAnalyticalRun = getSelectedAnalyticalRun();

                if (!lse.getValueIsAdjusting() && selectedAnalyticalRun != null) {
                    psmsTableModel.reset(selectedAnalyticalRun);
                    updatePsmTable();
                }

                mainController.getMainFrame().setCursor(new java.awt.Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        psmsSelectionModel.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent lse) {
                if (!lse.getValueIsAdjusting()) {
                    //update the spectrum panel
                    updateSpectrum();
                }
            }
        });

        projectOverviewPanel.getPsmTable().getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                psmsTableModel.updateSort(projectOverviewPanel.getPsmTable().columnAtPoint(e.getPoint()));
                psmsTableModel.setPage(0);

                updatePsmTable();
            }
        });

        projectOverviewPanel.getNextPageSpectra().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!psmsTableModel.isMaxPage()) {
                    psmsTableModel.setPage(psmsTableModel.getPage() + 1);

                    projectOverviewPanel.getPrevPageSpectra().setEnabled(true);
                    projectOverviewPanel.getFirstPageSpectra().setEnabled(true);

                    if (psmsTableModel.isMaxPage()) {
                        projectOverviewPanel.getNextPageSpectra().setEnabled(false);
                        projectOverviewPanel.getLastPageSpectra().setEnabled(false);
                    }

                    updatePsmTable();
                }
            }
        });

        projectOverviewPanel.getPrevPageSpectra().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (psmsTableModel.getPage() != 0) {
                    psmsTableModel.setPage(psmsTableModel.getPage() - 1);

                    projectOverviewPanel.getNextPageSpectra().setEnabled(true);
                    projectOverviewPanel.getLastPageSpectra().setEnabled(true);

                    if (psmsTableModel.getPage() == 0) {
                        projectOverviewPanel.getPrevPageSpectra().setEnabled(false);
                        projectOverviewPanel.getFirstPageSpectra().setEnabled(false);
                    }

                    updatePsmTable();
                }
            }
        });

        projectOverviewPanel.getFirstPageSpectra().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (psmsTableModel.getPage() != 0) {
                    psmsTableModel.setPage(0);

                    projectOverviewPanel.getNextPageSpectra().setEnabled(true);
                    projectOverviewPanel.getPrevPageSpectra().setEnabled(false);
                    projectOverviewPanel.getFirstPageSpectra().setEnabled(false);
                    projectOverviewPanel.getLastPageSpectra().setEnabled(true);

                    updatePsmTable();
                }
            }
        });

        projectOverviewPanel.getLastPageSpectra().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!psmsTableModel.isMaxPage()) {
                    psmsTableModel.setPage(psmsTableModel.getMaxPage());

                    projectOverviewPanel.getNextPageSpectra().setEnabled(false);
                    projectOverviewPanel.getPrevPageSpectra().setEnabled(true);
                    projectOverviewPanel.getFirstPageSpectra().setEnabled(true);
                    projectOverviewPanel.getLastPageSpectra().setEnabled(false);

                    updatePsmTable();
                }
            }
        });

        projectOverviewPanel.getFilterSpectra().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);

                String filterText = projectOverviewPanel.getFilterSpectra().getText();

                if (filterText.matches("^[a-zA-Z0-9]*$")) {
                    psmsTableModel.setFilter(projectOverviewPanel.getFilterSpectra().getText());

                    updatePsmTable();
                }
            }
        });
    }

    @Override
    public void showView() {
        //do nothing
    }

    /**
     * Update the spectrum table with new rows or no rows
     */
    private void updatePsmTable() {
        AnalyticalRun selectedAnalyticalRun = getSelectedAnalyticalRun();

        if (selectedAnalyticalRun != null) {
            setPsmTableCellRenderers();
            GlazedLists.replaceAll(spectra, psmsTableModel.getRows(selectedAnalyticalRun), false);
            projectOverviewPanel.getPageLabelSpectra().setText(psmsTableModel.getPageIndicator());
        } else {
            GlazedLists.replaceAll(spectra, new ArrayList<Spectrum>(), false);
            projectOverviewPanel.getPageLabelSpectra().setText("");
        }
    }

    /**
     * Listen to a ExperimentChangeEvent and update the experiments table if necessary.
     *
     * @param experimentChangeEvent the experimentChangeEvent
     */
    @Subscribe
    public void onExperimentChangeEvent(ExperimentChangeEvent experimentChangeEvent) {
        Experiment experiment = experimentChangeEvent.getExperiment();

        //check if the experiment belongs to the selected project
        if (experiment.getProject().equals(getSelectedProject())) {
            if (experimentChangeEvent.getType().equals(EntityChangeEvent.Type.CREATED)) {
                experiments.add(experiment);
            } else if (experimentChangeEvent.getType().equals(EntityChangeEvent.Type.DELETED)) {
                experiments.remove(experiment);
            } else if (experimentChangeEvent.getType().equals(EntityChangeEvent.Type.UPDATED)) {
                updateExperiment(experiment);
            }
        }
    }

    /**
     * Listen to a SampleChangeEvent and update the samples table if necessary.
     *
     * @param sampleChangeEvent the sampleChangeEvent
     */
    @Subscribe
    public void onSampleChangeEvent(SampleChangeEvent sampleChangeEvent) {
        Sample sample = sampleChangeEvent.getSample();

        if (sample.getExperiment().equals(getSelectedExperiment())) {
            if (sampleChangeEvent.getType().equals(EntityChangeEvent.Type.CREATED)) {
                samples.add(sample);
            } else if (sampleChangeEvent.getType().equals(EntityChangeEvent.Type.DELETED)) {
                samples.remove(sample);
            } else if (sampleChangeEvent.getType().equals(EntityChangeEvent.Type.UPDATED)) {
                updateSample(sample);
            }
        }
    }

    /**
     * Listen to a AnalyticalRunChangeEvent and update the analytical runs table if necessary.
     *
     * @param analyticalRunChangeEvent the AnalyticalRunChangeEvent
     */
    @Subscribe
    public void onAnalyticalRunChangeEvent(AnalyticalRunChangeEvent analyticalRunChangeEvent) {
        AnalyticalRun analyticalRun = analyticalRunChangeEvent.getAnalyticalRun();

        if (analyticalRun.getSample().equals(getSelectedSample())) {
            if (analyticalRunChangeEvent.getType().equals(EntityChangeEvent.Type.CREATED)) {
                analyticalRuns.add(analyticalRun);
            } else if (analyticalRunChangeEvent.getType().equals(EntityChangeEvent.Type.DELETED)) {
                analyticalRuns.remove(analyticalRun);
            } else if (analyticalRunChangeEvent.getType().equals(EntityChangeEvent.Type.UPDATED)) {
                updateAnalyticalRun(analyticalRun);
            }
        }
    }

    /**
     * Update the spectrum to the currently selected PSM.
     */
    public void updateSpectrum() {
        Spectrum selectedSpectrum = getSelectedSpectrum();

        if (getSelectedSpectrum() != null) {
            mainController.getMainFrame().setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));

            AnnotationPreferences annotationPreferences = projectOverviewPanel.getAnnotationPreferences();

            try {
                MSnSpectrum spectrum = new MSnSpectrum();

                spectrumService.fetchSpectrumFiles(selectedSpectrum);

                //map the colims spectrum to utilities MSnSpectrum
                colimsSpectrumMapper.map(selectedSpectrum, spectrum);

                Collection<Peak> peaks = spectrum.getPeakList();

                if (peaks == null || peaks.isEmpty()) {
                    // do nothing, peaks list not found
                } else {

                    // add the data to the spectrum panel
                    Precursor precursor = spectrum.getPrecursor();

                    SpectrumPanel spectrumPanel = new SpectrumPanel(
                            spectrum.getMzValuesAsArray(), spectrum.getIntensityValuesAsArray(),
                            precursor.getMz(),
                            spectrum.getPrecursor().getPossibleChargesAsString(),
                            //spectrumMatch.getBestAssumption().getIdentificationCharge().toString(), // @TODO: re-add me!
                            "", 40, false, false, false, 2, false);
                    //spectrumPanel.setKnownMassDeltas(peptideShakerGUI.getCurrentMassDeltas()); // @TODO: re-add me!
                    spectrumPanel.setDeltaMassWindow(annotationPreferences.getFragmentIonAccuracy());
                    spectrumPanel.setBorder(null);
                    spectrumPanel.setDataPointAndLineColor(utilitiesUserPreferences.getSpectrumAnnotatedPeakColor(), 0);
                    spectrumPanel.setPeakWaterMarkColor(utilitiesUserPreferences.getSpectrumBackgroundPeakColor());
                    spectrumPanel.setPeakWidth(utilitiesUserPreferences.getSpectrumAnnotatedPeakWidth());
                    spectrumPanel.setBackgroundPeakWidth(utilitiesUserPreferences.getSpectrumBackgroundPeakWidth());

                    //only do this for spectra that have a psm
                    if (!selectedSpectrum.getPeptides().isEmpty()) {
                        SpectrumMatch spectrumMatch = new SpectrumMatch();//peptideShakerGUI.getIdentification().getSpectrumMatch(spectrumKey); // @TODO: get the spectrum match
                        psmMapper.map(selectedSpectrum, spectrumMatch);

                        PeptideAssumption peptideAssumption = spectrumMatch.getBestPeptideAssumption();
                        int identificationCharge = peptideAssumption.getIdentificationCharge().value;

                        // @TODO: re-add the line below
                        //annotationPreferences.setCurrentSettings(peptideAssumption, !currentSpectrumKey.equalsIgnoreCase(spectrumKey), PeptideShaker.MATCHING_TYPE, peptideShakerGUI.getSearchParameters().getFragmentIonAccuracy());

                        SpecificAnnotationPreferences specificAnnotationPreferences = annotationPreferences.getSpecificAnnotationPreferences(
                                spectrum.getSpectrumKey(),
                                peptideAssumption,
                                new SequenceMatchingPreferences());
                        ArrayList<IonMatch> annotations = spectrumAnnotator.getSpectrumAnnotation(
                                annotationPreferences,
                                specificAnnotationPreferences,
                                (MSnSpectrum) spectrum,
                                peptideAssumption.getPeptide());
                        spectrumPanel.setAnnotations(SpectrumAnnotator.getSpectrumAnnotation(annotations));
                        //spectrumPanel.rescale(lowerMzZoomRange, upperMzZoomRange);

//                            if (!currentSpectrumKey.equalsIgnoreCase(spectrumKey)) {
//                                if (annotationPreferences.useAutomaticAnnotation()) {
//                                    annotationPreferences.setNeutralLossesSequenceDependant(true);
//                                }
//                            }
                        projectOverviewPanel.updateAnnotationMenus(identificationCharge, peptideAssumption.getPeptide());

                        //currentSpectrumKey = spectrumKey; // @TODO: re-add me
                        // show all or just the annotated peaks
                        spectrumPanel.showAnnotatedPeaksOnly(!annotationPreferences.showAllPeaks());
                        spectrumPanel.setYAxisZoomExcludesBackgroundPeaks(annotationPreferences.yAxisZoomExcludesBackgroundPeaks());

                        int forwardIon = projectOverviewPanel.getSearchParameters().getIonSearched1();
                        int rewindIon = projectOverviewPanel.getSearchParameters().getIonSearched2();

                        // add de novo sequencing
                        spectrumPanel.addAutomaticDeNovoSequencing(peptideAssumption.getPeptide(), annotations,
                                forwardIon, rewindIon, annotationPreferences.getDeNovoCharge(),
                                annotationPreferences.showForwardIonDeNovoTags(),
                                annotationPreferences.showRewindIonDeNovoTags(), false);

                        // add the spectrum panel to the frame
                        projectOverviewPanel.getSpectrumJPanel().removeAll();
                        projectOverviewPanel.getSpectrumJPanel().add(spectrumPanel);
                        projectOverviewPanel.getSpectrumJPanel().revalidate();
                        projectOverviewPanel.getSpectrumJPanel().repaint();

                        // create the sequence fragment ion view
                        projectOverviewPanel.getSecondarySpectrumPlotsJPanel().removeAll();
                        SequenceFragmentationPanel sequenceFragmentationPanel = new SequenceFragmentationPanel(
                                projectOverviewPanel.getTaggedPeptideSequence(
                                        peptideAssumption.getPeptide(),
                                        false, false, false),
                                annotations, true, projectOverviewPanel.getSearchParameters().getModificationProfile(), forwardIon, rewindIon);
                        sequenceFragmentationPanel.setMinimumSize(new Dimension(sequenceFragmentationPanel.getPreferredSize().width, sequenceFragmentationPanel.getHeight()));
                        sequenceFragmentationPanel.setOpaque(true);
                        sequenceFragmentationPanel.setBackground(Color.WHITE);
                        projectOverviewPanel.getSecondarySpectrumPlotsJPanel().add(sequenceFragmentationPanel);

                        // create the intensity histograms
                        projectOverviewPanel.getSecondarySpectrumPlotsJPanel().add(new IntensityHistogram(annotations, spectrum, INTENSITY_LEVEL));

                        // create the miniature mass error plot
                        //@todo is annotationPreferences.getFragmentIonAccuracy() the correct mass tolerance
                        MassErrorPlot massErrorPlot = new MassErrorPlot(annotations, spectrum, annotationPreferences.getFragmentIonAccuracy(), false);

                        //if (massErrorPlot.getNumberOfDataPointsInPlot() > 0) {
                        projectOverviewPanel.getSecondarySpectrumPlotsJPanel().add(massErrorPlot);
                        //}

                        //else only add the spectrum panel without annotations
                    } else {
                        // add the spectrum panel to the frame
                        projectOverviewPanel.getSpectrumJPanel().removeAll();
                        projectOverviewPanel.getSpectrumJPanel().add(spectrumPanel);
                        projectOverviewPanel.getSpectrumJPanel().revalidate();
                        projectOverviewPanel.getSpectrumJPanel().repaint();
                    }

                    // update the UI
                    projectOverviewPanel.getSecondarySpectrumPlotsJPanel().revalidate();
                    projectOverviewPanel.getSecondarySpectrumPlotsJPanel().repaint();

                    // update the panel border title
                    //updateSpectrumPanelBorderTitle(currentSpectrum); // @TODO: re-add later
                    projectOverviewPanel.getSpectrumMainPanel().revalidate();
                    projectOverviewPanel.getSpectrumMainPanel().repaint();
                }
            } catch (Exception e) {
                LOGGER.error(e.getCause(), e);
            }
            mainController.getMainFrame().setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        } else {
            clearSpectrum();
        }
    }

    /**
     * Clear the spectrum panel
     */
    private void clearSpectrum() {
        // nothing to display, empty previous results
        projectOverviewPanel.getSpectrumJPanel().removeAll();
        projectOverviewPanel.getSpectrumJPanel().revalidate();
        projectOverviewPanel.getSpectrumJPanel().repaint();

        projectOverviewPanel.getSecondarySpectrumPlotsJPanel().removeAll();
        projectOverviewPanel.getSecondarySpectrumPlotsJPanel().revalidate();
        projectOverviewPanel.getSecondarySpectrumPlotsJPanel().repaint();

        ((TitledBorder) projectOverviewPanel.getSpectrumMainPanel().getBorder()).setTitle("Spectrum & Fragment Ions");
        projectOverviewPanel.getSpectrumMainPanel().repaint();
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
     * Update the given experiment in the experiments EventList.
     *
     * @param updatedExperiment the updated experiment
     */
    private void updateExperiment(Experiment updatedExperiment) {
        //find the experiment in the experiments EventList.
        for (int i = 0; i < experiments.size(); i++) {
            if (experiments.get(i).getId().compareTo(updatedExperiment.getId()) == 0) {
                //update the experiments EventList
                experiments.set(i, updatedExperiment);
                break;
            }
        }
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
     * Update the given sample in the samples EventList.
     *
     * @param updatedSample the updated sample
     */
    private void updateSample(Sample updatedSample) {
        //find the sample in the samples EventList.
        for (int i = 0; i < samples.size(); i++) {
            if (samples.get(i).getId().compareTo(updatedSample.getId()) == 0) {
                //update the samples EventList
                samples.set(i, updatedSample);
                break;
            }
        }
    }

    /**
     * Get the selected analytical run from the analytical run table.
     *
     * @return the selected analytical run, null if no analytical run is selected
     */
    private AnalyticalRun getSelectedAnalyticalRun() {
        AnalyticalRun selectedAnalyticalRun = null;

        EventList<AnalyticalRun> selectedAnalyticalRuns = analyticalRunsSelectionModel.getSelected();
        if (!selectedAnalyticalRuns.isEmpty()) {
            selectedAnalyticalRun = selectedAnalyticalRuns.get(0);
        }

        return selectedAnalyticalRun;
    }

    /**
     * Update the given analytical run in the analytical runs EventList.
     *
     * @param updatedAnalyticalRun the updated sample
     */
    private void updateAnalyticalRun(AnalyticalRun updatedAnalyticalRun) {
        //find the analytical run in the analytical runs EventList.
        for (int i = 0; i < analyticalRuns.size(); i++) {
            if (analyticalRuns.get(i).getId().compareTo(updatedAnalyticalRun.getId()) == 0) {
                //update the samples EventList
                analyticalRuns.set(i, updatedAnalyticalRun);
                break;
            }
        }
    }

    /**
     * Get the selected spectrum from the psm table.
     *
     * @return the selected spectrum, null if no psm is selected
     */
    private Spectrum getSelectedSpectrum() {
        Spectrum selectedPsm = null;

        EventList<Spectrum> selectedPsms = psmsSelectionModel.getSelected();
        if (!selectedPsms.isEmpty()) {
            selectedPsm = selectedPsms.get(0);
        }

        return selectedPsm;
    }

    /**
     * Set the PSM table cell renderers.
     */
    private void setPsmTableCellRenderers() {
        AnalyticalRun analyticalRun = getSelectedAnalyticalRun();

        projectOverviewPanel.getPsmTable().getColumnModel().getColumn(PsmTableFormat.RETENTION_TIME).
                setCellRenderer(new JSparklinesIntervalChartTableCellRenderer(PlotOrientation.HORIZONTAL, spectrumService.getMinimumRetentionTime(analyticalRun),
                        spectrumService.getMaximumRetentionTime(analyticalRun), 50d, utilitiesUserPreferences.getSparklineColor(), utilitiesUserPreferences.getSparklineColor()));
        ((JSparklinesIntervalChartTableCellRenderer) projectOverviewPanel.getPsmTable().getColumnModel()
                .getColumn(PsmTableFormat.RETENTION_TIME).getCellRenderer()).showNumberAndChart(true, labelWidth + 5);
        ((JSparklinesIntervalChartTableCellRenderer) projectOverviewPanel.getPsmTable().getColumnModel()
                .getColumn(PsmTableFormat.RETENTION_TIME).getCellRenderer()).showReferenceLine(true, 0.02, java.awt.Color.BLACK);
    }
}
