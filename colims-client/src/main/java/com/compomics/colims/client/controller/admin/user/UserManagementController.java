package com.compomics.colims.client.controller.admin.user;

import com.compomics.colims.client.controller.Controllable;
import com.compomics.colims.client.controller.ColimsController;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.admin.UserManagementDialog;
import com.google.common.eventbus.EventBus;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("userManagementController")
public class UserManagementController implements Controllable {

    //model    
    //view
    private UserManagementDialog userManagementDialog;
    //parent controller
    @Autowired
    private ColimsController colimsController;
    //child controllers
    @Autowired
    private UserCrudController userCrudController;
    @Autowired
    private GroupCrudController groupCrudController;
    @Autowired
    private RoleCrudController roleCrudController;
    @Autowired
    private PermissionCrudController permissionCrudController;
    //services
    @Autowired
    private EventBus eventBus;

    public UserManagementController() {
    }

    public UserManagementDialog getUserManagementDialog() {
        return userManagementDialog;
    }

    @Override
    public void init() {
        //init view
        userManagementDialog = new UserManagementDialog(colimsController.getColimsFrame(), true);

        //init child controllers
        userCrudController.init();
        groupCrudController.init();
        roleCrudController.init();
        permissionCrudController.init();

        //add action listeners
        userManagementDialog.getCloseButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userManagementDialog.dispose();
            }
        });
    }

    @Override
    public void showView() {
        userCrudController.showView();
        groupCrudController.showView();
        roleCrudController.showView();
        permissionCrudController.showView();

        GuiUtils.centerDialogOnComponent(colimsController.getColimsFrame(), userManagementDialog);
        userManagementDialog.setVisible(true);
    }
}
