package com.compomics.colims.client.controller;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.AdvancedTableModel;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.GlazedListsSwing;
import com.compomics.colims.client.model.PeptideTableRow;
import com.compomics.colims.client.model.ProteinPanelPsmTableModel;
import com.compomics.colims.client.model.ProteinTableModel;
import com.compomics.colims.client.model.tableformat.PeptideTableFormat;
import com.compomics.colims.client.model.tableformat.ProteinPanelPsmTableFormat;
import com.compomics.colims.client.model.tableformat.ProteinTableFormat;
import com.compomics.colims.client.model.tableformat.PsmTableFormat;
import com.compomics.colims.client.view.ProteinOverviewPanel;
import com.compomics.colims.core.service.SpectrumService;
import com.compomics.colims.model.*;
import com.compomics.colims.repository.PeptideRepository;
import com.compomics.colims.repository.ProteinRepository;
import com.compomics.colims.repository.SpectrumRepository;
import com.compomics.util.preferences.UtilitiesUserPreferences;
import com.google.common.eventbus.EventBus;
import no.uib.jsparklines.renderers.JSparklinesIntervalChartTableCellRenderer;
import org.jfree.chart.plot.PlotOrientation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Iain on 19/06/2015.
 */
@Component
public class ProteinOverviewController implements Controllable {

    @Autowired
    private MainController mainController;
    @Autowired
    private SpectrumPopupController spectrumPopupController;
    @Autowired
    private EventBus eventBus;
    @Autowired
    private ProteinRepository proteinRepository;
    @Autowired
    private PeptideRepository peptideRepository;
    @Autowired
    private SpectrumRepository spectrumRepository;
    @Autowired
    private SpectrumService spectrumService;

    private ProteinOverviewPanel proteinOverviewPanel;
    private ProteinTableModel proteinTableModel;
    private AdvancedTableModel peptideTableModel;
    private ProteinPanelPsmTableModel psmTableModel;
    private final EventList<Protein> proteins = new BasicEventList<>();
    private final EventList<PeptideTableRow> peptides = new BasicEventList<>();
    private final EventList<Spectrum> spectra = new BasicEventList<>();
    private DefaultEventSelectionModel<Protein> proteinSelectionModel;
    private DefaultEventSelectionModel<PeptideTableRow> peptideSelectionModel;
    private DefaultEventSelectionModel<Spectrum> spectrumSelectionModel;
    private AnalyticalRun selectedAnalyticalRun;
    private List<Long> spectrumIdsForRun = new ArrayList<>();
    private double minimumRetentionTime;
    private double maximumRetentionTime;
    private double minimumCharge;
    private double maximumCharge;

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

        // init proteins table
        SortedList<Protein> sortedProteins = new SortedList<>(proteins, null);

        proteinTableModel = new ProteinTableModel(sortedProteins, new ProteinTableFormat(), proteinRepository);
        proteinOverviewPanel.getProteinsTable().setModel(proteinTableModel);
        proteinSelectionModel = new DefaultEventSelectionModel<>(sortedProteins);
        proteinSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        proteinOverviewPanel.getProteinsTable().setSelectionModel(proteinSelectionModel);

        // init peptides table
        SortedList<PeptideTableRow> sortedPeptides = new SortedList<>(peptides, null);

        peptideTableModel = GlazedListsSwing.eventTableModel(sortedPeptides, new PeptideTableFormat());
        proteinOverviewPanel.getPeptidesTable().setModel(peptideTableModel);
        peptideSelectionModel = new DefaultEventSelectionModel<>(sortedPeptides);
        peptideSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        proteinOverviewPanel.getPeptidesTable().setSelectionModel(peptideSelectionModel);

        // init PSM table
        SortedList<Spectrum> sortedSpectra = new SortedList<>(spectra, null);

        psmTableModel = new ProteinPanelPsmTableModel(sortedSpectra, new ProteinPanelPsmTableFormat(), spectrumRepository);
        proteinOverviewPanel.getPsmTable().setModel(psmTableModel);
        spectrumSelectionModel = new DefaultEventSelectionModel<>(sortedSpectra);
        spectrumSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        proteinOverviewPanel.getPsmTable().setSelectionModel(spectrumSelectionModel);

        proteinOverviewPanel.getProteinsTable().getColumnModel().getColumn(ProteinTableFormat.ID).setPreferredWidth(40);
        proteinOverviewPanel.getProteinsTable().getColumnModel().getColumn(ProteinTableFormat.ID).setMaxWidth(40);
        proteinOverviewPanel.getProteinsTable().getColumnModel().getColumn(ProteinTableFormat.ID).setMinWidth(40);

        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.SPECTRUM_ID).setPreferredWidth(40);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.SPECTRUM_ID).setMaxWidth(40);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.SPECTRUM_ID).setMinWidth(40);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.PRECURSOR_CHARGE).setPreferredWidth(10);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.PRECURSOR_MZRATIO).setPreferredWidth(50);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.PRECURSOR_INTENSITY).setPreferredWidth(50);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.RETENTION_TIME).setPreferredWidth(50);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.PSM_CONFIDENCE).setPreferredWidth(50);

        //  Listeners

        proteinOverviewPanel.getProjectTree().addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) proteinOverviewPanel.getProjectTree().getLastSelectedPathComponent();

            if (node != null && node.isLeaf()) {
                selectedAnalyticalRun = (AnalyticalRun) node.getUserObject();

                proteinTableModel.reset(selectedAnalyticalRun);
                updateProteinTable();

                spectrumIdsForRun = spectrumRepository.getSpectraIdsForRun(selectedAnalyticalRun);

                // Set scrollpane to match row count (TODO: doesn't work!)
                proteinOverviewPanel.getProteinsScrollPane().setPreferredSize(new Dimension(
                    proteinOverviewPanel.getProteinsTable().getPreferredSize().width,
                    proteinOverviewPanel.getProteinsTable().getRowHeight() * proteinTableModel.getPerPage() + 1
                ));

                minimumRetentionTime = spectrumService.getMinimumRetentionTime(selectedAnalyticalRun);
                maximumRetentionTime = spectrumService.getMinimumRetentionTime(selectedAnalyticalRun);
                minimumCharge = spectrumService.getMinimumCharge(selectedAnalyticalRun);
                maximumCharge = spectrumService.getMaximumCharge(selectedAnalyticalRun);
            }
        });

        proteinSelectionModel.addListSelectionListener(lse -> {
            if (!lse.getValueIsAdjusting()) {
                if (proteinSelectionModel.getSelected().isEmpty()) {
                    GlazedLists.replaceAll(peptides, new ArrayList<>(), false);
                } else {
                    List<PeptideHasProtein> newPeptides = peptideRepository.getPeptidesForProtein(proteinSelectionModel.getSelected().get(0), spectrumIdsForRun);

                    List<PeptideTableRow> peptideTableRows = new ArrayList<>();
                    Map<String, Integer> stringIntegerMap = new HashMap<>();

                    for (PeptideHasProtein peptideHasProtein : newPeptides) {
                        Peptide peptide = peptideHasProtein.getPeptide();

                        if (stringIntegerMap.containsKey(peptide.getSequence())) {
                            peptideTableRows.get(stringIntegerMap.get(peptide.getSequence())).addPeptide(peptide);
                        } else {
                            peptideTableRows.add(new PeptideTableRow(peptide));
                            stringIntegerMap.put(peptide.getSequence(), stringIntegerMap.size());
                        }
                    }

                    for (PeptideTableRow peptideTableRow : peptideTableRows) {
                        peptideTableRow.getPeptideHasModifications().addAll(peptideRepository.getModificationsForMultiplePeptides(peptideTableRow.getPeptides()));
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
                    List<Peptide> peptides = peptideRepository.getPeptidesFromSequence(peptideSelectionModel.getSelected().get(0).getSequence(), spectrumIdsForRun);
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
                proteinTableModel.updateSort(proteinOverviewPanel.getProteinsTable().columnAtPoint(e.getPoint()));
                proteinTableModel.setPage(0);

                updateProteinTable();
            }
        });

        proteinOverviewPanel.getFirstPageProteins().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                proteinTableModel.setPage(0);
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
                proteinTableModel.setPage(proteinTableModel.getPage() - 1);
                updateProteinTable();

                proteinOverviewPanel.getNextPageProteins().setEnabled(true);
                proteinOverviewPanel.getLastPageProteins().setEnabled(true);

                if (proteinTableModel.getPage() == 0) {
                    proteinOverviewPanel.getPrevPageProteins().setEnabled(false);
                    proteinOverviewPanel.getFirstPageProteins().setEnabled(false);
                }
            }
        });

        proteinOverviewPanel.getNextPageProteins().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                proteinTableModel.setPage(proteinTableModel.getPage() + 1);
                updateProteinTable();

                proteinOverviewPanel.getPrevPageProteins().setEnabled(true);
                proteinOverviewPanel.getFirstPageProteins().setEnabled(true);

                if (proteinTableModel.isMaxPage()) {
                    proteinOverviewPanel.getNextPageProteins().setEnabled(false);
                    proteinOverviewPanel.getLastPageProteins().setEnabled(false);
                }
            }
        });

        proteinOverviewPanel.getLastPageProteins().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                proteinTableModel.setPage(proteinTableModel.getMaxPage());
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
                    proteinTableModel.setFilter(proteinOverviewPanel.getFilterProteins().getText());

                    updateProteinTable();
                }
            }
        });
    }

    @Override
    public void showView() {}

    /**
     * Get the panel associated with this controller
     * @return Protein overview panel
     */
    public ProteinOverviewPanel getProteinOverviewPanel() {
        return proteinOverviewPanel;
    }

    /**
     * Build a node tree for a given project consisting of experiments, samples and runs
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
            GlazedLists.replaceAll(proteins, proteinTableModel.getRows(selectedAnalyticalRun), false);
            proteinOverviewPanel.getPageLabelProteins().setText(proteinTableModel.getPageIndicator());
        } else {
            GlazedLists.replaceAll(proteins, new ArrayList<>(), false);
            proteinOverviewPanel.getPageLabelProteins().setText("");
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
