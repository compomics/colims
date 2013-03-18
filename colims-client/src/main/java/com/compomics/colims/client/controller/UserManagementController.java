package com.compomics.colims.client.controller;

import com.compomics.colims.client.view.UserManagementDialog;
import com.google.common.eventbus.EventBus;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
