package com.compomics.colims.client.controller.admin;

import com.compomics.colims.client.controller.MainController;
import com.compomics.colims.client.view.admin.MetaDataManagementDialog;
import com.compomics.colims.client.view.admin.UserManagementDialog;
import com.google.common.eventbus.EventBus;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("metaDataManagementController")
public class MetadataManagementController {

    //model    
    //view
    private MetaDataManagementDialog metaDataManagementDialog;
    //parent controller
    @Autowired
    private MainController mainController;
    
    private PermissionCrudController permissionCrudController;    
    //services
    @Autowired
    private EventBus eventBus;
    
    public MetadataManagementController() {
    }

    public MetaDataManagementDialog getMetaDataManagementDialog() {
        return metaDataManagementDialog;
    }
        
    public void init() {                          
        //init view
        metaDataManagementDialog = new MetaDataManagementDialog(mainController.getMainFrame(), true);                
    }
}
