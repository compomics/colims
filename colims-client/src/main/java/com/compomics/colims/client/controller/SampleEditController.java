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
import com.compomics.colims.client.event.EntityChangeEvent;
import com.compomics.colims.client.event.MaterialChangeEvent;
import com.compomics.colims.client.event.ProtocolChangeEvent;
import com.compomics.colims.client.event.SampleChangeEvent;
import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.model.tableformat.AnalyticalRunManagementTableFormat;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.SampleBinaryFileDialog;
import com.compomics.colims.client.view.SampleEditDialog;
import com.compomics.colims.core.service.AbstractBinaryFileService;
import com.compomics.colims.core.service.MaterialService;
import com.compomics.colims.core.service.ProtocolService;
import com.compomics.colims.core.service.SampleService;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.Material;
import com.compomics.colims.model.Protocol;
import com.compomics.colims.model.Sample;
import com.compomics.colims.model.SampleBinaryFile;
import com.compomics.colims.model.comparator.IdComparator;
import com.compomics.colims.model.comparator.MaterialNameComparator;
import com.google.common.base.Joiner;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
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
 *
 * @author Niels Hulstaert
 */
@Component("sampleEditController")
public class SampleEditController implements Controllable {

    private static final Logger LOGGER = Logger.getLogger(SampleEditController.class);
    //model 
    private BindingGroup bindingGroup;
    private ObservableList<Protocol> protocolBindingList;
    private EventList<AnalyticalRun> analyticalRuns = new BasicEventList<>();
    private AdvancedTableModel<AnalyticalRun> analyticalRunsTableModel;
    private DefaultEventSelectionModel<AnalyticalRun> analyticalRunsSelectionModel;
    private Sample sampleToEdit;
    private List<Material> materials;
    //view
    private SampleEditDialog sampleEditDialog;
    private SampleBinaryFileDialog sampleBinaryFileDialog;
    //parent controller
    @Autowired
    private ExperimentEditController experimentEditController;
    //child controller
    @Autowired
    private AnalyticalRunEditController analyticalRunEditController;
    //services
    @Autowired
    private SampleService sampleService;
    @Autowired
    private MaterialService materialService;
    @Autowired
    private ProtocolService protocolService;
    @Autowired
    private AbstractBinaryFileService abstractBinaryFileService;
    @Autowired
    private EventBus eventBus;

    public SampleEditDialog getSampleEditDialog() {
        return sampleEditDialog;
    }

    @Override
    public void init() {
        //register to event bus
        eventBus.register(this);

        //init view
        sampleEditDialog = new SampleEditDialog(experimentEditController.getExperimentEditDialog(), true);
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
        sampleEditDialog.getAnalyticalRunsTable().getColumnModel().getColumn(AnalyticalRunManagementTableFormat.RUN_ID).setPreferredWidth(5);
        sampleEditDialog.getAnalyticalRunsTable().getColumnModel().getColumn(AnalyticalRunManagementTableFormat.NAME).setPreferredWidth(200);
        sampleEditDialog.getAnalyticalRunsTable().getColumnModel().getColumn(AnalyticalRunManagementTableFormat.START_DATE).setPreferredWidth(50);
        sampleEditDialog.getAnalyticalRunsTable().getColumnModel().getColumn(AnalyticalRunManagementTableFormat.CREATED).setPreferredWidth(50);
        sampleEditDialog.getAnalyticalRunsTable().getColumnModel().getColumn(AnalyticalRunManagementTableFormat.NUMBER_OF_SPECTRA).setPreferredWidth(50);

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
            public void actionPerformed(ActionEvent e) {
                //update projectToEdit with dialog input
                updateSampleToEdit();

                //validate project
                List<String> validationMessages = GuiUtils.validateEntity(sampleToEdit);

                if (validationMessages.isEmpty()) {
                    int index;
                    EntityChangeEvent.Type type;

                    if (sampleToEdit.getId() != null) {
                        sampleService.update(sampleToEdit);

                        index = experimentEditController.getSelectedSampleIndex();
                        type = EntityChangeEvent.Type.UPDATED;
                    } else {
                        //set experiment
                        sampleToEdit.setExperiment(experimentEditController.getExperimentToEdit());

                        sampleService.save(sampleToEdit);

                        index = experimentEditController.getSamplesSize() - 1;
                        type = EntityChangeEvent.Type.CREATED;

                        //add sample to overview table
                        experimentEditController.addSample(sampleToEdit);

                        sampleEditDialog.getSaveOrUpdateButton().setText("update");
                        updateAnalyticalRunButtonsState(true);
                    }
                    SampleChangeEvent sampleChangeEvent = new SampleChangeEvent(type, sampleToEdit);
                    eventBus.post(sampleChangeEvent);
                    
                    MessageEvent messageEvent = new MessageEvent("sample persist confirmation", "Sample " + sampleToEdit.getName() + " was persisted successfully!", JOptionPane.INFORMATION_MESSAGE);
                    eventBus.post(messageEvent);

                    //refresh selection in sample table
                    experimentEditController.setSelectedSample(index);
                } else {
                    MessageEvent messageEvent = new MessageEvent("validation failure", validationMessages, JOptionPane.WARNING_MESSAGE);
                    eventBus.post(messageEvent);
                }
            }
        });

        sampleBinaryFileDialog.getBinaryFileManagementPanel().addPropertyChangeListener(BinaryFileManagementPanel.ADD, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                SampleBinaryFile binaryFileToAdd = (SampleBinaryFile) evt.getNewValue();

                //set experiment in binary file
                binaryFileToAdd.setSample(sampleToEdit);

                //save binary file
                abstractBinaryFileService.save(binaryFileToAdd);

                sampleToEdit.getBinaryFiles().add(binaryFileToAdd);
                sampleEditDialog.getAttachementsTextField().setText(getAttachmentsAsString());
            }
        });

        sampleBinaryFileDialog.getBinaryFileManagementPanel().addPropertyChangeListener(BinaryFileManagementPanel.REMOVE, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                SampleBinaryFile binaryFileToRemove = (SampleBinaryFile) evt.getNewValue();

                if (sampleToEdit.getBinaryFiles().contains(binaryFileToRemove)) {
                    sampleToEdit.getBinaryFiles().remove(binaryFileToRemove);
                }

                //remove binary file
                abstractBinaryFileService.delete(binaryFileToRemove);

                sampleEditDialog.getAttachementsTextField().setText(getAttachmentsAsString());
            }
        });

        sampleBinaryFileDialog.getBinaryFileManagementPanel().addPropertyChangeListener(BinaryFileManagementPanel.FILE_TYPE_CHANGE, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                SampleBinaryFile binaryFileToUpdate = (SampleBinaryFile) evt.getNewValue();

                //update binary file
                abstractBinaryFileService.update(binaryFileToUpdate);

                sampleEditDialog.getAttachementsTextField().setText(getAttachmentsAsString());
            }
        });

        sampleBinaryFileDialog.getCloseButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sampleBinaryFileDialog.dispose();
            }
        });

        sampleEditDialog.getAttachmentsEditButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sampleBinaryFileDialog.getBinaryFileManagementPanel().populateList(sampleToEdit.getBinaryFiles());

                GuiUtils.centerDialogOnComponent(sampleEditDialog, sampleBinaryFileDialog);
                sampleBinaryFileDialog.setVisible(true);
            }
        });

        sampleEditDialog.getCloseButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sampleEditDialog.dispose();
            }
        });

        sampleEditDialog.getEditAnalyticalRunButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AnalyticalRun selectedAnalyticalRun = getSelectedAnalyticalRun();
                if (selectedAnalyticalRun != null) {
                    analyticalRunEditController.updateView(selectedAnalyticalRun);
                } else {
                    eventBus.post(new MessageEvent("analytical run selection", "Please select an analytical run to edit.", JOptionPane.INFORMATION_MESSAGE));
                }
            }
        });
    }

    @Override
    public void showView() {
        GuiUtils.centerDialogOnComponent(experimentEditController.getExperimentEditDialog(), sampleEditDialog);
        sampleEditDialog.setVisible(true);
    }

    /**
     * Update the sample edit dialog with the selected sample in the sample
     * overview table.
     *
     * @param sample
     */
    public void updateView(Sample sample) {
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
     * table
     *
     * @return
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
     * Set the selected analytical run in the analytical runs table
     *
     * @param index
     */
    public void setSelectedAnalyticalRun(int index) {
        analyticalRunsSelectionModel.clearSelection();
        analyticalRunsSelectionModel.setLeadSelectionIndex(index);
    }
    
    /**
     * Listen to a ProtocolChangeEvent.
     *
     * @param protocolChangeEvent the protocol change event
     */
    @Subscribe
    public void onProtocolChangeEvent(ProtocolChangeEvent protocolChangeEvent) {
        protocolBindingList.clear();
        protocolBindingList.addAll(protocolService.findAll());
    }

    /**
     * Listen to MaterialChangeEvent.
     *
     * @param materialChangeEvent the protocol change event
     */
    @Subscribe
    public void onMaterialChangeEvent(MaterialChangeEvent materialChangeEvent) {
        materials = materialService.findAll();
    }
    
    /**
     * Update the instance fields of the selected sample in the samples table
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
     * Get the attachments file names as a concatenated string.
     *
     * @return
     */
    private String getAttachmentsAsString() {
        String concatenatedString = "";

        Joiner joiner = Joiner.on(", ");
        concatenatedString = joiner.join(sampleToEdit.getBinaryFiles());

        return concatenatedString;
    }

    /**
     * Update the state (enables/disabled) of the analytical run related buttons
     *
     * @param enable the enable the buttons boolean
     */
    private void updateAnalyticalRunButtonsState(boolean enable) {
        sampleEditDialog.getEditAnalyticalRunButton().setEnabled(enable);
        sampleEditDialog.getDeleteAnalyticalRunButton().setEnabled(enable);
    }
}
