package com.compomics.colims.client.view;

import com.compomics.colims.client.controller.ProjectOverviewController;
import com.compomics.util.Util;
import com.compomics.util.experiment.biology.AminoAcidPattern;
import com.compomics.util.experiment.biology.Ion;
import com.compomics.util.experiment.biology.IonFactory;
import com.compomics.util.experiment.biology.NeutralLoss;
import com.compomics.util.experiment.biology.PTM;
import com.compomics.util.experiment.biology.PTMFactory;
import com.compomics.util.experiment.biology.Peptide;
import com.compomics.util.experiment.biology.ions.PeptideFragmentIon;
import static com.compomics.util.experiment.identification.Advocate.PeptideShaker;
import com.compomics.util.experiment.identification.SearchParameters;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.compomics.util.gui.error_handlers.HelpDialog;
import com.compomics.util.gui.export.graphics.ExportGraphicsDialog;
import com.compomics.util.gui.export.graphics.ExportGraphicsDialogParent;
import com.compomics.util.gui.spectrum.IntensityHistogram;
import com.compomics.util.gui.spectrum.MassErrorPlot;
import com.compomics.util.preferences.AnnotationPreferences;
import com.compomics.util.preferences.ModificationProfile;
import com.compomics.util.preferences.UtilitiesUserPreferences;
import java.awt.Component;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;

/**
 *
 * @author Niels Hulstaert
 */
public class ProjectOverviewPanel extends javax.swing.JPanel implements ExportGraphicsDialogParent {

    /**
     * Turns of the gradient painting for the bar charts.
     */
    static {
        XYBarRenderer.setDefaultBarPainter(new StandardXYBarPainter());
    }
    /**
     * The parent frame.
     */
    private JFrame colimsFrame;
    /**
     * The controller.
     */
    private ProjectOverviewController projectOverviewController;
    /**
     * The compomics PTM factory.
     */
    private PTMFactory ptmFactory = PTMFactory.getInstance();
    /**
     * The annotation preferences.
     */
    private AnnotationPreferences annotationPreferences = new AnnotationPreferences(); // @TODO: set the preferences
    /**
     * The neutral loss menus.
     */
    private HashMap<NeutralLoss, JCheckBoxMenuItem> lossMenus = new HashMap<NeutralLoss, JCheckBoxMenuItem>();
    /**
     * The charge menus.
     */
    private HashMap<Integer, JCheckBoxMenuItem> chargeMenus = new HashMap<Integer, JCheckBoxMenuItem>();
    /**
     * The current search parameters.
     */
    private SearchParameters searchParameters = new SearchParameters(); // @TODO: get from the given project
    /**
     * If true the relative error (ppm) is used instead of the absolute error
     * (Da).
     */
    private boolean useRelativeError = false;
    /**
     * The utilities user preferences.
     */
    private UtilitiesUserPreferences utilitiesUserPreferences;
    /**
     * Boolean indicating whether the spectrum shall be displayed.
     */
    private boolean displaySpectrum = true;
    /**
     * The last folder opened by the user. Defaults to user.home.
     */
    private String lastSelectedFolder = "user.home";
    /**
     * The label with for the numbers in the jsparklines columns.
     */
    private int labelWidth = 50;

    /**
     * Creates new form ProjectOverviewPanel1
     */
    public ProjectOverviewPanel(final JFrame colimsFrame, final ProjectOverviewController projectOverviewController, final UtilitiesUserPreferences utilitiesUserPreferences) {
        this.colimsFrame = colimsFrame;
        this.projectOverviewController = projectOverviewController;
        this.utilitiesUserPreferences = utilitiesUserPreferences;

        initComponents();

        // @TODO: these should be set according to the current selection
        annotationPreferences.setFragmentIonAccuracy(0.02);
        annotationPreferences.addIonType(Ion.IonType.PEPTIDE_FRAGMENT_ION, PeptideFragmentIon.B_ION);
        annotationPreferences.addIonType(Ion.IonType.PEPTIDE_FRAGMENT_ION, PeptideFragmentIon.Y_ION);
        annotationPreferences.addIonType(Ion.IonType.IMMONIUM_ION);
        annotationPreferences.addSelectedCharge(1);

        setUpGui();
    }

    public JTable getExperimentsTable() {
        return experimentsTable;
    }

    public JTable getProjectsTable() {
        return projectsTable;
    }

    public JTable getAnalyticalRunsTable() {
        return analyticalRunsTable;
    }

    public JTable getSamplesTable() {
        return samplesTable;
    }

    public JTable getPsmTable() {
        return psmTable;
    }

    public JPanel getSpectrumJPanel() {
        return spectrumJPanel;
    }

    public JPanel getSecondarySpectrumPlotsJPanel() {
        return secondarySpectrumPlotsJPanel;
    }

    public JPanel getSpectrumMainPanel() {
        return spectrumMainPanel;
    }

    public AnnotationPreferences getAnnotationPreferences() {
        return annotationPreferences;
    }

    public SearchParameters getSearchParameters() {
        return searchParameters;
    }

    /**
     * Set up the GUI.
     */
    private void setUpGui() {

        // make sure that the scroll panes are see-through
        projectsScrollPane.getViewport().setOpaque(false);
        experimentsScrollPane.getViewport().setOpaque(false);
        samplesScrollPane.getViewport().setOpaque(false);
        analyticalRunsScrollPane.getViewport().setOpaque(false);
        psmScrollPane.getViewport().setOpaque(false);

        // disable column reordering
        projectsTable.getTableHeader().setReorderingAllowed(false);
        experimentsTable.getTableHeader().setReorderingAllowed(false);
        samplesTable.getTableHeader().setReorderingAllowed(false);
        analyticalRunsTable.getTableHeader().setReorderingAllowed(false);
        psmTable.getTableHeader().setReorderingAllowed(false);

        // correct the color for the upper right corner
        JPanel projectsCorner = new JPanel();
        projectsCorner.setBackground(projectsTable.getTableHeader().getBackground());
        projectsScrollPane.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER, projectsCorner);
        JPanel experimentsCorner = new JPanel();
        experimentsCorner.setBackground(experimentsTable.getTableHeader().getBackground());
        experimentsScrollPane.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER, experimentsCorner);
        JPanel sampleTableCorner = new JPanel();
        sampleTableCorner.setBackground(samplesTable.getTableHeader().getBackground());
        samplesScrollPane.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER, sampleTableCorner);
        JPanel psmTableCorner = new JPanel();
        psmTableCorner.setBackground(psmTable.getTableHeader().getBackground());
        psmScrollPane.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER, psmTableCorner);

        // set up the table header tooltips
        setUpTableHeaderToolTips();

        // show the annotation menu bar
        spectrumAnnotationMenuPanel.add(annotationMenuBar);
    }

    /**
     * Set up the table header tooltips.
     */
    private void setUpTableHeaderToolTips() {
        // @TODO: implement me!
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        annotationMenuBar = new javax.swing.JMenuBar();
        splitterMenu5 = new javax.swing.JMenu();
        ionsMenu = new javax.swing.JMenu();
        aIonCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        bIonCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        cIonCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();
        xIonCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        yIonCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        zIonCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        splitterMenu8 = new javax.swing.JMenu();
        otherMenu = new javax.swing.JMenu();
        precursorCheckMenu = new javax.swing.JCheckBoxMenuItem();
        immoniumIonsCheckMenu = new javax.swing.JCheckBoxMenuItem();
        reporterIonsCheckMenu = new javax.swing.JCheckBoxMenuItem();
        lossSplitter = new javax.swing.JMenu();
        lossMenu = new javax.swing.JMenu();
        jSeparator7 = new javax.swing.JPopupMenu.Separator();
        adaptCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        splitterMenu2 = new javax.swing.JMenu();
        chargeMenu = new javax.swing.JMenu();
        splitterMenu3 = new javax.swing.JMenu();
        deNovoMenu = new javax.swing.JMenu();
        forwardIonsDeNovoCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        rewindIonsDeNovoCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        jSeparator19 = new javax.swing.JPopupMenu.Separator();
        deNovoChargeOneJRadioButtonMenuItem = new javax.swing.JRadioButtonMenuItem();
        deNovoChargeTwoJRadioButtonMenuItem = new javax.swing.JRadioButtonMenuItem();
        splitterMenu9 = new javax.swing.JMenu();
        settingsMenu = new javax.swing.JMenu();
        allCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        automaticAnnotationCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        errorPlotTypeCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        splitterMenu4 = new javax.swing.JMenu();
        exportGraphicsMenu = new javax.swing.JMenu();
        exportSpectrumMenu = new javax.swing.JMenu();
        exportSpectrumGraphicsJMenuItem = new javax.swing.JMenuItem();
        exportSpectrumAndPlotsGraphicsJMenuItem = new javax.swing.JMenuItem();
        exportSpectrumGraphicsSeparator = new javax.swing.JPopupMenu.Separator();
        exportSequenceFragmentationGraphicsJMenuItem = new javax.swing.JMenuItem();
        exportIntensityHistogramGraphicsJMenuItem = new javax.swing.JMenuItem();
        exportMassErrorPlotGraphicsJMenuItem = new javax.swing.JMenuItem();
        exportSpectrumValuesJMenuItem = new javax.swing.JMenuItem();
        splitterMenu6 = new javax.swing.JMenu();
        helpJMenu = new javax.swing.JMenu();
        helpMenuItem = new javax.swing.JMenuItem();
        splitterMenu7 = new javax.swing.JMenu();
        projectsPanel = new javax.swing.JPanel();
        projectsScrollPane = new javax.swing.JScrollPane();
        projectsTable = new javax.swing.JTable();
        experimentsPanel = new javax.swing.JPanel();
        experimentsScrollPane = new javax.swing.JScrollPane();
        experimentsTable = new javax.swing.JTable();
        samplesPanel = new javax.swing.JPanel();
        samplesScrollPane = new javax.swing.JScrollPane();
        samplesTable = new javax.swing.JTable();
        analyticalRunsPanel = new javax.swing.JPanel();
        analyticalRunsScrollPane = new javax.swing.JScrollPane();
        analyticalRunsTable = new javax.swing.JTable();
        psmPanel = new javax.swing.JPanel();
        psmScrollPane = new javax.swing.JScrollPane();
        psmTable = new javax.swing.JTable();
        spectrumMainPanel = new javax.swing.JPanel();
        spectrumContainerJPanel = new javax.swing.JPanel();
        spectrumJToolBar = new javax.swing.JToolBar();
        spectrumAnnotationMenuPanel = new javax.swing.JPanel();
        spectrumSplitPane = new javax.swing.JSplitPane();
        secondarySpectrumPlotsJPanel = new javax.swing.JPanel();
        spectrumOuterJPanel = new javax.swing.JPanel();
        spectrumPaddingPanel = new javax.swing.JPanel();
        spectrumJPanel = new javax.swing.JPanel();

        annotationMenuBar.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        annotationMenuBar.setOpaque(false);

        splitterMenu5.setText("|");
        splitterMenu5.setEnabled(false);
        annotationMenuBar.add(splitterMenu5);

        ionsMenu.setText("Ions");

        aIonCheckBoxMenuItem.setText("a");
        aIonCheckBoxMenuItem.setToolTipText("a-ions");
        aIonCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aIonCheckBoxMenuItemActionPerformed(evt);
            }
        });
        ionsMenu.add(aIonCheckBoxMenuItem);

        bIonCheckBoxMenuItem.setText("b");
        bIonCheckBoxMenuItem.setToolTipText("b-ions");
        bIonCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bIonCheckBoxMenuItemActionPerformed(evt);
            }
        });
        ionsMenu.add(bIonCheckBoxMenuItem);

        cIonCheckBoxMenuItem.setText("c");
        cIonCheckBoxMenuItem.setToolTipText("c-ions");
        cIonCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cIonCheckBoxMenuItemActionPerformed(evt);
            }
        });
        ionsMenu.add(cIonCheckBoxMenuItem);
        ionsMenu.add(jSeparator6);

        xIonCheckBoxMenuItem.setText("x");
        xIonCheckBoxMenuItem.setToolTipText("x-ions");
        xIonCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                xIonCheckBoxMenuItemActionPerformed(evt);
            }
        });
        ionsMenu.add(xIonCheckBoxMenuItem);

        yIonCheckBoxMenuItem.setText("y");
        yIonCheckBoxMenuItem.setToolTipText("y-ions");
        yIonCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                yIonCheckBoxMenuItemActionPerformed(evt);
            }
        });
        ionsMenu.add(yIonCheckBoxMenuItem);

        zIonCheckBoxMenuItem.setText("z");
        zIonCheckBoxMenuItem.setToolTipText("z-ions");
        zIonCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zIonCheckBoxMenuItemActionPerformed(evt);
            }
        });
        ionsMenu.add(zIonCheckBoxMenuItem);

        annotationMenuBar.add(ionsMenu);

        splitterMenu8.setText("|");
        splitterMenu8.setEnabled(false);
        annotationMenuBar.add(splitterMenu8);

        otherMenu.setText("Other");

        precursorCheckMenu.setSelected(true);
        precursorCheckMenu.setText("Precursor");
        precursorCheckMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                precursorCheckMenuActionPerformed(evt);
            }
        });
        otherMenu.add(precursorCheckMenu);

        immoniumIonsCheckMenu.setSelected(true);
        immoniumIonsCheckMenu.setText("Immonium");
        immoniumIonsCheckMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                immoniumIonsCheckMenuActionPerformed(evt);
            }
        });
        otherMenu.add(immoniumIonsCheckMenu);

        reporterIonsCheckMenu.setSelected(true);
        reporterIonsCheckMenu.setText("Reporter");
        reporterIonsCheckMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reporterIonsCheckMenuActionPerformed(evt);
            }
        });
        otherMenu.add(reporterIonsCheckMenu);

        annotationMenuBar.add(otherMenu);

        lossSplitter.setText("|");
        lossSplitter.setEnabled(false);
        annotationMenuBar.add(lossSplitter);

        lossMenu.setText("Loss");
        lossMenu.add(jSeparator7);

        adaptCheckBoxMenuItem.setText("Adapt");
        adaptCheckBoxMenuItem.setToolTipText("Adapt losses to sequence and modifications");
        adaptCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                adaptCheckBoxMenuItemActionPerformed(evt);
            }
        });
        lossMenu.add(adaptCheckBoxMenuItem);

        annotationMenuBar.add(lossMenu);

        splitterMenu2.setText("|");
        splitterMenu2.setEnabled(false);
        annotationMenuBar.add(splitterMenu2);

        chargeMenu.setText("Charge");
        annotationMenuBar.add(chargeMenu);

        splitterMenu3.setText("|");
        splitterMenu3.setEnabled(false);
        annotationMenuBar.add(splitterMenu3);

        deNovoMenu.setText("De Novo");

        forwardIonsDeNovoCheckBoxMenuItem.setText("f-ions");
        forwardIonsDeNovoCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                forwardIonsDeNovoCheckBoxMenuItemActionPerformed(evt);
            }
        });
        deNovoMenu.add(forwardIonsDeNovoCheckBoxMenuItem);

        rewindIonsDeNovoCheckBoxMenuItem.setText("r-ions");
        rewindIonsDeNovoCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rewindIonsDeNovoCheckBoxMenuItemActionPerformed(evt);
            }
        });
        deNovoMenu.add(rewindIonsDeNovoCheckBoxMenuItem);
        deNovoMenu.add(jSeparator19);

        deNovoChargeOneJRadioButtonMenuItem.setSelected(true);
        deNovoChargeOneJRadioButtonMenuItem.setText("Single Charge");
        deNovoChargeOneJRadioButtonMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deNovoChargeOneJRadioButtonMenuItemActionPerformed(evt);
            }
        });
        deNovoMenu.add(deNovoChargeOneJRadioButtonMenuItem);

        deNovoChargeTwoJRadioButtonMenuItem.setText("Double Charge");
        deNovoChargeTwoJRadioButtonMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deNovoChargeTwoJRadioButtonMenuItemActionPerformed(evt);
            }
        });
        deNovoMenu.add(deNovoChargeTwoJRadioButtonMenuItem);

        annotationMenuBar.add(deNovoMenu);

        splitterMenu9.setText("|");
        splitterMenu9.setEnabled(false);
        annotationMenuBar.add(splitterMenu9);

        settingsMenu.setText("Settings");

        allCheckBoxMenuItem.setText("Show All Peaks");
        allCheckBoxMenuItem.setToolTipText("Show all peaks or just the annotated peaks");
        allCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                allCheckBoxMenuItemActionPerformed(evt);
            }
        });
        settingsMenu.add(allCheckBoxMenuItem);
        settingsMenu.add(jSeparator5);

        automaticAnnotationCheckBoxMenuItem.setSelected(true);
        automaticAnnotationCheckBoxMenuItem.setText("Automatic Annotation");
        automaticAnnotationCheckBoxMenuItem.setToolTipText("Use automatic annotation");
        automaticAnnotationCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                automaticAnnotationCheckBoxMenuItemActionPerformed(evt);
            }
        });
        settingsMenu.add(automaticAnnotationCheckBoxMenuItem);

        errorPlotTypeCheckBoxMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        errorPlotTypeCheckBoxMenuItem.setSelected(true);
        errorPlotTypeCheckBoxMenuItem.setText("Absolute Mass Error Plot");
        errorPlotTypeCheckBoxMenuItem.setToolTipText("Plot the mass error in Da or ppm ");
        errorPlotTypeCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                errorPlotTypeCheckBoxMenuItemActionPerformed(evt);
            }
        });
        settingsMenu.add(errorPlotTypeCheckBoxMenuItem);

        annotationMenuBar.add(settingsMenu);

        splitterMenu4.setText("|");
        splitterMenu4.setEnabled(false);
        annotationMenuBar.add(splitterMenu4);

        exportGraphicsMenu.setText("Export");

        exportSpectrumMenu.setText("Figure");

        exportSpectrumGraphicsJMenuItem.setText("Spectrum");
        exportSpectrumGraphicsJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportSpectrumGraphicsJMenuItemActionPerformed(evt);
            }
        });
        exportSpectrumMenu.add(exportSpectrumGraphicsJMenuItem);

        exportSpectrumAndPlotsGraphicsJMenuItem.setText("Spectrum & Plots");
        exportSpectrumAndPlotsGraphicsJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportSpectrumAndPlotsGraphicsJMenuItemActionPerformed(evt);
            }
        });
        exportSpectrumMenu.add(exportSpectrumAndPlotsGraphicsJMenuItem);
        exportSpectrumMenu.add(exportSpectrumGraphicsSeparator);

        exportSequenceFragmentationGraphicsJMenuItem.setText("Sequence Fragmentation");
        exportSequenceFragmentationGraphicsJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportSequenceFragmentationGraphicsJMenuItemActionPerformed(evt);
            }
        });
        exportSpectrumMenu.add(exportSequenceFragmentationGraphicsJMenuItem);

        exportIntensityHistogramGraphicsJMenuItem.setText("Intensity Histogram");
        exportIntensityHistogramGraphicsJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportIntensityHistogramGraphicsJMenuItemActionPerformed(evt);
            }
        });
        exportSpectrumMenu.add(exportIntensityHistogramGraphicsJMenuItem);

        exportMassErrorPlotGraphicsJMenuItem.setText("Mass Error Plot");
        exportMassErrorPlotGraphicsJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportMassErrorPlotGraphicsJMenuItemActionPerformed(evt);
            }
        });
        exportSpectrumMenu.add(exportMassErrorPlotGraphicsJMenuItem);

        exportGraphicsMenu.add(exportSpectrumMenu);

        exportSpectrumValuesJMenuItem.setText("Spectrum as MGF");
        exportSpectrumValuesJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportSpectrumValuesJMenuItemActionPerformed(evt);
            }
        });
        exportGraphicsMenu.add(exportSpectrumValuesJMenuItem);

        annotationMenuBar.add(exportGraphicsMenu);

        splitterMenu6.setText("|");
        splitterMenu6.setEnabled(false);
        annotationMenuBar.add(splitterMenu6);

        helpJMenu.setText("Help");

        helpMenuItem.setText("Help");
        helpMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpMenuItemActionPerformed(evt);
            }
        });
        helpJMenu.add(helpMenuItem);

        annotationMenuBar.add(helpJMenu);

        splitterMenu7.setText("|");
        splitterMenu7.setEnabled(false);
        annotationMenuBar.add(splitterMenu7);

        setBackground(new java.awt.Color(255, 255, 255));

        projectsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("projects"));
        projectsPanel.setOpaque(false);
        projectsPanel.setPreferredSize(new java.awt.Dimension(306, 176));

        projectsScrollPane.setOpaque(false);

        projectsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                { new Integer(1), "A", "Project 1"},
                { new Integer(2), "B", "Project 2"},
                { new Integer(3), "B", "Project 3"},
                { new Integer(4), "C", "Project 4"}
            },
            new String [] {
                "", "Label", "Title"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        projectsTable.setPreferredSize(new java.awt.Dimension(150, 64));
        projectsScrollPane.setViewportView(projectsTable);

        javax.swing.GroupLayout projectsPanelLayout = new javax.swing.GroupLayout(projectsPanel);
        projectsPanel.setLayout(projectsPanelLayout);
        projectsPanelLayout.setHorizontalGroup(
            projectsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(projectsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(projectsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE)
                .addContainerGap())
        );
        projectsPanelLayout.setVerticalGroup(
            projectsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(projectsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(projectsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        experimentsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("experiments"));
        experimentsPanel.setOpaque(false);
        experimentsPanel.setPreferredSize(new java.awt.Dimension(306, 176));

        experimentsScrollPane.setOpaque(false);

        experimentsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                { new Integer(1), "Exp A"},
                { new Integer(2), "Exp B"},
                { new Integer(3), "Exp C"},
                { new Integer(4), "Exp D"}
            },
            new String [] {
                "", "Title"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        experimentsScrollPane.setViewportView(experimentsTable);

        javax.swing.GroupLayout experimentsPanelLayout = new javax.swing.GroupLayout(experimentsPanel);
        experimentsPanel.setLayout(experimentsPanelLayout);
        experimentsPanelLayout.setHorizontalGroup(
            experimentsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(experimentsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(experimentsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE)
                .addContainerGap())
        );
        experimentsPanelLayout.setVerticalGroup(
            experimentsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(experimentsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(experimentsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE)
                .addContainerGap())
        );

        samplesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("samples"));
        samplesPanel.setOpaque(false);
        samplesPanel.setPreferredSize(new java.awt.Dimension(306, 176));

        samplesScrollPane.setOpaque(false);

        samplesTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                { new Integer(1), "Sample A"},
                { new Integer(2), "Sample B"},
                { new Integer(3), "Sample C"},
                { new Integer(4), "Sample D"}
            },
            new String [] {
                "", "Name"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        samplesScrollPane.setViewportView(samplesTable);

        javax.swing.GroupLayout samplesPanelLayout = new javax.swing.GroupLayout(samplesPanel);
        samplesPanel.setLayout(samplesPanelLayout);
        samplesPanelLayout.setHorizontalGroup(
            samplesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(samplesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(samplesScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE)
                .addContainerGap())
        );
        samplesPanelLayout.setVerticalGroup(
            samplesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(samplesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(samplesScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE)
                .addContainerGap())
        );

        analyticalRunsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("analytical runs"));
        analyticalRunsPanel.setOpaque(false);
        analyticalRunsPanel.setPreferredSize(new java.awt.Dimension(306, 176));

        analyticalRunsScrollPane.setOpaque(false);

        analyticalRunsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                { new Integer(1), "Run 1"},
                { new Integer(2), "Run 2"},
                { new Integer(3), "Run 3"},
                { new Integer(4), "Run 4"}
            },
            new String [] {
                "", "Name"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        analyticalRunsScrollPane.setViewportView(analyticalRunsTable);

        javax.swing.GroupLayout analyticalRunsPanelLayout = new javax.swing.GroupLayout(analyticalRunsPanel);
        analyticalRunsPanel.setLayout(analyticalRunsPanelLayout);
        analyticalRunsPanelLayout.setHorizontalGroup(
            analyticalRunsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(analyticalRunsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(analyticalRunsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE)
                .addContainerGap())
        );
        analyticalRunsPanelLayout.setVerticalGroup(
            analyticalRunsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(analyticalRunsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(analyticalRunsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE)
                .addContainerGap())
        );

        psmPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("peptide spectrum matches"));
        psmPanel.setOpaque(false);

        psmScrollPane.setOpaque(false);

        psmTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                { new Integer(1), "Q10586", "ABC", "spectrum_1",  new Double(970.48),  new Integer(2),  new Double(639526.0),  new Double(1646.55402)},
                { new Integer(2), "Q10586", "ABC", "spectrum_2",  new Double(926.11),  new Integer(3),  new Double(973623.1875),  new Double(1646.74992)},
                { new Integer(3), "Q10586", "ABC", "spectrum_3",  new Double(899.42),  new Integer(2),  new Double(1608097.75),  new Double(1646.90436)},
                { new Integer(4), "Q10586", "ABC", "spectrum_4",  new Double(886.96),  new Integer(2),  new Double(615790.3125),  new Double(1647.13464)}
            },
            new String [] {
                "", "Accession", "Sequence", "Title", "m/z", "Charge", "Intensity", "RT"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.Integer.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        psmTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                psmTableMouseReleased(evt);
            }
        });
        psmScrollPane.setViewportView(psmTable);

        javax.swing.GroupLayout psmPanelLayout = new javax.swing.GroupLayout(psmPanel);
        psmPanel.setLayout(psmPanelLayout);
        psmPanelLayout.setHorizontalGroup(
            psmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(psmPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(psmScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 1011, Short.MAX_VALUE)
                .addContainerGap())
        );
        psmPanelLayout.setVerticalGroup(
            psmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(psmPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(psmScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE)
                .addContainerGap())
        );

        spectrumMainPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("spectrum"));
        spectrumMainPanel.setOpaque(false);

        spectrumContainerJPanel.setBackground(new java.awt.Color(255, 255, 255));

        spectrumJToolBar.setBackground(new java.awt.Color(255, 255, 255));
        spectrumJToolBar.setBorder(null);
        spectrumJToolBar.setFloatable(false);
        spectrumJToolBar.setRollover(true);
        spectrumJToolBar.setBorderPainted(false);

        spectrumAnnotationMenuPanel.setLayout(new javax.swing.BoxLayout(spectrumAnnotationMenuPanel, javax.swing.BoxLayout.LINE_AXIS));
        spectrumJToolBar.add(spectrumAnnotationMenuPanel);

        spectrumSplitPane.setBackground(new java.awt.Color(255, 255, 255));
        spectrumSplitPane.setBorder(null);
        spectrumSplitPane.setDividerLocation(80);
        spectrumSplitPane.setDividerSize(0);
        spectrumSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        secondarySpectrumPlotsJPanel.setOpaque(false);
        secondarySpectrumPlotsJPanel.setLayout(new javax.swing.BoxLayout(secondarySpectrumPlotsJPanel, javax.swing.BoxLayout.LINE_AXIS));
        spectrumSplitPane.setTopComponent(secondarySpectrumPlotsJPanel);

        spectrumOuterJPanel.setBackground(new java.awt.Color(255, 255, 255));

        spectrumPaddingPanel.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout spectrumPaddingPanelLayout = new javax.swing.GroupLayout(spectrumPaddingPanel);
        spectrumPaddingPanel.setLayout(spectrumPaddingPanelLayout);
        spectrumPaddingPanelLayout.setHorizontalGroup(
            spectrumPaddingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        spectrumPaddingPanelLayout.setVerticalGroup(
            spectrumPaddingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 17, Short.MAX_VALUE)
        );

        spectrumJPanel.setBackground(new java.awt.Color(255, 255, 255));
        spectrumJPanel.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout spectrumOuterJPanelLayout = new javax.swing.GroupLayout(spectrumOuterJPanel);
        spectrumOuterJPanel.setLayout(spectrumOuterJPanelLayout);
        spectrumOuterJPanelLayout.setHorizontalGroup(
            spectrumOuterJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(spectrumPaddingPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(spectrumJPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 1011, Short.MAX_VALUE)
        );
        spectrumOuterJPanelLayout.setVerticalGroup(
            spectrumOuterJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(spectrumOuterJPanelLayout.createSequentialGroup()
                .addComponent(spectrumPaddingPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spectrumJPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 386, Short.MAX_VALUE))
        );

        spectrumSplitPane.setRightComponent(spectrumOuterJPanel);

        javax.swing.GroupLayout spectrumContainerJPanelLayout = new javax.swing.GroupLayout(spectrumContainerJPanel);
        spectrumContainerJPanel.setLayout(spectrumContainerJPanelLayout);
        spectrumContainerJPanelLayout.setHorizontalGroup(
            spectrumContainerJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(spectrumContainerJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(spectrumJToolBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(spectrumSplitPane)
        );
        spectrumContainerJPanelLayout.setVerticalGroup(
            spectrumContainerJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, spectrumContainerJPanelLayout.createSequentialGroup()
                .addComponent(spectrumSplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 489, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spectrumJToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout spectrumMainPanelLayout = new javax.swing.GroupLayout(spectrumMainPanel);
        spectrumMainPanel.setLayout(spectrumMainPanelLayout);
        spectrumMainPanelLayout.setHorizontalGroup(
            spectrumMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1031, Short.MAX_VALUE)
            .addGroup(spectrumMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(spectrumMainPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(spectrumContainerJPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        spectrumMainPanelLayout.setVerticalGroup(
            spectrumMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(spectrumMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(spectrumMainPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(spectrumContainerJPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(projectsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(psmPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(samplesPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(analyticalRunsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(experimentsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spectrumMainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(psmPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(projectsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(experimentsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(samplesPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(analyticalRunsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(spectrumMainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void psmTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_psmTableMouseReleased
        int row = psmTable.getSelectedRow();

        if (row != -1) {
            this.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
            projectOverviewController.updateSpectrum();
            this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        }
    }//GEN-LAST:event_psmTableMouseReleased

    private void aIonCheckBoxMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aIonCheckBoxMenuItemActionPerformed
        updateAnnotationPreferences();
    }//GEN-LAST:event_aIonCheckBoxMenuItemActionPerformed

    private void bIonCheckBoxMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bIonCheckBoxMenuItemActionPerformed
        updateAnnotationPreferences();
    }//GEN-LAST:event_bIonCheckBoxMenuItemActionPerformed

    private void cIonCheckBoxMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cIonCheckBoxMenuItemActionPerformed
        updateAnnotationPreferences();
    }//GEN-LAST:event_cIonCheckBoxMenuItemActionPerformed

    private void xIonCheckBoxMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_xIonCheckBoxMenuItemActionPerformed
        updateAnnotationPreferences();
    }//GEN-LAST:event_xIonCheckBoxMenuItemActionPerformed

    private void yIonCheckBoxMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_yIonCheckBoxMenuItemActionPerformed
        updateAnnotationPreferences();
    }//GEN-LAST:event_yIonCheckBoxMenuItemActionPerformed

    private void zIonCheckBoxMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zIonCheckBoxMenuItemActionPerformed
        updateAnnotationPreferences();
    }//GEN-LAST:event_zIonCheckBoxMenuItemActionPerformed

    private void precursorCheckMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_precursorCheckMenuActionPerformed
        updateAnnotationPreferences();
    }//GEN-LAST:event_precursorCheckMenuActionPerformed

    private void immoniumIonsCheckMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_immoniumIonsCheckMenuActionPerformed
        updateAnnotationPreferences();
    }//GEN-LAST:event_immoniumIonsCheckMenuActionPerformed

    private void reporterIonsCheckMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reporterIonsCheckMenuActionPerformed
        updateAnnotationPreferences();
    }//GEN-LAST:event_reporterIonsCheckMenuActionPerformed

    private void adaptCheckBoxMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_adaptCheckBoxMenuItemActionPerformed
        updateAnnotationPreferences();
    }//GEN-LAST:event_adaptCheckBoxMenuItemActionPerformed

    private void forwardIonsDeNovoCheckBoxMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_forwardIonsDeNovoCheckBoxMenuItemActionPerformed
        updateAnnotationPreferences();
    }//GEN-LAST:event_forwardIonsDeNovoCheckBoxMenuItemActionPerformed

    private void rewindIonsDeNovoCheckBoxMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rewindIonsDeNovoCheckBoxMenuItemActionPerformed
        updateAnnotationPreferences();
    }//GEN-LAST:event_rewindIonsDeNovoCheckBoxMenuItemActionPerformed

    private void deNovoChargeOneJRadioButtonMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deNovoChargeOneJRadioButtonMenuItemActionPerformed
        updateAnnotationPreferences();
    }//GEN-LAST:event_deNovoChargeOneJRadioButtonMenuItemActionPerformed

    private void deNovoChargeTwoJRadioButtonMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deNovoChargeTwoJRadioButtonMenuItemActionPerformed
        updateAnnotationPreferences();
    }//GEN-LAST:event_deNovoChargeTwoJRadioButtonMenuItemActionPerformed

    private void allCheckBoxMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allCheckBoxMenuItemActionPerformed
        updateAnnotationPreferences();
    }//GEN-LAST:event_allCheckBoxMenuItemActionPerformed

    private void automaticAnnotationCheckBoxMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_automaticAnnotationCheckBoxMenuItemActionPerformed
        if (automaticAnnotationCheckBoxMenuItem.isSelected()) {
            adaptCheckBoxMenuItem.setSelected(true);
            try {
                //@todo which matching type should be used here?
                annotationPreferences.resetAutomaticAnnotation(AminoAcidPattern.MatchingType.string, searchParameters.getFragmentIonAccuracy());                
            } catch (Exception e) {
                e.printStackTrace(); // @TODO: add better exception handling
            }

            for (int availableCharge : chargeMenus.keySet()) {
                chargeMenus.get(availableCharge).setSelected(annotationPreferences.getValidatedCharges().contains(availableCharge));
            }
        }

        updateAnnotationPreferences();
    }//GEN-LAST:event_automaticAnnotationCheckBoxMenuItemActionPerformed

    private void errorPlotTypeCheckBoxMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_errorPlotTypeCheckBoxMenuItemActionPerformed
        useRelativeError = !errorPlotTypeCheckBoxMenuItem.isSelected();
        updateSpectrum();
    }//GEN-LAST:event_errorPlotTypeCheckBoxMenuItemActionPerformed

    private void exportSpectrumGraphicsJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportSpectrumGraphicsJMenuItemActionPerformed
        new ExportGraphicsDialog(colimsFrame, this, true, (Component) getSpectrum());
    }//GEN-LAST:event_exportSpectrumGraphicsJMenuItemActionPerformed

    private void exportSpectrumAndPlotsGraphicsJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportSpectrumAndPlotsGraphicsJMenuItemActionPerformed
        new ExportGraphicsDialog(colimsFrame, this, true, (Component) getSpectrumAndPlots());
    }//GEN-LAST:event_exportSpectrumAndPlotsGraphicsJMenuItemActionPerformed

    private void exportSequenceFragmentationGraphicsJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportSequenceFragmentationGraphicsJMenuItemActionPerformed
        new ExportGraphicsDialog(colimsFrame, this, true, (Component) getSequenceFragmentationPlot());
    }//GEN-LAST:event_exportSequenceFragmentationGraphicsJMenuItemActionPerformed

    private void exportIntensityHistogramGraphicsJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportIntensityHistogramGraphicsJMenuItemActionPerformed
        ChartPanel chartPanel = getIntensityHistogramPlot().getChartPanel();
        ChartPanel tempChartPanel = new ChartPanel(chartPanel.getChart());
        tempChartPanel.setBounds(new Rectangle(chartPanel.getBounds().width * 5, chartPanel.getBounds().height * 5));
        new ExportGraphicsDialog(colimsFrame, this, true, tempChartPanel);
    }//GEN-LAST:event_exportIntensityHistogramGraphicsJMenuItemActionPerformed

    private void exportMassErrorPlotGraphicsJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportMassErrorPlotGraphicsJMenuItemActionPerformed
        if (getMassErrorPlot() != null) {
            ChartPanel chartPanel = getMassErrorPlot().getChartPanel();
            ChartPanel tempChartPanel = new ChartPanel(chartPanel.getChart());
            tempChartPanel.setBounds(new Rectangle(chartPanel.getBounds().width * 5, chartPanel.getBounds().height * 5));
            new ExportGraphicsDialog(colimsFrame, this, true, tempChartPanel);
        } else {
            JOptionPane.showMessageDialog(this, "No mass error plot to export!", "Export Error", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_exportMassErrorPlotGraphicsJMenuItemActionPerformed

    private void exportSpectrumValuesJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportSpectrumValuesJMenuItemActionPerformed

        String spectrumAsMgf = getSpectrumAsMgf();

        if (spectrumAsMgf != null) {

            File selectedFile = getUserSelectedFile(".mgf", "(Mascot Generic Format) *.mgf", "Save As...", false);

            if (selectedFile != null) {
                try {
                    FileWriter w = new FileWriter(selectedFile);
                    BufferedWriter bw = new BufferedWriter(w);
                    bw.write(spectrumAsMgf);
                    bw.close();
                    w.close();

                    JOptionPane.showMessageDialog(this, "Spectrum saved to " + selectedFile.getPath() + ".",
                            "File Saved", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "An error occured while saving " + selectedFile.getPath() + ".\n"
                            + "See resources/PeptideShaker.log for details.", "Save Error", JOptionPane.WARNING_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_exportSpectrumValuesJMenuItemActionPerformed

    private void helpMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpMenuItemActionPerformed
        setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
        new HelpDialog(colimsFrame, getClass().getResource("/helpFiles/SpectrumPanel.html"),
                Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/help.GIF")),
                Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/peptide-shaker.gif")),
                "PeptideShaker - Help");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_helpMenuItemActionPerformed

    /**
     * Save the current annotation preferences selected in the annotation menus.
     */
    public void updateAnnotationPreferences() {

        annotationPreferences.clearIonTypes();
        if (aIonCheckBoxMenuItem.isSelected()) {
            annotationPreferences.addIonType(Ion.IonType.PEPTIDE_FRAGMENT_ION, PeptideFragmentIon.A_ION);
        }
        if (bIonCheckBoxMenuItem.isSelected()) {
            annotationPreferences.addIonType(Ion.IonType.PEPTIDE_FRAGMENT_ION, PeptideFragmentIon.B_ION);
        }
        if (cIonCheckBoxMenuItem.isSelected()) {
            annotationPreferences.addIonType(Ion.IonType.PEPTIDE_FRAGMENT_ION, PeptideFragmentIon.C_ION);
        }
        if (xIonCheckBoxMenuItem.isSelected()) {
            annotationPreferences.addIonType(Ion.IonType.PEPTIDE_FRAGMENT_ION, PeptideFragmentIon.X_ION);
        }
        if (yIonCheckBoxMenuItem.isSelected()) {
            annotationPreferences.addIonType(Ion.IonType.PEPTIDE_FRAGMENT_ION, PeptideFragmentIon.Y_ION);
        }
        if (zIonCheckBoxMenuItem.isSelected()) {
            annotationPreferences.addIonType(Ion.IonType.PEPTIDE_FRAGMENT_ION, PeptideFragmentIon.Z_ION);
        }
        if (precursorCheckMenu.isSelected()) {
            annotationPreferences.addIonType(Ion.IonType.PRECURSOR_ION);
        }
        if (immoniumIonsCheckMenu.isSelected()) {
            annotationPreferences.addIonType(Ion.IonType.IMMONIUM_ION);
        }
        if (reporterIonsCheckMenu.isSelected()) {
//            for (int subtype : getReporterIons()) { // @TODO: re-add reporter ions
//                annotationPreferences.addIonType(Ion.IonType.REPORTER_ION, subtype);
//            }
        }

        annotationPreferences.clearNeutralLosses();

        for (NeutralLoss neutralLoss : lossMenus.keySet()) {
            if (lossMenus.get(neutralLoss).isSelected()) {
                annotationPreferences.addNeutralLoss(neutralLoss);
            }
        }

        annotationPreferences.clearCharges();

        for (int charge : chargeMenus.keySet()) {
            if (chargeMenus.get(charge).isSelected()) {
                annotationPreferences.addSelectedCharge(charge);
            }
        }

        annotationPreferences.useAutomaticAnnotation(automaticAnnotationCheckBoxMenuItem.isSelected());
        annotationPreferences.setNeutralLossesSequenceDependant(adaptCheckBoxMenuItem.isSelected());

        annotationPreferences.setShowAllPeaks(allCheckBoxMenuItem.isSelected());

        annotationPreferences.setShowForwardIonDeNovoTags(forwardIonsDeNovoCheckBoxMenuItem.isSelected());
        annotationPreferences.setShowRewindIonDeNovoTags(rewindIonsDeNovoCheckBoxMenuItem.isSelected());

        if (deNovoChargeOneJRadioButtonMenuItem.isSelected()) {
            annotationPreferences.setDeNovoCharge(1);
        } else {
            annotationPreferences.setDeNovoCharge(2);
        }

        updateSpectrumAnnotations();
    }

    /**
     * Updates the spectrum annotation. Used when the user updates the
     * annotation accuracy.
     */
    public void updateSpectrum() {
        projectOverviewController.updateSpectrum();
    }

    /**
     * Returns the spectrum panel.
     *
     * @return the spectrum panel, or null if the spectrum tab is not enabled
     */
    public Component getSpectrum() {
        return (Component) spectrumJPanel.getComponent(0);
    }

    /**
     * Returns the extended spectrum panel.
     *
     * @return the extended spectrum panel, or null if the spectrum tab is not
     * enabled
     */
    public Component getSpectrumAndPlots() {
        return spectrumSplitPane;
    }

    /**
     * Returns the sequence fragmentation plot panel.
     *
     * @return the sequence fragmentation plot panel, or null if the spectrum
     * tab is not enabled
     */
    public Component getSequenceFragmentationPlot() {
        return (Component) secondarySpectrumPlotsJPanel.getComponent(0);
    }

    /**
     * Returns the intensity histogram plot panel.
     *
     * @return the intensity histogram plot panel, or null if the spectrum tab
     * is not enabled
     */
    public IntensityHistogram getIntensityHistogramPlot() {
        return (IntensityHistogram) secondarySpectrumPlotsJPanel.getComponent(1);
    }

    /**
     * Returns the mass error plot panel.
     *
     * @return the mass error plot panel, or null if the spectrum tab is not
     * enabled or the the mass error plot is not showing
     */
    public MassErrorPlot getMassErrorPlot() {
        return (MassErrorPlot) secondarySpectrumPlotsJPanel.getComponent(2);
    }

    /**
     * Returns the current spectrum as an mgf string.
     *
     * @return the current spectrum as an mgf string
     */
    public String getSpectrumAsMgf() {

        // @TODO: get the spectrum!
//        int[] selectedRows = psmTable.getSelectedRows();
//
//        if (selectedRows.length > 0) {
//
//            StringBuilder spectraAsMgf = new StringBuilder();
//
//            SelfUpdatingTableModel tableModel = (SelfUpdatingTableModel) psmTable.getModel();
//            for (int row : selectedRows) {
//                int psmIndex = tableModel.getViewIndex(row);
//                String spectrumKey = psmKeys.get(psmIndex);
//                MSnSpectrum currentSpectrum = peptideShakerGUI.getSpectrum(spectrumKey);
//                spectraAsMgf.append(currentSpectrum.asMgf());
//            }
//
//            return spectraAsMgf.toString();
//        }
        return null;
    }

    @Override
    public void setSelectedExportFolder(String selectedFolder) {
        // @TODO: implement me!
    }

    @Override
    public String getDefaultExportFolder() {
        return new File("user.home").getAbsolutePath(); // @TODO: implement me!
    }

    @Override
    public Image getNormalIcon() {
        return Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/compomics-utilities.png")); // @TODO: replace by colims icon
    }

    @Override
    public Image getWaitingIcon() {
        return Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/compomics-utilities.png")); // @TODO: replace by colims icon
    }

    /**
     * Returns the file selected by the user, or null if no file was selected.
     *
     * @param aFileEnding the file type, e.g., .txt
     * @param aFileFormatDescription the file format description, e.g., (Mascot
     * Generic Format) *.mgf
     * @param aDialogTitle the title for the dialog
     * @param openDialog if true an open dialog is shown, false results in a
     * save dialog
     * @return the file selected by the user, or null if no file or folder was
     * selected
     */
    public File getUserSelectedFile(String aFileEnding, String aFileFormatDescription, String aDialogTitle, boolean openDialog) {

        File selectedFile = Util.getUserSelectedFile(this, aFileEnding, aFileFormatDescription, aDialogTitle, lastSelectedFolder, openDialog);

        if (selectedFile != null) {
            if (selectedFile.isDirectory()) {
                lastSelectedFolder = selectedFile.getAbsolutePath();
            } else {
                lastSelectedFolder = selectedFile.getParentFile().getAbsolutePath();
            }
        }

        return selectedFile;
    }

    /**
     * Updates the annotations in the selected tab.
     */
    public void updateSpectrumAnnotations() {
        updateSpectrum();
    }

    /**
     * Update the annotation menu bar with the current annotation preferences.
     *
     * @param precursorCharge
     * @param peptide
     */
    public void updateAnnotationMenus(int precursorCharge, Peptide peptide) {

        aIonCheckBoxMenuItem.setSelected(false);
        bIonCheckBoxMenuItem.setSelected(false);
        cIonCheckBoxMenuItem.setSelected(false);
        xIonCheckBoxMenuItem.setSelected(false);
        yIonCheckBoxMenuItem.setSelected(false);
        zIonCheckBoxMenuItem.setSelected(false);
        precursorCheckMenu.setSelected(false);
        immoniumIonsCheckMenu.setSelected(false);
        reporterIonsCheckMenu.setSelected(false);

        for (Ion.IonType ionType : annotationPreferences.getIonTypes().keySet()) {
            if (ionType == Ion.IonType.IMMONIUM_ION) {
                immoniumIonsCheckMenu.setSelected(true);
            } else if (ionType == Ion.IonType.PRECURSOR_ION) {
                precursorCheckMenu.setSelected(true);
            } else if (ionType == Ion.IonType.REPORTER_ION) {
                reporterIonsCheckMenu.setSelected(true);
            } else if (ionType == Ion.IonType.PEPTIDE_FRAGMENT_ION) {
                for (int subtype : annotationPreferences.getIonTypes().get(ionType)) {
                    if (subtype == PeptideFragmentIon.A_ION) {
                        aIonCheckBoxMenuItem.setSelected(true);
                    } else if (subtype == PeptideFragmentIon.B_ION) {
                        bIonCheckBoxMenuItem.setSelected(true);
                    } else if (subtype == PeptideFragmentIon.C_ION) {
                        cIonCheckBoxMenuItem.setSelected(true);
                    } else if (subtype == PeptideFragmentIon.X_ION) {
                        xIonCheckBoxMenuItem.setSelected(true);
                    } else if (subtype == PeptideFragmentIon.Y_ION) {
                        yIonCheckBoxMenuItem.setSelected(true);
                    } else if (subtype == PeptideFragmentIon.Z_ION) {
                        zIonCheckBoxMenuItem.setSelected(true);
                    }
                }
            }
        }

        boolean selected;

        ArrayList<String> selectedLosses = new ArrayList<String>();

        for (JCheckBoxMenuItem lossMenuItem : lossMenus.values()) {

            if (lossMenuItem.isSelected()) {
                selectedLosses.add(lossMenuItem.getText());
            }

            lossMenu.remove(lossMenuItem);
        }

        lossMenu.setVisible(true);
        lossSplitter.setVisible(true);
        lossMenus.clear();

        HashMap<String, NeutralLoss> neutralLosses = new HashMap<String, NeutralLoss>();

        // add the general neutral losses
        for (NeutralLoss neutralLoss : IonFactory.getInstance().getDefaultNeutralLosses()) {
            neutralLosses.put(neutralLoss.name, neutralLoss);
        }

        // add the sequence specific neutral losses
        for (ModificationMatch modMatch : peptide.getModificationMatches()) {
            PTM ptm = ptmFactory.getPTM(modMatch.getTheoreticPtm());
            for (NeutralLoss neutralLoss : ptm.getNeutralLosses()) {
                neutralLosses.put(neutralLoss.name, neutralLoss);
            }
        }

        ArrayList<String> names = new ArrayList<String>(neutralLosses.keySet());
        Collections.sort(names);

        ArrayList<String> finalSelectedLosses = selectedLosses;

        if (names.isEmpty()) {
            lossMenu.setVisible(false);
            lossSplitter.setVisible(false);
        } else {
            for (int i = 0; i < names.size(); i++) {

                if (annotationPreferences.areNeutralLossesSequenceDependant()) {
                    selected = false;
                    for (NeutralLoss neutralLoss : annotationPreferences.getNeutralLosses().getAccountedNeutralLosses()) {
                        if (neutralLoss.isSameAs(neutralLoss)) {
                            selected = true;
                            break;
                        }
                    }
                } else {
                    selected = finalSelectedLosses.contains(names.get(i));
                }

                JCheckBoxMenuItem lossMenuItem = new JCheckBoxMenuItem(names.get(i));
                lossMenuItem.setSelected(selected);
                lossMenuItem.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        annotationPreferences.useAutomaticAnnotation(false);
                        annotationPreferences.setNeutralLossesSequenceDependant(false);
                        updateAnnotationPreferences();
                    }
                });
                lossMenus.put(neutralLosses.get(names.get(i)), lossMenuItem);
                lossMenu.add(lossMenuItem, i);
            }
        }

        ArrayList<String> selectedCharges = new ArrayList<String>();

        for (JCheckBoxMenuItem chargeMenuItem : chargeMenus.values()) {

            if (chargeMenuItem.isSelected()) {
                selectedCharges.add(chargeMenuItem.getText());
            }

            chargeMenu.remove(chargeMenuItem);
        }

        chargeMenus.clear();

        if (precursorCharge == 1) {
            precursorCharge = 2;
        }

        final ArrayList<String> finalSelectedCharges = selectedCharges;

        for (int charge = 1; charge < precursorCharge; charge++) {

            JCheckBoxMenuItem chargeMenuItem = new JCheckBoxMenuItem(charge + "+");

            if (annotationPreferences.useAutomaticAnnotation()) {
                chargeMenuItem.setSelected(annotationPreferences.getValidatedCharges().contains(charge));
            } else {
                if (finalSelectedCharges.contains(charge + "+")) {
                    chargeMenuItem.setSelected(true);
                } else {
                    chargeMenuItem.setSelected(false);
                }
            }

            chargeMenuItem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    annotationPreferences.useAutomaticAnnotation(false);
                    updateAnnotationPreferences();
                }
            });

            chargeMenus.put(charge, chargeMenuItem);
            chargeMenu.add(chargeMenuItem);
        }

        automaticAnnotationCheckBoxMenuItem.setSelected(annotationPreferences.useAutomaticAnnotation());
        adaptCheckBoxMenuItem.setSelected(annotationPreferences.areNeutralLossesSequenceDependant());

        // disable/enable the neutral loss options
        for (JCheckBoxMenuItem lossMenuItem : lossMenus.values()) {
            lossMenuItem.setEnabled(!annotationPreferences.areNeutralLossesSequenceDependant());
        }

        allCheckBoxMenuItem.setSelected(annotationPreferences.showAllPeaks());
    }

    /**
     * Returns the modified sequence as an tagged string with potential
     * modification sites color coded or with PTM tags, e.g, &lt;mox&gt;. /!\
     * This method will work only if the PTM found in the peptide are in the
     * PTMFactory. /!\ This method uses the modifications as set in the
     * modification matches of this peptide and displays all of them.
     *
     * @param peptide the peptide
     * @param useHtmlColorCoding if true, color coded HTML is used, otherwise
     * PTM tags, e.g, &lt;mox&gt;, are used
     * @param includeHtmlStartEndTag if true, start and end HTML tags are added
     * @param useShortName if true the short names are used in the tags
     * @return the tagged sequence as a string
     */
    public String getTaggedPeptideSequence(Peptide peptide, boolean useHtmlColorCoding, boolean includeHtmlStartEndTag, boolean useShortName) {

        HashMap<Integer, ArrayList<String>> mainModificationSites = new HashMap<Integer, ArrayList<String>>();
        HashMap<Integer, ArrayList<String>> secondaryModificationSites = new HashMap<Integer, ArrayList<String>>();
        HashMap<Integer, ArrayList<String>> fixedModificationSites = new HashMap<Integer, ArrayList<String>>();

        ModificationProfile modificationProfile = searchParameters.getModificationProfile();

        for (ModificationMatch modMatch : peptide.getModificationMatches()) {
            String modName = modMatch.getTheoreticPtm();

            if (ptmFactory.getPTM(modMatch.getTheoreticPtm()).getType() == PTM.MODAA) { // exclude terminal ptms

                int modSite = modMatch.getModificationSite();

                if (modMatch.isVariable()) {
                    if (modMatch.isConfident()) {
                        if (!mainModificationSites.containsKey(modSite)) {
                            mainModificationSites.put(modSite, new ArrayList<String>());
                        }
                        mainModificationSites.get(modSite).add(modName);
                    } else {
                        if (!secondaryModificationSites.containsKey(modSite)) {
                            secondaryModificationSites.put(modSite, new ArrayList<String>());
                        }
                        secondaryModificationSites.get(modSite).add(modName);
                    }
                } else {
                    if (!fixedModificationSites.containsKey(modSite)) {
                        fixedModificationSites.put(modSite, new ArrayList<String>());
                    }
                    fixedModificationSites.get(modSite).add(modName);
                }
            }

        }

        return Peptide.getTaggedModifiedSequence(modificationProfile, peptide, mainModificationSites,
                secondaryModificationSites, fixedModificationSites, useHtmlColorCoding, includeHtmlStartEndTag, useShortName);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBoxMenuItem aIonCheckBoxMenuItem;
    private javax.swing.JCheckBoxMenuItem adaptCheckBoxMenuItem;
    private javax.swing.JCheckBoxMenuItem allCheckBoxMenuItem;
    private javax.swing.JPanel analyticalRunsPanel;
    private javax.swing.JScrollPane analyticalRunsScrollPane;
    private javax.swing.JTable analyticalRunsTable;
    private javax.swing.JMenuBar annotationMenuBar;
    private javax.swing.JCheckBoxMenuItem automaticAnnotationCheckBoxMenuItem;
    private javax.swing.JCheckBoxMenuItem bIonCheckBoxMenuItem;
    private javax.swing.JCheckBoxMenuItem cIonCheckBoxMenuItem;
    private javax.swing.JMenu chargeMenu;
    private javax.swing.JRadioButtonMenuItem deNovoChargeOneJRadioButtonMenuItem;
    private javax.swing.JRadioButtonMenuItem deNovoChargeTwoJRadioButtonMenuItem;
    private javax.swing.JMenu deNovoMenu;
    private javax.swing.JCheckBoxMenuItem errorPlotTypeCheckBoxMenuItem;
    private javax.swing.JPanel experimentsPanel;
    private javax.swing.JScrollPane experimentsScrollPane;
    private javax.swing.JTable experimentsTable;
    private javax.swing.JMenu exportGraphicsMenu;
    private javax.swing.JMenuItem exportIntensityHistogramGraphicsJMenuItem;
    private javax.swing.JMenuItem exportMassErrorPlotGraphicsJMenuItem;
    private javax.swing.JMenuItem exportSequenceFragmentationGraphicsJMenuItem;
    private javax.swing.JMenuItem exportSpectrumAndPlotsGraphicsJMenuItem;
    private javax.swing.JMenuItem exportSpectrumGraphicsJMenuItem;
    private javax.swing.JPopupMenu.Separator exportSpectrumGraphicsSeparator;
    private javax.swing.JMenu exportSpectrumMenu;
    private javax.swing.JMenuItem exportSpectrumValuesJMenuItem;
    private javax.swing.JCheckBoxMenuItem forwardIonsDeNovoCheckBoxMenuItem;
    private javax.swing.JMenu helpJMenu;
    private javax.swing.JMenuItem helpMenuItem;
    private javax.swing.JCheckBoxMenuItem immoniumIonsCheckMenu;
    private javax.swing.JMenu ionsMenu;
    private javax.swing.JPopupMenu.Separator jSeparator19;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JPopupMenu.Separator jSeparator7;
    private javax.swing.JMenu lossMenu;
    private javax.swing.JMenu lossSplitter;
    private javax.swing.JMenu otherMenu;
    private javax.swing.JCheckBoxMenuItem precursorCheckMenu;
    private javax.swing.JPanel projectsPanel;
    private javax.swing.JScrollPane projectsScrollPane;
    private javax.swing.JTable projectsTable;
    private javax.swing.JPanel psmPanel;
    private javax.swing.JScrollPane psmScrollPane;
    private javax.swing.JTable psmTable;
    private javax.swing.JCheckBoxMenuItem reporterIonsCheckMenu;
    private javax.swing.JCheckBoxMenuItem rewindIonsDeNovoCheckBoxMenuItem;
    private javax.swing.JPanel samplesPanel;
    private javax.swing.JScrollPane samplesScrollPane;
    private javax.swing.JTable samplesTable;
    private javax.swing.JPanel secondarySpectrumPlotsJPanel;
    private javax.swing.JMenu settingsMenu;
    private javax.swing.JPanel spectrumAnnotationMenuPanel;
    private javax.swing.JPanel spectrumContainerJPanel;
    private javax.swing.JPanel spectrumJPanel;
    private javax.swing.JToolBar spectrumJToolBar;
    private javax.swing.JPanel spectrumMainPanel;
    private javax.swing.JPanel spectrumOuterJPanel;
    private javax.swing.JPanel spectrumPaddingPanel;
    private javax.swing.JSplitPane spectrumSplitPane;
    private javax.swing.JMenu splitterMenu2;
    private javax.swing.JMenu splitterMenu3;
    private javax.swing.JMenu splitterMenu4;
    private javax.swing.JMenu splitterMenu5;
    private javax.swing.JMenu splitterMenu6;
    private javax.swing.JMenu splitterMenu7;
    private javax.swing.JMenu splitterMenu8;
    private javax.swing.JMenu splitterMenu9;
    private javax.swing.JCheckBoxMenuItem xIonCheckBoxMenuItem;
    private javax.swing.JCheckBoxMenuItem yIonCheckBoxMenuItem;
    private javax.swing.JCheckBoxMenuItem zIonCheckBoxMenuItem;
    // End of variables declaration//GEN-END:variables
}
