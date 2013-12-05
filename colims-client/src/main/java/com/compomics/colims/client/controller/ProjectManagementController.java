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
import com.compomics.colims.client.event.MessageEvent;
import com.compomics.colims.client.model.ExperimentsOverviewTableFormat;
import com.compomics.colims.client.model.ProjectsOverviewTableFormat;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.ProjectEditDialog;
import com.compomics.colims.client.view.ProjectManagementPanel;
import com.compomics.colims.core.service.ProjectService;
import com.compomics.colims.core.service.UserService;
import com.compomics.colims.model.Experiment;
import com.compomics.colims.model.Project;
import com.compomics.colims.model.User;
import com.compomics.colims.model.comparator.CvTermAccessionComparator;
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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.log4j.Logger;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JComboBoxBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("projectManagementController")
public class ProjectManagementController {

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
    private MainController mainController;
    //services
    @Autowired
    private ProjectService projectService;
    @Autowired
    private UserService userService;
    @Autowired
    private EventBus eventBus;

    public ProjectManagementPanel getProjectsOverviewPanel() {
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

    private void initProjectManagementPanel() {
        projectManagementPanel = new ProjectManagementPanel();

        //init projects table
        projects.addAll(projectService.findAllWithEagerFetching());
        SortedList<Project> sortedProjects = new SortedList<>(projects, new IdComparator());
        projectsTableModel = GlazedListsSwing.eventTableModel(sortedProjects, new ProjectsOverviewTableFormat());
        projectManagementPanel.getProjectsTable().setModel(projectsTableModel);
        projectsSelectionModel = new DefaultEventSelectionModel<>(sortedProjects);
        projectManagementPanel.getProjectsTable().setSelectionModel(projectsSelectionModel);

        //init projects experiment table
        SortedList<Experiment> sortedExperiments = new SortedList<>(experiments, new IdComparator());
        experimentsTableModel = GlazedListsSwing.eventTableModel(sortedExperiments, new ExperimentsOverviewTableFormat());
        projectManagementPanel.getExperimentsTable().setModel(experimentsTableModel);
        experimentsSelectionModel = new DefaultEventSelectionModel<>(sortedExperiments);
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
                    EventList<Project> selectedProjects = projectsSelectionModel.getSelected();
                    if (!selectedProjects.isEmpty()) {
                        Project selectedProject = selectedProjects.get(0);
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
                    EventList<Experiment> selectedExperiments = experimentsSelectionModel.getSelected();
                    if (!selectedExperiments.isEmpty()) {
                        //for the moment, do nothing
                    }
                }
            }
        });

        projectManagementPanel.getAddProjectButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                projectToEdit = createDefaultProject();
                updateProjectEditDialog(projectToEdit);

                projectEditDialog.setLocationRelativeTo(null);
                projectEditDialog.setVisible(true);
            }
        });

        projectManagementPanel.getEditProjectButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                projectToEdit = getSelectedProject();
                if (projectToEdit != null) {
                    updateProjectEditDialog(projectToEdit);

                    projectEditDialog.setLocationRelativeTo(null);
                    projectEditDialog.setVisible(true);
                }
                else{
                    eventBus.post(new MessageEvent("Project selection", "Please select a project to edit.", JOptionPane.INFORMATION_MESSAGE));
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
        projectEditDialog = new ProjectEditDialog(mainController.getMainFrame(), true);
        
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
                //validate project
                List<String> validationMessages = GuiUtils.validateEntity(projectToEdit);
                //check for a new project if the project title already exists in the db                
                if (projectToEdit.getId() == null && isExistingInstrumentName(selectedInstrument)) {
                    validationMessages.add(selectedInstrument.getName() + " already exists in the database, please choose another instrument name.");
                }
                if (validationMessages.isEmpty()) {
                    if (selectedInstrument.getId() != null) {
                        instrumentService.update(selectedInstrument);
                    } else {
                        instrumentService.save(selectedInstrument);
                    }
                    instrumentEditDialog.getInstrumentSaveOrUpdateButton().setText("update");

                    MessageEvent messageEvent = new MessageEvent("Instrument persist confirmation", "Instrument " + selectedInstrument.getName() + " was persisted successfully!", JOptionPane.INFORMATION_MESSAGE);
                    eventBus.post(messageEvent);

                    //refresh selection in instrument list in management overview dialog
                    int index = instrumentManagementDialog.getInstrumentList().getSelectedIndex();
                    instrumentManagementDialog.getInstrumentList().getSelectionModel().clearSelection();
                    instrumentManagementDialog.getInstrumentList().setSelectedIndex(index);
                } else {
                    MessageEvent messageEvent = new MessageEvent("Validation failure", validationMessages, JOptionPane.ERROR_MESSAGE);
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

    private void updateProjectEditDialog(Project project) {
        if (project.getId() != null) {
            projectEditDialog.getSaveOrUpdateButton().setText("update");
        } else {
            projectEditDialog.getSaveOrUpdateButton().setText("save");
        }

        projectEditDialog.getTitleTextField().setText(project.getTitle());
        projectEditDialog.getLabelTextField().setText(project.getLabel());

        //set the selected item in the owner combobox        
        projectEditDialog.getOwnerComboBox().setSelectedItem(project.getOwner());
        projectEditDialog.getDescriptionTextArea().setText(project.getDescription());
        //populate user dual list
        projectEditDialog.getUserDualList().populateLists(userService.findAll(), project.getUsers());
    }

    /**
     * Get the selected project in the project management table.
     *
     * @return the selected project
     */
    private Project getSelectedProject() {
        Project selectedProject = null;

        //get the selected project and show the project edit dialog
        EventList<Project> selectedProjects = projectsSelectionModel.getSelected();
        if (!selectedProjects.isEmpty()) {
            selectedProject = selectedProjects.get(0);
        }

        return selectedProject;
    }
    
    /**
     * Check if a project with the given project title exists in the
     * database.
     *
     * @param project the project
     * @return does the project title exist
     */
    private boolean isExistingProjectTitle(Project project) {
        boolean isExistingProjectTitle = true;
        Project foundProject = projectService.findByName(instrument.getName());
        if (foundInstrument == null) {
            isExistingInstrumentName = false;
        }

        return isExistingInstrumentName;
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
    private Project createDefaultExperiment() {
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
}
