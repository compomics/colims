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
import com.compomics.colims.client.view.admin.FastaDbManagementDialog;
import com.compomics.colims.core.service.FastaDbService;
import com.compomics.colims.model.FastaDb;
import com.compomics.colims.model.TaxonomyCvParam;
import com.compomics.colims.model.comparator.IdComparator;
import com.compomics.colims.model.enums.FastaDbType;
import com.google.common.eventbus.EventBus;
import java.awt.CardLayout;
import java.awt.Dimension;
import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.jdesktop.beansbinding.*;
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
    private static final Dimension SAVE_OR_UPDATE_PANEL_DIMENSION = new Dimension(542, 311);

    //model
    private BindingGroup bindingGroup;
    private final EventList<FastaDb> fastaDbs = new BasicEventList<>();
    DefaultEventListModel<FastaDb> fastaDbListModel;
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

        //init fastaDbSaveUpdate
        fastaDbSaveUpdateController.init();
        //init binding
        bindingGroup = new BindingGroup();

        fastaDbs.addAll(fastaDbService.findAll());
        SortedList<FastaDb> sortedFastaDbList = new SortedList<>(fastaDbs, new IdComparator());
        fastaDbListModel = GlazedListsSwing.eventListModel(sortedFastaDbList);
        fastaDbManagementDialog.getFastaDbList().setModel(fastaDbListModel);
        fastaDbSelectionModel = new DefaultEventSelectionModel<>(sortedFastaDbList);
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

        bindingGroup.bind();

        fastaDbManagementDialog.getFastaDbList().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                if (fastaDbManagementDialog.getFastaDbList().getSelectedIndex() != -1) {
                    FastaDb fastaDb = getSelectedFastaDb();
                    //enable update button if necessary
                    fastaDbManagementDialog.getUpdateButton().setEnabled(fastaDb.getId() != null);

                    //set "none" taxonomy if necessary
                    TaxonomyCvParam taxonomy = getSelectedFastaDb().getTaxonomy();
                    if (taxonomy == null) {
                        fastaDb.setTaxonomy(TAXONOMY_CV_PARAM_NONE);
                    }
                    fastaDbManagementDialog.getTaxonomyTextField().setText(fastaDb.getTaxonomy().toString());

                    //enable delete button
                    fastaDbManagementDialog.getDeleteButton().setEnabled(true);
                } else {
                    fastaDbManagementDialog.getUpdateButton().setEnabled(false);
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
            showSaveOrUpdatePanel();
        });

        fastaDbManagementDialog.getUpdateButton().addActionListener(e -> {
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

                        fastaDbs.remove(fastaDbManagementDialog.getFastaDbList().getSelectedIndex());
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

        fastaDbManagementDialog.getUpdateButton().setSelected(false);
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

        int selectedIndex = fastaDbManagementDialog.getFastaDbList().getSelectedIndex();
        if (selectedIndex != -1) {
            selectedFastaDb = fastaDbs.get(selectedIndex);
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
     * Add a new fasta db to the fastadb binding list
     *
     * @param fastaDb
     */
    public void addFastaDb(FastaDb fastaDb) {
        fastaDbs.add(fastaDb);

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
