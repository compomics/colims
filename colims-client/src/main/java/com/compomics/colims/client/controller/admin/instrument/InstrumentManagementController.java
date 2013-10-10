package com.compomics.colims.client.controller.admin.instrument;

import com.compomics.colims.client.compoment.DualList;
import com.compomics.colims.client.controller.Controllable;
import com.compomics.colims.client.controller.MainController;
import com.compomics.colims.client.controller.admin.CvTermManagementController;
import com.compomics.colims.client.event.CvTermChangeEvent;
import com.compomics.colims.client.event.MessageEvent;
import com.compomics.colims.client.model.CvTermSummaryListModel;
import com.compomics.colims.client.model.CvTermTableModel;
import com.compomics.colims.client.renderer.CvTermSummaryCellRenderer;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.admin.instrument.InstrumentManagementDialog;
import com.compomics.colims.client.view.admin.instrument.InstrumentManagementOverviewDialog;
import com.compomics.colims.client.view.admin.instrument.InstrumentTypeManagementDialog;
import com.compomics.colims.core.service.CvTermService;
import com.compomics.colims.core.service.InstrumentService;
import com.compomics.colims.core.service.InstrumentTypeService;
import com.compomics.colims.model.CvTerm;
import com.compomics.colims.model.Instrument;
import com.compomics.colims.model.InstrumentCvTerm;
import com.compomics.colims.model.InstrumentType;
import com.compomics.colims.model.enums.CvTermType;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import no.uib.olsdialog.OLSInputable;
import org.hibernate.exception.ConstraintViolationException;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ELProperty;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JComboBoxBinding;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("instrumentManagementController")
public class InstrumentManagementController implements Controllable, OLSInputable {

    //model      
    private CvTermSummaryListModel<InstrumentCvTerm> cvTermSummaryListModel;
    private ObservableList<Instrument> instrumentBindingList;
    private ObservableList<InstrumentType> instrumentTypeBindingList;
    private BindingGroup bindingGroup;
    //view
    private InstrumentManagementOverviewDialog instrumentManagementOverviewDialog;
    private InstrumentTypeManagementDialog instrumentTypeManagementDialog;
    private InstrumentManagementDialog instrumentManagementDialog;
    //parent controller
    @Autowired
    private MainController mainController;
    @Autowired
    private CvTermManagementController cvTermManagementController;
    //services
    @Autowired
    private InstrumentService instrumentService;
    @Autowired
    private InstrumentTypeService instrumentTypeService;
    @Autowired
    private CvTermService cvTermService;
    @Autowired
    private EventBus eventBus;

    public InstrumentManagementController() {
    }

    public InstrumentManagementOverviewDialog getInstrumentManagementOverviewDialog() {
        return instrumentManagementOverviewDialog;
    }

    /**
     * Listen to a CV term change event posted by the
     * CvTermManagementController. If the InstrumentManagementDialog is visible,
     * clear the selection in the CV term summary list.
     */
    @Subscribe
    public void onCvTermChangeEvent(CvTermChangeEvent cvTermChangeEvent) {
        if (instrumentManagementDialog.isVisible()) {
            instrumentManagementDialog.getCvTermSummaryList().getSelectionModel().clearSelection();
        }
    }

    @Override
    public void init() {
        //register to event bus
        eventBus.register(this);

        //init binding
        bindingGroup = new BindingGroup();

        //init views     
        initInstrumentManagementOverviewDialog();
        initInstrumentTypeManagementDialog();
        initInstrumentManagementDialog();

        bindingGroup.bind();
    }

    private void initInstrumentManagementOverviewDialog() {
        instrumentManagementOverviewDialog = new InstrumentManagementOverviewDialog(mainController.getMainFrame(), true);

        //add binding
        instrumentBindingList = ObservableCollections.observableList(instrumentService.findAll());
        JListBinding instrumentListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, instrumentBindingList, instrumentManagementOverviewDialog.getInstrumentList());
        bindingGroup.addBinding(instrumentListBinding);

        //add action listeners
        instrumentManagementOverviewDialog.getInstrumentList().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    if (instrumentManagementOverviewDialog.getInstrumentList().getSelectedIndex() != -1) {
                        Instrument selectedInstrument = instrumentBindingList.get(instrumentManagementOverviewDialog.getInstrumentList().getSelectedIndex());

                        //check if the instrument has an ID.
                        //If so, change the save button text and the info state label.
                        if (selectedInstrument.getId() != null) {
                            instrumentManagementOverviewDialog.getInstrumentStateInfoLabel().setText("");
                        } else {
                            instrumentManagementOverviewDialog.getInstrumentStateInfoLabel().setText("This instrument hasn't been persisted to the database.");
                        }

                        //init CvTermModel
                        List<CvTerm> cvTerms = new ArrayList<>();
                        cvTerms.add(selectedInstrument.getSource());
                        cvTerms.add(selectedInstrument.getDetector());
                        for (InstrumentCvTerm analyzer : selectedInstrument.getAnalyzers()) {
                            cvTerms.add(analyzer);
                        }
                        CvTermTableModel cvTermTableModel = new CvTermTableModel(cvTerms);
                        instrumentManagementOverviewDialog.getInstrumentDetailTable().setModel(cvTermTableModel);
                    } else {
                        //clear detail view
                        instrumentManagementOverviewDialog.getInstrumentDetailTable().setModel(new CvTermTableModel());
                        instrumentManagementOverviewDialog.getInstrumentStateInfoLabel().setText("");
                    }
                }
            }
        });

        instrumentManagementOverviewDialog.getAddInstrumentButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Instrument defaultInstrument = createDefaultInstrument();
                instrumentBindingList.add(defaultInstrument);
                instrumentManagementOverviewDialog.getInstrumentList().setSelectedIndex(instrumentBindingList.size() - 1);
            }
        });

        instrumentManagementOverviewDialog.getDeleteInstrumentButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (instrumentManagementOverviewDialog.getInstrumentList().getSelectedIndex() != -1) {
                    Instrument instrumentToDelete = getSelectedInstrument();
                    //check if the instrument is already has an id.
                    //If so, delete the instrument from the db.
                    if (instrumentToDelete.getId() != null) {
                        boolean deleted = instrumentService.checkUsageBeforeDeletion(instrumentToDelete);
                        if (!deleted) {
                            eventBus.post(new MessageEvent("Instrument deletion", "The instrument was used for one or more analytical runs stored in the database. "
                                    + "Hence, the instrument was not deleted.", JOptionPane.WARNING_MESSAGE));
                        } else {
                            instrumentBindingList.remove(instrumentManagementOverviewDialog.getInstrumentList().getSelectedIndex());
                            instrumentManagementOverviewDialog.getInstrumentList().getSelectionModel().clearSelection();
                        }
                    }
                }
            }
        });

        instrumentManagementOverviewDialog.getEditInstrumentButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (instrumentManagementOverviewDialog.getInstrumentList().getSelectedIndex() != -1) {
                    updateEditInstrumentDialog(getSelectedInstrument());
                    //show dialog
                    instrumentManagementDialog.setLocationRelativeTo(null);
                    instrumentManagementDialog.setVisible(true);
                }
            }
        });

        instrumentManagementOverviewDialog.getCloseInstrumentManagementButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                instrumentManagementOverviewDialog.setVisible(false);
            }
        });

    }

    private void initInstrumentManagementDialog() {
        instrumentManagementDialog = new InstrumentManagementDialog(mainController.getMainFrame(), true);

        //add binding
        Binding instrumentNameBinding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, instrumentManagementOverviewDialog.getInstrumentList(), ELProperty.create("${selectedElement.name}"), instrumentManagementDialog.getNameTextField(), BeanProperty.create("text"), "instrumentNameBinding");
        bindingGroup.addBinding(instrumentNameBinding);
        JComboBoxBinding instrumentTypeComboBoxBinding = SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ_WRITE, instrumentTypeBindingList, instrumentManagementDialog.getTypeComboBox());
        bindingGroup.addBinding(instrumentTypeComboBoxBinding);

        //set model and renderer
        cvTermSummaryListModel = new CvTermSummaryListModel();
        instrumentManagementDialog.getCvTermSummaryList().setModel(cvTermSummaryListModel);
        instrumentManagementDialog.getCvTermSummaryList().setCellRenderer(new CvTermSummaryCellRenderer<InstrumentCvTerm>());

        //add action listeners
        instrumentManagementDialog.getCvTermSummaryList().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    if (instrumentManagementDialog.getCvTermSummaryList().getSelectedIndex() != -1) {
                        //get selected cvTermType from summary list                        
                        CvTermType selectedcvTermType = (CvTermType) instrumentManagementDialog.getCvTermSummaryList().getSelectedValue();

                        //load duallist for the selected cvTermType
                        List<InstrumentCvTerm> availableCvTerms = cvTermService.findByCvTermByType(InstrumentCvTerm.class, selectedcvTermType);

                        List<InstrumentCvTerm> addedCvTerms;
                        if (cvTermSummaryListModel.isSingleCvTerm(selectedcvTermType)) {
                            addedCvTerms = new ArrayList<>();
                            InstrumentCvTerm instrumentCvTerm = cvTermSummaryListModel.getSingleCvTerms().get(selectedcvTermType);
                            //check for null value
                            if (instrumentCvTerm != null) {
                                addedCvTerms.add(instrumentCvTerm);
                            }
                            instrumentManagementDialog.getCvTermDualList().populateLists(availableCvTerms, addedCvTerms, 1);
                        } else {
                            addedCvTerms = cvTermSummaryListModel.getMultipleCvTerms().get(selectedcvTermType);
                            instrumentManagementDialog.getCvTermDualList().populateLists(availableCvTerms, addedCvTerms);
                        }
                    } else {
                        instrumentManagementDialog.getCvTermDualList().clear();
                    }
                }
            }
        });

        instrumentManagementDialog.getEditInstrumentTypesButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                instrumentTypeManagementDialog.setLocationRelativeTo(null);
                instrumentTypeManagementDialog.setVisible(true);
            }
        });

        instrumentManagementDialog.getCvTermDualList().addPropertyChangeListener(DualList.CHANGED, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                //get selected cvTermType                        
                CvTermType selectedcvTermType = (CvTermType) instrumentManagementDialog.getCvTermSummaryList().getSelectedValue();

                Instrument instrument = getSelectedInstrument();
                List<InstrumentCvTerm> addedItems = instrumentManagementDialog.getCvTermDualList().getAddedItems();

                //check for property
                if (selectedcvTermType.equals(CvTermType.SOURCE)) {
                    if (!addedItems.isEmpty()) {
                        InstrumentCvTerm source = addedItems.get(0);
                        instrument.setSource(source);
                        cvTermSummaryListModel.updateSingleCvTerm(CvTermType.SOURCE, source);
                    } else {
                        instrument.setSource(null);
                        cvTermSummaryListModel.updateSingleCvTerm(CvTermType.SOURCE, null);
                    }
                } else if (selectedcvTermType.equals(CvTermType.DETECTOR)) {
                    if (!addedItems.isEmpty()) {
                        InstrumentCvTerm detector = addedItems.get(0);
                        instrument.setDetector(detector);
                        cvTermSummaryListModel.updateSingleCvTerm(CvTermType.DETECTOR, detector);
                    } else {
                        instrument.setDetector(null);
                        cvTermSummaryListModel.updateSingleCvTerm(CvTermType.DETECTOR, null);
                    }
                } else if (selectedcvTermType.equals(CvTermType.ANALYZER)) {
                    instrument.setAnalyzers(addedItems);
                    cvTermSummaryListModel.updateMultipleCvTerm(CvTermType.ANALYZER, addedItems);
                }

            }
        });

        instrumentManagementDialog.getInstrumentSaveOrUpdateButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Instrument selectedInstrument = getSelectedInstrument();
                //validate instrument
                List<String> validationMessages = GuiUtils.validateEntity(selectedInstrument);
                //check for a new instrument if the instrument name already exists in the db                
                if (selectedInstrument.getId() == null && isExistingInstrumentName(selectedInstrument)) {
                    validationMessages.add(selectedInstrument.getName() + " already exists in the database, please choose another instrument name.");
                }
                if (validationMessages.isEmpty()) {
                    if (selectedInstrument.getId() != null) {
                        instrumentService.update(selectedInstrument);
                    } else {
                        instrumentService.save(selectedInstrument);
                    }
                    instrumentManagementDialog.getNameTextField().setEnabled(false);
                    instrumentManagementDialog.getInstrumentSaveOrUpdateButton().setText("update");

                    MessageEvent messageEvent = new MessageEvent("Instrument persist confirmation", "Instrument " + selectedInstrument.getName() + " was persisted successfully!", JOptionPane.INFORMATION_MESSAGE);
                    eventBus.post(messageEvent);

                    //refresh selection in instrument list in management overview dialog
                    int index = instrumentManagementOverviewDialog.getInstrumentList().getSelectedIndex();
                    instrumentManagementOverviewDialog.getInstrumentList().getSelectionModel().clearSelection();
                    instrumentManagementOverviewDialog.getInstrumentList().setSelectedIndex(index);
                } else {
                    MessageEvent messageEvent = new MessageEvent("Validation failure", validationMessages, JOptionPane.ERROR_MESSAGE);
                    eventBus.post(messageEvent);
                }
            }
        });

        instrumentManagementDialog.getTypeComboBox().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Instrument selectedInstrument = getSelectedInstrument();
                if (selectedInstrument != null) {
                    //set instrument type                    
                    InstrumentType selectedInstrumentType = instrumentTypeBindingList.get(instrumentManagementDialog.getTypeComboBox().getSelectedIndex());
                    selectedInstrument.setInstrumentType(selectedInstrumentType);
                }
            }
        });

        instrumentManagementDialog.getCloseEditInstrumentButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                instrumentManagementDialog.setVisible(false);
            }
        });

        instrumentManagementDialog.getEditInstrumentCvTermsButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //check if a CV term group is selected in the CV term summary list
                if (instrumentManagementDialog.getCvTermSummaryList().getSelectedIndex() != -1) {
                    //get selected cvTermType from summary list                        
                    CvTermType selectedcvTermType = (CvTermType) instrumentManagementDialog.getCvTermSummaryList().getSelectedValue();

                    List<CvTerm> cvTerms = cvTermService.findByCvTermByType(selectedcvTermType);

                    //update the CV term list
                    cvTermManagementController.updateDialog(selectedcvTermType, cvTerms);

                    cvTermManagementController.getCvTermManagementDialog().setLocationRelativeTo(null);
                    cvTermManagementController.getCvTermManagementDialog().setVisible(true);
                }
            }
        });
    }

    private void initInstrumentTypeManagementDialog() {
        instrumentTypeManagementDialog = new InstrumentTypeManagementDialog(mainController.getMainFrame(), true);

        instrumentTypeBindingList = ObservableCollections.observableList(instrumentTypeService.findAll());
        JListBinding instrumentTypeListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, instrumentTypeBindingList, instrumentTypeManagementDialog.getInstrumentTypeList());
        bindingGroup.addBinding(instrumentTypeListBinding);

        //instrument type bindings
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

                        //check if the instrument type has an ID.
                        //If so, disable the name text field and change the save button label.
                        if (instrumentType.getId() != null) {
                            instrumentTypeManagementDialog.getInstrumentTypeNameTextField().setEnabled(false);
                            instrumentTypeManagementDialog.getInstrumentTypeSaveOrUpdateButton().setText("update");
                            instrumentTypeManagementDialog.getInstrumentTypeStateInfoLabel().setText("");
                        } else {
                            instrumentTypeManagementDialog.getInstrumentTypeNameTextField().setEnabled(true);
                            instrumentTypeManagementDialog.getInstrumentTypeSaveOrUpdateButton().setText("save");
                            instrumentTypeManagementDialog.getInstrumentTypeStateInfoLabel().setText("This instrument type hasn't been persisted to the database.");
                        }
                    } else {
                        instrumentTypeManagementDialog.getInstrumentTypeSaveOrUpdateButton().setEnabled(false);
                        clearInstrumentTypeDetailFields();
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

                    //check if permission has an id.
                    //If so, try to delete the permission from the db.
                    if (instrumentTypeToDelete.getId() != null) {
                        try {
                            instrumentTypeService.delete(instrumentTypeToDelete);

                            instrumentTypeBindingList.remove(instrumentTypeManagementDialog.getInstrumentTypeList().getSelectedIndex());
                            instrumentTypeManagementDialog.getInstrumentTypeList().getSelectionModel().clearSelection();
                            //clearInstrumentTypeDetailFields();
                        } catch (DataIntegrityViolationException dive) {
                            //check if the instrument type can be deleted without breaking existing database relations,
                            //i.e. are there any constraints violations
                            if (dive.getCause() instanceof ConstraintViolationException) {
                                MessageEvent messageEvent = new MessageEvent("CV term delete", "CV term " + instrumentTypeToDelete.getName() + " cannot be deleted because it is being used by instrument(s).", JOptionPane.INFORMATION_MESSAGE);
                                eventBus.post(messageEvent);
                            } else {
                                //pass the exception
                                throw dive;
                            }
                        }
                    } else {
                        instrumentTypeBindingList.remove(instrumentTypeManagementDialog.getInstrumentTypeList().getSelectedIndex());
                        instrumentTypeManagementDialog.getInstrumentTypeList().getSelectionModel().clearSelection();
                        //clearInstrumentTypeDetailFields();
                    }
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
                if (selectedInstrumentType.getId() != null && isExistingInstrumentTypeName(selectedInstrumentType)) {
                    validationMessages.add(selectedInstrumentType.getName() + " already exists in the database, please choose another instrument type name.");
                }
                if (validationMessages.isEmpty()) {
                    if (selectedInstrumentType.getId() != null) {
                        instrumentTypeService.update(selectedInstrumentType);
                    } else {
                        instrumentTypeService.save(selectedInstrumentType);
                        //refresh permission list
                        instrumentTypeManagementDialog.getInstrumentTypeList().updateUI();
                    }
                    instrumentTypeManagementDialog.getInstrumentTypeNameTextField().setEnabled(false);
                    instrumentTypeManagementDialog.getInstrumentTypeSaveOrUpdateButton().setText("update");
                    instrumentTypeManagementDialog.getInstrumentTypeStateInfoLabel().setText("");

                    MessageEvent messageEvent = new MessageEvent("Instrument type persist confirmation", "Instrument type " + selectedInstrumentType.getName() + " was persisted successfully!", JOptionPane.INFORMATION_MESSAGE);
                    eventBus.post(messageEvent);
                } else {
                    MessageEvent messageEvent = new MessageEvent("Validation failure", validationMessages, JOptionPane.ERROR_MESSAGE);
                    eventBus.post(messageEvent);
                }
            }
        });

        instrumentTypeManagementDialog.getCloseInstrumentTypeManagementButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                instrumentTypeManagementDialog.setVisible(false);
            }
        });
    }

    @Override
    public void insertOLSResult(String field, String selectedValue, String accession, String ontologyShort, String ontologyLong, int modifiedRow, String mappedTerm, Map<String, String> metadata) {
        if (field.equalsIgnoreCase("addAnalyzerButton")) {
            InstrumentCvTerm analyzer = new InstrumentCvTerm(CvTermType.ANALYZER, ontologyLong, ontologyShort, accession, selectedValue);
            addAnalyzer(getSelectedInstrument(), analyzer);
        }
    }

    @Override
    public Window getWindow() {
        return instrumentManagementOverviewDialog;
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
            InstrumentCvTerm foundAnalyzer = (InstrumentCvTerm) cvTermService.findByAccession(analyzer.getAccession(), CvTermType.ANALYZER);
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
     * Check if a instrument with the given instrument type name exists in the
     * database.
     *
     * @param instrument the instrument
     * @return does the instrument name exist
     */
    private boolean isExistingInstrumentName(Instrument instrument) {
        boolean isExistingInstrumentName = true;
        Instrument foundInstrument = instrumentService.findByName(instrument.getName());
        if (foundInstrument == null) {
            isExistingInstrumentName = false;
        }

        return isExistingInstrumentName;
    }

    /**
     * Get the selected instrument in the instrument JList.
     *
     * @return the selected instrument
     */
    private Instrument getSelectedInstrument() {
        int selectedIndex = instrumentManagementOverviewDialog.getInstrumentList().getSelectedIndex();
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
        List<InstrumentCvTerm> sources = cvTermService.findByCvTermByType(InstrumentCvTerm.class, CvTermType.SOURCE);
        if (!sources.isEmpty()) {
            defaultInstrument.setSource(sources.get(0));
        }
        //find detectors
        List<InstrumentCvTerm> detectors = cvTermService.findByCvTermByType(InstrumentCvTerm.class, CvTermType.DETECTOR);
        if (!detectors.isEmpty()) {
            defaultInstrument.setDetector(detectors.get(0));
        }
        //find analyzers
        List<InstrumentCvTerm> analyzers = cvTermService.findByCvTermByType(InstrumentCvTerm.class, CvTermType.ANALYZER);
        if (!analyzers.isEmpty()) {
            List<InstrumentCvTerm> defaultAnalyzers = new ArrayList<>();
            defaultAnalyzers.add(analyzers.get(0));
            defaultInstrument.setAnalyzers(defaultAnalyzers);
        }
        return defaultInstrument;
    }

    private void updateEditInstrumentDialog(Instrument instrument) {
        //set the selected item in the instrument type combobox        
        instrumentManagementDialog.getTypeComboBox().setSelectedItem(instrument.getInstrumentType());

        if (instrument.getId() != null) {
            instrumentManagementDialog.getInstrumentSaveOrUpdateButton().setText("update");
        } else {
            instrumentManagementDialog.getInstrumentSaveOrUpdateButton().setText("save");
        }

        //add the single CV terms
        EnumMap<CvTermType, InstrumentCvTerm> singleCvTerms = new EnumMap<>(CvTermType.class);
        singleCvTerms.put(CvTermType.SOURCE, instrument.getSource());
        singleCvTerms.put(CvTermType.DETECTOR, instrument.getDetector());

        //add the multiple CV terms
        EnumMap<CvTermType, List<InstrumentCvTerm>> multipleCvTerms = new EnumMap<>(CvTermType.class);
        multipleCvTerms.put(CvTermType.ANALYZER, instrument.getAnalyzers());
        cvTermSummaryListModel.update(singleCvTerms, multipleCvTerms);
    }

    /**
     * Clear the instrument type detail fields
     */
    private void clearInstrumentTypeDetailFields() {
        instrumentTypeManagementDialog.getInstrumentTypeNameTextField().setText("");
        instrumentTypeManagementDialog.getInstrumentTypeDescriptionTextArea().setText("");
    }
}
