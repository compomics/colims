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
import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.event.message.StorageQueuesConnectionErrorMessageEvent;
import com.compomics.colims.client.event.message.UnexpectedErrorMessageEvent;
import com.compomics.colims.client.model.table.format.AnalyticalRunManagementTableFormat;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.SampleRunsDialog;
import com.compomics.colims.core.distributed.model.DeleteDbTask;
import com.compomics.colims.core.service.AnalyticalRunService;
import com.compomics.colims.core.service.SearchAndValidationSettingsService;
import com.compomics.colims.model.*;
import com.compomics.colims.model.comparator.IdComparator;
import com.compomics.colims.model.enums.DefaultPermission;
import com.compomics.colims.model.enums.FastaDbType;
import com.google.common.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for viewing and editing sample runs.
 *
 * @author demet
 */
@Component("sampleRunsController")
@Lazy
public class SampleRunsController implements Controllable {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SampleRunsController.class);
    //view
    SampleRunsDialog sampleRunsDialog;
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
    //child controller
    @Autowired
    @Lazy
    private AnalyticalRunEditController analyticalRunEditController;
    @Autowired
    @Lazy
    private MzIdentMlExportController mzIdentMlExportController;
    //services
    @Autowired
    private QueueManager queueManager;
    @Autowired
    private EventBus eventBus;
    @Autowired
    private AnalyticalRunService analyticalRunService;
    @Autowired
    private SearchAndValidationSettingsService searchAndValidationSettingsService;

    @Override
    @PostConstruct
    public void init() {
        //register to event bus
        eventBus.register(this);

        sampleRunsDialog = new SampleRunsDialog(mainController.getMainFrame(), true);

        //init sample analytical runs table
        SortedList<AnalyticalRun> sortedAnalyticalRuns = new SortedList<>(analyticalRuns, new IdComparator());
        analyticalRunsTableModel = GlazedListsSwing.eventTableModel(sortedAnalyticalRuns, new AnalyticalRunManagementTableFormat());
        sampleRunsDialog.getAnalyticalRunsTable().setModel(analyticalRunsTableModel);
        analyticalRunsSelectionModel = new DefaultEventSelectionModel<>(sortedAnalyticalRuns);
        analyticalRunsSelectionModel.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        sampleRunsDialog.getAnalyticalRunsTable().setSelectionModel(analyticalRunsSelectionModel);

        //set column widths
        sampleRunsDialog.getAnalyticalRunsTable().getColumnModel().getColumn(AnalyticalRunManagementTableFormat.RUN_ID).setPreferredWidth(65);
        sampleRunsDialog.getAnalyticalRunsTable().getColumnModel().getColumn(AnalyticalRunManagementTableFormat.RUN_ID).setMaxWidth(65);
        sampleRunsDialog.getAnalyticalRunsTable().getColumnModel().getColumn(AnalyticalRunManagementTableFormat.RUN_ID).setMinWidth(65);
        sampleRunsDialog.getAnalyticalRunsTable().getColumnModel().getColumn(AnalyticalRunManagementTableFormat.NAME).setPreferredWidth(200);
        sampleRunsDialog.getAnalyticalRunsTable().getColumnModel().getColumn(AnalyticalRunManagementTableFormat.START_DATE).setPreferredWidth(100);
        sampleRunsDialog.getAnalyticalRunsTable().getColumnModel().getColumn(AnalyticalRunManagementTableFormat.START_DATE).setMaxWidth(100);
        sampleRunsDialog.getAnalyticalRunsTable().getColumnModel().getColumn(AnalyticalRunManagementTableFormat.START_DATE).setMinWidth(100);
        sampleRunsDialog.getAnalyticalRunsTable().getColumnModel().getColumn(AnalyticalRunManagementTableFormat.CREATED).setPreferredWidth(100);
        sampleRunsDialog.getAnalyticalRunsTable().getColumnModel().getColumn(AnalyticalRunManagementTableFormat.CREATED).setMaxWidth(100);
        sampleRunsDialog.getAnalyticalRunsTable().getColumnModel().getColumn(AnalyticalRunManagementTableFormat.CREATED).setMinWidth(100);
        sampleRunsDialog.getAnalyticalRunsTable().getColumnModel().getColumn(AnalyticalRunManagementTableFormat.NUMBER_OF_SPECTRA).setPreferredWidth(80);
        sampleRunsDialog.getAnalyticalRunsTable().getColumnModel().getColumn(AnalyticalRunManagementTableFormat.NUMBER_OF_SPECTRA).setMaxWidth(80);
        sampleRunsDialog.getAnalyticalRunsTable().getColumnModel().getColumn(AnalyticalRunManagementTableFormat.NUMBER_OF_SPECTRA).setMinWidth(80);

        sampleRunsDialog.getEditAnalyticalRunButton().addActionListener(e -> {
            EventList<AnalyticalRun> selectedAnalyticalRuns = analyticalRunsSelectionModel.getSelected();

            if (selectedAnalyticalRuns.size() == 1) {
                analyticalRunEditController.updateView(selectedAnalyticalRuns.get(0));
            } else {
                eventBus.post(new MessageEvent("Analytical run selection", "Please select one and only one analytical run to edit.", JOptionPane.INFORMATION_MESSAGE));
            }
        });

        sampleRunsDialog.getDeleteAnalyticalRunButton().addActionListener(e -> {
            EventList<AnalyticalRun> selectedAnalyticalRuns = analyticalRunsSelectionModel.getSelected();
            if (selectedAnalyticalRuns.size() == 1) {
                boolean deleteConfirmation = deleteEntity(selectedAnalyticalRuns.get(0), AnalyticalRun.class);
                if (deleteConfirmation) {
                    //close the dialog
                    sampleRunsDialog.dispose();
                }
            } else {
                eventBus.post(new MessageEvent("Analytical run selection", "Please select one and only one analytical run to delete.", JOptionPane.INFORMATION_MESSAGE));
            }
        });

        sampleRunsDialog.getExportMzIdentMlButton().addActionListener(e -> {
            EventList<AnalyticalRun> selectedAnalyticalRuns = analyticalRunsSelectionModel.getSelected();
            if (!selectedAnalyticalRuns.isEmpty()) {
                mzIdentMlExportController.showView();
            } else {
                eventBus.post(new MessageEvent("Analytical run selection", "Please select at least one analytical run to export.", JOptionPane.INFORMATION_MESSAGE));
            }
        });

        analyticalRunsSelectionModel.addListSelectionListener(lse -> {
            if (!lse.getValueIsAdjusting()) {
                AnalyticalRun analyticalRun = getSelectedAnalyticalRun();
                setAnalyticalRunDetailsSearchSettings(analyticalRun);
            }
        });

        sampleRunsDialog.getCloseButton().addActionListener(e -> sampleRunsDialog.dispose());
    }

    @Override
    public void showView() {
        GuiUtils.centerDialogOnComponent(mainController.getMainFrame(), sampleRunsDialog);
        sampleRunsDialog.setVisible(true);
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
            sampleRunsDialog.getEditAnalyticalRunButton().setEnabled(true);
            sampleRunsDialog.getDeleteAnalyticalRunButton().setEnabled(true);
        } else {
            sampleRunsDialog.getEditAnalyticalRunButton().setEnabled(false);
            sampleRunsDialog.getDeleteAnalyticalRunButton().setEnabled(false);
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
     * @param entity        the entity to delete
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
                        eventBus.post(new MessageEvent("Delete run confirmation", "The delete task has been sent to the distributed module.", JOptionPane.INFORMATION_MESSAGE));
                    } catch (IOException e) {
                        LOGGER.error(e.getMessage(), e);
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
    private AnalyticalRun getSelectedAnalyticalRun() {
        AnalyticalRun selectedAnalyticalRun = null;

        EventList<AnalyticalRun> selectedAnalyticalRuns = analyticalRunsSelectionModel.getSelected();
        if (!selectedAnalyticalRuns.isEmpty()) {
            selectedAnalyticalRun = selectedAnalyticalRuns.get(0);
        }

        return selectedAnalyticalRun;
    }

    /**
     * Get the selected analytical runs from the analytical runs table.
     *
     * @return the selected runs, an empty list if nothing is selected
     */
    public List<AnalyticalRun> getSelectedAnalyticalRuns() {
        EventList<AnalyticalRun> selectedAnalyticalRunEventList = analyticalRunsSelectionModel.getSelected();

        return selectedAnalyticalRunEventList.stream().collect(Collectors.toList());
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

            sampleRunsDialog.getNameTextField().setText(analyticalRun.getName());
            sampleRunsDialog.getStartDateTextField().setText(analyticalRun.getStartDate().toString());

            sampleRunsDialog.getInstrumentTextField().setText(analyticalRun.getInstrument().getName());

            if (analyticalRun.getStorageLocation() != null) {
                sampleRunsDialog.getLocationTextField().setText(analyticalRun.getStorageLocation());
            } else {
                sampleRunsDialog.getLocationTextField().setText("");
            }

            if (analyticalRun.getId() != null) {
                sampleRunsDialog.getAttachmentsTextField().setText(analyticalRun.getBinaryFiles().stream().map(BinaryFile::toString).collect(Collectors.joining(", ")));
            } else {
                sampleRunsDialog.getAttachmentsTextField().setText("");
            }

            sampleRunsDialog.getSearchEngineTextField().setText(analyticalRun.getSearchAndValidationSettings().getSearchEngine().toString());
            analyticalRun.getSearchAndValidationSettings().getSearchSettingsHasFastaDbs().forEach(fastaDb -> {
                if (fastaDb.getFastaDbType().equals(FastaDbType.PRIMARY)) {
                    sampleRunsDialog.getFastaNameTextField().setText(fastaDb.getFastaDb().getFileName());
                }
            });
            sampleRunsDialog.getEnzymeTextField().setText(analyticalRun.getSearchAndValidationSettings().getSearchParameters().getEnzymes());
            sampleRunsDialog.getMaxMissedCleTextField().setText(analyticalRun.getSearchAndValidationSettings().getSearchParameters().getNumberOfMissedCleavages());
            sampleRunsDialog.getPreMasTolTextField().setText(analyticalRun.getSearchAndValidationSettings().getSearchParameters().getPrecMassTolerance().toString());
        } else {
            sampleRunsDialog.getNameTextField().setText("");
            sampleRunsDialog.getStartDateTextField().setText("");
            sampleRunsDialog.getInstrumentTextField().setText("");
            sampleRunsDialog.getLocationTextField().setText("");
            sampleRunsDialog.getAttachmentsTextField().setText("");
            sampleRunsDialog.getSearchEngineTextField().setText("");
            sampleRunsDialog.getFastaNameTextField().setText("");
            sampleRunsDialog.getEnzymeTextField().setText("");
            sampleRunsDialog.getMaxMissedCleTextField().setText("");
            sampleRunsDialog.getPreMasTolTextField().setText("");
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
     * Get sampleRunsDialog
     *
     * @return sampleRunsDialog
     */
    public SampleRunsDialog getSampleRunsDialog() {
        return sampleRunsDialog;
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
