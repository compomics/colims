package com.compomics.colims.client.controller.admin.user;

import com.compomics.colims.client.compoment.DualList;
import com.compomics.colims.client.controller.Controllable;
import com.compomics.colims.client.controller.ColimsController;
import com.compomics.colims.client.event.DbConstraintMessageEvent;
import static com.compomics.colims.client.event.EntityChangeEvent.Type.CREATED;
import static com.compomics.colims.client.event.EntityChangeEvent.Type.DELETED;
import static com.compomics.colims.client.event.EntityChangeEvent.Type.UPDATED;
import com.compomics.colims.client.event.GroupChangeEvent;
import com.compomics.colims.client.event.MessageEvent;
import com.compomics.colims.client.event.UserChangeEvent;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.admin.UserManagementDialog;
import com.compomics.colims.core.service.GroupService;
import com.compomics.colims.core.service.UserService;
import com.compomics.colims.model.Group;
import com.compomics.colims.model.User;
import com.compomics.colims.model.comparator.GroupNameComparator;
import com.compomics.colims.repository.AuthenticationBean;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("userCrudController")
public class UserCrudController implements Controllable {

    private static final Logger LOGGER = Logger.getLogger(ColimsController.class);
    //model
    private ObservableList<User> userBindingList;
    private BindingGroup bindingGroup;
    private List<Group> availableGroups;
    private boolean areChildrenAffected;
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

    @Override
    public void init() {
        //get view
        userManagementDialog = userManagementController.getUserManagementDialog();

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
                    if (userManagementDialog.getUserList().getSelectedIndex() != -1) {
                        User selectedUser = getSelectedUser();

                        //check if the selected user is the current user.
                        //If so, disable the delete button
                        if (authenticationBean.getCurrentUser().equals(selectedUser)) {
                            userManagementDialog.getDeleteUserButton().setEnabled(false);
                        } else {
                            userManagementDialog.getDeleteUserButton().setEnabled(true);
                        }

                        //enable save button
                        userManagementDialog.getUserSaveOrUpdateButton().setEnabled(true);

                        //check if the user is has an ID.
                        //If so, disable the name text field and change the save button label.
                        if (selectedUser.getId() != null) {
                            userService.fetchAuthenticationRelations(selectedUser);

                            userManagementDialog.getUserNameTextField().setEnabled(false);
                            userManagementDialog.getUserSaveOrUpdateButton().setText("update");
                            userManagementDialog.getUserStateInfoLabel().setText("");
                        } else {
                            userManagementDialog.getUserNameTextField().setEnabled(true);
                            userManagementDialog.getUserSaveOrUpdateButton().setText("save");
                            userManagementDialog.getUserStateInfoLabel().setText("This user hasn't been persisted to the database.");
                        }

                        //populate dual list with groups                        
                        userManagementDialog.getGroupDualList().populateLists(availableGroups, selectedUser.getGroups());
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
                newUser.setUserName(authenticationBean.getCurrentUser().getName());
                userBindingList.add(newUser);
                userManagementDialog.getUserNameTextField().setEnabled(true);
                userManagementDialog.getUserList().setSelectedIndex(userBindingList.size() - 1);
            }
        });

        userManagementDialog.getDeleteUserButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (userManagementDialog.getUserList().getSelectedIndex() != -1) {
                    User userToDelete = getSelectedUser();
                    //check if the user is already has an id.
                    //If so, delete the user from the db.
                    if (userToDelete.getId() != null) {
                        try {
                            userService.delete(userToDelete);
                            eventBus.post(new UserChangeEvent(UserChangeEvent.Type.DELETED, true, userToDelete));

                            userBindingList.remove(userManagementDialog.getUserList().getSelectedIndex());
                            userManagementDialog.getUserList().getSelectionModel().clearSelection();
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
                        userBindingList.remove(userManagementDialog.getUserList().getSelectedIndex());
                        resetSelection();
                    }
                }
            }
        });

        userManagementDialog.getGroupDualList().addPropertyChangeListener(DualList.CHANGED, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                //change groups of the selected user                                    
                List<Group> addedGroups = userManagementDialog.getGroupDualList().getAddedItems();

                //add groups to the selected user
                User selectedUser = getSelectedUser();
                selectedUser.setGroups(addedGroups);

                areChildrenAffected = true;
            }
        });

        userManagementDialog.getUserSaveOrUpdateButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                User selectedUser = getSelectedUser();
                //validate user
                List<String> validationMessages = GuiUtils.validateEntity(selectedUser);
                //check for a new user if the user name already exists in the db                
                if (selectedUser.getId() == null && isExistingUserName(selectedUser)) {
                    validationMessages.add(selectedUser.getName() + " already exists in the database"
                            + "\n" + "please choose another user name.");
                }
                if (validationMessages.isEmpty()) {
                    if (selectedUser.getId() != null) {
                        userService.update(selectedUser);
                    } else {
                        userService.save(selectedUser);
                    }
                    userManagementDialog.getUserNameTextField().setEnabled(false);
                    userManagementDialog.getUserSaveOrUpdateButton().setText("update");
                    userManagementDialog.getUserStateInfoLabel().setText("");

                    UserChangeEvent.Type type = (selectedUser.getId() == null) ? UserChangeEvent.Type.CREATED : UserChangeEvent.Type.UPDATED;
                    eventBus.post(new UserChangeEvent(type, areChildrenAffected, selectedUser));

                    MessageEvent messageEvent = new MessageEvent("user persist confirmation", "User " + selectedUser.getName() + " was persisted successfully!", JOptionPane.INFORMATION_MESSAGE);
                    eventBus.post(messageEvent);
                } else {
                    MessageEvent messageEvent = new MessageEvent("validation failure", validationMessages, JOptionPane.WARNING_MESSAGE);
                    eventBus.post(messageEvent);
                }
            }
        });
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
    public void onGroupChangeEvent(GroupChangeEvent groupChangeEvent) {
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
     * Check if the CV term with the given user name exists in the database
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
     * Get the selected user in the user JList.
     *
     * @return the selected user
     */
    private User getSelectedUser() {
        int selectedUserIndex = userManagementDialog.getUserList().getSelectedIndex();
        User selectedUser = (selectedUserIndex != -1) ? userBindingList.get(selectedUserIndex) : null;
        return selectedUser;
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
