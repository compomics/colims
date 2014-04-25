package com.compomics.colims.client.controller;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.AdvancedTableModel;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.GlazedListsSwing;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import com.compomics.colims.client.event.EntityChangeEvent;
import com.compomics.colims.client.event.ExperimentChangeEvent;
import com.compomics.colims.client.event.SampleChangeEvent;
import com.compomics.colims.client.event.message.DbConstraintMessageEvent;
import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.model.tableformat.ExperimentManagementTableFormat;
import com.compomics.colims.client.model.tableformat.ProjectManagementTableFormat;
import com.compomics.colims.client.model.tableformat.SampleManagementTableFormat;
import com.compomics.colims.client.view.ProjectManagementPanel;
import com.compomics.colims.core.service.ExperimentService;
import com.compomics.colims.core.service.ProjectService;
import com.compomics.colims.core.service.SampleService;
import com.compomics.colims.core.service.UserService;
import com.compomics.colims.model.Experiment;
import com.compomics.colims.model.Project;
import com.compomics.colims.model.Protocol;
import com.compomics.colims.model.Sample;
import com.compomics.colims.model.User;
import com.compomics.colims.model.comparator.IdComparator;
import com.google.common.eventbus.EventBus;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("projectManagementController")
public class ProjectManagementController implements Controllable {

    private static final Logger LOGGER = Logger.getLogger(ProjectManagementController.class);
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
    private ProjectManagementPanel projectManagementPanel;
    //child controller
    @Autowired
    private ProjectEditController projectEditController;
    @Autowired
    private ExperimentEditController experimentEditController;
    @Autowired
    private SampleEditController sampleEditController;
    @Autowired
    private AnalyticalRunSetupController analyticalRunSetupController;
    //parent controller
    @Autowired
    private ColimsController colimsController;
    //services
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ExperimentService experimentService;
    @Autowired
    private SampleService sampleService;
    @Autowired
    private UserService userService;
    @Autowired
    private EventBus eventBus;

    public ProjectManagementPanel getProjectManagementPanel() {
        return projectManagementPanel;
    }

    @Override
    public void init() {
        //register to event bus
        eventBus.register(this);

        //init view
        projectManagementPanel = new ProjectManagementPanel();

        //init child controllers
        projectEditController.init();
        experimentEditController.init();
        sampleEditController.init();
        analyticalRunSetupController.init();

        //init projects table        
        SortedList<Project> sortedProjects = new SortedList<>(colimsController.getProjects(), new IdComparator());
        projectsTableModel = GlazedListsSwing.eventTableModel(sortedProjects, new ProjectManagementTableFormat());
        projectManagementPanel.getProjectsTable().setModel(projectsTableModel);
        projectsSelectionModel = new DefaultEventSelectionModel<>(sortedProjects);
        projectsSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        projectManagementPanel.getProjectsTable().setSelectionModel(projectsSelectionModel);

        //set column widths
        projectManagementPanel.getProjectsTable().getColumnModel().getColumn(ProjectManagementTableFormat.PROJECT_ID).setPreferredWidth(5);
        projectManagementPanel.getProjectsTable().getColumnModel().getColumn(ProjectManagementTableFormat.TITLE).setPreferredWidth(300);
        projectManagementPanel.getProjectsTable().getColumnModel().getColumn(ProjectManagementTableFormat.LABEL).setPreferredWidth(100);
        projectManagementPanel.getProjectsTable().getColumnModel().getColumn(ProjectManagementTableFormat.OWNER).setPreferredWidth(100);
        projectManagementPanel.getProjectsTable().getColumnModel().getColumn(ProjectManagementTableFormat.CREATED).setPreferredWidth(50);
        projectManagementPanel.getProjectsTable().getColumnModel().getColumn(ProjectManagementTableFormat.NUMBER_OF_EXPERIMENTS).setPreferredWidth(50);

        //init projects experiment table
        SortedList<Experiment> sortedExperiments = new SortedList<>(experiments, new IdComparator());
        experimentsTableModel = GlazedListsSwing.eventTableModel(sortedExperiments, new ExperimentManagementTableFormat());
        projectManagementPanel.getExperimentsTable().setModel(experimentsTableModel);
        experimentsSelectionModel = new DefaultEventSelectionModel<>(sortedExperiments);
        experimentsSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        projectManagementPanel.getExperimentsTable().setSelectionModel(experimentsSelectionModel);

        //set column widths
        projectManagementPanel.getExperimentsTable().getColumnModel().getColumn(ExperimentManagementTableFormat.EXPERIMENT_ID).setPreferredWidth(5);
        projectManagementPanel.getExperimentsTable().getColumnModel().getColumn(ExperimentManagementTableFormat.TITLE).setPreferredWidth(300);
        projectManagementPanel.getExperimentsTable().getColumnModel().getColumn(ExperimentManagementTableFormat.NUMBER).setPreferredWidth(100);
        projectManagementPanel.getExperimentsTable().getColumnModel().getColumn(ExperimentManagementTableFormat.CREATED).setPreferredWidth(50);
        projectManagementPanel.getExperimentsTable().getColumnModel().getColumn(ExperimentManagementTableFormat.NUMBER_OF_SAMPLES).setPreferredWidth(50);

        //init experiment samples table
        SortedList<Sample> sortedSamples = new SortedList<>(samples, new IdComparator());
        samplesTableModel = GlazedListsSwing.eventTableModel(sortedSamples, new SampleManagementTableFormat());
        projectManagementPanel.getSamplesTable().setModel(samplesTableModel);
        samplesSelectionModel = new DefaultEventSelectionModel<>(sortedSamples);
        samplesSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        projectManagementPanel.getSamplesTable().setSelectionModel(samplesSelectionModel);

        //set column widths
        projectManagementPanel.getSamplesTable().getColumnModel().getColumn(SampleManagementTableFormat.SAMPLE_ID).setPreferredWidth(5);
        projectManagementPanel.getSamplesTable().getColumnModel().getColumn(SampleManagementTableFormat.NAME).setPreferredWidth(300);
        projectManagementPanel.getSamplesTable().getColumnModel().getColumn(SampleManagementTableFormat.PROTOCOL).setPreferredWidth(100);
        projectManagementPanel.getSamplesTable().getColumnModel().getColumn(SampleManagementTableFormat.CREATED).setPreferredWidth(50);
        projectManagementPanel.getSamplesTable().getColumnModel().getColumn(SampleManagementTableFormat.NUMBER_OF_RUNS).setPreferredWidth(50);

        //set sorting
        TableComparatorChooser projectsTableSorter = TableComparatorChooser.install(
                projectManagementPanel.getProjectsTable(), sortedProjects, TableComparatorChooser.SINGLE_COLUMN);
        TableComparatorChooser experimentsTableSorter = TableComparatorChooser.install(
                projectManagementPanel.getExperimentsTable(), sortedExperiments, TableComparatorChooser.SINGLE_COLUMN);
        TableComparatorChooser samplesTableSorter = TableComparatorChooser.install(
                projectManagementPanel.getSamplesTable(), sortedSamples, TableComparatorChooser.SINGLE_COLUMN);

        //add action listeners
        projectsSelectionModel.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent lse) {
                if (!lse.getValueIsAdjusting()) {
                    Project selectedProject = getSelectedProject();
                    if (selectedProject != null) {
                        //fill project experiments table                        
                        GlazedLists.replaceAll(experiments, selectedProject.getExperiments(), false);
                    } else {
                        GlazedLists.replaceAll(experiments, new ArrayList<Experiment>(), false);
                    }
                }
            }
        });                

        projectManagementPanel.getAddProjectButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                projectEditController.updateView(createDefaultProject());
            }
        });

        projectManagementPanel.getEditProjectButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Project selectedProject = getSelectedProject();
                if (selectedProject != null) {
                    projectEditController.updateView(selectedProject);
                } else {
                    eventBus.post(new MessageEvent("Project selection", "Please select a project to edit.", JOptionPane.INFORMATION_MESSAGE));
                }
            }
        });

        projectManagementPanel.getDeleteProjectButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Project projectToDelete = getSelectedProject();

                if (projectToDelete != null) {
                    try {
                        projectService.delete(projectToDelete);

                        //remove from overview table and clear selection
                        colimsController.getProjects().remove(projectToDelete);
                        projectsSelectionModel.clearSelection();
                    } catch (DataIntegrityViolationException dive) {
                        //check if the project can be deleted without breaking existing database relations,
                        //i.e. are there any constraints violations
                        if (dive.getCause() instanceof ConstraintViolationException) {
                            DbConstraintMessageEvent dbConstraintMessageEvent = new DbConstraintMessageEvent("project", projectToDelete.getLabel());
                            eventBus.post(dbConstraintMessageEvent);
                        } else {
                            //pass the exception
                            throw dive;
                        }
                    }
                } else {
                    eventBus.post(new MessageEvent("Project selection", "Please select a project to delete.", JOptionPane.INFORMATION_MESSAGE));
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

        projectManagementPanel.getAddExperimentButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (getSelectedProject() != null) {
                    experimentEditController.updateView(createDefaultExperiment());
                } else {
                    eventBus.post(new MessageEvent("Experiment addition", "Please select a project to add an experiment to.", JOptionPane.INFORMATION_MESSAGE));
                }
            }
        });

        projectManagementPanel.getEditExperimentButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Experiment selectedExperiment = getSelectedExperiment();
                if (selectedExperiment != null) {
                    experimentEditController.updateView(selectedExperiment);
                } else {
                    eventBus.post(new MessageEvent("Experiment selection", "Please select an experiment to edit.", JOptionPane.INFORMATION_MESSAGE));
                }
            }
        });

        projectManagementPanel.getDeleteExperimentButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Experiment experimentToDelete = getSelectedExperiment();

                if (experimentToDelete != null) {
                    try {
                        experimentService.delete(experimentToDelete);

                        //remove from overview table and clear selection
                        experiments.remove(experimentToDelete);
                        experimentsSelectionModel.clearSelection();
                        eventBus.post(new ExperimentChangeEvent(EntityChangeEvent.Type.DELETED, experimentToDelete));

                        //remove experiment from the selected project and update the table
                        getSelectedProject().getExperiments().remove(experimentToDelete);
                        projectManagementPanel.getProjectsTable().updateUI();
                    } catch (DataIntegrityViolationException dive) {
                        //check if the experiment can be deleted without breaking existing database relations,
                        //i.e. are there any constraints violations
                        if (dive.getCause() instanceof ConstraintViolationException) {
                            DbConstraintMessageEvent dbConstraintMessageEvent = new DbConstraintMessageEvent("experiment", Long.toString(experimentToDelete.getNumber()));
                            eventBus.post(dbConstraintMessageEvent);
                        } else {
                            //pass the exception
                            throw dive;
                        }
                    }
                } else {
                    eventBus.post(new MessageEvent("Experiment selection", "Please select an experiment to delete.", JOptionPane.INFORMATION_MESSAGE));
                }
            }
        });
        
        projectManagementPanel.getAddAnalyticalRunButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {    
                Sample selectedSample = getSelectedSample();
                if (selectedSample != null) {
                    analyticalRunSetupController.showView();
                } else {
                    eventBus.post(new MessageEvent("Analytical run addition", "Please select a sample to add the run to.", JOptionPane.INFORMATION_MESSAGE));
                }                
            }
        });

        projectManagementPanel.getAddSampleButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (getSelectedExperiment() != null) {
                    sampleEditController.updateView(createDefaultSample());
                } else {
                    eventBus.post(new MessageEvent("Sample addition", "Please select an experiment to add a sample to.", JOptionPane.INFORMATION_MESSAGE));
                }                
            }
        });

        projectManagementPanel.getEditSampleButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Sample selectedSample = getSelectedSample();
                if (selectedSample != null) {
                    sampleEditController.updateView(selectedSample);
                } else {
                    eventBus.post(new MessageEvent("Sample selection", "Please select a sample to edit.", JOptionPane.INFORMATION_MESSAGE));
                }
            }
        });

        projectManagementPanel.getDeleteSampleButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Sample sampleToDelete = getSelectedSample();

                if (sampleToDelete != null) {
                    try {
                        sampleService.delete(sampleToDelete);

                        //remove from overview table and clear selection
                        samples.remove(sampleToDelete);
                        samplesSelectionModel.clearSelection();
                        eventBus.post(new SampleChangeEvent(EntityChangeEvent.Type.DELETED, sampleToDelete));

                        //remove sample from the selected experiment and update the table
                        getSelectedExperiment().getSamples().remove(sampleToDelete);
                        projectManagementPanel.getExperimentsTable().updateUI();
                    } catch (DataIntegrityViolationException dive) {
                        //check if the sample can be deleted without breaking existing database relations,
                        //i.e. are there any constraints violations
                        if (dive.getCause() instanceof ConstraintViolationException) {
                            DbConstraintMessageEvent dbConstraintMessageEvent = new DbConstraintMessageEvent("sample", sampleToDelete.getName());
                            eventBus.post(dbConstraintMessageEvent);
                        } else {
                            //pass the exception
                            throw dive;
                        }
                    }
                } else {
                    eventBus.post(new MessageEvent("Sample selection", "Please select a sample to delete.", JOptionPane.INFORMATION_MESSAGE));
                }
            }
        });
    }

    @Override
    public void showView() {
        //do nothing
    }

    /**
     * Get the row index of the selected project in the projects table
     *
     * @return
     */
    public int getSelectedProjectIndex() {
        return projectsSelectionModel.getLeadSelectionIndex();
    }

    /**
     * Set the selected project in the projects table
     *
     * @param index
     */
    public void setSelectedProject(int index) {
        projectsSelectionModel.clearSelection();
        projectsSelectionModel.setLeadSelectionIndex(index);
    }

    /**
     * Add a project to the projects table
     *
     * @param project
     */
    public void addProject(Project project) {
        colimsController.getProjects().add(project);
    }

    /**
     * Get the number of projects in the projects table
     *
     * @return
     */
    public int getProjectsSize() {
        return colimsController.getProjects().size();
    }

    /**
     * Get the selected project from the project overview table.
     *
     * @return the selected project, null if no project is selected
     */
    public Project getSelectedProject() {
        Project selectedProject = null;

        EventList<Project> selectedProjects = projectsSelectionModel.getSelected();
        if (!selectedProjects.isEmpty()) {
            selectedProject = selectedProjects.get(0);
        }

        return selectedProject;
    }

    /**
     * Get the row index of the selected experiment in the experiments table
     *
     * @return
     */
    public int getSelectedExperimentIndex() {
        return experimentsSelectionModel.getLeadSelectionIndex();
    }

    /**
     * Set the selected experiment in the experiments table
     *
     * @param index
     */
    public void setSelectedExperiment(int index) {
        experimentsSelectionModel.clearSelection();
        experimentsSelectionModel.setLeadSelectionIndex(index);
    }

    /**
     * Add an experiment to the experiments table
     *
     * @param experiment
     */
    public void addExperiment(Experiment experiment) {
        experiments.add(experiment);

        //add the experiment to the selected project and update the projects table
        getSelectedProject().getExperiments().add(experiment);
        projectManagementPanel.getProjectsTable().updateUI();
    }

    /**
     * Get the number of experiments in the experiments table
     *
     * @return
     */
    public int getExperimentsSize() {
        return experiments.size();
    }

    /**
     * Get the selected experiment from the experiment overview table.
     *
     * @return the selected experiment, null if no experiment is selected
     */
    public Experiment getSelectedExperiment() {
        Experiment selectedExperiment = null;

        EventList<Experiment> selectedExperiments = experimentsSelectionModel.getSelected();
        if (!selectedExperiments.isEmpty()) {
            selectedExperiment = selectedExperiments.get(0);
        }

        return selectedExperiment;
    }
    
    /**
     * Get the row index of the selected sample in the samples table
     *
     * @return
     */
    public int getSelectedSampleIndex() {
        return samplesSelectionModel.getLeadSelectionIndex();
    }

    /**
     * Set the selected sample in the samples table
     *
     * @param index
     */
    public void setSelectedSample(int index) {
        samplesSelectionModel.clearSelection();
        samplesSelectionModel.setLeadSelectionIndex(index);
    }

    /**
     * Add a sample to the samples table
     *
     * @param sample
     */
    public void addSample(Sample sample) {
        samples.add(sample);
        
        //add the sample to the selected experiment and update the experiments table
        getSelectedExperiment().getSamples().add(sample);
        projectManagementPanel.getProjectsTable().updateUI();
    }

    /**
     * Get the number of samples in the samples table
     *
     * @return
     */
    public int getSamplesSize() {
        return samples.size();
    }
    
    /**
     * Get the selected sample from the sample overview table.
     *
     * @return the selected sample, null if no sample is selected
     */
    public Sample getSelectedSample() {
        Sample selectedSample = null;

        EventList<Sample> selectedSamples = samplesSelectionModel.getSelected();
        if (!selectedSamples.isEmpty()) {
            selectedSample = selectedSamples.get(0);
        }

        return selectedSample;
    }

    /**
     * Create a default project, with some default properties.
     *
     * @return the default project
     */
    private Project createDefaultProject() {
        Project defaultProject = new Project();

        defaultProject.setTitle("default project title");
        defaultProject.setLabel("def_label");

        //set default owner, i.e. the user with the most projects
        User userWithMostProjectOwns = projectService.getUserWithMostProjectOwns();
        if (userWithMostProjectOwns != null) {
            defaultProject.setOwner(userWithMostProjectOwns);
        } else {
            defaultProject.setOwner(userService.findAll().get(0));
        }

        return defaultProject;
    }

    /**
     * Create a default experiment, with some default properties.
     *
     * @return the default experiment
     */
    private Experiment createDefaultExperiment() {
        Experiment defaultExperiment = new Experiment();

        defaultExperiment.setTitle("default experiment title");
        defaultExperiment.setNumber(1L);

        return defaultExperiment;
    }
    
    /**
     * Create a default sample, with some default properties.
     *
     * @return the default sample
     */
    private Sample createDefaultSample() {
        Sample defaultSample = new Sample();

        defaultSample.setName("default sample name");
        Protocol mostUsedProtocol = sampleService.getMostUsedProtocol();
        if (mostUsedProtocol != null) {
            defaultSample.setProtocol(mostUsedProtocol);
        }

        return defaultSample;
    }
}
