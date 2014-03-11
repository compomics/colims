package com.compomics.colims.client.controller;

import com.compomics.colims.client.view.MaxQuantDataImportPanel;
import com.compomics.colims.core.io.maxquant.MaxQuantDataImport;
import com.compomics.util.io.filefilters.FastaFileFilter;
import com.google.common.eventbus.EventBus;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("maxQuantDataImportController")
public class MaxQuantDataImportController implements Controllable {

    private static final Logger LOGGER = Logger.getLogger(MaxQuantDataImportController.class);
    //model
    private File maxQuantDirectory;
    private File fastaFile;
    //view
    private MaxQuantDataImportPanel maxQuantDataImportPanel;
    //parent controller
    @Autowired
    private AnalyticalRunSetupController analyticalRunSetupController;
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

        maxQuantDataImportPanel.getSelectMaxQuantDirectoryButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //in response to the button click, show open dialog 
                int returnVal = maxQuantDataImportPanel.getMaxQuantDirectoryChooser().showOpenDialog(maxQuantDataImportPanel);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    maxQuantDirectory = maxQuantDataImportPanel.getMaxQuantDirectoryChooser().getSelectedFile();

                    //show MaxQuant directory name in label
                    maxQuantDataImportPanel.getMaxQuantDirectoryLabel().setText(maxQuantDirectory.getName());
                }
            }
        });

        //init fasta file selection
        //disable select multiple files
        maxQuantDataImportPanel.getFastaFileChooser().setMultiSelectionEnabled(false);
        //set fasta file filter
        maxQuantDataImportPanel.getFastaFileChooser().setFileFilter(new FastaFileFilter());

        maxQuantDataImportPanel.getSelectFastaButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //in response to the button click, show open dialog 
                int returnVal = maxQuantDataImportPanel.getFastaFileChooser().showOpenDialog(maxQuantDataImportPanel);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    fastaFile = maxQuantDataImportPanel.getFastaFileChooser().getSelectedFile();

                    //show fasta file name in label
                    maxQuantDataImportPanel.getFastaFileLabel().setText(fastaFile.getName());
                }
            }
        });
    }

    @Override
    public void showView() {
        //reset the input fields
        maxQuantDataImportPanel.getMaxQuantDirectoryLabel().setText("");
        maxQuantDataImportPanel.getFastaFileLabel().setText("");
    }

    /**
     * Validate the user input. Returns an empty list if no validation errors
     * were encountered.
     *
     * @return the list of validation messages
     */
    public List<String> validate() {
        List<String> validationMessages = new ArrayList();

        if (maxQuantDirectory == null) {
            validationMessages.add("Please select a MaxQuant data files directory.");
        }
        if (fastaFile == null) {
            validationMessages.add("Please select a FASTA file.");
        }

        return validationMessages;
    }

    public MaxQuantDataImport getDataImport() {
        MaxQuantDataImport maxQuantDataImport = new MaxQuantDataImport(maxQuantDirectory, fastaFile);

        return maxQuantDataImport;
    }

}
