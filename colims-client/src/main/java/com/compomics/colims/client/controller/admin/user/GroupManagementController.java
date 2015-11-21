package com.compomics.colims.client.controller.admin.user;

import com.compomics.colims.client.compoment.DualList;
import com.compomics.colims.client.controller.Controllable;
import com.compomics.colims.client.event.EntityChangeEvent;
import com.compomics.colims.client.event.admin.GroupChangeEvent;
import com.compomics.colims.client.event.admin.RoleChangeEvent;
import com.compomics.colims.client.event.admin.UserChangeEvent;
import com.compomics.colims.client.event.message.DbConstraintMessageEvent;
import com.compomics.colims.client.event.message.DefaultDbEntryMessageEvent;
import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.admin.UserManagementDialog;
import com.compomics.colims.core.service.GroupService;
import com.compomics.colims.core.service.RoleService;
import com.compomics.colims.model.Group;
import com.compomics.colims.model.Role;
import com.compomics.colims.model.comparator.RoleNameComparator;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.hibernate.exception.ConstraintViolationException;
import org.jdesktop.beansbinding.*;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
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
 * @author Niels Hulstaert
 */
@Component("groupManagementController")
@Lazy
public class GroupManagementController implements Controllable {

    //model
    /**
     * Group binding list.
     */
    private ObservableList<Group> groupBindingList;
    /**
     * The binding group.
     */
    private BindingGroup bindingGroup;
    /**
     * The list of available roles.
     */
    private List<Role> availableRoles;
    /**
     * Boolean that keeps track wether children are affected or not.
     */
    private boolean areChildrenAffected;
    //view
    private UserManagementDialog userManagementDialog;
    //parent controller
    @Autowired
    private UserManagementParentController userManagementController;
    @Autowired
    private EventBus eventBus;
    //services
    @Autowired
    private GroupService groupService;
    @Autowired
    private RoleService roleService;

    @Override
    @PostConstruct
    public void init() {
        //get view
        userManagementDialog = userManagementController.getUserManagementDialog();

        //init dual list
        userManagementDialog.getRoleDualList().init(new RoleNameComparator());

        areChildrenAffected = false;

        //register to event bus
        eventBus.register(this);

        //load available roles
        availableRoles = roleService.findAll();

        //disable save and delete button
        userManagementDialog.getGroupSaveOrUpdateButton().setEnabled(false);
        userManagementDialog.getDeleteGroupButton().setEnabled(false);

        //init binding
        bindingGroup = new BindingGroup();

        groupBindingList = ObservableCollections.observableList(groupService.findAll());
        JListBinding groupListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, groupBindingList, userManagementDialog.getGroupList());
        bindingGroup.addBinding(groupListBinding);

        //group bindings
        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, userManagementDialog.getGroupList(), BeanProperty.create("selectedElement.name"), userManagementDialog.getGroupNameTextField(), ELProperty.create("${text}"), "nameBinding");
        bindingGroup.addBinding(binding);
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, userManagementDialog.getGroupList(), BeanProperty.create("selectedElement.description"), userManagementDialog.getGroupDescriptionTextArea(), ELProperty.create("${text}"), "descriptionBinding");
        bindingGroup.addBinding(binding);

        bindingGroup.bind();

        //add listeners
        userManagementDialog.getGroupNameTextField().addFocusListener(new FocusListener() {
            @Override
            public void focusGained(final FocusEvent e) {
            }

            @Override
            public void focusLost(final FocusEvent e) {
                userManagementDialog.getGroupList().updateUI();
            }
        });

        userManagementDialog.getGroupList().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                if (userManagementDialog.getGroupList().getSelectedIndex() != -1) {
                    Group selectedGroup = getSelectedGroup();

                    //enable save and delete button
                    userManagementDialog.getGroupSaveOrUpdateButton().setEnabled(true);
                    userManagementDialog.getDeleteGroupButton().setEnabled(true);

                    //check if the group has an ID.
                    //If so, disable the name text field and change the save button label.
                    if (selectedGroup.getId() != null) {
                        userManagementDialog.getGroupNameTextField().setEnabled(false);
                        userManagementDialog.getGroupSaveOrUpdateButton().setText("update");
                        userManagementDialog.getGroupStateInfoLabel().setText("");
                    } else {
                        userManagementDialog.getGroupNameTextField().setEnabled(true);
                        userManagementDialog.getGroupSaveOrUpdateButton().setText("save");
                        userManagementDialog.getGroupStateInfoLabel().setText("This group hasn't been stored in the database.");
                    }

                    //populate dual list with roles
                    userManagementDialog.getRoleDualList().populateLists(availableRoles, selectedGroup.getRoles());
                } else {
                    userManagementDialog.getGroupSaveOrUpdateButton().setEnabled(false);
                }
            }
        });

        userManagementDialog.getAddGroupButton().addActionListener(e -> {
            Group newGroup = new Group("name");
            groupBindingList.add(newGroup);
            userManagementDialog.getGroupNameTextField().setEnabled(true);
            userManagementDialog.getGroupList().setSelectedIndex(groupBindingList.size() - 1);
        });

        userManagementDialog.getDeleteGroupButton().addActionListener(e -> {
            if (userManagementDialog.getGroupList().getSelectedIndex() != -1) {
                Group groupToDelete = getSelectedGroup();
                //check if group is already has an id.
                //If so, delete the group from the db.
                if (groupToDelete.getId() != null) {
                    //check if the group is a default group
                    if (!groupService.isDefaultGroup(groupToDelete)) {
                        try {
                            groupService.remove(groupToDelete);
                            eventBus.post(new GroupChangeEvent(EntityChangeEvent.Type.DELETED, true, groupToDelete));

                            groupBindingList.remove(userManagementDialog.getGroupList().getSelectedIndex());
                            resetSelection();
                        } catch (DataIntegrityViolationException dive) {
                            //check if the group can be deleted without breaking existing database relations,
                            //i.e. are there any constraints violations
                            if (dive.getCause() instanceof ConstraintViolationException) {
                                DbConstraintMessageEvent dbConstraintMessageEvent = new DbConstraintMessageEvent("group", groupToDelete.getName());
                                eventBus.post(dbConstraintMessageEvent);
                            } else {
                                //pass the exception
                                throw dive;
                            }
                        }
                    } else {
                        DefaultDbEntryMessageEvent defaultDbEntryMessageEvent = new DefaultDbEntryMessageEvent("group", groupToDelete.getName());
                        eventBus.post(defaultDbEntryMessageEvent);
                    }
                } else {
                    groupBindingList.remove(userManagementDialog.getGroupList().getSelectedIndex());
                    resetSelection();
                }
            }
        });

        userManagementDialog.getRoleDualList().addPropertyChangeListener(DualList.CHANGED, evt -> {
            //change roles of the selected group
            List<Role> addedRoles = (List<Role>) evt.getNewValue();

            //add roles to the selected group
            Group selectedGroup = getSelectedGroup();
            selectedGroup.setRoles(addedRoles);

            areChildrenAffected = true;
        });

        userManagementDialog.getGroupSaveOrUpdateButton().addActionListener(e -> {
            Group selectedGroup = getSelectedGroup();
            //validate group
            List<String> validationMessages = GuiUtils.validateEntity(selectedGroup);
            //check for a new group if the group name already exists in the db
            if (selectedGroup.getId() == null && isExistingGroupName(selectedGroup)) {
                validationMessages.add(selectedGroup.getName() + " already exists in the database,"
                        + System.lineSeparator() + "please choose another group name.");
            }
            if (validationMessages.isEmpty()) {
                if (selectedGroup.getId() != null) {
                    selectedGroup = groupService.merge(selectedGroup);
                } else {
                    groupService.persist(selectedGroup);
                }
                userManagementDialog.getGroupNameTextField().setEnabled(false);
                userManagementDialog.getGroupSaveOrUpdateButton().setText("update");
                userManagementDialog.getGroupStateInfoLabel().setText("");

                EntityChangeEvent.Type type = (selectedGroup.getId() == null) ? EntityChangeEvent.Type.CREATED : EntityChangeEvent.Type.UPDATED;
                eventBus.post(new GroupChangeEvent(type, areChildrenAffected, selectedGroup));

                MessageEvent messageEvent = new MessageEvent("Group store confirmation", "Group " + selectedGroup.getName() + " was stored successfully!", JOptionPane.INFORMATION_MESSAGE);
                eventBus.post(messageEvent);
            } else {
                MessageEvent messageEvent = new MessageEvent("Validation failure", validationMessages, JOptionPane.WARNING_MESSAGE);
                eventBus.post(messageEvent);
            }
        });
    }

    @Override
    public void showView() {
        resetSelection();
    }

    /**
     * Listen to a UserChangeEvent and update the groups if necessary.
     *
     * @param userChangeEvent the UserChangeEvent
     */
    @Subscribe
    public void onUserChangeEvent(final UserChangeEvent userChangeEvent) {
        if (userChangeEvent.areChildrenAffected()) {
            groupBindingList.clear();
            groupBindingList.addAll(groupService.findAll());
        }
    }

    /**
     * Listen to a RoleChangeEvent and update the available roles in the DualList.
     *
     * @param roleChangeEvent the RoleChangeEvent
     */
    @Subscribe
    public void onRoleChangeEvent(final RoleChangeEvent roleChangeEvent) {
        switch (roleChangeEvent.getType()) {
            case CREATED:
            case UPDATED:
                int index = availableRoles.indexOf(roleChangeEvent.getRole());
                if (index != -1) {
                    availableRoles.set(index, roleChangeEvent.getRole());
                } else {
                    availableRoles.add(roleChangeEvent.getRole());
                }
                break;
            case DELETED:
                availableRoles.remove(roleChangeEvent.getRole());
                //update the group binding list
                groupBindingList.clear();
                groupBindingList.addAll(groupService.findAll());
                break;
            default:
                break;
        }
        resetSelection();
    }

    /**
     * Reload the groups from the database after canceling.
     */
    public void onCancel() {
        groupBindingList.clear();
        groupBindingList.addAll(groupService.findAll());
    }

    /**
     * Check if a group with the given group name exists in the database.
     *
     * @param group the selected group
     * @return does the group name exist
     */
    private boolean isExistingGroupName(final Group group) {
        boolean isExistingGroupName = true;
        Group foundGroup = groupService.findByName(group.getName());
        if (foundGroup == null) {
            isExistingGroupName = false;
        }

        return isExistingGroupName;
    }

    /**
     * Get the selected group in the group JList.
     *
     * @return the selected group
     */
    private Group getSelectedGroup() {
        int selectedGroupIndex = userManagementDialog.getGroupList().getSelectedIndex();
        return (selectedGroupIndex != -1) ? groupBindingList.get(selectedGroupIndex) : null;
    }

    /**
     * Reset the selection in the group list.
     */
    private void resetSelection() {
        //clear selection
        userManagementDialog.getGroupList().getSelectionModel().clearSelection();
        if (!groupBindingList.isEmpty()) {
            userManagementDialog.getGroupList().setSelectedIndex(0);
        }
    }
}
