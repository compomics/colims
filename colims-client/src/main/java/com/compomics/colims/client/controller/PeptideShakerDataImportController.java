package com.compomics.colims.client.controller;

import com.compomics.colims.client.controller.admin.FastaDbManagementController;
import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.model.filter.CpsFileFilter;
import com.compomics.colims.client.view.PeptideShakerDataImportPanel;
import com.compomics.colims.core.io.PeptideShakerImport;
import com.compomics.colims.core.service.FastaDbService;
import com.compomics.colims.core.util.PathUtils;
import com.compomics.colims.model.FastaDb;
import com.compomics.colims.model.enums.FastaDbType;
import com.compomics.util.io.filefilters.MgfFileFilter;
import com.google.common.eventbus.EventBus;
import org.apache.log4j.Logger;
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

    /**
     * The experiments location as provided in the client properties file.
     */
    @Value("${experiments.path}")
    private String experimentsPath = "";
    //model
    private Path cpsxArchive;
    private FastaDb fastaDb;
    private final DefaultListModel<Path> mgfFileListModel = new DefaultListModel();
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
    @PostConstruct
    public void init() {
        //get view from parent controller
        peptideShakerDataImportPanel = analyticalRunsAdditionController.getAnalyticalRunsAdditionDialog().getPeptideShakerDataImportPanel();

        //register to event bus
        eventBus.register(this);

        Path experimentsDirectory = Paths.get(experimentsPath);
        if (!Files.exists(experimentsDirectory)) {
            throw new IllegalArgumentException("The experiments directory defined in the client properties file " + experimentsPath + " doesn't exist.");
        }

        //init cps file selection
        //disable select multiple files
        peptideShakerDataImportPanel.getCpsFileChooser().setMultiSelectionEnabled(false);
        //set cps file filter
        peptideShakerDataImportPanel.getCpsFileChooser().setFileFilter(new CpsFileFilter());
        peptideShakerDataImportPanel.getCpsFileChooser().setCurrentDirectory(experimentsDirectory.toFile());

        peptideShakerDataImportPanel.getSelectCpsButton().addActionListener(e -> {
            //in response to the button click, show open dialog
            int returnVal = peptideShakerDataImportPanel.getCpsFileChooser().showOpenDialog(peptideShakerDataImportPanel);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    Path fullCpsxArchivePath = peptideShakerDataImportPanel.getCpsFileChooser().getSelectedFile().toPath();
                    cpsxArchive = PathUtils.getRelativeChildPath(experimentsDirectory, fullCpsxArchivePath);
                    //show cps file name in label
                    peptideShakerDataImportPanel.getCpsTextField().setText(cpsxArchive.toString());
                } catch (IllegalArgumentException ex) {
                    MessageEvent messageEvent = new MessageEvent("Invalid cpsx file location", "The cpsx file location doesn't contain the experiments directory as defined in the properties file.", JOptionPane.WARNING_MESSAGE);
                    eventBus.post(messageEvent);
                }
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
                renderer.setText(((Path) value).getFileName().toString());

                return renderer;
            }
        });

        //enable select multiple file
        peptideShakerDataImportPanel.getMgfFileChooser().setMultiSelectionEnabled(true);
        //set mgf file filter
        peptideShakerDataImportPanel.getMgfFileChooser().setFileFilter(new MgfFileFilter());

        peptideShakerDataImportPanel.getMgfFileChooser().setCurrentDirectory(experimentsDirectory.toFile());

        peptideShakerDataImportPanel.getAddMgfButton().addActionListener(e -> {
            //in response to the button click, show open dialog
            int returnVal = peptideShakerDataImportPanel.getMgfFileChooser().showOpenDialog(peptideShakerDataImportPanel);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                for (int i = 0; i < peptideShakerDataImportPanel.getMgfFileChooser().getSelectedFiles().length; i++) {
                    try {
                        Path fullMgfPath = peptideShakerDataImportPanel.getMgfFileChooser().getSelectedFiles()[i].toPath();
                        Path mgfPath = PathUtils.getRelativeChildPath(experimentsDirectory, fullMgfPath);
                        mgfFileListModel.add(i, mgfPath);
                    } catch (IllegalArgumentException ex) {
                        MessageEvent messageEvent = new MessageEvent("Invalid MGF file location", "The MGF file location doesn't contain the experiments directory as defined in the properties file.", JOptionPane.WARNING_MESSAGE);
                        eventBus.post(messageEvent);
                    }
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

    public void showEditView(PeptideShakerImport peptideShakerImport) {
        showView();
        if (peptideShakerImport.getFastaDbIds().get(FastaDbType.PRIMARY) != null) {
            fastaDb = fastaDbService.findById(peptideShakerImport.getFastaDbIds().get(FastaDbType.PRIMARY).get(0));
            peptideShakerDataImportPanel.getFastaDbTextField().setText(fastaDb.getFilePath());
        } else {
            fastaDb = null;
            peptideShakerDataImportPanel.getFastaDbTextField().setText("");
        }
        peptideShakerDataImportPanel.getCpsTextField().setText(peptideShakerImport.getPeptideShakerCpsxArchive().toString());

        peptideShakerImport.getMgfFiles().forEach(mgfFileListModel::addElement);
    }

    /**
     * Validate the user input before unpacking the cps archive. Returns an
     * empty list if no validation errors were encountered.
     *
     * @return the list of validation messages
     */
    public List<String> validate() {
        List<String> validationMessages = new ArrayList();

        if (cpsxArchive == null) {
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
        List<Path> mgfFiles = new ArrayList<>();
        for (int i = 0; i < mgfFileListModel.size(); i++) {
            mgfFiles.add(mgfFileListModel.get(i));
        }

        EnumMap<FastaDbType, List<Long>> fastaDbIds = new EnumMap<>(FastaDbType.class);
        fastaDbIds.put(FastaDbType.PRIMARY, new ArrayList<>(Collections.singletonList(fastaDb.getId())));

        return new PeptideShakerImport(cpsxArchive, fastaDbIds, mgfFiles);
    }

}
