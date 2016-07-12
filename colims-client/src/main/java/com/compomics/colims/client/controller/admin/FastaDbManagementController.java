package com.compomics.colims.client.controller.admin;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import com.compomics.colims.client.controller.AnalyticalRunsAdditionController;
import com.compomics.colims.client.controller.Controllable;
import com.compomics.colims.client.event.EntityChangeEvent;
import com.compomics.colims.client.event.admin.CvParamChangeEvent;
import com.compomics.colims.client.event.message.DbConstraintMessageEvent;
import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.admin.FastaDbManagementDialog;
import com.compomics.colims.core.service.CvParamService;
import com.compomics.colims.core.service.FastaDbService;
import com.compomics.colims.model.FastaDb;
import com.compomics.colims.model.TaxonomyCvParam;
import com.compomics.colims.model.comparator.IdComparator;
import com.compomics.colims.model.cv.CvParam;
import com.compomics.colims.model.enums.FastaDbType;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.awt.CardLayout;
import java.awt.Dimension;
import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.jdesktop.beansbinding.*;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jdesktop.swingbinding.JComboBoxBinding;

/**
 * The FASTA db management view controller.
 *
 * @author Niels Hulstaert
 */
@Component("fastaDbManagementController")
@Lazy
public class FastaDbManagementController implements Controllable {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(FastaDbManagementController.class);

    /**
     * The preselected ontology namespaces.
     */
    private static final List<String> PRESELECTED_ONTOLOGY_NAMESPACES = Arrays.asList("ncbitaxon");
    /**
     * The default taxonomy value for the taxonomy combo box.
     */
    private static final TaxonomyCvParam TAXONOMY_CV_PARAM_NONE = new TaxonomyCvParam("none", "none", "none", "none");

    
    private static final String FASTA_DB_SAVE_UPDATE_PANEL = "fastaDbSaveUpdatePanel";
    //model
    private BindingGroup bindingGroup;
    private ObservableList<FastaDb> fastaDbBindingList;
    private ObservableList<CvParam> taxonomyBindingList;
    
    private DefaultEventSelectionModel<FastaDb> fastaDbSelectionModel;
    //view
    private FastaDbManagementDialog fastaDbManagementDialog;
    //parent controller
    @Autowired
    private AnalyticalRunsAdditionController analyticalRunsAdditionController;
    //services
    @Autowired
    private EventBus eventBus;
    @Autowired
    private CvParamService cvParamService;
    @Autowired
    private FastaDbService fastaDbService;
    @Autowired
    @Lazy
    private FastaDbSaveUpdateController fastaDbSaveUpdateController;
    
    @Override
    @PostConstruct
    public void init() {
        //init view
        fastaDbManagementDialog = new FastaDbManagementDialog(analyticalRunsAdditionController.getAnalyticalRunsAdditionDialog(), true);

        //register to event bus
        eventBus.register(this);
        // init fastaDbSaveUpdate
        fastaDbSaveUpdateController.init();
        //init binding
        bindingGroup = new BindingGroup();

        fastaDbBindingList = ObservableCollections.observableList(new ArrayList<>());
        JListBinding fastaDbListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, fastaDbBindingList, fastaDbManagementDialog.getFastaDbList());
        bindingGroup.addBinding(fastaDbListBinding);
        
        taxonomyBindingList = ObservableCollections.observableList(new ArrayList<>());
        taxonomyBindingList.add(TAXONOMY_CV_PARAM_NONE);
        taxonomyBindingList.addAll(cvParamService.findByCvParamByClass(TaxonomyCvParam.class));
        JComboBoxBinding taxonomyComboBoxBinding = SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ_WRITE, taxonomyBindingList, fastaDbManagementDialog.getTaxomomyComboBox());
        bindingGroup.addBinding(taxonomyComboBoxBinding);

        //selected fasta database bindings
        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, fastaDbManagementDialog.getFastaDbList(), BeanProperty.create("selectedElement.name"), fastaDbManagementDialog.getNameTextField(), ELProperty.create("${text}"), "nameBinding");
        bindingGroup.addBinding(binding);
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, fastaDbManagementDialog.getFastaDbList(), BeanProperty.create("selectedElement.fileName"), fastaDbManagementDialog.getFileNameTextField(), ELProperty.create("${text}"), "fileNameBinding");
        bindingGroup.addBinding(binding);
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, fastaDbManagementDialog.getFastaDbList(), BeanProperty.create("selectedElement.filePath"), fastaDbManagementDialog.getFilePathTextField(), ELProperty.create("${text}"), "filePathBinding");
        bindingGroup.addBinding(binding);
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, fastaDbManagementDialog.getFastaDbList(), BeanProperty.create("selectedElement.version"), fastaDbManagementDialog.getVersionTextField(), ELProperty.create("${text}"), "versionBinding");
        bindingGroup.addBinding(binding);
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, fastaDbManagementDialog.getFastaDbList(), BeanProperty.create("selectedElement.headerParseRule"), fastaDbManagementDialog.getHeaderParseRuleTextField(), ELProperty.create("${text}"), "headerParseRuleBinding");
        bindingGroup.addBinding(binding);

        bindingGroup.bind();
        fastaDbBindingList.addAll(fastaDbService.findAll());
        EventList<FastaDb> fastaDbList = new BasicEventList<>();
        fastaDbList.addAll(fastaDbBindingList);
        SortedList<FastaDb> sortedFastaDbList= new SortedList<>(fastaDbList, new IdComparator());
        fastaDbSelectionModel = new DefaultEventSelectionModel<>(sortedFastaDbList);
        fastaDbSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fastaDbManagementDialog.getFastaDbList().setSelectionModel(fastaDbSelectionModel);

        fastaDbManagementDialog.getFastaDbList().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                if (fastaDbManagementDialog.getFastaDbList().getSelectedIndex() != -1) {
                    FastaDb fastaDb = getSelectedFastaDb();

                    if (fastaDb.getTaxonomy() == null) {
                        fastaDbManagementDialog.getTaxomomyComboBox().setSelectedIndex(0);
                    } else {
                        fastaDbManagementDialog.getTaxomomyComboBox().getModel().setSelectedItem(fastaDb.getTaxonomy());
                    }

                    //enable delete button
                    fastaDbManagementDialog.getDeleteButton().setEnabled(true);

                    //check if the FASTA DB has an ID.
                    //If so, disable the name text field and change the save button label.
                    if (fastaDb.getId() != null) {
                        fastaDbManagementDialog.getFastaDbStateInfoLabel().setText("");
                    } else {

                        fastaDbManagementDialog.getFastaDbStateInfoLabel().setText("This fasta DB hasn't been stored in the database.");
                    }
                } else {
                    clearFastaDbDetailFields();
                }
            }
        });
    
        fastaDbManagementDialog.getAddButton().addActionListener(e -> {
            fastaDbSaveUpdateController.clearFastaDbDetailFields();
            // create new fasta db
            FastaDb newFastaDb = new FastaDb();
            newFastaDb.setName("name");
            // send the new fastaDb to fastaDbSaveUpdateController
            newFastaDb.setTaxonomy(TAXONOMY_CV_PARAM_NONE);
            fastaDbSaveUpdateController.updateView(newFastaDb);
 
            // set panel and its size
            getCardLayout().show(fastaDbManagementDialog.getMainPanel(), FASTA_DB_SAVE_UPDATE_PANEL);
            fastaDbManagementDialog.getMainPanel().setPreferredSize(new Dimension(542,311));
            fastaDbManagementDialog.pack();
        });

        fastaDbManagementDialog.getUpdateButton().addActionListener(e -> {
            // send the selected fastaDb to fastaDbSaveUpdateController
            if(getSelectedFastaDb().getTaxonomy() == null){
                getSelectedFastaDb().setTaxonomy(TAXONOMY_CV_PARAM_NONE);
            }
            fastaDbSaveUpdateController.updateView(getSelectedFastaDb());
            // set panel and its size
            getCardLayout().show(fastaDbManagementDialog.getMainPanel(), FASTA_DB_SAVE_UPDATE_PANEL);
            fastaDbManagementDialog.getMainPanel().setPreferredSize(new Dimension(542,311));
            fastaDbManagementDialog.pack();
        });
        
        fastaDbManagementDialog.getDeleteButton().addActionListener(e -> {
            if (fastaDbManagementDialog.getFastaDbList().getSelectedIndex() != -1) {
                FastaDb fastaDbToDelete = getSelectedFastaDb();

                //check if FASTA DB has an id.
                //If so, try to delete the fasta DB from the db.
                if (fastaDbToDelete.getId() != null) {
                    try {
                        fastaDbService.remove(fastaDbToDelete);

                        fastaDbBindingList.remove(fastaDbManagementDialog.getFastaDbList().getSelectedIndex());
                        fastaDbManagementDialog.getFastaDbList().getSelectionModel().clearSelection();
                    } catch (DataIntegrityViolationException dive) {
                        //check if the instrument type can be deleted without breaking existing database relations,
                        //i.e. are there any constraints violations
                        if (dive.getCause() instanceof ConstraintViolationException) {
                            DbConstraintMessageEvent dbConstraintMessageEvent = new DbConstraintMessageEvent("fasta db", fastaDbToDelete.getName());
                            eventBus.post(dbConstraintMessageEvent);
                        } else {
                            //pass the exception
                            throw dive;
                        }
                    }
                } else {
                    fastaDbBindingList.remove(fastaDbManagementDialog.getFastaDbList().getSelectedIndex());
                    fastaDbManagementDialog.getFastaDbList().getSelectionModel().clearSelection();
                    clearFastaDbDetailFields();
                }
            } else {
                eventBus.post(new MessageEvent("Fasta DB selection", "Please select a fasta DB to delete.", JOptionPane.INFORMATION_MESSAGE));
            }
        });

        fastaDbManagementDialog.getOkButton().addActionListener(e -> {
            FastaDb fastaDb = getSelectedFastaDb();

            //validate before closing dialog
            List<String> validationMessages = new ArrayList<>();
            if (fastaDb == null) {
                validationMessages.add("Please select a fasta DB from the list.");
            } else {
                //validate user input
                validationMessages.addAll(validate(fastaDb));
            }

            if (validationMessages.isEmpty()) {
                fastaDbManagementDialog.dispose();
            } else {
                MessageEvent messageEvent = new MessageEvent("Validation failure", validationMessages, JOptionPane.WARNING_MESSAGE);
                eventBus.post(messageEvent);
            }
        });

        fastaDbManagementDialog.getCancelButton().addActionListener(e -> {
            //clear the selection
            fastaDbManagementDialog.getFastaDbList().getSelectionModel().clearSelection();
            fastaDbManagementDialog.dispose();
        });

        fastaDbManagementDialog.getPrimaryCheckBox().addActionListener(e -> updateFastaDbList());

        fastaDbManagementDialog.getAdditionalCheckBox().addActionListener(e -> updateFastaDbList());

        fastaDbManagementDialog.getContaminantsCheckBox().addActionListener(e -> updateFastaDbList());
    }

    @Override
    public void showView() {
        //refresh FASTA DB list
        fastaDbBindingList.clear();
        fastaDbBindingList.addAll(fastaDbService.findAll());

        fastaDbManagementDialog.getFastaDbStateInfoLabel().setText("");
        clearFastaDbDetailFields();

        GuiUtils.centerDialogOnComponent(analyticalRunsAdditionController.getAnalyticalRunsAdditionDialog(), fastaDbManagementDialog);
        fastaDbManagementDialog.setVisible(true);
    }

    /**
     * Return the selected FASTA DB.
     *
     * @return the selected FastaDb instance
     */
    public FastaDb getFastaDb() {
        return getSelectedFastaDb();
    }

    /**
     * Listen to a CV param change event posted by the
     * CvParamManagementController. If the InstrumentManagementDialog is
     * visible, clear the selection in the CV param summary list.
     *
     * @param cvParamChangeEvent the CvParamChangeEvent instance
     */
    @Subscribe
    public void onCvParamChangeEvent(CvParamChangeEvent cvParamChangeEvent) {
        CvParam cvParam = cvParamChangeEvent.getCvParam();
        if (cvParam instanceof TaxonomyCvParam) {
            EntityChangeEvent.Type type = cvParamChangeEvent.getType();
            switch (type) {
                case CREATED:
                    taxonomyBindingList.add(cvParam);
                    break;
                case UPDATED:
                    taxonomyBindingList.set(taxonomyBindingList.indexOf(cvParam), cvParam);
                    break;
                case DELETED:
                    taxonomyBindingList.remove(cvParam);
                    break;
            }
        }
    }

    /**
     * Validate the user input and return a list of validation messages.
     *
     * @param fastaDb the FastaDb instance
     * @return the list of validation messages
     */
    private List<String> validate(final FastaDb fastaDb) {
        List<String> validationMessages = new ArrayList();

        if (fastaDb.getId() == null) {
            validationMessages.add("You need to save the selected fasta DB before using it.");
        }

        return validationMessages;
    }

    /**
     * Get the selected FASTA DB in the FASTA DB JList.
     *
     * @return the selected FASTA DB
     */
    private FastaDb getSelectedFastaDb() {
        FastaDb selectedFastaDb = null;

        int selectedIndex = fastaDbManagementDialog.getFastaDbList().getSelectedIndex();
        if (selectedIndex != -1) {
            selectedFastaDb = fastaDbBindingList.get(selectedIndex);
            fastaDbManagementDialog.getUpdateButton().setEnabled(true);
        }

        return selectedFastaDb;
    }

    /**
     * Set the selected fastaDb in the fastaDb list.
     *
     * @param index the row index
     */
    public void setSelectedFasta(final int index) {
        fastaDbSelectionModel.clearSelection();
        fastaDbSelectionModel.setLeadSelectionIndex(index);
    }

    
    /**
     * Clear the FASTA DB detail fields.
     */
    private void clearFastaDbDetailFields() {
        fastaDbManagementDialog.getNameTextField().setText("");
        fastaDbManagementDialog.getFileNameTextField().setText("");
        fastaDbManagementDialog.getFilePathTextField().setText("");
        fastaDbManagementDialog.getVersionTextField().setText("");
        fastaDbManagementDialog.getTaxomomyComboBox().setSelectedIndex(0);
        fastaDbManagementDialog.getFastaDbStateInfoLabel().setText("");
    }

    /**
     * Update the FASTA DB list when the state of one of the type check boxes
     * has been changed.
     */
    private void updateFastaDbList() {
        List<FastaDbType> fastaDbTypes = new ArrayList<>();

        //clear the selection
        fastaDbManagementDialog.getFastaDbList().getSelectionModel().clearSelection();

        //check which checkboxes are selected
        if (fastaDbManagementDialog.getPrimaryCheckBox().isSelected()) {
            fastaDbTypes.add(FastaDbType.PRIMARY);
            fastaDbManagementDialog.getUpdateButton().setEnabled(false);
        }
        if (fastaDbManagementDialog.getAdditionalCheckBox().isSelected()) {
            fastaDbTypes.add(FastaDbType.ADDITIONAL);
            fastaDbManagementDialog.getUpdateButton().setEnabled(false);
        }
        if (fastaDbManagementDialog.getContaminantsCheckBox().isSelected()) {
            fastaDbTypes.add(FastaDbType.CONTAMINANTS);
            fastaDbManagementDialog.getUpdateButton().setEnabled(false);
        }

        if (fastaDbTypes.isEmpty()) {
            //find them all
            fastaDbBindingList.clear();
            fastaDbBindingList.addAll(fastaDbService.findAll());
        } else {
            fastaDbBindingList.clear();
            fastaDbBindingList.addAll(fastaDbService.findByFastaDbType(fastaDbTypes));
        }
    }
    
    /**
     * Get fastaDb Management Dialog
     * 
     * @return  fastaDbManagementDialog
     */
    public FastaDbManagementDialog getFastaDbManagementDialog() {
        return fastaDbManagementDialog;
    }

     /**
     * Get the card layout.
     *
     * @return the CardLayout
     */
    public CardLayout getCardLayout() {
        return (CardLayout) fastaDbManagementDialog.getMainPanel().getLayout();
    }

    /**
     * Get FastaDb Binding List
     * 
     * @return fastaDbBindingList
     */
    public ObservableList<FastaDb> getFastaDbBindingList() {
        return fastaDbBindingList;
    }
    
    /**
     * Add a new fasta db to the fastadb binding list
     * 
     * @param fastaDb 
     */
    public void addFastaDb(FastaDb fastaDb){
        getFastaDbBindingList().add(fastaDb);
    }
    
    /**
     * Get the row index of the selected fastaDB in the fastaDb management panel.
     *
     * @return the row index
     */
    public int getSelectedFastaDbIndex() {
        return fastaDbSelectionModel.getLeadSelectionIndex();
    }
}
