package com.compomics.colims.client.controller;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.swing.AdvancedTableModel;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import com.compomics.colims.client.compoment.BinaryFileManagementPanel;
import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.ExperimentBinaryFileDialog;
import com.compomics.colims.client.view.ExperimentEditDialog;
import com.compomics.colims.core.service.ExperimentService;
import com.compomics.colims.core.service.ProtocolService;
import com.compomics.colims.model.AbstractBinaryFile;
import com.compomics.colims.model.Experiment;
import com.compomics.colims.model.ExperimentBinaryFile;
import com.compomics.colims.model.Sample;
import com.google.common.eventbus.EventBus;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("experimentEditController")
public class ExperimentEditController implements Controllable {

    private static final Logger LOGGER = Logger.getLogger(ExperimentEditController.class);
    //model   
    private EventList<Sample> samples = new BasicEventList<>();
    private AdvancedTableModel<Sample> samplesTableModel;
    private DefaultEventSelectionModel<Sample> samplesSelectionModel;
    private Experiment experimentToEdit;
    //view
    private ExperimentEditDialog experimentEditDialog;
    private ExperimentBinaryFileDialog experimentBinaryFileDialog;
    //parent controller
    @Autowired
    private ProjectManagementController projectManagementController;
    @Autowired
    private ColimsController colimsController;
    //services
    @Autowired
    private ExperimentService experimentService;
    @Autowired
    private ProtocolService protocolService;
    @Autowired
    private EventBus eventBus;

    public ExperimentEditDialog getExperimentEditDialog() {
        return experimentEditDialog;
    }

    public void init() {
        //register to event bus
        eventBus.register(this);

        //init view
        experimentEditDialog = new ExperimentEditDialog(colimsController.getColimsFrame(), true);
        experimentBinaryFileDialog = new ExperimentBinaryFileDialog(colimsController.getColimsFrame(), true);

        //add action listeners                        
        experimentEditDialog.getSaveOrUpdateButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //update projectToEdit with dialog input
                updateExperimentToEdit();

                //validate project
                List<String> validationMessages = GuiUtils.validateEntity(experimentToEdit);
                //check for a new experiment if the experiment title already exists in the db                
                if (experimentToEdit.getId() == null && isExistingExperimentTitle(experimentToEdit)) {
                    validationMessages.add(experimentToEdit.getTitle() + " already exists in the database,"
                            + "\n" + "please choose another experiment title.");
                }
                int index = 0;
                if (validationMessages.isEmpty()) {
                    if (experimentToEdit.getId() != null) {
                        experimentService.update(experimentToEdit);
                        index = projectManagementController.getSelectedExperimentIndex();
                    } else {
                        experimentService.save(experimentToEdit);
                        //add experiment to overview table
                        projectManagementController.addExperiment(experimentToEdit);
                        index = projectManagementController.getExperimentsSize() - 1;
                    }
                    experimentEditDialog.getSaveOrUpdateButton().setText("update");

                    MessageEvent messageEvent = new MessageEvent("experiment persist confirmation", "Experiment " + experimentToEdit.getNumber() + " was persisted successfully!", JOptionPane.INFORMATION_MESSAGE);
                    eventBus.post(messageEvent);

                    //refresh selection in experiment list in management overview dialog
                    projectManagementController.setSelectedExperiment(index);
                } else {
                    MessageEvent messageEvent = new MessageEvent("validation failure", validationMessages, JOptionPane.WARNING_MESSAGE);
                    eventBus.post(messageEvent);
                }
            }
        });
        
        experimentBinaryFileDialog.getBinaryFileManagementPanel().addPropertyChangeListener(BinaryFileManagementPanel.ADDED, new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                List<ExperimentBinaryFile> binaryFilesToAdd = (List<ExperimentBinaryFile>) evt.getNewValue();
                
                experimentToEdit.getBinaryFiles().addAll(binaryFilesToAdd);
            }
        });
        
        experimentBinaryFileDialog.getBinaryFileManagementPanel().addPropertyChangeListener(BinaryFileManagementPanel.REMOVED, new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                List<ExperimentBinaryFile> binaryFilesToRemove = (List<ExperimentBinaryFile>) evt.getNewValue();
                
//                if(experimentToEdit.getBinaryFiles().){
//                    
//                }
            }
        });
        
        experimentEditDialog.getAttachmentsEditButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                experimentBinaryFileDialog.setLocationRelativeTo(null);
                experimentBinaryFileDialog.setVisible(true);
            }
        });

        experimentEditDialog.getCancelButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                experimentEditDialog.dispose();
            }
        });
    }

    @Override
    public void showView() {
        experimentEditDialog.setLocationRelativeTo(null);
        experimentEditDialog.setVisible(true);
    }

    /**
     * Update the project edit dialog with the selected project in the project
     * overview table.
     */
    public void updateView(Experiment experiment) {
        experimentToEdit = experiment;

        if (experimentToEdit.getId() != null) {
            experimentEditDialog.getSaveOrUpdateButton().setText("update");
        } else {
            experimentEditDialog.getSaveOrUpdateButton().setText("save");
        }

        experimentEditDialog.getTitleTextField().setText(experimentToEdit.getTitle());
        experimentEditDialog.getNumberTextField().setText(Long.toString(experimentToEdit.getNumber()));
        experimentEditDialog.getDescriptionTextArea().setText(experimentToEdit.getDescription());
        experimentEditDialog.getStorageLocationTextField().setText(experimentToEdit.getStorageLocation());

        //fill project experiments table                        
        GlazedLists.replaceAll(samples, experimentToEdit.getSamples(), false);

        showView();
    }

    /**
     * Update the instance fields of the selected experiment in the experiments
     * table
     */
    private void updateExperimentToEdit() {
        experimentToEdit.setTitle(experimentEditDialog.getTitleTextField().getText());
        experimentToEdit.setNumber(Long.parseLong(experimentEditDialog.getNumberTextField().getText()));
        experimentToEdit.setDescription(experimentEditDialog.getDescriptionTextArea().getText());
    }

    /**
     * Check if a experiment with the given experiment title exists in the
     * database.
     *
     * @param experiment the experiment
     * @return does the experiment title exist
     */
    private boolean isExistingExperimentTitle(Experiment experiment) {
        boolean isExistingExperimentTitle = true;
        Experiment foundExperiment = experimentService.findByTitle(experiment.getTitle());
        if (foundExperiment == null) {
            isExistingExperimentTitle = false;
        }

        return isExistingExperimentTitle;
    }
}
