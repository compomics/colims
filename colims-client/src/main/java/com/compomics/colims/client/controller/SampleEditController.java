package com.compomics.colims.client.controller;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import com.compomics.colims.client.compoment.BinaryFileManagementPanel;
import com.compomics.colims.client.compoment.DualList;
import com.compomics.colims.client.distributed.QueueManager;
import com.compomics.colims.client.distributed.producer.DbTaskProducer;
import com.compomics.colims.client.event.*;
import com.compomics.colims.client.event.admin.MaterialChangeEvent;
import com.compomics.colims.client.event.admin.ProtocolChangeEvent;
import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.SampleBinaryFileDialog;
import com.compomics.colims.client.view.SampleEditDialog;
import com.compomics.colims.core.service.BinaryFileService;
import com.compomics.colims.core.service.MaterialService;
import com.compomics.colims.core.service.ProtocolService;
import com.compomics.colims.core.service.SampleService;
import com.compomics.colims.model.*;
import com.compomics.colims.model.comparator.MaterialNameComparator;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JComboBoxBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The sample edit view controller.
 *
 * @author Niels Hulstaert
 */
@Component("sampleEditController")
@Lazy
public class SampleEditController implements Controllable {

    //model
    private BindingGroup bindingGroup;
    private ObservableList<Protocol> protocolBindingList;
    private final EventList<AnalyticalRun> analyticalRuns = new BasicEventList<>();
    private Sample sampleToEdit;
    private List<Material> materials;
    //view
    private SampleEditDialog sampleEditDialog;
    private SampleBinaryFileDialog sampleBinaryFileDialog;
    //parent controller
    @Autowired
    private MainController mainController;
    @Autowired
    private ProjectManagementController projectManagementController;
    //services
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
    @PostConstruct
    public void init() {
        //register to event bus
        eventBus.register(this);

        //init view
        sampleEditDialog = new SampleEditDialog(mainController.getMainFrame(), true);
        sampleBinaryFileDialog = new SampleBinaryFileDialog(sampleEditDialog, true);
        sampleBinaryFileDialog.getBinaryFileManagementPanel().init(SampleBinaryFile.class);

        //init dual list
        sampleEditDialog.getMaterialDualList().init(new MaterialNameComparator());

        materials = materialService.findAll();

        //add binding
        bindingGroup = new BindingGroup();
        protocolBindingList = ObservableCollections.observableList(protocolService.findAll());

        JComboBoxBinding protocolComboBoxBinding = SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ_WRITE, protocolBindingList, sampleEditDialog.getProtocolComboBox());
        bindingGroup.addBinding(protocolComboBoxBinding);

        bindingGroup.bind();

        //add action listeners
        sampleEditDialog.getMaterialDualList().addPropertyChangeListener(DualList.CHANGED, evt -> {
            List<Material> addedMaterials = (List<Material>) evt.getNewValue();

            sampleToEdit.setMaterials(addedMaterials);
        });

        sampleEditDialog.getSaveOrUpdateButton().addActionListener(e -> {
            //update sampleToEdit with dialog input
            updateSampleToEdit();

            //validate sample
            List<String> validationMessages = GuiUtils.validateEntity(sampleToEdit);
            if (validationMessages.isEmpty()) {
                int index;
                EntityChangeEvent.Type type;

                if (sampleToEdit.getId() != null) {
                    sampleToEdit = sampleService.merge(sampleToEdit);

                    index = projectManagementController.getSelectedSampleIndex();
                    type = EntityChangeEvent.Type.UPDATED;
                } else {
                    //set experiment
                    sampleToEdit.setExperiment(projectManagementController.getSelectedExperiment());

                    sampleService.persist(sampleToEdit);

                    index = projectManagementController.getSamplesSize() - 1;
                    type = EntityChangeEvent.Type.CREATED;

                    //add sample to overview table
                    projectManagementController.addSample(sampleToEdit);

                    sampleEditDialog.getSaveOrUpdateButton().setText("update");
                }
                SampleChangeEvent sampleChangeEvent = new SampleChangeEvent(type, projectManagementController.getSelectedProject().getId(), sampleToEdit.getId());
                eventBus.post(sampleChangeEvent);

                MessageEvent messageEvent = new MessageEvent("Sample store confirmation", "Sample " + sampleToEdit.getName() + " was stored successfully!", JOptionPane.INFORMATION_MESSAGE);
                eventBus.post(messageEvent);

                //refresh selection in sample table
                projectManagementController.setSelectedSample(index);

                sampleEditDialog.dispose();
            } else {
                MessageEvent messageEvent = new MessageEvent("Validation failure", validationMessages, JOptionPane.WARNING_MESSAGE);
                eventBus.post(messageEvent);
            }
        });

        sampleBinaryFileDialog.getBinaryFileManagementPanel().addPropertyChangeListener(BinaryFileManagementPanel.ADD, evt -> {
            SampleBinaryFile binaryFileToAdd = (SampleBinaryFile) evt.getNewValue();

            //set experiment in binary file
            binaryFileToAdd.setSample(sampleToEdit);

            //save binary file
            binaryFileService.persist(binaryFileToAdd);

            sampleToEdit.getBinaryFiles().add(binaryFileToAdd);

            sampleEditDialog.getAttachementsTextField().setText(getAttachmentsAsString());
        });

        sampleBinaryFileDialog.getBinaryFileManagementPanel().addPropertyChangeListener(BinaryFileManagementPanel.REMOVE, evt -> {
            SampleBinaryFile binaryFileToRemove = (SampleBinaryFile) evt.getNewValue();

            sampleToEdit.getBinaryFiles().remove(binaryFileToRemove);

            //update the sample
            sampleToEdit = sampleService.merge(sampleToEdit);

            sampleEditDialog.getAttachementsTextField().setText(getAttachmentsAsString());
        });

        sampleBinaryFileDialog.getBinaryFileManagementPanel().addPropertyChangeListener(BinaryFileManagementPanel.FILE_TYPE_CHANGE, evt -> {
            SampleBinaryFile binaryFileToUpdate = (SampleBinaryFile) evt.getNewValue();

            //update binary file
            binaryFileService.merge(binaryFileToUpdate);

            sampleEditDialog.getAttachementsTextField().setText(getAttachmentsAsString());
        });

        sampleBinaryFileDialog.getCloseButton().addActionListener(e -> sampleBinaryFileDialog.dispose());

        sampleEditDialog.getAttachmentsEditButton().addActionListener(e -> {
            if (sampleToEdit.getId() != null) {
                sampleBinaryFileDialog.getBinaryFileManagementPanel().populateList(sampleToEdit.getBinaryFiles());

                GuiUtils.centerDialogOnComponent(sampleEditDialog, sampleBinaryFileDialog);
                sampleBinaryFileDialog.setVisible(true);
            } else {
                MessageEvent messageEvent = new MessageEvent("Sample attachments", "Please save the sample first before adding attachments.", JOptionPane.WARNING_MESSAGE);
                eventBus.post(messageEvent);
            }
        });

        sampleEditDialog.getCancelButton().addActionListener(e -> {
            if (sampleToEdit.getId() != null) {
                //roll back the changes
                Sample rolledBackSample = sampleService.findById(sampleToEdit.getId());

                //fetch sample binary files
                sampleService.fetchMaterialsAndBinaryFiles(rolledBackSample);

                sampleToEdit.setBinaryFiles(rolledBackSample.getBinaryFiles());
                sampleToEdit.setMaterials(rolledBackSample.getMaterials());
            }

            sampleEditDialog.dispose();
        });
    }

    @Override
    public void showView() {
        GuiUtils.centerDialogOnComponent(mainController.getMainFrame(), sampleEditDialog);
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
            //fetch sample binary files
            sampleService.fetchMaterialsAndBinaryFiles(sampleToEdit);
        } else {
            sampleEditDialog.getSaveOrUpdateButton().setText("save");
        }

        sampleEditDialog.getNameTextField().setText(sampleToEdit.getName());
        sampleEditDialog.getConditionTextField().setText(sampleToEdit.getCondition());
        //set the selected item in the owner combobox
        sampleEditDialog.getProtocolComboBox().getModel().setSelectedItem(sampleToEdit.getProtocol());
        sampleEditDialog.getStorageLocationTextField().setText(sampleToEdit.getStorageLocation());
        sampleEditDialog.getAttachementsTextField().setText(getAttachmentsAsString());

        //populate user dual list
        sampleEditDialog.getMaterialDualList().populateLists(materials, sampleToEdit.getMaterials());
        showView();
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
     * Listen to a SampleChangeEvent.
     *
     * @param sampleChangeEvent the SampleChangeEvent instance
     */
    @Subscribe
    public void onSampleChangeEvent(SampleChangeEvent sampleChangeEvent) {
        if (sampleEditDialog.isVisible() && sampleToEdit.getId().equals(sampleChangeEvent.getSampleId())) {
            if (sampleChangeEvent.getType().equals(EntityChangeEvent.Type.DELETED)) {
                JOptionPane.showMessageDialog(sampleEditDialog, "Another user removed this sample so the sample edit dialog will close.", "Experiment removed", JOptionPane.WARNING_MESSAGE);
                sampleEditDialog.dispose();
            } else if (sampleChangeEvent.getType().equals(EntityChangeEvent.Type.RUNS_ADDED)) {
                //add the new runs
                sampleToEdit.getAnalyticalRuns().stream()
                        .filter(analyticalRun -> !analyticalRuns.contains(analyticalRun))
                        .forEach(analyticalRuns::add);
            }
        }
    }

    /**
     * Listen to a ExperimentChangeEvent.
     *
     * @param experimentChangeEvent the ExperimentChangeEvent instance
     */
    @Subscribe
    public void onExperimentChangeEvent(ExperimentChangeEvent experimentChangeEvent) {
        if (sampleEditDialog.isVisible() && sampleToEdit.getExperiment().getId().equals(experimentChangeEvent.getExperimentId())) {
            if (experimentChangeEvent.getType().equals(EntityChangeEvent.Type.DELETED)) {
                JOptionPane.showMessageDialog(sampleEditDialog, "Another user removed the experiment associated with this sample so the sample edit dialog will close.", "Experiment removed", JOptionPane.WARNING_MESSAGE);
                sampleEditDialog.dispose();
            }
        }
    }

    /**
     * Listen to a ProjectChangeEvent.
     *
     * @param projectChangeEvent the ProjectChangeEvent instance
     */
    @Subscribe
    public void onProjectChangeEvent(ProjectChangeEvent projectChangeEvent) {
        if (sampleEditDialog.isVisible() && sampleToEdit.getExperiment().getProject().getId().equals(projectChangeEvent.getProjectId())) {
            if (projectChangeEvent.getType().equals(EntityChangeEvent.Type.DELETED)) {
                JOptionPane.showMessageDialog(sampleEditDialog, "Another user removed the project associated with this sample so the sample edit dialog will close.", "Project removed", JOptionPane.WARNING_MESSAGE);
                sampleEditDialog.dispose();
            }
        }
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
        return sampleToEdit.getBinaryFiles().stream().map(BinaryFile::toString).collect(Collectors.joining(", "));
    }
}
