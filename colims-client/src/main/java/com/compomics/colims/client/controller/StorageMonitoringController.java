package com.compomics.colims.client.controller;

import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.StorageMonitoringDialog;
import com.compomics.colims.core.service.AnalyticalRunService;
import com.compomics.colims.core.service.InstrumentService;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.Instrument;
import com.google.common.eventbus.EventBus;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("storageMonitoringController")
public class StorageMonitoringController implements Controllable {

    private static final Logger LOGGER = Logger.getLogger(StorageMonitoringController.class);

    //model   
    private BindingGroup bindingGroup;
    private ObservableList instrumentBindingList;
    //view
    private StorageMonitoringDialog storageMonitoringDialog;
    //parent controller
    @Autowired
    private ColimsController colimsController;
    //services
    @Autowired
    private EventBus eventBus;

    public StorageMonitoringDialog getStorageMonitoringDialog() {
        return storageMonitoringDialog;
    }    

    @Override
    public void init() {
        //register to event bus
        eventBus.register(this);

        //init view
        storageMonitoringDialog = new StorageMonitoringDialog(colimsController.getColimsFrame(), true);        

        instrumentBindingList = ObservableCollections.observableList(new ArrayList());

        //add binding
        bindingGroup = new BindingGroup();        

        bindingGroup.bind();        
    }

    @Override
    public void showView() {
        GuiUtils.centerDialogOnComponent(colimsController.getColimsFrame(), storageMonitoringDialog);
        storageMonitoringDialog.setVisible(true);
    }    

}
