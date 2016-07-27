package com.compomics.colims.client.controller;

import com.compomics.colims.client.compoment.BinaryFileManagementPanel;
import com.compomics.colims.client.event.EntityChangeEvent;
import com.compomics.colims.client.event.ExperimentChangeEvent;
import com.compomics.colims.client.event.ProjectChangeEvent;
import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.ExperimentBinaryFileDialog;
import com.compomics.colims.client.view.ExperimentEditDialog;
import com.compomics.colims.core.service.BinaryFileService;
import com.compomics.colims.core.service.ExperimentService;
import com.compomics.colims.model.Experiment;
import com.compomics.colims.model.ExperimentBinaryFile;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The experiment edit view controller.
 *
 * @author Niels Hulstaert
 */
@Component("experimentEditController")
@Lazy
public class ExperimentEditController implements Controllable {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(ExperimentEditController.class);

    //model
    private Experiment experimentToEdit;
    //view
    private ExperimentEditDialog experimentEditDialog;
    private ExperimentBinaryFileDialog experimentBinaryFileDialog;
    //parent controller
    @Autowired
    private ProjectManagementController projectManagementController;
    @Autowired
    private MainController mainController;
    //services
    @Autowired
    private ExperimentService experimentService;
    @Autowired
    private BinaryFileService binaryFileService;
    @Autowired
    private EventBus eventBus;

    /**
     * Get the view of this controller.
     *
     * @return the ExperimentEditDialog
     */
    public ExperimentEditDialog getExperimentEditDialog() {
        return experimentEditDialog;
    }

    /**
     * Get the experiment to edit.
     *
     * @return the experiment to edit
     */
    public Experiment getExperimentToEdit() {
        return experimentToEdit;
    }

    @Override
    @PostConstruct
    public void init() {
        //register to event bus
        eventBus.register(this);

        //init view
        experimentEditDialog = new ExperimentEditDialog(mainController.getMainFrame(), true);
        experimentBinaryFileDialog = new ExperimentBinaryFileDialog(experimentEditDialog, true);
        experimentBinaryFileDialog.getBinaryFileManagementPanel().init(ExperimentBinaryFile.class);

        //add action listeners
        experimentEditDialog.getSaveOrUpdateButton().addActionListener(e -> {
            List<String> validationMessages = new ArrayList<>();

            //update experimentToEdit with dialog input, catch NumberFormatException for experiment number
            try {
                updateExperimentToEdit();
            } catch (NumberFormatException nfe) {
                validationMessages.add("The experiment number must be a number.");
            }

            //validate experiment
            validationMessages.addAll(GuiUtils.validateEntity(experimentToEdit));
            //check if the experiment title already exists in the db
            if (isExistingExperimentTitle(experimentToEdit)) {
                validationMessages.add(experimentToEdit.getTitle() + " already exists in the database for the project,"
                        + System.lineSeparator() + "please choose another experiment title.");
            }
            if (validationMessages.isEmpty()) {
                int index;
                EntityChangeEvent.Type type;

                if (experimentToEdit.getId() != null) {
                    experimentToEdit = experimentService.merge(experimentToEdit);

                    index = projectManagementController.getSelectedExperimentIndex();
                    type = EntityChangeEvent.Type.UPDATED;
                } else {
                    //set project
                    experimentToEdit.setProject(projectManagementController.getSelectedProject());

                    experimentService.persist(experimentToEdit);

                    index = projectManagementController.getExperimentsSize() - 1;
                    type = EntityChangeEvent.Type.CREATED;

                    //add experiment to overview table
                    projectManagementController.addExperiment(experimentToEdit);

                    experimentEditDialog.getSaveOrUpdateButton().setText("update");
                }
                ExperimentChangeEvent experimentChangeEvent = new ExperimentChangeEvent(type, experimentToEdit.getId());
                eventBus.post(experimentChangeEvent);

                MessageEvent messageEvent = new MessageEvent("Experiment store confirmation", "Experiment " + experimentToEdit.getNumber() + " was stored successfully!", JOptionPane.INFORMATION_MESSAGE);
                eventBus.post(messageEvent);

                //refresh selection in experiment table
                projectManagementController.setSelectedExperiment(index);
            } else {
                MessageEvent messageEvent = new MessageEvent("Validation failure", validationMessages, JOptionPane.WARNING_MESSAGE);
                eventBus.post(messageEvent);
            }
        });

        experimentBinaryFileDialog.getBinaryFileManagementPanel().addPropertyChangeListener(BinaryFileManagementPanel.ADD, evt -> {
            ExperimentBinaryFile binaryFileToAdd = (ExperimentBinaryFile) evt.getNewValue();

            //set experiment in binary file
            binaryFileToAdd.setExperiment(experimentToEdit);

            //save binary file
            binaryFileService.persist(binaryFileToAdd);

            experimentToEdit.getBinaryFiles().add(binaryFileToAdd);
            experimentEditDialog.getAttachementsTextField().setText(getAttachmentsAsString());
        });

        experimentBinaryFileDialog.getBinaryFileManagementPanel().addPropertyChangeListener(BinaryFileManagementPanel.REMOVE, evt -> {
            ExperimentBinaryFile binaryFileToRemove = (ExperimentBinaryFile) evt.getNewValue();

            if (experimentToEdit.getBinaryFiles().contains(binaryFileToRemove)) {
                experimentToEdit.getBinaryFiles().remove(binaryFileToRemove);
            }

            //update the experiment
            experimentToEdit = experimentService.merge(experimentToEdit);

            experimentEditDialog.getAttachementsTextField().setText(getAttachmentsAsString());
        });

        experimentBinaryFileDialog.getBinaryFileManagementPanel().addPropertyChangeListener(BinaryFileManagementPanel.FILE_TYPE_CHANGE, evt -> {
            ExperimentBinaryFile binaryFileToUpdate = (ExperimentBinaryFile) evt.getNewValue();

            //update binary file
            binaryFileService.merge(binaryFileToUpdate);

            experimentEditDialog.getAttachementsTextField().setText(getAttachmentsAsString());
        });

        experimentBinaryFileDialog.getCloseButton().addActionListener(e -> experimentBinaryFileDialog.dispose());

        experimentEditDialog.getAttachmentsEditButton().addActionListener(e -> {
            if (experimentToEdit.getId() != null) {
                experimentBinaryFileDialog.getBinaryFileManagementPanel().populateList(experimentToEdit.getBinaryFiles());

                GuiUtils.centerDialogOnComponent(experimentEditDialog, experimentBinaryFileDialog);
                experimentBinaryFileDialog.setVisible(true);
            } else {
                MessageEvent messageEvent = new MessageEvent("Experiment attachments", "Please save the experiment first before adding attachments.", JOptionPane.WARNING_MESSAGE);
                eventBus.post(messageEvent);
            }
        });

        experimentEditDialog.getCloseButton().addActionListener(e -> experimentEditDialog.dispose());

    }

    @Override
    public void showView() {
        GuiUtils.centerDialogOnComponent(mainController.getMainFrame(), experimentEditDialog);
        experimentEditDialog.setVisible(true);
    }

    /**
     * Update the experiment edit dialog with the selected experiment in the
     * <p/>
     * experiment overview table.
     *
     * @param experiment the Experiment instance
     */
    public void updateView(final Experiment experiment) {
        experimentToEdit = experiment;

        if (experimentToEdit.getId() != null) {
            experimentEditDialog.getSaveOrUpdateButton().setText("update");
            //fetch experiment binary files
            experimentService.fetchBinaryFiles(experimentToEdit);
        } else {
            experimentEditDialog.getSaveOrUpdateButton().setText("save");
        }

        experimentEditDialog.getTitleTextField().setText(experimentToEdit.getTitle());
        experimentEditDialog.getNumberTextField().setText(Long.toString(experimentToEdit.getNumber()));
        experimentEditDialog.getDescriptionTextArea().setText(experimentToEdit.getDescription());
        experimentEditDialog.getStorageLocationTextField().setText(experimentToEdit.getStorageLocation());
        experimentEditDialog.getAttachementsTextField().setText(getAttachmentsAsString());

        showView();
    }

    /**
     * Listen to a ExperimentChangeEvent.
     *
     * @param experimentChangeEvent the ExperimentChangeEvent instance
     */
    @Subscribe
    public void onExperimentChangeEvent(ExperimentChangeEvent experimentChangeEvent) {
        if (experimentEditDialog.isVisible() && experimentToEdit.getId().equals(experimentChangeEvent.getExperimentId())) {
            if (experimentChangeEvent.getType().equals(EntityChangeEvent.Type.DELETED)) {
                JOptionPane.showMessageDialog(experimentEditDialog, "Another user removed this experiment so the experiment edit dialog will close.", "Experiment removed", JOptionPane.WARNING_MESSAGE);
                experimentEditDialog.dispose();
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
        if (experimentEditDialog.isVisible() && experimentToEdit.getProject().getId().equals(projectChangeEvent.getProjectId())) {
            if (projectChangeEvent.getType().equals(EntityChangeEvent.Type.DELETED)) {
                JOptionPane.showMessageDialog(experimentEditDialog, "Another user removed the project associated with this experiment so the experiment edit dialog will close.", "Project removed", JOptionPane.WARNING_MESSAGE);
                experimentEditDialog.dispose();
            }
        }
    }

    /**
     * Update the instance fields of the selected experiment in the experiments
     * table.
     */
    private void updateExperimentToEdit() {
        experimentToEdit.setTitle(experimentEditDialog.getTitleTextField().getText());
        experimentToEdit.setNumber(Long.parseLong(experimentEditDialog.getNumberTextField().getText()));
        experimentToEdit.setDescription(experimentEditDialog.getDescriptionTextArea().getText());
        experimentToEdit.setStorageLocation(experimentEditDialog.getStorageLocationTextField().getText());
    }

    /**
     * Check if a experiment with the given experiment title exists in the
     * database.
     *
     * @param experiment the experiment
     * @return does the experiment title exist
     */
    private boolean isExistingExperimentTitle(final Experiment experiment) {
        Long count = experimentService.countByProjectIdAndTitle(projectManagementController.getSelectedProject().getId(), experiment);

        return count != 0;
    }

    /**
     * Get the attachments file names as a concatenated string.
     *
     * @return the joined attachments String
     */
    private String getAttachmentsAsString() {
        return experimentToEdit.getBinaryFiles().stream().map(binaryFile -> binaryFile.toString()).collect(Collectors.joining(", "));
    }

}
