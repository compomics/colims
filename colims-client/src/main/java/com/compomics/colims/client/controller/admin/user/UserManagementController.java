package com.compomics.colims.client.controller.admin.user;

import com.compomics.colims.client.compoment.DualList;
import com.compomics.colims.client.controller.Controllable;
import com.compomics.colims.client.controller.MainController;
import com.compomics.colims.client.event.EntityChangeEvent;
import com.compomics.colims.client.event.admin.GroupChangeEvent;
import com.compomics.colims.client.event.admin.UserChangeEvent;
import com.compomics.colims.client.event.message.DbConstraintMessageEvent;
import com.compomics.colims.client.event.message.DefaultDbEntryMessageEvent;
import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.admin.UserManagementDialog;
import com.compomics.colims.core.service.GroupService;
import com.compomics.colims.core.service.InstitutionService;
import com.compomics.colims.core.service.UserService;
import com.compomics.colims.model.Group;
import com.compomics.colims.model.Institution;
import com.compomics.colims.model.User;
import com.compomics.colims.model.UserBean;
import com.compomics.colims.model.comparator.GroupNameComparator;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.jdesktop.beansbinding.*;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JComboBoxBinding;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.List;

/**
 * The user management view parent controller.
 *
 * @author Niels Hulstaert
 */
@Component("userManagementParentController")
@Lazy
public class UserManagementController implements Controllable {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(MainController.class);

    //model
    private ObservableList<User> userBindingList;
    private ObservableList<Institution> institutionBindingList;
    private BindingGroup bindingGroup;
    private List<Group> availableGroups;
    private boolean areChildrenAffected;
    //view
    private UserManagementDialog userManagementDialog;
    //parent controller
    @Autowired
    private UserManagementParentController userManagementParentController;
    @Autowired
    @Lazy
    private InstitutionManagementController institutionManagementController;
    @Autowired
    private EventBus eventBus;
    @Autowired
    private UserBean userBean;
    //services
    @Autowired
    private UserService userService;
    @Autowired
    private InstitutionService institutionService;
    @Autowired
    private GroupService groupService;

    /**
     * Get the parent controller.
     *
     * @return the UserManagementParentController
     */
    public UserManagementParentController getUserManagementParentController() {
        return userManagementParentController;
    }

    /**
     * Get the institution binding list.
     *
     * @return the institution binding list
     */
    public ObservableList<Institution> getInstitutionBindingList() {
        return institutionBindingList;
    }

    @Override
    @PostConstruct
    public void init() {
        //get view
        userManagementDialog = userManagementParentController.getUserManagementDialog();

        institutionBindingList = ObservableCollections.observableList(institutionService.findAll());

        //init child controller
        institutionManagementController.init();

        //init dual list
        userManagementDialog.getGroupDualList().init(new GroupNameComparator());

        areChildrenAffected = false;

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

        //user bindings
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

        JComboBoxBinding protocolComboBoxBinding = SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ_WRITE, institutionBindingList, userManagementDialog.getInstitutionComboBox());
        bindingGroup.addBinding(protocolComboBoxBinding);

        bindingGroup.bind();

        //add listeners
        userManagementDialog.getUserNameTextField().addFocusListener(new FocusListener() {
            @Override
            public void focusGained(final FocusEvent e) {
            }

            @Override
            public void focusLost(final FocusEvent e) {
                userManagementDialog.getUserList().updateUI();
            }
        });

        userManagementDialog.getUserList().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                if (userManagementDialog.getUserList().getSelectedIndex() != -1) {
                    User selectedUser = getSelectedUser();

                    //check if the selected user is the current user.
                    //If so, disable the delete button
                    if (userBean.getCurrentUser().equals(selectedUser)) {
                        userManagementDialog.getDeleteUserButton().setEnabled(false);
                    } else {
                        userManagementDialog.getDeleteUserButton().setEnabled(true);
                    }

                    //enable save button
                    userManagementDialog.getUserSaveOrUpdateButton().setEnabled(true);

                    //set the selected item in the institution combobox
                    if (selectedUser.getInstitution() != null) {
                        //fetch institution association
                        userService.fetchInstitution(selectedUser);
                        userManagementDialog.getInstitutionComboBox().getModel()
                                .setSelectedItem(selectedUser.getInstitution());
                    }

                    //check if the user is has an ID.
                    //If so, disable the name text field and change the save button label.
                    if (selectedUser.getId() != null) {
                        userManagementDialog.getUserNameTextField().setEnabled(false);
                        userManagementDialog.getUserSaveOrUpdateButton().setText("update");
                        userManagementDialog.getUserStateInfoLabel().setText("");
                    } else {
                        userManagementDialog.getUserNameTextField().setEnabled(true);
                        userManagementDialog.getUserSaveOrUpdateButton().setText("save");
                        userManagementDialog.getUserStateInfoLabel().setText("This user hasn't been stored in the database.");
                    }

                    //populate dual list with groups
                    userManagementDialog.getGroupDualList().populateLists(availableGroups, selectedUser.getGroups());
                } else {
                    userManagementDialog.getUserSaveOrUpdateButton().setEnabled(false);
                }
            }
        });

        userManagementDialog.getAddUserButton().addActionListener(e -> {
            User newUser = new User("name");
            newUser.setUserName(userBean.getCurrentUser().getName());
            userBindingList.add(newUser);
            userManagementDialog.getUserNameTextField().setEnabled(true);
            userManagementDialog.getUserList().setSelectedIndex(userBindingList.size() - 1);
        });

        userManagementDialog.getDeleteUserButton().addActionListener(e -> {
            if (userManagementDialog.getUserList().getSelectedIndex() != -1) {
                User userToDelete = getSelectedUser();
                //check if the user is already has an id.
                //If so, delete the user from the db.
                if (userToDelete.getId() != null) {
                    //check if the user is a default user
                    if (!userService.isDefaultUser(userToDelete)) {
                        try {
                            userService.remove(userToDelete);
                            eventBus.post(new UserChangeEvent(UserChangeEvent.Type.DELETED, true, userToDelete));

                            userBindingList.remove(userManagementDialog.getUserList().getSelectedIndex());
                            resetSelection();
                        } catch (DataIntegrityViolationException dive) {
                            //check if the user can be deleted without breaking existing database relations,
                            //i.e. are there any constraints violations
                            if (dive.getCause() instanceof ConstraintViolationException) {
                                DbConstraintMessageEvent dbConstraintMessageEvent = new DbConstraintMessageEvent("user", userToDelete.getName());
                                eventBus.post(dbConstraintMessageEvent);
                            } else {
                                //pass the exception
                                throw dive;
                            }
                        }
                    } else {
                        DefaultDbEntryMessageEvent defaultDbEntryMessageEvent = new DefaultDbEntryMessageEvent("user", userToDelete.getName());
                        eventBus.post(defaultDbEntryMessageEvent);
                    }
                } else {
                    userBindingList.remove(userManagementDialog.getUserList().getSelectedIndex());
                    resetSelection();
                }
            }
        });

        userManagementDialog.getGroupDualList().addPropertyChangeListener(DualList.CHANGED, evt -> {
            //change groups of the selected user
            List<Group> addedGroups = (List<Group>) evt.getNewValue();

            //add groups to the selected user
            User selectedUser = getSelectedUser();
            selectedUser.setGroups(addedGroups);

            areChildrenAffected = true;
        });

        userManagementDialog.getUserSaveOrUpdateButton().addActionListener(e -> {
            User selectedUser = getSelectedUser();
            //validate user
            List<String> validationMessages = GuiUtils.validateEntity(selectedUser);
            //check for a new user if the user name already exists in the db
            if (selectedUser.getId() == null && isExistingUserName(selectedUser)) {
                validationMessages.add(selectedUser.getName() + " already exists in the database"
                        + System.lineSeparator() + "please choose another user name.");
            }
            if (validationMessages.isEmpty()) {
                Institution selectedInstitution = null;
                if (userManagementDialog.getInstitutionComboBox().getSelectedIndex() != -1) {
                    selectedInstitution = (Institution) userManagementDialog.getInstitutionComboBox().getSelectedItem();
                }

                //save or update the user
                UserChangeEvent.Type type;
                if (selectedUser.getId() != null) {
                    type = EntityChangeEvent.Type.UPDATED;
                    if (selectedInstitution != null && !selectedInstitution.equals(selectedUser.getInstitution())) {
                        selectedUser.setInstitution(selectedInstitution);
                    }
                    selectedUser = userService.merge(selectedUser);
                } else {
                    type = EntityChangeEvent.Type.CREATED;
                    if (selectedInstitution != null) {
                        selectedUser.setInstitution(selectedInstitution);
                    }
                    userService.persist(selectedUser);
                }
                userManagementDialog.getUserNameTextField().setEnabled(false);
                userManagementDialog.getUserSaveOrUpdateButton().setText("update");
                userManagementDialog.getUserStateInfoLabel().setText("");
                
                eventBus.post(new UserChangeEvent(type, areChildrenAffected, selectedUser));

                MessageEvent messageEvent = new MessageEvent("User store confirmation", "User " + selectedUser.getName() + " was stored successfully!", JOptionPane.INFORMATION_MESSAGE);
                eventBus.post(messageEvent);
            } else {
                MessageEvent messageEvent = new MessageEvent("Validation failure", validationMessages, JOptionPane.WARNING_MESSAGE);
                eventBus.post(messageEvent);
            }
        });

        userManagementDialog.getInstitutionManagementButton().addActionListener(e -> institutionManagementController.showView());
    }

    @Override
    public void showView() {
        resetSelection();
    }

    /**
     * Listen to a GroupChangeEvent and update the available groups in the
     * DualList.
     *
     * @param groupChangeEvent the GroupEvent
     */
    @Subscribe
    public void onGroupChangeEvent(final GroupChangeEvent groupChangeEvent) {
        switch (groupChangeEvent.getType()) {
            case CREATED:
            case UPDATED:
                int index = availableGroups.indexOf(groupChangeEvent.getGroup());
                if (index != -1) {
                    availableGroups.set(index, groupChangeEvent.getGroup());
                } else {
                    availableGroups.add(groupChangeEvent.getGroup());
                }
                break;
            case DELETED:
                availableGroups.remove(groupChangeEvent.getGroup());
                //update the user binding list
                userBindingList.clear();
                userBindingList.addAll(userService.findAll());
                break;
            default:
                break;
        }
        resetSelection();
    }

    /**
     * Reload the users from the database after canceling.
     */
    public void onCancel() {
        userBindingList.clear();
        userBindingList.addAll(userService.findAll());

        institutionBindingList.clear();
        institutionBindingList.addAll(institutionService.findAll());
    }

    /**
     * Check if a user with the given user name exists in the database.
     *
     * @param user the selected user
     * @return the does exist boolean
     */
    private boolean isExistingUserName(final User user) {
        User foundUser = userService.findByName(user.getName());

        return foundUser != null;
    }

    /**
     * Get the selected user in the user JList.
     *
     * @return the selected user
     */
    private User getSelectedUser() {
        int selectedUserIndex = userManagementDialog.getUserList().getSelectedIndex();
        return (selectedUserIndex != -1) ? userBindingList.get(selectedUserIndex) : null;
    }

    /**
     * Reset the selection in the user list.
     */
    private void resetSelection() {
        //clear selection
        userManagementDialog.getUserList().getSelectionModel().clearSelection();
        if (!userBindingList.isEmpty()) {
            userManagementDialog.getUserList().setSelectedIndex(0);
        }
    }
}
