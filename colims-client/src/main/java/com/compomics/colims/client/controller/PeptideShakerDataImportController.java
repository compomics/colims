package com.compomics.colims.client.controller;

import com.compomics.colims.client.controller.admin.FastaDbManagementController;
import com.compomics.colims.client.model.filter.CpsFileFilter;
import com.compomics.colims.client.view.PeptideShakerDataImportPanel;
import com.compomics.colims.core.io.PeptideShakerImport;
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
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * The PeptideShaker data import view controller.
 *
 * @author Niels Hulstaert
 */
@Component("peptideShakerDataImportController")
@Lazy
public class PeptideShakerDataImportController implements Controllable {

    /**
     * Logger instance.
     */
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
            public void actionPerformed(final ActionEvent e) {
                //in response to the button click, show open dialog
                int returnVal = peptideShakerDataImportPanel.getCpsFileChooser().showOpenDialog(peptideShakerDataImportPanel);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    cpsArchive = peptideShakerDataImportPanel.getCpsFileChooser().getSelectedFile();

                    //show cps file name in label
                    peptideShakerDataImportPanel.getCpsTextField().setText(cpsArchive.getAbsolutePath());
                }
            }
        });

        peptideShakerDataImportPanel.getSelectFastaButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                fastaDbManagementController.showView();

                fastaDb = fastaDbManagementController.getFastaDb();

                if (fastaDb != null) {
                    peptideShakerDataImportPanel.getFastaDbTextField().setText(fastaDb.getFilePath());
                }
            }
        });

        //init mgf file(s) selection
        peptideShakerDataImportPanel.getMgfFileList().setModel(mgfFileListModel);
        peptideShakerDataImportPanel.getMgfFileList().setCellRenderer(new ListCellRenderer() {
            protected final DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

            @Override
            public java.awt.Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus) {
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
            public void actionPerformed(final ActionEvent e) {
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
            public void actionPerformed(final ActionEvent e) {
                int[] selectedIndices = peptideShakerDataImportPanel.getMgfFileList().getSelectedIndices();
                for (int selectedIndex : selectedIndices) {
                    mgfFileListModel.remove(selectedIndex);
                }
            }
        });
    }

    @Override
    public void showView() {
        //reset the input fields
        peptideShakerDataImportPanel.getCpsTextField().setText("");
        peptideShakerDataImportPanel.getFastaDbTextField().setText("");
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
     * Get the PeptideShakerImport.
     *
     * @return the PeptideShakerImport
     */
    public PeptideShakerImport getDataImport() {
        List<File> mgfFiles = new ArrayList<>();
        for (int i = 0; i < mgfFileListModel.size(); i++) {
            mgfFiles.add(mgfFileListModel.get(i));
        }

        return new PeptideShakerImport(cpsArchive, fastaDb, mgfFiles);
    }

}
