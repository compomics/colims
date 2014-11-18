package com.compomics.colims.client.controller;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.AdvancedTableModel;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.GlazedListsSwing;
import com.compomics.colims.client.compoment.BinaryFileManagementPanel;
import com.compomics.colims.client.compoment.DualList;
import com.compomics.colims.client.event.AnalyticalRunChangeEvent;
import com.compomics.colims.client.event.EntityChangeEvent;
import com.compomics.colims.client.event.admin.MaterialChangeEvent;
import com.compomics.colims.client.event.admin.ProtocolChangeEvent;
import com.compomics.colims.client.event.SampleChangeEvent;
import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.event.message.StorageQueuesConnectionErrorMessageEvent;
import com.compomics.colims.client.model.tableformat.AnalyticalRunManagementTableFormat;
import com.compomics.colims.client.distributed.producer.DbTaskProducer;
import com.compomics.colims.client.distributed.QueueManager;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.SampleBinaryFileDialog;
import com.compomics.colims.client.view.SampleEditDialog;
import com.compomics.colims.core.io.mztab.MzTabExporter;
import com.compomics.colims.core.service.BinaryFileService;
import com.compomics.colims.core.service.MaterialService;
import com.compomics.colims.core.service.ProtocolService;
import com.compomics.colims.core.service.SampleService;
import com.compomics.colims.distributed.model.DeleteDbTask;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.DatabaseEntity;
import com.compomics.colims.model.Material;
import com.compomics.colims.model.Protocol;
import com.compomics.colims.model.Sample;
import com.compomics.colims.model.SampleBinaryFile;
import com.compomics.colims.model.comparator.IdComparator;
import com.compomics.colims.model.comparator.MaterialNameComparator;
import com.compomics.colims.model.enums.DefaultPermission;
import com.compomics.colims.repository.AuthenticationBean;
import com.compomics.util.gui.waiting.waitinghandlers.ProgressDialogX;
import com.google.common.base.Joiner;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import org.apache.log4j.Logger;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JComboBoxBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The sample edit view controller.
 *
 * @author Niels Hulstaert
 */
@Component("sampleEditController")
public class SampleEditController implements Controllable {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(SampleEditController.class);

    //model
    private BindingGroup bindingGroup;
    private ObservableList<Protocol> protocolBindingList;
    private final EventList<AnalyticalRun> analyticalRuns = new BasicEventList<>();
    private AdvancedTableModel<AnalyticalRun> analyticalRunsTableModel;
    private DefaultEventSelectionModel<AnalyticalRun> analyticalRunsSelectionModel;
    private Sample sampleToEdit;
    private List<Material> materials;
    @Autowired
    private AuthenticationBean authenticationBean;
    //view
    private SampleEditDialog sampleEditDialog;
    private SampleBinaryFileDialog sampleBinaryFileDialog;
    private ProgressDialogX analyticalRunProgressDialog;
    //parent controller
    @Autowired
    private ColimsController colimsController;
    @Autowired
    private ProjectManagementController projectManagementController;
    //child controller
    @Autowired
    private AnalyticalRunEditController analyticalRunEditController;
    //services
    @Autowired
    private MzTabExporter mzTabExporter;
    @Autowired
    private SampleService sampleService;
    @Autowired
    private MaterialService materialService;
    @Autowired
    private ProtocolService protocolService;
    @Autowired
    private BinaryFileService binaryFileService;
    @Autowired
    private DbTaskProducer dbTaskProducer;
    @Autowired
    private QueueManager queueManager;
    @Autowired
    private EventBus eventBus;

    /**
     * Get the view of this controller.
     *
     * @return the SampleEditDialog
     */
    public SampleEditDialog getSampleEditDialog() {
        return sampleEditDialog;
    }

    @Override
    public void init() {
        //register to event bus
        eventBus.register(this);

        //init view
        sampleEditDialog = new SampleEditDialog(colimsController.getColimsFrame(), true);
        sampleBinaryFileDialog = new SampleBinaryFileDialog(sampleEditDialog, true);
        sampleBinaryFileDialog.getBinaryFileManagementPanel().init(SampleBinaryFile.class);

        //init child controller
        analyticalRunEditController.init();

        //init dual list
        sampleEditDialog.getMaterialDualList().init(new MaterialNameComparator());

        materials = materialService.findAll();

        //init sample analyticalruns table
        SortedList<AnalyticalRun> sortedAnalyticalRuns = new SortedList<>(analyticalRuns, new IdComparator());
        analyticalRunsTableModel = GlazedListsSwing.eventTableModel(sortedAnalyticalRuns, new AnalyticalRunManagementTableFormat());
        sampleEditDialog.getAnalyticalRunsTable().setModel(analyticalRunsTableModel);
        analyticalRunsSelectionModel = new DefaultEventSelectionModel<>(sortedAnalyticalRuns);
        analyticalRunsSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sampleEditDialog.getAnalyticalRunsTable().setSelectionModel(analyticalRunsSelectionModel);

        //set column widths
        sampleEditDialog.getAnalyticalRunsTable().getColumnModel().getColumn(AnalyticalRunManagementTableFormat.RUN_ID).setPreferredWidth(35);
        sampleEditDialog.getAnalyticalRunsTable().getColumnModel().getColumn(AnalyticalRunManagementTableFormat.RUN_ID).setMaxWidth(35);
        sampleEditDialog.getAnalyticalRunsTable().getColumnModel().getColumn(AnalyticalRunManagementTableFormat.RUN_ID).setMinWidth(35);
        sampleEditDialog.getAnalyticalRunsTable().getColumnModel().getColumn(AnalyticalRunManagementTableFormat.NAME).setPreferredWidth(200);
        sampleEditDialog.getAnalyticalRunsTable().getColumnModel().getColumn(AnalyticalRunManagementTableFormat.START_DATE).setPreferredWidth(80);
        sampleEditDialog.getAnalyticalRunsTable().getColumnModel().getColumn(AnalyticalRunManagementTableFormat.START_DATE).setMaxWidth(80);
        sampleEditDialog.getAnalyticalRunsTable().getColumnModel().getColumn(AnalyticalRunManagementTableFormat.START_DATE).setMinWidth(80);
        sampleEditDialog.getAnalyticalRunsTable().getColumnModel().getColumn(AnalyticalRunManagementTableFormat.CREATED).setPreferredWidth(80);
        sampleEditDialog.getAnalyticalRunsTable().getColumnModel().getColumn(AnalyticalRunManagementTableFormat.CREATED).setMaxWidth(80);
        sampleEditDialog.getAnalyticalRunsTable().getColumnModel().getColumn(AnalyticalRunManagementTableFormat.CREATED).setMinWidth(80);
        sampleEditDialog.getAnalyticalRunsTable().getColumnModel().getColumn(AnalyticalRunManagementTableFormat.NUMBER_OF_SPECTRA).setPreferredWidth(80);
        sampleEditDialog.getAnalyticalRunsTable().getColumnModel().getColumn(AnalyticalRunManagementTableFormat.NUMBER_OF_SPECTRA).setMaxWidth(80);
        sampleEditDialog.getAnalyticalRunsTable().getColumnModel().getColumn(AnalyticalRunManagementTableFormat.NUMBER_OF_SPECTRA).setMinWidth(80);

        //add binding
        bindingGroup = new BindingGroup();
        protocolBindingList = ObservableCollections.observableList(protocolService.findAll());

        JComboBoxBinding protocolComboBoxBinding = SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ_WRITE, protocolBindingList, sampleEditDialog.getProtocolComboBox());
        bindingGroup.addBinding(protocolComboBoxBinding);

        bindingGroup.bind();

        //add action listeners
        sampleEditDialog.getMaterialDualList().addPropertyChangeListener(DualList.CHANGED, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                List<Material> addedMaterials = (List<Material>) evt.getNewValue();

                sampleToEdit.setMaterials(addedMaterials);
            }
        });

        sampleEditDialog.getSaveOrUpdateButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                //update sampleToEdit with dialog input
                updateSampleToEdit();

                //validate sample
                List<String> validationMessages = GuiUtils.validateEntity(sampleToEdit);
                if (validationMessages.isEmpty()) {
                    int index;
                    EntityChangeEvent.Type type;

                    if (sampleToEdit.getId() != null) {
                        sampleService.update(sampleToEdit);

                        index = projectManagementController.getSelectedSampleIndex();
                        type = EntityChangeEvent.Type.UPDATED;
                    } else {
                        //set experiment
                        sampleToEdit.setExperiment(projectManagementController.getSelectedExperiment());

                        sampleService.save(sampleToEdit);

                        index = projectManagementController.getSamplesSize() - 1;
                        type = EntityChangeEvent.Type.CREATED;

                        //add sample to overview table
                        projectManagementController.addSample(sampleToEdit);

                        sampleEditDialog.getSaveOrUpdateButton().setText("update");
                        updateAnalyticalRunButtonsState(true);
                    }
                    SampleChangeEvent sampleChangeEvent = new SampleChangeEvent(type, sampleToEdit);
                    eventBus.post(sampleChangeEvent);

                    MessageEvent messageEvent = new MessageEvent("Sample store confirmation", "Sample " + sampleToEdit.getName() + " was stored successfully!", JOptionPane.INFORMATION_MESSAGE);
                    eventBus.post(messageEvent);

                    //refresh selection in sample table
                    projectManagementController.setSelectedSample(index);
                } else {
                    MessageEvent messageEvent = new MessageEvent("Validation failure", validationMessages, JOptionPane.WARNING_MESSAGE);
                    eventBus.post(messageEvent);
                }
            }
        });

        sampleBinaryFileDialog.getBinaryFileManagementPanel().addPropertyChangeListener(BinaryFileManagementPanel.ADD, new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent evt) {
                SampleBinaryFile binaryFileToAdd = (SampleBinaryFile) evt.getNewValue();

                //set experiment in binary file
                binaryFileToAdd.setSample(sampleToEdit);

                //save binary file
                binaryFileService.save(binaryFileToAdd);

                sampleToEdit.getBinaryFiles().add(binaryFileToAdd);
                sampleEditDialog.getAttachementsTextField().setText(getAttachmentsAsString());
            }
        });

        sampleBinaryFileDialog.getBinaryFileManagementPanel().addPropertyChangeListener(BinaryFileManagementPanel.REMOVE, new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent evt) {
                SampleBinaryFile binaryFileToRemove = (SampleBinaryFile) evt.getNewValue();

                if (sampleToEdit.getBinaryFiles().contains(binaryFileToRemove)) {
                    sampleToEdit.getBinaryFiles().remove(binaryFileToRemove);
                }

                //remove binary file
                binaryFileService.delete(binaryFileToRemove);

                sampleEditDialog.getAttachementsTextField().setText(getAttachmentsAsString());
            }
        });

        sampleBinaryFileDialog.getBinaryFileManagementPanel().addPropertyChangeListener(BinaryFileManagementPanel.FILE_TYPE_CHANGE, new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent evt) {
                SampleBinaryFile binaryFileToUpdate = (SampleBinaryFile) evt.getNewValue();

                //update binary file
                binaryFileService.update(binaryFileToUpdate);

                sampleEditDialog.getAttachementsTextField().setText(getAttachmentsAsString());
            }
        });

        sampleBinaryFileDialog.getCancelButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                sampleBinaryFileDialog.dispose();
            }
        });

        sampleEditDialog.getAttachmentsEditButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (sampleToEdit.getId() != null) {
                    sampleBinaryFileDialog.getBinaryFileManagementPanel().populateList(sampleToEdit.getBinaryFiles());

                    GuiUtils.centerDialogOnComponent(sampleEditDialog, sampleBinaryFileDialog);
                    sampleBinaryFileDialog.setVisible(true);
                } else {
                    MessageEvent messageEvent = new MessageEvent("Sample attachments", "Please save the sample first before adding attachments.", JOptionPane.WARNING_MESSAGE);
                    eventBus.post(messageEvent);
                }
            }
        });

        sampleEditDialog.getCancelButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                sampleEditDialog.dispose();
            }
        });

        sampleEditDialog.getEditAnalyticalRunButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                AnalyticalRun selectedAnalyticalRun = getSelectedAnalyticalRun();
                if (selectedAnalyticalRun != null) {
                    analyticalRunEditController.updateView(selectedAnalyticalRun);
                } else {
                    eventBus.post(new MessageEvent("Analytical run selection", "Please select an analytical run to edit.", JOptionPane.INFORMATION_MESSAGE));
                }
            }
        });

        sampleEditDialog.getDeleteAnalyticalRunButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                AnalyticalRun analyticalRunToDelete = getSelectedAnalyticalRun();

                if (analyticalRunToDelete != null) {
                    boolean deleteConfirmation = deleteEntity(analyticalRunToDelete, AnalyticalRun.class);
                    if (deleteConfirmation) {
                        //remove from overview table and clear selection
                        analyticalRuns.remove(analyticalRunToDelete);
                        analyticalRunsSelectionModel.clearSelection();
                        eventBus.post(new AnalyticalRunChangeEvent(EntityChangeEvent.Type.DELETED, analyticalRunToDelete));

                        //remove analytical run from the selected sample and update the table
                        sampleToEdit.getAnalyticalRuns().remove(analyticalRunToDelete);
                        sampleEditDialog.getAnalyticalRunsTable().updateUI();
                    }
                } else {
                    eventBus.post(new MessageEvent("Analytical run selection", "Please select an analytical run to delete.", JOptionPane.INFORMATION_MESSAGE));
                }
            }
        });

        sampleEditDialog.getExportAnalyticalRunButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                AnalyticalRun selectedAnalyticalRun = getSelectedAnalyticalRun();
                if (selectedAnalyticalRun != null) {
                    int returnVal = sampleEditDialog.getExportDirectoryChooser().showOpenDialog(sampleEditDialog);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File exportDirectory = sampleEditDialog.getExportDirectoryChooser().getSelectedFile();
                        if (exportDirectory.isDirectory()) {
                            AnalyticalRunExportWorker analyticalRunExportWorker = new AnalyticalRunExportWorker(exportDirectory);
                            analyticalRunExportWorker.execute();
                        }
                    }
                } else {
                    eventBus.post(new MessageEvent("Analytical run selection", "Please select an analytical run to export.", JOptionPane.INFORMATION_MESSAGE));
                }
            }
        });
    }

    @Override
    public void showView() {
        GuiUtils.centerDialogOnComponent(colimsController.getColimsFrame(), sampleEditDialog);
        sampleEditDialog.setVisible(true);
    }

    /**
     * Update the sample edit dialog with the selected sample in the sample
     * overview table.
     *
     * @param sample the Sample
     */
    public void updateView(final Sample sample) {
        sampleToEdit = sample;

        if (sampleToEdit.getId() != null) {
            sampleEditDialog.getSaveOrUpdateButton().setText("update");
            updateAnalyticalRunButtonsState(true);
            //fetch sample binary files
            sampleService.fetchBinaryFiles(sampleToEdit);
            //fetch sample materials
            sampleService.fetchMaterials(sampleToEdit);
        } else {
            sampleEditDialog.getSaveOrUpdateButton().setText("save");
            updateAnalyticalRunButtonsState(false);
        }

        sampleEditDialog.getNameTextField().setText(sampleToEdit.getName());
        sampleEditDialog.getConditionTextField().setText(sampleToEdit.getCondition());
        //set the selected item in the owner combobox
        sampleEditDialog.getProtocolComboBox().setSelectedItem(sampleToEdit.getProtocol());
        sampleEditDialog.getStorageLocationTextField().setText(sampleToEdit.getStorageLocation());
        sampleEditDialog.getAttachementsTextField().setText(getAttachmentsAsString());

        //populate user dual list
        sampleEditDialog.getMaterialDualList().populateLists(materials, sampleToEdit.getMaterials());

        //fill analytical runs table
        GlazedLists.replaceAll(analyticalRuns, sampleToEdit.getAnalyticalRuns(), false);

        showView();
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
     * Get the selected analytical run from the analytical run overview table.
     *
     * @return the selected analytical run, null if no analytical run is
     * selected
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
     * Set the selected analytical run in the analytical runs table.
     *
     * @param index the selected analytical run index
     */
    public void setSelectedAnalyticalRun(final int index) {
        analyticalRunsSelectionModel.clearSelection();
        analyticalRunsSelectionModel.setLeadSelectionIndex(index);
    }

    /**
     * Listen to a ProtocolChangeEvent.
     *
     * @param protocolChangeEvent the protocol change event
     */
    @Subscribe
    public void onProtocolChangeEvent(final ProtocolChangeEvent protocolChangeEvent) {
        protocolBindingList.clear();
        protocolBindingList.addAll(protocolService.findAll());
    }

    /**
     * Listen to MaterialChangeEvent.
     *
     * @param materialChangeEvent the material change event
     */
    @Subscribe
    public void onMaterialChangeEvent(final MaterialChangeEvent materialChangeEvent) {
        materials = materialService.findAll();
    }

    /**
     * Delete the database entity (project, experiment, analytical runs) from
     * the database. Shows a confirmation dialog first. When confirmed, a
     * DeleteDbTask message is sent to the DB task queue.
     *
     * @param entity the entity to delete
     * @param dbEntityClass the database entity class
     * @return true if the delete task is confirmed.
     */
    private boolean deleteEntity(final DatabaseEntity entity, final Class dbEntityClass) {
        boolean deleteConfirmation = false;

        //check delete permissions
        if (authenticationBean.getDefaultPermissions().get(DefaultPermission.DELETE)) {
            int option = JOptionPane.showConfirmDialog(colimsController.getColimsFrame(), "Are you sure? This will remove all underlying database relations (spectra, psm's, ...) as well."
                    + System.lineSeparator() + "A delete task will be sent to the database task queue.", "Delete " + dbEntityClass.getSimpleName() + " confirmation.", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                //check connection
                if (queueManager.testConnection()) {
                    DeleteDbTask deleteDbTask = new DeleteDbTask(dbEntityClass, entity.getId(), authenticationBean.getCurrentUser().getId());
                    dbTaskProducer.sendDbTask(deleteDbTask);

                    deleteConfirmation = true;
                } else {
                    eventBus.post(new StorageQueuesConnectionErrorMessageEvent(queueManager.getBrokerName(), queueManager.getBrokerUrl(), queueManager.getBrokerJmxUrl()));
                }
            }
        } else {
            colimsController.showPermissionErrorDialog("Your user doesn't have rights to delete this " + entity.getClass().getSimpleName());
        }

        return deleteConfirmation;
    }

    /**
     * Update the instance fields of the selected sample in the samples table.
     */
    private void updateSampleToEdit() {
        sampleToEdit.setName(sampleEditDialog.getNameTextField().getText());
        sampleToEdit.setCondition(sampleEditDialog.getConditionTextField().getText());
        if (sampleEditDialog.getProtocolComboBox().getSelectedIndex() != -1) {
            sampleToEdit.setProtocol(protocolBindingList.get(sampleEditDialog.getProtocolComboBox().getSelectedIndex()));
        }
        sampleToEdit.setStorageLocation(sampleEditDialog.getStorageLocationTextField().getText());
    }

    /**
     * Get the attachments file names as a concatenated String.
     *
     * @return the concatenated String
     */
    private String getAttachmentsAsString() {
        String concatenatedString = "";

        Joiner joiner = Joiner.on(", ");
        concatenatedString = joiner.join(sampleToEdit.getBinaryFiles());

        return concatenatedString;
    }

    /**
     * Update the state (enables/disabled) of the analytical run related buttons.
     *
     * @param enable the enable the buttons boolean
     */
    private void updateAnalyticalRunButtonsState(final boolean enable) {
        sampleEditDialog.getEditAnalyticalRunButton().setEnabled(enable);
        sampleEditDialog.getDeleteAnalyticalRunButton().setEnabled(enable);
    }

    /**
     * Swingworker that exports the given AnalyticalRun in mzTab format.
     */
    private class AnalyticalRunExportWorker extends SwingWorker<Void, Void>{

        private final File exportDirectory;

        public AnalyticalRunExportWorker(File exportDirectory) {
            this.exportDirectory = exportDirectory;
        }

        @Override
        protected Void doInBackground() throws Exception {
            mzTabExporter.exportAnalyticalRun(exportDirectory, getSelectedAnalyticalRun());

            return null;
        }

        @Override
        protected void done() {
            super.done();
        }

    }
}
