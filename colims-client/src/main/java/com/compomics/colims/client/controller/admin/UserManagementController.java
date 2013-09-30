package com.compomics.colims.client.controller.admin;

import com.compomics.colims.client.controller.Controllable;
import com.compomics.colims.client.controller.MainController;
import com.compomics.colims.client.view.admin.UserManagementDialog;
import com.google.common.eventbus.EventBus;
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
    private MainController mainController;
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
        userManagementDialog = new UserManagementDialog(mainController.getMainFrame(), true);        

        //init child controllers
        userCrudController.init();
        groupCrudController.init();
        roleCrudController.init();
        permissionCrudController.init();
    }
}
