package com.compomics.colims.client.controller;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.AdvancedTableModel;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.GlazedListsSwing;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import com.compomics.colims.client.compoment.DualList;
import com.compomics.colims.client.event.message.DbConstraintMessageEvent;
import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.model.format.ExperimentsOverviewTableFormat;
import com.compomics.colims.client.model.format.ProjectsOverviewTableFormat;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.ProjectEditDialog;
import com.compomics.colims.client.view.ProjectManagementPanel;
import com.compomics.colims.core.service.ProjectService;
import com.compomics.colims.core.service.UserService;
import com.compomics.colims.model.Experiment;
import com.compomics.colims.model.Project;
import com.compomics.colims.model.User;
import com.compomics.colims.model.comparator.IdComparator;
import com.compomics.colims.model.comparator.UserNameComparator;
import com.google.common.eventbus.EventBus;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JComboBoxBinding;
import org.jdesktop.swingbinding.SwingBindings;
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
    private BindingGroup bindingGroup;
    private ObservableList<User> userBindingList;
    private Project projectToEdit;
    //view
    private ProjectManagementPanel projectManagementPanel;
    private ProjectEditDialog projectEditDialog;
    //parent controller
    @Autowired
    private ColimsController mainController;
    //services
    @Autowired
    private ProjectService projectService;
    @Autowired
    private UserService userService;
    @Autowired
    private EventBus eventBus;

    public ProjectManagementPanel getProjectManagementPanel() {
        return projectManagementPanel;
    }

    public void init() {
        //register to event bus
        eventBus.register(this);

        bindingGroup = new BindingGroup();

        initProjectManagementPanel();
        initProjectEditDialog();

        bindingGroup.bind();
    }
    
    @Override
    public void showView() {
        //do nothing
    }

    private void initProjectManagementPanel() {
        projectManagementPanel = new ProjectManagementPanel();

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
                projectToEdit = createDefaultProject();
                updateProjectEditDialog();

                projectEditDialog.setLocationRelativeTo(null);
                projectEditDialog.setVisible(true);
            }
        });

        projectManagementPanel.getEditProjectButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Project selectedProject = getSelectedProject();
                if (selectedProject != null) {
                    projectToEdit = selectedProject;

                    updateProjectEditDialog();

                    projectEditDialog.setLocationRelativeTo(null);
                    projectEditDialog.setVisible(true);
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

    private void initProjectEditDialog() {
        projectEditDialog = new ProjectEditDialog(mainController.getColimsFrame(), true);

        //init dual list
        projectEditDialog.getUserDualList().init(new UserNameComparator());

        //add binding
        userBindingList = ObservableCollections.observableList(userService.findAll());

        JComboBoxBinding instrumentTypeComboBoxBinding = SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ_WRITE, userBindingList, projectEditDialog.getOwnerComboBox());
        bindingGroup.addBinding(instrumentTypeComboBoxBinding);

        //add action listeners
        projectEditDialog.getUserDualList().addPropertyChangeListener(DualList.CHANGED, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                List<User> addedUsers = projectEditDialog.getUserDualList().getAddedItems();

                projectToEdit.setUsers(addedUsers);
            }
        });

        projectEditDialog.getSaveOrUpdateButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //update projectToEdit with dialog input
                updateProjectToEdit();

                //validate project
                List<String> validationMessages = GuiUtils.validateEntity(projectToEdit);
                //check for a new project if the project title already exists in the db                
                if (projectToEdit.getId() == null && isExistingProjectTitle(projectToEdit)) {
                    validationMessages.add(projectToEdit.getTitle() + " already exists in the database,"
                            + "\n" + "please choose another project title.");
                }
                int index = 0;
                if (validationMessages.isEmpty()) {
                    if (projectToEdit.getId() != null) {
                        projectService.update(projectToEdit);
                        index = projectsSelectionModel.getLeadSelectionIndex();
                    } else {
                        projectService.save(projectToEdit);
                        //add project to overview table
                        projects.add(projectToEdit);
                        index = projects.size() - 1;
                    }
                    projectEditDialog.getSaveOrUpdateButton().setText("update");

                    MessageEvent messageEvent = new MessageEvent("project persist confirmation", "Project " + projectToEdit.getLabel() + " was persisted successfully!", JOptionPane.INFORMATION_MESSAGE);
                    eventBus.post(messageEvent);

                    //refresh selection in project list in management overview dialog
                    projectsSelectionModel.clearSelection();
                    projectsSelectionModel.setLeadSelectionIndex(index);
                } else {
                    MessageEvent messageEvent = new MessageEvent("validation failure", validationMessages, JOptionPane.WARNING_MESSAGE);
                    eventBus.post(messageEvent);
                }
            }
        });

        projectEditDialog.getCancelButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                projectEditDialog.dispose();
            }
        });
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
     * Update the project edit dialog with the selected project in the project
     * overview table.
     */
    private void updateProjectEditDialog() {
        if (projectToEdit.getId() != null) {
            projectEditDialog.getSaveOrUpdateButton().setText("update");
        } else {
            projectEditDialog.getSaveOrUpdateButton().setText("save");
        }

        projectEditDialog.getTitleTextField().setText(projectToEdit.getTitle());
        projectEditDialog.getLabelTextField().setText(projectToEdit.getLabel());

        //set the selected item in the owner combobox        
        projectEditDialog.getOwnerComboBox().setSelectedItem(projectToEdit.getOwner());
        projectEditDialog.getDescriptionTextArea().setText(projectToEdit.getDescription());
        //populate user dual list
        projectEditDialog.getUserDualList().populateLists(userService.findAll(), projectToEdit.getUsers());
    }

    /**
     * Update the instance fields of the projectToEdit variable with the dialog
     * input.
     *
     */
    private void updateProjectToEdit() {
        projectToEdit.setTitle(projectEditDialog.getTitleTextField().getText());
        projectToEdit.setLabel(projectEditDialog.getLabelTextField().getText());
        projectToEdit.setOwner(userBindingList.get(projectEditDialog.getOwnerComboBox().getSelectedIndex()));
        projectToEdit.setDescription(projectEditDialog.getDescriptionTextArea().getText());
        //the users have been update by the duallist listener
    }

    /**
     * Check if a project with the given project title exists in the database.
     *
     * @param project the project
     * @return does the project title exist
     */
    private boolean isExistingProjectTitle(Project project) {
        boolean isExistingProjectTitle = true;
        Project foundProject = projectService.findByTitle(project.getTitle());
        if (foundProject == null) {
            isExistingProjectTitle = false;
        }

        return isExistingProjectTitle;
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
