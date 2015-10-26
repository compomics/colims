package com.compomics.colims.client.controller;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.AdvancedTableModel;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.GlazedListsSwing;
import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.factory.PsmPanelGenerator;
import com.compomics.colims.client.model.table.format.PeptideTableFormat;
import com.compomics.colims.client.model.table.format.ProteinGroupTableFormat;
import com.compomics.colims.client.model.table.format.ProteinPanelPsmTableFormat;
import com.compomics.colims.client.model.table.format.PsmTableFormat;
import com.compomics.colims.client.model.table.model.PeptideExportModel;
import com.compomics.colims.client.model.table.model.PeptideTableRow;
import com.compomics.colims.client.model.table.model.ProteinGroupTableModel;
import com.compomics.colims.client.model.table.model.ProteinPanelPsmTableModel;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.ProteinOverviewPanel;
import com.compomics.colims.client.view.SpectrumPopupDialog;
import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.service.PeptideService;
import com.compomics.colims.core.service.ProteinGroupService;
import com.compomics.colims.core.service.SpectrumService;
import com.compomics.colims.model.*;
import com.compomics.colims.repository.hibernate.model.PeptideDTO;
import com.compomics.colims.repository.hibernate.model.ProteinGroupDTO;
import com.compomics.util.gui.TableProperties;
import com.compomics.util.preferences.UtilitiesUserPreferences;
import com.google.common.eventbus.EventBus;
import no.uib.jsparklines.renderers.JSparklinesBarChartTableCellRenderer;
import no.uib.jsparklines.renderers.JSparklinesMultiIntervalChartTableCellRenderer;
import org.apache.log4j.Logger;
import org.jfree.chart.plot.PlotOrientation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by Iain on 19/06/2015.
 */
@Component("proteinOverviewController")
public class ProteinOverviewController implements Controllable {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(ProteinOverviewController.class);

    private ProteinGroupTableModel proteinGroupTableModel;
    private AdvancedTableModel peptideTableModel;
    private ProteinPanelPsmTableModel psmTableModel;
    private final EventList<ProteinGroupDTO> proteinGroupDTOs = new BasicEventList<>();
    private final EventList<PeptideTableRow> peptideTableRows = new BasicEventList<>();
    private final EventList<Peptide> psms = new BasicEventList<>();
    private DefaultEventSelectionModel<ProteinGroupDTO> proteinGroupSelectionModel;
    private DefaultEventSelectionModel<PeptideTableRow> peptideSelectionModel;
    private DefaultEventSelectionModel<Peptide> psmSelectionModel;
    private AnalyticalRun selectedAnalyticalRun;
    private double minimumRetentionTime;
    private double maximumRetentionTime;
    private double minimumMzRatio;
    private double maximumMzRatio;
    private int minimumCharge;
    private int maximumCharge;
    /**
     * The utilities user preferences.
     */
    private final UtilitiesUserPreferences utilitiesUserPreferences = new UtilitiesUserPreferences();
    //view
    private ProteinOverviewPanel proteinOverviewPanel;
    //child view
    private SpectrumPopupDialog psmPopupDialog;

    //parent controller
    @Autowired
    private MainController mainController;
    //services
    @Autowired
    private EventBus eventBus;
    @Autowired
    private PeptideService peptideService;
    @Autowired
    private SpectrumService spectrumService;
    @Autowired
    private ProteinGroupService proteinGroupService;
    @Autowired
    private PsmPanelGenerator psmPanelGenerator;

    //column filters
    private static final Pattern HTML_TAGS = Pattern.compile("<[a-z/]{1,5}>");

    /**
     * Get the panel associated with this controller.
     *
     * @return the protein overview panel
     */
    public ProteinOverviewPanel getProteinOverviewPanel() {
        return proteinOverviewPanel;
    }

    @Override
    public void init() {

        eventBus.register(this);

        //init views
        proteinOverviewPanel = new ProteinOverviewPanel(mainController.getMainFrame(), this);
        psmPopupDialog = new SpectrumPopupDialog(mainController.getMainFrame(), true);

        DefaultMutableTreeNode projectsNode = new DefaultMutableTreeNode("Projects");

        for (Project project : mainController.getProjects()) {
            projectsNode.add(buildProjectTree(project));
        }

        DefaultTreeModel treeModel = new DefaultTreeModel(projectsNode);
        proteinOverviewPanel.getProjectTree().setModel(treeModel);

        //init protein group table
        SortedList<ProteinGroupDTO> sortedProteinGroups = new SortedList<>(proteinGroupDTOs, null);

        proteinGroupTableModel = new ProteinGroupTableModel(sortedProteinGroups, new ProteinGroupTableFormat(), 20, ProteinGroupTableFormat.ID);
        proteinOverviewPanel.getProteinsTable().setModel(proteinGroupTableModel);
        proteinGroupSelectionModel = new DefaultEventSelectionModel<>(sortedProteinGroups);
        proteinGroupSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        proteinOverviewPanel.getProteinsTable().setSelectionModel(proteinGroupSelectionModel);

        //init peptide table
        SortedList<PeptideTableRow> sortedPeptides = new SortedList<>(peptideTableRows, null);

        peptideTableModel = GlazedListsSwing.eventTableModel(sortedPeptides, new PeptideTableFormat());
        proteinOverviewPanel.getPeptidesTable().setModel(peptideTableModel);
        peptideSelectionModel = new DefaultEventSelectionModel<>(sortedPeptides);
        peptideSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        proteinOverviewPanel.getPeptidesTable().setSelectionModel(peptideSelectionModel);

        //init PSM table
        SortedList<Peptide> sortedPsms = new SortedList<>(psms, null);

        psmTableModel = new ProteinPanelPsmTableModel(sortedPsms, new ProteinPanelPsmTableFormat());
        proteinOverviewPanel.getPsmTable().setModel(psmTableModel);
        psmSelectionModel = new DefaultEventSelectionModel<>(sortedPsms);
        psmSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        proteinOverviewPanel.getPsmTable().setSelectionModel(psmSelectionModel);

        proteinOverviewPanel.getProteinsTable().getColumnModel().getColumn(ProteinGroupTableFormat.ID).setPreferredWidth(70);
        proteinOverviewPanel.getProteinsTable().getColumnModel().getColumn(ProteinGroupTableFormat.ID).setMaxWidth(150);
        proteinOverviewPanel.getProteinsTable().getColumnModel().getColumn(ProteinGroupTableFormat.ID).setMinWidth(50);
        proteinOverviewPanel.getProteinsTable().getColumnModel().getColumn(ProteinGroupTableFormat.ACCESSION).setPreferredWidth(120);
        proteinOverviewPanel.getProteinsTable().getColumnModel().getColumn(ProteinGroupTableFormat.ACCESSION).setMaxWidth(150);
        proteinOverviewPanel.getProteinsTable().getColumnModel().getColumn(ProteinGroupTableFormat.ACCESSION).setMinWidth(50);
        proteinOverviewPanel.getProteinsTable().getColumnModel().getColumn(ProteinGroupTableFormat.SEQUENCE).setMinWidth(50);
        proteinOverviewPanel.getProteinsTable().getColumnModel().getColumn(ProteinGroupTableFormat.NUMBER_OF_DISTINCT_PEPTIDE_SEQUENCES).setPreferredWidth(145);
        proteinOverviewPanel.getProteinsTable().getColumnModel().getColumn(ProteinGroupTableFormat.NUMBER_OF_DISTINCT_PEPTIDE_SEQUENCES).setMaxWidth(200);
        proteinOverviewPanel.getProteinsTable().getColumnModel().getColumn(ProteinGroupTableFormat.NUMBER_OF_DISTINCT_PEPTIDE_SEQUENCES).setMinWidth(50);
        proteinOverviewPanel.getProteinsTable().getColumnModel().getColumn(ProteinGroupTableFormat.NUMBER_OF_SPECTRA).setPreferredWidth(60);
        proteinOverviewPanel.getProteinsTable().getColumnModel().getColumn(ProteinGroupTableFormat.NUMBER_OF_SPECTRA).setMaxWidth(100);
        proteinOverviewPanel.getProteinsTable().getColumnModel().getColumn(ProteinGroupTableFormat.NUMBER_OF_SPECTRA).setMinWidth(50);
        proteinOverviewPanel.getProteinsTable().getColumnModel().getColumn(ProteinGroupTableFormat.CONFIDENCE).setPreferredWidth(80);
        proteinOverviewPanel.getProteinsTable().getColumnModel().getColumn(ProteinGroupTableFormat.CONFIDENCE).setMaxWidth(100);
        proteinOverviewPanel.getProteinsTable().getColumnModel().getColumn(ProteinGroupTableFormat.CONFIDENCE).setMinWidth(50);

        proteinOverviewPanel.getPeptidesTable().getColumnModel().getColumn(PeptideTableFormat.SEQUENCE).setPreferredWidth(150);
        proteinOverviewPanel.getPeptidesTable().getColumnModel().getColumn(PeptideTableFormat.PROTEIN_INFERENCE).setPreferredWidth(50);
        proteinOverviewPanel.getPeptidesTable().getColumnModel().getColumn(PeptideTableFormat.PROTEIN_INFERENCE).setMaxWidth(50);
        proteinOverviewPanel.getPeptidesTable().getColumnModel().getColumn(PeptideTableFormat.PROTEIN_INFERENCE).setMinWidth(20);
        proteinOverviewPanel.getPeptidesTable().getColumnModel().getColumn(PeptideTableFormat.START).setPreferredWidth(150);
        proteinOverviewPanel.getPeptidesTable().getColumnModel().getColumn(PeptideTableFormat.NUMBER_OF_SPECTRA).setPreferredWidth(60);
        proteinOverviewPanel.getPeptidesTable().getColumnModel().getColumn(PeptideTableFormat.NUMBER_OF_SPECTRA).setMaxWidth(100);
        proteinOverviewPanel.getPeptidesTable().getColumnModel().getColumn(PeptideTableFormat.NUMBER_OF_SPECTRA).setMinWidth(50);
        proteinOverviewPanel.getPeptidesTable().getColumnModel().getColumn(PeptideTableFormat.CONFIDENCE).setPreferredWidth(80);
        proteinOverviewPanel.getPeptidesTable().getColumnModel().getColumn(PeptideTableFormat.CONFIDENCE).setMaxWidth(100);
        proteinOverviewPanel.getPeptidesTable().getColumnModel().getColumn(PeptideTableFormat.CONFIDENCE).setMinWidth(50);

        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.SPECTRUM_ID).setPreferredWidth(100);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.SPECTRUM_ID).setMaxWidth(100);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.SPECTRUM_ID).setMinWidth(100);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.CHARGE).setPreferredWidth(100);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.CHARGE).setMaxWidth(150);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.CHARGE).setMinWidth(50);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.PRECURSOR_MZRATIO).setPreferredWidth(100);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.PRECURSOR_MZRATIO).setMaxWidth(100);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.PRECURSOR_MZRATIO).setMinWidth(100);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.CHARGE).setPreferredWidth(200);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.PSM_CONFIDENCE).setPreferredWidth(100);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.PSM_CONFIDENCE).setMaxWidth(100);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.PSM_CONFIDENCE).setMinWidth(100);

        proteinOverviewPanel.getExportFileChooser().setApproveButtonText("Save");

        //Listeners
        proteinOverviewPanel.getProjectTree().addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) proteinOverviewPanel.getProjectTree().getLastSelectedPathComponent();

            if (node != null && node.isLeaf() && node.getUserObject() instanceof AnalyticalRun) {
                selectedAnalyticalRun = (AnalyticalRun) node.getUserObject();

                //load search settings for the run
                psmPanelGenerator.loadSettingsForRun(selectedAnalyticalRun);

                //get minimum and maximum projections for SparkLines rendering
                Object[] spectraProjections = spectrumService.getSpectraProjections(selectedAnalyticalRun);
                minimumRetentionTime = (double) spectraProjections[0];
                maximumRetentionTime = (double) spectraProjections[1];
                minimumMzRatio = (double) spectraProjections[2];
                maximumMzRatio = (double) spectraProjections[3];
                minimumCharge = (int) spectraProjections[4];
                maximumCharge = (int) spectraProjections[5];

                setPsmTableCellRenderers();

                proteinGroupTableModel.reset(selectedAnalyticalRun);
                updateProteinTable();

                // Set scrollpane to match row count (TODO: doesn't work!)
                proteinOverviewPanel.getProteinsScrollPane().setPreferredSize(new Dimension(
                        proteinOverviewPanel.getProteinsTable().getPreferredSize().width,
                        proteinOverviewPanel.getProteinsTable().getRowHeight() * proteinGroupTableModel.getPerPage() + 1
                ));

                minimumRetentionTime = spectrumService.getMinimumRetentionTime(selectedAnalyticalRun);
                maximumRetentionTime = spectrumService.getMinimumRetentionTime(selectedAnalyticalRun);
                minimumCharge = spectrumService.getMinimumCharge(selectedAnalyticalRun);
                maximumCharge = spectrumService.getMaximumCharge(selectedAnalyticalRun);
            }
        });

        proteinGroupSelectionModel.addListSelectionListener(lse -> {
            if (!lse.getValueIsAdjusting()) {
                if (!proteinGroupSelectionModel.getSelected().isEmpty()) {
                    ProteinGroupDTO selectedProteinGroupDTO = proteinGroupSelectionModel.getSelected().get(0);
                    //get the PeptideDTO instances for the selected protein group
                    List<PeptideDTO> peptideDTOs = peptideService.getPeptideDTOByProteinGroupId(selectedProteinGroupDTO.getId());

                    //map to PeptideTableRow objects
                    List<PeptideTableRow> mappedPeptideTableRows = mapPeptideDTOs(peptideDTOs);

                    setPeptideTableCellRenderers();

                    GlazedLists.replaceAll(peptideTableRows, mappedPeptideTableRows, false);
                } else {
                    GlazedLists.replaceAll(peptideTableRows, new ArrayList<>(), false);
                }
            }
        });

        peptideSelectionModel.addListSelectionListener(lse -> {
            if (!lse.getValueIsAdjusting()) {
                if (peptideSelectionModel.getSelected().isEmpty()) {
                    GlazedLists.replaceAll(psms, new ArrayList<>(), false);
                } else {
                    PeptideTableRow selectedPeptideTableRow = peptideSelectionModel.getSelected().get(0);
                    List<Peptide> selectedPsms = selectedPeptideTableRow.getPeptides();

                    setPsmTableCellRenderers();

                    GlazedLists.replaceAll(psms, selectedPsms, false);
                }
            }
        });

        proteinOverviewPanel.getPsmTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getClickCount() == 2 && !psmSelectionModel.getSelected().isEmpty()) {
                    showPsmPopDialog(psmSelectionModel.getSelected().get(0));
                }
            }
        });

        proteinOverviewPanel.getProteinsTable().getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                proteinGroupTableModel.updateSort(proteinOverviewPanel.getProteinsTable().columnAtPoint(e.getPoint()));
                proteinGroupTableModel.setPage(0);

                updateProteinTable();
            }
        });

        proteinOverviewPanel.getFirstPageProteins().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                proteinGroupTableModel.setPage(0);
                updateProteinTable();

                proteinOverviewPanel.getNextPageProteins().setEnabled(true);
                proteinOverviewPanel.getPrevPageProteins().setEnabled(false);
                proteinOverviewPanel.getFirstPageProteins().setEnabled(false);
                proteinOverviewPanel.getLastPageProteins().setEnabled(true);
            }
        });

        proteinOverviewPanel.getPrevPageProteins().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                proteinGroupTableModel.setPage(proteinGroupTableModel.getPage() - 1);
                updateProteinTable();

                proteinOverviewPanel.getNextPageProteins().setEnabled(true);
                proteinOverviewPanel.getLastPageProteins().setEnabled(true);

                if (proteinGroupTableModel.getPage() == 0) {
                    proteinOverviewPanel.getPrevPageProteins().setEnabled(false);
                    proteinOverviewPanel.getFirstPageProteins().setEnabled(false);
                }
            }
        });

        proteinOverviewPanel.getNextPageProteins().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                proteinGroupTableModel.setPage(proteinGroupTableModel.getPage() + 1);
                updateProteinTable();

                proteinOverviewPanel.getPrevPageProteins().setEnabled(true);
                proteinOverviewPanel.getFirstPageProteins().setEnabled(true);

                if (proteinGroupTableModel.isMaxPage()) {
                    proteinOverviewPanel.getNextPageProteins().setEnabled(false);
                    proteinOverviewPanel.getLastPageProteins().setEnabled(false);
                }
            }
        });

        proteinOverviewPanel.getLastPageProteins().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                proteinGroupTableModel.setPage(proteinGroupTableModel.getMaxPage());
                updateProteinTable();

                proteinOverviewPanel.getNextPageProteins().setEnabled(false);
                proteinOverviewPanel.getPrevPageProteins().setEnabled(true);
                proteinOverviewPanel.getFirstPageProteins().setEnabled(true);
                proteinOverviewPanel.getLastPageProteins().setEnabled(false);
            }
        });

        proteinOverviewPanel.getFilterProteins().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);

                String filterText = proteinOverviewPanel.getFilterProteins().getText();

                if (filterText.matches("^[a-zA-Z0-9]*$")) {
                    proteinGroupTableModel.setFilter(proteinOverviewPanel.getFilterProteins().getText());

                    updateProteinTable();
                }
            }
        });

        proteinOverviewPanel.getExportProteins().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                proteinOverviewPanel.getExportFileChooser().setDialogTitle("Export protein data");

                if (proteinOverviewPanel.getExportFileChooser().showOpenDialog(proteinOverviewPanel) == JFileChooser.APPROVE_OPTION) {
                    mainController.getMainFrame().setCursor(new Cursor(Cursor.WAIT_CURSOR));

                    EventList<ProteinGroupDTO> exportProteinGroupDTOs = new BasicEventList<>();
                    ProteinGroupTableModel exportModel = new ProteinGroupTableModel(new SortedList<>(exportProteinGroupDTOs, null), new ProteinGroupTableFormat(), 20, ProteinGroupTableFormat.ID);
                    exportModel.setPerPage(0);
                    GlazedLists.replaceAll(exportProteinGroupDTOs, exportModel.getRows(selectedAnalyticalRun), false);

                    exportTable(proteinOverviewPanel.getExportFileChooser().getSelectedFile(), exportModel);

                    mainController.getMainFrame().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            }
        });

        proteinOverviewPanel.getExportPeptides().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                proteinOverviewPanel.getExportFileChooser().setDialogTitle("Export peptide data");

                if (proteinOverviewPanel.getExportFileChooser().showOpenDialog(proteinOverviewPanel) == JFileChooser.APPROVE_OPTION) {
                    mainController.getMainFrame().setCursor(new Cursor(Cursor.WAIT_CURSOR));

                    Map<Integer, Pattern> columnFilter = new HashMap<>();
                    columnFilter.put(0, HTML_TAGS);

                    // need a new model... or table format?
                    // could potentially lose the filtering if its only on this model
                    // hmm
                    PeptideExportModel exportModel = new PeptideExportModel();
                    exportModel.setPeptideTableRows(sortedPeptides);

                    exportTable(proteinOverviewPanel.getExportFileChooser().getSelectedFile(), exportModel);

                    mainController.getMainFrame().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            }
        });

        proteinOverviewPanel.getExportPSMs().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                proteinOverviewPanel.getExportFileChooser().setDialogTitle("Export PSM data");

                if (proteinOverviewPanel.getExportFileChooser().showOpenDialog(proteinOverviewPanel) == JFileChooser.APPROVE_OPTION) {
                    mainController.getMainFrame().setCursor(new Cursor(Cursor.WAIT_CURSOR));

                    exportTable(proteinOverviewPanel.getExportFileChooser().getSelectedFile(), psmTableModel);

                    mainController.getMainFrame().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            }
        });
    }

    @Override
    public void showView() {
        //do nothing
    }

    /**
     * Build a node tree for a given project consisting of experiments, samples and runs.
     *
     * @param project A project to represent
     * @return A node of nodes
     */
    private DefaultMutableTreeNode buildProjectTree(Project project) {
        DefaultMutableTreeNode projectNode = new DefaultMutableTreeNode(project.getTitle());

        if (project.getExperiments().size() > 0) {
            for (Experiment experiment : project.getExperiments()) {
                DefaultMutableTreeNode experimentNode = new DefaultMutableTreeNode(experiment.getTitle());

                if (experiment.getSamples().size() > 0) {
                    for (Sample sample : experiment.getSamples()) {
                        DefaultMutableTreeNode sampleNode = new DefaultMutableTreeNode(sample.getName());

                        if (sample.getAnalyticalRuns().size() > 0) {
                            for (AnalyticalRun analyticalRun : sample.getAnalyticalRuns()) {
                                DefaultMutableTreeNode runNode = new DefaultMutableTreeNode(analyticalRun);

                                sampleNode.add(runNode);
                            }

                            experimentNode.add(sampleNode);
                        }
                    }

                    projectNode.add(experimentNode);
                }
            }
        }

        return projectNode;
    }

    /**
     * Update protein table contents and label.
     */
    private void updateProteinTable() {
        if (selectedAnalyticalRun != null) {
            GlazedLists.replaceAll(proteinGroupDTOs, proteinGroupTableModel.getRows(selectedAnalyticalRun), false);
            proteinOverviewPanel.getPageLabelProteins().setText(proteinGroupTableModel.getPageIndicator());
        } else {
            GlazedLists.replaceAll(proteinGroupDTOs, new ArrayList<>(), false);
            proteinOverviewPanel.getPageLabelProteins().setText("");
        }
    }

    /**
     * Show the given PSM in the spectrum popup dialog.
     *
     * @param peptide the Peptide instance
     */
    private void showPsmPopDialog(Peptide peptide) {
        try {
            JPanel spectrumJPanel = psmPopupDialog.getSpectrumJPanel();
            JPanel secondarySpectrumPlotsJPanel = psmPopupDialog.getSecondarySpectrumPlotsJPanel();

            psmPanelGenerator.addPsm(peptide, spectrumJPanel, secondarySpectrumPlotsJPanel);

            GuiUtils.centerDialogOnComponent(mainController.getMainFrame(), psmPopupDialog);
            psmPopupDialog.setVisible(true);
        } catch (MappingException | InterruptedException | SQLException | IOException | ClassNotFoundException e) {
            LOGGER.error(e, e.getCause());
            eventBus.post(new MessageEvent("Spectrum popup dialog problem", "The spectrum cannot be shown", JOptionPane.ERROR_MESSAGE));
        }
    }

    /**
     * Save the contents of a data table to a tab delimited file.
     *
     * @param filename   File to be saved as [filename].tsv
     * @param tableModel A table model to retrieve data from
     * @param <T>        Class extending TableModel
     */
    private <T extends TableModel> void exportTable(File filename, T tableModel) {
        exportTable(filename, tableModel, new HashMap<>());
    }

    /**
     * Save the contents of a data table to a tab delimited file.
     *
     * @param filename      File to be saved as [filename].tsv
     * @param tableModel    A table model to retrieve data from
     * @param columnFilters Patterns to match and filter values
     * @param <T>           Class extending TableModel
     */
    private <T extends TableModel> void exportTable(File filename, T tableModel, Map<Integer, Pattern> columnFilters) {
        try (FileWriter fileWriter = new FileWriter(filename + ".tsv")) {
            int columnCount = tableModel.getColumnCount();
            int rowCount = tableModel.getRowCount();
            StringBuilder line = new StringBuilder();

            // write column headers
            for (int i = 0; i < columnCount; ++i) {
                if (i > 0) {
                    line.append("\t");
                }

                line.append(tableModel.getColumnName(i));
            }

            fileWriter.write(line.append("\n").toString());

            // write rows
            for (int i = 0; i < rowCount; ++i) {
                line = new StringBuilder();

                for (int j = 0; j < columnCount; ++j) {
                    if (j > 0) {
                        line.append("\t");
                    }

                    if (columnFilters.get(j) == null) {
                        line.append(tableModel.getValueAt(i, j));
                    } else {
                        line.append(columnFilters.get(j).matcher(tableModel.getValueAt(i, j).toString()).replaceAll(""));
                    }

                    if (j == columnCount - 1 && i < rowCount - 1) {
                        line.append("\n");
                    }
                }

                fileWriter.write(line.toString());
            }

            //  show dialog
            JOptionPane.showMessageDialog(proteinOverviewPanel, "Data exported to " + filename + ".tsv");
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            eventBus.post(new MessageEvent("Export error", "Exporting tabular data failed: " + System.lineSeparator() + System.lineSeparator() + e.getMessage(), JOptionPane.ERROR_MESSAGE));
        }
    }

    /**
     * Map the list of PeptideDTO instances associated with the given protein group to a list of PeptideTableRow
     * instances.
     *
     * @param peptideDTOs the list of PeptideDTO instances
     * @return a list of PeptideTableRow instances
     */
    private List<PeptideTableRow> mapPeptideDTOs(List<PeptideDTO> peptideDTOs) {
        Map<PeptideDTO, PeptideTableRow> peptideTableRowMap = new HashMap<>();

        for (PeptideDTO peptideDTO : peptideDTOs) {
            if (peptideTableRowMap.containsKey(peptideDTO)) {
                PeptideTableRow peptideTableRow = peptideTableRowMap.get(peptideDTO);
                peptideTableRow.addPeptideDTO(peptideDTO);
            } else {
                peptideTableRowMap.put(peptideDTO, new PeptideTableRow(peptideDTO, proteinGroupSelectionModel.getSelected().get(0).getMainSequence()));
            }
        }

        return new ArrayList<>(peptideTableRowMap.values());
    }

    /**
     * Set SparkLines for the peptide table.
     */
    private void setPeptideTableCellRenderers() {
        String mainSequence = proteinGroupSelectionModel.getSelected().get(0).getMainSequence();

        proteinOverviewPanel.getPeptidesTable().getColumnModel().getColumn(PeptideTableFormat.START)
                .setCellRenderer(new JSparklinesMultiIntervalChartTableCellRenderer(
                        PlotOrientation.HORIZONTAL, (double) mainSequence.length(),
                        ((double) mainSequence.length()) / TableProperties.getLabelWidth(), utilitiesUserPreferences.getSparklineColor()));
        ((JSparklinesMultiIntervalChartTableCellRenderer) proteinOverviewPanel.getPeptidesTable().getColumnModel().getColumn(PeptideTableFormat.START).getCellRenderer()).showReferenceLine(true, 0.02, Color.BLACK);
        ((JSparklinesMultiIntervalChartTableCellRenderer) proteinOverviewPanel.getPeptidesTable().getColumnModel().getColumn(PeptideTableFormat.START).getCellRenderer()).showNumberAndChart(true, TableProperties.getLabelWidth() - 10);
    }

    /**
     * Set SparkLines for the PSM table.
     */
    private void setPsmTableCellRenderers() {
//        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.CHARGE)
//                .setCellRenderer(new JSparklinesBarChartTableCellRenderer(PlotOrientation.HORIZONTAL,
//                -peptideShakerGUI.getIdentificationParameters().getSearchParameters().getPrecursorAccuracy(), peptideShakerGUI.getIdentificationParameters().getSearchParameters().getPrecursorAccuracy(),
//                peptideShakerGUI.getSparklineColor(), peptideShakerGUI.getSparklineColor()));
//
//        ((JSparklinesBarChartTableCellRenderer) proteinOverviewPanel.getPsmTable()
//                .getColumnModel()
//                .getColumn(ProteinPanelPsmTableFormat.CHARGE)
//                .getCellRenderer())
//                .showNumberAndChart(true, TableProperties.getLabelWidth());
//
//        proteinOverviewPanel.getPsmTable()
//                .getColumnModel()
//                .getColumn(ProteinPanelPsmTableFormat.CHARGE)
//                .setCellRenderer(new JSparklinesBarChartTableCellRenderer(PlotOrientation.HORIZONTAL,
//                (double) ((PSMaps) peptideShakerGUI.getIdentification().getUrParam(new PSMaps())).getPsmSpecificMap().getMaxCharge(), peptideShakerGUI.getSparklineColor()));

        proteinOverviewPanel.getPsmTable()
                .getColumnModel()
                .getColumn(ProteinPanelPsmTableFormat.CHARGE)
                .setCellRenderer(
                        new JSparklinesBarChartTableCellRenderer(
                                PlotOrientation.HORIZONTAL,
                                (double) maximumCharge,
                                utilitiesUserPreferences.getSparklineColor()
                        )
                );

        ((JSparklinesBarChartTableCellRenderer) proteinOverviewPanel.getPsmTable()
                .getColumnModel()
                .getColumn(PsmTableFormat.PRECURSOR_CHARGE)
                .getCellRenderer())
                .showNumberAndChart(true, TableProperties.getLabelWidth() - 30);
    }

}
