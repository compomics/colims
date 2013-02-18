package com.compomics.colims.client.controller;

import com.compomics.colims.client.view.UserManagementDialog;
import com.compomics.colims.core.service.GroupService;
import com.compomics.colims.core.service.PermissionService;
import com.compomics.colims.core.service.RoleService;
import com.compomics.colims.core.service.UserService;
import com.compomics.colims.model.Group;
import com.compomics.colims.model.Permission;
import com.compomics.colims.model.Role;
import com.google.common.eventbus.EventBus;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("userManagementController")
public class UserManagementController {

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
    private EventBus eventBus;
    //services
    
    public UserManagementController() {
    }

    public UserManagementDialog getUserManagementDialog() {
        return userManagementDialog;
    }

    public void init() {
        //init view
        userManagementDialog = new UserManagementDialog(mainController.getMainFrame(), true);

        //init child controllers
        userCrudController.init();               
    }
}
