package com.compomics.colims.client.controller.admin;

import com.compomics.colims.client.controller.MainController;
import com.compomics.colims.client.view.admin.MetaDataManagementDialog;
import com.compomics.colims.client.view.admin.UserManagementDialog;
import com.compomics.colims.core.service.InstrumentService;
import com.compomics.colims.model.Instrument;
import com.compomics.colims.model.User;
import com.google.common.eventbus.EventBus;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("metaDataManagementController")
public class MetadataManagementController {

    //model  
    private ObservableList<Instrument> instrumentBindingList;
    private BindingGroup bindingGroup;
    //view
    private MetaDataManagementDialog metaDataManagementDialog;
    //parent controller
    @Autowired
    private MainController mainController;
    @Autowired
    private InstrumentService instrumentService;
    
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
        
        //register to event bus
        eventBus.register(this);

        //init binding
        bindingGroup = new BindingGroup();

        instrumentBindingList = ObservableCollections.observableList(instrumentService.findAll());
        JListBinding userListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, instrumentBindingList, metaDataManagementDialog.getInstrumentList());
        bindingGroup.addBinding(userListBinding);

        //instrument bindings
        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, metaDataManagementDialog.getInstrumentList(), BeanProperty.create("selectedElement.name"), metaDataManagementDialog.getInstrumentNameTextField(), ELProperty.create("${text}"), "nameBinding");
        bindingGroup.addBinding(binding);
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, metaDataManagementDialog.getInstrumentList(), BeanProperty.create("selectedElement.type"), metaDataManagementDialog.getInstrumentTypeTextField(), ELProperty.create("${text}"), "typeBinding");
        bindingGroup.addBinding(binding);
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, metaDataManagementDialog.getInstrumentList(), BeanProperty.create("selectedElement.source.name"), metaDataManagementDialog.getInstrumentSourceTextField(), ELProperty.create("${text}"), "sourceBinding");
        bindingGroup.addBinding(binding);
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, metaDataManagementDialog.getInstrumentList(), BeanProperty.create("selectedElement.detector.name"), metaDataManagementDialog.getInstrumentDetectorTextField(), ELProperty.create("${text}"), "detectorBinding");
        bindingGroup.addBinding(binding);
//        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, metaDataManagementDialog.getInstrumentList(), BeanProperty.create("selectedElement.analyzer"), metaDataManagementDialog.getInstrumentDetectorTextField(), ELProperty.create("${text}"), "analyzerBinding");
//        bindingGroup.addBinding(binding);
                
        bindingGroup.bind();

        //add listeners
        
    }
}
