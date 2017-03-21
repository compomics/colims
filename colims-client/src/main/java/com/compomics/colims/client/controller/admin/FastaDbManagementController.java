package com.compomics.colims.client.controller.admin;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.DefaultEventListModel;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.GlazedListsSwing;
import com.compomics.colims.client.controller.AnalyticalRunsAdditionController;
import com.compomics.colims.client.controller.Controllable;
import com.compomics.colims.client.event.message.DbConstraintMessageEvent;
import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.fasta.FastaDbManagementDialog;
import com.compomics.colims.core.service.FastaDbService;
import com.compomics.colims.model.FastaDb;
import com.compomics.colims.model.TaxonomyCvParam;
import com.compomics.colims.model.enums.FastaDbType;
import com.google.common.eventbus.EventBus;
import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.jdesktop.beansbinding.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

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
     * The value used for the database name if it's not in the EMBL-EBI list.
     */
    public static final String DATABASE_NAME_NOT_PRESENT = "not in the EMBL-EBI list";
    /**
     * The value shown in the user interface if a {@link FastaDb} property is empty.
     */
    public static final String UNKNOWN = "unknown";
    /**
     * The default taxonomy value for the taxonomy combo box.
     */
    private static final TaxonomyCvParam TAXONOMY_CV_PARAM_NONE = new TaxonomyCvParam("none", "none", "none");
    /**
     * The overview panel name.
     */
    private static final String FASTA_DB_OVERVIEW_PANEL = "fastaDbManagementParentPanel";
    /**
     * The save or update panel name.
     */
    public static final String FASTA_DB_SAVE_UPDATE_PANEL = "fastaDbSaveUpdatePanel";
    /**
     * The overview panel preferred dimension.
     */
    private static final Dimension OVERVIEW_PANEL_DIMENSION = new Dimension(952, 353);
    /**
     * The overview panel preferred dimension.
     */
    private static final Dimension SAVE_OR_UPDATE_PANEL_DIMENSION = new Dimension(564, 311);

    //model
    private BindingGroup bindingGroup;
    private final EventList<FastaDb> fastaDbs = new BasicEventList<>();
    private SortedList<FastaDb> sortedFastaDbs;
    private DefaultEventListModel<FastaDb> fastaDbListModel;
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
    private FastaDbService fastaDbService;
    @Autowired
    @Lazy
    private FastaDbSaveUpdateController fastaDbSaveUpdateController;

    @Override
    @PostConstruct
    public void init() {
        //init view
        fastaDbManagementDialog = new FastaDbManagementDialog(analyticalRunsAdditionController.getAnalyticalRunsAdditionDialog(), true);

        //init binding
        bindingGroup = new BindingGroup();

        fastaDbs.addAll(fastaDbService.findAll());
        sortedFastaDbs = new SortedList<>(fastaDbs, (FastaDb o1, FastaDb o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
        fastaDbListModel = GlazedListsSwing.eventListModel(sortedFastaDbs);
        fastaDbManagementDialog.getFastaDbList().setModel(fastaDbListModel);
        fastaDbSelectionModel = new DefaultEventSelectionModel<>(sortedFastaDbs);
        fastaDbSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fastaDbManagementDialog.getFastaDbList().setSelectionModel(fastaDbSelectionModel);

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
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, fastaDbManagementDialog.getFastaDbList(), BeanProperty.create("selectedElement.databaseName"), fastaDbManagementDialog.getDatabaseTextField(), ELProperty.create("${text}"), "databaseNameBinding");
        bindingGroup.addBinding(binding);

        bindingGroup.bind();

        fastaDbManagementDialog.getFastaDbList().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                if (fastaDbManagementDialog.getFastaDbList().getSelectedIndex() != -1) {
                    FastaDb fastaDb = getSelectedFastaDb();
                    //enable the edit button if necessary
                    fastaDbManagementDialog.getEditButton().setEnabled(fastaDb.getId() != null);

                    //set "none" taxonomy if necessary
                    TaxonomyCvParam taxonomy = getSelectedFastaDb().getTaxonomy();
                    if (taxonomy == null) {
                        fastaDb.setTaxonomy(TAXONOMY_CV_PARAM_NONE);
                    }
                    fastaDbManagementDialog.getTaxonomyTextField().setText(fastaDb.getTaxonomy().toString());
                    if (fastaDb.getDatabaseName() == null) {
                        fastaDb.setDatabaseName(DATABASE_NAME_NOT_PRESENT);
                    }
                    fastaDbManagementDialog.getDatabaseTextField().setText(fastaDb.getDatabaseName());
                    if (fastaDb.getHeaderParseRule() == null) {
                        fastaDb.setHeaderParseRule(UNKNOWN);
                    }
                    fastaDbManagementDialog.getHeaderParseRuleTextField().setText(fastaDb.getHeaderParseRule());
                    if (fastaDb.getVersion() == null) {
                        fastaDb.setVersion(UNKNOWN);
                    }
                    fastaDbManagementDialog.getVersionTextField().setText(fastaDb.getVersion());

                    //enable delete button
                    fastaDbManagementDialog.getDeleteButton().setEnabled(true);
                } else {
                    fastaDbManagementDialog.getEditButton().setEnabled(false);
                    clearFastaDbDetailFields();
                }
            }
        });

        fastaDbManagementDialog.getAddButton().addActionListener(e -> {
            fastaDbSaveUpdateController.clearFastaDbDetailFields();
            //create a new fasta db instance with some default values
            FastaDb newFastaDb = new FastaDb();
            newFastaDb.setName("name");
            newFastaDb.setTaxonomy(TAXONOMY_CV_PARAM_NONE);
            newFastaDb.setDatabaseName(DATABASE_NAME_NOT_PRESENT);
            //send the new fastaDb to fastaDbSaveUpdateController
            fastaDbSaveUpdateController.updateView(newFastaDb);

            // set panel and its size
            showSaveOrUpdatePanel();
        });

        fastaDbManagementDialog.getEditButton().addActionListener(e -> {
            // send the selected fastaDb to fastaDbSaveUpdateController
            fastaDbSaveUpdateController.updateView(getSelectedFastaDb());

            // set panel and its size
            showSaveOrUpdatePanel();
        });

        fastaDbManagementDialog.getDeleteButton().addActionListener(e -> {
            if (fastaDbManagementDialog.getFastaDbList().getSelectedIndex() != -1) {
                FastaDb fastaDbToDelete = getSelectedFastaDb();

                //check if FASTA DB has an id.
                //If so, try to delete the fasta DB from the db.
                if (fastaDbToDelete.getId() != null) {
                    try {
                        fastaDbService.remove(fastaDbToDelete);

                        fastaDbs.remove(fastaDbToDelete);
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
                    fastaDbs.remove(fastaDbManagementDialog.getFastaDbList().getSelectedIndex());
                    fastaDbManagementDialog.getFastaDbList().getSelectionModel().clearSelection();
                    clearFastaDbDetailFields();
                }
            } else {
                eventBus.post(new MessageEvent("Fasta DB selection", "Please select a fasta DB to delete.", JOptionPane.INFORMATION_MESSAGE));
            }
        });

        fastaDbManagementDialog.getSelectButton().addActionListener(e -> {
            FastaDb fastaDb = getSelectedFastaDb();

            //validate before closing dialog
            List<String> validationMessages = new ArrayList<>();
            if (fastaDb == null) {
                validationMessages.add("Please select a fasta DB from the list.");
            }

            if (validationMessages.isEmpty()) {
                fastaDbManagementDialog.dispose();
            } else {
                MessageEvent messageEvent = new MessageEvent("Validation failure", validationMessages, JOptionPane.WARNING_MESSAGE);
                eventBus.post(messageEvent);
            }
        });

        fastaDbManagementDialog.getCloseButton().addActionListener(e -> {
            setSelectedFasta(-1);
            fastaDbManagementDialog.dispose();
        });

        fastaDbManagementDialog.getPrimaryCheckBox().addActionListener(e -> updateFastaDbList());

        fastaDbManagementDialog.getAdditionalCheckBox().addActionListener(e -> updateFastaDbList());

        fastaDbManagementDialog.getContaminantsCheckBox().addActionListener(e -> updateFastaDbList());
    }

    @Override
    public void showView() {
        //refresh FASTA DB list
        fastaDbs.clear();
        fastaDbs.addAll(fastaDbService.findAll());

        //clear the selection
        fastaDbSelectionModel.clearSelection();

        fastaDbManagementDialog.getEditButton().setSelected(false);
        clearFastaDbDetailFields();

        showOverviewPanel();

        fastaDbManagementDialog.setVisible(true);
    }

    /**
     * Get the selected FASTA DB in the FASTA DB JList.
     *
     * @return the selected FASTA DB
     */
    public FastaDb getSelectedFastaDb() {
        FastaDb selectedFastaDb = null;

        int selectedIndex = fastaDbSelectionModel.getLeadSelectionIndex();
        if (selectedIndex != -1) {
            selectedFastaDb = sortedFastaDbs.get(selectedIndex);
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
     * Get the fasta DB list size.
     *
     * @return the size of the fasta DB list
     */
    public int getFastaDbListSize() {
        return fastaDbs.size();
    }

    /**
     * Add a new fasta db to the fastadb binding list.
     *
     * @param fastaDb
     * @return the index of the added FastaDb instance
     */
    public int addFastaDb(FastaDb fastaDb) {
        fastaDbs.add(fastaDb);
        fastaDbManagementDialog.getFastaDbList().updateUI();
        return sortedFastaDbs.indexOf(fastaDb);
    }

    /**
     * update fasta db binding list and clear selection.
     */
    public void updateFastaDb() {
        fastaDbManagementDialog.getFastaDbList().updateUI();
        fastaDbSelectionModel.clearSelection();
    }

    /**
     * Get the row index of the selected fastaDB in the fastaDb management
     * panel.
     *
     * @return the row index
     */
    public int getSelectedFastaDbIndex() {
        return fastaDbSelectionModel.getLeadSelectionIndex();
    }

    /**
     * Get fastaDb Management Dialog.
     *
     * @return fastaDbManagementDialog
     */
    public FastaDbManagementDialog getFastaDbManagementDialog() {
        return fastaDbManagementDialog;
    }

    /**
     * Show the fasta DB management overview panel.
     */
    public void showOverviewPanel() {
        getCardLayout().show(fastaDbManagementDialog.getMainPanel(), FASTA_DB_OVERVIEW_PANEL);
        fastaDbManagementDialog.getMainPanel().setPreferredSize(OVERVIEW_PANEL_DIMENSION);
        fastaDbManagementDialog.pack();

        GuiUtils.centerDialogOnComponent(analyticalRunsAdditionController.getAnalyticalRunsAdditionDialog(), fastaDbManagementDialog);
    }

    /**
     * Show the fasta DB save or update panel.
     */
    private void showSaveOrUpdatePanel() {
        getCardLayout().show(fastaDbManagementDialog.getMainPanel(), FASTA_DB_SAVE_UPDATE_PANEL);
        fastaDbManagementDialog.getMainPanel().setPreferredSize(SAVE_OR_UPDATE_PANEL_DIMENSION);
        fastaDbManagementDialog.pack();

        GuiUtils.centerDialogOnComponent(analyticalRunsAdditionController.getAnalyticalRunsAdditionDialog(), fastaDbManagementDialog);
    }

    /**
     * Clear the FASTA DB detail fields.
     */
    private void clearFastaDbDetailFields() {
        fastaDbManagementDialog.getNameTextField().setText("");
        fastaDbManagementDialog.getFileNameTextField().setText("");
        fastaDbManagementDialog.getFilePathTextField().setText("");
        fastaDbManagementDialog.getVersionTextField().setText("");
        fastaDbManagementDialog.getTaxonomyTextField().setText("");
        fastaDbManagementDialog.getHeaderParseRuleTextField().setText("");
        fastaDbManagementDialog.getDatabaseTextField().setText("");
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
            fastaDbManagementDialog.getEditButton().setEnabled(false);
        }
        if (fastaDbManagementDialog.getAdditionalCheckBox().isSelected()) {
            fastaDbTypes.add(FastaDbType.ADDITIONAL);
            fastaDbManagementDialog.getEditButton().setEnabled(false);
        }
        if (fastaDbManagementDialog.getContaminantsCheckBox().isSelected()) {
            fastaDbTypes.add(FastaDbType.CONTAMINANTS);
            fastaDbManagementDialog.getEditButton().setEnabled(false);
        }

        if (fastaDbTypes.isEmpty()) {
            //find them all
            fastaDbs.clear();
            fastaDbs.addAll(fastaDbService.findAll());
        } else {
            fastaDbs.clear();
            fastaDbs.addAll(fastaDbService.findByFastaDbType(fastaDbTypes));
        }
    }

    /**
     * Get the card layout.
     *
     * @return the CardLayout
     */
    private CardLayout getCardLayout() {
        return (CardLayout) fastaDbManagementDialog.getMainPanel().getLayout();
    }

}
