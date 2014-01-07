package com.compomics.colims.client.view;

import com.compomics.colims.client.compoment.BinaryFileManagementPanel;
import com.compomics.colims.model.ExperimentBinaryFile;
import java.awt.Dialog;
import javax.swing.JButton;

/**
 *
 * @author Niels Hulstaert
 */
public class ExperimentBinaryFileDialog extends javax.swing.JDialog {

    /**
     * Creates new form LoginDialog
     */
    public ExperimentBinaryFileDialog(Dialog parent, boolean modal) {
        super(parent, modal);

        initComponents();       
    }

    public BinaryFileManagementPanel<ExperimentBinaryFile> getBinaryFileManagementPanel() {
        return binaryFileManagementPanel;
    }    

    public JButton getCloseButton() {
        return closeButton;
    }        

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        binaryFileManagementParentPanel = new javax.swing.JPanel();
        binaryFileManagementPanel = new com.compomics.colims.client.compoment.BinaryFileManagementPanel<ExperimentBinaryFile>();
        closeButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("experiment metadata management");
        setBackground(new java.awt.Color(240, 240, 240));

        binaryFileManagementParentPanel.setBackground(new java.awt.Color(255, 255, 255));

        binaryFileManagementPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        closeButton.setText("close");
        closeButton.setMaximumSize(new java.awt.Dimension(80, 25));
        closeButton.setMinimumSize(new java.awt.Dimension(80, 25));
        closeButton.setPreferredSize(new java.awt.Dimension(80, 25));

        javax.swing.GroupLayout binaryFileManagementParentPanelLayout = new javax.swing.GroupLayout(binaryFileManagementParentPanel);
        binaryFileManagementParentPanel.setLayout(binaryFileManagementParentPanelLayout);
        binaryFileManagementParentPanelLayout.setHorizontalGroup(
            binaryFileManagementParentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(binaryFileManagementParentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(binaryFileManagementParentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(binaryFileManagementPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, binaryFileManagementParentPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        binaryFileManagementParentPanelLayout.setVerticalGroup(
            binaryFileManagementParentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(binaryFileManagementParentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(binaryFileManagementPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(binaryFileManagementParentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(binaryFileManagementParentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.compomics.colims.client.compoment.BinaryFileManagementPanel<ExperimentBinaryFile> binaryFileManagementPanel;
    private javax.swing.JPanel binaryFileManagementParentPanel;
    private javax.swing.JButton closeButton;
    // End of variables declaration//GEN-END:variables
}
