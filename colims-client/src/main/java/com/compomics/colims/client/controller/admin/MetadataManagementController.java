package com.compomics.colims.client.controller.admin;

import com.compomics.colims.client.controller.Controllable;
import com.compomics.colims.client.controller.MainController;
import com.compomics.colims.client.view.admin.MetaDataManagementDialog;
import com.compomics.colims.client.view.admin.UserManagementDialog;
import com.compomics.colims.core.service.InstrumentService;
import com.compomics.colims.model.Instrument;
import com.compomics.colims.model.InstrumentCvTerm;
import com.compomics.colims.model.User;
import com.google.common.eventbus.EventBus;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import no.uib.olsdialog.OLSDialog;
import no.uib.olsdialog.OLSInputable;
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
public class MetadataManagementController implements Controllable, OLSInputable {

    //model  
    private ObservableList<Instrument> instrumentBindingList;
    private ObservableList<InstrumentCvTerm> analyzerBindingList;
    private BindingGroup bindingGroup;
    //view
    private MetaDataManagementDialog metaDataManagementDialog;
    //parent controller
    @Autowired
    private MainController mainController;
    //services
    @Autowired
    private InstrumentService instrumentService;    
    @Autowired
    private EventBus eventBus;

    public MetadataManagementController() {
    }

    public MetaDataManagementDialog getMetaDataManagementDialog() {
        return metaDataManagementDialog;
    }

    @Override
    public void init() {
        //init view
        metaDataManagementDialog = new MetaDataManagementDialog(mainController.getMainFrame(), true, this);

        //register to event bus
        eventBus.register(this);

        //init binding
        bindingGroup = new BindingGroup();

        instrumentBindingList = ObservableCollections.observableList(instrumentService.findAll());
        JListBinding userListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, instrumentBindingList, metaDataManagementDialog.getInstrumentList());
        bindingGroup.addBinding(userListBinding);
        analyzerBindingList = ObservableCollections.observableList(new ArrayList<InstrumentCvTerm>());
        JListBinding analyzerListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, analyzerBindingList, metaDataManagementDialog.getAnalyzerList());
        bindingGroup.addBinding(analyzerListBinding);
        
        //instrument bindings
        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, metaDataManagementDialog.getInstrumentList(), BeanProperty.create("selectedElement.name"), metaDataManagementDialog.getInstrumentNameTextField(), ELProperty.create("${text}"), "nameBinding");
        bindingGroup.addBinding(binding);
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, metaDataManagementDialog.getInstrumentList(), BeanProperty.create("selectedElement.type"), metaDataManagementDialog.getInstrumentTypeTextField(), ELProperty.create("${text}"), "typeBinding");
        bindingGroup.addBinding(binding);
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, metaDataManagementDialog.getInstrumentList(), BeanProperty.create("selectedElement.source.name"), metaDataManagementDialog.getInstrumentSourceTextField(), ELProperty.create("${text}"), "sourceBinding");
        bindingGroup.addBinding(binding);
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, metaDataManagementDialog.getInstrumentList(), BeanProperty.create("selectedElement.detector.name"), metaDataManagementDialog.getInstrumentDetectorTextField(), ELProperty.create("${text}"), "detectorBinding");
        bindingGroup.addBinding(binding);
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, metaDataManagementDialog.getInstrumentList(), BeanProperty.create("selectedElement.analyzer"), metaDataManagementDialog.getInstrumentDetectorTextField(), ELProperty.create("${text}"), "analyzerBinding");
        bindingGroup.addBinding(binding);

        bindingGroup.bind();
        
        //add action listeners
        metaDataManagementDialog.getInstrumentList().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    if (metaDataManagementDialog.getInstrumentList().getSelectedIndex() != -1) {
                        
                        Instrument selectedInstrument = instrumentBindingList.get(metaDataManagementDialog.getInstrumentList().getSelectedIndex());
                        
                        //populate the analyzers list with the analyzers from the selected instrument
                        analyzerBindingList.clear();
                        analyzerBindingList.addAll(selectedInstrument.getAnalyzers());
                    }
                }
            }
        }); 
        
        metaDataManagementDialog.getAddAnalyzerButton().addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                metaDataManagementDialog.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
                new OLSDialog(metaDataManagementDialog, this, true, "singleAnalyzer", "MS", null);
                metaDataManagementDialog.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
            }
            
        });
    }

    @Override
    public void insertOLSResult(String field, String selectedValue, String accession, String ontologyShort, String ontologyLong, int modifiedRow, String mappedTerm, Map<String, String> metadata) {
        if (field.equalsIgnoreCase("addAnalyzerButton")) {
           InstrumentCvTerm analyzer = new InstrumentCvTerm();
        }
    }

    @Override
    public Window getWindow() {
        return metaDataManagementDialog;
    }
    
    private void addAnalyzer(){
        
    }
}
