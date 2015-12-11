package com.compomics.colims.client.controller;

import com.compomics.colims.client.compoment.DualList;
import com.compomics.colims.client.event.EntityChangeEvent;
import com.compomics.colims.client.event.ProjectChangeEvent;
import com.compomics.colims.client.event.admin.UserChangeEvent;
import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.ProjectEditDialog;
import com.compomics.colims.core.service.ProjectService;
import com.compomics.colims.core.service.UserService;
import com.compomics.colims.model.Project;
import com.compomics.colims.model.User;
import com.compomics.colims.model.comparator.UserNameComparator;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.apache.log4j.Logger;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JComboBoxBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.util.List;

/**
 * The project edit view controller.
 *
 * @author Niels Hulstaert
 */
@Component("projectEditController")
@Lazy
public class ProjectEditController implements Controllable {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(ProjectEditController.class);

    //model
    private BindingGroup bindingGroup;
    private ObservableList<User> userBindingList;
    private List<User> users;
    private Project projectToEdit;
    //view
    private ProjectEditDialog projectEditDialog;
    //parent controller
    @Autowired
    private ProjectManagementController projectManagementController;
    @Autowired
    private MainController mainController;
    //services
    @Autowired
    private ProjectService projectService;
    @Autowired
    private UserService userService;
    @Autowired
    private EventBus eventBus;

    /**
     * Get the view of this controller.
     *
     * @return the ProjectEditDialog
     */
    public ProjectEditDialog getProjectEditDialog() {
        return projectEditDialog;
    }

    @Override
    @PostConstruct
    public void init() {
        //register to event bus
        eventBus.register(this);

        //init view
        projectEditDialog = new ProjectEditDialog(mainController.getMainFrame(), true);

        //init dual list
        users = userService.findAll();
        projectEditDialog.getUserDualList().init(new UserNameComparator());

        //add binding
        bindingGroup = new BindingGroup();

        userBindingList = ObservableCollections.observableList(users);

        JComboBoxBinding ownerComboBoxBinding = SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ_WRITE, userBindingList, projectEditDialog.getOwnerComboBox());
        bindingGroup.addBinding(ownerComboBoxBinding);

        bindingGroup.bind();

        //add action listeners
        projectEditDialog.getUserDualList().addPropertyChangeListener(DualList.CHANGED, evt -> {
            List<User> addedUsers = (List<User>) evt.getNewValue();

            projectToEdit.setUsers(addedUsers);
        });

        projectEditDialog.getSaveOrUpdateButton().addActionListener(e -> {
            //update projectToEdit with dialog input
            updateProjectToEdit();

            //validate project
            List<String> validationMessages = GuiUtils.validateEntity(projectToEdit);
            //check for a new project if the project title already exists in the db
            if (projectToEdit.getId() == null && isExistingProjectTitle(projectToEdit)) {
                validationMessages.add(projectToEdit.getTitle() + " already exists in the database,"
                        + System.lineSeparator() + "please choose another project title.");
            }
            if (validationMessages.isEmpty()) {
                int index;
                EntityChangeEvent.Type type;

                if (projectToEdit.getId() != null) {
                    projectService.merge(projectToEdit);

                    index = projectManagementController.getSelectedProjectIndex();
                    type = EntityChangeEvent.Type.UPDATED;
                } else {
                    projectService.persist(projectToEdit);

                    index = projectManagementController.getProjectsSize() - 1;
                    type = EntityChangeEvent.Type.CREATED;

                    //add project to overview table
                    projectManagementController.addProject(projectToEdit);

                    projectEditDialog.getSaveOrUpdateButton().setText("update");
                }
                eventBus.post(new ProjectChangeEvent(type, projectToEdit.getId()));

                MessageEvent messageEvent = new MessageEvent("Project store confirmation", "Project " + projectToEdit.getLabel() + " was stored successfully!", JOptionPane.INFORMATION_MESSAGE);
                eventBus.post(messageEvent);

                //refresh selection in project list in management overview dialog
                projectManagementController.setSelectedProject(index);
            } else {
                MessageEvent messageEvent = new MessageEvent("Validation failure", validationMessages, JOptionPane.WARNING_MESSAGE);
                eventBus.post(messageEvent);
            }
        });

        projectEditDialog.getCancelButton().addActionListener(e -> projectEditDialog.dispose());
    }

    @Override
    public void showView() {
        GuiUtils.centerDialogOnComponent(mainController.getMainFrame(), projectEditDialog);
        projectEditDialog.setVisible(true);
    }

    /**
     * Update the project edit dialog with the selected project in the project overview table.
     *
     * @param project the Project instance
     */
    public void updateView(final Project project) {
        projectToEdit = project;

        if (projectToEdit.getId() != null) {
            projectEditDialog.getSaveOrUpdateButton().setText("update");
        } else {
            projectEditDialog.getSaveOrUpdateButton().setText("save");
        }

        projectEditDialog.getTitleTextField().setText(projectToEdit.getTitle());
        projectEditDialog.getLabelTextField().setText(projectToEdit.getLabel());
        //set the selected item in the owner combobox
        projectEditDialog.getOwnerComboBox().getModel().setSelectedItem(projectToEdit.getOwner());
        projectEditDialog.getDescriptionTextArea().setText(projectToEdit.getDescription());
        //populate user dual list
        projectService.fetchUsers(projectToEdit);
        projectEditDialog.getUserDualList().populateLists(users, projectToEdit.getUsers());

        showView();
    }

    /**
     * Listen to a UserChangeEvent and reload the users.
     *
     * @param userChangeEvent the UserChangeEvent
     */
    @Subscribe
    public void onUserChangeEvent(final UserChangeEvent userChangeEvent) {
        users.clear();
        users.addAll(userService.findAll());
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
    private boolean isExistingProjectTitle(final Project project) {
        Long count = projectService.countByTitle(project.getTitle());

        return count != 0;
    }
}
