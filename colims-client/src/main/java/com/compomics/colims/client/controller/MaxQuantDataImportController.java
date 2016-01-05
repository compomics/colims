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
    private File maxQuantDirectory;
    private FastaDb fastaDb;
    //view
    private MaxQuantDataImportPanel maxQuantDataImportPanel;
    //parent controller
    @Autowired
    private AnalyticalRunSetupController analyticalRunSetupController;
    //child controller
    @Autowired
    private FastaDbManagementController fastaDbManagementController;
    //services
    @Autowired
    private EventBus eventBus;

    @Override
    public void init() {
        //get view from parent controller
        maxQuantDataImportPanel = analyticalRunSetupController.getAnalyticalRunSetupDialog().getMaxQuantDataImportPanel();

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
                maxQuantDirectory = maxQuantDataImportPanel.getMaxQuantDirectoryChooser().getSelectedFile();

                //show MaxQuant directory name in label
                maxQuantDataImportPanel.getMaxQuantDirectoryTextField().setText(maxQuantDirectory.getAbsolutePath());
            }
        });

        maxQuantDataImportPanel.getSelectFastaButton().addActionListener(e -> {
            fastaDbManagementController.showView();

            fastaDb = fastaDbManagementController.getFastaDb();

            maxQuantDataImportPanel.getFastaDbTextField().setText(fastaDb.getFilePath());
        });
    }

    @Override
    public void showView() {
        //reset the input fields
        maxQuantDataImportPanel.getMaxQuantDirectoryTextField().setText("");
        maxQuantDataImportPanel.getFastaDbTextField().setText("");
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
        if (fastaDb == null) {
            validationMessages.add("Please select a FASTA file.");
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
        fastaDbIds.put(FastaDbType.PRIMARY, fastaDb.getId());

        return new MaxQuantImport(maxQuantDirectory, fastaDbIds);
    }

}
