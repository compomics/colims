package com.compomics.colims.client.controller;

import com.compomics.colims.client.event.UserChangeEvent;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.UserManagementDialog;
import com.compomics.colims.core.service.UserService;
import com.compomics.colims.model.Role;
import com.compomics.colims.model.User;
import com.google.common.eventbus.EventBus;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jdesktop.beansbinding.*;
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
@Component("userManagementController")
public class UserManagementController {

    //model
    private ObservableList<User> userBindingList;
    private BindingGroup bindingGroup;
    //view
    private UserManagementDialog userManagementDialog;
    //parent controller
    @Autowired
    private MainController mainController;
    @Autowired
    private EventBus eventBus;
    //services
    @Autowired
    private UserService userService;

    public UserManagementController() {
    }

    public UserManagementDialog getUserManagementDialog() {
        return userManagementDialog;
    }

    public void init() {
        //init view
        userManagementDialog = new UserManagementDialog(mainController.getMainFrame(), true);

        //add roles to comboBox
        for (Role role : userService.findAllRoles()) {
            userManagementDialog.getRoleComboBox().addItem(role);
        }

        //disable save and delete button
        userManagementDialog.getSaveOrUpdateButton().setEnabled(false);
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
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, userManagementDialog.getUserList(), BeanProperty.create("selectedElement.grou"), userManagementDialog.getRoleComboBox(), BeanProperty.create("selectedItem"), "roleBinding");
        
        
        
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
                        if (mainController.getCurrentUser().equals(getSelectedUser())) {
                            userManagementDialog.getDeleteUserButton().setEnabled(false);
                        } else {
                            userManagementDialog.getDeleteUserButton().setEnabled(true);
                        }
                        //enable save button
                        userManagementDialog.getSaveOrUpdateButton().setEnabled(true);
                        //check if the user is found in the db.
                        //If so, disable the name text field and change the save button label.
                        if (isExistingUser(getSelectedUser())) {
                            userManagementDialog.getUserNameTextField().setEnabled(false);
                            userManagementDialog.getSaveOrUpdateButton().setText("update");
                            userManagementDialog.getUserStateInfoLabel().setText("");
                        } else {
                            userManagementDialog.getUserNameTextField().setEnabled(true);
                            userManagementDialog.getSaveOrUpdateButton().setText("save");
                            userManagementDialog.getUserStateInfoLabel().setText("This user hasn't been saved to the database.");
                        }
                    } else {
                        userManagementDialog.getSaveOrUpdateButton().setEnabled(false);
                    }
                }
            }
        });

        userManagementDialog.getAddUserButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                User newUser = new User("name");
                newUser.setUsername(mainController.getCurrentUser().getName());
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

        userManagementDialog.getSaveOrUpdateButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //validate user
                List<String> validationMessages = GuiUtils.validateEntity(getSelectedUser());
                if (validationMessages.isEmpty()) {
                    boolean isExistingUser = isExistingUser(getSelectedUser());

                    User userToSave = getSelectedUser();
                    userService.saveOrUpdate(userToSave);
                    userManagementDialog.getUserStateInfoLabel().setText("");

                    UserChangeEvent.Type type = (isExistingUser) ? UserChangeEvent.Type.UPDATED : UserChangeEvent.Type.CREATED;
                    eventBus.post(new UserChangeEvent(type, userToSave));

                    mainController.showMessageDialog("User save confirmation", "User " + userToSave.getName() + " was saved successfully!", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    mainController.showMessageDialog("Validation failure", validationMessages, JOptionPane.ERROR_MESSAGE);
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
    private boolean isExistingUser(User user) {
        boolean isExistingUser = true;
        User foundUser = userService.findByName(user.getName());
        if (foundUser == null) {
            isExistingUser = false;
        }

        return isExistingUser;
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
