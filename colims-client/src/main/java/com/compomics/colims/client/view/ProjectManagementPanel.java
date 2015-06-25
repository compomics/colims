package com.compomics.colims.client.view;

import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

/**
 *
 * @author Niels Hulstaert
 */
public class ProjectManagementPanel extends javax.swing.JPanel {

    /**
     * Creates new form OverviewPanel
     */
    public ProjectManagementPanel() {
        initComponents();

        projectsTableScrollPane.getViewport().setOpaque(false);
        projectsTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        experimentsTableScrollPane.getViewport().setOpaque(false);
        experimentsTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        samplesTableScrollPane.getViewport().setOpaque(false);
        samplesTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    }

    public JButton getAddExperimentButton() {
        return addExperimentButton;
    }

    public JButton getAddProjectButton() {
        return addProjectButton;
    }

    public JButton getEditExperimentButton() {
        return editExperimentButton;
    }

    public JButton getEditProjectButton() {
        return editProjectButton;
    }

    public JTable getExperimentsTable() {
        return experimentsTable;
    }

    public JTable getProjectsTable() {
        return projectsTable;
    }

    public JButton getDeleteProjectButton() {
        return deleteProjectButton;
    }

    public JButton getDeleteExperimentButton() {
        return deleteExperimentButton;
    }

    public JButton getAddSampleButton() {
        return addSampleButton;
    }

    public JButton getDeleteSampleButton() {
        return deleteSampleButton;
    }

    public JButton getEditSampleButton() {
        return editSampleButton;
    }

    public JTable getSamplesTable() {
        return samplesTable;
    }

    public JButton getOtherSampleActionsButton() {
        return otherSampleActionsButton;
    }

    public JMenuItem getAddRunMenuItem() {
        return addRunMenuItem;
    }

    public JMenuItem getMzTabExportMenuItem() {
        return mzTabExportMenuItem;
    }

    public JPopupMenu getSamplePopupMenu() {
        return samplePopupMenu;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        samplePopupMenu = new javax.swing.JPopupMenu();
        addRunMenuItem = new javax.swing.JMenuItem();
        mzTabExportMenuItem = new javax.swing.JMenuItem();
        projectsOverviewPanel = new javax.swing.JPanel();
        editProjectButton = new javax.swing.JButton();
        addProjectButton = new javax.swing.JButton();
        projectsTableScrollPane = new javax.swing.JScrollPane();
        projectsTable = new javax.swing.JTable();
        deleteProjectButton = new javax.swing.JButton();
        experimentsOverviewPanel = new javax.swing.JPanel();
        addExperimentButton = new javax.swing.JButton();
        editExperimentButton = new javax.swing.JButton();
        experimentsTableScrollPane = new javax.swing.JScrollPane();
        experimentsTable = new javax.swing.JTable();
        deleteExperimentButton = new javax.swing.JButton();
        samplesOverviewPanel = new javax.swing.JPanel();
        addSampleButton = new javax.swing.JButton();
        editSampleButton = new javax.swing.JButton();
        samplesTableScrollPane = new javax.swing.JScrollPane();
        samplesTable = new javax.swing.JTable();
        deleteSampleButton = new javax.swing.JButton();
        otherSampleActionsButton = new javax.swing.JButton();

        addRunMenuItem.setText("add run...");
        samplePopupMenu.add(addRunMenuItem);

        mzTabExportMenuItem.setText("export to mzTab...");
        samplePopupMenu.add(mzTabExportMenuItem);

        setBackground(new java.awt.Color(255, 255, 255));
        setLayout(new java.awt.GridBagLayout());

        projectsOverviewPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Projects"));
        projectsOverviewPanel.setOpaque(false);
        projectsOverviewPanel.setPreferredSize(new java.awt.Dimension(20, 20));

        editProjectButton.setText("edit...");
        editProjectButton.setToolTipText("edit the metadata of an existing project");
        editProjectButton.setMaximumSize(new java.awt.Dimension(80, 25));
        editProjectButton.setMinimumSize(new java.awt.Dimension(80, 25));
        editProjectButton.setPreferredSize(new java.awt.Dimension(80, 25));

        addProjectButton.setText("add...");
        addProjectButton.setToolTipText("add a new project");
        addProjectButton.setMaximumSize(new java.awt.Dimension(80, 25));
        addProjectButton.setMinimumSize(new java.awt.Dimension(80, 25));
        addProjectButton.setPreferredSize(new java.awt.Dimension(80, 25));

        projectsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        projectsTable.setOpaque(false);
        projectsTableScrollPane.setViewportView(projectsTable);

        deleteProjectButton.setText("delete");
        deleteProjectButton.setToolTipText("delete a project");
        deleteProjectButton.setEnabled(false);
        deleteProjectButton.setMaximumSize(new java.awt.Dimension(80, 25));
        deleteProjectButton.setMinimumSize(new java.awt.Dimension(80, 25));
        deleteProjectButton.setPreferredSize(new java.awt.Dimension(80, 25));

        javax.swing.GroupLayout projectsOverviewPanelLayout = new javax.swing.GroupLayout(projectsOverviewPanel);
        projectsOverviewPanel.setLayout(projectsOverviewPanelLayout);
        projectsOverviewPanelLayout.setHorizontalGroup(
            projectsOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(projectsOverviewPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(projectsOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, projectsOverviewPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(addProjectButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(editProjectButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteProjectButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(projectsTableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 656, Short.MAX_VALUE))
                .addContainerGap())
        );
        projectsOverviewPanelLayout.setVerticalGroup(
            projectsOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, projectsOverviewPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(projectsTableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(projectsOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(deleteProjectButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(editProjectButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addProjectButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(projectsOverviewPanel, gridBagConstraints);

        experimentsOverviewPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Project experiments"));
        experimentsOverviewPanel.setOpaque(false);
        experimentsOverviewPanel.setPreferredSize(new java.awt.Dimension(20, 20));

        addExperimentButton.setText("add...");
        addExperimentButton.setToolTipText("add a new experiment");
        addExperimentButton.setMaximumSize(new java.awt.Dimension(80, 25));
        addExperimentButton.setMinimumSize(new java.awt.Dimension(80, 25));
        addExperimentButton.setPreferredSize(new java.awt.Dimension(80, 25));

        editExperimentButton.setText("edit...");
        editExperimentButton.setToolTipText("edit the metadata of an existing experiment");
        editExperimentButton.setMaximumSize(new java.awt.Dimension(80, 25));
        editExperimentButton.setMinimumSize(new java.awt.Dimension(80, 25));
        editExperimentButton.setPreferredSize(new java.awt.Dimension(80, 25));

        experimentsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        experimentsTable.setOpaque(false);
        experimentsTableScrollPane.setViewportView(experimentsTable);

        deleteExperimentButton.setText("delete");
        deleteExperimentButton.setToolTipText("delete an experiment");
        deleteExperimentButton.setEnabled(false);
        deleteExperimentButton.setMaximumSize(new java.awt.Dimension(80, 25));
        deleteExperimentButton.setMinimumSize(new java.awt.Dimension(80, 25));
        deleteExperimentButton.setPreferredSize(new java.awt.Dimension(80, 25));

        javax.swing.GroupLayout experimentsOverviewPanelLayout = new javax.swing.GroupLayout(experimentsOverviewPanel);
        experimentsOverviewPanel.setLayout(experimentsOverviewPanelLayout);
        experimentsOverviewPanelLayout.setHorizontalGroup(
            experimentsOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(experimentsOverviewPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(experimentsOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(experimentsOverviewPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(addExperimentButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(editExperimentButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteExperimentButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(experimentsTableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 656, Short.MAX_VALUE))
                .addContainerGap())
        );
        experimentsOverviewPanelLayout.setVerticalGroup(
            experimentsOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, experimentsOverviewPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(experimentsTableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(experimentsOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addExperimentButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(editExperimentButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(deleteExperimentButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(experimentsOverviewPanel, gridBagConstraints);

        samplesOverviewPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Experiment samples"));
        samplesOverviewPanel.setOpaque(false);
        samplesOverviewPanel.setPreferredSize(new java.awt.Dimension(20, 20));

        addSampleButton.setText("add...");
        addSampleButton.setToolTipText("add a new sample");
        addSampleButton.setMaximumSize(new java.awt.Dimension(80, 25));
        addSampleButton.setMinimumSize(new java.awt.Dimension(80, 25));
        addSampleButton.setPreferredSize(new java.awt.Dimension(80, 25));

        editSampleButton.setText("edit...");
        editSampleButton.setToolTipText("edit the metadata of an existing sample");
        editSampleButton.setMaximumSize(new java.awt.Dimension(80, 25));
        editSampleButton.setMinimumSize(new java.awt.Dimension(80, 25));
        editSampleButton.setPreferredSize(new java.awt.Dimension(80, 25));

        samplesTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        samplesTable.setOpaque(false);
        samplesTableScrollPane.setViewportView(samplesTable);

        deleteSampleButton.setText("delete");
        deleteSampleButton.setToolTipText("delete a sample");
        deleteSampleButton.setEnabled(false);
        deleteSampleButton.setMaximumSize(new java.awt.Dimension(80, 25));
        deleteSampleButton.setMinimumSize(new java.awt.Dimension(80, 25));
        deleteSampleButton.setPreferredSize(new java.awt.Dimension(80, 25));

        otherSampleActionsButton.setText("other...");
        otherSampleActionsButton.setToolTipText("other sample actions");
        otherSampleActionsButton.setMaximumSize(new java.awt.Dimension(80, 25));
        otherSampleActionsButton.setMinimumSize(new java.awt.Dimension(80, 25));
        otherSampleActionsButton.setPreferredSize(new java.awt.Dimension(80, 25));

        javax.swing.GroupLayout samplesOverviewPanelLayout = new javax.swing.GroupLayout(samplesOverviewPanel);
        samplesOverviewPanel.setLayout(samplesOverviewPanelLayout);
        samplesOverviewPanelLayout.setHorizontalGroup(
            samplesOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(samplesOverviewPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(samplesOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(samplesOverviewPanelLayout.createSequentialGroup()
                        .addComponent(otherSampleActionsButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(addSampleButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(editSampleButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteSampleButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(samplesTableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 648, Short.MAX_VALUE))
                .addContainerGap())
        );
        samplesOverviewPanelLayout.setVerticalGroup(
            samplesOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, samplesOverviewPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(samplesTableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(samplesOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addSampleButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(editSampleButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(deleteSampleButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(otherSampleActionsButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(samplesOverviewPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addExperimentButton;
    private javax.swing.JButton addProjectButton;
    private javax.swing.JMenuItem addRunMenuItem;
    private javax.swing.JButton addSampleButton;
    private javax.swing.JButton deleteExperimentButton;
    private javax.swing.JButton deleteProjectButton;
    private javax.swing.JButton deleteSampleButton;
    private javax.swing.JButton editExperimentButton;
    private javax.swing.JButton editProjectButton;
    private javax.swing.JButton editSampleButton;
    private javax.swing.JPanel experimentsOverviewPanel;
    private javax.swing.JTable experimentsTable;
    private javax.swing.JScrollPane experimentsTableScrollPane;
    private javax.swing.JMenuItem mzTabExportMenuItem;
    private javax.swing.JButton otherSampleActionsButton;
    private javax.swing.JPanel projectsOverviewPanel;
    private javax.swing.JTable projectsTable;
    private javax.swing.JScrollPane projectsTableScrollPane;
    private javax.swing.JPopupMenu samplePopupMenu;
    private javax.swing.JPanel samplesOverviewPanel;
    private javax.swing.JTable samplesTable;
    private javax.swing.JScrollPane samplesTableScrollPane;
    // End of variables declaration//GEN-END:variables
}
