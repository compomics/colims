package com.compomics.colims.client.view;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTextField;

/**
 *
 * @author Niels Hulstaert
 */
public class MaxQuantDataImportPanel extends javax.swing.JPanel {

    private final JFileChooser maxQuantDirectoryChooser = new JFileChooser();
    private final JFileChooser primaryFastaFileChooser = new JFileChooser();
    private final JFileChooser additionalFastaFileChooser = new JFileChooser();
    private final JFileChooser contaminantsFastaFileChooser = new JFileChooser();

    /**
     * Creates new form PeptideShakerDataImportPanel
     */
    public MaxQuantDataImportPanel() {
        initComponents();
    }

    public JFileChooser getMaxQuantDirectoryChooser() {
        return maxQuantDirectoryChooser;
    }

    public JFileChooser getPrimaryFastaFileChooser() {
        return primaryFastaFileChooser;
    }

    public JFileChooser getAdditionalFastaFileChooser() {
        return additionalFastaFileChooser;
    }

    public JFileChooser getContaminantsFastaFileChooser() {
        return contaminantsFastaFileChooser;
    }

    public JTextField getAdditionalFastaDbTextField() {
        return additionalFastaDbTextField;
    }

    public JTextField getContaminantsFastaDbTextField() {
        return contaminantsFastaDbTextField;
    }

    public JTextField getMaxQuantDirectoryTextField() {
        return maxQuantDirectoryTextField;
    }

    public JTextField getPrimaryFastaDbTextField() {
        return primaryFastaDbTextField;
    }

    public JButton getSelectAddtionalFastaDbButton() {
        return selectAdditionalFastaDbButton;
    }

    public JButton getSelectContaminantsFastaDbButton() {
        return selectContaminantsFastaDbButton;
    }

    public JButton getSelectMaxQuantDirectoryButton() {
        return selectMaxQuantDirectoryButton;
    }

    public JButton getSelectPrimaryFastaDbButton() {
        return selectPrimaryFastaDbButton;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        maxQuantDirectorySelectionLabel = new javax.swing.JLabel();
        selectMaxQuantDirectoryButton = new javax.swing.JButton();
        selectPrimaryFastaDbButton = new javax.swing.JButton();
        primaryFastaFileSelectionLabel = new javax.swing.JLabel();
        maxQuantDirectoryTextField = new javax.swing.JTextField();
        primaryFastaDbTextField = new javax.swing.JTextField();
        contaminantsFastaFileSelectionLabel = new javax.swing.JLabel();
        contaminantsFastaDbTextField = new javax.swing.JTextField();
        selectContaminantsFastaDbButton = new javax.swing.JButton();
        additionalFastaFileSelectionLabel = new javax.swing.JLabel();
        additionalFastaDbTextField = new javax.swing.JTextField();
        selectAdditionalFastaDbButton = new javax.swing.JButton();

        setOpaque(false);

        maxQuantDirectorySelectionLabel.setText("Select the MaxQuant files directory*:");

        selectMaxQuantDirectoryButton.setText("browse...");
        selectMaxQuantDirectoryButton.setMaximumSize(new java.awt.Dimension(80, 25));
        selectMaxQuantDirectoryButton.setMinimumSize(new java.awt.Dimension(80, 25));
        selectMaxQuantDirectoryButton.setPreferredSize(new java.awt.Dimension(80, 25));

        selectPrimaryFastaDbButton.setText("browse...");
        selectPrimaryFastaDbButton.setMaximumSize(new java.awt.Dimension(80, 25));
        selectPrimaryFastaDbButton.setMinimumSize(new java.awt.Dimension(80, 25));
        selectPrimaryFastaDbButton.setPreferredSize(new java.awt.Dimension(80, 25));

        primaryFastaFileSelectionLabel.setText("Select a primary FASTA file*:");

        maxQuantDirectoryTextField.setEditable(false);

        primaryFastaDbTextField.setEditable(false);

        contaminantsFastaFileSelectionLabel.setText("Select a contaminants FASTA file*:");

        selectContaminantsFastaDbButton.setText("browse...");
        selectContaminantsFastaDbButton.setMaximumSize(new java.awt.Dimension(80, 25));
        selectContaminantsFastaDbButton.setMinimumSize(new java.awt.Dimension(80, 25));
        selectContaminantsFastaDbButton.setPreferredSize(new java.awt.Dimension(80, 25));

        additionalFastaFileSelectionLabel.setText("Select an additional FASTA file (with e.g. some additional sequences):");

        selectAdditionalFastaDbButton.setText("browse...");
        selectAdditionalFastaDbButton.setMaximumSize(new java.awt.Dimension(80, 25));
        selectAdditionalFastaDbButton.setMinimumSize(new java.awt.Dimension(80, 25));
        selectAdditionalFastaDbButton.setPreferredSize(new java.awt.Dimension(80, 25));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(contaminantsFastaFileSelectionLabel)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(maxQuantDirectoryTextField)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(selectMaxQuantDirectoryButton, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(primaryFastaDbTextField)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(selectPrimaryFastaDbButton, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(maxQuantDirectorySelectionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(primaryFastaFileSelectionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(contaminantsFastaDbTextField)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(additionalFastaFileSelectionLabel)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addComponent(additionalFastaDbTextField))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(selectContaminantsFastaDbButton, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
                                    .addComponent(selectAdditionalFastaDbButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addGap(452, 452, 452))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(maxQuantDirectorySelectionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(selectMaxQuantDirectoryButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(maxQuantDirectoryTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(primaryFastaFileSelectionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(selectPrimaryFastaDbButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(primaryFastaDbTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(contaminantsFastaFileSelectionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(contaminantsFastaDbTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(selectContaminantsFastaDbButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(additionalFastaFileSelectionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(additionalFastaDbTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(selectAdditionalFastaDbButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField additionalFastaDbTextField;
    private javax.swing.JLabel additionalFastaFileSelectionLabel;
    private javax.swing.JTextField contaminantsFastaDbTextField;
    private javax.swing.JLabel contaminantsFastaFileSelectionLabel;
    private javax.swing.JLabel maxQuantDirectorySelectionLabel;
    private javax.swing.JTextField maxQuantDirectoryTextField;
    private javax.swing.JTextField primaryFastaDbTextField;
    private javax.swing.JLabel primaryFastaFileSelectionLabel;
    private javax.swing.JButton selectAdditionalFastaDbButton;
    private javax.swing.JButton selectContaminantsFastaDbButton;
    private javax.swing.JButton selectMaxQuantDirectoryButton;
    private javax.swing.JButton selectPrimaryFastaDbButton;
    // End of variables declaration//GEN-END:variables
}
