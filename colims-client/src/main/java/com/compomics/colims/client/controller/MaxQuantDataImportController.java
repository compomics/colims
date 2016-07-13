package com.compomics.colims.client.controller;

import com.compomics.colims.client.controller.admin.FastaDbManagementController;
import com.compomics.colims.client.view.MaxQuantDataImportPanel;
import com.compomics.colims.core.io.MaxQuantImport;
import com.compomics.colims.model.FastaDb;
import com.compomics.colims.model.enums.FastaDbType;
import com.compomics.util.io.filefilters.XmlFileFilter;
import com.google.common.eventbus.EventBus;
import java.awt.Dimension;
import java.io.File;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.nio.file.Path;
import java.util.ArrayList;
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
    private static final Logger LOGGER = Logger.getLogger(MaxQuantDataImportController.class);

    //model
    private File parameterFile;
    private Path combinedFolderDirectory;
    private FastaDb primaryFastaDb;
    private FastaDb additionalFastaDb;
    private FastaDb contaminantsFastaDb;
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

    @Override
    public void init() {
        //get view from parent controller
        maxQuantDataImportPanel = analyticalRunsAdditionController.getAnalyticalRunsAdditionDialog().getMaxQuantDataImportPanel();

        //register to event bus
        eventBus.register(this);

        //init parameter file directory selection
        //disable select multiple files
        maxQuantDataImportPanel.getParameterDirectoryChooser().setMultiSelectionEnabled(false);
        //set select directories only
        maxQuantDataImportPanel.getParameterDirectoryChooser().setFileFilter(new XmlFileFilter());

        maxQuantDataImportPanel.getSelectParameterDirectoryButton().addActionListener(e -> {
            //in response to the button click, show open dialog
            int returnVal = maxQuantDataImportPanel.getParameterDirectoryChooser().showOpenDialog(maxQuantDataImportPanel);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                parameterFile = maxQuantDataImportPanel.getParameterDirectoryChooser().getSelectedFile();

                //show MaxQuant directory name in label
                maxQuantDataImportPanel.getParameterDirectoryTextField().setText(parameterFile.getAbsolutePath());
            }
        });

        //init Combined Folder directory file selection
        //disable select multiple files
        maxQuantDataImportPanel.getCombinedFolderChooser().setMultiSelectionEnabled(false);
        //set select directories only
        maxQuantDataImportPanel.getCombinedFolderChooser().setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        maxQuantDataImportPanel.getSelectCombinedFolderButton().addActionListener(e -> {
            //in response to the button click, show open dialog
            int returnVal = maxQuantDataImportPanel.getCombinedFolderChooser().showOpenDialog(maxQuantDataImportPanel);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                combinedFolderDirectory = maxQuantDataImportPanel.getCombinedFolderChooser().getSelectedFile().toPath();
                // show combined folder directory name in label
                maxQuantDataImportPanel.getCombinedFolderDirectoryTextField().setText(combinedFolderDirectory.toString());
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
            additionalFastaDb = fastaDbManagementController.getSelectedFastaDb();

            if (additionalFastaDb != null) {
                maxQuantDataImportPanel.getAdditionalFastaDbTextField().setText(additionalFastaDb.getFilePath());
            } else {
                maxQuantDataImportPanel.getAdditionalFastaDbTextField().setText("");
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
    }

    @Override
    public void showView() {
        //reset the FASTA fields
        primaryFastaDb = null;
        additionalFastaDb = null;
        contaminantsFastaDb = null;
        //reset the input fields
        maxQuantDataImportPanel.getParameterDirectoryTextField().setText("");
        maxQuantDataImportPanel.getCombinedFolderDirectoryTextField().setText("");
        maxQuantDataImportPanel.getPrimaryFastaDbTextField().setText("");
        maxQuantDataImportPanel.getAdditionalFastaDbTextField().setText("");
        maxQuantDataImportPanel.getContaminantsFastaDbTextField().setText("");
    }

    /**
     * Validate the user input. Returns an empty list if no validation errors
     * were encountered.
     *
     * @return the list of validation messages
     */
    public List<String> validate() {
        List<String> validationMessages = new ArrayList<>();

        if (parameterFile == null) {
            validationMessages.add("Please select a parameter file.");
        }
        if (combinedFolderDirectory == null) {
            validationMessages.add("Please select Combined Folder directory.");
        }
        if (primaryFastaDb == null) {
            validationMessages.add("Please select a primary FASTA file.");
        }
        if (contaminantsFastaDb == null) {
            validationMessages.add("Please select a contaminants FASTA file.");
        }

        return validationMessages;
    }

    /**
     * Get the MaxQuantImport.
     *
     * @return the MaxQuantImport
     */
    public MaxQuantImport getDataImport() {
        EnumMap<FastaDbType, Long> fastaDbIds = new EnumMap<>(FastaDbType.class);
        fastaDbIds.put(FastaDbType.PRIMARY, primaryFastaDb.getId());
        fastaDbIds.put(FastaDbType.CONTAMINANTS, contaminantsFastaDb.getId());
        if (additionalFastaDb != null) {
            fastaDbIds.put(FastaDbType.ADDITIONAL, additionalFastaDb.getId());
        }

        return new MaxQuantImport(parameterFile.toPath(), combinedFolderDirectory, fastaDbIds);
    }

}
