/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.client.view;

import javax.swing.*;

/**
 *
 * @author Iain
 */
public class ProteinOverviewPanel extends javax.swing.JPanel {

    /**
     * Creates new form ProteinOverviewPanel.
     */
    public ProteinOverviewPanel() {
        initComponents();

        proteinGroupTableScrollPane.getViewport().setOpaque(false);
        peptideTableScrollPane.getViewport().setOpaque(false);
        psmTableScrollPane.getViewport().setOpaque(false);
    }

    public JButton getExportPeptidesButton() {
        return exportPeptidesButton;
    }

    public JButton getExportProteinGroupsButton() {
        return exportProteinGroupsButton;
    }

    public JButton getExportPsmsButton() {
        return exportPsmsButton;
    }

    public JButton getFirstProteinGroupPageButton() {
        return firstProteinGroupPageButton;
    }

    public JButton getLastProteinGroupPageButton() {
        return lastProteinGroupPageButton;
    }

    public JButton getNextProteinGroupPageButton() {
        return nextProteinGroupPageButton;
    }

    public JTable getPeptideTable() {
        return peptideTable;
    }

    public JButton getPrevProteinGroupPageButton() {
        return prevProteinGroupPageButton;
    }

    public JTree getProjectTree() {
        return projectTree;
    }

    public JTextField getProteinGroupFilterTextField() {
        proteinGroupFilterTextField.setToolTipText("This process might take long time");
        return proteinGroupFilterTextField;
    }

    public JLabel getProteinGroupPageLabel() {
        return proteinGroupPageLabel;
    }

    public JTable getProteinGroupTable() {
        return proteinGroupTable;
    }

    public JTable getPsmTable() {
        return psmTable;
    }

    public JFileChooser getExportFileChooser() {
        return exportFileChooser;
    }

    public JScrollPane getProteinGroupTableScrollPane() {
        return proteinGroupTableScrollPane;
    }

    public JButton getFilterButton() {
        return filterButton;
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

        exportFileChooser = new javax.swing.JFileChooser();
        jSplitPane1 = new javax.swing.JSplitPane();
        treeParentPanel = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        projectTree = new javax.swing.JTree();
        tablesParentPanel = new javax.swing.JPanel();
        proteinGroupTablePanel = new javax.swing.JPanel();
        proteinGroupTableScrollPane = new javax.swing.JScrollPane();
        proteinGroupTable = new javax.swing.JTable();
        proteinGroupFilterTextField = new javax.swing.JTextField();
        firstProteinGroupPageButton = new javax.swing.JButton();
        prevProteinGroupPageButton = new javax.swing.JButton();
        nextProteinGroupPageButton = new javax.swing.JButton();
        lastProteinGroupPageButton = new javax.swing.JButton();
        exportProteinGroupsButton = new javax.swing.JButton();
        proteinGroupPageLabel = new javax.swing.JLabel();
        proteinGroupFilterLabel = new javax.swing.JLabel();
        filterButton = new javax.swing.JButton();
        peptideTablePanel = new javax.swing.JPanel();
        peptideTableScrollPane = new javax.swing.JScrollPane();
        peptideTable = new javax.swing.JTable();
        exportPeptidesButton = new javax.swing.JButton();
        psmTablePanel = new javax.swing.JPanel();
        psmTableScrollPane = new javax.swing.JScrollPane();
        psmTable = new javax.swing.JTable();
        exportPsmsButton = new javax.swing.JButton();

        exportFileChooser.setDialogTitle("Save protein data as TSV file");

        setBackground(new java.awt.Color(255, 255, 255));
        setMinimumSize(new java.awt.Dimension(0, 0));
        setPreferredSize(new java.awt.Dimension(0, 0));
        setLayout(new javax.swing.OverlayLayout(this));

        jSplitPane1.setDividerLocation(330);

        treeParentPanel.setOpaque(false);

        jScrollPane4.setHorizontalScrollBar(null);

        projectTree.setMaximumSize(new java.awt.Dimension(32767, 32767));
        projectTree.setPreferredSize(null);
        jScrollPane4.setViewportView(projectTree);

        javax.swing.GroupLayout treeParentPanelLayout = new javax.swing.GroupLayout(treeParentPanel);
        treeParentPanel.setLayout(treeParentPanelLayout);
        treeParentPanelLayout.setHorizontalGroup(
            treeParentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
        );
        treeParentPanelLayout.setVerticalGroup(
            treeParentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 798, Short.MAX_VALUE)
        );

        jSplitPane1.setLeftComponent(treeParentPanel);

        tablesParentPanel.setOpaque(false);
        tablesParentPanel.setLayout(new java.awt.GridBagLayout());

        proteinGroupTablePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Protein Groups"));
        proteinGroupTablePanel.setName(""); // NOI18N
        proteinGroupTablePanel.setOpaque(false);

        proteinGroupTableScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        proteinGroupTable.setModel(new javax.swing.table.DefaultTableModel(
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
        proteinGroupTableScrollPane.setViewportView(proteinGroupTable);

        proteinGroupFilterTextField.setToolTipText("This process might take long time");
        proteinGroupFilterTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                proteinGroupFilterTextFieldActionPerformed(evt);
            }
        });

        firstProteinGroupPageButton.setText("<<");
        firstProteinGroupPageButton.setMaximumSize(new java.awt.Dimension(48, 25));
        firstProteinGroupPageButton.setMinimumSize(new java.awt.Dimension(48, 25));
        firstProteinGroupPageButton.setPreferredSize(new java.awt.Dimension(48, 25));

        prevProteinGroupPageButton.setText("<");
        prevProteinGroupPageButton.setMaximumSize(new java.awt.Dimension(38, 25));
        prevProteinGroupPageButton.setMinimumSize(new java.awt.Dimension(38, 25));
        prevProteinGroupPageButton.setPreferredSize(new java.awt.Dimension(38, 25));

        nextProteinGroupPageButton.setText(">");
        nextProteinGroupPageButton.setMaximumSize(new java.awt.Dimension(38, 25));
        nextProteinGroupPageButton.setMinimumSize(new java.awt.Dimension(38, 25));
        nextProteinGroupPageButton.setPreferredSize(new java.awt.Dimension(38, 25));

        lastProteinGroupPageButton.setText(">>");
        lastProteinGroupPageButton.setMaximumSize(new java.awt.Dimension(48, 25));
        lastProteinGroupPageButton.setMinimumSize(new java.awt.Dimension(48, 25));
        lastProteinGroupPageButton.setPreferredSize(new java.awt.Dimension(48, 25));
        lastProteinGroupPageButton.setRequestFocusEnabled(false);

        exportProteinGroupsButton.setText("Export...");
        exportProteinGroupsButton.setMaximumSize(new java.awt.Dimension(80, 25));
        exportProteinGroupsButton.setMinimumSize(new java.awt.Dimension(80, 25));
        exportProteinGroupsButton.setPreferredSize(new java.awt.Dimension(80, 25));

        proteinGroupPageLabel.setMaximumSize(new java.awt.Dimension(60, 27));
        proteinGroupPageLabel.setMinimumSize(new java.awt.Dimension(60, 27));

        proteinGroupFilterLabel.setText("Filter on protein accession or sequence:");

        filterButton.setText("Filter");
        filterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout proteinGroupTablePanelLayout = new javax.swing.GroupLayout(proteinGroupTablePanel);
        proteinGroupTablePanel.setLayout(proteinGroupTablePanelLayout);
        proteinGroupTablePanelLayout.setHorizontalGroup(
            proteinGroupTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(proteinGroupTablePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(proteinGroupTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(proteinGroupTableScrollPane, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, proteinGroupTablePanelLayout.createSequentialGroup()
                        .addComponent(exportProteinGroupsButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(proteinGroupFilterLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(proteinGroupFilterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(filterButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 179, Short.MAX_VALUE)
                        .addComponent(proteinGroupPageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(firstProteinGroupPageButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(prevProteinGroupPageButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(nextProteinGroupPageButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lastProteinGroupPageButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        proteinGroupTablePanelLayout.setVerticalGroup(
            proteinGroupTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(proteinGroupTablePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(proteinGroupTableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(proteinGroupTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(proteinGroupTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, proteinGroupTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lastProteinGroupPageButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(nextProteinGroupPageButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(prevProteinGroupPageButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(firstProteinGroupPageButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, proteinGroupTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(exportProteinGroupsButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(proteinGroupFilterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(proteinGroupFilterLabel)
                            .addComponent(filterButton)))
                    .addComponent(proteinGroupPageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.4;
        tablesParentPanel.add(proteinGroupTablePanel, gridBagConstraints);

        peptideTablePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Peptides"));
        peptideTablePanel.setOpaque(false);

        peptideTableScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        peptideTable.setModel(new javax.swing.table.DefaultTableModel(
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
        peptideTableScrollPane.setViewportView(peptideTable);

        exportPeptidesButton.setText("Export...");
        exportPeptidesButton.setMaximumSize(new java.awt.Dimension(80, 25));
        exportPeptidesButton.setMinimumSize(new java.awt.Dimension(80, 25));
        exportPeptidesButton.setPreferredSize(new java.awt.Dimension(80, 25));

        javax.swing.GroupLayout peptideTablePanelLayout = new javax.swing.GroupLayout(peptideTablePanel);
        peptideTablePanel.setLayout(peptideTablePanelLayout);
        peptideTablePanelLayout.setHorizontalGroup(
            peptideTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(peptideTablePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(peptideTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(peptideTableScrollPane)
                    .addGroup(peptideTablePanelLayout.createSequentialGroup()
                        .addComponent(exportPeptidesButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 1241, Short.MAX_VALUE)))
                .addContainerGap())
        );
        peptideTablePanelLayout.setVerticalGroup(
            peptideTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, peptideTablePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(peptideTableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(exportPeptidesButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.3;
        tablesParentPanel.add(peptideTablePanel, gridBagConstraints);

        psmTablePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Peptide-Spectrum Matches (double-click to view spectrum)"));
        psmTablePanel.setOpaque(false);

        psmTableScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        psmTable.setModel(new javax.swing.table.DefaultTableModel(
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
        psmTableScrollPane.setViewportView(psmTable);

        exportPsmsButton.setText("Export...");
        exportPsmsButton.setMaximumSize(new java.awt.Dimension(80, 25));
        exportPsmsButton.setMinimumSize(new java.awt.Dimension(80, 25));
        exportPsmsButton.setPreferredSize(new java.awt.Dimension(80, 25));

        javax.swing.GroupLayout psmTablePanelLayout = new javax.swing.GroupLayout(psmTablePanel);
        psmTablePanel.setLayout(psmTablePanelLayout);
        psmTablePanelLayout.setHorizontalGroup(
            psmTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(psmTablePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(psmTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(psmTableScrollPane)
                    .addGroup(psmTablePanelLayout.createSequentialGroup()
                        .addComponent(exportPsmsButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 1241, Short.MAX_VALUE)))
                .addContainerGap())
        );
        psmTablePanelLayout.setVerticalGroup(
            psmTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(psmTablePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(psmTableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(exportPsmsButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.3;
        tablesParentPanel.add(psmTablePanel, gridBagConstraints);

        jSplitPane1.setRightComponent(tablesParentPanel);

        add(jSplitPane1);
    }// </editor-fold>//GEN-END:initComponents

    private void proteinGroupFilterTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_proteinGroupFilterTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_proteinGroupFilterTextFieldActionPerformed

    private void filterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_filterButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFileChooser exportFileChooser;
    private javax.swing.JButton exportPeptidesButton;
    private javax.swing.JButton exportProteinGroupsButton;
    private javax.swing.JButton exportPsmsButton;
    private javax.swing.JButton filterButton;
    private javax.swing.JButton firstProteinGroupPageButton;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JButton lastProteinGroupPageButton;
    private javax.swing.JButton nextProteinGroupPageButton;
    private javax.swing.JTable peptideTable;
    private javax.swing.JPanel peptideTablePanel;
    private javax.swing.JScrollPane peptideTableScrollPane;
    private javax.swing.JButton prevProteinGroupPageButton;
    private javax.swing.JTree projectTree;
    private javax.swing.JLabel proteinGroupFilterLabel;
    private javax.swing.JTextField proteinGroupFilterTextField;
    private javax.swing.JLabel proteinGroupPageLabel;
    private javax.swing.JTable proteinGroupTable;
    private javax.swing.JPanel proteinGroupTablePanel;
    private javax.swing.JScrollPane proteinGroupTableScrollPane;
    private javax.swing.JTable psmTable;
    private javax.swing.JPanel psmTablePanel;
    private javax.swing.JScrollPane psmTableScrollPane;
    private javax.swing.JPanel tablesParentPanel;
    private javax.swing.JPanel treeParentPanel;
    // End of variables declaration//GEN-END:variables

}
