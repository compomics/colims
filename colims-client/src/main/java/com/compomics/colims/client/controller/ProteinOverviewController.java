package com.compomics.colims.client.controller;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.AdvancedTableModel;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.GlazedListsSwing;
import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.factory.SpectrumPanelGenerator;
import com.compomics.colims.client.model.PeptideExportModel;
import com.compomics.colims.client.model.PeptideTableRow;
import com.compomics.colims.client.model.ProteinGroupTableModel;
import com.compomics.colims.client.model.ProteinPanelPsmTableModel;
import com.compomics.colims.client.model.tableformat.PeptideTableFormat;
import com.compomics.colims.client.model.tableformat.ProteinGroupTableFormat;
import com.compomics.colims.client.model.tableformat.ProteinPanelPsmTableFormat;
import com.compomics.colims.client.model.tableformat.PsmTableFormat;
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
    private final EventList<PeptideTableRow> peptides = new BasicEventList<>();
    private final EventList<Spectrum> spectra = new BasicEventList<>();
    private DefaultEventSelectionModel<ProteinGroupDTO> proteinGroupDTOSelectionModel;
    private DefaultEventSelectionModel<PeptideTableRow> peptideSelectionModel;
    private DefaultEventSelectionModel<Spectrum> spectrumSelectionModel;
    private AnalyticalRun selectedAnalyticalRun;
    private double minimumRetentionTime;
    private double maximumRetentionTime;
    private double minimumCharge;
    private double maximumCharge;
    //view
    private ProteinOverviewPanel proteinOverviewPanel;
    //child view
    private SpectrumPopupDialog spectrumPopupDialog;

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
    private SpectrumPanelGenerator spectrumPanelGenerator;

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
        spectrumPopupDialog = new SpectrumPopupDialog(mainController.getMainFrame(), true);

        DefaultMutableTreeNode projectsNode = new DefaultMutableTreeNode("Projects");

        for (Project project : mainController.getProjects()) {
            projectsNode.add(buildProjectTree(project));
        }

        DefaultTreeModel treeModel = new DefaultTreeModel(projectsNode);
        proteinOverviewPanel.getProjectTree().setModel(treeModel);

        //init proteinGroupForRuns table
        SortedList<ProteinGroupDTO> sortedProteinGroups = new SortedList<>(proteinGroupDTOs, null);

        proteinGroupTableModel = new ProteinGroupTableModel(sortedProteinGroups, new ProteinGroupTableFormat());
        proteinOverviewPanel.getProteinsTable().setModel(proteinGroupTableModel);
        proteinGroupDTOSelectionModel = new DefaultEventSelectionModel<>(sortedProteinGroups);
        proteinGroupDTOSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        proteinOverviewPanel.getProteinsTable().setSelectionModel(proteinGroupDTOSelectionModel);

        //init peptides table
        SortedList<PeptideTableRow> sortedPeptides = new SortedList<>(peptides, null);

        peptideTableModel = GlazedListsSwing.eventTableModel(sortedPeptides, new PeptideTableFormat());
        proteinOverviewPanel.getPeptidesTable().setModel(peptideTableModel);
        peptideSelectionModel = new DefaultEventSelectionModel<>(sortedPeptides);
        peptideSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        proteinOverviewPanel.getPeptidesTable().setSelectionModel(peptideSelectionModel);

        //init PSM table
        SortedList<Spectrum> sortedSpectra = new SortedList<>(spectra, null);

        psmTableModel = new ProteinPanelPsmTableModel(sortedSpectra, new ProteinPanelPsmTableFormat());
        proteinOverviewPanel.getPsmTable().setModel(psmTableModel);
        spectrumSelectionModel = new DefaultEventSelectionModel<>(sortedSpectra);
        spectrumSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        proteinOverviewPanel.getPsmTable().setSelectionModel(spectrumSelectionModel);

        proteinOverviewPanel.getProteinsTable().getColumnModel().getColumn(ProteinGroupTableFormat.ID).setPreferredWidth(80);
        proteinOverviewPanel.getProteinsTable().getColumnModel().getColumn(ProteinGroupTableFormat.ID).setMaxWidth(100);
        proteinOverviewPanel.getProteinsTable().getColumnModel().getColumn(ProteinGroupTableFormat.ID).setMinWidth(50);
        proteinOverviewPanel.getProteinsTable().getColumnModel().getColumn(ProteinGroupTableFormat.ACCESSION).setPreferredWidth(150);
        proteinOverviewPanel.getProteinsTable().getColumnModel().getColumn(ProteinGroupTableFormat.ACCESSION).setMaxWidth(120);
        proteinOverviewPanel.getProteinsTable().getColumnModel().getColumn(ProteinGroupTableFormat.ACCESSION).setMinWidth(50);
        proteinOverviewPanel.getProteinsTable().getColumnModel().getColumn(ProteinGroupTableFormat.SEQUENCE).setMinWidth(50);
        proteinOverviewPanel.getProteinsTable().getColumnModel().getColumn(ProteinGroupTableFormat.NUMBER_OF_DISTINCT_PEPTIDE_SEQUENCES).setPreferredWidth(100);
        proteinOverviewPanel.getProteinsTable().getColumnModel().getColumn(ProteinGroupTableFormat.NUMBER_OF_DISTINCT_PEPTIDE_SEQUENCES).setMaxWidth(100);
        proteinOverviewPanel.getProteinsTable().getColumnModel().getColumn(ProteinGroupTableFormat.NUMBER_OF_DISTINCT_PEPTIDE_SEQUENCES).setMinWidth(50);
        proteinOverviewPanel.getProteinsTable().getColumnModel().getColumn(ProteinGroupTableFormat.NUMBER_OF_SPECTRA).setPreferredWidth(90);
        proteinOverviewPanel.getProteinsTable().getColumnModel().getColumn(ProteinGroupTableFormat.NUMBER_OF_SPECTRA).setMaxWidth(90);
        proteinOverviewPanel.getProteinsTable().getColumnModel().getColumn(ProteinGroupTableFormat.NUMBER_OF_SPECTRA).setMinWidth(50);
        proteinOverviewPanel.getProteinsTable().getColumnModel().getColumn(ProteinGroupTableFormat.CONFIDENCE).setPreferredWidth(80);
        proteinOverviewPanel.getProteinsTable().getColumnModel().getColumn(ProteinGroupTableFormat.CONFIDENCE).setMaxWidth(80);
        proteinOverviewPanel.getProteinsTable().getColumnModel().getColumn(ProteinGroupTableFormat.CONFIDENCE).setMinWidth(50);

        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.SPECTRUM_ID).setPreferredWidth(100);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.SPECTRUM_ID).setMaxWidth(100);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.SPECTRUM_ID).setMinWidth(100);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.PRECURSOR_CHARGE).setPreferredWidth(50);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.PRECURSOR_CHARGE).setMaxWidth(50);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.PRECURSOR_CHARGE).setMinWidth(50);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.PRECURSOR_MZRATIO).setPreferredWidth(100);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.PRECURSOR_MZRATIO).setMaxWidth(100);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.PRECURSOR_MZRATIO).setMinWidth(100);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.PRECURSOR_INTENSITY).setPreferredWidth(100);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.PRECURSOR_INTENSITY).setMaxWidth(100);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.PRECURSOR_INTENSITY).setMinWidth(100);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.RETENTION_TIME).setPreferredWidth(100);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.RETENTION_TIME).setMaxWidth(100);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.RETENTION_TIME).setMinWidth(100);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.PSM_CONFIDENCE).setPreferredWidth(100);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.PSM_CONFIDENCE).setMaxWidth(100);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.PSM_CONFIDENCE).setMinWidth(100);

        proteinOverviewPanel.getExportFileChooser().setApproveButtonText("Save");

        //Listeners
        proteinOverviewPanel.getProjectTree().addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) proteinOverviewPanel.getProjectTree().getLastSelectedPathComponent();

            if (node != null && node.isLeaf() && node.getUserObject() instanceof AnalyticalRun) {
                selectedAnalyticalRun = (AnalyticalRun) node.getUserObject();

                spectrumPanelGenerator.loadSettingsForRun(selectedAnalyticalRun);

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

        proteinGroupDTOSelectionModel.addListSelectionListener(lse -> {
            if (!lse.getValueIsAdjusting()) {
                if (!proteinGroupDTOSelectionModel.getSelected().isEmpty()) {
                    ProteinGroupDTO selectedProteinGroupDTO = proteinGroupDTOSelectionModel.getSelected().get(0);
                    //get the PeptideDTO instances for the selected protein group
                    List<PeptideDTO> peptideDTOs = peptideService.getPeptideDTOByProteinGroupId(selectedProteinGroupDTO.getId());

                    if(selectedProteinGroupDTO.getId().equals(3601L)){
                        System.out.println("test");
                    }

                    //map to PeptideTableRow objects
                    List<PeptideTableRow> peptideTableRows = mapPeptideDTOs(peptideDTOs);

                    GlazedLists.replaceAll(peptides, peptideTableRows, false);
                } else {
                    GlazedLists.replaceAll(peptides, new ArrayList<>(), false);
                }
            }
        });

        peptideSelectionModel.addListSelectionListener(lse -> {
            if (!lse.getValueIsAdjusting()) {
                if (peptideSelectionModel.getSelected().isEmpty()) {
                    GlazedLists.replaceAll(spectra, new ArrayList<>(), false);
                } else {
                    PeptideTableRow selectedPeptideTableRow = peptideSelectionModel.getSelected().get(0);
                    List<Spectrum> selectedSpectra = selectedPeptideTableRow.getSpectra();

                    setPsmTableCellRenderers();

                    GlazedLists.replaceAll(spectra, selectedSpectra, false);
                }
            }
        });

        proteinOverviewPanel.getPsmTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getClickCount() == 2 && !spectrumSelectionModel.getSelected().isEmpty()) {
                    showSpectrumPopDialog(spectrumSelectionModel.getSelected().get(0));
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
                    ProteinGroupTableModel exportModel = new ProteinGroupTableModel(new SortedList<>(exportProteinGroupDTOs, null), new ProteinGroupTableFormat());
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
                    exportModel.setPeptides(sortedPeptides);

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
     * Show the given spectrum in the spectrum popup dialog.
     *
     * @param spectrum the Spectrum instance
     */
    private void showSpectrumPopDialog(Spectrum spectrum) {
        try {
            JPanel spectrumJPanel = spectrumPopupDialog.getSpectrumJPanel();
            JPanel secondarySpectrumPlotsJPanel = spectrumPopupDialog.getSecondarySpectrumPlotsJPanel();

            spectrumPanelGenerator.addSpectrum(spectrum, spectrumJPanel, secondarySpectrumPlotsJPanel);

            GuiUtils.centerDialogOnComponent(mainController.getMainFrame(), spectrumPopupDialog);
            spectrumPopupDialog.setVisible(true);
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
                peptideTableRowMap.put(peptideDTO, new PeptideTableRow(peptideDTO));
            }
        }

        return new ArrayList<>(peptideTableRowMap.values());
    }

    /**
     * Set sparklines for the PSM table.
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
