package com.compomics.colims.client.controller;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import ca.odell.glazedlists.swing.EventSelectionModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import com.compomics.colims.client.factory.SpectrumPanelGenerator;
import com.compomics.colims.client.model.SpectrumTableFormat;
import com.compomics.colims.client.view.HomePanel;
import com.compomics.colims.core.service.ProjectService;
import com.compomics.colims.core.service.SpectrumService;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.Experiment;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.Project;
import com.compomics.colims.model.Sample;
import com.compomics.colims.model.Spectrum;
import com.compomics.colims.model.comparator.SpectrumIdComparator;
import com.compomics.util.gui.spectrum.SpectrumPanel;
import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.log4j.Logger;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("homeController")
public class HomeController {

    private static final Logger LOGGER = Logger.getLogger(HomeController.class);
    //model
    private BindingGroup bindingGroup;
    private ObservableList<Project> projectBindingList;
    private ObservableList<Experiment> experimentBindingList;
    private ObservableList<Sample> sampleBindingList;
    private ObservableList<AnalyticalRun> analyticalRunBindingList;
    private EventList<Spectrum> spectrumEventList;
    private SortedList<Spectrum> sortedSpectrumList;
    //view
    private HomePanel homePanel;
    //parent controller
    @Autowired
    private MainController mainController;
    //services
    @Autowired
    private ProjectService projectService;
    @Autowired
    private SpectrumService spectrumService;
    @Autowired
    private SpectrumPanelGenerator spectrumPanelGenerator;

    public HomeController() {
    }

    public HomePanel getHomePanel() {
        return homePanel;
    }

    public void init() {
        homePanel = new HomePanel();

        //init spectrum table
        spectrumEventList = new BasicEventList<>();
        sortedSpectrumList = new SortedList<>(spectrumEventList, new SpectrumIdComparator());
        homePanel.getSpectrumJTable().setModel(new DefaultEventTableModel(sortedSpectrumList, new SpectrumTableFormat()));
        homePanel.getSpectrumJTable().setSelectionModel(new DefaultEventSelectionModel(sortedSpectrumList));

        //use MULTIPLE_COLUMN_MOUSE to allow sorting by multiple columns
        TableComparatorChooser tableSorter = TableComparatorChooser.install(
                homePanel.getSpectrumJTable(), sortedSpectrumList, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE);

        //init bindings
        bindingGroup = new BindingGroup();

        projectBindingList = ObservableCollections.observableList(projectService.findAllWithEagerFetching());
        JListBinding projectJListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, projectBindingList, homePanel.getProjectJList());
        bindingGroup.addBinding(projectJListBinding);
        experimentBindingList = ObservableCollections.observableList(new ArrayList<Experiment>());
        JListBinding experimentJListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, experimentBindingList, homePanel.getExperimentJList());
        bindingGroup.addBinding(experimentJListBinding);
        sampleBindingList = ObservableCollections.observableList(new ArrayList<Sample>());
        JListBinding sampleJListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, sampleBindingList, homePanel.getSampleJList());
        bindingGroup.addBinding(sampleJListBinding);
        analyticalRunBindingList = ObservableCollections.observableList(new ArrayList<AnalyticalRun>());
        JListBinding analyticalRunJListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, analyticalRunBindingList, homePanel.getAnalyticalRunJList());
        bindingGroup.addBinding(analyticalRunJListBinding);

        bindingGroup.bind();

        //add action listeners
        homePanel.getProjectJList().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                ListSelectionModel listSelectionModel = (ListSelectionModel) e.getSource();
                if (!listSelectionModel.isSelectionEmpty()) {
                    int selectedProjectIndex = homePanel.getProjectJList().getSelectedIndex();
                    if (selectedProjectIndex != -1) {
                        Project selectedProject = projectBindingList.get(selectedProjectIndex);
                        experimentBindingList.clear();
                        experimentBindingList.addAll(selectedProject.getExperiments());

                        //clear previous selections
                        sampleBindingList.clear();
                        analyticalRunBindingList.clear();
                        spectrumEventList.clear();
                    }
                }
            }
        });

        homePanel.getExperimentJList().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                ListSelectionModel listSelectionModel = (ListSelectionModel) e.getSource();
                if (!listSelectionModel.isSelectionEmpty()) {
                    int selectedExperimentIndex = homePanel.getExperimentJList().getSelectedIndex();
                    if (selectedExperimentIndex != -1) {
                        Experiment selectedExperiment = experimentBindingList.get(selectedExperimentIndex);
                        sampleBindingList.clear();
                        sampleBindingList.addAll(selectedExperiment.getSamples());

                        //clear previous selections                        
                        analyticalRunBindingList.clear();
                        spectrumEventList.clear();
                    }
                }
            }
        });

        homePanel.getSampleJList().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                ListSelectionModel listSelectionModel = (ListSelectionModel) e.getSource();
                if (!listSelectionModel.isSelectionEmpty()) {
                    int selectedSampleIndex = homePanel.getSampleJList().getSelectedIndex();
                    if (selectedSampleIndex != -1) {
                        Sample sample = sampleBindingList.get(selectedSampleIndex);
                        analyticalRunBindingList.clear();
                        analyticalRunBindingList.addAll(sample.getAnalyticalRuns());

                        //clear previous selections
                        spectrumEventList.clear();
                    }
                }
            }
        });

        homePanel.getAnalyticalRunJList().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                ListSelectionModel listSelectionModel = (ListSelectionModel) e.getSource();
                if (!listSelectionModel.isSelectionEmpty()) {
                    int selectedAnalyticalRunIndex = homePanel.getAnalyticalRunJList().getSelectedIndex();
                    if (selectedAnalyticalRunIndex != -1) {
                        AnalyticalRun selectedAnalyticalRun = analyticalRunBindingList.get(selectedAnalyticalRunIndex);
                        spectrumEventList.clear();
                        spectrumEventList.addAll(spectrumService.findSpectraByAnalyticalRunId(selectedAnalyticalRun.getId()));
                    }
                }
            }
        });

        homePanel.getSpectrumJTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent lse) {
                if (!lse.getValueIsAdjusting()) {
                    if (homePanel.getSpectrumJTable().getSelectedRow() != -1) {
                        Spectrum spectrum = sortedSpectrumList.get(homePanel.getSpectrumJTable().getSelectedRow());

                        Map<Double, Double> spectrumPeaks = spectrumService.getSpectrumPeaks(spectrum.getId());

                        //check if the spectrum has been matched
                        Peptide peptide = (spectrum.getPeptides().isEmpty()) ? null : spectrum.getPeptides().get(0);
                        SpectrumPanel spectrumPanel = spectrumPanelGenerator.getSpectrumPanel(spectrum.getMzRatio(), spectrum.getCharge(), spectrumPeaks, peptide);

                        addSpectrumPanel(spectrumPanel);
                    }
                }
            }
        });
    }

    private void addSpectrumPanel(SpectrumPanel spectrumPanel) {
        //remove spectrum panel if already present
        if (homePanel.getSpectrumDetailPanel().getComponentCount() != 0) {
            homePanel.getSpectrumDetailPanel().remove(0);
        }

        if (spectrumPanel != null) {
            //add the spectrum panel to the identifications detail panel
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.weighty = 1.0;

            homePanel.getSpectrumDetailPanel().add(spectrumPanel, gridBagConstraints);
        }

        homePanel.getSpectrumDetailPanel().validate();
        homePanel.getSpectrumDetailPanel().repaint();
    }
}
