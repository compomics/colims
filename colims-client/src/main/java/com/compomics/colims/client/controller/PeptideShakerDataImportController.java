package com.compomics.colims.client.controller;

import com.compomics.colims.client.controller.admin.FastaDbManagementController;
import com.compomics.colims.client.model.filter.CpsFileFilter;
import com.compomics.colims.client.view.PeptideShakerDataImportPanel;
import com.compomics.colims.core.io.PeptideShakerImport;
import com.compomics.colims.core.service.FastaDbService;
import com.compomics.colims.model.FastaDb;
import com.compomics.colims.model.enums.FastaDbType;
import com.compomics.util.io.filefilters.MgfFileFilter;
import com.google.common.eventbus.EventBus;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.io.File;
import java.util.*;

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
    public void init() {
        //get view from parent controller
        peptideShakerDataImportPanel = analyticalRunsAdditionController.getAnalyticalRunsAdditionDialog().getPeptideShakerDataImportPanel();

        //register to event bus
        eventBus.register(this);

        //init cps file selection
        //disable select multiple files
        peptideShakerDataImportPanel.getCpsFileChooser().setMultiSelectionEnabled(false);
        //set cps file filter
        peptideShakerDataImportPanel.getCpsFileChooser().setFileFilter(new CpsFileFilter());

        peptideShakerDataImportPanel.getSelectCpsButton().addActionListener(e -> {
            //in response to the button click, show open dialog
            int returnVal = peptideShakerDataImportPanel.getCpsFileChooser().showOpenDialog(peptideShakerDataImportPanel);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                cpsArchive = peptideShakerDataImportPanel.getCpsFileChooser().getSelectedFile();

                //show cps file name in label
                peptideShakerDataImportPanel.getCpsTextField().setText(cpsArchive.getAbsolutePath());
            }
        });

        peptideShakerDataImportPanel.getSelectFastaButton().addActionListener(e -> {
            fastaDbManagementController.showView();

            fastaDb = fastaDbManagementController.getSelectedFastaDb();

            if (fastaDb != null) {
                peptideShakerDataImportPanel.getFastaDbTextField().setText(fastaDb.getFilePath());
            } else {
                peptideShakerDataImportPanel.getFastaDbTextField().setText("");
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

        peptideShakerDataImportPanel.getAddMgfButton().addActionListener(e -> {
            //in response to the button click, show open dialog
            int returnVal = peptideShakerDataImportPanel.getMgfFileChooser().showOpenDialog(peptideShakerDataImportPanel);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                for (int i = 0; i < peptideShakerDataImportPanel.getMgfFileChooser().getSelectedFiles().length; i++) {
                    mgfFileListModel.add(i, peptideShakerDataImportPanel.getMgfFileChooser().getSelectedFiles()[i]);
                }
            }
        });

        peptideShakerDataImportPanel.getRemoveMgfButton().addActionListener(e -> {
            int[] selectedIndices = peptideShakerDataImportPanel.getMgfFileList().getSelectedIndices();
            for (int selectedIndex : selectedIndices) {
                mgfFileListModel.remove(selectedIndex);
            }
        });
    }

    @Override
    public void showView() {
        //reset the FASTA field
        fastaDb = null;
        //reset the input fields
        peptideShakerDataImportPanel.getCpsTextField().setText("");
        peptideShakerDataImportPanel.getFastaDbTextField().setText("");
        mgfFileListModel.clear();
    }

    public void showEditView(PeptideShakerImport peptideShakerImport){
        showView();
        if(peptideShakerImport.getFastaDbIds().get(FastaDbType.PRIMARY) != null){
            fastaDb = fastaDbService.findById(peptideShakerImport.getFastaDbIds().get(FastaDbType.PRIMARY).get(0));
            peptideShakerDataImportPanel.getFastaDbTextField().setText(fastaDb.getFilePath());
        }else{
            fastaDb = null;
            peptideShakerDataImportPanel.getFastaDbTextField().setText("");
        }
        peptideShakerDataImportPanel.getCpsTextField().setText(peptideShakerImport.getPeptideShakerCpsArchive().getPath());
        
        peptideShakerImport.getMgfFiles().forEach(mgfFile -> mgfFileListModel.addElement(mgfFile)); 
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

        EnumMap<FastaDbType, List<Long>> fastaDbIds = new EnumMap<>(FastaDbType.class);
        fastaDbIds.put(FastaDbType.PRIMARY, new ArrayList<>(Collections.singletonList(fastaDb.getId())));

        return new PeptideShakerImport(cpsArchive, fastaDbIds, mgfFiles);
    }

}
