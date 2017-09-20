/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.client.view.fasta;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTable;

/**
 *
 * @author Niels Hulstaert
 */
public class HeaderParseRuleTestDialog extends javax.swing.JDialog {

    /**
     * Dialog constructor.
     *
     * @param parent the parent frame
     * @param modal the modal boolean
     */
    public HeaderParseRuleTestDialog(JDialog parent, boolean modal) {
        super(parent, modal);
        initComponents();

        headersTableScrollPane.getViewport().setOpaque(false);
        headersTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    }

    public JButton getCloseButton() {
        return closeButton;
    }

    public JTable getHeadersTable() {
        return headersTable;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        parentPanel = new javax.swing.JPanel();
        headersTableScrollPane = new javax.swing.JScrollPane();
        headersTable = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        closeButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("FASTA header parse rule test");
        setBackground(new java.awt.Color(255, 255, 255));

        parentPanel.setBackground(new java.awt.Color(255, 255, 255));
        parentPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        headersTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"RRR|", null},
                {"RRR|", null},
                {"RRR|", null},
                {"RRR|", null},
                {"RRR|", null},
                {"RRR|", null},
                {"RRR|", null},
                {"RRR|", null},
                {"RRR|", null},
                {"RRR|", null}
            },
            new String [] {
                "Title 1", "Title 2"
            }
        ));
        headersTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        headersTableScrollPane.setViewportView(headersTable);

        jLabel1.setText("The parsed protein accession should match the protein ID in the proteinGroups.txt file.");

        javax.swing.GroupLayout parentPanelLayout = new javax.swing.GroupLayout(parentPanel);
        parentPanel.setLayout(parentPanelLayout);
        parentPanelLayout.setHorizontalGroup(
            parentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(headersTableScrollPane)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        parentPanelLayout.setVerticalGroup(
            parentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, parentPanelLayout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(headersTableScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        closeButton.setText("close");
        closeButton.setMaximumSize(new java.awt.Dimension(80, 25));
        closeButton.setMinimumSize(new java.awt.Dimension(80, 25));
        closeButton.setPreferredSize(new java.awt.Dimension(80, 25));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(parentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 521, Short.MAX_VALUE)
                        .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(parentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton closeButton;
    private javax.swing.JTable headersTable;
    private javax.swing.JScrollPane headersTableScrollPane;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel parentPanel;
    // End of variables declaration//GEN-END:variables
}