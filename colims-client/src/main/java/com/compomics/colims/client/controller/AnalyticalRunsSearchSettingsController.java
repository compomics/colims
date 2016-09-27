/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.client.controller;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.AdvancedTableModel;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.GlazedListsSwing;
import com.compomics.colims.client.distributed.QueueManager;
import com.compomics.colims.client.distributed.producer.DbTaskProducer;
import com.compomics.colims.client.event.AnalyticalRunChangeEvent;
import com.compomics.colims.client.event.EntityChangeEvent;
import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.event.message.StorageQueuesConnectionErrorMessageEvent;
import com.compomics.colims.client.event.message.UnexpectedErrorMessageEvent;
import com.compomics.colims.client.model.table.format.AnalyticalRunManagementTableFormat;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.AnalyticalRunsSearchSettingsDialog;
import com.compomics.colims.core.distributed.model.DeleteDbTask;
import com.compomics.colims.core.service.AnalyticalRunService;
import com.compomics.colims.core.service.BinaryFileService;
import com.compomics.colims.core.service.SearchAndValidationSettingsService;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.DatabaseEntity;
import com.compomics.colims.model.Sample;
import com.compomics.colims.model.UserBean;
import com.compomics.colims.model.comparator.IdComparator;
import com.compomics.colims.model.enums.DefaultPermission;
import com.compomics.colims.model.enums.FastaDbType;
import com.google.common.eventbus.EventBus;
import java.io.IOException;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Analytical Runs and Search Settings controller
 *
 * @author demet
 */
@Component("analyticalRunsSearchSettingsController")
@Lazy
public class AnalyticalRunsSearchSettingsController implements Controllable {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(AnalyticalRunsSearchSettingsController.class);
    //view
    AnalyticalRunsSearchSettingsDialog analyticalRunsSearchSettingsDialog;
    //model
    private final EventList<AnalyticalRun> analyticalRuns = new BasicEventList<>();
    private AdvancedTableModel<AnalyticalRun> analyticalRunsTableModel;
    private DefaultEventSelectionModel<AnalyticalRun> analyticalRunsSelectionModel;
    private Sample sampleToEdit;

    @Autowired
    private UserBean userBean;
    @Autowired
    private DbTaskProducer dbTaskProducer;
    //parent controller
    @Autowired
    private MainController mainController;
    @Autowired
    private ProjectManagementController projectManagementController;

    //child controller
    @Autowired
    @Lazy
    private AnalyticalRunEditController analyticalRunEditController;

    //services
    @Autowired
    private QueueManager queueManager;
    @Autowired
    private EventBus eventBus;
    @Autowired
    private AnalyticalRunService analyticalRunService;
    @Autowired
    private BinaryFileService binaryFileService;
    @Autowired
    private SearchAndValidationSettingsService searchAndValidationSettingsService;

    @Override
    @PostConstruct
    public void init() {
        //register to event bus
        eventBus.register(this);

        analyticalRunsSearchSettingsDialog = new AnalyticalRunsSearchSettingsDialog(mainController.getMainFrame(), true);

        //init sample analytical runs table
        SortedList<AnalyticalRun> sortedAnalyticalRuns = new SortedList<>(analyticalRuns, new IdComparator());
        analyticalRunsTableModel = GlazedListsSwing.eventTableModel(sortedAnalyticalRuns, new AnalyticalRunManagementTableFormat());
        analyticalRunsSearchSettingsDialog.getAnalyticalRunsTable().setModel(analyticalRunsTableModel);
        analyticalRunsSelectionModel = new DefaultEventSelectionModel<>(sortedAnalyticalRuns);
        analyticalRunsSelectionModel.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        analyticalRunsSearchSettingsDialog.getAnalyticalRunsTable().setSelectionModel(analyticalRunsSelectionModel);

        //set column widths
        analyticalRunsSearchSettingsDialog.getAnalyticalRunsTable().getColumnModel().getColumn(AnalyticalRunManagementTableFormat.RUN_ID).setPreferredWidth(35);
        analyticalRunsSearchSettingsDialog.getAnalyticalRunsTable().getColumnModel().getColumn(AnalyticalRunManagementTableFormat.RUN_ID).setMaxWidth(35);
        analyticalRunsSearchSettingsDialog.getAnalyticalRunsTable().getColumnModel().getColumn(AnalyticalRunManagementTableFormat.RUN_ID).setMinWidth(35);
        analyticalRunsSearchSettingsDialog.getAnalyticalRunsTable().getColumnModel().getColumn(AnalyticalRunManagementTableFormat.NAME).setPreferredWidth(200);
        analyticalRunsSearchSettingsDialog.getAnalyticalRunsTable().getColumnModel().getColumn(AnalyticalRunManagementTableFormat.START_DATE).setPreferredWidth(80);
        analyticalRunsSearchSettingsDialog.getAnalyticalRunsTable().getColumnModel().getColumn(AnalyticalRunManagementTableFormat.START_DATE).setMaxWidth(80);
        analyticalRunsSearchSettingsDialog.getAnalyticalRunsTable().getColumnModel().getColumn(AnalyticalRunManagementTableFormat.START_DATE).setMinWidth(80);
        analyticalRunsSearchSettingsDialog.getAnalyticalRunsTable().getColumnModel().getColumn(AnalyticalRunManagementTableFormat.CREATED).setPreferredWidth(80);
        analyticalRunsSearchSettingsDialog.getAnalyticalRunsTable().getColumnModel().getColumn(AnalyticalRunManagementTableFormat.CREATED).setMaxWidth(80);
        analyticalRunsSearchSettingsDialog.getAnalyticalRunsTable().getColumnModel().getColumn(AnalyticalRunManagementTableFormat.CREATED).setMinWidth(80);
        analyticalRunsSearchSettingsDialog.getAnalyticalRunsTable().getColumnModel().getColumn(AnalyticalRunManagementTableFormat.NUMBER_OF_SPECTRA).setPreferredWidth(80);
        analyticalRunsSearchSettingsDialog.getAnalyticalRunsTable().getColumnModel().getColumn(AnalyticalRunManagementTableFormat.NUMBER_OF_SPECTRA).setMaxWidth(80);
        analyticalRunsSearchSettingsDialog.getAnalyticalRunsTable().getColumnModel().getColumn(AnalyticalRunManagementTableFormat.NUMBER_OF_SPECTRA).setMinWidth(80);

        analyticalRunsSearchSettingsDialog.getEditAnalyticalRunButton().addActionListener(e -> {
            EventList<AnalyticalRun> selectedAnalyticalRuns = analyticalRunsSelectionModel.getSelected();

            if (selectedAnalyticalRuns.size() == 1) {
                analyticalRunEditController.updateView(selectedAnalyticalRuns.get(0));
            } else {
                eventBus.post(new MessageEvent("Analytical run selection", "Please select one and only one analytical run to edit.", JOptionPane.INFORMATION_MESSAGE));
            }
        });
        analyticalRunsSearchSettingsDialog.getDeleteAnalyticalRunButton().addActionListener(e -> {
            EventList<AnalyticalRun> selectedAnalyticalRuns = analyticalRunsSelectionModel.getSelected();

            if (selectedAnalyticalRuns.size() == 1) {
                boolean deleteConfirmation = deleteEntity(selectedAnalyticalRuns.get(0), AnalyticalRun.class);
                if (deleteConfirmation) {
                    AnalyticalRun selectedAnalyticalRun = selectedAnalyticalRuns.get(0);

                    //remove from overview table and clear selection
                    analyticalRuns.remove(selectedAnalyticalRun);
                    analyticalRunsSelectionModel.clearSelection();

                    eventBus.post(new AnalyticalRunChangeEvent(EntityChangeEvent.Type.DELETED, selectedAnalyticalRun.getId(), sampleToEdit.getId()));

                    //remove analytical run from the selected sample and update the table
                    sampleToEdit.getAnalyticalRuns().remove(selectedAnalyticalRun);
                    analyticalRunsSearchSettingsDialog.getAnalyticalRunsTable().updateUI();
                }
            } else {
                eventBus.post(new MessageEvent("Analytical run selection", "Please select one and only one analytical run to delete.", JOptionPane.INFORMATION_MESSAGE));
            }
        });

        analyticalRunsSelectionModel.addListSelectionListener(lse -> {
            if (!lse.getValueIsAdjusting()) {
                AnalyticalRun analyticalRun = getSelectedAnalyticalRun();
                setAnalyticalRunDetailsSearchSettings(analyticalRun);
            }
        });

        analyticalRunsSearchSettingsDialog.getCloseButton().addActionListener(e -> analyticalRunsSearchSettingsDialog.dispose());
    }

    @Override
    public void showView() {
        GuiUtils.centerDialogOnComponent(mainController.getMainFrame(), analyticalRunsSearchSettingsDialog);
        analyticalRunsSearchSettingsDialog.setVisible(true);
    }

    /**
     * Update the Analytical runs and Search settings dialog with the selected
     * sample in the sample overview table.
     *
     * @param sample the Sample
     */
    public void updateView(final Sample sample) {
        sampleToEdit = sample;

        if (sampleToEdit.getId() != null) {
            analyticalRunsSearchSettingsDialog.getEditAnalyticalRunButton().setEnabled(true);
            analyticalRunsSearchSettingsDialog.getDeleteAnalyticalRunButton().setEnabled(true);
        } else {
            analyticalRunsSearchSettingsDialog.getEditAnalyticalRunButton().setEnabled(false);
            analyticalRunsSearchSettingsDialog.getDeleteAnalyticalRunButton().setEnabled(false);
        }
        //fill analytical runs table
        GlazedLists.replaceAll(analyticalRuns, sampleToEdit.getAnalyticalRuns(), false);

        showView();
    }

    /**
     * Delete the database entity (project, experiment, analytical runs) from
     * the database. Shows a confirmation dialog first. When confirmed, a
     * DeleteDbTask message is sent to the DB task queue. A message dialog is
     * shown in case the queue cannot be reached or in case of an IOException
     * thrown by the sendDbTask method.
     *
     * @param entity the entity to delete
     * @param dbEntityClass the database entity class
     * @return true if the delete task is confirmed.
     */
    private boolean deleteEntity(final DatabaseEntity entity, final Class dbEntityClass) {
        boolean deleteConfirmation = false;

        //check delete permissions
        if (userBean.getDefaultPermissions().get(DefaultPermission.DELETE)) {
            int option = JOptionPane.showConfirmDialog(mainController.getMainFrame(), "Are you sure? This will remove all underlying database relations (spectra, psm's, ...) as well."
                    + System.lineSeparator() + "A delete task will be sent to the database task queue.", "Delete " + dbEntityClass.getSimpleName() + " confirmation.", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                //check connection
                if (queueManager.isReachable()) {
                    DeleteDbTask deleteDbTask = new DeleteDbTask(dbEntityClass, entity.getId(), userBean.getCurrentUser().getId());
                    try {
                        dbTaskProducer.sendDbTask(deleteDbTask);
                        deleteConfirmation = true;
                    } catch (IOException e) {
                        LOGGER.error(e, e.getCause());
                        eventBus.post(new UnexpectedErrorMessageEvent(e.getMessage()));
                    }
                } else {
                    eventBus.post(new StorageQueuesConnectionErrorMessageEvent(queueManager.getBrokerName(), queueManager.getBrokerUrl(), queueManager.getBrokerJmxUrl()));
                }
            }
        } else {
            mainController.showPermissionErrorDialog("Your user doesn't have rights to delete this " + entity.getClass().getSimpleName());
        }

        return deleteConfirmation;
    }

    /**
     * Get the selected analytical run from the analytical runs table.
     *
     * @return the selected run, null if no run is selected
     */
    public AnalyticalRun getSelectedAnalyticalRun() {
        AnalyticalRun selectedAnalyticalRun = null;

        EventList<AnalyticalRun> selectedAnalyticalRuns = analyticalRunsSelectionModel.getSelected();
        if (!selectedAnalyticalRuns.isEmpty()) {
            selectedAnalyticalRun = selectedAnalyticalRuns.get(0);
        }

        return selectedAnalyticalRun;
    }

    /**
     * Set the analytical run details and search settings by given analytical
     * run Set empty if run is null
     *
     * @param analyticalRun
     */
    public void setAnalyticalRunDetailsSearchSettings(AnalyticalRun analyticalRun) {
        if (analyticalRun != null) {
            // fetch all needed fields
            fetchAnalyticalRun(analyticalRun);
            analyticalRunsSearchSettingsDialog.getNameTextField().setText(analyticalRun.getName());
            analyticalRunsSearchSettingsDialog.getStartDateTextField().setText(analyticalRun.getStartDate().toString());

            analyticalRunsSearchSettingsDialog.getInstrumentTextField().setText(analyticalRun.getInstrument().getName());

            if (analyticalRun.getStorageLocation() != null) {
                analyticalRunsSearchSettingsDialog.getLocationTextField().setText(analyticalRun.getStorageLocation());
            } else {
                analyticalRunsSearchSettingsDialog.getLocationTextField().setText("");
            }

            if (analyticalRun.getId() != null) {
                analyticalRunsSearchSettingsDialog.getAttachmentsTextField().setText(analyticalRun.getBinaryFiles().stream().map(binaryFile -> binaryFile.toString()).collect(Collectors.joining(", ")));
            } else {
                analyticalRunsSearchSettingsDialog.getAttachmentsTextField().setText("");
            }

            analyticalRunsSearchSettingsDialog.getSearchEngineTextField().setText(analyticalRun.getSearchAndValidationSettings().getSearchEngine().toString());
            analyticalRun.getSearchAndValidationSettings().getSearchSettingsHasFastaDbs().forEach(fastaDb -> {
                if (fastaDb.getFastaDbType().equals(FastaDbType.PRIMARY)) {
                    analyticalRunsSearchSettingsDialog.getFastaNameTextField().setText(fastaDb.getFastaDb().getFileName());
                }
            });
            analyticalRunsSearchSettingsDialog.getEnzymeTextField().setText(analyticalRun.getSearchAndValidationSettings().getSearchParameters().getEnzymes());
            analyticalRunsSearchSettingsDialog.getMaxMissedCleTextField().setText(analyticalRun.getSearchAndValidationSettings().getSearchParameters().getNumberOfMissedCleavages().toString());
            analyticalRunsSearchSettingsDialog.getPreMasTolTextField().setText(analyticalRun.getSearchAndValidationSettings().getSearchParameters().getPrecMassTolerance().toString());
        } else {
            analyticalRunsSearchSettingsDialog.getNameTextField().setText("");
            analyticalRunsSearchSettingsDialog.getStartDateTextField().setText("");
            analyticalRunsSearchSettingsDialog.getInstrumentTextField().setText("");
            analyticalRunsSearchSettingsDialog.getLocationTextField().setText("");
            analyticalRunsSearchSettingsDialog.getAttachmentsTextField().setText("");
            analyticalRunsSearchSettingsDialog.getSearchEngineTextField().setText("");
            analyticalRunsSearchSettingsDialog.getFastaNameTextField().setText("");
            analyticalRunsSearchSettingsDialog.getEnzymeTextField().setText("");
            analyticalRunsSearchSettingsDialog.getMaxMissedCleTextField().setText("");
            analyticalRunsSearchSettingsDialog.getPreMasTolTextField().setText("");
        }
    }

    /**
     * Fetch all needed fields.
     *
     * @param analyticalRun
     */
    public void fetchAnalyticalRun(AnalyticalRun analyticalRun) {
        //fetch the instrument if necessary
        analyticalRunService.fetchInstrument(analyticalRun);
        // fetch binary files if analytical run Id is not null
        if (analyticalRun.getId() != null) {
            analyticalRunService.fetchBinaryFiles(analyticalRun);
        }
        // fetch search settings has fasta db
        searchAndValidationSettingsService.fetchSearchSettingsHasFastaDb(analyticalRun.getSearchAndValidationSettings());
    }

    /**
     * Get analyticalRunsSearchSettingsDialog
     *
     * @return analyticalRunsSearchSettingsDialog
     */
    public AnalyticalRunsSearchSettingsDialog getAnalyticalRunsSearchSettingsDialog() {
        return analyticalRunsSearchSettingsDialog;
    }

    /**
     * Get the row index of the selected analytical run in the analytical runs
     * table.
     *
     * @return the selected analytical run index
     */
    public int getSelectedAnalyticalRunIndex() {
        return analyticalRunsSelectionModel.getLeadSelectionIndex();
    }

    /**
     * Set the selected analytical run in the analytical runs table.
     *
     * @param index the selected analytical run index
     */
    public void setSelectedAnalyticalRun(final int index) {
        analyticalRunsSelectionModel.clearSelection();
        analyticalRunsSelectionModel.setLeadSelectionIndex(index);
    }
}
