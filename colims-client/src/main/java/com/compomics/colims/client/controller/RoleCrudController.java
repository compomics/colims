package com.compomics.colims.client.controller;

import com.compomics.colims.client.bean.AuthenticationBean;
import com.compomics.colims.client.event.EntityChangeEvent;
import com.compomics.colims.client.event.GroupChangeEvent;
import com.compomics.colims.client.event.MessageEvent;
import com.compomics.colims.client.event.RoleChangeEvent;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.UserManagementDialog;
import com.compomics.colims.core.service.GroupService;
import com.compomics.colims.core.service.PermissionService;
import com.compomics.colims.core.service.RoleService;
import com.compomics.colims.model.Group;
import com.compomics.colims.model.Permission;
import com.compomics.colims.model.Role;
import com.google.common.eventbus.EventBus;
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
@Component("roleCrudController")
public class RoleCrudController {

    //model
    private ObservableList<Role> roleBindingList;
    private BindingGroup bindingGroup;
    private List<Permission> availablePermissions;
    //view
    private UserManagementDialog userManagementDialog;
    //parent controller
    @Autowired
    private UserManagementController userManagementController;
    @Autowired
    private EventBus eventBus;
    //services
    @Autowired
    private RoleService roleService;
    @Autowired
    private PermissionService permissionService;

    public void init() {
        userManagementDialog = userManagementController.getUserManagementDialog();

        //register to event bus
        eventBus.register(this);

        //load available permissions
        availablePermissions = permissionService.findAll();

        //disable save and delete button
        userManagementDialog.getUserSaveOrUpdateButton().setEnabled(false);
        userManagementDialog.getDeleteUserButton().setEnabled(false);

        //init binding
        bindingGroup = new BindingGroup();

        roleBindingList = ObservableCollections.observableList(roleService.findAll());
        JListBinding roleListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, roleBindingList, userManagementDialog.getRoleList());
        bindingGroup.addBinding(roleListBinding);

        //user bindingd
        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, userManagementDialog.getRoleList(), BeanProperty.create("selectedElement.name"), userManagementDialog.getRoleNameTextField(), ELProperty.create("${text}"), "nameBinding");
        bindingGroup.addBinding(binding);
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, userManagementDialog.getRoleList(), BeanProperty.create("selectedElement.description"), userManagementDialog.getRoleDescriptionTextArea(), ELProperty.create("${text}"), "descriptionBinding");
        bindingGroup.addBinding(binding);

        bindingGroup.bind();

        //add listeners
        userManagementDialog.getRoleNameTextField().addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                userManagementDialog.getRoleList().updateUI();
            }
        });

        userManagementDialog.getRoleList().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    if (getSelectedRoleIndex() != -1) {
                        Role selectedRole = getSelectedRole();

                        //enable save button
                        userManagementDialog.getUserSaveOrUpdateButton().setEnabled(true);

                        //check if the group is found in the db.
                        //If so, disable the name text field and change the save button label.
                        if (isExistingRoleName(selectedRole)) {
                            //@todo see if we need to fetch the relations
                            //userService.fetchAuthenticationRelations(selectedUser);

                            userManagementDialog.getRoleNameTextField().setEnabled(false);
                            userManagementDialog.getRoleSaveOrUpdateButton().setText("update");
                            userManagementDialog.getRoleStateInfoLabel().setText("");
                        } else {
                            userManagementDialog.getRoleNameTextField().setEnabled(true);
                            userManagementDialog.getRoleSaveOrUpdateButton().setText("save");
                            userManagementDialog.getRoleStateInfoLabel().setText("This role hasn't been saved to the database.");
                        }

                        //populate dual list with permission                        
                        userManagementDialog.getPermissionDualList().populateLists(availablePermissions, selectedRole.getPermissions());
                    } else {
                        userManagementDialog.getUserSaveOrUpdateButton().setEnabled(false);
                    }
                }
            }
        });

        userManagementDialog.getAddUserButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Role newRole = new Role("name");
                roleBindingList.add(newRole);
                userManagementDialog.getRoleNameTextField().setEnabled(true);
                userManagementDialog.getRoleList().setSelectedIndex(roleBindingList.size() - 1);
            }
        });

        userManagementDialog.getDeleteGroupButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (getSelectedRoleIndex() != -1) {
                    Role roleToDelete = getSelectedRole();
                    //check if user is already has an id.
                    //If so, delete the user from the db.
                    if (roleToDelete.getId() != null) {
                        roleService.delete(roleToDelete);
                        eventBus.post(new RoleChangeEvent(EntityChangeEvent.Type.DELETED, roleToDelete));
                    }
                    roleBindingList.remove(getSelectedRoleIndex());
                    userManagementDialog.getRoleList().setSelectedIndex(roleBindingList.size() - 1);
                }
            }
        });

        userManagementDialog.getRoleSaveOrUpdateButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Role selectedRole = getSelectedRole();
                //validate user
                List<String> validationMessages = GuiUtils.validateEntity(getSelectedRole());
                //check for a new group if the user name already exists in the db                
                if (!isExistingRole(selectedRole) && isExistingRoleName(selectedRole)) {
                    validationMessages.add(selectedRole.getName() + " already exists in the database, please choose another role name.");
                }
                if (validationMessages.isEmpty()) {
                    //if modified, add groups to user
                    List<Role> addedRoles = userManagementDialog.getRoleDualList().getAddedItems();

                    if (isExistingRole(selectedRole)) {
                        roleService.updateRole(selectedRole, addedRoles);
                    } else {
                        roleService.saveRole(selectedRole, addedRoles);
                    }
                    userManagementDialog.getGroupStateInfoLabel().setText("");

                    EntityChangeEvent.Type type = (selectedRole.getId() == null) ? EntityChangeEvent.Type.CREATED : EntityChangeEvent.Type.UPDATED;
                    eventBus.post(new RoleChangeEvent(type, selectedRole));

                    MessageEvent messageEvent = new MessageEvent("Role save confirmation", "Role " + selectedRole.getName() + " was saved successfully!", JOptionPane.INFORMATION_MESSAGE);
                    eventBus.post(messageEvent);
                } else {
                    MessageEvent messageEvent = new MessageEvent("Validation failure", validationMessages, JOptionPane.ERROR_MESSAGE);
                    eventBus.post(messageEvent);
                }
            }
        });
    }

    /**
     * Check if the role exists in the database; i.e. does the role has an ID?
     *
     * @param role the given role
     * @return does the role exist
     */
    private boolean isExistingRole(Role role) {
        return role.getId() != null;
    }

    /**
     * Check if a role with the given role name exists in the database.
     *
     * @param role the selected role
     * @return does the role name exist
     */
    private boolean isExistingRoleName(Role role) {
        boolean isExistingRoleName = true;
        Role foundRole = roleService.findByName(role.getName());
        if (foundRole == null) {
            isExistingRoleName = false;
        }

        return isExistingRoleName;
    }

    /**
     * Get the selected role in the role JList.
     *
     * @return the selected role
     */
    private Role getSelectedRole() {
        Role selectedRole = (getSelectedRoleIndex() != -1) ? roleBindingList.get(getSelectedRoleIndex()) : null;
        return selectedRole;
    }

    /**
     * Get the selected role index in the role JList.
     *
     * @return the selected index
     */
    private int getSelectedRoleIndex() {
        return userManagementDialog.getRoleList().getSelectedIndex();
    }
}