package com.compomics.colims.client.controller;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.AdvancedTableModel;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.GlazedListsSwing;
import com.compomics.colims.client.compoment.BinaryFileManagementPanel;
import com.compomics.colims.client.event.message.DbConstraintMessageEvent;
import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.model.tableformat.SampleManagementTableFormat;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.ExperimentBinaryFileDialog;
import com.compomics.colims.client.view.ExperimentEditDialog;
import com.compomics.colims.core.service.AbstractBinaryFileService;
import com.compomics.colims.core.service.ExperimentService;
import com.compomics.colims.core.service.SampleService;
import com.compomics.colims.model.Experiment;
import com.compomics.colims.model.ExperimentBinaryFile;
import com.compomics.colims.model.Protocol;
import com.compomics.colims.model.Sample;
import com.compomics.colims.model.comparator.IdComparator;
import com.google.common.base.Joiner;
import com.google.common.eventbus.EventBus;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
    //child controller
    @Autowired
    private SampleEditController sampleEditController;
    //parent controller
    @Autowired
    private ProjectManagementController projectManagementController;
    @Autowired
    private ColimsController colimsController;
    //services
    @Autowired
    private ExperimentService experimentService;
    @Autowired
    private SampleService sampleService;
    @Autowired
    private AbstractBinaryFileService abstractBinaryFileService;
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
        experimentBinaryFileDialog = new ExperimentBinaryFileDialog(experimentEditDialog, true);
        experimentBinaryFileDialog.getBinaryFileManagementPanel().init(ExperimentBinaryFile.class);

        //init child controller
        sampleEditController.init();

        //init projects experiment table
        SortedList<Sample> sortedSamples = new SortedList<>(samples, new IdComparator());
        samplesTableModel = GlazedListsSwing.eventTableModel(sortedSamples, new SampleManagementTableFormat());
        experimentEditDialog.getSamplesTable().setModel(samplesTableModel);
        samplesSelectionModel = new DefaultEventSelectionModel<>(sortedSamples);
        samplesSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        experimentEditDialog.getSamplesTable().setSelectionModel(samplesSelectionModel);
        
        //set column widths
        experimentEditDialog.getSamplesTable().getColumnModel().getColumn(SampleManagementTableFormat.SAMPLE_ID).setPreferredWidth(5);
        experimentEditDialog.getSamplesTable().getColumnModel().getColumn(SampleManagementTableFormat.NAME).setPreferredWidth(200);
        experimentEditDialog.getSamplesTable().getColumnModel().getColumn(SampleManagementTableFormat.CONDITION).setPreferredWidth(100);
        experimentEditDialog.getSamplesTable().getColumnModel().getColumn(SampleManagementTableFormat.PROTOCOL).setPreferredWidth(100);
        experimentEditDialog.getSamplesTable().getColumnModel().getColumn(SampleManagementTableFormat.CREATED).setPreferredWidth(50);        
        experimentEditDialog.getSamplesTable().getColumnModel().getColumn(SampleManagementTableFormat.NUMBER_OF_RUNS).setPreferredWidth(50);

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

                    //refresh selection in experiment table
                    projectManagementController.setSelectedExperiment(index);
                } else {
                    MessageEvent messageEvent = new MessageEvent("validation failure", validationMessages, JOptionPane.WARNING_MESSAGE);
                    eventBus.post(messageEvent);
                }
            }
        });

        experimentBinaryFileDialog.getBinaryFileManagementPanel().addPropertyChangeListener(BinaryFileManagementPanel.ADD, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                ExperimentBinaryFile binaryFileToAdd = (ExperimentBinaryFile) evt.getNewValue();

                //set experiment in binary file
                binaryFileToAdd.setExperiment(experimentToEdit);

                //save binary file
                abstractBinaryFileService.save(binaryFileToAdd);

                experimentToEdit.getBinaryFiles().add(binaryFileToAdd);
                experimentEditDialog.getAttachementsTextField().setText(getAttachmentsAsString());
            }
        });

        experimentBinaryFileDialog.getBinaryFileManagementPanel().addPropertyChangeListener(BinaryFileManagementPanel.REMOVE, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                ExperimentBinaryFile binaryFileToRemove = (ExperimentBinaryFile) evt.getNewValue();

                if (experimentToEdit.getBinaryFiles().contains(binaryFileToRemove)) {
                    experimentToEdit.getBinaryFiles().remove(binaryFileToRemove);
                }

                //remove binary file
                abstractBinaryFileService.delete(binaryFileToRemove);

                experimentEditDialog.getAttachementsTextField().setText(getAttachmentsAsString());
            }
        });

        experimentBinaryFileDialog.getBinaryFileManagementPanel().addPropertyChangeListener(BinaryFileManagementPanel.FILE_TYPE_CHANGE, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                ExperimentBinaryFile binaryFileToUpdate = (ExperimentBinaryFile) evt.getNewValue();

                //update binary file
                abstractBinaryFileService.update(binaryFileToUpdate);

                experimentEditDialog.getAttachementsTextField().setText(getAttachmentsAsString());
            }
        });

        experimentEditDialog.getAttachmentsEditButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                experimentBinaryFileDialog.getBinaryFileManagementPanel().populateList(experimentToEdit.getBinaryFiles());

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

        experimentEditDialog.getAddSampleButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sampleEditController.updateView(createDefaultSample());
            }
        });

        experimentEditDialog.getEditSampleButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Sample selectedSample = getSelectedSample();
                if (selectedSample != null) {
                    sampleEditController.updateView(selectedSample);
                } else {
                    eventBus.post(new MessageEvent("sample selection", "Please select a sample to edit.", JOptionPane.INFORMATION_MESSAGE));
                }
            }
        });

        experimentEditDialog.getDeleteSampleButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Sample sampleToDelete = getSelectedSample();

                if (sampleToDelete != null) {
                    try {
                        sampleService.delete(sampleToDelete);

                        //remove from overview table and clear selection
                        samples.remove(sampleToDelete);
                        samplesSelectionModel.clearSelection();
                    } catch (DataIntegrityViolationException dive) {
                        //check if the sample can be deleted without breaking existing database relations,
                        //i.e. are there any constraints violations
                        if (dive.getCause() instanceof ConstraintViolationException) {
                            DbConstraintMessageEvent dbConstraintMessageEvent = new DbConstraintMessageEvent("sample", sampleToDelete.getName());
                            eventBus.post(dbConstraintMessageEvent);
                        } else {
                            //pass the exception
                            throw dive;
                        }
                    }
                }
            }
        });
    }

    @Override
    public void showView() {
        experimentEditDialog.setLocationRelativeTo(null);
        experimentEditDialog.setVisible(true);
    }

    /**
     * Update the experiment edit dialog with the selected experiment in the
     * experiment overview table.
     */
    public void updateView(Experiment experiment) {
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

        //fill project experiments table                        
        GlazedLists.replaceAll(samples, experimentToEdit.getSamples(), false);

        showView();
    }

    /**
     * Get the row index of the selected sample in the samples table
     *
     * @return
     */
    public int getSelectedSampleIndex() {
        return samplesSelectionModel.getLeadSelectionIndex();
    }

    /**
     * Set the selected sample in the samples table
     *
     * @param index
     */
    public void setSelectedSample(int index) {
        samplesSelectionModel.clearSelection();
        samplesSelectionModel.setLeadSelectionIndex(index);
    }

    /**
     * Add a sample to the samples table
     *
     * @param sample
     */
    public void addSample(Sample sample) {
        samples.add(sample);
    }

    /**
     * Get the number of samples in the samples table
     *
     * @return
     */
    public int getSamplesSize() {
        return samples.size();
    }

    /**
     * Get the selected sample from the sample overview table.
     *
     * @return the selected sample, null if no sample is selected
     */
    public Sample getSelectedSample() {
        Sample selectedSample = null;

        EventList<Sample> selectedSamples = samplesSelectionModel.getSelected();
        if (!selectedSamples.isEmpty()) {
            selectedSample = selectedSamples.get(0);
        }

        return selectedSample;
    }

    /**
     * Update the instance fields of the selected experiment in the experiments
     * table
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
    private boolean isExistingExperimentTitle(Experiment experiment) {
        boolean isExistingExperimentTitle = true;
        Experiment foundExperiment = experimentService.findByTitle(experiment.getTitle());
        if (foundExperiment == null) {
            isExistingExperimentTitle = false;
        }

        return isExistingExperimentTitle;
    }

    /**
     * Get the attachments file names as a concatenated string.
     *
     * @return
     */
    private String getAttachmentsAsString() {
        String concatenatedString = "";

        Joiner joiner = Joiner.on(", ");
        concatenatedString = joiner.join(experimentToEdit.getBinaryFiles());

        return concatenatedString;
    }

    /**
     * Create a default sample, with some default properties.
     *
     * @return the default sample
     */
    private Sample createDefaultSample() {
        Sample defaultSample = new Sample();

        defaultSample.setName("default sample name");
        Protocol mostUsedProtocol = sampleService.getMostUsedProtocol();
        if (mostUsedProtocol != null) {
            defaultSample.setProtocol(mostUsedProtocol);
        }

        return defaultSample;
    }
}
