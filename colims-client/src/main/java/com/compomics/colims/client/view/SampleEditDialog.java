package com.compomics.colims.client.view;

import com.compomics.colims.client.compoment.DualList;

import javax.swing.*;

/**
 *
 * @author Niels Hulstaert
 */
public class SampleEditDialog extends javax.swing.JDialog {

    private final JFileChooser exportDirectoryChooser = new JFileChooser();

    /**
     * Dialog constructor.
     *
     * @param parent the parent dialog
     * @param modal is the dialog modal
     */
    public SampleEditDialog(final JFrame parent, final boolean modal) {
        super(parent, modal);

        initComponents();

        //select only directories
        exportDirectoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        //select multiple file
        exportDirectoryChooser.setMultiSelectionEnabled(Boolean.FALSE);

    }

    public DualList getMaterialDualList() {
        return materialDualList;
    }

    public JTextField getAttachementsTextField() {
        return attachementsTextField;
    }

    public JButton getAttachmentsEditButton() {
        return attachmentsEditButton;
    }

    public JButton getCancelButton() {
        return closeButton;
    }

    public JTextField getConditionTextField() {
        return conditionTextField;
    }

    public JTextField getNameTextField() {
        return nameTextField;
    }

    public JComboBox getProtocolComboBox() {
        return protocolComboBox;
    }

    public JButton getSaveOrUpdateButton() {
        return saveOrUpdateButton;
    }

    public JTextField getStorageLocationTextField() {
        return storageLocationTextField;
    }

    public JFileChooser getExportDirectoryChooser() {
        return exportDirectoryChooser;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sampleEditPanel = new javax.swing.JPanel();
        saveOrUpdateButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();
        sampleParentPanel = new javax.swing.JPanel();
        detailPanel = new javax.swing.JPanel();
        attachementsLabel = new javax.swing.JLabel();
        conditionTextField = new javax.swing.JTextField();
        nameLabel = new javax.swing.JLabel();
        storageLocationLabel = new javax.swing.JLabel();
        attachementsTextField = new javax.swing.JTextField();
        storageLocationTextField = new javax.swing.JTextField();
        attachmentsEditButton = new javax.swing.JButton();
        protocolLabel = new javax.swing.JLabel();
        conditionLabel = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        protocolComboBox = new javax.swing.JComboBox();
        materialDualList = new com.compomics.colims.client.compoment.DualList();
        attachementsLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Sample metadata management");

        sampleEditPanel.setBackground(new java.awt.Color(255, 255, 255));

        saveOrUpdateButton.setText("save");
        saveOrUpdateButton.setToolTipText("");
        saveOrUpdateButton.setMaximumSize(new java.awt.Dimension(80, 25));
        saveOrUpdateButton.setMinimumSize(new java.awt.Dimension(80, 25));
        saveOrUpdateButton.setPreferredSize(new java.awt.Dimension(80, 25));

        closeButton.setText("close");
        closeButton.setToolTipText("");
        closeButton.setMaximumSize(new java.awt.Dimension(80, 25));
        closeButton.setMinimumSize(new java.awt.Dimension(80, 25));
        closeButton.setPreferredSize(new java.awt.Dimension(80, 25));

        sampleParentPanel.setOpaque(false);

        detailPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Detail"));
        detailPanel.setOpaque(false);

        attachementsLabel.setText("Attachments");

        nameLabel.setText("Name*");

        storageLocationLabel.setText("Storage location");

        attachementsTextField.setEditable(false);

        attachmentsEditButton.setText("edit...");
        attachmentsEditButton.setToolTipText("edit the binary attachments");
        attachmentsEditButton.setMaximumSize(new java.awt.Dimension(80, 25));
        attachmentsEditButton.setMinimumSize(new java.awt.Dimension(80, 25));
        attachmentsEditButton.setPreferredSize(new java.awt.Dimension(80, 25));

        protocolLabel.setText("Protocol");

        conditionLabel.setText("Condition");

        attachementsLabel1.setText("Materials");

        javax.swing.GroupLayout detailPanelLayout = new javax.swing.GroupLayout(detailPanel);
        detailPanel.setLayout(detailPanelLayout);
        detailPanelLayout.setHorizontalGroup(
            detailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(detailPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(detailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(detailPanelLayout.createSequentialGroup()
                        .addGroup(detailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(conditionLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(nameLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(protocolLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(detailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(nameTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 542, Short.MAX_VALUE)
                            .addComponent(conditionTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 542, Short.MAX_VALUE)
                            .addComponent(protocolComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(detailPanelLayout.createSequentialGroup()
                        .addGroup(detailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(attachementsLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(attachementsLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(detailPanelLayout.createSequentialGroup()
                                .addComponent(storageLocationLabel)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(detailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(detailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(detailPanelLayout.createSequentialGroup()
                                    .addComponent(attachementsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 456, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(attachmentsEditButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(storageLocationTextField, javax.swing.GroupLayout.Alignment.TRAILING))
                            .addComponent(materialDualList, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 542, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        detailPanelLayout.setVerticalGroup(
            detailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(detailPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(detailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameLabel)
                    .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(detailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(conditionLabel)
                    .addComponent(conditionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(detailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(protocolLabel)
                    .addComponent(protocolComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(detailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(storageLocationTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(storageLocationLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(detailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(attachementsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(attachmentsEditButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(attachementsLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(detailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(attachementsLabel1)
                    .addComponent(materialDualList, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout sampleParentPanelLayout = new javax.swing.GroupLayout(sampleParentPanel);
        sampleParentPanel.setLayout(sampleParentPanelLayout);
        sampleParentPanelLayout.setHorizontalGroup(
            sampleParentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(detailPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        sampleParentPanelLayout.setVerticalGroup(
            sampleParentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sampleParentPanelLayout.createSequentialGroup()
                .addComponent(detailPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout sampleEditPanelLayout = new javax.swing.GroupLayout(sampleEditPanel);
        sampleEditPanel.setLayout(sampleEditPanelLayout);
        sampleEditPanelLayout.setHorizontalGroup(
            sampleEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sampleEditPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(saveOrUpdateButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(sampleEditPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(sampleParentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        sampleEditPanelLayout.setVerticalGroup(
            sampleEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sampleEditPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(sampleParentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(sampleEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(saveOrUpdateButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sampleEditPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sampleEditPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel attachementsLabel;
    private javax.swing.JLabel attachementsLabel1;
    private javax.swing.JTextField attachementsTextField;
    private javax.swing.JButton attachmentsEditButton;
    private javax.swing.JButton closeButton;
    private javax.swing.JLabel conditionLabel;
    private javax.swing.JTextField conditionTextField;
    private javax.swing.JPanel detailPanel;
    private com.compomics.colims.client.compoment.DualList materialDualList;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JComboBox protocolComboBox;
    private javax.swing.JLabel protocolLabel;
    private javax.swing.JPanel sampleEditPanel;
    private javax.swing.JPanel sampleParentPanel;
    private javax.swing.JButton saveOrUpdateButton;
    private javax.swing.JLabel storageLocationLabel;
    private javax.swing.JTextField storageLocationTextField;
    // End of variables declaration//GEN-END:variables
}
