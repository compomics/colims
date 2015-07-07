package com.compomics.colims.client.controller;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.AdvancedTableModel;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.GlazedListsSwing;
import com.compomics.colims.client.model.ProteinTableModel;
import com.compomics.colims.client.model.PsmTableModel;
import com.compomics.colims.client.model.tableformat.PeptideTableFormat;
import com.compomics.colims.client.model.tableformat.ProteinTableFormat;
import com.compomics.colims.client.model.tableformat.PsmTableFormat;
import com.compomics.colims.client.view.ProteinOverviewPanel;
import com.compomics.colims.model.*;
import com.compomics.colims.repository.PeptideRepository;
import com.compomics.colims.repository.ProteinRepository;
import com.compomics.colims.repository.SpectrumRepository;
import com.google.common.eventbus.EventBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Iain on 19/06/2015.
 */
@Component
public class ProteinOverviewController implements Controllable {

    @Autowired
    private MainController mainController;
    @Autowired
    private EventBus eventBus;
    @Autowired
    private ProteinRepository proteinRepository;
    @Autowired
    private PeptideRepository peptideRepository;
    @Autowired
    private SpectrumRepository spectrumRepository;

    private ProteinOverviewPanel proteinOverviewPanel;
    private ProteinTableModel proteinTableModel;
    private AdvancedTableModel peptideTableModel;
    private PsmTableModel psmTableModel;
    private final EventList<Protein> proteins = new BasicEventList<>();
    private final EventList<Peptide> peptides = new BasicEventList<>();
    private final EventList<Spectrum> spectra = new BasicEventList<>();
    private DefaultEventSelectionModel<Protein> proteinSelectionModel;
    private DefaultEventSelectionModel<Peptide> peptideSelectionModel;
    private DefaultEventSelectionModel<Spectrum> spectrumSelectionModel;
    private AnalyticalRun selectedAnalyticalRun;

    @Override
    public void init() {
        eventBus.register(this);
        proteinOverviewPanel = new ProteinOverviewPanel(mainController.getMainFrame(), this);

        // set up the tree!
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
        SortedList<Peptide> sortedPeptides = new SortedList<>(peptides, null);

        peptideTableModel = GlazedListsSwing.eventTableModel(sortedPeptides, new PeptideTableFormat());
        proteinOverviewPanel.getPeptidesTable().setModel(peptideTableModel);
        peptideSelectionModel = new DefaultEventSelectionModel<>(sortedPeptides);
        peptideSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        proteinOverviewPanel.getPeptidesTable().setSelectionModel(peptideSelectionModel);

        // init PSM table
        SortedList<Spectrum> sortedSpectra = new SortedList<>(spectra, null);

        psmTableModel = new PsmTableModel(sortedSpectra, new PsmTableFormat(), spectrumRepository);
        proteinOverviewPanel.getPsmTable().setModel(psmTableModel);
//        spectrumSelectionModel = new DefaultEventSelectionModel<>(sortedSpectra);
//        spectrumSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//        proteinOverviewPanel.getPsmTable().setSelectionModel(spectrumSelectionModel);

        proteinOverviewPanel.getProteinsTable().getColumnModel().getColumn(ProteinTableFormat.ID).setPreferredWidth(40);
        proteinOverviewPanel.getProteinsTable().getColumnModel().getColumn(ProteinTableFormat.ID).setMaxWidth(40);
        proteinOverviewPanel.getProteinsTable().getColumnModel().getColumn(ProteinTableFormat.ID).setMinWidth(40);

        proteinOverviewPanel.getPeptidesTable().getColumnModel().getColumn(PeptideTableFormat.ID).setPreferredWidth(40);
        proteinOverviewPanel.getPeptidesTable().getColumnModel().getColumn(PeptideTableFormat.ID).setMaxWidth(40);
        proteinOverviewPanel.getPeptidesTable().getColumnModel().getColumn(PeptideTableFormat.ID).setMinWidth(40);

        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(PsmTableFormat.SPECTRUM_ID).setPreferredWidth(40);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(PsmTableFormat.SPECTRUM_ID).setMaxWidth(40);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(PsmTableFormat.SPECTRUM_ID).setMinWidth(40);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(PsmTableFormat.PRECURSOR_CHARGE).setPreferredWidth(10);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(PsmTableFormat.PRECURSOR_MZRATIO).setPreferredWidth(50);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(PsmTableFormat.PRECURSOR_INTENSITY).setPreferredWidth(50);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(PsmTableFormat.RETENTION_TIME).setPreferredWidth(50);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(PsmTableFormat.PEPTIDE_SEQUENCE).setPreferredWidth(300);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(PsmTableFormat.PSM_CONFIDENCE).setPreferredWidth(50);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(PsmTableFormat.PROTEIN_ACCESSIONS).setPreferredWidth(300);

        //  Listeners

        proteinOverviewPanel.getProjectTree().addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) proteinOverviewPanel.getProjectTree().getLastSelectedPathComponent();

            if (node != null && node.isLeaf()) {
                selectedAnalyticalRun = (AnalyticalRun) node.getUserObject();

                proteinTableModel.reset(selectedAnalyticalRun);
                updateProteinTable();
            }
        });

        proteinSelectionModel.addListSelectionListener(lse -> {
            if (!lse.getValueIsAdjusting()) {
                if (proteinSelectionModel.getSelected().isEmpty()) {
                    GlazedLists.replaceAll(peptides, new ArrayList<>(), false);
                } else {
                    GlazedLists.replaceAll(peptides, peptideRepository.getPeptidesForProtein(proteinSelectionModel.getSelected().get(0)), false);
                }
            }
        });

        peptideSelectionModel.addListSelectionListener(lse -> {
            if (!lse.getValueIsAdjusting()) {
                if (peptideSelectionModel.getSelected().isEmpty()) {
                    GlazedLists.replaceAll(spectra, new ArrayList<>(), false);
                } else {
                    List<Spectrum> spectrumList = new ArrayList<>();
                    spectrumList.add(peptideSelectionModel.getSelected().get(0).getSpectrum());

                    GlazedLists.replaceAll(spectra, spectrumList, false);
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

    private void updateProteinTable() {
        if (selectedAnalyticalRun != null) {
            GlazedLists.replaceAll(proteins, proteinTableModel.getRows(selectedAnalyticalRun), false);
            proteinOverviewPanel.getPageLabelProteins().setText(proteinTableModel.getPageIndicator());
        } else {
            GlazedLists.replaceAll(proteins, new ArrayList<>(), false);
            proteinOverviewPanel.getPageLabelProteins().setText("");
        }
    }
}
