package com.compomics.colims.client.controller;

import com.compomics.colims.client.controller.admin.FastaDbManagementController;
import com.compomics.colims.client.model.filter.CpsFileFilter;
import com.compomics.colims.client.view.PeptideShakerDataImportPanel;
import com.compomics.colims.core.io.peptideshaker.PeptideShakerImport;
import com.compomics.colims.model.FastaDb;
import com.compomics.util.io.filefilters.MgfFileFilter;
import com.google.common.eventbus.EventBus;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("peptideShakerDataImportController")
public class PeptideShakerDataImportController implements Controllable {

    private static final Logger LOGGER = Logger.getLogger(PeptideShakerDataImportController.class);
    //model
    private File cpsArchive;
    private FastaDb fastaDb;
    private final DefaultListModel<File> mgfFileListModel = new DefaultListModel();
    //view
    private PeptideShakerDataImportPanel peptideShakerDataImportPanel;
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
        peptideShakerDataImportPanel = analyticalRunSetupController.getAnalyticalRunSetupDialog().getPeptideShakerDataImportPanel();

        //register to event bus
        eventBus.register(this);

        //init cps file selection
        //disable select multiple files
        peptideShakerDataImportPanel.getCpsFileChooser().setMultiSelectionEnabled(false);
        //set cps file filter
        peptideShakerDataImportPanel.getCpsFileChooser().setFileFilter(new CpsFileFilter());

        peptideShakerDataImportPanel.getSelectCpsButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //in response to the button click, show open dialog 
                int returnVal = peptideShakerDataImportPanel.getCpsFileChooser().showOpenDialog(peptideShakerDataImportPanel);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    cpsArchive = peptideShakerDataImportPanel.getCpsFileChooser().getSelectedFile();

                    //show cps file name in label
                    peptideShakerDataImportPanel.getCpsFileLabel().setText(cpsArchive.getAbsolutePath());
                }
            }
        });

        peptideShakerDataImportPanel.getSelectFastaButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                fastaDbManagementController.showView();

                fastaDb = fastaDbManagementController.getFastaDb();

                if (fastaDb != null) {
                    peptideShakerDataImportPanel.getFastaDbLabel().setText(fastaDb.getFilePath());
                }
            }
        });

        //init mgf file(s) selection
        peptideShakerDataImportPanel.getMgfFileList().setModel(mgfFileListModel);
        peptideShakerDataImportPanel.getMgfFileList().setCellRenderer(new ListCellRenderer() {
            protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

            @Override
            public java.awt.Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index,
                        isSelected, cellHasFocus);

                renderer.setText(((File) value).getName());

                return renderer;
            }
        });

        //enable select multiple file
        peptideShakerDataImportPanel.getMgfFileChooser().setMultiSelectionEnabled(true);
        //set mgf file filter
        peptideShakerDataImportPanel.getMgfFileChooser().setFileFilter(new MgfFileFilter());

        peptideShakerDataImportPanel.getAddMgfButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //in response to the button click, show open dialog
                int returnVal = peptideShakerDataImportPanel.getMgfFileChooser().showOpenDialog(peptideShakerDataImportPanel);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    for (int i = 0; i < peptideShakerDataImportPanel.getMgfFileChooser().getSelectedFiles().length; i++) {
                        mgfFileListModel.add(i, peptideShakerDataImportPanel.getMgfFileChooser().getSelectedFiles()[i]);
                    }
                }
            }
        });

        peptideShakerDataImportPanel.getRemoveMgfButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int[] selectedIndices = peptideShakerDataImportPanel.getMgfFileList().getSelectedIndices();
                for (int i = 0; i < selectedIndices.length; i++) {
                    mgfFileListModel.remove(selectedIndices[i]);
                }
            }
        });
    }

    @Override
    public void showView() {
        //reset the input fields
        peptideShakerDataImportPanel.getCpsFileLabel().setText("");
        peptideShakerDataImportPanel.getFastaDbLabel().setText("");
        mgfFileListModel.clear();
    }

    /**
     * Validate the user input before unpacking the cps archive. Returns an
     * empty list if no validation errors were encountered.
     *
     * @return the list of validation messages
     */
    public List<String> validate() {
        List<String> validationMessages = new ArrayList();

        if (cpsArchive == null) {
            validationMessages.add("Please select a Peptide .cps file.");
        }
        if (fastaDb == null) {
            validationMessages.add("Please select a fasta DB.");
        }
        if (mgfFileListModel.isEmpty()) {
            validationMessages.add("Please select one or more MGF files.");
        }

        return validationMessages;
    }

    /**
     *
     * @return
     */
    public PeptideShakerImport getDataImport() {
        List<File> mgfFiles = new ArrayList<>();
        for (int i = 0; i < mgfFileListModel.size(); i++) {
            mgfFiles.add(mgfFileListModel.get(i));
        }

        PeptideShakerImport peptideShakerImport = new PeptideShakerImport(cpsArchive, fastaDb, mgfFiles);

        return peptideShakerImport;
    }

}
