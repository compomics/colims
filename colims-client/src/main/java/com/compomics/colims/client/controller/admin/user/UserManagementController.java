package com.compomics.colims.client.controller.admin.user;

import com.compomics.colims.client.controller.Controllable;
import com.compomics.colims.client.controller.ColimsController;
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
    private ColimsController mainController;
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
        userManagementDialog = new UserManagementDialog(mainController.getColimsFrame(), true);        

        //init child controllers
        userCrudController.init();
        groupCrudController.init();
        roleCrudController.init();
        permissionCrudController.init();
    }
    
    @Override
    public void showView() {
        userCrudController.showView();
        groupCrudController.showView();
        roleCrudController.showView();
        permissionCrudController.showView();
        
        userManagementDialog.setVisible(true);
    } 
}
