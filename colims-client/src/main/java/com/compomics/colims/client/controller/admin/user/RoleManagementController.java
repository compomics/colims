package com.compomics.colims.client.controller.admin.user;

import com.compomics.colims.client.compoment.DualList;
import com.compomics.colims.client.controller.Controllable;
import com.compomics.colims.client.event.message.DbConstraintMessageEvent;
import com.compomics.colims.client.event.EntityChangeEvent;
import static com.compomics.colims.client.event.EntityChangeEvent.Type.CREATED;
import static com.compomics.colims.client.event.EntityChangeEvent.Type.DELETED;
import static com.compomics.colims.client.event.EntityChangeEvent.Type.UPDATED;
import com.compomics.colims.client.event.admin.GroupChangeEvent;
import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.event.admin.PermissionChangeEvent;
import com.compomics.colims.client.event.admin.RoleChangeEvent;
import com.compomics.colims.client.event.message.DefaultDbEntryMessageEvent;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.admin.UserManagementDialog;
import com.compomics.colims.core.service.PermissionService;
import com.compomics.colims.core.service.RoleService;
import com.compomics.colims.model.Permission;
import com.compomics.colims.model.Role;
import com.compomics.colims.model.comparator.PermissionNameComparator;
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
@Component("roleManagementController")
public class RoleManagementController implements Controllable {

    //model
    private ObservableList<Role> roleBindingList;
    private BindingGroup bindingGroup;
    private List<Permission> availablePermissions;
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
    private RoleService roleService;
    @Autowired
    private PermissionService permissionService;

    @Override
    public void init() {
        //get view
        userManagementDialog = userManagementController.getUserManagementDialog();

        //init dual list
        userManagementDialog.getPermissionDualList().init(new PermissionNameComparator());

        areChildrenAffected = false;

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
            public void focusGained(final FocusEvent e) {
            }

            @Override
            public void focusLost(final FocusEvent e) {
                userManagementDialog.getRoleList().updateUI();
            }
        });

        userManagementDialog.getRoleList().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(final ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    if (userManagementDialog.getRoleList().getSelectedIndex() != -1) {
                        Role selectedRole = getSelectedRole();

                        //enable save and delete button
                        userManagementDialog.getRoleSaveOrUpdateButton().setEnabled(true);
                        userManagementDialog.getDeleteRoleButton().setEnabled(true);

                        //check if the role has an ID.
                        //If so, disable the name text field and change the save button label.
                        if (selectedRole.getId() != null) {
                            userManagementDialog.getRoleNameTextField().setEnabled(false);
                            userManagementDialog.getRoleSaveOrUpdateButton().setText("update");
                            userManagementDialog.getRoleStateInfoLabel().setText("");
                        } else {
                            userManagementDialog.getRoleNameTextField().setEnabled(true);
                            userManagementDialog.getRoleSaveOrUpdateButton().setText("save");
                            userManagementDialog.getRoleStateInfoLabel().setText("This role hasn't been stored in the database.");
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
            public void actionPerformed(final ActionEvent e) {
                Role newRole = new Role("name");
                roleBindingList.add(newRole);
                userManagementDialog.getRoleNameTextField().setEnabled(true);
                userManagementDialog.getRoleList().setSelectedIndex(roleBindingList.size() - 1);
            }
        });

        userManagementDialog.getDeleteRoleButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (userManagementDialog.getRoleList().getSelectedIndex() != -1) {
                    Role roleToDelete = getSelectedRole();
                    //check if role is already has an id.
                    //If so, delete the role from the db.
                    if (roleToDelete.getId() != null) {
                        //check if the role is a default role
                        if (!roleService.isDefaultRole(roleToDelete)) {
                            try {
                                roleService.delete(roleToDelete);
                                eventBus.post(new RoleChangeEvent(EntityChangeEvent.Type.DELETED, true, roleToDelete));

                                roleBindingList.remove(userManagementDialog.getRoleList().getSelectedIndex());
                                resetSelection();
                            } catch (DataIntegrityViolationException dive) {
                                //check if the role can be deleted without breaking existing database relations,
                                //i.e. are there any constraints violations
                                if (dive.getCause() instanceof ConstraintViolationException) {
                                    DbConstraintMessageEvent dbConstraintMessageEvent = new DbConstraintMessageEvent("role", roleToDelete.getName());
                                    eventBus.post(dbConstraintMessageEvent);
                                } else {
                                    //pass the exception
                                    throw dive;
                                }
                            }
                        } else {
                            DefaultDbEntryMessageEvent defaultDbEntryMessageEvent = new DefaultDbEntryMessageEvent("role", roleToDelete.getName());
                            eventBus.post(defaultDbEntryMessageEvent);
                        }
                    } else {
                        roleBindingList.remove(userManagementDialog.getRoleList().getSelectedIndex());
                        resetSelection();
                    }
                }
            }
        });

        userManagementDialog.getPermissionDualList().addPropertyChangeListener(DualList.CHANGED, new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent evt) {
                //change permissions of the selected role
                List<Permission> addedPermissions = (List<Permission>) evt.getNewValue();

                //add permissions to the selected role
                Role selectedRole = getSelectedRole();
                selectedRole.setPermissions(addedPermissions);

                areChildrenAffected = true;
            }
        });

        userManagementDialog.getRoleSaveOrUpdateButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Role selectedRole = getSelectedRole();
                //validate role
                List<String> validationMessages = GuiUtils.validateEntity(selectedRole);
                //check for a new group if the role name already exists in the db
                if (selectedRole.getId() == null && isExistingRoleName(selectedRole)) {
                    validationMessages.add(selectedRole.getName() + " already exists in the database,"
                            + System.lineSeparator() + "please choose another role name.");
                }
                if (validationMessages.isEmpty()) {
                    if (selectedRole.getId() != null) {
                        roleService.update(selectedRole);
                    } else {
                        roleService.save(selectedRole);
                    }
                    userManagementDialog.getRoleNameTextField().setEnabled(false);
                    userManagementDialog.getRoleSaveOrUpdateButton().setText("update");
                    userManagementDialog.getRoleStateInfoLabel().setText("");

                    EntityChangeEvent.Type type = (selectedRole.getId() == null) ? EntityChangeEvent.Type.CREATED : EntityChangeEvent.Type.UPDATED;
                    eventBus.post(new RoleChangeEvent(type, areChildrenAffected, selectedRole));

                    MessageEvent messageEvent = new MessageEvent("Role store confirmation", "Role " + selectedRole.getName() + " was stored successfully!", JOptionPane.INFORMATION_MESSAGE);
                    eventBus.post(messageEvent);
                } else {
                    MessageEvent messageEvent = new MessageEvent("Validation failure", validationMessages, JOptionPane.WARNING_MESSAGE);
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
     * Listen to a GroupChangeEvent and update the roles if necessary.
     *
     * @param groupChangeEvent the GroupChangeEvent
     */
    @Subscribe
    public void onGroupChangeEvent(final GroupChangeEvent groupChangeEvent) {
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
    public void onPermissionChangeEvent(final PermissionChangeEvent permissionChangeEvent) {
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
        resetSelection();
    }

    /**
     * Reload the roles from the database after canceling.
     */
    public void onCancel() {
        roleBindingList.clear();
        roleBindingList.addAll(roleService.findAll());
    }

    /**
     * Check if a role with the given role name exists in the database.
     *
     * @param role the selected role
     * @return does the role name exist
     */
    private boolean isExistingRoleName(final Role role) {
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
        int selectedRoleIndex = userManagementDialog.getRoleList().getSelectedIndex();
        return (selectedRoleIndex != -1) ? roleBindingList.get(selectedRoleIndex) : null;
    }

    /**
     * Reset the selection in the role list.
     */
    private void resetSelection() {
        //clear selection
        userManagementDialog.getRoleList().getSelectionModel().clearSelection();
        if (!roleBindingList.isEmpty()) {
            userManagementDialog.getRoleList().setSelectedIndex(0);
        }
    }
}
