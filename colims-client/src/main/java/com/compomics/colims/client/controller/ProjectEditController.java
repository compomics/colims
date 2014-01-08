package com.compomics.colims.client.controller;

import com.compomics.colims.client.compoment.DualList;
import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.ProjectEditDialog;
import com.compomics.colims.core.service.ProjectService;
import com.compomics.colims.core.service.UserService;
import com.compomics.colims.model.Project;
import com.compomics.colims.model.User;
import com.compomics.colims.model.comparator.UserNameComparator;
import com.google.common.eventbus.EventBus;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.JOptionPane;
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
@Component("projectEditController")
public class ProjectEditController implements Controllable {

    private static final Logger LOGGER = Logger.getLogger(ProjectEditController.class);
    //model   
    private BindingGroup bindingGroup;
    private ObservableList<User> userBindingList;
    private Project projectToEdit;
    //view
    private ProjectEditDialog projectEditDialog;
    //parent controller
    @Autowired
    private ProjectManagementController projectManagementController;
    @Autowired
    private ColimsController colimsController;
    //services
    @Autowired
    private ProjectService projectService;
    @Autowired
    private UserService userService;
    @Autowired
    private EventBus eventBus;

    public ProjectEditDialog getProjectEditDialog() {
        return projectEditDialog;
    }

    @Override
    public void init() {
        //register to event bus
        eventBus.register(this);

        //init view
        projectEditDialog = new ProjectEditDialog(colimsController.getColimsFrame(), true);

        //init dual list
        projectEditDialog.getUserDualList().init(new UserNameComparator());

        //add binding
        bindingGroup = new BindingGroup();

        userBindingList = ObservableCollections.observableList(userService.findAll());

        JComboBoxBinding ownerComboBoxBinding = SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ_WRITE, userBindingList, projectEditDialog.getOwnerComboBox());
        bindingGroup.addBinding(ownerComboBoxBinding);

        bindingGroup.bind();

        //add action listeners
        projectEditDialog.getUserDualList().addPropertyChangeListener(DualList.CHANGED, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                List<User> addedUsers = (List<User>) evt.getNewValue();

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
                        index = projectManagementController.getSelectedProjectIndex();
                    } else {
                        projectService.save(projectToEdit);

                        //add project to overview table
                        projectManagementController.addProject(projectToEdit);

                        index = projectManagementController.getProjectsSize() - 1;

                        projectEditDialog.getSaveOrUpdateButton().setText("update");
                    }
                    MessageEvent messageEvent = new MessageEvent("project persist confirmation", "Project " + projectToEdit.getLabel() + " was persisted successfully!", JOptionPane.INFORMATION_MESSAGE);
                    eventBus.post(messageEvent);

                    //refresh selection in project list in management overview dialog
                    projectManagementController.setSelectedProject(index);
                } else {
                    MessageEvent messageEvent = new MessageEvent("validation failure", validationMessages, JOptionPane.WARNING_MESSAGE);
                    eventBus.post(messageEvent);
                }
            }
        });

        projectEditDialog.getCloseButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                projectEditDialog.dispose();
            }
        });
    }

    @Override
    public void showView() {
        GuiUtils.centerDialogOnComponent(colimsController.getColimsFrame(), projectEditDialog);
        projectEditDialog.setVisible(true);
    }

    /**
     * Update the project edit dialog with the selected project in the project
     * overview table.
     */
    public void updateView(Project project) {
        projectToEdit = project;

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

        showView();
    }

    /**
     * Update the instance fields of the selected project in the projects table
     */
    private void updateProjectToEdit() {
        projectToEdit.setTitle(projectEditDialog.getTitleTextField().getText());
        projectToEdit.setLabel(projectEditDialog.getLabelTextField().getText());
        projectToEdit.setOwner(userBindingList.get(projectEditDialog.getOwnerComboBox().getSelectedIndex()));
        projectToEdit.setDescription(projectEditDialog.getDescriptionTextArea().getText());
        //the users have been updated by the duallist listener
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
}
