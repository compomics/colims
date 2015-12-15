package com.compomics.colims.client.view;

import com.compomics.colims.client.controller.ProjectOverviewController;
import com.compomics.util.experiment.biology.*;
import com.compomics.util.experiment.biology.ions.PeptideFragmentIon;
import com.compomics.util.experiment.identification.identification_parameters.PtmSettings;
import com.compomics.util.experiment.identification.identification_parameters.SearchParameters;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.compomics.util.experiment.identification.spectrum_annotation.AnnotationSettings;
import com.compomics.util.gui.error_handlers.HelpDialog;
import com.compomics.util.preferences.UtilitiesUserPreferences;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Niels Hulstaert
 */
public class ProjectOverviewPanel extends javax.swing.JPanel {

    /**
     * Turns of the gradient painting for the bar charts.
     */
    static {
        XYBarRenderer.setDefaultBarPainter(new StandardXYBarPainter());
    }

    /**
     * The parent frame.
     */
    private JFrame mainFrame;
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
    private AnnotationSettings annotationSettings = new AnnotationSettings(); // @TODO: set the preferences
    /**
     * The neutral loss menus.
     */
    private HashMap<NeutralLoss, JCheckBoxMenuItem> lossMenus = new HashMap<>();
    /**
     * The charge menus.
     */
    private HashMap<Integer, JCheckBoxMenuItem> chargeMenus = new HashMap<>();
    /**
     * The current search parameters.
     */
    private SearchParameters searchParameters = new SearchParameters(); // @TODO: get from the given project
    /**
     * If true the relative error (ppm) is used instead of the absolute error (Da).
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
     * The label with for the numbers in the jsparklines columns.
     */
    private int labelWidth = 50;

    /**
     * Creates new form ProjectOverviewPanel1
     */
    public ProjectOverviewPanel(final JFrame mainFrame, final ProjectOverviewController projectOverviewController, final UtilitiesUserPreferences utilitiesUserPreferences) {
        this.mainFrame = mainFrame;
        this.projectOverviewController = projectOverviewController;
        this.utilitiesUserPreferences = utilitiesUserPreferences;

        initComponents();

        // @TODO: these should be set according to the current selection
        annotationSettings.setFragmentIonAccuracy(0.02);
        annotationSettings.addIonType(Ion.IonType.PEPTIDE_FRAGMENT_ION, PeptideFragmentIon.B_ION);
        annotationSettings.addIonType(Ion.IonType.PEPTIDE_FRAGMENT_ION, PeptideFragmentIon.Y_ION);
        annotationSettings.addIonType(Ion.IonType.IMMONIUM_ION);
        // @todo update to latest utilities version
//        annotationSettings.addSelectedCharge(1);

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

    public AnnotationSettings getAnnotationSettings() {
        return annotationSettings;
    }

    public SearchParameters getSearchParameters() {
        return searchParameters;
    }

    public JButton getNextPageSpectra() {
        return nextPageSpectra;
    }

    public JButton getPrevPageSpectra() {
        return prevPageSpectra;
    }

    public JButton getFirstPageSpectra() {
        return firstPageSpectra;
    }

    public JButton getLastPageSpectra() {
        return lastPageSpectra;
    }

    public JTextField getFilterSpectra() {
        return filterSpectra;
    }

    public JLabel getPageLabelSpectra() {
        return pageLabelSpectra;
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
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        annotationMenuBar = new javax.swing.JMenuBar();
        otherMenu = new javax.swing.JMenu();
        lossSplitter = new javax.swing.JMenu();
        lossMenu = new javax.swing.JMenu();
        adaptCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        forwardIonsDeNovoCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        rewindIonsDeNovoCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        deNovoChargeOneJRadioButtonMenuItem = new javax.swing.JRadioButtonMenuItem();
        deNovoChargeTwoJRadioButtonMenuItem = new javax.swing.JRadioButtonMenuItem();
        settingsMenu = new javax.swing.JMenu();
        allCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        automaticAnnotationCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        errorPlotTypeCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        splitterMenu4 = new javax.swing.JMenu();
        helpJMenu = new javax.swing.JMenu();
        helpMenuItem = new javax.swing.JMenuItem();
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
        nextPageSpectra = new javax.swing.JButton();
        prevPageSpectra = new javax.swing.JButton();
        firstPageSpectra = new javax.swing.JButton();
        lastPageSpectra = new javax.swing.JButton();
        filterSpectra = new javax.swing.JTextField();
        pageLabelSpectra = new javax.swing.JLabel();
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

        settingsMenu.setText("Settings");
//
//        allCheckBoxMenuItem.setText("Show All Peaks");
//        allCheckBoxMenuItem.setToolTipText("Show all peaks or just the annotated peaks");
//        allCheckBoxMenuItem.addActionListener(evt -> allCheckBoxMenuItemActionPerformed(evt));
//        settingsMenu.add(allCheckBoxMenuItem);
//        settingsMenu.add(jSeparator5);
//
//        automaticAnnotationCheckBoxMenuItem.setSelected(true);
//        automaticAnnotationCheckBoxMenuItem.setText("Automatic Annotation");
//        automaticAnnotationCheckBoxMenuItem.setToolTipText("Use automatic annotation");
//        automaticAnnotationCheckBoxMenuItem.addActionListener(evt -> automaticAnnotationCheckBoxMenuItemActionPerformed(evt));
//        settingsMenu.add(automaticAnnotationCheckBoxMenuItem);

        errorPlotTypeCheckBoxMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        errorPlotTypeCheckBoxMenuItem.setSelected(true);
        errorPlotTypeCheckBoxMenuItem.setText("Absolute Mass Error Plot");
        errorPlotTypeCheckBoxMenuItem.setToolTipText("Plot the mass error in Da or ppm ");
        errorPlotTypeCheckBoxMenuItem.addActionListener(this::errorPlotTypeCheckBoxMenuItemActionPerformed);
        settingsMenu.add(errorPlotTypeCheckBoxMenuItem);

        annotationMenuBar.add(settingsMenu);

        splitterMenu4.setText("|");
        splitterMenu4.setEnabled(false);
        annotationMenuBar.add(splitterMenu4);

        helpJMenu.setText("Help");

        helpMenuItem.setText("Help");
        helpMenuItem.addActionListener(evt -> helpMenuItemActionPerformed(evt));
        helpJMenu.add(helpMenuItem);

        annotationMenuBar.add(helpJMenu);

        setBackground(new java.awt.Color(255, 255, 255));

        projectsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Projects"));
        projectsPanel.setOpaque(false);
        projectsPanel.setPreferredSize(new java.awt.Dimension(306, 176));

        projectsScrollPane.setOpaque(false);

        projectsTable.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{
                        {1, "A", "Project 1"},
                        {2, "B", "Project 2"},
                        {3, "B", "Project 3"},
                        {4, "C", "Project 4"}
                },
                new String[]{
                        "", "Label", "Title"
                }
        ) {
            Class[] types = new Class[]{
                    java.lang.Integer.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean[]{
                    false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
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
                                .addComponent(projectsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE)
                                .addContainerGap())
        );
        projectsPanelLayout.setVerticalGroup(
                projectsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(projectsPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(projectsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                .addContainerGap())
        );

        experimentsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Project Experiments"));
        experimentsPanel.setOpaque(false);
        experimentsPanel.setPreferredSize(new java.awt.Dimension(306, 176));

        experimentsScrollPane.setOpaque(false);

        experimentsTable.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{
                        {1, "Exp A"},
                        {2, "Exp B"},
                        {3, "Exp C"},
                        {4, "Exp D"}
                },
                new String[]{
                        "", "Title"
                }
        ) {
            Class[] types = new Class[]{
                    java.lang.Integer.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean[]{
                    false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        experimentsScrollPane.setViewportView(experimentsTable);

        javax.swing.GroupLayout experimentsPanelLayout = new javax.swing.GroupLayout(experimentsPanel);
        experimentsPanel.setLayout(experimentsPanelLayout);
        experimentsPanelLayout.setHorizontalGroup(
                experimentsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(experimentsPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(experimentsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE)
                                .addContainerGap())
        );
        experimentsPanelLayout.setVerticalGroup(
                experimentsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(experimentsPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(experimentsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)
                                .addContainerGap())
        );

        samplesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Experiment samples"));
        samplesPanel.setOpaque(false);
        samplesPanel.setPreferredSize(new java.awt.Dimension(306, 176));

        samplesScrollPane.setOpaque(false);

        samplesTable.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{
                        {1, "Sample A"},
                        {2, "Sample B"},
                        {3, "Sample C"},
                        {4, "Sample D"}
                },
                new String[]{
                        "", "Name"
                }
        ) {
            Class[] types = new Class[]{
                    java.lang.Integer.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean[]{
                    false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        samplesScrollPane.setViewportView(samplesTable);

        javax.swing.GroupLayout samplesPanelLayout = new javax.swing.GroupLayout(samplesPanel);
        samplesPanel.setLayout(samplesPanelLayout);
        samplesPanelLayout.setHorizontalGroup(
                samplesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(samplesPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(samplesScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE)
                                .addContainerGap())
        );
        samplesPanelLayout.setVerticalGroup(
                samplesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(samplesPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(samplesScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)
                                .addContainerGap())
        );

        analyticalRunsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Sample analytical runs"));
        analyticalRunsPanel.setOpaque(false);
        analyticalRunsPanel.setPreferredSize(new java.awt.Dimension(306, 176));

        analyticalRunsScrollPane.setOpaque(false);

        analyticalRunsTable.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{
                        {1, "Run 1"},
                        {2, "Run 2"},
                        {3, "Run 3"},
                        {4, "Run 4"}
                },
                new String[]{
                        "", "Name"
                }
        ) {
            Class[] types = new Class[]{
                    java.lang.Integer.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean[]{
                    false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        analyticalRunsScrollPane.setViewportView(analyticalRunsTable);

        javax.swing.GroupLayout analyticalRunsPanelLayout = new javax.swing.GroupLayout(analyticalRunsPanel);
        analyticalRunsPanel.setLayout(analyticalRunsPanelLayout);
        analyticalRunsPanelLayout.setHorizontalGroup(
                analyticalRunsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(analyticalRunsPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(analyticalRunsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE)
                                .addContainerGap())
        );
        analyticalRunsPanelLayout.setVerticalGroup(
                analyticalRunsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(analyticalRunsPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(analyticalRunsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
                                .addContainerGap())
        );

        psmPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Peptide spectrum matches"));
        psmPanel.setOpaque(false);

        psmScrollPane.setOpaque(false);

        psmTable.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{
                        {new Integer(1), "Q10586", "ABC", "spectrum_1", new Double(970.48), new Integer(2), new Double(639526.0), new Double(1646.55402)},
                        {new Integer(2), "Q10586", "ABC", "spectrum_2", new Double(926.11), new Integer(3), new Double(973623.1875), new Double(1646.74992)},
                        {new Integer(3), "Q10586", "ABC", "spectrum_3", new Double(899.42), new Integer(2), new Double(1608097.75), new Double(1646.90436)},
                        {new Integer(4), "Q10586", "ABC", "spectrum_4", new Double(886.96), new Integer(2), new Double(615790.3125), new Double(1647.13464)}
                },
                new String[]{
                        "", "Accession", "Sequence", "Title", "m/z", "Charge", "Intensity", "RT"
                }
        ) {
            Class[] types = new Class[]{
                    java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.Integer.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean[]{
                    false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        psmTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                psmTableMouseReleased(evt);
            }
        });
        psmScrollPane.setViewportView(psmTable);

        nextPageSpectra.setText(">");

        prevPageSpectra.setText("<");
        prevPageSpectra.setEnabled(false);

        firstPageSpectra.setText("<<");
        firstPageSpectra.setEnabled(false);

        lastPageSpectra.setText(">>");

        filterSpectra.addActionListener(evt -> filterSpectraActionPerformed(evt));

        javax.swing.GroupLayout psmPanelLayout = new javax.swing.GroupLayout(psmPanel);
        psmPanel.setLayout(psmPanelLayout);
        psmPanelLayout.setHorizontalGroup(
                psmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(psmPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(psmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(psmScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 1011, Short.MAX_VALUE)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, psmPanelLayout.createSequentialGroup()
                                                .addGap(0, 0, Short.MAX_VALUE)
                                                .addGroup(psmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(filterSpectra, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, psmPanelLayout.createSequentialGroup()
                                                                .addComponent(pageLabelSpectra)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(firstPageSpectra)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(prevPageSpectra)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(nextPageSpectra)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(lastPageSpectra)))))
                                .addContainerGap())
        );
        psmPanelLayout.setVerticalGroup(
                psmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(psmPanelLayout.createSequentialGroup()
                                .addComponent(filterSpectra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(psmScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 91, Short.MAX_VALUE)
                                .addGap(16, 16, 16)
                                .addGroup(psmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(nextPageSpectra)
                                        .addComponent(prevPageSpectra)
                                        .addComponent(firstPageSpectra)
                                        .addComponent(lastPageSpectra)
                                        .addComponent(pageLabelSpectra)))
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
                                .addComponent(spectrumJPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE))
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
                        .addGap(0, 1041, Short.MAX_VALUE)
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
                                        .addComponent(projectsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(experimentsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(samplesPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(analyticalRunsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 181, Short.MAX_VALUE))
                                        .addComponent(spectrumMainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void psmTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_psmTableMouseReleased

    }//GEN-LAST:event_psmTableMouseReleased

    private void allCheckBoxMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allCheckBoxMenuItemActionPerformed
    }//GEN-LAST:event_allCheckBoxMenuItemActionPerformed

    private void automaticAnnotationCheckBoxMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_automaticAnnotationCheckBoxMenuItemActionPerformed
    }//GEN-LAST:event_automaticAnnotationCheckBoxMenuItemActionPerformed

    private void errorPlotTypeCheckBoxMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_errorPlotTypeCheckBoxMenuItemActionPerformed
        useRelativeError = !errorPlotTypeCheckBoxMenuItem.isSelected();
        updateSpectrum();
    }//GEN-LAST:event_errorPlotTypeCheckBoxMenuItemActionPerformed

    private void helpMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpMenuItemActionPerformed
        setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
        new HelpDialog(mainFrame, getClass().getResource("/helpFiles/SpectrumPanel.html"),
                Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/help.GIF")),
                Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/peptide-shaker.gif")),
                "PeptideShaker - Help");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_helpMenuItemActionPerformed

    private void filterSpectraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterSpectraActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_filterSpectraActionPerformed

    /**
     * Updates the spectrum annotation. Used when the user updates the annotation accuracy.
     */
    public void updateSpectrum() {
        projectOverviewController.updatePsm();
    }

    /**
     * Updates the annotations in the selected tab.
     */
    public void updateSpectrumAnnotations() {
        updateSpectrum();
    }

    /**
     * Returns the modified sequence as an tagged string with potential modification sites color coded or with PTM tags,
     * e.g, &lt;mox&gt;. /!\ This method will work only if the PTM found in the peptide are in the PTMFactory. /!\ This
     * method uses the modifications as set in the modification matches of this peptide and displays all of them.
     *
     * @param peptide                the peptide
     * @param useHtmlColorCoding     if true, color coded HTML is used, otherwise PTM tags, e.g, &lt;mox&gt;, are used
     * @param includeHtmlStartEndTag if true, start and end HTML tags are added
     * @param useShortName           if true the short names are used in the tags
     * @return the tagged sequence as a string
     */
    public String getTaggedPeptideSequence(Peptide peptide, boolean useHtmlColorCoding, boolean includeHtmlStartEndTag, boolean useShortName) {

        HashMap<Integer, ArrayList<String>> confidentLocations = new HashMap<>();
        HashMap<Integer, ArrayList<String>> representativeAmbiguousLocations = new HashMap<>();
        HashMap<Integer, ArrayList<String>> secondaryAmbiguousLocations = new HashMap<>();
        HashMap<Integer, ArrayList<String>> fixedModifications = new HashMap<>();

        PtmSettings ptmSettings = searchParameters.getPtmSettings();

        for (ModificationMatch modMatch : peptide.getModificationMatches()) {
            String modName = modMatch.getTheoreticPtm();

            if (ptmFactory.getPTM(modMatch.getTheoreticPtm()).getType() == PTM.MODAA) { // exclude terminal ptms

                int modSite = modMatch.getModificationSite();

                if (modMatch.isVariable()) {
                    if (modMatch.isConfident()) {
                        if (!confidentLocations.containsKey(modSite)) {
                            confidentLocations.put(modSite, new ArrayList<>());
                        }
                        confidentLocations.get(modSite).add(modName);
                    } else {
                        if (!secondaryAmbiguousLocations.containsKey(modSite)) {
                            secondaryAmbiguousLocations.put(modSite, new ArrayList<>());
                        }
                        secondaryAmbiguousLocations.get(modSite).add(modName);
                    }
                } else {
                    if (!fixedModifications.containsKey(modSite)) {
                        fixedModifications.put(modSite, new ArrayList<>());
                    }
                    fixedModifications.get(modSite).add(modName);
                }
            }

        }

        return Peptide.getTaggedModifiedSequence(ptmSettings, peptide, confidentLocations, representativeAmbiguousLocations,
                secondaryAmbiguousLocations, fixedModifications, useHtmlColorCoding, includeHtmlStartEndTag, useShortName);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBoxMenuItem adaptCheckBoxMenuItem;
    private javax.swing.JCheckBoxMenuItem allCheckBoxMenuItem;
    private javax.swing.JPanel analyticalRunsPanel;
    private javax.swing.JScrollPane analyticalRunsScrollPane;
    private javax.swing.JTable analyticalRunsTable;
    private javax.swing.JMenuBar annotationMenuBar;
    private javax.swing.JCheckBoxMenuItem automaticAnnotationCheckBoxMenuItem;
    private javax.swing.JRadioButtonMenuItem deNovoChargeOneJRadioButtonMenuItem;
    private javax.swing.JRadioButtonMenuItem deNovoChargeTwoJRadioButtonMenuItem;
    private javax.swing.JCheckBoxMenuItem errorPlotTypeCheckBoxMenuItem;
    private javax.swing.JPanel experimentsPanel;
    private javax.swing.JScrollPane experimentsScrollPane;
    private javax.swing.JTable experimentsTable;
    private javax.swing.JTextField filterSpectra;
    private javax.swing.JButton firstPageSpectra;
    private javax.swing.JCheckBoxMenuItem forwardIonsDeNovoCheckBoxMenuItem;
    private javax.swing.JMenu helpJMenu;
    private javax.swing.JMenuItem helpMenuItem;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JButton lastPageSpectra;
    private javax.swing.JMenu lossMenu;
    private javax.swing.JMenu lossSplitter;
    private javax.swing.JButton nextPageSpectra;
    private javax.swing.JMenu otherMenu;
    private javax.swing.JLabel pageLabelSpectra;
    private javax.swing.JButton prevPageSpectra;
    private javax.swing.JPanel projectsPanel;
    private javax.swing.JScrollPane projectsScrollPane;
    private javax.swing.JTable projectsTable;
    private javax.swing.JPanel psmPanel;
    private javax.swing.JScrollPane psmScrollPane;
    private javax.swing.JTable psmTable;
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
    private javax.swing.JMenu splitterMenu4;
    // End of variables declaration//GEN-END:variables
}
