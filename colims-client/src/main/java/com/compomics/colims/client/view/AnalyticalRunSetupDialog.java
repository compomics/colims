package com.compomics.colims.client.view;

import java.awt.Color;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTable;

/**
 *
 * @author Niels Hulstaert
 */
public class AnalyticalRunSetupDialog extends javax.swing.JDialog {

    /**
     * Creates new form AnalyticalRunSetupDialog
     *
     * @param parent the dialog parent
     * @param modal the modal boolean
     */
    public AnalyticalRunSetupDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);

        initComponents();

        projectsScrollPane.getViewport().setOpaque(false);
        experimentsScrollPane.getViewport().setOpaque(false);
        samplesScrollPane.getViewport().setOpaque(false);
        this.getContentPane().setBackground(Color.WHITE);
    }

    public JButton getBackButton() {
        return backButton;
    }

    public JButton getProceedButton() {
        return proceedButton;
    }

    public JPanel getTopPanel() {
        return topPanel;
    }

    public JButton getFinishButton() {
        return finishButton;
    }

    public JLabel getInfoLabel() {
        return infoLabel;
    }

    public JTable getExperimentsTable() {
        return experimentsTable;
    }

    public JTable getProjectsTable() {
        return projectsTable;
    }

    public JTable getSamplesTable() {
        return samplesTable;
    }

    public JButton getCloseButton() {
        return closeButton;
    }

    public ButtonGroup getDataTypeButtonGroup() {
        return dataTypeButtonGroup;
    }

    public JRadioButton getMaxQuantRadioButton() {
        return maxQuantRadioButton;
    }

    public JRadioButton getPeptideShakerRadioButton() {
        return peptideShakerRadioButton;
    }

    public PeptideShakerDataImportPanel getPeptideShakerDataImportPanel() {
        return peptideShakerDataImportPanel;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dataTypeButtonGroup = new javax.swing.ButtonGroup();
        parentPanel = new javax.swing.JPanel();
        topPanel = new javax.swing.JPanel();
        sampleSelectionPanel = new javax.swing.JPanel();
        projectsPanel = new javax.swing.JPanel();
        projectsScrollPane = new javax.swing.JScrollPane();
        projectsTable = new javax.swing.JTable();
        experimentsPanel = new javax.swing.JPanel();
        experimentsScrollPane = new javax.swing.JScrollPane();
        experimentsTable = new javax.swing.JTable();
        samplesPanel = new javax.swing.JPanel();
        samplesScrollPane = new javax.swing.JScrollPane();
        samplesTable = new javax.swing.JTable();
        dataTypeSelectionPanel = new javax.swing.JPanel();
        dataTypeSelectionLabel = new javax.swing.JLabel();
        peptideShakerRadioButton = new javax.swing.JRadioButton();
        maxQuantRadioButton = new javax.swing.JRadioButton();
        peptideShakerDataImportPanel = new com.compomics.colims.client.view.PeptideShakerDataImportPanel();
        testPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        testPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        bottomPanel = new javax.swing.JPanel();
        backButton = new javax.swing.JButton();
        proceedButton = new javax.swing.JButton();
        infoLabel = new javax.swing.JLabel();
        finishButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("analytical run setup");

        parentPanel.setOpaque(false);

        topPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        topPanel.setOpaque(false);
        topPanel.setPreferredSize(new java.awt.Dimension(400, 285));
        topPanel.setLayout(new java.awt.CardLayout());

        sampleSelectionPanel.setName("sampleSelectionPanel"); // NOI18N
        sampleSelectionPanel.setOpaque(false);

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
                .addComponent(projectsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)
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
                .addComponent(experimentsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 302, Short.MAX_VALUE)
                .addContainerGap())
        );
        experimentsPanelLayout.setVerticalGroup(
            experimentsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(experimentsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(experimentsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
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
                .addComponent(samplesScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)
                .addContainerGap())
        );
        samplesPanelLayout.setVerticalGroup(
            samplesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(samplesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(samplesScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout sampleSelectionPanelLayout = new javax.swing.GroupLayout(sampleSelectionPanel);
        sampleSelectionPanel.setLayout(sampleSelectionPanelLayout);
        sampleSelectionPanelLayout.setHorizontalGroup(
            sampleSelectionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sampleSelectionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(projectsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(experimentsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(samplesPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 335, Short.MAX_VALUE)
                .addContainerGap())
        );
        sampleSelectionPanelLayout.setVerticalGroup(
            sampleSelectionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, sampleSelectionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(sampleSelectionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(projectsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE)
                    .addComponent(experimentsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE)
                    .addComponent(samplesPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE))
                .addContainerGap())
        );

        topPanel.add(sampleSelectionPanel, "sampleSelectionPanel");

        dataTypeSelectionPanel.setName("dataTypeSelectionPanel"); // NOI18N
        dataTypeSelectionPanel.setOpaque(false);

        dataTypeSelectionLabel.setText("Select the analytical run data type");

        dataTypeButtonGroup.add(peptideShakerRadioButton);
        peptideShakerRadioButton.setText("PeptideShaker");
        peptideShakerRadioButton.setOpaque(false);

        dataTypeButtonGroup.add(maxQuantRadioButton);
        maxQuantRadioButton.setText("MaxQuant");
        maxQuantRadioButton.setOpaque(false);

        javax.swing.GroupLayout dataTypeSelectionPanelLayout = new javax.swing.GroupLayout(dataTypeSelectionPanel);
        dataTypeSelectionPanel.setLayout(dataTypeSelectionPanelLayout);
        dataTypeSelectionPanelLayout.setHorizontalGroup(
            dataTypeSelectionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dataTypeSelectionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(dataTypeSelectionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(maxQuantRadioButton)
                    .addComponent(peptideShakerRadioButton)
                    .addComponent(dataTypeSelectionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(855, Short.MAX_VALUE))
        );
        dataTypeSelectionPanelLayout.setVerticalGroup(
            dataTypeSelectionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dataTypeSelectionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(dataTypeSelectionLabel)
                .addGap(18, 18, 18)
                .addComponent(peptideShakerRadioButton)
                .addGap(18, 18, 18)
                .addComponent(maxQuantRadioButton)
                .addContainerGap(129, Short.MAX_VALUE))
        );

        topPanel.add(dataTypeSelectionPanel, "dataTypeSelectionPanel");

        peptideShakerDataImportPanel.setName("peptideShakerDataImportPanel"); // NOI18N
        topPanel.add(peptideShakerDataImportPanel, "peptideShakerDataImportPanel");

        testPanel.setName("testPanel"); // NOI18N

        jLabel1.setText("testPanel1");

        javax.swing.GroupLayout testPanelLayout = new javax.swing.GroupLayout(testPanel);
        testPanel.setLayout(testPanelLayout);
        testPanelLayout.setHorizontalGroup(
            testPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(testPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(786, Short.MAX_VALUE))
        );
        testPanelLayout.setVerticalGroup(
            testPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(testPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(211, Short.MAX_VALUE))
        );

        topPanel.add(testPanel, "testPanel");

        testPanel2.setName("testPanel2"); // NOI18N

        jLabel2.setText("testPanel2");

        javax.swing.GroupLayout testPanel2Layout = new javax.swing.GroupLayout(testPanel2);
        testPanel2.setLayout(testPanel2Layout);
        testPanel2Layout.setHorizontalGroup(
            testPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(testPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(749, Short.MAX_VALUE))
        );
        testPanel2Layout.setVerticalGroup(
            testPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(testPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addContainerGap(211, Short.MAX_VALUE))
        );

        topPanel.add(testPanel2, "testPanel2");

        bottomPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        bottomPanel.setOpaque(false);

        backButton.setText("back");
        backButton.setMaximumSize(new java.awt.Dimension(80, 25));
        backButton.setMinimumSize(new java.awt.Dimension(80, 25));
        backButton.setPreferredSize(new java.awt.Dimension(80, 25));

        proceedButton.setText("proceed");
        proceedButton.setMaximumSize(new java.awt.Dimension(80, 25));
        proceedButton.setMinimumSize(new java.awt.Dimension(80, 25));
        proceedButton.setPreferredSize(new java.awt.Dimension(80, 25));

        infoLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        infoLabel.setMaximumSize(new java.awt.Dimension(34, 20));
        infoLabel.setMinimumSize(new java.awt.Dimension(34, 20));
        infoLabel.setName(""); // NOI18N
        infoLabel.setPreferredSize(new java.awt.Dimension(34, 20));

        finishButton.setText("finish");
        finishButton.setMaximumSize(new java.awt.Dimension(80, 25));
        finishButton.setMinimumSize(new java.awt.Dimension(80, 25));
        finishButton.setPreferredSize(new java.awt.Dimension(80, 25));

        closeButton.setText("close");
        closeButton.setPreferredSize(new java.awt.Dimension(80, 25));

        javax.swing.GroupLayout bottomPanelLayout = new javax.swing.GroupLayout(bottomPanel);
        bottomPanel.setLayout(bottomPanelLayout);
        bottomPanelLayout.setHorizontalGroup(
            bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, bottomPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(infoLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 666, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(backButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(proceedButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(finishButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        bottomPanelLayout.setVerticalGroup(
            bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bottomPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(infoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(proceedButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(backButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(finishButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout parentPanelLayout = new javax.swing.GroupLayout(parentPanel);
        parentPanel.setLayout(parentPanelLayout);
        parentPanelLayout.setHorizontalGroup(
            parentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(parentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(parentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(bottomPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(topPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        parentPanelLayout.setVerticalGroup(
            parentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(parentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(topPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 241, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bottomPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(parentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(parentPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton backButton;
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JButton closeButton;
    private javax.swing.ButtonGroup dataTypeButtonGroup;
    private javax.swing.JLabel dataTypeSelectionLabel;
    private javax.swing.JPanel dataTypeSelectionPanel;
    private javax.swing.JPanel experimentsPanel;
    private javax.swing.JScrollPane experimentsScrollPane;
    private javax.swing.JTable experimentsTable;
    private javax.swing.JButton finishButton;
    private javax.swing.JLabel infoLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JRadioButton maxQuantRadioButton;
    private javax.swing.JPanel parentPanel;
    private com.compomics.colims.client.view.PeptideShakerDataImportPanel peptideShakerDataImportPanel;
    private javax.swing.JRadioButton peptideShakerRadioButton;
    private javax.swing.JButton proceedButton;
    private javax.swing.JPanel projectsPanel;
    private javax.swing.JScrollPane projectsScrollPane;
    private javax.swing.JTable projectsTable;
    private javax.swing.JPanel sampleSelectionPanel;
    private javax.swing.JPanel samplesPanel;
    private javax.swing.JScrollPane samplesScrollPane;
    private javax.swing.JTable samplesTable;
    private javax.swing.JPanel testPanel;
    private javax.swing.JPanel testPanel2;
    private javax.swing.JPanel topPanel;
    // End of variables declaration//GEN-END:variables
}
