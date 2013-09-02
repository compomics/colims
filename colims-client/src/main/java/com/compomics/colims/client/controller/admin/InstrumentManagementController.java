package com.compomics.colims.client.controller.admin;

import com.compomics.colims.client.controller.Controllable;
import com.compomics.colims.client.controller.MainController;
import com.compomics.colims.client.event.MessageEvent;
import com.compomics.colims.client.model.CvTermSummaryListModel;
import com.compomics.colims.client.model.CvTermTableModel;
import com.compomics.colims.client.renderer.CvTermSummaryCellRenderer;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.admin.EditInstrumentDialog;
import com.compomics.colims.client.view.admin.InstrumentManagementDialog;
import com.compomics.colims.client.view.admin.InstrumentTypeManagementDialog;
import com.compomics.colims.core.service.InstrumentCvTermService;
import com.compomics.colims.core.service.InstrumentService;
import com.compomics.colims.core.service.InstrumentTypeService;
import com.compomics.colims.model.CvTerm;
import com.compomics.colims.model.Instrument;
import com.compomics.colims.model.InstrumentCvTerm;
import com.compomics.colims.model.InstrumentType;
import com.compomics.colims.model.enums.CvTermProperty;
import com.compomics.colims.model.enums.InstrumentCvProperty;
import com.google.common.eventbus.EventBus;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
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
    private CvTermSummaryListModel cvTermSummaryListModel;
    private ObservableList<Instrument> instrumentBindingList;
    private ObservableList<InstrumentType> instrumentTypeBindingList;
    private BindingGroup bindingGroup;    
    //view
    private InstrumentManagementDialog instrumentManagementDialog;
    private InstrumentTypeManagementDialog instrumentTypeManagementDialog;
    private EditInstrumentDialog editInstrumentDialog;
    //parent controller
    @Autowired
    private MainController mainController;
    //services
    @Autowired
    private InstrumentService instrumentService;
    @Autowired
    private InstrumentTypeService instrumentTypeService;
    @Autowired
    private InstrumentCvTermService instrumentCvTermService;
    @Autowired
    private EventBus eventBus;

    public InstrumentManagementController() {
    }

    public InstrumentManagementDialog getInstrumentManagementDialog() {
        return instrumentManagementDialog;
    }

    @Override
    public void init() {
        //register to event bus
        eventBus.register(this);

        //init binding
        bindingGroup = new BindingGroup();

        //init views     
        initInstrumentManagementDialog();
        initInstrumentTypeManagementDialog();
        initEditInstrumentDialog();

        bindingGroup.bind();

//        instrumentManagementDialog.getAddAnalyzerButton().addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                instrumentManagementDialog.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
//                //new OLSDialog(metaDataManagementDialog, this, true, "singleAnalyzer", "MS", null);
//
//                Map<String, List<String>> preselectedOntologies = new HashMap<>();
//                List msPreselectedParentTerms = new ArrayList<>();
//                msPreselectedParentTerms.add("MS:1000458");  // Source Description
//                preselectedOntologies.put("PRIDE", null);
//                preselectedOntologies.put("PSI", null);
//                preselectedOntologies.put("MS", msPreselectedParentTerms);
//
//                //open 
//                new OLSDialog(instrumentManagementDialog, InstrumentManagementController.this, true, "addAnalyzerButton", "MS", "singleAnalyzer", preselectedOntologies);
//
//                instrumentManagementDialog.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
//            }
//        });
    }

    private void initInstrumentManagementDialog() {
        instrumentManagementDialog = new InstrumentManagementDialog(mainController.getMainFrame(), true);

        //add binding
        instrumentBindingList = ObservableCollections.observableList(instrumentService.findAll());
        JListBinding instrumentListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, instrumentBindingList, instrumentManagementDialog.getInstrumentList());
        bindingGroup.addBinding(instrumentListBinding);

        //add action listeners
        instrumentManagementDialog.getInstrumentList().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    if (instrumentManagementDialog.getInstrumentList().getSelectedIndex() != -1) {
                        Instrument selectedInstrument = instrumentBindingList.get(instrumentManagementDialog.getInstrumentList().getSelectedIndex());

                        //init CvTermModel
                        List<CvTerm> cvTerms = new ArrayList<>();
                        cvTerms.add(selectedInstrument.getSource());
                        cvTerms.add(selectedInstrument.getDetector());
                        for (InstrumentCvTerm analyzer : selectedInstrument.getAnalyzers()) {
                            cvTerms.add(analyzer);
                        }
                        CvTermTableModel cvTermTableModel = new CvTermTableModel(cvTerms);
                        instrumentManagementDialog.getInstrumentDetailTable().setModel(cvTermTableModel);
                    }
                }
            }
        });

        instrumentManagementDialog.getAddInstrumentButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Instrument defaultInstrument = createDefaultInstrument();
                instrumentBindingList.add(defaultInstrument);
                instrumentManagementDialog.getInstrumentList().setSelectedIndex(instrumentBindingList.size() - 1);
            }
        });

        instrumentManagementDialog.getDeleteInstrumentButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (instrumentManagementDialog.getInstrumentList().getSelectedIndex() != -1) {
                    Instrument instrumentToDelete = getSelectedInstrument();
                    //check if the instrument is already has an id.
                    //If so, delete the instrument from the db.
                    if (instrumentToDelete.getId() != null) {
                        boolean deleted = instrumentService.checkUsageBeforeDeletion(instrumentToDelete);
                        if (!deleted) {
                            eventBus.post(new MessageEvent("Instrument deletion", "The instrument was used for one or more analytical runs stored in the database. "
                                    + "Hence, the instrument was not deleted.", JOptionPane.WARNING_MESSAGE));
                        } else {
                            instrumentBindingList.remove(instrumentManagementDialog.getInstrumentList().getSelectedIndex());
                            instrumentManagementDialog.getInstrumentList().setSelectedIndex(instrumentBindingList.size() - 1);
                        }
                    }
                }
            }
        });

        instrumentManagementDialog.getEditInstrumentButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (instrumentManagementDialog.getInstrumentList().getSelectedIndex() != -1) {
                    updateEditInstrumentDialog(getSelectedInstrument());
                    //show dialog
                    editInstrumentDialog.setLocationRelativeTo(null);
                    editInstrumentDialog.setVisible(true);
                }
            }
        });

    }

    private void initEditInstrumentDialog() {
        editInstrumentDialog = new EditInstrumentDialog(mainController.getMainFrame(), true);
        
        //add binding
        Binding instrumentNameBinding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, instrumentManagementDialog.getInstrumentList(), ELProperty.create("${selectedElement.name}"), editInstrumentDialog.getNameTextField(), BeanProperty.create("text"), "instrumentNameBinding");        
        bindingGroup.addBinding(instrumentNameBinding);
        
        //set model and renderer
        cvTermSummaryListModel = new CvTermSummaryListModel();
        editInstrumentDialog.getCvTermSummaryList().setModel(cvTermSummaryListModel);
        editInstrumentDialog.getCvTermSummaryList().setCellRenderer(new CvTermSummaryCellRenderer());

        //add action listeners
        editInstrumentDialog.getCvTermSummaryList().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    if (instrumentTypeManagementDialog.getInstrumentTypeList().getSelectedIndex() != -1) {
                        
                    }
                }
            }
        });

        editInstrumentDialog.getEditInstrumentTypesButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                instrumentTypeManagementDialog.setLocationRelativeTo(null);
                instrumentTypeManagementDialog.setVisible(true);
            }
        });
    }

    private void initInstrumentTypeManagementDialog() {
        instrumentTypeManagementDialog = new InstrumentTypeManagementDialog(mainController.getMainFrame(), true);

        instrumentTypeBindingList = ObservableCollections.observableList(instrumentTypeService.findAll());
        JListBinding instrumentTypeListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, instrumentTypeBindingList, instrumentTypeManagementDialog.getInstrumentTypeList());
        bindingGroup.addBinding(instrumentTypeListBinding);

        //permission bindings
        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, instrumentTypeManagementDialog.getInstrumentTypeList(), BeanProperty.create("selectedElement.name"), instrumentTypeManagementDialog.getInstrumentTypeNameTextField(), ELProperty.create("${text}"), "nameBinding");
        bindingGroup.addBinding(binding);
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, instrumentTypeManagementDialog.getInstrumentTypeList(), BeanProperty.create("selectedElement.description"), instrumentTypeManagementDialog.getInstrumentTypeDescriptionTextArea(), ELProperty.create("${text}"), "descriptionBinding");
        bindingGroup.addBinding(binding);

        //add listeners
        instrumentTypeManagementDialog.getInstrumentTypeList().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    if (instrumentTypeManagementDialog.getInstrumentTypeList().getSelectedIndex() != -1) {
                        InstrumentType instrumentType = getSelectedInstrumentType();

                        //enable save and delete button
                        instrumentTypeManagementDialog.getInstrumentTypeSaveOrUpdateButton().setEnabled(true);
                        instrumentTypeManagementDialog.getDeleteInstrumentTypeButton().setEnabled(true);

                        //check if the instrument type is found in the db.
                        //If so, disable the name text field and change the save button label.
                        if (isExistingInstrumentTypeName(instrumentType)) {
                            instrumentTypeManagementDialog.getInstrumentTypeNameTextField().setEnabled(false);
                            instrumentTypeManagementDialog.getInstrumentTypeSaveOrUpdateButton().setText("update");
                            instrumentTypeManagementDialog.getInstrumentTypeStateInfoLabel().setText("");
                        } else {
                            instrumentTypeManagementDialog.getInstrumentTypeNameTextField().setEnabled(true);
                            instrumentTypeManagementDialog.getInstrumentTypeSaveOrUpdateButton().setText("save");
                            instrumentTypeManagementDialog.getInstrumentTypeStateInfoLabel().setText("This permission hasn't been saved to the database.");
                        }
                    } else {
                        instrumentTypeManagementDialog.getInstrumentTypeSaveOrUpdateButton().setEnabled(false);
                    }
                }
            }
        });

        instrumentTypeManagementDialog.getAddInstrumentTypeButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                InstrumentType newInstrumentType = new InstrumentType();
                newInstrumentType.setName("name");
                instrumentTypeBindingList.add(newInstrumentType);
                instrumentTypeManagementDialog.getInstrumentTypeNameTextField().setEnabled(true);
                instrumentTypeManagementDialog.getInstrumentTypeList().setSelectedIndex(instrumentTypeBindingList.size() - 1);
            }
        });

        instrumentTypeManagementDialog.getDeleteInstrumentTypeButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (instrumentTypeManagementDialog.getInstrumentTypeList().getSelectedIndex() != -1) {
                    InstrumentType instrumentTypeToDelete = getSelectedInstrumentType();
                    //check if permission is already has an id.
                    //If so, delete the permission from the db.
                    if (instrumentTypeToDelete.getId() != null) {
                        instrumentTypeService.delete(instrumentTypeToDelete);
                    }
                    instrumentTypeBindingList.remove(instrumentTypeManagementDialog.getInstrumentTypeList().getSelectedIndex());
                    instrumentTypeManagementDialog.getInstrumentTypeList().setSelectedIndex(instrumentTypeBindingList.size() - 1);
                }
            }
        });

        instrumentTypeManagementDialog.getInstrumentTypeSaveOrUpdateButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                InstrumentType selectedInstrumentType = getSelectedInstrumentType();
                //validate instrument type
                List<String> validationMessages = GuiUtils.validateEntity(selectedInstrumentType);
                //check for a new instrument type if the instrument type name already exists in the db                
                if (!isExistingInstrumentType(selectedInstrumentType) && isExistingInstrumentTypeName(selectedInstrumentType)) {
                    validationMessages.add(selectedInstrumentType.getName() + " already exists in the database, please choose another instrument type name.");
                }
                if (validationMessages.isEmpty()) {
                    if (isExistingInstrumentType(selectedInstrumentType)) {
                        instrumentTypeService.update(selectedInstrumentType);
                    } else {
                        instrumentTypeService.save(selectedInstrumentType);
                        //refresh permission list
                        instrumentTypeManagementDialog.getInstrumentTypeList().updateUI();
                    }
                    instrumentTypeManagementDialog.getInstrumentTypeNameTextField().setEnabled(false);
                    instrumentTypeManagementDialog.getInstrumentTypeSaveOrUpdateButton().setText("update");
                    instrumentTypeManagementDialog.getInstrumentTypeStateInfoLabel().setText("");

                    MessageEvent messageEvent = new MessageEvent("Instrument type save confirmation", "Instrument type " + selectedInstrumentType.getName() + " was saved successfully!", JOptionPane.INFORMATION_MESSAGE);
                    eventBus.post(messageEvent);
                } else {
                    MessageEvent messageEvent = new MessageEvent("Validation failure", validationMessages, JOptionPane.ERROR_MESSAGE);
                    eventBus.post(messageEvent);
                }
            }
        });
    }

    @Override
    public void insertOLSResult(String field, String selectedValue, String accession, String ontologyShort, String ontologyLong, int modifiedRow, String mappedTerm, Map<String, String> metadata) {
        if (field.equalsIgnoreCase("addAnalyzerButton")) {
            InstrumentCvTerm analyzer = new InstrumentCvTerm(CvTermProperty.ANALYZER, ontologyLong, ontologyShort, accession, selectedValue);
            addAnalyzer(getSelectedInstrument(), analyzer);
        }
    }

    @Override
    public Window getWindow() {
        return instrumentManagementDialog;
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
            InstrumentCvTerm foundAnalyzer = instrumentCvTermService.findByAccession(analyzer.getAccession(), InstrumentCvProperty.ANALYZER);
            if (foundAnalyzer != null) {
                instrument.getAnalyzers().add(foundAnalyzer);
            } else {
                instrument.getAnalyzers().add(analyzer);
            }
            instrumentService.update(instrument);
        }
    }

    /**
     * Check if a instrument type with the given instrument type name exists in
     * the database.
     *
     * @param instrumentType the instrument type
     * @return does the instrument type name exist
     */
    private boolean isExistingInstrumentTypeName(InstrumentType instrumentType) {
        boolean isExistingInstrumentTypeName = true;
        InstrumentType foundInstrumentType = instrumentTypeService.findByName(instrumentType.getName());
        if (foundInstrumentType == null) {
            isExistingInstrumentTypeName = false;
        }

        return isExistingInstrumentTypeName;
    }

    /**
     * Get the selected instrument in the instrument JList.
     *
     * @return the selected instrument
     */
    private Instrument getSelectedInstrument() {
        int selectedIndex = instrumentManagementDialog.getInstrumentList().getSelectedIndex();
        Instrument selectedInstrument = (selectedIndex != -1) ? instrumentBindingList.get(selectedIndex) : null;
        return selectedInstrument;
    }

    /**
     * Get the selected instrument type in the instrument type JList.
     *
     * @return the selected instrument type
     */
    private InstrumentType getSelectedInstrumentType() {
        int selectedIndex = instrumentTypeManagementDialog.getInstrumentTypeList().getSelectedIndex();
        InstrumentType selectedInstrumentType = (selectedIndex != -1) ? instrumentTypeBindingList.get(selectedIndex) : null;
        return selectedInstrumentType;
    }

    /**
     * Check if the instrument type exists in the database; i.e. does the
     * instrument type has an ID?
     *
     * @param instrumentType
     * @return does the instrument type exist
     */
    private boolean isExistingInstrumentType(InstrumentType instrumentType) {
        return instrumentType.getId() != null;
    }

    /**
     * Create a default instrument, with some default properties.
     *
     * @return the default experiment
     */
    private Instrument createDefaultInstrument() {
        Instrument defaultInstrument = new Instrument("default instrument name");
        //find instrument types
        List<InstrumentType> instrumentTypes = instrumentTypeService.findAll();
        if (!instrumentTypes.isEmpty()) {
            defaultInstrument.setInstrumentType(instrumentTypes.get(0));
        }
        defaultInstrument.setInstrumentType(instrumentTypes.get(0));
        //find sources
        List<InstrumentCvTerm> sources = instrumentCvTermService.findByInstrumentCvProperty(InstrumentCvProperty.SOURCE);
        if (!sources.isEmpty()) {
            defaultInstrument.setSource(sources.get(0));
        }
        //find detectors
        List<InstrumentCvTerm> detectors = instrumentCvTermService.findByInstrumentCvProperty(InstrumentCvProperty.DETECTOR);
        if (!detectors.isEmpty()) {
            defaultInstrument.setDetector(detectors.get(0));
        }
        //find analyzers
        List<InstrumentCvTerm> analyzers = instrumentCvTermService.findByInstrumentCvProperty(InstrumentCvProperty.ANALYZER);
        if (!analyzers.isEmpty()) {
            List<InstrumentCvTerm> defaultAnalyzers = new ArrayList<>();
            defaultAnalyzers.add(analyzers.get(0));
            defaultInstrument.setAnalyzers(defaultAnalyzers);
        }
        return defaultInstrument;
    }

    private void updateEditInstrumentDialog(Instrument instrument) {
        //add the single CV terms
        EnumMap<CvTermProperty, CvTerm> singleCvTerms = new EnumMap<>(CvTermProperty.class);
        singleCvTerms.put(CvTermProperty.SOURCE, instrument.getSource());
        singleCvTerms.put(CvTermProperty.DETECTOR, instrument.getDetector());
        singleCvTerms.put(CvTermProperty.CELL_BASED, null);
        
        //add the multiple CV terms
        EnumMap<CvTermProperty, List<? extends CvTerm>> multipleCvTerms = new EnumMap<>(CvTermProperty.class);
        multipleCvTerms.put(CvTermProperty.ANALYZER, instrument.getAnalyzers());        
        cvTermSummaryListModel.update(singleCvTerms, multipleCvTerms);
    }
}
