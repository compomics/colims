package com.compomics.colims.client.controller.admin;

import com.compomics.colims.client.compoment.DualList;
import com.compomics.colims.client.controller.Controllable;
import com.compomics.colims.client.controller.ColimsController;
import com.compomics.colims.client.event.EntityChangeEvent;
import com.compomics.colims.client.event.InstrumentChangeEvent;
import com.compomics.colims.client.event.admin.CvTermChangeEvent;
import com.compomics.colims.client.event.message.DbConstraintMessageEvent;
import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.model.CvTermSummaryListModel;
import com.compomics.colims.client.model.CvTermTableModel;
import com.compomics.colims.client.renderer.CvTermSummaryCellRenderer;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.admin.instrument.InstrumentEditDialog;
import com.compomics.colims.client.view.admin.instrument.InstrumentManagementDialog;
import com.compomics.colims.client.view.admin.instrument.InstrumentTypeCrudDialog;
import com.compomics.colims.core.service.CvTermService;
import com.compomics.colims.core.service.InstrumentService;
import com.compomics.colims.core.service.InstrumentTypeService;
import com.compomics.colims.model.CvTerm;
import com.compomics.colims.model.Instrument;
import com.compomics.colims.model.InstrumentCvTerm;
import com.compomics.colims.model.InstrumentType;
import com.compomics.colims.model.comparator.CvTermAccessionComparator;
import com.compomics.colims.model.enums.CvTermType;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
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
public class InstrumentManagementController implements Controllable {

    //model      
    private CvTermSummaryListModel<InstrumentCvTerm> cvTermSummaryListModel;
    private ObservableList<Instrument> instrumentBindingList;
    private ObservableList<InstrumentType> instrumentTypeBindingList;
    private BindingGroup bindingGroup;
    private Instrument instrumentToEdit;
    //view
    private InstrumentManagementDialog instrumentManagementDialog;
    private InstrumentEditDialog instrumentEditDialog;
    private InstrumentTypeCrudDialog instrumentTypeCrudDialog;
    //parent controller
    @Autowired
    private ColimsController colimsController;
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

    /**
     *
     */
    public InstrumentManagementController() {
    }

    /**
     *
     * @return
     */
    public InstrumentManagementDialog getInstrumentManagementOverviewDialog() {
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
        initInstrumentTypeCrudDialog();
        initInstrumentEditDialog();

        bindingGroup.bind();
    }

    @Override
    public void showView() {
        //clear selection
        instrumentManagementDialog.getInstrumentList().getSelectionModel().clearSelection();

        GuiUtils.centerDialogOnComponent(colimsController.getColimsFrame(), instrumentManagementDialog);
        instrumentManagementDialog.setVisible(true);
    }

    /**
     * Listen to a CV term change event posted by the
     * CvTermManagementController. If the InstrumentManagementDialog is visible,
     * clear the selection in the CV term summary list.
     * 
     * @param cvTermChangeEvent
     */
    @Subscribe
    public void onCvTermChangeEvent(CvTermChangeEvent cvTermChangeEvent) {
        if (instrumentEditDialog.isVisible()) {
            instrumentEditDialog.getCvTermSummaryList().getSelectionModel().clearSelection();
        }
    }

    private void initInstrumentManagementDialog() {
        instrumentManagementDialog = new InstrumentManagementDialog(colimsController.getColimsFrame(), true);

        //add binding
        instrumentBindingList = ObservableCollections.observableList(instrumentService.findAll());
        JListBinding instrumentListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, instrumentBindingList, instrumentManagementDialog.getInstrumentList());
        bindingGroup.addBinding(instrumentListBinding);

        //add action listeners
        instrumentManagementDialog.getInstrumentList().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedIndex = instrumentManagementDialog.getInstrumentList().getSelectedIndex();
                    if (selectedIndex != -1 && instrumentBindingList.get(selectedIndex) != null) {
                        Instrument selectedInstrument = instrumentBindingList.get(selectedIndex);

                        //init CvTermModel
                        List<CvTerm> cvTerms = new ArrayList<>();
                        if (selectedInstrument.getSource() != null) {
                            cvTerms.add(selectedInstrument.getSource());
                        }
                        if (selectedInstrument.getDetector() != null) {
                            cvTerms.add(selectedInstrument.getDetector());
                        }
                        for (InstrumentCvTerm analyzer : selectedInstrument.getAnalyzers()) {
                            cvTerms.add(analyzer);
                        }
                        CvTermTableModel cvTermTableModel = new CvTermTableModel(cvTerms);
                        instrumentManagementDialog.getInstrumentDetailsTable().setModel(cvTermTableModel);
                    } else {
                        //clear detail view
                        clearInstrumentDetailFields();
                    }
                }
            }
        });

        instrumentManagementDialog.getAddInstrumentButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateInstrumentEditDialog(createDefaultInstrument());

                //show dialog
                GuiUtils.centerDialogOnComponent(instrumentManagementDialog, instrumentEditDialog);
                instrumentEditDialog.setVisible(true);
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
                        try {
                            instrumentService.delete(instrumentToDelete);

                            instrumentBindingList.remove(instrumentManagementDialog.getInstrumentList().getSelectedIndex());
                            instrumentManagementDialog.getInstrumentList().getSelectionModel().clearSelection();
                            
                            eventBus.post(new InstrumentChangeEvent(EntityChangeEvent.Type.DELETED));
                        } catch (DataIntegrityViolationException dive) {
                            //check if the instrument can be deleted without breaking existing database relations,
                            //i.e. are there any constraints violations
                            if (dive.getCause() instanceof ConstraintViolationException) {
                                DbConstraintMessageEvent dbConstraintMessageEvent = new DbConstraintMessageEvent("instrument", instrumentToDelete.getName());
                                eventBus.post(dbConstraintMessageEvent);
                            } else {
                                //pass the exception
                                throw dive;
                            }
                        }
                    } else {
                        instrumentBindingList.remove(instrumentManagementDialog.getInstrumentList().getSelectedIndex());
                        instrumentManagementDialog.getInstrumentList().getSelectionModel().clearSelection();
                    }
                }
            }
        });

        instrumentManagementDialog.getEditInstrumentButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (instrumentManagementDialog.getInstrumentList().getSelectedIndex() != -1) {
                    updateInstrumentEditDialog(getSelectedInstrument());

                    //show dialog
                    GuiUtils.centerDialogOnComponent(instrumentManagementDialog, instrumentEditDialog);
                    instrumentEditDialog.setVisible(true);
                }
            }
        });

        instrumentManagementDialog.getCancelInstrumentManagementButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                instrumentManagementDialog.dispose();
            }
        });

    }

    private void initInstrumentEditDialog() {
        instrumentEditDialog = new InstrumentEditDialog(instrumentManagementDialog, true);

        //init dual list
        instrumentEditDialog.getCvTermDualList().init(new CvTermAccessionComparator());

        //add binding        
        JComboBoxBinding instrumentTypeComboBoxBinding = SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ_WRITE, instrumentTypeBindingList, instrumentEditDialog.getTypeComboBox());
        bindingGroup.addBinding(instrumentTypeComboBoxBinding);

        //set model and renderer
        cvTermSummaryListModel = new CvTermSummaryListModel();
        instrumentEditDialog.getCvTermSummaryList().setModel(cvTermSummaryListModel);
        instrumentEditDialog.getCvTermSummaryList().setCellRenderer(new CvTermSummaryCellRenderer<InstrumentCvTerm>());

        //add action listeners
        instrumentEditDialog.getCvTermSummaryList().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    if (instrumentEditDialog.getCvTermSummaryList().getSelectedIndex() != -1) {
                        //get selected cvTermType from summary list                        
                        CvTermType selectedcvTermType = (CvTermType) instrumentEditDialog.getCvTermSummaryList().getSelectedValue();

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
                            instrumentEditDialog.getCvTermDualList().populateLists(availableCvTerms, addedCvTerms, 1);
                        } else {
                            addedCvTerms = cvTermSummaryListModel.getMultiCvTerms().get(selectedcvTermType);
                            instrumentEditDialog.getCvTermDualList().populateLists(availableCvTerms, addedCvTerms);
                        }
                    } else {
                        instrumentEditDialog.getCvTermDualList().clear();
                    }
                }
            }
        });

        instrumentEditDialog.getInstrumentTypesCrudButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GuiUtils.centerDialogOnComponent(instrumentEditDialog, instrumentTypeCrudDialog);
                instrumentTypeCrudDialog.setVisible(true);
            }
        });

        instrumentEditDialog.getCvTermDualList().addPropertyChangeListener(DualList.CHANGED, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                //get selected cvTermType                        
                CvTermType selectedcvTermType = (CvTermType) instrumentEditDialog.getCvTermSummaryList().getSelectedValue();

                List<InstrumentCvTerm> addedItems = (List<InstrumentCvTerm>) evt.getNewValue();

                //check for property
                if (selectedcvTermType.equals(CvTermType.SOURCE)) {
                    if (!addedItems.isEmpty()) {
                        InstrumentCvTerm source = addedItems.get(0);
                        instrumentToEdit.setSource(source);
                        cvTermSummaryListModel.updateSingleCvTerm(CvTermType.SOURCE, source);
                    } else {
                        instrumentToEdit.setSource(null);
                        cvTermSummaryListModel.updateSingleCvTerm(CvTermType.SOURCE, null);
                    }
                } else if (selectedcvTermType.equals(CvTermType.DETECTOR)) {
                    if (!addedItems.isEmpty()) {
                        InstrumentCvTerm detector = addedItems.get(0);
                        instrumentToEdit.setDetector(detector);
                        cvTermSummaryListModel.updateSingleCvTerm(CvTermType.DETECTOR, detector);
                    } else {
                        instrumentToEdit.setDetector(null);
                        cvTermSummaryListModel.updateSingleCvTerm(CvTermType.DETECTOR, null);
                    }
                } else if (selectedcvTermType.equals(CvTermType.ANALYZER)) {
                    instrumentToEdit.setAnalyzers(addedItems);
                    cvTermSummaryListModel.updateMultiCvTerm(CvTermType.ANALYZER, addedItems);
                }

            }
        });

        instrumentEditDialog.getInstrumentSaveOrUpdateButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //update with dialog input
                updateInstrumentToEdit();

                //validate instrument
                List<String> validationMessages = GuiUtils.validateEntity(instrumentToEdit);
                //check for a new instrument if the instrument name already exists in the db                
                if (instrumentToEdit.getId() == null && isExistingInstrumentName(instrumentToEdit)) {
                    validationMessages.add(instrumentToEdit.getName() + " already exists in the database,"
                            + "\n" + "please choose another instrument name.");
                }                
                if (validationMessages.isEmpty()) {
                    int index;
                    EntityChangeEvent.Type type;
                    if (instrumentToEdit.getId() != null) {
                        instrumentService.update(instrumentToEdit);
                        index = instrumentManagementDialog.getInstrumentList().getSelectedIndex();                        
                        type = EntityChangeEvent.Type.UPDATED;
                    } else {
                        instrumentService.save(instrumentToEdit);
                        //add instrument to overview list
                        instrumentBindingList.add(instrumentToEdit);
                        index = instrumentBindingList.size() - 1;
                        instrumentEditDialog.getInstrumentStateInfoLabel().setText("");
                        type = EntityChangeEvent.Type.CREATED;
                    }
                    instrumentEditDialog.getInstrumentSaveOrUpdateButton().setText("update");
                    
                    eventBus.post(new InstrumentChangeEvent(type));

                    MessageEvent messageEvent = new MessageEvent("Instrument store confirmation", "Instrument " + instrumentToEdit.getName() + " was stored successfully!", JOptionPane.INFORMATION_MESSAGE);
                    eventBus.post(messageEvent);

                    //refresh selection in instrument list in management overview dialog                    
                    instrumentManagementDialog.getInstrumentList().getSelectionModel().clearSelection();
                    instrumentManagementDialog.getInstrumentList().setSelectedIndex(index);
                } else {
                    MessageEvent messageEvent = new MessageEvent("Validation failure", validationMessages, JOptionPane.WARNING_MESSAGE);
                    eventBus.post(messageEvent);
                }
            }
        });

        instrumentEditDialog.getTypeComboBox().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (instrumentToEdit != null) {
                    //set instrument type 
                    if (!instrumentTypeBindingList.isEmpty()) {
                        InstrumentType selectedInstrumentType = instrumentTypeBindingList.get(instrumentEditDialog.getTypeComboBox().getSelectedIndex());
                        instrumentToEdit.setInstrumentType(selectedInstrumentType);
                    }
                }
            }
        });

        instrumentEditDialog.getCancelInstrumentEditButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                instrumentEditDialog.dispose();
            }
        });

        instrumentEditDialog.getInstrumentCvTermsCrudButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //check if a CV term group is selected in the CV term summary list
                if (instrumentEditDialog.getCvTermSummaryList().getSelectedIndex() != -1) {
                    //get selected cvTermType from summary list                        
                    CvTermType selectedcvTermType = (CvTermType) instrumentEditDialog.getCvTermSummaryList().getSelectedValue();

                    List<CvTerm> cvTerms = cvTermService.findByCvTermByType(selectedcvTermType);

                    //update the CV term list
                    cvTermManagementController.updateDialog(selectedcvTermType, cvTerms);

                    cvTermManagementController.showView();
                }
            }
        });
    }

    private void initInstrumentTypeCrudDialog() {
        instrumentTypeCrudDialog = new InstrumentTypeCrudDialog(instrumentEditDialog, true);

        instrumentTypeBindingList = ObservableCollections.observableList(instrumentTypeService.findAll());
        JListBinding instrumentTypeListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, instrumentTypeBindingList, instrumentTypeCrudDialog.getInstrumentTypeList());
        bindingGroup.addBinding(instrumentTypeListBinding);

        //instrument type bindings
        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, instrumentTypeCrudDialog.getInstrumentTypeList(), BeanProperty.create("selectedElement.name"), instrumentTypeCrudDialog.getInstrumentTypeNameTextField(), ELProperty.create("${text}"), "nameBinding");
        bindingGroup.addBinding(binding);
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, instrumentTypeCrudDialog.getInstrumentTypeList(), BeanProperty.create("selectedElement.description"), instrumentTypeCrudDialog.getInstrumentTypeDescriptionTextArea(), ELProperty.create("${text}"), "descriptionBinding");
        bindingGroup.addBinding(binding);

        //add listeners
        instrumentTypeCrudDialog.getInstrumentTypeList().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    if (instrumentTypeCrudDialog.getInstrumentTypeList().getSelectedIndex() != -1) {
                        InstrumentType instrumentType = getSelectedInstrumentType();

                        //enable save and delete button
                        instrumentTypeCrudDialog.getInstrumentTypeSaveOrUpdateButton().setEnabled(true);
                        instrumentTypeCrudDialog.getDeleteInstrumentTypeButton().setEnabled(true);

                        //check if the instrument type has an ID.
                        //If so, disable the name text field and change the save button label.
                        if (instrumentType.getId() != null) {
                            instrumentTypeCrudDialog.getInstrumentTypeNameTextField().setEnabled(false);
                            instrumentTypeCrudDialog.getInstrumentTypeSaveOrUpdateButton().setText("update");
                            instrumentTypeCrudDialog.getInstrumentTypeStateInfoLabel().setText("");
                        } else {
                            instrumentTypeCrudDialog.getInstrumentTypeNameTextField().setEnabled(true);
                            instrumentTypeCrudDialog.getInstrumentTypeSaveOrUpdateButton().setText("save");
                            instrumentTypeCrudDialog.getInstrumentTypeStateInfoLabel().setText("This instrument type hasn't been persisted to the database.");
                        }
                    } else {
                        instrumentTypeCrudDialog.getInstrumentTypeSaveOrUpdateButton().setEnabled(false);
                        clearInstrumentTypeDetailFields();
                    }
                }
            }
        });

        instrumentTypeCrudDialog.getAddInstrumentTypeButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                InstrumentType newInstrumentType = new InstrumentType();
                newInstrumentType.setName("name");
                instrumentTypeBindingList.add(newInstrumentType);
                instrumentTypeCrudDialog.getInstrumentTypeNameTextField().setEnabled(true);
                instrumentTypeCrudDialog.getInstrumentTypeList().setSelectedIndex(instrumentTypeBindingList.size() - 1);
            }
        });

        instrumentTypeCrudDialog.getDeleteInstrumentTypeButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (instrumentTypeCrudDialog.getInstrumentTypeList().getSelectedIndex() != -1) {
                    InstrumentType instrumentTypeToDelete = getSelectedInstrumentType();

                    //check if the instrument type has an id.
                    //If so, try to delete the instrument type from the db.
                    if (instrumentTypeToDelete.getId() != null) {
                        try {
                            instrumentTypeService.delete(instrumentTypeToDelete);

                            instrumentTypeBindingList.remove(instrumentTypeCrudDialog.getInstrumentTypeList().getSelectedIndex());
                            instrumentTypeCrudDialog.getInstrumentTypeList().getSelectionModel().clearSelection();
                            //clearInstrumentTypeDetailFields();
                        } catch (DataIntegrityViolationException dive) {
                            //check if the instrument type can be deleted without breaking existing database relations,
                            //i.e. are there any constraints violations
                            if (dive.getCause() instanceof ConstraintViolationException) {
                                DbConstraintMessageEvent dbConstraintMessageEvent = new DbConstraintMessageEvent("instrument type", instrumentTypeToDelete.getName());
                                eventBus.post(dbConstraintMessageEvent);
                            } else {
                                //pass the exception
                                throw dive;
                            }
                        }
                    } else {
                        instrumentTypeBindingList.remove(instrumentTypeCrudDialog.getInstrumentTypeList().getSelectedIndex());
                        instrumentTypeCrudDialog.getInstrumentTypeList().getSelectionModel().clearSelection();
                        //clearInstrumentTypeDetailFields();
                    }
                }
            }
        });

        instrumentTypeCrudDialog.getInstrumentTypeSaveOrUpdateButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                InstrumentType selectedInstrumentType = getSelectedInstrumentType();
                //validate instrument type
                List<String> validationMessages = GuiUtils.validateEntity(selectedInstrumentType);
                //check for a new instrument type if the instrument type name already exists in the db                
                if (selectedInstrumentType.getId() != null && isExistingInstrumentTypeName(selectedInstrumentType)) {
                    validationMessages.add(selectedInstrumentType.getName() + " already exists in the database,"
                            + "\n" + "please choose another instrument type name.");
                }
                if (validationMessages.isEmpty()) {
                    if (selectedInstrumentType.getId() != null) {
                        instrumentTypeService.update(selectedInstrumentType);
                    } else {
                        instrumentTypeService.save(selectedInstrumentType);
                        //refresh instrument type list
                        instrumentTypeCrudDialog.getInstrumentTypeList().updateUI();
                    }
                    instrumentTypeCrudDialog.getInstrumentTypeNameTextField().setEnabled(false);
                    instrumentTypeCrudDialog.getInstrumentTypeSaveOrUpdateButton().setText("update");
                    instrumentTypeCrudDialog.getInstrumentTypeStateInfoLabel().setText("");

                    MessageEvent messageEvent = new MessageEvent("Instrument type store confirmation", "Instrument type " + selectedInstrumentType.getName() + " was stored successfully!", JOptionPane.INFORMATION_MESSAGE);
                    eventBus.post(messageEvent);
                } else {
                    MessageEvent messageEvent = new MessageEvent("Validation failure", validationMessages, JOptionPane.WARNING_MESSAGE);
                    eventBus.post(messageEvent);
                }
            }
        });

        instrumentTypeCrudDialog.getCancelInstrumentTypeCrudButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                instrumentTypeCrudDialog.dispose();
            }
        });
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
     * Check if a instrument with the given instrument name exists in the
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
        int selectedIndex = instrumentTypeCrudDialog.getInstrumentTypeList().getSelectedIndex();
        InstrumentType selectedInstrumentType = (selectedIndex != -1) ? instrumentTypeBindingList.get(selectedIndex) : null;
        return selectedInstrumentType;
    }

    /**
     * Create a default instrument, with some default properties.
     *
     * @return the default instrument
     */
    private Instrument createDefaultInstrument() {
        Instrument defaultInstrument = new Instrument("default instrument name");
        //find instrument types
        List<InstrumentType> instrumentTypes = instrumentTypeService.findAll();
        if (!instrumentTypes.isEmpty()) {
            defaultInstrument.setInstrumentType(instrumentTypes.get(0));
        }
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

    /**
     * Update the instrumentToEdit with input from the instrumentEditDialog
     */
    public void updateInstrumentToEdit() {
        instrumentToEdit.setName(instrumentEditDialog.getNameTextField().getText());
    }

    /**
     * Update the instrument edit dialog with the given instrument
     *
     * @param instrument
     */
    private void updateInstrumentEditDialog(Instrument instrument) {
        instrumentToEdit = instrument;

        //check if the instrument has an ID.
        //If so, change the save button text and the info state label.
        if (instrumentToEdit.getId() != null) {
            instrumentEditDialog.getInstrumentSaveOrUpdateButton().setText("update");
            instrumentEditDialog.getInstrumentStateInfoLabel().setText("");
        } else {
            instrumentEditDialog.getInstrumentSaveOrUpdateButton().setText("save");
            instrumentEditDialog.getInstrumentStateInfoLabel().setText("This instrument hasn't been persisted to the database.");
        }

        instrumentEditDialog.getNameTextField().setText(instrumentToEdit.getName());

        //add the single CV terms
        EnumMap<CvTermType, InstrumentCvTerm> singleCvTerms = new EnumMap<>(CvTermType.class);
        singleCvTerms.put(CvTermType.SOURCE, instrumentToEdit.getSource());
        singleCvTerms.put(CvTermType.DETECTOR, instrumentToEdit.getDetector());

        //add the multiple CV terms
        EnumMap<CvTermType, List<InstrumentCvTerm>> multipleCvTerms = new EnumMap<>(CvTermType.class);
        multipleCvTerms.put(CvTermType.ANALYZER, instrumentToEdit.getAnalyzers());
        cvTermSummaryListModel.update(singleCvTerms, multipleCvTerms);

        //set the selected item in the instrument type combobox        
        instrumentEditDialog.getTypeComboBox().setSelectedItem(instrumentToEdit.getInstrumentType());

        //clear selection in CV term summary list
        instrumentEditDialog.getCvTermSummaryList().getSelectionModel().clearSelection();
    }

    /**
     * Clear the instrument detail fields
     */
    private void clearInstrumentDetailFields() {
        instrumentManagementDialog.getInstrumentDetailsTable().setModel(new CvTermTableModel());
    }

    /**
     * Clear the instrument type detail fields
     */
    private void clearInstrumentTypeDetailFields() {
        instrumentTypeCrudDialog.getInstrumentTypeStateInfoLabel().setText("");
        instrumentTypeCrudDialog.getInstrumentTypeNameTextField().setText("");
        instrumentTypeCrudDialog.getInstrumentTypeDescriptionTextArea().setText("");
    }
}
