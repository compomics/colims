package com.compomics.colims.client.controller.admin;

import com.compomics.colims.client.controller.Controllable;
import com.compomics.colims.client.event.EntityChangeEvent;
import static com.compomics.colims.client.event.EntityChangeEvent.Type.CREATED;
import static com.compomics.colims.client.event.EntityChangeEvent.Type.DELETED;
import static com.compomics.colims.client.event.EntityChangeEvent.Type.UPDATED;
import com.compomics.colims.client.event.GroupChangeEvent;
import com.compomics.colims.client.event.MessageEvent;
import com.compomics.colims.client.event.PermissionChangeEvent;
import com.compomics.colims.client.event.RoleChangeEvent;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.admin.UserManagementDialog;
import com.compomics.colims.core.service.PermissionService;
import com.compomics.colims.core.service.RoleService;
import com.compomics.colims.model.Permission;
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
@Component("roleCrudController")
public class RoleCrudController implements Controllable {

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

    /**
     * Listen to a GroupChangeEvent and update the roles if necessary.
     *
     * @param groupChangeEvent the GroupChangeEvent
     */
    @Subscribe
    public void onGroupChangeEvent(GroupChangeEvent groupChangeEvent) {
        if (groupChangeEvent.areChildrenAffected()) {
            roleBindingList.clear();
            roleBindingList.addAll(roleService.findAll());
        }
    }

    /**
     * Listen to a PermissionChangeEvent and update the available permissions in
     * the DualList.
     *
     * @param permissionChangeEvent the PermissionChangeEvent
     */
    @Subscribe
    public void onPermissionChangeEvent(PermissionChangeEvent permissionChangeEvent) {
        switch (permissionChangeEvent.getType()) {
            case CREATED:
            case UPDATED:
                int index = availablePermissions.indexOf(permissionChangeEvent.getPermission());
                if (index != -1) {
                    availablePermissions.set(index, permissionChangeEvent.getPermission());
                } else {
                    availablePermissions.add(permissionChangeEvent.getPermission());
                }
                break;
            case DELETED:
                availablePermissions.remove(permissionChangeEvent.getPermission());
                //update the role binding list
                roleBindingList.clear();
                roleBindingList.addAll(roleService.findAll());
                break;
            default:
                break;
        }
        if (!roleBindingList.isEmpty()) {
            userManagementDialog.getRoleList().setSelectedIndex(0);
        }
    }

    @Override
    public void init() {
        //get view
        userManagementDialog = userManagementController.getUserManagementDialog();

        //register to event bus
        eventBus.register(this);

        //load available permissions
        availablePermissions = permissionService.findAll();

        //disable save and delete button
        userManagementDialog.getRoleSaveOrUpdateButton().setEnabled(false);
        userManagementDialog.getDeleteRoleButton().setEnabled(false);

        //init binding
        bindingGroup = new BindingGroup();

        roleBindingList = ObservableCollections.observableList(roleService.findAll());
        JListBinding roleListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, roleBindingList, userManagementDialog.getRoleList());
        bindingGroup.addBinding(roleListBinding);

        //permission bindings
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

                        //enable save and delete button
                        userManagementDialog.getRoleSaveOrUpdateButton().setEnabled(true);
                        userManagementDialog.getDeleteRoleButton().setEnabled(true);

                        //check if the group is found in the db.
                        //If so, disable the name text field and change the save button label.
                        if (isExistingRoleName(selectedRole)) {
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
                        userManagementDialog.getRoleSaveOrUpdateButton().setEnabled(false);
                    }
                }
            }
        });

        userManagementDialog.getAddRoleButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Role newRole = new Role("name");
                roleBindingList.add(newRole);
                userManagementDialog.getRoleNameTextField().setEnabled(true);
                userManagementDialog.getRoleList().setSelectedIndex(roleBindingList.size() - 1);
            }
        });

        userManagementDialog.getDeleteRoleButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (getSelectedRoleIndex() != -1) {
                    Role roleToDelete = getSelectedRole();
                    //check if role is already has an id.
                    //If so, delete the role from the db.
                    if (roleToDelete.getId() != null) {
                        roleService.delete(roleToDelete);
                        eventBus.post(new RoleChangeEvent(EntityChangeEvent.Type.DELETED, true, roleToDelete));
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
                //validate role
                List<String> validationMessages = GuiUtils.validateEntity(getSelectedRole());
                //check for a new group if the role name already exists in the db                
                if (!isExistingRole(selectedRole) && isExistingRoleName(selectedRole)) {
                    validationMessages.add(selectedRole.getName() + " already exists in the database, please choose another role name.");
                }
                if (validationMessages.isEmpty()) {
                    //check if permissions have been added or removed
                    if (userManagementDialog.getPermissionDualList().isModified()) {
                        List<Permission> addedPermissions = userManagementDialog.getPermissionDualList().getAddedItems();

                        //add permissions to the selected role
                        selectedRole.setPermissions(addedPermissions);
                    }

                    if (isExistingRole(selectedRole)) {
                        roleService.update(selectedRole);
                    } else {
                        roleService.save(selectedRole);
                    }
                    userManagementDialog.getRoleNameTextField().setEnabled(false);
                    userManagementDialog.getRoleSaveOrUpdateButton().setText("update");
                    userManagementDialog.getRoleStateInfoLabel().setText("");

                    EntityChangeEvent.Type type = (selectedRole.getId() == null) ? EntityChangeEvent.Type.CREATED : EntityChangeEvent.Type.UPDATED;
                    eventBus.post(new RoleChangeEvent(type, userManagementDialog.getPermissionDualList().isModified(), selectedRole));

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
