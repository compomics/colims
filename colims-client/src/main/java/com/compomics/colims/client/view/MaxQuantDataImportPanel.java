package com.compomics.colims.client.view;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Niels Hulstaert
 */
public class MaxQuantDataImportPanel extends javax.swing.JPanel {

    private final JFileChooser maxQuantDirectoryChooser = new JFileChooser();
    private final JFileChooser fastaFileChooser = new JFileChooser();    

    /**
     * Creates new form PeptideShakerDataImportPanel
     */
    public MaxQuantDataImportPanel() {
        initComponents();                 
    }  

    public JFileChooser getMaxQuantDirectoryChooser() {
        return maxQuantDirectoryChooser;
    }

    public JFileChooser getFastaFileChooser() {
        return fastaFileChooser;
    }

    public JLabel getFastaFileLabel() {
        return fastaFileLabel;
    }

    public JLabel getMaxQuantDirectoryLabel() {
        return maxQuantDirectoryLabel;
    }

    public JButton getSelectFastaButton() {
        return selectFastaButton;
    }        

    public JButton getSelectMaxQuantDirectoryButton() {
        return selectMaxQuantDirectoryButton;
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
        maxQuantDirectoryLabel = new javax.swing.JLabel();
        fastaFileLabel = new javax.swing.JLabel();
        selectMaxQuantDirectoryButton = new javax.swing.JButton();
        selectFastaButton = new javax.swing.JButton();
        fastaFileSelectionLabel = new javax.swing.JLabel();

        setOpaque(false);

        maxQuantDirectorySelectionLabel.setText("Select the MaxQuant text files directory");

        maxQuantDirectoryLabel.setBorder(BorderFactory.createCompoundBorder(UIManager.getBorder("TextField.border"), new EmptyBorder(0, 5, 0, 0)));

        fastaFileLabel.setBorder(BorderFactory.createCompoundBorder(UIManager.getBorder("TextField.border"), new EmptyBorder(0, 5, 0, 0)));

        selectMaxQuantDirectoryButton.setText("browse...");
        selectMaxQuantDirectoryButton.setPreferredSize(new java.awt.Dimension(80, 25));

        selectFastaButton.setText("browse...");
        selectFastaButton.setPreferredSize(new java.awt.Dimension(80, 25));

        fastaFileSelectionLabel.setText("Select a FASTA file");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(maxQuantDirectorySelectionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(maxQuantDirectoryLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(selectMaxQuantDirectoryButton, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(fastaFileLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(selectFastaButton, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(fastaFileSelectionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(452, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(maxQuantDirectorySelectionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(maxQuantDirectoryLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(selectMaxQuantDirectoryButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(fastaFileSelectionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fastaFileLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(selectFastaButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(163, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel fastaFileLabel;
    private javax.swing.JLabel fastaFileSelectionLabel;
    private javax.swing.JLabel maxQuantDirectoryLabel;
    private javax.swing.JLabel maxQuantDirectorySelectionLabel;
    private javax.swing.JButton selectFastaButton;
    private javax.swing.JButton selectMaxQuantDirectoryButton;
    // End of variables declaration//GEN-END:variables
}
