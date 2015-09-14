package com.compomics.colims.client.controller;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.AdvancedTableModel;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.GlazedListsSwing;
import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.model.PeptideTableRow;
import com.compomics.colims.client.model.ProteinPanelPsmTableModel;
import com.compomics.colims.client.model.ProteinGroupTableModel;
import com.compomics.colims.client.model.tableformat.PeptideTableFormat;
import com.compomics.colims.client.model.tableformat.ProteinPanelPsmTableFormat;
import com.compomics.colims.client.model.tableformat.ProteinGroupTableFormat;
import com.compomics.colims.client.model.tableformat.PsmTableFormat;
import com.compomics.colims.client.view.ProteinOverviewPanel;
import com.compomics.colims.core.service.PeptideService;
import com.compomics.colims.core.service.SpectrumService;
import com.compomics.colims.model.*;
import com.compomics.util.preferences.UtilitiesUserPreferences;
import com.google.common.eventbus.EventBus;
import no.uib.jsparklines.renderers.JSparklinesIntervalChartTableCellRenderer;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Iain on 19/06/2015.
 */
@Component("proteinOverviewController")
public class ProteinOverviewController implements Controllable {

    private static final Logger LOGGER = Logger.getLogger(ProteinOverviewController.class);

    private ProteinGroupTableModel proteinGroupTableModel;
    private AdvancedTableModel peptideTableModel;
    private ProteinPanelPsmTableModel psmTableModel;
    private final EventList<ProteinGroup> proteinGroups = new BasicEventList<>();
    private final EventList<PeptideTableRow> peptides = new BasicEventList<>();
    private final EventList<Spectrum> spectra = new BasicEventList<>();
    private DefaultEventSelectionModel<ProteinGroup> proteinGroupSelectionModel;
    private DefaultEventSelectionModel<PeptideTableRow> peptideSelectionModel;
    private DefaultEventSelectionModel<Spectrum> spectrumSelectionModel;
    private AnalyticalRun selectedAnalyticalRun;
    private List<Long> spectrumIdsForRun = new ArrayList<>();
    private double minimumRetentionTime;
    private double maximumRetentionTime;
    private double minimumCharge;
    private double maximumCharge;
    //view
    private ProteinOverviewPanel proteinOverviewPanel;

    //parent controller
    @Autowired
    private MainController mainController;
    //child controller
    @Autowired
    private SpectrumPopupController spectrumPopupController;
    //services
    @Autowired
    private EventBus eventBus;
    @Autowired
    private PeptideService peptideService;
    @Autowired
    private SpectrumService spectrumService;

    @Override
    public void init() {
        eventBus.register(this);
        proteinOverviewPanel = new ProteinOverviewPanel(mainController.getMainFrame(), this);

        spectrumPopupController.init();

        DefaultMutableTreeNode projectsNode = new DefaultMutableTreeNode("Projects");

        for (Project project : mainController.getProjects()) {
            projectsNode.add(buildProjectTree(project));
        }

        DefaultTreeModel treeModel = new DefaultTreeModel(projectsNode);
        proteinOverviewPanel.getProjectTree().setModel(treeModel);

        // init proteinGroups table
        SortedList<ProteinGroup> sortedProteinGroups = new SortedList<>(proteinGroups, null);

        proteinGroupTableModel = new ProteinGroupTableModel(sortedProteinGroups, new ProteinGroupTableFormat());
        proteinOverviewPanel.getProteinsTable().setModel(proteinGroupTableModel);
        proteinGroupSelectionModel = new DefaultEventSelectionModel<>(sortedProteinGroups);
        proteinGroupSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        proteinOverviewPanel.getProteinsTable().setSelectionModel(proteinGroupSelectionModel);

        // init peptides table
        SortedList<PeptideTableRow> sortedPeptides = new SortedList<>(peptides, null);

        peptideTableModel = GlazedListsSwing.eventTableModel(sortedPeptides, new PeptideTableFormat());
        proteinOverviewPanel.getPeptidesTable().setModel(peptideTableModel);
        peptideSelectionModel = new DefaultEventSelectionModel<>(sortedPeptides);
        peptideSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        proteinOverviewPanel.getPeptidesTable().setSelectionModel(peptideSelectionModel);

        // init PSM table
        SortedList<Spectrum> sortedSpectra = new SortedList<>(spectra, null);

        psmTableModel = new ProteinPanelPsmTableModel(sortedSpectra, new ProteinPanelPsmTableFormat());
        proteinOverviewPanel.getPsmTable().setModel(psmTableModel);
        spectrumSelectionModel = new DefaultEventSelectionModel<>(sortedSpectra);
        spectrumSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        proteinOverviewPanel.getPsmTable().setSelectionModel(spectrumSelectionModel);

        proteinOverviewPanel.getProteinsTable().getColumnModel().getColumn(ProteinGroupTableFormat.ID).setPreferredWidth(40);
        proteinOverviewPanel.getProteinsTable().getColumnModel().getColumn(ProteinGroupTableFormat.ID).setMaxWidth(40);
        proteinOverviewPanel.getProteinsTable().getColumnModel().getColumn(ProteinGroupTableFormat.ID).setMinWidth(40);
        proteinOverviewPanel.getProteinsTable().getColumnModel().getColumn(ProteinGroupTableFormat.PEP).setPreferredWidth(150);
        proteinOverviewPanel.getProteinsTable().getColumnModel().getColumn(ProteinGroupTableFormat.PEP).setMaxWidth(150);
        proteinOverviewPanel.getProteinsTable().getColumnModel().getColumn(ProteinGroupTableFormat.PEP).setMinWidth(150);
        proteinOverviewPanel.getProteinsTable().getColumnModel().getColumn(ProteinGroupTableFormat.PROBABILITY).setPreferredWidth(100);
        proteinOverviewPanel.getProteinsTable().getColumnModel().getColumn(ProteinGroupTableFormat.PROBABILITY).setMaxWidth(100);
        proteinOverviewPanel.getProteinsTable().getColumnModel().getColumn(ProteinGroupTableFormat.PROBABILITY).setMinWidth(100);

        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.SPECTRUM_ID).setPreferredWidth(40);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.SPECTRUM_ID).setMaxWidth(40);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.SPECTRUM_ID).setMinWidth(40);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.PRECURSOR_CHARGE).setPreferredWidth(10);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.PRECURSOR_MZRATIO).setPreferredWidth(50);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.PRECURSOR_INTENSITY).setPreferredWidth(50);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.RETENTION_TIME).setPreferredWidth(50);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.PSM_CONFIDENCE).setPreferredWidth(50);

        proteinOverviewPanel.getExportFileChooser().setApproveButtonText("Save");

        //  Listeners

        proteinOverviewPanel.getProjectTree().addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) proteinOverviewPanel.getProjectTree().getLastSelectedPathComponent();

            if (node != null && node.isLeaf() && node.getUserObject() instanceof AnalyticalRun) {
                selectedAnalyticalRun = (AnalyticalRun) node.getUserObject();

                proteinGroupTableModel.reset(selectedAnalyticalRun);
                updateProteinTable();

                spectrumIdsForRun = spectrumService.getSpectraIdsForRun(selectedAnalyticalRun);

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
                if (proteinGroupSelectionModel.getSelected().isEmpty()) {
                    GlazedLists.replaceAll(peptides, new ArrayList<>(), false);
                } else {
                    List<Peptide> peptidesForSelectedProtein = peptideService.getPeptidesForProteinGroup(proteinGroupSelectionModel.getSelected().get(0), spectrumIdsForRun);

                    List<PeptideTableRow> peptideTableRows = new ArrayList<>();
                    Map<String, Integer> sequencesRowIndices = new HashMap<>();

                    for (Peptide peptide : peptidesForSelectedProtein) {
                        if (sequencesRowIndices.containsKey(peptide.getSequence())) {
                            peptideTableRows.get(sequencesRowIndices.get(peptide.getSequence())).addPeptide(peptide);
                        } else {
                            peptideTableRows.add(new PeptideTableRow(peptide));
                            sequencesRowIndices.put(peptide.getSequence(), sequencesRowIndices.size());
                        }
                    }

                    for (PeptideTableRow peptideTableRow : peptideTableRows) {
                        peptideTableRow.getPeptideHasModifications().addAll(peptideService.getModificationsForMultiplePeptides(peptideTableRow.getPeptides()));
                    }

                    GlazedLists.replaceAll(peptides, peptideTableRows, false);
                }
            }
        });

        peptideSelectionModel.addListSelectionListener(lse -> {
            if (!lse.getValueIsAdjusting()) {
                if (peptideSelectionModel.getSelected().isEmpty()) {
                    GlazedLists.replaceAll(spectra, new ArrayList<>(), false);
                } else {
                    List<Peptide> peptides = peptideService.getPeptidesFromSequence(peptideSelectionModel.getSelected().get(0).getSequence(), spectrumIdsForRun);
                    List<Spectrum> selectedSpectra = peptides.stream().map(Peptide::getSpectrum).collect(Collectors.toList());

                    setPsmTableCellRenderers();

                    GlazedLists.replaceAll(spectra, selectedSpectra, false);
                }
            }
        });

        proteinOverviewPanel.getPsmTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getClickCount() == 2 && !spectrumSelectionModel.getSelected().isEmpty()) {
                    spectrumPopupController.updateView(spectrumSelectionModel.getSelected().get(0));
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

                    EventList<Protein> exportProteins = new BasicEventList<>();
                    ProteinGroupTableModel exportModel = new ProteinGroupTableModel(new SortedList<>(exportProteins, null), new ProteinGroupTableFormat());
                    exportModel.setPerPage(0);
                    GlazedLists.replaceAll(exportProteins, exportModel.getRows(selectedAnalyticalRun), false);

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

                    exportTable(proteinOverviewPanel.getExportFileChooser().getSelectedFile(), peptideTableModel);

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
    }

    /**
     * Get the panel associated with this controller
     *
     * @return Protein overview panel
     */
    public ProteinOverviewPanel getProteinOverviewPanel() {
        return proteinOverviewPanel;
    }

    /**
     * Build a node tree for a given project consisting of experiments, samples and runs
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
     * Update protein table contents and label
     */
    private void updateProteinTable() {
        if (selectedAnalyticalRun != null) {
            GlazedLists.replaceAll(proteinGroups, proteinGroupTableModel.getRows(selectedAnalyticalRun), false);
            proteinOverviewPanel.getPageLabelProteins().setText(proteinGroupTableModel.getPageIndicator());
        } else {
            GlazedLists.replaceAll(proteinGroups, new ArrayList<>(), false);
            proteinOverviewPanel.getPageLabelProteins().setText("");
        }
    }

    /**
     * Save the contents of a data table to a tab delimited file
     *
     * @param filename      File to be saved as [filename].tsv
     * @param tableModel    A table model to retrieve data from
     * @param <T>           Class extending TableModel
     */
    private <T extends TableModel> void exportTable(File filename, T tableModel) {
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

                    line.append(tableModel.getValueAt(i, j));

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
            eventBus.post(new MessageEvent("Export error", "Exporting tabular data failed: "  + System.lineSeparator() + System.lineSeparator() + e.getMessage(), JOptionPane.ERROR_MESSAGE));
        }
    }

    /**
     * Set sparklines for the PSM table
     */
    private void setPsmTableCellRenderers() {
        Color sparklineColor = new UtilitiesUserPreferences().getSparklineColor();
        int labelWidth = 55;

        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.RETENTION_TIME)
            .setCellRenderer(
                new JSparklinesIntervalChartTableCellRenderer(PlotOrientation.HORIZONTAL,
                    minimumRetentionTime,
                    maximumRetentionTime,
                    50d,
                    sparklineColor,
                    sparklineColor
                )
            );

        ((JSparklinesIntervalChartTableCellRenderer) proteinOverviewPanel.getPsmTable()
            .getColumnModel()
            .getColumn(PsmTableFormat.RETENTION_TIME)
            .getCellRenderer())
            .showNumberAndChart(true, labelWidth);

        ((JSparklinesIntervalChartTableCellRenderer) proteinOverviewPanel.getPsmTable()
            .getColumnModel()
            .getColumn(PsmTableFormat.RETENTION_TIME)
            .getCellRenderer())
            .showReferenceLine(true, 0.02, java.awt.Color.BLACK);

        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.PRECURSOR_CHARGE)
            .setCellRenderer(
                new JSparklinesIntervalChartTableCellRenderer(
                    PlotOrientation.HORIZONTAL,
                    minimumCharge,
                    maximumCharge,
                    50d,
                    sparklineColor,
                    sparklineColor
                )
            );

        ((JSparklinesIntervalChartTableCellRenderer) proteinOverviewPanel.getPsmTable()
            .getColumnModel()
            .getColumn(PsmTableFormat.PRECURSOR_CHARGE)
            .getCellRenderer())
            .showNumberAndChart(true, labelWidth);

        ((JSparklinesIntervalChartTableCellRenderer) proteinOverviewPanel.getPsmTable()
            .getColumnModel()
            .getColumn(PsmTableFormat.PRECURSOR_CHARGE)
            .getCellRenderer())
            .showReferenceLine(true, 0.02, java.awt.Color.BLACK);
    }
}
