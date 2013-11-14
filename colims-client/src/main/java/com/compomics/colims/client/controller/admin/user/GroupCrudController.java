package com.compomics.colims.client.controller.admin.user;

import com.compomics.colims.client.compoment.DualList;
import com.compomics.colims.client.controller.Controllable;
import com.compomics.colims.client.event.DbConstraintMessageEvent;
import com.compomics.colims.client.event.EntityChangeEvent;
import static com.compomics.colims.client.event.EntityChangeEvent.Type.CREATED;
import static com.compomics.colims.client.event.EntityChangeEvent.Type.DELETED;
import static com.compomics.colims.client.event.EntityChangeEvent.Type.UPDATED;
import com.compomics.colims.client.event.GroupChangeEvent;
import com.compomics.colims.client.event.MessageEvent;
import com.compomics.colims.client.event.RoleChangeEvent;
import com.compomics.colims.client.event.UserChangeEvent;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.admin.UserManagementDialog;
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
@Component("groupCrudController")
public class GroupCrudController implements Controllable {

    //model
    private ObservableList<Group> groupBindingList;
    private BindingGroup bindingGroup;
    private List<Role> availableRoles;
    boolean areChildrenAffected;
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

    @Override
    public void init() {
        //get view
        userManagementDialog = userManagementController.getUserManagementDialog();

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
                            userManagementDialog.getGroupStateInfoLabel().setText("This group hasn't been persisted to the database.");
                        }

                        //populate dual list with roles                        
                        userManagementDialog.getRoleDualList().populateLists(availableRoles, selectedGroup.getRoles());
                    } else {
                        userManagementDialog.getGroupSaveOrUpdateButton().setEnabled(false);
                        clearGroupDetailFields();
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
                if (userManagementDialog.getGroupList().getSelectedIndex() != -1) {
                    Group groupToDelete = getSelectedGroup();
                    //check if group is already has an id.
                    //If so, delete the group from the db.
                    if (groupToDelete.getId() != null) {
                        try {
                            groupService.delete(groupToDelete);
                            eventBus.post(new GroupChangeEvent(EntityChangeEvent.Type.DELETED, true, groupToDelete));

                            groupBindingList.remove(userManagementDialog.getGroupList().getSelectedIndex());
                            userManagementDialog.getGroupList().getSelectionModel().clearSelection();
                        } catch (DataIntegrityViolationException dive) {
                            //check if the group can be deleted without breaking existing database relations,
                            //i.e. are there any constraints violations
                            if (dive.getCause() instanceof ConstraintViolationException) {
                                DbConstraintMessageEvent dbConstraintMessageEvent = new DbConstraintMessageEvent(groupToDelete.getName());
                                eventBus.post(dbConstraintMessageEvent);
                            } else {
                                //pass the exception
                                throw dive;
                            }
                        }
                    } else {
                        groupBindingList.remove(userManagementDialog.getGroupList().getSelectedIndex());
                        userManagementDialog.getGroupList().getSelectionModel().clearSelection();
                    }
                }
            }
        });

        userManagementDialog.getRoleDualList().addPropertyChangeListener(DualList.CHANGED, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                //change roles of the selected group                                    
                List<Role> addedRoles = userManagementDialog.getRoleDualList().getAddedItems();

                //add roles to the selected group
                Group selectedGroup = getSelectedGroup();
                selectedGroup.setRoles(addedRoles);

                areChildrenAffected = true;
            }
        });

        userManagementDialog.getGroupSaveOrUpdateButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Group selectedGroup = getSelectedGroup();
                //validate group
                List<String> validationMessages = GuiUtils.validateEntity(selectedGroup);
                //check for a new group if the group name already exists in the db                
                if (selectedGroup.getId() == null && isExistingGroupName(selectedGroup)) {
                    validationMessages.add(selectedGroup.getName() + " already exists in the database, please choose another group name.");
                }
                if (validationMessages.isEmpty()) {
                    if (selectedGroup.getId() != null) {
                        groupService.update(selectedGroup);
                    } else {
                        groupService.save(selectedGroup);
                    }
                    userManagementDialog.getGroupNameTextField().setEnabled(false);
                    userManagementDialog.getGroupSaveOrUpdateButton().setText("update");
                    userManagementDialog.getGroupStateInfoLabel().setText("");

                    EntityChangeEvent.Type type = (selectedGroup.getId() == null) ? EntityChangeEvent.Type.CREATED : EntityChangeEvent.Type.UPDATED;
                    eventBus.post(new GroupChangeEvent(type, areChildrenAffected, selectedGroup));

                    MessageEvent messageEvent = new MessageEvent("Group persist confirmation", "Group " + selectedGroup.getName() + " was persisted successfully!", JOptionPane.INFORMATION_MESSAGE);
                    eventBus.post(messageEvent);
                } else {
                    MessageEvent messageEvent = new MessageEvent("Validation failure", validationMessages, JOptionPane.ERROR_MESSAGE);
                    eventBus.post(messageEvent);
                }
            }
        });
    }

    @Override
    public void showView() {
        //clear selection
        userManagementDialog.getGroupList().getSelectionModel().clearSelection();
    }

    /**
     * Listen to a UserChangeEvent and update the groups if necessary.
     *
     * @param userChangeEvent the UserChangeEvent
     */
    @Subscribe
    public void onUserChangeEvent(UserChangeEvent userChangeEvent) {
        if (userChangeEvent.areChildrenAffected()) {
            groupBindingList.clear();
            groupBindingList.addAll(groupService.findAll());
        }
    }

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
        userManagementDialog.getGroupList().getSelectionModel().clearSelection();
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
        int selectedGroupIndex = userManagementDialog.getGroupList().getSelectedIndex();
        Group selectedGroup = (selectedGroupIndex != -1) ? groupBindingList.get(selectedGroupIndex) : null;
        return selectedGroup;
    }

    /**
     * Clear the group detail fields
     */
    private void clearGroupDetailFields() {
        userManagementDialog.getGroupNameTextField().setText("");
        userManagementDialog.getGroupDescriptionTextArea().setText("");
        userManagementDialog.getRoleDualList().clear();
    }
}