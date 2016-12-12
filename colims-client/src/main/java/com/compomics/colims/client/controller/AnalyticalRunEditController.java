package com.compomics.colims.client.controller;

import com.compomics.colims.client.compoment.BinaryFileManagementPanel;
import com.compomics.colims.client.event.*;
import com.compomics.colims.client.event.admin.InstrumentChangeEvent;
import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.AnalyticalRunBinaryFileDialog;
import com.compomics.colims.client.view.AnalyticalRunEditDialog;
import com.compomics.colims.core.service.AnalyticalRunService;
import com.compomics.colims.core.service.BinaryFileService;
import com.compomics.colims.core.service.InstrumentService;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.AnalyticalRunBinaryFile;
import com.compomics.colims.model.BinaryFile;
import com.compomics.colims.model.Instrument;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.apache.log4j.Logger;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.PersistenceException;

import org.hibernate.exception.GenericJDBCException;

/**
 * @author Niels Hulstaert
 */
@Component("analyticalRunEditController")
@Lazy
public class AnalyticalRunEditController implements Controllable {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(AnalyticalRunEditController.class);

    //model
    private BindingGroup bindingGroup;
    private ObservableList<Instrument> instrumentBindingList;
    private AnalyticalRun analyticalRunToEdit;
    //view
    private AnalyticalRunEditDialog analyticalRunEditDialog;
    private AnalyticalRunBinaryFileDialog analyticalRunBinaryFileDialog;
    //parent controller
    @Autowired
    private AnalyticalRunsSearchSettingsController analyticalRunsSearchSettingsController;
    //services
    @Autowired
    private AnalyticalRunService analyticalRunService;
    @Autowired
    private InstrumentService instrumentService;
    @Autowired
    private BinaryFileService binaryFileService;
    @Autowired
    private EventBus eventBus;

    /**
     * Get the view of this controller.
     *
     * @return the AnalyticalRunEditDialog
     */
    public AnalyticalRunEditDialog getAnalyticalRunEditDialog() {
        return analyticalRunEditDialog;
    }

    @Override
    @PostConstruct
    public void init() {
        //register to event bus
        eventBus.register(this);

        //init view
        analyticalRunEditDialog = new AnalyticalRunEditDialog(analyticalRunsSearchSettingsController.getAnalyticalRunsSearchSettingsDialog(), true);
        analyticalRunBinaryFileDialog = new AnalyticalRunBinaryFileDialog(analyticalRunEditDialog, true);
        analyticalRunBinaryFileDialog.getBinaryFileManagementPanel().init(AnalyticalRunBinaryFile.class);

        //set DateTimePicker format
        analyticalRunEditDialog.getDateTimePicker().setFormats(new SimpleDateFormat("dd-MM-yyyy HH:mm"));
        analyticalRunEditDialog.getDateTimePicker().setTimeFormat(DateFormat.getTimeInstance(DateFormat.MEDIUM));

        instrumentBindingList = ObservableCollections.observableList(instrumentService.findAll());

        //add binding
        bindingGroup = new BindingGroup();

        JComboBoxBinding instrumentComboBoxBinding = SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ_WRITE, instrumentBindingList, analyticalRunEditDialog.getInstrumentComboBox());
        bindingGroup.addBinding(instrumentComboBoxBinding);

        bindingGroup.bind();

        analyticalRunEditDialog.getUpdateButton().addActionListener(e -> {
            //update analyticalRunToEdit with dialog input
            updateAnalyticalRunToEdit();

            //validate analytical run
            List<String> validationMessages = GuiUtils.validateEntity(analyticalRunToEdit);

            if (validationMessages.isEmpty()) {
                analyticalRunToEdit = analyticalRunService.merge(analyticalRunToEdit);
                int index = analyticalRunsSearchSettingsController.getSelectedAnalyticalRunIndex();

                MessageEvent messageEvent = new MessageEvent("Analytical run store confirmation", "Analytical run " + analyticalRunToEdit.getName() + " was stored successfully!", JOptionPane.INFORMATION_MESSAGE);
                eventBus.post(messageEvent);

                //refresh selection in analytical list in sample edit dialog
                analyticalRunsSearchSettingsController.setSelectedAnalyticalRun(index);
            } else {
                MessageEvent messageEvent = new MessageEvent("Validation failure", validationMessages, JOptionPane.WARNING_MESSAGE);
                eventBus.post(messageEvent);
            }
        });

        analyticalRunBinaryFileDialog.getBinaryFileManagementPanel().addPropertyChangeListener(BinaryFileManagementPanel.ADD, evt -> {
            AnalyticalRunBinaryFile binaryFileToAdd = (AnalyticalRunBinaryFile) evt.getNewValue();
            //set experiment in binary file
            binaryFileToAdd.setAnalyticalRun(analyticalRunToEdit);
            try{
                //save binary file
                binaryFileService.persist(binaryFileToAdd);
                analyticalRunToEdit.getBinaryFiles().add(binaryFileToAdd);
                analyticalRunEditDialog.getAttachementsTextField().setText(getAttachmentsAsString());
            }catch(PersistenceException e){
                MessageEvent messageEvent ;
                if(e.getCause().getClass() == GenericJDBCException.class){
                    messageEvent = new MessageEvent("Analytical run attachments", "Please set the MySQL packet size to a larger value.", JOptionPane.WARNING_MESSAGE);
                }else{
                    messageEvent = new MessageEvent("Analytical run attachments", "Unexpected error occurred.", JOptionPane.ERROR_MESSAGE);
                }
                eventBus.post(messageEvent);
            }
        });

        analyticalRunBinaryFileDialog.getBinaryFileManagementPanel().addPropertyChangeListener(BinaryFileManagementPanel.REMOVE, evt -> {
            AnalyticalRunBinaryFile binaryFileToRemove = (AnalyticalRunBinaryFile) evt.getNewValue();

            if (analyticalRunToEdit.getBinaryFiles().contains(binaryFileToRemove)) {
                analyticalRunToEdit.getBinaryFiles().remove(binaryFileToRemove);
            }

            //update the analytical run
            analyticalRunToEdit = analyticalRunService.merge(analyticalRunToEdit);

            analyticalRunEditDialog.getAttachementsTextField().setText(getAttachmentsAsString());
        });

        analyticalRunBinaryFileDialog.getBinaryFileManagementPanel().addPropertyChangeListener(BinaryFileManagementPanel.FILE_TYPE_CHANGE, evt -> {
            AnalyticalRunBinaryFile binaryFileToUpdate = (AnalyticalRunBinaryFile) evt.getNewValue();

            //update binary file
            binaryFileService.merge(binaryFileToUpdate);

            analyticalRunEditDialog.getAttachementsTextField().setText(getAttachmentsAsString());
        });

        analyticalRunBinaryFileDialog.getCloseButton().addActionListener(e -> analyticalRunBinaryFileDialog.dispose());

        analyticalRunEditDialog.getAttachmentsEditButton().addActionListener(e -> {
            if (analyticalRunToEdit.getId() != null) {
                analyticalRunBinaryFileDialog.getBinaryFileManagementPanel().populateList(analyticalRunToEdit.getBinaryFiles());

                GuiUtils.centerDialogOnComponent(analyticalRunEditDialog, analyticalRunBinaryFileDialog);
                analyticalRunBinaryFileDialog.setVisible(true);
            } else {
                MessageEvent messageEvent = new MessageEvent("Experiment attachments", "Please save the experiment first before adding attachments.", JOptionPane.WARNING_MESSAGE);
                eventBus.post(messageEvent);
            }
        });

        analyticalRunEditDialog.getCloseButton().addActionListener(e -> analyticalRunEditDialog.dispose());
    }

    @Override
    public void showView() {
        GuiUtils.centerDialogOnComponent(analyticalRunsSearchSettingsController.getAnalyticalRunsSearchSettingsDialog(), analyticalRunEditDialog);
        analyticalRunEditDialog.setVisible(true);
    }

    /**
     * Update the analytical run edit dialog with the selected analytical run in
     * the analytical run overview table.
     *
     * @param analyticalRun the selected analytical run in the overview table
     */
    public void updateView(final AnalyticalRun analyticalRun) {
        analyticalRunToEdit = analyticalRun;

        //fetch the instrument if necessary
        analyticalRunService.fetchInstrument(analyticalRun);

        analyticalRunEditDialog.getNameTextField().setText(analyticalRunToEdit.getName());
        if (analyticalRun.getStartDate() != null) {
            analyticalRunEditDialog.getDateTimePicker().setDate(analyticalRunToEdit.getStartDate());
        }

        //set the selected item in the instrument combobox
        analyticalRunEditDialog.getInstrumentComboBox().getModel().setSelectedItem(analyticalRunToEdit.getInstrument());

        analyticalRunEditDialog.getStorageLocationTextField().setText(analyticalRunToEdit.getStorageLocation());

        if(analyticalRun.getId() != null){
            // fetch binary files if analytical run Id is not null
            analyticalRunService.fetchBinaryFiles(analyticalRun);
            analyticalRunEditDialog.getAttachementsTextField().setText(analyticalRun.getBinaryFiles().stream().map(BinaryFile::toString).collect(Collectors.joining(", ")));
        }else{
            analyticalRunEditDialog.getAttachementsTextField().setText("");
        }
        showView();
    }

    /**
     * Listen to an InstrumentChangeEvent.
     *
     * @param instrumentChangeEvent the instrument change event
     */
    @Subscribe
    public void onInstrumentChangeEvent(InstrumentChangeEvent instrumentChangeEvent) {
        instrumentBindingList.clear();
        instrumentBindingList.addAll(instrumentService.findAll());
    }

    /**
     * Listen to a AnalyticalRunChangeEvent and update the runs table if
     * necessary.
     *
     * @param analyticalRunChangeEvent the AnalyticalRunChangeEvent instance
     */
    @Subscribe
    public void onAnalyticalChangeEvent(AnalyticalRunChangeEvent analyticalRunChangeEvent) {
        if (analyticalRunEditDialog.isVisible() && analyticalRunToEdit.getId().equals(analyticalRunChangeEvent.getAnalyticalRunId())) {
            if (analyticalRunChangeEvent.getType().equals(EntityChangeEvent.Type.DELETED)) {
                JOptionPane.showMessageDialog(analyticalRunEditDialog, "Another user removed the sample this run so the run edit dialog will close.", "Experiment removed", JOptionPane.WARNING_MESSAGE);
                analyticalRunEditDialog.dispose();
            }
        }
    }

    /**
     * Listen to a SampleChangeEvent.
     *
     * @param sampleChangeEvent the SampleChangeEvent instance
     */
    @Subscribe
    public void onSampleChangeEvent(SampleChangeEvent sampleChangeEvent) {
        if (analyticalRunEditDialog.isVisible() && analyticalRunToEdit.getSample().getId().equals(sampleChangeEvent.getSampleId())) {
            if (sampleChangeEvent.getType().equals(EntityChangeEvent.Type.DELETED)) {
                JOptionPane.showMessageDialog(analyticalRunEditDialog, "Another user removed the sample associated with this run so the run edit dialog will close.", "Experiment removed", JOptionPane.WARNING_MESSAGE);
                analyticalRunEditDialog.dispose();
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
        if (analyticalRunEditDialog.isVisible() && analyticalRunToEdit.getSample().getExperiment().getId().equals(experimentChangeEvent.getExperimentId())) {
            if (experimentChangeEvent.getType().equals(EntityChangeEvent.Type.DELETED)) {
                JOptionPane.showMessageDialog(analyticalRunEditDialog, "Another user removed the experiment associated with this run so the run edit dialog will close.", "Experiment removed", JOptionPane.WARNING_MESSAGE);
                analyticalRunEditDialog.dispose();
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
        if (analyticalRunEditDialog.isVisible() && analyticalRunToEdit.getSample().getExperiment().getProject().getId().equals(projectChangeEvent.getProjectId())) {
            if (projectChangeEvent.getType().equals(EntityChangeEvent.Type.DELETED)) {
                JOptionPane.showMessageDialog(analyticalRunEditDialog, "Another user removed the project associated with this run so the run edit dialog will close.", "Project removed", JOptionPane.WARNING_MESSAGE);
                analyticalRunEditDialog.dispose();
            }
        }
    }

    /**
     * Update the instance fields of the selected analytical run in the
     * analytical runs table
     */
    private void updateAnalyticalRunToEdit() {
        analyticalRunToEdit.setName(analyticalRunEditDialog.getNameTextField().getText());
        if (analyticalRunEditDialog.getDateTimePicker().getDate() != null) {
            analyticalRunToEdit.setStartDate(analyticalRunEditDialog.getDateTimePicker().getDate());
        }
        if (analyticalRunEditDialog.getInstrumentComboBox().getSelectedIndex() != -1) {
            analyticalRunToEdit.setInstrument(instrumentBindingList.get(analyticalRunEditDialog.getInstrumentComboBox().getSelectedIndex()));
        }
        analyticalRunToEdit.setStorageLocation(analyticalRunEditDialog.getStorageLocationTextField().getText());
    }

    /**
     * Get the attachments file names as a concatenated string.
     *
     * @return the joined attachments String
     */
    private String getAttachmentsAsString() {
        return analyticalRunToEdit.getBinaryFiles().stream().map(BinaryFile::toString).collect(Collectors.joining(", "));
    }

}
