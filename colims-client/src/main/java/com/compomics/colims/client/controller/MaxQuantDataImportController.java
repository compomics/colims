package com.compomics.colims.client.controller;

import com.compomics.colims.client.controller.admin.FastaDbManagementController;
import com.compomics.colims.client.view.MaxQuantDataImportPanel;
import com.compomics.colims.core.io.MaxQuantImport;
import com.compomics.colims.model.FastaDb;
import com.compomics.colims.model.enums.FastaDbType;
import com.google.common.eventbus.EventBus;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.io.File;
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
    private Path maxQuantDirectory;
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

        //init MaxQuant directory file selection
        //disable select multiple files
        maxQuantDataImportPanel.getMaxQuantDirectoryChooser().setMultiSelectionEnabled(false);
        //set select directories only
        maxQuantDataImportPanel.getMaxQuantDirectoryChooser().setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        maxQuantDataImportPanel.getSelectMaxQuantDirectoryButton().addActionListener(e -> {
            //in response to the button click, show open dialog
            int returnVal = maxQuantDataImportPanel.getMaxQuantDirectoryChooser().showOpenDialog(maxQuantDataImportPanel);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                maxQuantDirectory = maxQuantDataImportPanel.getMaxQuantDirectoryChooser().getSelectedFile().toPath();

                //show MaxQuant directory name in label
                maxQuantDataImportPanel.getMaxQuantDirectoryTextField().setText(maxQuantDirectory.toString());
            }
        });

        maxQuantDataImportPanel.getSelectPrimaryFastaDbButton().addActionListener(e -> {
            fastaDbManagementController.showView();

            primaryFastaDb = fastaDbManagementController.getFastaDb();

            if (primaryFastaDb != null) {
                maxQuantDataImportPanel.getPrimaryFastaDbTextField().setText(primaryFastaDb.getFilePath());
            } else {
                maxQuantDataImportPanel.getPrimaryFastaDbTextField().setText("");
            }
        });

        maxQuantDataImportPanel.getSelectAddtionalFastaDbButton().addActionListener(e -> {
            fastaDbManagementController.showView();

            additionalFastaDb = fastaDbManagementController.getFastaDb();

            if (additionalFastaDb != null) {
                maxQuantDataImportPanel.getAdditionalFastaDbTextField().setText(additionalFastaDb.getFilePath());
            } else {
                maxQuantDataImportPanel.getAdditionalFastaDbTextField().setText("");
            }
        });

        maxQuantDataImportPanel.getSelectContaminantsFastaDbButton().addActionListener(e -> {
            fastaDbManagementController.showView();

            contaminantsFastaDb = fastaDbManagementController.getFastaDb();

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
        maxQuantDataImportPanel.getMaxQuantDirectoryTextField().setText("");
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

        if (maxQuantDirectory == null) {
            validationMessages.add("Please select a MaxQuant data files directory.");
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

        return new MaxQuantImport(maxQuantDirectory, fastaDbIds);
    }

}
