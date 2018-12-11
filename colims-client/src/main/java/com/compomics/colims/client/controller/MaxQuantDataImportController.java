package com.compomics.colims.client.controller;

import com.compomics.colims.client.controller.admin.FastaDbManagementController;
import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.view.MaxQuantDataImportPanel;
import com.compomics.colims.core.io.MaxQuantImport;
import com.compomics.colims.core.service.FastaDbService;
import com.compomics.colims.core.util.PathUtils;
import com.compomics.colims.model.FastaDb;
import com.compomics.colims.model.enums.FastaDbType;
import com.compomics.util.io.filefilters.XmlFileFilter;
import com.google.common.eventbus.EventBus;
import org.slf4j.Logger;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;

/**
 * The MaxQuant data import view controller.
 *
 * @author Niels Hulstaert
 */
@Component("maxQuantDataImportController")
@Lazy
public class MaxQuantDataImportController implements Controllable {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MaxQuantDataImportController.class);

    /**
     * The experiments location as provided in the client properties file.
     */
    @Value("${experiments.path}")
    private String experimentsPath = "";
    //model
    private Path mqparFile;
    private Path combinedDirectory;
    private Path fullCombinedDirectory;
    private FastaDb primaryFastaDb;
    private FastaDb contaminantsFastaDb;
    private boolean includeContaminants;
    private boolean includeUnidentifiedSpectra;
    private boolean storeRunsSequentially;
    private List<String> selectedProteinGroupHeaders;
    private ObservableList<FastaDb> additionalFastaDbBindingList;
    private BindingGroup bindingGroup;
    //view
    private MaxQuantDataImportPanel maxQuantDataImportPanel;
    //parent controller
    @Autowired
    private AnalyticalRunsAdditionController analyticalRunsAdditionController;
    //child controller
    @Autowired
    private FastaDbManagementController fastaDbManagementController;
    //services
    @Autowired
    private EventBus eventBus;
    @Autowired
    private FastaDbService fastaDbService;

    @Override
    @PostConstruct
    public void init() {
        //get view from parent controller
        maxQuantDataImportPanel = analyticalRunsAdditionController.getAnalyticalRunsAdditionDialog().getMaxQuantDataImportPanel();

        //register to event bus
        eventBus.register(this);

        //init binding
        bindingGroup = new BindingGroup();

        additionalFastaDbBindingList = ObservableCollections.observableList(new ArrayList<>());
        JListBinding additionalFastaDbListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, additionalFastaDbBindingList, maxQuantDataImportPanel.getAdditionalFastaFileList());
        bindingGroup.addBinding(additionalFastaDbListBinding);

        bindingGroup.bind();

        Path experimentsDirectory = Paths.get(experimentsPath);
        if (!Files.exists(experimentsDirectory)) {
            throw new IllegalArgumentException("The experiments directory defined in the client properties file (/config/colims-client.properties) " + experimentsPath + " doesn't exist."
                    + System.lineSeparator()
                    + "Please make sure the path is correct and mapped on the file system.");
        }

        //init the mqpar file directory selection
        //disable select multiple files
        maxQuantDataImportPanel.getMqparFileChooser().setMultiSelectionEnabled(false);
        //set select directories only
        maxQuantDataImportPanel.getMqparFileChooser().setFileFilter(new XmlFileFilter());

        maxQuantDataImportPanel.getMqparFileChooser().setCurrentDirectory(experimentsDirectory.toFile());

        maxQuantDataImportPanel.getSelectMqparFileButton().addActionListener(e -> {
            //in response to the button click, show open dialog
            int returnVal = maxQuantDataImportPanel.getMqparFileChooser().showOpenDialog(maxQuantDataImportPanel);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    Path fullMqparPath = maxQuantDataImportPanel.getMqparFileChooser().getSelectedFile().toPath();
                    mqparFile = PathUtils.getRelativeChildPath(experimentsDirectory, fullMqparPath);
                    //show MaxQuant directory name in label
                    maxQuantDataImportPanel.getMqparFileTextField().setText(mqparFile.getFileName().toString());
                } catch (IllegalArgumentException ex) {
                    MessageEvent messageEvent = new MessageEvent("Invalid mqpar file location", "The mqpar file location doesn't contain the experiments directory as defined in the properties file.", JOptionPane.WARNING_MESSAGE);
                    eventBus.post(messageEvent);
                }
            }
        });

        //init the combined directory file selection
        //disable select multiple files
        maxQuantDataImportPanel.getCombinedFolderChooser().setMultiSelectionEnabled(false);
        //set select directories only
        maxQuantDataImportPanel.getCombinedFolderChooser().setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        maxQuantDataImportPanel.getCombinedFolderChooser().setCurrentDirectory(experimentsDirectory.toFile());

        maxQuantDataImportPanel.getSelectCombinedFolderButton().addActionListener(e -> {
            //in response to the button click, show open dialog
            int returnVal = maxQuantDataImportPanel.getCombinedFolderChooser().showOpenDialog(maxQuantDataImportPanel);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    fullCombinedDirectory = maxQuantDataImportPanel.getCombinedFolderChooser().getSelectedFile().toPath();
                    combinedDirectory = PathUtils.getRelativeChildPath(experimentsDirectory, fullCombinedDirectory);
                    //show combined directory name in label
                    maxQuantDataImportPanel.getCombinedFolderDirectoryTextField().setText(combinedDirectory.toString());

                    //set the mqpar file file chooser to the parent directory of the combined directory
                    maxQuantDataImportPanel.getMqparFileChooser().setCurrentDirectory(fullCombinedDirectory.getParent().toFile());
                } catch (IllegalArgumentException ex) {
                    MessageEvent messageEvent = new MessageEvent("Invalid combined directory location", "The combined directory location doesn't contain the experiments directory as defined in the properties file.", JOptionPane.WARNING_MESSAGE);
                    eventBus.post(messageEvent);
                }
            }
        });

        maxQuantDataImportPanel.getSelectPrimaryFastaDbButton().addActionListener(e -> {
            fastaDbManagementController.showView();
            primaryFastaDb = fastaDbManagementController.getSelectedFastaDb();

            if (primaryFastaDb != null) {
                maxQuantDataImportPanel.getPrimaryFastaDbTextField().setText(primaryFastaDb.getFilePath());
            } else {
                maxQuantDataImportPanel.getPrimaryFastaDbTextField().setText("");
            }
        });

        maxQuantDataImportPanel.getSelectAdditionalFastaDbButton().addActionListener(e -> {
            fastaDbManagementController.showView();

            if (fastaDbManagementController.getSelectedFastaDb() != null) {
                additionalFastaDbBindingList.add(fastaDbManagementController.getSelectedFastaDb());
                maxQuantDataImportPanel.getAdditionalFastaFileList().setCellRenderer(new AdditionalFastaDbListCellRenderer());
            }
        });

        maxQuantDataImportPanel.getRemoveAdditionalFastaDbButton().addActionListener(e -> {
            if (maxQuantDataImportPanel.getAdditionalFastaFileList().getSelectedIndex() != -1) {
                additionalFastaDbBindingList.remove(maxQuantDataImportPanel.getAdditionalFastaFileList().getSelectedIndex());
                maxQuantDataImportPanel.getAdditionalFastaFileList().getSelectionModel().clearSelection();
            } else {
                eventBus.post(new MessageEvent("Additional Fasta db selection", "Please select an additional fasta db to remove.", JOptionPane.INFORMATION_MESSAGE));
            }

        });

        maxQuantDataImportPanel.getSelectContaminantsFastaDbButton().addActionListener(e -> {
            fastaDbManagementController.showView();
            contaminantsFastaDb = fastaDbManagementController.getSelectedFastaDb();

            if (contaminantsFastaDb != null) {
                maxQuantDataImportPanel.getContaminantsFastaDbTextField().setText(contaminantsFastaDb.getFilePath());
            } else {
                maxQuantDataImportPanel.getContaminantsFastaDbTextField().setText("");
            }
        });

        maxQuantDataImportPanel.getContaminantsCheckBox().addActionListener(e -> includeContaminants = true);

        maxQuantDataImportPanel.getUnidentifiedSpectraCheckBox().addActionListener(e -> includeUnidentifiedSpectra = true);

        maxQuantDataImportPanel.getStoreRunsSequentiallyCheckBox().addActionListener(e -> storeRunsSequentially = true);
    }

    @Override
    public void showView() {
        //reset the FASTA fields
        primaryFastaDb = null;
        contaminantsFastaDb = null;
        includeContaminants = false;
        includeUnidentifiedSpectra = false;
        storeRunsSequentially = false;
        selectedProteinGroupHeaders = new ArrayList<>();
        additionalFastaDbBindingList.clear();
        //reset the input fields
        maxQuantDataImportPanel.getMqparFileTextField().setText("");
        maxQuantDataImportPanel.getCombinedFolderDirectoryTextField().setText("");
        maxQuantDataImportPanel.getPrimaryFastaDbTextField().setText("");
        maxQuantDataImportPanel.getAdditionalFastaFileList().clearSelection();
        maxQuantDataImportPanel.getContaminantsFastaDbTextField().setText("");
        maxQuantDataImportPanel.getContaminantsCheckBox().setSelected(false);
        maxQuantDataImportPanel.getUnidentifiedSpectraCheckBox().setSelected(false);
        //reset the file chooser directories
        Path experimentsDirectory = Paths.get(experimentsPath);
        maxQuantDataImportPanel.getMqparFileChooser().setCurrentDirectory(experimentsDirectory.toFile());
        maxQuantDataImportPanel.getCombinedFolderChooser().setCurrentDirectory(experimentsDirectory.toFile());
    }

    /**
     * Show the view with the given {@link MaxQuantImport} values filled in.
     *
     * @param maxQuantImport the {@link MaxQuantImport} instance
     */
    public void populateView(MaxQuantImport maxQuantImport) {
        showView();
        maxQuantDataImportPanel.getMqparFileTextField().setText(maxQuantImport.getMqParFile().toString());
        maxQuantDataImportPanel.getCombinedFolderDirectoryTextField().setText(maxQuantImport.getCombinedDirectory().toString());
        if (maxQuantImport.getFastaDbIds().get(FastaDbType.PRIMARY).get(0) != null) {
            primaryFastaDb = fastaDbService.findById(maxQuantImport.getFastaDbIds().get(FastaDbType.PRIMARY).get(0));
            maxQuantDataImportPanel.getPrimaryFastaDbTextField().setText(primaryFastaDb.getFilePath());
        } else {
            primaryFastaDb = null;
            maxQuantDataImportPanel.getPrimaryFastaDbTextField().setText("");
        }
        maxQuantImport.getFastaDbIds().get(FastaDbType.ADDITIONAL).stream().forEach((additionalFastaDbId) -> {
            FastaDb additionalFastaDb = fastaDbService.findById(additionalFastaDbId);
            additionalFastaDbBindingList.add(additionalFastaDb);
        });
        if (maxQuantImport.getFastaDbIds().get(FastaDbType.CONTAMINANTS).get(0) != null) {
            contaminantsFastaDb = fastaDbService.findById(maxQuantImport.getFastaDbIds().get(FastaDbType.CONTAMINANTS).get(0));
            maxQuantDataImportPanel.getContaminantsFastaDbTextField().setText(contaminantsFastaDb.getFilePath());
        } else {
            contaminantsFastaDb = null;
            maxQuantDataImportPanel.getContaminantsFastaDbTextField().setText("");
        }

    }

    /**
     * Validate the user input. Returns an empty list if no validation errors
     * were encountered.
     *
     * @return the list of validation messages
     */
    public List<String> validate() {
        List<String> validationMessages = new ArrayList<>();

        if (mqparFile == null) {
            validationMessages.add("Please select a parameter file.");
        }
        if (combinedDirectory == null) {
            validationMessages.add("Please select Combined Folder directory.");
        }
        if (primaryFastaDb == null) {
            validationMessages.add("Please select a primary FASTA file.");
        }
        if (contaminantsFastaDb == null) {
            validationMessages.add("Please select a contaminants FASTA file.");
        }
        additionalFastaDbBindingList.stream().forEach(additionalFastaDb -> {
            if (additionalFastaDb != null && additionalFastaDb.getHeaderParseRule() == null) {
                validationMessages.add("Please add header parse rule to additional FASTA file!");
            }
        });

        return validationMessages;
    }

    /**
     * Get the MaxQuantImport.
     *
     * @return the MaxQuantImport
     */
    public MaxQuantImport getDataImport() {
        EnumMap<FastaDbType, List<Long>> fastaDbIds = new EnumMap<>(FastaDbType.class);
        fastaDbIds.put(FastaDbType.PRIMARY, new ArrayList<>(Collections.singletonList(primaryFastaDb.getId())));
        fastaDbIds.put(FastaDbType.CONTAMINANTS, new ArrayList<>(Collections.singletonList(contaminantsFastaDb.getId())));
        List<Long> additionalFastaDbIDs = new ArrayList<>();
        additionalFastaDbBindingList.stream().forEach(additionalFastaDb -> {
            if (additionalFastaDb != null) {
                additionalFastaDbIDs.add(additionalFastaDb.getId());
            }
        });
        fastaDbIds.put(FastaDbType.ADDITIONAL, additionalFastaDbIDs);

        return new MaxQuantImport(mqparFile.toString(), combinedDirectory.toString(), fullCombinedDirectory.toString(), fastaDbIds, includeContaminants,
                includeUnidentifiedSpectra, storeRunsSequentially, selectedProteinGroupHeaders, analyticalRunsAdditionController.getSelectedQuantificationMethod());
    }

}

class AdditionalFastaDbListCellRenderer extends DefaultListCellRenderer {

    private static final long serialVersionUID = -1577134281957137748L;

    /**
     * Add only FASTA DB path to the list.
     *
     * @param list
     * @param value
     * @param index
     * @param isSelected
     * @param cellHasFocus
     * @return
     */
    @Override
    public java.awt.Component getListCellRendererComponent(final JList<?> list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        FastaDb fastaDb = (FastaDb) list.getModel().getElementAt(index);

        String labelText = "";

        if (fastaDb != null) {
            labelText = fastaDb.getFilePath();
        }

        setText(labelText);

        return this;
    }
}
