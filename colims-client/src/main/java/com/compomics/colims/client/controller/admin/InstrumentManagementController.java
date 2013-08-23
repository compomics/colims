package com.compomics.colims.client.controller.admin;

import com.compomics.colims.client.controller.Controllable;
import com.compomics.colims.client.controller.MainController;
import com.compomics.colims.client.event.MessageEvent;
import com.compomics.colims.client.view.admin.InstrumentTypeManagementDialog;
import com.compomics.colims.client.view.admin.MetaDataManagementDialog;
import com.compomics.colims.core.service.InstrumentService;
import com.compomics.colims.core.service.InstrumentTypeService;
import com.compomics.colims.model.Instrument;
import com.compomics.colims.model.InstrumentCvTerm;
import com.compomics.colims.model.InstrumentType;
import com.compomics.colims.model.enums.InstrumentCvProperty;
import com.google.common.eventbus.EventBus;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
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
public class InstrumentManagementController implements Controllable, OLSInputable {

    //model  
    private ObservableList<Instrument> instrumentBindingList;
    private ObservableList<InstrumentCvTerm> analyzerBindingList;
    private ObservableList<InstrumentType> instrumentTypeBindingList;
    private BindingGroup bindingGroup;
    //view
    private MetaDataManagementDialog metaDataManagementDialog;
    private InstrumentTypeManagementDialog instrumentTypeManagementDialog;
    //parent controller
    @Autowired
    private MainController mainController;
    //services
    @Autowired
    private InstrumentService instrumentService;
    @Autowired
    private InstrumentTypeService instrumentTypeService;
    @Autowired
    private EventBus eventBus;

    public InstrumentManagementController() {
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
        JListBinding instrumentListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, instrumentBindingList, metaDataManagementDialog.getInstrumentList());
        bindingGroup.addBinding(instrumentListBinding);
        analyzerBindingList = ObservableCollections.observableList(new ArrayList<InstrumentCvTerm>());
        JListBinding analyzerListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, analyzerBindingList, metaDataManagementDialog.getAnalyzerList());
        bindingGroup.addBinding(analyzerListBinding);
        instrumentTypeBindingList = ObservableCollections.observableList(instrumentTypeService.findAll());
        JListBinding instrumentTypeListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, instrumentTypeBindingList, metaDataManagementDialog.getInstrumentList());
        bindingGroup.addBinding(instrumentTypeListBinding);

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

        metaDataManagementDialog.getAddAnalyzerButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                metaDataManagementDialog.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
                //new OLSDialog(metaDataManagementDialog, this, true, "singleAnalyzer", "MS", null);

                Map<String, List<String>> preselectedOntologies = new HashMap<>();
                List msPreselectedParentTerms = new ArrayList<>();
                msPreselectedParentTerms.add("MS:1000458");  // Source Description
                preselectedOntologies.put("PRIDE", null);
                preselectedOntologies.put("PSI", null);
                preselectedOntologies.put("MS", msPreselectedParentTerms);

                //open 
                new OLSDialog(metaDataManagementDialog, InstrumentManagementController.this, true, "addAnalyzerButton", "MS", "singleAnalyzer", preselectedOntologies);

                metaDataManagementDialog.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
            }
        });
    }

    @Override
    public void insertOLSResult(String field, String selectedValue, String accession, String ontologyShort, String ontologyLong, int modifiedRow, String mappedTerm, Map<String, String> metadata) {
        if (field.equalsIgnoreCase("addAnalyzerButton")) {
            InstrumentCvTerm analyzer = new InstrumentCvTerm(InstrumentCvProperty.ANALYZER, ontologyLong, ontologyShort, accession, selectedValue);
            addAnalyzer(getSelectedInstrument(), analyzer);
        }
    }

    @Override
    public Window getWindow() {
        return metaDataManagementDialog;
    }

    /**
     * Add an analyzer to the given instrument. Checks if the instrument already
     * has the analyzer and if the analyzer is already in the db.
     *
     * @param instrument
     * @param analyzer
     */
    private void addAnalyzer(Instrument instrument, InstrumentCvTerm analyzer) {
        Instrument selectedInstrument = getSelectedInstrument();
        //check if the selected instrument already has the found analyzer
        if (selectedInstrument.getAnalyzers().contains(analyzer)) {
            MessageEvent messageEvent = new MessageEvent("Analyzer addition", "Analyzer with accession " + analyzer.getAccession() + " has already been added to the selected instrument.", JOptionPane.ERROR_MESSAGE);
            eventBus.post(messageEvent);
        } else {
            //check if the analyzer is found in the db
            InstrumentCvTerm foundAnalyzer = instrumentService.findAnalyzerByAccession(analyzer.getAccession());
            if (foundAnalyzer != null) {
                instrument.getAnalyzers().add(foundAnalyzer);
            } else {
                instrument.getAnalyzers().add(analyzer);
            }
            instrumentService.update(instrument);
        }
    }

    /**
     * Get the selected instrument in the instrument JList.
     *
     * @return the selected instrument
     */
    private Instrument getSelectedInstrument() {
        int selectedIndex = metaDataManagementDialog.getInstrumentList().getSelectedIndex();
        Instrument selectedInstrument = (selectedIndex != -1) ? instrumentBindingList.get(selectedIndex) : null;
        return selectedInstrument;
    }
}
