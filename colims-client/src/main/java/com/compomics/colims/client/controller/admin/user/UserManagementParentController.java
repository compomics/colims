package com.compomics.colims.client.controller.admin.user;

import com.compomics.colims.client.controller.Controllable;
import com.compomics.colims.client.controller.MainController;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.admin.UserManagementDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("userManagementController")
public class UserManagementParentController implements Controllable {

    //model
    //view
    private UserManagementDialog userManagementDialog;
    //parent controller
    @Autowired
    private MainController mainController;
    //child controllers
    @Autowired
    private UserManagementController userManagementController;
    @Autowired
    private GroupManagementController groupManagementController;
    @Autowired
    private RoleManagementController roleManagementController;
    @Autowired
    private PermissionManagementController permissionManagementController;

    /**
     * No-arg constructor.
     */
    public UserManagementParentController() {
    }

    /**
     * Get the view of this controller.
     *
     * @return the UserManagementDialog
     */
    public UserManagementDialog getUserManagementDialog() {
        return userManagementDialog;
    }

    @Override
    public void init() {
        //init view
        userManagementDialog = new UserManagementDialog(mainController.getMainFrame(), true);

        //init child controllers
        userManagementController.init();
        groupManagementController.init();
        roleManagementController.init();
        permissionManagementController.init();

        //add action listeners
        userManagementDialog.getCloseButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                userManagementDialog.dispose();
            }
        });
    }

    @Override
    public void showView() {
        userManagementController.showView();
        groupManagementController.showView();
        roleManagementController.showView();
        permissionManagementController.showView();

        GuiUtils.centerDialogOnComponent(mainController.getMainFrame(), userManagementDialog);
        userManagementDialog.setVisible(true);
    }
}
