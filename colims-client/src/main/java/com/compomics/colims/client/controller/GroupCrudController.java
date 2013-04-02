package com.compomics.colims.client.controller;

import com.compomics.colims.client.event.EntityChangeEvent;
import static com.compomics.colims.client.event.EntityChangeEvent.Type.CREATED;
import static com.compomics.colims.client.event.EntityChangeEvent.Type.DELETED;
import static com.compomics.colims.client.event.EntityChangeEvent.Type.UPDATED;
import com.compomics.colims.client.event.GroupChangeEvent;
import com.compomics.colims.client.event.MessageEvent;
import com.compomics.colims.client.event.RoleChangeEvent;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.UserManagementDialog;
import com.compomics.colims.core.service.GroupService;
import com.compomics.colims.core.service.RoleService;
import com.compomics.colims.model.Group;
import com.compomics.colims.model.Role;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.List;
import javax.swing.JOptionPane;
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
@Component("groupCrudController")
public class GroupCrudController {

    //model
    private ObservableList<Group> groupBindingList;
    private BindingGroup bindingGroup;
    private List<Role> availableRoles;
    //view
    private UserManagementDialog userManagementDialog;
    //parent controller
    @Autowired
    private UserManagementController userManagementController;
    @Autowired
    private EventBus eventBus;
    //services
    @Autowired
    private GroupService groupService;
    @Autowired
    private RoleService roleService;
    
    /**
     * Listen to a RoleChangeEvent and update the available roles in the
     * DualList.
     *
     * @param roleChangeEvent the RoleChangeEvent
     */
    @Subscribe
    public void onRoleChangeEvent(RoleChangeEvent roleChangeEvent) {
        switch (roleChangeEvent.getType()) {
            case CREATED:
            case UPDATED:
                int index = availableRoles.indexOf(roleChangeEvent.getRole());
                if (index != -1) {
                    availableRoles.set(index, roleChangeEvent.getRole());
                }
                else{
                    
                }
            case DELETED:
                availableRoles.remove(roleChangeEvent.getRole());
            default:
        }
    }

    public void init() {
        //get view
        userManagementDialog = userManagementController.getUserManagementDialog();

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
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                userManagementDialog.getGroupList().updateUI();
            }
        });

        userManagementDialog.getGroupList().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    if (getSelectedGroupIndex() != -1) {
                        Group selectedGroup = getSelectedGroup();

                        //enable save and delete button
                        userManagementDialog.getGroupSaveOrUpdateButton().setEnabled(true);
                        userManagementDialog.getDeleteGroupButton().setEnabled(true);

                        //check if the group is found in the db.
                        //If so, disable the name text field and change the save button label.
                        if (isExistingGroupName(selectedGroup)) {
                            //@todo see if we need to fetch the relations
                            //groupService.fetchAuthenticationRelations(selectedGroup);

                            userManagementDialog.getGroupNameTextField().setEnabled(false);
                            userManagementDialog.getGroupSaveOrUpdateButton().setText("update");
                            userManagementDialog.getGroupStateInfoLabel().setText("");
                        } else {
                            userManagementDialog.getGroupNameTextField().setEnabled(true);
                            userManagementDialog.getGroupSaveOrUpdateButton().setText("save");
                            userManagementDialog.getGroupStateInfoLabel().setText("This group hasn't been saved to the database.");
                        }

                        //populate dual list with roles                        
                        userManagementDialog.getRoleDualList().populateLists(availableRoles, selectedGroup.getRoles());
                    } else {
                        userManagementDialog.getGroupSaveOrUpdateButton().setEnabled(false);
                    }
                }
            }
        });

        userManagementDialog.getAddGroupButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Group newGroup = new Group("name");
                groupBindingList.add(newGroup);
                userManagementDialog.getGroupNameTextField().setEnabled(true);
                userManagementDialog.getGroupList().setSelectedIndex(groupBindingList.size() - 1);
            }
        });

        userManagementDialog.getDeleteGroupButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (getSelectedGroupIndex() != -1) {
                    Group groupToDelete = getSelectedGroup();
                    //check if group is already has an id.
                    //If so, delete the group from the db.
                    if (groupToDelete.getId() != null) {
                        groupService.delete(groupToDelete);
                        eventBus.post(new GroupChangeEvent(EntityChangeEvent.Type.DELETED, groupToDelete));
                    }
                    groupBindingList.remove(getSelectedGroupIndex());
                    userManagementDialog.getGroupList().setSelectedIndex(groupBindingList.size() - 1);
                }
            }
        });

        userManagementDialog.getGroupSaveOrUpdateButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Group selectedGroup = getSelectedGroup();
                //validate group
                List<String> validationMessages = GuiUtils.validateEntity(getSelectedGroup());
                //check for a new group if the group name already exists in the db                
                if (!isExistingGroup(selectedGroup) && isExistingGroupName(selectedGroup)) {
                    validationMessages.add(selectedGroup.getName() + " already exists in the database, please choose another group name.");
                }
                if (validationMessages.isEmpty()) {
                    List<Role> addedRoles = userManagementDialog.getRoleDualList().getAddedItems();

                    if (isExistingGroup(selectedGroup)) {
                        groupService.updateGroup(selectedGroup, addedRoles);
                    } else {
                        groupService.saveGroup(selectedGroup, addedRoles);
                    }
                    userManagementDialog.getGroupStateInfoLabel().setText("");

                    EntityChangeEvent.Type type = (selectedGroup.getId() == null) ? EntityChangeEvent.Type.CREATED : EntityChangeEvent.Type.UPDATED;
                    eventBus.post(new GroupChangeEvent(type, selectedGroup));

                    MessageEvent messageEvent = new MessageEvent("Group save confirmation", "Group " + selectedGroup.getName() + " was saved successfully!", JOptionPane.INFORMATION_MESSAGE);
                    eventBus.post(messageEvent);
                } else {
                    MessageEvent messageEvent = new MessageEvent("Validation failure", validationMessages, JOptionPane.ERROR_MESSAGE);
                    eventBus.post(messageEvent);
                }
            }
        });
    }

    /**
     * Check if the group exists in the database; i.e. does the group has an ID?
     *
     * @param group the given group
     * @return does the group exist
     */
    private boolean isExistingGroup(Group group) {
        return group.getId() != null;
    }

    /**
     * Check if a group with the given group name exists in the database.
     *
     * @param group the selected group
     * @return does the group name exist
     */
    private boolean isExistingGroupName(Group group) {
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
        Group selectedGroup = (getSelectedGroupIndex() != -1) ? groupBindingList.get(getSelectedGroupIndex()) : null;
        return selectedGroup;
    }

    /**
     * Get the selected group index in the group JList.
     *
     * @return the selected index
     */
    private int getSelectedGroupIndex() {
        return userManagementDialog.getGroupList().getSelectedIndex();
    }
}
