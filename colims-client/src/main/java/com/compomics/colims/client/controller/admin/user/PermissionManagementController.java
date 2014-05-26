package com.compomics.colims.client.controller.admin.user;

import com.compomics.colims.client.controller.Controllable;
import com.compomics.colims.client.event.message.DbConstraintMessageEvent;
import com.compomics.colims.client.event.message.DefaultDbEntryMessageEvent;
import com.compomics.colims.client.event.EntityChangeEvent;
import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.event.admin.PermissionChangeEvent;
import com.compomics.colims.client.event.admin.RoleChangeEvent;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.admin.UserManagementDialog;
import com.compomics.colims.core.service.PermissionService;
import com.compomics.colims.model.Permission;
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
@Component("permissionManagementController")
public class PermissionManagementController implements Controllable {

    //model
    private ObservableList<Permission> permissionBindingList;
    private BindingGroup bindingGroup;
    //view
    private UserManagementDialog userManagementDialog;
    //parent controller
    @Autowired
    private UserManagementParentController userManagementController;
    //event bus    
    @Autowired
    private EventBus eventBus;
    //services
    @Autowired
    private PermissionService permissionService;

    @Override
    public void init() {
        //get view
        userManagementDialog = userManagementController.getUserManagementDialog();

        //register to event bus
        eventBus.register(this);

        //disable save and delete button
        userManagementDialog.getPermissionSaveOrUpdateButton().setEnabled(false);
        userManagementDialog.getDeletePermissionButton().setEnabled(false);

        //init binding
        bindingGroup = new BindingGroup();

        permissionBindingList = ObservableCollections.observableList(permissionService.findAll());
        JListBinding permissionListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, permissionBindingList, userManagementDialog.getPermissionList());
        bindingGroup.addBinding(permissionListBinding);

        //permission bindings
        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, userManagementDialog.getPermissionList(), BeanProperty.create("selectedElement.name"), userManagementDialog.getPermissionNameTextField(), ELProperty.create("${text}"), "nameBinding");
        bindingGroup.addBinding(binding);
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, userManagementDialog.getPermissionList(), BeanProperty.create("selectedElement.description"), userManagementDialog.getPermissionDescriptionTextArea(), ELProperty.create("${text}"), "descriptionBinding");
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

        userManagementDialog.getPermissionList().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(final ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    if (userManagementDialog.getPermissionList().getSelectedIndex() != -1) {
                        Permission selectedPermission = getSelectedPermission();

                        //enable save and delete button
                        userManagementDialog.getPermissionSaveOrUpdateButton().setEnabled(true);
                        userManagementDialog.getDeletePermissionButton().setEnabled(true);

                        //check if the permission has an ID.
                        //If so, disable the name text field and change the save button label.
                        if (selectedPermission.getId() != null) {
                            userManagementDialog.getPermissionNameTextField().setEnabled(false);
                            userManagementDialog.getPermissionSaveOrUpdateButton().setText("update");
                            userManagementDialog.getPermissionStateInfoLabel().setText("");
                        } else {
                            userManagementDialog.getPermissionNameTextField().setEnabled(true);
                            userManagementDialog.getPermissionSaveOrUpdateButton().setText("save");
                            userManagementDialog.getPermissionStateInfoLabel().setText("This permission hasn't been stored to the database.");
                        }
                    } else {
                        userManagementDialog.getPermissionSaveOrUpdateButton().setEnabled(false);
                    }
                }
            }
        });

        userManagementDialog.getAddPermissionButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Permission newPermission = new Permission("name");
                permissionBindingList.add(newPermission);
                userManagementDialog.getPermissionNameTextField().setEnabled(true);
                userManagementDialog.getPermissionList().setSelectedIndex(permissionBindingList.size() - 1);
            }
        });

        userManagementDialog.getDeletePermissionButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (userManagementDialog.getPermissionList().getSelectedIndex() != -1) {
                    Permission permissionToDelete = getSelectedPermission();
                    //check if permission is already has an id.
                    //If so, delete the permission from the db.
                    if (permissionToDelete.getId() != null) {
                        //check if the permission is a default permission
                        if (!permissionService.isDefaultPermission(permissionToDelete)) {
                            try {
                                permissionService.delete(permissionToDelete);
                                eventBus.post(new PermissionChangeEvent(EntityChangeEvent.Type.DELETED, false, permissionToDelete));

                                permissionBindingList.remove(userManagementDialog.getPermissionList().getSelectedIndex());
                                resetSelection();
                            } catch (DataIntegrityViolationException dive) {
                                //check if the permission can be deleted without breaking existing database relations,
                                //i.e. are there any constraints violations
                                if (dive.getCause() instanceof ConstraintViolationException) {
                                    DbConstraintMessageEvent dbConstraintMessageEvent = new DbConstraintMessageEvent("permission", permissionToDelete.getName());
                                    eventBus.post(dbConstraintMessageEvent);
                                } else {
                                    //pass the exception
                                    throw dive;
                                }
                            }
                        } else {
                            DefaultDbEntryMessageEvent defaultDbEntryMessageEvent = new DefaultDbEntryMessageEvent("permission", permissionToDelete.getName());
                            eventBus.post(defaultDbEntryMessageEvent);
                        }
                    } else {
                        permissionBindingList.remove(userManagementDialog.getPermissionList().getSelectedIndex());
                        resetSelection();
                    }
                }
            }
        });

        userManagementDialog.getPermissionSaveOrUpdateButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Permission selectedPermission = getSelectedPermission();
                //validate permission
                List<String> validationMessages = GuiUtils.validateEntity(selectedPermission);
                //check for a new permission if the permission name already exists in the db                
                if (selectedPermission.getId() == null && isExistingPermissionName(selectedPermission)) {
                    validationMessages.add(selectedPermission.getName() + " already exists in the database,"
                            + "\n" + "please choose another permission name.");
                }
                if (validationMessages.isEmpty()) {
                    if (selectedPermission.getId() != null) {
                        permissionService.update(selectedPermission);
                    } else {
                        permissionService.save(selectedPermission);
                        //refresh permission list
                        userManagementDialog.getPermissionList().updateUI();
                    }
                    userManagementDialog.getPermissionNameTextField().setEnabled(false);
                    userManagementDialog.getPermissionSaveOrUpdateButton().setText("update");
                    userManagementDialog.getPermissionStateInfoLabel().setText("");

                    EntityChangeEvent.Type type = (selectedPermission.getId() == null) ? EntityChangeEvent.Type.CREATED : EntityChangeEvent.Type.UPDATED;
                    eventBus.post(new PermissionChangeEvent(type, false, selectedPermission));

                    MessageEvent messageEvent = new MessageEvent("Permission store confirmation", "Permission " + selectedPermission.getName() + " was stored successfully!", JOptionPane.INFORMATION_MESSAGE);
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
     * Listen to a RoleChangeEvent and update the permissions if necessary.
     *
     * @param roleChangeEvent the RoleChangeEvent
     */
    @Subscribe
    public void onRoleChangeEvent(final RoleChangeEvent roleChangeEvent) {
        if (roleChangeEvent.areChildrenAffected()) {
            permissionBindingList.clear();
            permissionBindingList.addAll(permissionService.findAll());
        }
    }

    /**
     * Check if a permission with the given permission name exists in the
     * database.
     *
     * @param permission the selected permission
     * @return does the permission name exist
     */
    private boolean isExistingPermissionName(final Permission permission) {
        boolean isExistingPermissionName = true;
        Permission foundPermission = permissionService.findByName(permission.getName());
        if (foundPermission == null) {
            isExistingPermissionName = false;
        }

        return isExistingPermissionName;
    }

    /**
     * Get the selected permission in the permission JList.
     *
     * @return the selected permission
     */
    private Permission getSelectedPermission() {
        int seletedPermissionIndex = userManagementDialog.getPermissionList().getSelectedIndex();
        Permission selectedPermission = (seletedPermissionIndex != -1) ? permissionBindingList.get(seletedPermissionIndex) : null;
        return selectedPermission;
    }

    /**
     * Reset the selection in the role list.
     */
    private void resetSelection() {
        //clear selection
        userManagementDialog.getPermissionList().getSelectionModel().clearSelection();
        if (!permissionBindingList.isEmpty()) {
            userManagementDialog.getPermissionList().setSelectedIndex(0);
        }
    }
}
