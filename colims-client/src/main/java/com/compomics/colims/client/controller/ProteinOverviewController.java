package com.compomics.colims.client.controller;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.AdvancedTableModel;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.GlazedListsSwing;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import com.compomics.colims.client.comparator.PeptideStartIndexComparator;
import com.compomics.colims.client.event.AnalyticalRunChangeEvent;
import com.compomics.colims.client.event.ExperimentChangeEvent;
import com.compomics.colims.client.event.ProjectChangeEvent;
import com.compomics.colims.client.event.SampleChangeEvent;
import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.factory.SpectrumPanelGenerator;
import com.compomics.colims.client.model.table.format.PeptideTableFormat;
import com.compomics.colims.client.model.table.format.ProteinGroupTableFormat;
import com.compomics.colims.client.model.table.format.ProteinPanelPsmTableFormat;
import com.compomics.colims.client.model.table.model.PeptideExportModel;
import com.compomics.colims.client.model.table.model.PeptideTableRow;
import com.compomics.colims.client.model.table.model.ProteinGroupTableModel;
import com.compomics.colims.client.renderer.PeptideSequenceRenderer;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.ProteinOverviewPanel;
import com.compomics.colims.client.view.SpectrumDialog;
import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.service.PeptideService;
import com.compomics.colims.core.service.SpectrumService;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.Project;
import com.compomics.colims.repository.hibernate.PeptideDTO;
import com.compomics.colims.repository.hibernate.ProteinGroupDTO;
import com.compomics.util.gui.TableProperties;
import com.compomics.util.preferences.UtilitiesUserPreferences;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import no.uib.jsparklines.renderers.JSparklinesBarChartTableCellRenderer;
import no.uib.jsparklines.renderers.JSparklinesIntervalChartTableCellRenderer;
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
import java.awt.event.*;
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
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * Created by Iain on 19/06/2015.
 */
@Component("proteinOverviewController")
public class ProteinOverviewController implements Controllable {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(ProteinOverviewController.class);

    //column filters
    private static final Pattern HTML_TAGS = Pattern.compile("<[a-z/]{1,5}>");

    private DefaultTreeModel projectTreeModel;
    private ProteinGroupTableModel proteinGroupTableModel;
    private AdvancedTableModel peptideTableModel;
    private AdvancedTableModel psmTableModel;
    private final EventList<ProteinGroupDTO> proteinGroupDTOs = new BasicEventList<>();
    private final EventList<PeptideTableRow> peptideTableRows = new BasicEventList<>();
    private final EventList<Peptide> psms = new BasicEventList<>();
    private DefaultEventSelectionModel<ProteinGroupDTO> proteinGroupSelectionModel;
    private DefaultEventSelectionModel<PeptideTableRow> peptideSelectionModel;
    private DefaultEventSelectionModel<Peptide> psmSelectionModel;
    private final ProteinPanelPsmTableFormat psmTableFormat = new ProteinPanelPsmTableFormat();
    private final List<AnalyticalRun> selectedAnalyticalRuns = new ArrayList<>();
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
    private SpectrumPanelGenerator spectrumPanelGenerator;

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
        //register to event bus
        eventBus.register(this);

        //init views
        proteinOverviewPanel = new ProteinOverviewPanel();

        //init and populate project tree
        DefaultMutableTreeNode projectsNode = new DefaultMutableTreeNode("Projects");
        projectTreeModel = new DefaultTreeModel(projectsNode);
        proteinOverviewPanel.getProjectTree().setModel(projectTreeModel);
        proteinOverviewPanel.getProjectTree().getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        populateProjectTree();

        //init protein group table
        SortedList<ProteinGroupDTO> sortedProteinGroups = new SortedList<>(proteinGroupDTOs, null);

        proteinGroupTableModel = new ProteinGroupTableModel(sortedProteinGroups, new ProteinGroupTableFormat(), 20, ProteinGroupTableFormat.ID);
        proteinOverviewPanel.getProteinGroupTable().setModel(proteinGroupTableModel);
        proteinGroupSelectionModel = new DefaultEventSelectionModel<>(sortedProteinGroups);
        proteinGroupSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        proteinOverviewPanel.getProteinGroupTable().setSelectionModel(proteinGroupSelectionModel);

        //init peptide table
        //SortedList<PeptideTableRow> sortedPeptides = new SortedList<>(peptideTableRows, (o1, o2) -> o2.getPeptides().size() - (o1.getPeptides().size()));
        SortedList<PeptideTableRow> sortedPeptides = new SortedList<>(peptideTableRows, new PeptideStartIndexComparator());

        peptideTableModel = GlazedListsSwing.eventTableModel(sortedPeptides, new PeptideTableFormat());
        proteinOverviewPanel.getPeptideTable().setModel(peptideTableModel);
        peptideSelectionModel = new DefaultEventSelectionModel<>(sortedPeptides);
        peptideSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        proteinOverviewPanel.getPeptideTable().setSelectionModel(peptideSelectionModel);

        //use MULTIPLE_COLUMN_MOUSE to allow sorting by multiple columns
        TableComparatorChooser peptideTableSorter = TableComparatorChooser.install(
                proteinOverviewPanel.getPeptideTable(), sortedPeptides, TableComparatorChooser.SINGLE_COLUMN);

        //init PSM table
        SortedList<Peptide> sortedPsms = new SortedList<>(psms, (o1, o2) -> o1.getPsmPostErrorProbability().compareTo(o2.getPsmPostErrorProbability()));

        psmTableModel = GlazedListsSwing.eventTableModel(sortedPsms, psmTableFormat);
        proteinOverviewPanel.getPsmTable().setModel(psmTableModel);
        psmSelectionModel = new DefaultEventSelectionModel<>(sortedPsms);
        psmSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        proteinOverviewPanel.getPsmTable().setSelectionModel(psmSelectionModel);

        //use MULTIPLE_COLUMN_MOUSE to allow sorting by multiple columns
        TableComparatorChooser psmTableSorter = TableComparatorChooser.install(
                proteinOverviewPanel.getPsmTable(), sortedPsms, TableComparatorChooser.SINGLE_COLUMN);

        //set column width of the different tables
        proteinOverviewPanel.getProteinGroupTable().getColumnModel().getColumn(ProteinGroupTableFormat.ID).setPreferredWidth(70);
        proteinOverviewPanel.getProteinGroupTable().getColumnModel().getColumn(ProteinGroupTableFormat.ID).setMaxWidth(150);
        proteinOverviewPanel.getProteinGroupTable().getColumnModel().getColumn(ProteinGroupTableFormat.ID).setMinWidth(50);
        proteinOverviewPanel.getProteinGroupTable().getColumnModel().getColumn(ProteinGroupTableFormat.ACCESSION).setPreferredWidth(150);
        proteinOverviewPanel.getProteinGroupTable().getColumnModel().getColumn(ProteinGroupTableFormat.ACCESSION).setMaxWidth(250);
        proteinOverviewPanel.getProteinGroupTable().getColumnModel().getColumn(ProteinGroupTableFormat.ACCESSION).setMinWidth(50);
        proteinOverviewPanel.getProteinGroupTable().getColumnModel().getColumn(ProteinGroupTableFormat.SEQUENCE).setMinWidth(50);
        proteinOverviewPanel.getProteinGroupTable().getColumnModel().getColumn(ProteinGroupTableFormat.NUMBER_OF_DISTINCT_PEPTIDE_SEQUENCES).setPreferredWidth(145);
        proteinOverviewPanel.getProteinGroupTable().getColumnModel().getColumn(ProteinGroupTableFormat.NUMBER_OF_DISTINCT_PEPTIDE_SEQUENCES).setMaxWidth(200);
        proteinOverviewPanel.getProteinGroupTable().getColumnModel().getColumn(ProteinGroupTableFormat.NUMBER_OF_DISTINCT_PEPTIDE_SEQUENCES).setMinWidth(50);
        proteinOverviewPanel.getProteinGroupTable().getColumnModel().getColumn(ProteinGroupTableFormat.NUMBER_OF_SPECTRA).setPreferredWidth(60);
        proteinOverviewPanel.getProteinGroupTable().getColumnModel().getColumn(ProteinGroupTableFormat.NUMBER_OF_SPECTRA).setMaxWidth(100);
        proteinOverviewPanel.getProteinGroupTable().getColumnModel().getColumn(ProteinGroupTableFormat.NUMBER_OF_SPECTRA).setMinWidth(50);
        proteinOverviewPanel.getProteinGroupTable().getColumnModel().getColumn(ProteinGroupTableFormat.CONFIDENCE).setPreferredWidth(80);
        proteinOverviewPanel.getProteinGroupTable().getColumnModel().getColumn(ProteinGroupTableFormat.CONFIDENCE).setMaxWidth(100);
        proteinOverviewPanel.getProteinGroupTable().getColumnModel().getColumn(ProteinGroupTableFormat.CONFIDENCE).setMinWidth(50);

        proteinOverviewPanel.getPeptideTable().getColumnModel().getColumn(PeptideTableFormat.SEQUENCE).setPreferredWidth(150);
        proteinOverviewPanel.getPeptideTable().getColumnModel().getColumn(PeptideTableFormat.PROTEIN_INFERENCE).setPreferredWidth(50);
        proteinOverviewPanel.getPeptideTable().getColumnModel().getColumn(PeptideTableFormat.PROTEIN_INFERENCE).setMaxWidth(50);
        proteinOverviewPanel.getPeptideTable().getColumnModel().getColumn(PeptideTableFormat.PROTEIN_INFERENCE).setMinWidth(20);
        proteinOverviewPanel.getPeptideTable().getColumnModel().getColumn(PeptideTableFormat.START).setPreferredWidth(150);
        proteinOverviewPanel.getPeptideTable().getColumnModel().getColumn(PeptideTableFormat.NUMBER_OF_SPECTRA).setPreferredWidth(60);
        proteinOverviewPanel.getPeptideTable().getColumnModel().getColumn(PeptideTableFormat.NUMBER_OF_SPECTRA).setMaxWidth(100);
        proteinOverviewPanel.getPeptideTable().getColumnModel().getColumn(PeptideTableFormat.NUMBER_OF_SPECTRA).setMinWidth(50);
        proteinOverviewPanel.getPeptideTable().getColumnModel().getColumn(PeptideTableFormat.CONFIDENCE).setPreferredWidth(80);
        proteinOverviewPanel.getPeptideTable().getColumnModel().getColumn(PeptideTableFormat.CONFIDENCE).setMaxWidth(100);
        proteinOverviewPanel.getPeptideTable().getColumnModel().getColumn(PeptideTableFormat.CONFIDENCE).setMinWidth(50);

        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.SPECTRUM_ID).setPreferredWidth(100);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.SPECTRUM_ID).setMaxWidth(100);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.SPECTRUM_ID).setMinWidth(100);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.PRECURSOR_CHARGE).setPreferredWidth(100);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.PRECURSOR_CHARGE).setMaxWidth(150);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.PRECURSOR_CHARGE).setMinWidth(50);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.PRECURSOR_MZRATIO).setPreferredWidth(100);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.PRECURSOR_MZRATIO).setMaxWidth(100);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.PRECURSOR_MZRATIO).setMinWidth(100);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.PRECURSOR_CHARGE).setPreferredWidth(200);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.PSM_CONFIDENCE).setPreferredWidth(100);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.PSM_CONFIDENCE).setMaxWidth(100);
        proteinOverviewPanel.getPsmTable().getColumnModel().getColumn(ProteinPanelPsmTableFormat.PSM_CONFIDENCE).setMinWidth(100);

        proteinOverviewPanel.getExportFileChooser().setApproveButtonText("Save");

        //setProteinGroupTableCellRenderers();
        //Listeners
        proteinOverviewPanel.getProjectTree().addTreeSelectionListener((TreeSelectionEvent e) -> {
            selectedAnalyticalRuns.clear();

            TreePath[] treePaths = proteinOverviewPanel.getProjectTree().getSelectionPaths();
            if (treePaths != null) {
                for (TreePath treePath : treePaths) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) treePath.getLastPathComponent();
                    if (node != null && node.isLeaf() && node.getUserObject() instanceof AnalyticalRun) {
                        AnalyticalRun selectedAnalyticalRun = (AnalyticalRun) node.getUserObject();
                        selectedAnalyticalRuns.add(selectedAnalyticalRun);
                    }
                }

                if (selectedAnalyticalRuns.size() > 0) {
                    //for the moment, take the search settings from the first selected run
                    //load search settings for the run
                    spectrumPanelGenerator.loadSettingsForRun(selectedAnalyticalRuns.get(0));
                    //set search parameters in PSM table formatter
                    psmTableFormat.setSearchParameters(selectedAnalyticalRuns.get(0).getSearchAndValidationSettings().getSearchParameters());

                    setPsmTableCellRenderers();

                    proteinGroupTableModel.reset(getSelectedAnalyticalRunIds());
                    updateProteinGroupTable();

                    //Set scrollpane to match row count (TODO: doesn't work!)
                    proteinOverviewPanel.getProteinGroupTableScrollPane().setPreferredSize(new Dimension(
                            proteinOverviewPanel.getProteinGroupTable().getPreferredSize().width,
                            proteinOverviewPanel.getProteinGroupTable().getRowHeight() * proteinGroupTableModel.getPerPage() + 1
                    ));

                    //get minimum and maximum projections for SparkLines rendering
                    Object[] spectraProjections = spectrumService.getSpectraProjections(getSelectedAnalyticalRunIds());
                    minimumRetentionTime = (double) spectraProjections[0];
                    maximumRetentionTime = (double) spectraProjections[1];
                    minimumMzRatio = (double) spectraProjections[2];
                    maximumMzRatio = (double) spectraProjections[3];
                    minimumCharge = (int) spectraProjections[4];
                    maximumCharge = (int) spectraProjections[5];
                }
            } else {
                //clear the selection
                GlazedLists.replaceAll(proteinGroupDTOs, new ArrayList<>(), false);
            }

        });

        proteinGroupSelectionModel.addListSelectionListener(lse -> {
            if (!lse.getValueIsAdjusting()) {
                if (!proteinGroupSelectionModel.getSelected().isEmpty()) {
                    ProteinGroupDTO selectedProteinGroupDTO = proteinGroupSelectionModel.getSelected().get(0);

                    //get the PeptideDTO instances for the selected protein group
                    List<PeptideDTO> peptideDTOs = peptideService.getPeptideDTO(selectedProteinGroupDTO.getId(), getSelectedAnalyticalRunIds());

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

                    GlazedLists.replaceAll(psms, selectedPsms, false);
                }
            }
        });

        proteinOverviewPanel.getPeptideTable().addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent evt) {
                int rowIndex = proteinOverviewPanel.getPeptideTable().rowAtPoint(evt.getPoint());
                int columnIndex = proteinOverviewPanel.getPeptideTable().columnAtPoint(evt.getPoint());

                if (rowIndex != -1 && columnIndex != -1 && proteinOverviewPanel.getPeptideTable().getValueAt(rowIndex, columnIndex) != null) {
                    if (columnIndex == PeptideTableFormat.SEQUENCE) {
                        proteinOverviewPanel.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

                        String sequence = (String) proteinOverviewPanel.getPeptideTable().getValueAt(rowIndex, columnIndex);

                        if (sequence.contains("<span") || sequence.contains("_")) {
                            try {
                                PeptideTableRow selectedPeptideTableRow = (PeptideTableRow) peptideTableModel.getElementAt(rowIndex);
                                String tooltip = PeptideSequenceRenderer.getModificationsHtmlToolTip(selectedPeptideTableRow.getSequence(), selectedPeptideTableRow.getPeptideHasModifications(), false);
                                proteinOverviewPanel.getPeptideTable().setToolTipText(tooltip);
                            } catch (Exception e) {
                                LOGGER.error(e.getMessage(), e);
                            }
                        } else {
                            proteinOverviewPanel.getPeptideTable().setToolTipText(null);
                        }
                    } else {
                        proteinOverviewPanel.getPeptideTable().setToolTipText(null);
                    }
                } else {
                    proteinOverviewPanel.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                    proteinOverviewPanel.getPeptideTable().setToolTipText(null);
                }
            }
        });

        proteinOverviewPanel.getPsmTable().addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent evt) {
                int rowIndex = proteinOverviewPanel.getPsmTable().rowAtPoint(evt.getPoint());
                int columnIndex = proteinOverviewPanel.getPsmTable().columnAtPoint(evt.getPoint());

                if (rowIndex != -1 && columnIndex != -1 && proteinOverviewPanel.getPsmTable().getValueAt(rowIndex, columnIndex) != null) {
                    if (columnIndex == ProteinPanelPsmTableFormat.SEQUENCE) {
                        proteinOverviewPanel.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

                        String sequence = (String) proteinOverviewPanel.getPsmTable().getValueAt(rowIndex, columnIndex);

                        if (sequence.contains("<span") || sequence.contains("_")) {
                            try {
                                Peptide selectedPeptide = (Peptide) psmTableModel.getElementAt(rowIndex);
                                String tooltip = PeptideSequenceRenderer.getModificationsHtmlToolTip(selectedPeptide.getSequence(), selectedPeptide.getPeptideHasModifications(), true);
                                proteinOverviewPanel.getPsmTable().setToolTipText(tooltip);
                            } catch (Exception e) {
                                LOGGER.error(e.getMessage(), e);
                            }
                        } else {
                            proteinOverviewPanel.getPsmTable().setToolTipText(null);
                        }
                    } else {
                        proteinOverviewPanel.getPsmTable().setToolTipText(null);
                    }
                } else {
                    proteinOverviewPanel.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                    proteinOverviewPanel.getPsmTable().setToolTipText(null);
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

        proteinOverviewPanel.getProteinGroupTable().getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                proteinGroupTableModel.updateSort(proteinOverviewPanel.getProteinGroupTable().columnAtPoint(e.getPoint()));
                proteinGroupTableModel.setPage(0);

                updateProteinGroupTable();
            }
        });

        proteinOverviewPanel.getFirstProteinGroupPageButton().addActionListener(e -> {
            proteinGroupTableModel.setPage(0);
            updateProteinGroupTable();

            proteinOverviewPanel.getNextProteinGroupPageButton().setEnabled(true);
            proteinOverviewPanel.getPrevProteinGroupPageButton().setEnabled(false);
            proteinOverviewPanel.getFirstProteinGroupPageButton().setEnabled(false);
            proteinOverviewPanel.getLastProteinGroupPageButton().setEnabled(true);
        });

        proteinOverviewPanel.getPrevProteinGroupPageButton().addActionListener(e -> {
            proteinGroupTableModel.setPage(proteinGroupTableModel.getPage() - 1);
            updateProteinGroupTable();

            proteinOverviewPanel.getNextProteinGroupPageButton().setEnabled(true);
            proteinOverviewPanel.getLastProteinGroupPageButton().setEnabled(true);

            if (proteinGroupTableModel.getPage() == 0) {
                proteinOverviewPanel.getPrevProteinGroupPageButton().setEnabled(false);
                proteinOverviewPanel.getFirstProteinGroupPageButton().setEnabled(false);
            }
        });

        proteinOverviewPanel.getNextProteinGroupPageButton().addActionListener(e -> {
            proteinGroupTableModel.setPage(proteinGroupTableModel.getPage() + 1);
            updateProteinGroupTable();

            proteinOverviewPanel.getPrevProteinGroupPageButton().setEnabled(true);
            proteinOverviewPanel.getFirstProteinGroupPageButton().setEnabled(true);

            if (proteinGroupTableModel.isMaxPage()) {
                proteinOverviewPanel.getNextProteinGroupPageButton().setEnabled(false);
                proteinOverviewPanel.getLastProteinGroupPageButton().setEnabled(false);
            }
        });

        proteinOverviewPanel.getLastProteinGroupPageButton().addActionListener(e -> {
            proteinGroupTableModel.setPage(proteinGroupTableModel.getMaxPage());
            updateProteinGroupTable();

            proteinOverviewPanel.getNextProteinGroupPageButton().setEnabled(false);
            proteinOverviewPanel.getPrevProteinGroupPageButton().setEnabled(true);
            proteinOverviewPanel.getFirstProteinGroupPageButton().setEnabled(true);
            proteinOverviewPanel.getLastProteinGroupPageButton().setEnabled(false);
        });

        proteinOverviewPanel.getProteinGroupFilterTextField().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);

                String filterText = proteinOverviewPanel.getProteinGroupFilterTextField().getText();

                if (filterText.matches("^[a-zA-Z0-9]*$")) {
                    proteinGroupTableModel.setFilter(proteinOverviewPanel.getProteinGroupFilterTextField().getText());

                    updateProteinGroupTable();
                }
            }
        });

        proteinOverviewPanel.getExportProteinGroupsButton().addActionListener(e -> {
            proteinOverviewPanel.getExportFileChooser().setDialogTitle("Export protein data");

            if (proteinOverviewPanel.getExportFileChooser().showOpenDialog(proteinOverviewPanel) == JFileChooser.APPROVE_OPTION) {
                mainController.getMainFrame().setCursor(new Cursor(Cursor.WAIT_CURSOR));

                EventList<ProteinGroupDTO> exportProteinGroupDTOs = new BasicEventList<>();
                ProteinGroupTableModel exportModel = new ProteinGroupTableModel(new SortedList<>(exportProteinGroupDTOs, null), new ProteinGroupTableFormat(), 20, ProteinGroupTableFormat.ID);
                exportModel.setPerPage(0);
                GlazedLists.replaceAll(exportProteinGroupDTOs, exportModel.getRows(getSelectedAnalyticalRunIds()), false);

                exportTable(proteinOverviewPanel.getExportFileChooser().getSelectedFile(), exportModel);

                mainController.getMainFrame().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        proteinOverviewPanel.getExportPeptidesButton().addActionListener(e -> {
            proteinOverviewPanel.getExportFileChooser().setDialogTitle("Export peptide data");

            if (proteinOverviewPanel.getExportFileChooser().showOpenDialog(proteinOverviewPanel) == JFileChooser.APPROVE_OPTION) {
                mainController.getMainFrame().setCursor(new Cursor(Cursor.WAIT_CURSOR));

                Map<Integer, Pattern> columnFilter = new HashMap<>();
                columnFilter.put(0, HTML_TAGS);

                //need a new model... or table format?
                //could potentially lose the filtering if its only on this model
                PeptideExportModel exportModel = new PeptideExportModel();
                exportModel.setPeptideTableRows(sortedPeptides);

                exportTable(proteinOverviewPanel.getExportFileChooser().getSelectedFile(), exportModel);

                mainController.getMainFrame().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        proteinOverviewPanel.getExportPsmsButton().addActionListener(e -> {
            proteinOverviewPanel.getExportFileChooser().setDialogTitle("Export PSM data");

            if (proteinOverviewPanel.getExportFileChooser().showOpenDialog(proteinOverviewPanel) == JFileChooser.APPROVE_OPTION) {
                mainController.getMainFrame().setCursor(new Cursor(Cursor.WAIT_CURSOR));

                exportTable(proteinOverviewPanel.getExportFileChooser().getSelectedFile(), psmTableModel);

                mainController.getMainFrame().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
    }

    @Override
    public void showView() {
        //do nothing
    }

    /**
     * Listen to a ProjectChangeEvent and update the project tree.
     *
     * @param projectChangeEvent the ProjectChangeEvent instance
     */
    @Subscribe
    public void onProjectChangeEvent(ProjectChangeEvent projectChangeEvent) {
        populateProjectTree();
    }

    /**
     * Listen to a ExperimentChangeEvent and update the project tree.
     *
     * @param experimentChangeEvent the ExperimentChangeEvent instance
     */
    @Subscribe
    public void onExperimentChangeEvent(ExperimentChangeEvent experimentChangeEvent) {
        populateProjectTree();
    }

    /**
     * Listen to a SampleChangeEvent and update the project tree.
     *
     * @param sampleChangeEvent the SampleChangeEvent instance
     */
    @Subscribe
    public void onSampleChangeEvent(SampleChangeEvent sampleChangeEvent) {
        populateProjectTree();
    }

    /**
     * Listen to a AnalyticalRunChangeEvent and update the project tree.
     *
     * @param analyticalRunChangeEvent the AnalyticalRunChangeEvent instance
     */
    @Subscribe
    public void onAnalyticalRunChangeEvent(AnalyticalRunChangeEvent analyticalRunChangeEvent) {
        populateProjectTree();
    }

    /**
     * Init the project tree.
     */
    private void populateProjectTree() {
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) projectTreeModel.getRoot();

        rootNode.removeAllChildren();
        mainController.getProjects().stream().forEach((project) -> rootNode.add(buildProjectTreeNode(project)));
        projectTreeModel.reload();
    }

    /**
     * Build a node tree for a given project consisting of experiments, samples
     * and runs.
     *
     * @param project A project to represent
     * @return A node of nodes
     */
    private DefaultMutableTreeNode buildProjectTreeNode(Project project) {
        DefaultMutableTreeNode projectNode = new DefaultMutableTreeNode(project.getTitle());

        if (project.getExperiments().size() > 0) {
            project.getExperiments().stream().forEach(experiment -> {
                DefaultMutableTreeNode experimentNode = new DefaultMutableTreeNode(experiment.getTitle());
                if (experiment.getSamples().size() > 0) {
                    experiment.getSamples().stream().forEach(sample -> {
                        DefaultMutableTreeNode sampleNode = new DefaultMutableTreeNode(sample.getName());
                        if (sample.getAnalyticalRuns().size() > 0) {
                            sample.getAnalyticalRuns().stream().forEach(analyticalRun -> {
                                DefaultMutableTreeNode runNode = new DefaultMutableTreeNode(analyticalRun);
                                sampleNode.add(runNode);
                            });
                        }
                        experimentNode.add(sampleNode);
                    });
                }
                projectNode.add(experimentNode);
            });
        }

        return projectNode;
    }

    /**
     * Update the protein group table contents and label.
     */
    private void updateProteinGroupTable() {
        GlazedLists.replaceAll(proteinGroupDTOs, new ArrayList<>(), false);
        proteinOverviewPanel.getProteinGroupPageLabel().setText("");
        if (!selectedAnalyticalRuns.isEmpty()) {
            GlazedLists.replaceAll(proteinGroupDTOs, proteinGroupTableModel.getRows(getSelectedAnalyticalRunIds()), false);
            proteinOverviewPanel.getProteinGroupPageLabel().setText(proteinGroupTableModel.getPageIndicator());
        }
    }

    /**
     * Show the given PSM in the spectrum dialog.
     *
     * @param peptide the Peptide instance
     */
    private void showPsmPopDialog(Peptide peptide) {
        try {
            SpectrumDialog spectrumDialog = spectrumPanelGenerator.generateSpectrumDialog(mainController.getMainFrame(), peptide);

            GuiUtils.centerDialogOnComponent(mainController.getMainFrame(), spectrumDialog);
            spectrumDialog.setVisible(true);
        } catch (MappingException | InterruptedException | SQLException | IOException | ClassNotFoundException e) {
            LOGGER.error(e, e.getCause());
            eventBus.post(new MessageEvent("Spectrum dialog problem", "The spectrum cannot be shown", JOptionPane.ERROR_MESSAGE));
        }
    }

    /**
     * Save the contents of a data table to a tab delimited file.
     *
     * @param filename File to be saved as [filename].tsv
     * @param tableModel A table model to retrieve data from
     * @param <T> Class extending TableModel
     */
    private <T extends TableModel> void exportTable(File filename, T tableModel) {
        exportTable(filename, tableModel, new HashMap<>());
    }

    /**
     * Save the contents of a data table to a tab delimited file.
     *
     * @param filename File to be saved as [filename].tsv
     * @param tableModel A table model to retrieve data from
     * @param columnFilters Patterns to match and filter values
     * @param <T> Class extending TableModel
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

            //show dialog
            eventBus.post(new MessageEvent("Export error", "Data exported to " + filename + ".tsv", JOptionPane.INFORMATION_MESSAGE));
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            eventBus.post(new MessageEvent("Export error", "Exporting tabular data failed: " + System.lineSeparator() + System.lineSeparator() + e.getMessage(), JOptionPane.ERROR_MESSAGE));
        }
    }

    /**
     * Get the selected run IDs.
     *
     * @return the list of selected run IDs
     */
    private List<Long> getSelectedAnalyticalRunIds() {
        return selectedAnalyticalRuns.stream().map(AnalyticalRun::getId).collect(Collectors.toList());
    }

    /**
     * Map the list of PeptideDTO instances associated with the given protein
     * group to a list of PeptideTableRow instances.
     *
     * @param peptideDTOs the set of PeptideDTO instances
     * @return a list of PeptideTableRow instances
     */
    private List<PeptideTableRow> mapPeptideDTOs(List<PeptideDTO> peptideDTOs) {
        Map<PeptideDTO, PeptideTableRow> peptideTableRowMap = new HashMap<>();

        peptideDTOs.stream().forEach((peptideDTO) -> {
            if (peptideTableRowMap.containsKey(peptideDTO)) {
                PeptideTableRow peptideTableRow = peptideTableRowMap.get(peptideDTO);
                peptideTableRow.addPeptideDTO(peptideDTO);
            } else {
                peptideTableRowMap.put(peptideDTO, new PeptideTableRow(peptideDTO, proteinGroupSelectionModel.getSelected().get(0).getMainSequence()));
            }
        });

        return new ArrayList<>(peptideTableRowMap.values());
    }

    /**
     * Set SparkLines for the protein group table.
     */
    private void setProteinGroupTableCellRenderers() {
        String mainSequence = proteinGroupSelectionModel.getSelected().get(0).getMainSequence();

        proteinOverviewPanel.getProteinGroupTable()
                .getColumnModel()
                .getColumn(PeptideTableFormat.START)
                .setCellRenderer(new JSparklinesMultiIntervalChartTableCellRenderer(
                        PlotOrientation.HORIZONTAL, (double) mainSequence.length(),
                        ((double) mainSequence.length()) / TableProperties.getLabelWidth(), utilitiesUserPreferences.getSparklineColor()));

        ((JSparklinesMultiIntervalChartTableCellRenderer) proteinOverviewPanel.getPeptideTable()
                .getColumnModel()
                .getColumn(PeptideTableFormat.START)
                .getCellRenderer())
                .showReferenceLine(true, 0.02, Color.BLACK);

        ((JSparklinesMultiIntervalChartTableCellRenderer) proteinOverviewPanel.getPeptideTable()
                .getColumnModel()
                .getColumn(PeptideTableFormat.START)
                .getCellRenderer())
                .showNumberAndChart(true, TableProperties.getLabelWidth() - 10);
    }

    /**
     * Set SparkLines for the peptide table.
     */
    private void setPeptideTableCellRenderers() {
        String mainSequence = proteinGroupSelectionModel.getSelected().get(0).getMainSequence();

        proteinOverviewPanel.getPeptideTable()
                .getColumnModel()
                .getColumn(PeptideTableFormat.START)
                .setCellRenderer(new JSparklinesMultiIntervalChartTableCellRenderer(
                        PlotOrientation.HORIZONTAL, (double) mainSequence.length(),
                        ((double) mainSequence.length()) / TableProperties.getLabelWidth(), utilitiesUserPreferences.getSparklineColor()));

        ((JSparklinesMultiIntervalChartTableCellRenderer) proteinOverviewPanel.getPeptideTable()
                .getColumnModel()
                .getColumn(PeptideTableFormat.START)
                .getCellRenderer())
                .showReferenceLine(true, 0.02, Color.BLACK);

        ((JSparklinesMultiIntervalChartTableCellRenderer) proteinOverviewPanel.getPeptideTable()
                .getColumnModel()
                .getColumn(PeptideTableFormat.START)
                .getCellRenderer())
                .showNumberAndChart(true, TableProperties.getLabelWidth() - 10);
    }

    /**
     * Set SparkLines for the PSM table.
     */
    private void setPsmTableCellRenderers() {
        proteinOverviewPanel.getPsmTable()
                .getColumnModel()
                .getColumn(ProteinPanelPsmTableFormat.PRECURSOR_MASS_ERROR)
                .setCellRenderer(new JSparklinesBarChartTableCellRenderer(PlotOrientation.HORIZONTAL,
                        selectedAnalyticalRuns.get(0).getSearchAndValidationSettings().getSearchParameters().getPrecMassTolerance(),
                        selectedAnalyticalRuns.get(0).getSearchAndValidationSettings().getSearchParameters().getPrecMassTolerance(),
                        utilitiesUserPreferences.getSparklineColor(),
                        utilitiesUserPreferences.getSparklineColor())
                );

        ((JSparklinesBarChartTableCellRenderer) proteinOverviewPanel.getPsmTable()
                .getColumnModel()
                .getColumn(ProteinPanelPsmTableFormat.PRECURSOR_MASS_ERROR)
                .getCellRenderer())
                .showNumberAndChart(true, TableProperties.getLabelWidth());

        proteinOverviewPanel.getPsmTable()
                .getColumnModel()
                .getColumn(ProteinPanelPsmTableFormat.PRECURSOR_CHARGE)
                .setCellRenderer(
                        new JSparklinesBarChartTableCellRenderer(
                                PlotOrientation.HORIZONTAL,
                                (double) maximumCharge,
                                utilitiesUserPreferences.getSparklineColor()
                        )
                );

        ((JSparklinesBarChartTableCellRenderer) proteinOverviewPanel.getPsmTable()
                .getColumnModel()
                .getColumn(ProteinPanelPsmTableFormat.PRECURSOR_CHARGE)
                .getCellRenderer())
                .showNumberAndChart(true, TableProperties.getLabelWidth() - 30);

        proteinOverviewPanel.getPsmTable()
                .getColumnModel().getColumn(ProteinPanelPsmTableFormat.RETENTION_TIME)
                .setCellRenderer(new JSparklinesIntervalChartTableCellRenderer(PlotOrientation.HORIZONTAL,
                        minimumRetentionTime,
                        maximumRetentionTime,
                        maximumRetentionTime / 50,
                        utilitiesUserPreferences.getSparklineColor(),
                        utilitiesUserPreferences.getSparklineColor())
                );

        ((JSparklinesIntervalChartTableCellRenderer) proteinOverviewPanel.getPsmTable()
                .getColumnModel()
                .getColumn(ProteinPanelPsmTableFormat.RETENTION_TIME)
                .getCellRenderer())
                .showNumberAndChart(true, TableProperties.getLabelWidth() + 5);

        ((JSparklinesIntervalChartTableCellRenderer) proteinOverviewPanel.getPsmTable()
                .getColumnModel()
                .getColumn(ProteinPanelPsmTableFormat.RETENTION_TIME)
                .getCellRenderer())
                .showReferenceLine(true, 0.02, java.awt.Color.BLACK);
    }

}
