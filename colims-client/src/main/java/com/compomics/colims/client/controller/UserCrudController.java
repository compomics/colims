package com.compomics.colims.client.controller;

import com.compomics.colims.client.bean.AuthenticationBean;
import com.compomics.colims.client.event.MessageEvent;
import com.compomics.colims.client.event.UserChangeEvent;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.UserManagementDialog;
import com.compomics.colims.core.service.GroupService;
import com.compomics.colims.core.service.UserService;
import com.compomics.colims.model.Group;
import com.compomics.colims.model.Role;
import com.compomics.colims.model.User;
import com.compomics.colims.model.UserHasGroup;
import com.google.common.eventbus.EventBus;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ELProperty;
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
@Component("userCrudController")
public class UserCrudController {

    //model
    private ObservableList<User> userBindingList;
    private BindingGroup bindingGroup;
    private List<Group> availableGroups;
    //view
    private UserManagementDialog userManagementDialog;
    //parent controller
    @Autowired
    private UserManagementController userManagementController;
    @Autowired
    private EventBus eventBus;
    @Autowired
    private AuthenticationBean authenticationBean;
    //services
    @Autowired
    private UserService userService;
    @Autowired
    private GroupService groupService;

    public void init() {
        //init view
        userManagementDialog = userManagementController.getUserManagementDialog();

        //register to event bus
        eventBus.register(this);

        //load available groups
        availableGroups = groupService.findAll();

        //disable save and delete button
        userManagementDialog.getUserSaveOrUpdateButton().setEnabled(false);
        userManagementDialog.getDeleteUserButton().setEnabled(false);

        //init binding
        bindingGroup = new BindingGroup();

        userBindingList = ObservableCollections.observableList(userService.findAll());
        JListBinding userListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, userBindingList, userManagementDialog.getUserList());
        bindingGroup.addBinding(userListBinding);

        //user bindingd
        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, userManagementDialog.getUserList(), BeanProperty.create("selectedElement.name"), userManagementDialog.getUserNameTextField(), ELProperty.create("${text}"), "nameBinding");
        bindingGroup.addBinding(binding);
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, userManagementDialog.getUserList(), BeanProperty.create("selectedElement.firstName"), userManagementDialog.getFirstNameTextField(), ELProperty.create("${text}"), "firstNameBinding");
        bindingGroup.addBinding(binding);
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, userManagementDialog.getUserList(), BeanProperty.create("selectedElement.lastName"), userManagementDialog.getLastNameTextField(), ELProperty.create("${text}"), "lastNameBinding");
        bindingGroup.addBinding(binding);
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, userManagementDialog.getUserList(), BeanProperty.create("selectedElement.email"), userManagementDialog.getEmailTextField(), ELProperty.create("${text}"), "emailBinding");
        bindingGroup.addBinding(binding);
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, userManagementDialog.getUserList(), BeanProperty.create("selectedElement.password"), userManagementDialog.getPasswordTextField(), ELProperty.create("${text}"), "passwordBinding");
        bindingGroup.addBinding(binding);

        bindingGroup.bind();

        //add listeners
        userManagementDialog.getUserNameTextField().addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                userManagementDialog.getUserList().updateUI();
            }
        });

        userManagementDialog.getUserList().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    if (getSelectedUserIndex() != -1) {
                        //check if the selected user is the current user.
                        //If so, disable the delete button
                        if (authenticationBean.getCurrentUser().equals(getSelectedUser())) {
                            userManagementDialog.getDeleteUserButton().setEnabled(false);
                        } else {
                            userManagementDialog.getDeleteUserButton().setEnabled(true);
                        }

                        //enable save button
                        userManagementDialog.getUserSaveOrUpdateButton().setEnabled(true);

                        //check if the user is found in the db.
                        //If so, disable the name text field and change the save button label.
                        if (isExistingUserName(getSelectedUser())) {
                            userService.fetchAuthenticationRelations(getSelectedUser());

                            userManagementDialog.getUserNameTextField().setEnabled(false);
                            userManagementDialog.getUserSaveOrUpdateButton().setText("update");
                            userManagementDialog.getUserStateInfoLabel().setText("");
                        } else {
                            userManagementDialog.getUserNameTextField().setEnabled(true);
                            userManagementDialog.getUserSaveOrUpdateButton().setText("save");
                            userManagementDialog.getUserStateInfoLabel().setText("This user hasn't been saved to the database.");
                        }

                        //populate dual list with groups                        
                        userManagementDialog.getGroupDualList().populateLists(availableGroups, getSelectedUser().getGroups());
                    } else {
                        userManagementDialog.getUserSaveOrUpdateButton().setEnabled(false);
                    }
                }
            }
        });

        userManagementDialog.getAddUserButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                User newUser = new User("name");
                newUser.setUsername(authenticationBean.getCurrentUser().getName());
                userBindingList.add(newUser);
                userManagementDialog.getUserNameTextField().setEnabled(true);
                userManagementDialog.getUserList().setSelectedIndex(userBindingList.size() - 1);
            }
        });

        userManagementDialog.getDeleteUserButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (getSelectedUserIndex() != -1) {
                    User userToDelete = getSelectedUser();
                    //check if user is already has an id.
                    //If so, delete the user from the db.
                    if (userToDelete.getId() != null) {
                        userService.delete(userToDelete);
                        eventBus.post(new UserChangeEvent(UserChangeEvent.Type.DELETED, userToDelete));
                    }
                    userBindingList.remove(getSelectedUserIndex());
                    userManagementDialog.getUserList().setSelectedIndex(userBindingList.size() - 1);
                }
            }
        });

        userManagementDialog.getUserSaveOrUpdateButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //validate user
                List<String> validationMessages = GuiUtils.validateEntity(getSelectedUser());
                if (validationMessages.isEmpty()) {
                    User userToSaveOrUpdate = getSelectedUser();
                    //boolean isExistingUserName = isExistingUserName(userToSaveOrUpdate);

                    //if modified, add groups to user
                    List<Group> addedGroups = userManagementDialog.getGroupDualList().getAddedItems();
                    List<Group> currentGroups = getSelectedUser().getGroups();
                    for (Group addedGroup : addedGroups) {
                        if (!currentGroups.contains(addedGroup)) {
                            UserHasGroup userHasGroup = new UserHasGroup();
                            userHasGroup.setGroup(addedGroup);
                            userHasGroup.setUser(getSelectedUser());

                            //userHasGroup.setCreationdate(new Date());
                            //userHasGroup.setModificationdate(new Date());
                            //userHasGroup.setUsername("test");

                            userToSaveOrUpdate.getUserHasGroups().add(userHasGroup);
                        }
                    }

                    if (userToSaveOrUpdate.getId() == null) {
                        userService.save(userToSaveOrUpdate);
                    } else {
                        userService.update(userToSaveOrUpdate);
                    }
                    userManagementDialog.getUserStateInfoLabel().setText("");

                    UserChangeEvent.Type type = (userToSaveOrUpdate.getId() == null) ? UserChangeEvent.Type.CREATED : UserChangeEvent.Type.UPDATED;
                    eventBus.post(new UserChangeEvent(type, userToSaveOrUpdate));

                    MessageEvent messageEvent = new MessageEvent("User save confirmation", "User " + userToSaveOrUpdate.getName() + " was saved successfully!", JOptionPane.INFORMATION_MESSAGE);
                    eventBus.post(messageEvent);
                } else {
                    MessageEvent messageEvent = new MessageEvent("Validation failure", validationMessages, JOptionPane.ERROR_MESSAGE);
                    eventBus.post(messageEvent);
                }
            }
        });
    }

    /**
     * Checks if the user with the given user name exists in the database
     *
     * @param user the selected user
     * @return the does exist boolean
     */
    private boolean isExistingUserName(User user) {
        boolean isExistingUserName = true;
        User foundUser = userService.findByName(user.getName());
        if (foundUser == null) {
            isExistingUserName = false;
        }

        return isExistingUserName;
    }

    /**
     * Gets the selected user in the user JList.
     *
     * @return the selected user
     */
    private User getSelectedUser() {
        User selectedUser = (getSelectedUserIndex() != -1) ? userBindingList.get(getSelectedUserIndex()) : null;
        return selectedUser;
    }

    /**
     * Gets the selected user index in the user JList.
     *
     * @return the selected index
     */
    private int getSelectedUserIndex() {
        return userManagementDialog.getUserList().getSelectedIndex();
    }
}
