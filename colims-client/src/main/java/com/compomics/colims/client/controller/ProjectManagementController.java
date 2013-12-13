package com.compomics.colims.client.controller;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.AdvancedTableModel;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.GlazedListsSwing;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import com.compomics.colims.client.event.message.DbConstraintMessageEvent;
import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.model.format.ExperimentsOverviewTableFormat;
import com.compomics.colims.client.model.format.ProjectsOverviewTableFormat;
import com.compomics.colims.client.view.ProjectManagementPanel;
import com.compomics.colims.core.service.ProjectService;
import com.compomics.colims.core.service.UserService;
import com.compomics.colims.model.Experiment;
import com.compomics.colims.model.Project;
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
import org.jdesktop.observablecollections.ObservableList;
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
    private EventList<Project> projects = new BasicEventList<>();
    private AdvancedTableModel<Project> projectsTableModel;
    private DefaultEventSelectionModel<Project> projectsSelectionModel;
    private EventList<Experiment> experiments = new BasicEventList<>();
    private AdvancedTableModel<Experiment> experimentsTableModel;
    private DefaultEventSelectionModel<Experiment> experimentsSelectionModel;
    //view
    private ProjectManagementPanel projectManagementPanel;
    //child controller
    @Autowired
    private ProjectEditController projectEditController;
    //parent controller
    @Autowired
    private ColimsController colimsController;
    //services
    @Autowired
    private ProjectService projectService;
    @Autowired
    private EventBus eventBus;

    public ProjectManagementPanel getProjectManagementPanel() {
        return projectManagementPanel;
    }

    public void init() {
        //register to event bus
        eventBus.register(this);

        //init view
        projectManagementPanel = new ProjectManagementPanel();
        
        //init child controllers
        projectEditController.init();

        //init projects table
        projects.addAll(projectService.findAllWithEagerFetching());
        SortedList<Project> sortedProjects = new SortedList<>(projects, new IdComparator());
        projectsTableModel = GlazedListsSwing.eventTableModel(sortedProjects, new ProjectsOverviewTableFormat());
        projectManagementPanel.getProjectsTable().setModel(projectsTableModel);
        projectsSelectionModel = new DefaultEventSelectionModel<>(sortedProjects);
        projectsSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        projectManagementPanel.getProjectsTable().setSelectionModel(projectsSelectionModel);

        //init projects experiment table
        SortedList<Experiment> sortedExperiments = new SortedList<>(experiments, new IdComparator());
        experimentsTableModel = GlazedListsSwing.eventTableModel(sortedExperiments, new ExperimentsOverviewTableFormat());
        projectManagementPanel.getExperimentsTable().setModel(experimentsTableModel);
        experimentsSelectionModel = new DefaultEventSelectionModel<>(sortedExperiments);
        experimentsSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        projectManagementPanel.getExperimentsTable().setSelectionModel(experimentsSelectionModel);

        //use MULTIPLE_COLUMN_MOUSE to allow sorting by multiple columns
        TableComparatorChooser projectsTableSorter = TableComparatorChooser.install(
                projectManagementPanel.getProjectsTable(), sortedProjects, TableComparatorChooser.SINGLE_COLUMN);
        TableComparatorChooser experimentsTableSorter = TableComparatorChooser.install(
                projectManagementPanel.getExperimentsTable(), sortedExperiments, TableComparatorChooser.SINGLE_COLUMN);

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

        experimentsSelectionModel.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent lse) {
                if (!lse.getValueIsAdjusting()) {
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
                    eventBus.post(new MessageEvent("project selection", "Please select a project to edit.", JOptionPane.INFORMATION_MESSAGE));
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
                        projects.remove(projectToDelete);
                        projectsSelectionModel.clearSelection();
                    } catch (DataIntegrityViolationException dive) {
                        //check if the instrument can be deleted without breaking existing database relations,
                        //i.e. are there any constraints violations
                        if (dive.getCause() instanceof ConstraintViolationException) {
                            DbConstraintMessageEvent dbConstraintMessageEvent = new DbConstraintMessageEvent("project", projectToDelete.getLabel());
                            eventBus.post(dbConstraintMessageEvent);
                        } else {
                            //pass the exception
                            throw dive;
                        }
                    }
                }
            }
        });

        projectManagementPanel.getAddExperimentButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });

        projectManagementPanel.getEditExperimentButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
        projects.add(project);
    }

    /**
     * Get the number of projects in the projects table
     *
     * @return
     */
    public int getProjectsSize() {
        return projects.size();
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
     * Add a project to the projects table
     *
     * @param project
     */
    public void addExperiment(Experiment experiment) {
        experiments.add(experiment);
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
}
