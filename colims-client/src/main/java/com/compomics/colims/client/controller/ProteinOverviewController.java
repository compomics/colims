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

    @Override
    public void init() {
        eventBus.register(this);
        proteinOverviewPanel = new ProteinOverviewPanel(mainController.getMainFrame(), this);

        // set up the tree!
        DefaultMutableTreeNode projectsNode = new DefaultMutableTreeNode("Projects");

        for (Project project : mainController.getProjects()) {
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

                projectsNode.add(projectNode);
            }
        }

        DefaultTreeModel treeModel = new DefaultTreeModel(projectsNode);
        proteinOverviewPanel.getProjectTree().setModel(treeModel);

        // init proteins table
        SortedList<Protein> sortedProteins = new SortedList<>(proteins, null);

        proteinTableModel = new ProteinTableModel(sortedProteins, new ProteinTableFormat());
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

        //  Listeners

        proteinOverviewPanel.getProjectTree().addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) proteinOverviewPanel.getProjectTree().getLastSelectedPathComponent();

            if (node != null && node.isLeaf()) {
                AnalyticalRun analyticalRun = (AnalyticalRun) node.getUserObject();

                // TODO: page it because it's slow

                GlazedLists.replaceAll(proteins, proteinRepository.getProteinsForRun(analyticalRun), false);
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
    }

    @Override
    public void showView() {}

    public ProteinOverviewPanel getProteinOverviewPanel() {
        return proteinOverviewPanel;
    }
}
