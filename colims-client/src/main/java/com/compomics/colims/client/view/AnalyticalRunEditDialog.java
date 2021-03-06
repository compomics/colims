package com.compomics.colims.client.view;

import com.compomics.colims.client.compoment.DateTimePicker;
import java.awt.Dialog;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;

/**
 *
 * @author Niels Hulstaert
 */
public class AnalyticalRunEditDialog extends javax.swing.JDialog {

    /**
     * Dialog constructor.
     *
     * @param parent the parent dialog
     * @param modal is the dialog modal
     */
    public AnalyticalRunEditDialog(final Dialog parent, final boolean modal) {
        super(parent, modal);
        initComponents();
    }

    public JButton getCloseButton() {
        return closeButton;
    }

    public DateTimePicker getDateTimePicker() {
        return dateTimePicker;
    }

    public JComboBox getInstrumentComboBox() {
        return instrumentComboBox;
    }

    public JButton getUpdateButton() {
        return updateButton;
    }

    public JTextField getNameTextField() {
        return nameTextField;
    }

    public JTextField getStorageLocationTextField() {
        return storageLocationTextField;
    }

    public JTextField getAttachementsTextField() {
        return attachementsTextField;
    }

    public JButton getAttachmentsEditButton() {
        return attachmentsEditButton;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        analyticalRunEditPanel = new javax.swing.JPanel();
        nameTextField = new javax.swing.JTextField();
        nameLabel = new javax.swing.JLabel();
        labelLabel = new javax.swing.JLabel();
        instrumentLabel = new javax.swing.JLabel();
        instrumentComboBox = new javax.swing.JComboBox();
        updateButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();
        dateTimePicker = new com.compomics.colims.client.compoment.DateTimePicker();
        locationLabel = new javax.swing.JLabel();
        storageLocationTextField = new javax.swing.JTextField();
        attachementsLabel = new javax.swing.JLabel();
        attachementsTextField = new javax.swing.JTextField();
        attachmentsEditButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Analytical run metadata management");

        analyticalRunEditPanel.setBackground(new java.awt.Color(255, 255, 255));

        nameLabel.setText("Name*");

        labelLabel.setText("Start Date");

        instrumentLabel.setText("Instrument");

        updateButton.setText("update");
        updateButton.setToolTipText("");
        updateButton.setMaximumSize(new java.awt.Dimension(80, 25));
        updateButton.setMinimumSize(new java.awt.Dimension(80, 25));
        updateButton.setPreferredSize(new java.awt.Dimension(80, 25));

        closeButton.setText("close");
        closeButton.setToolTipText("");
        closeButton.setMaximumSize(new java.awt.Dimension(80, 25));
        closeButton.setMinimumSize(new java.awt.Dimension(80, 25));
        closeButton.setPreferredSize(new java.awt.Dimension(80, 25));

        dateTimePicker.setMaximumSize(new java.awt.Dimension(104, 27));
        dateTimePicker.setMinimumSize(new java.awt.Dimension(104, 27));
        dateTimePicker.setName(""); // NOI18N
        dateTimePicker.setPreferredSize(new java.awt.Dimension(104, 27));

        locationLabel.setText("Location");

        attachementsLabel.setText("Attachments");

        attachementsTextField.setEditable(false);

        attachmentsEditButton.setText("edit...");
        attachmentsEditButton.setToolTipText("edit the binary attachments");
        attachmentsEditButton.setMaximumSize(new java.awt.Dimension(80, 25));
        attachmentsEditButton.setMinimumSize(new java.awt.Dimension(80, 25));
        attachmentsEditButton.setPreferredSize(new java.awt.Dimension(80, 25));

        javax.swing.GroupLayout analyticalRunEditPanelLayout = new javax.swing.GroupLayout(analyticalRunEditPanel);
        analyticalRunEditPanel.setLayout(analyticalRunEditPanelLayout);
        analyticalRunEditPanelLayout.setHorizontalGroup(
            analyticalRunEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(analyticalRunEditPanelLayout.createSequentialGroup()
                .addGroup(analyticalRunEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(analyticalRunEditPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(updateButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(analyticalRunEditPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(analyticalRunEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(analyticalRunEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(nameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(instrumentLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(labelLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(locationLabel)
                            .addComponent(attachementsLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(analyticalRunEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(analyticalRunEditPanelLayout.createSequentialGroup()
                                .addComponent(attachementsTextField)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(attachmentsEditButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(dateTimePicker, javax.swing.GroupLayout.DEFAULT_SIZE, 595, Short.MAX_VALUE)
                            .addComponent(nameTextField)
                            .addComponent(instrumentComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(storageLocationTextField))))
                .addContainerGap())
        );
        analyticalRunEditPanelLayout.setVerticalGroup(
            analyticalRunEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(analyticalRunEditPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(analyticalRunEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameLabel)
                    .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(analyticalRunEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelLabel)
                    .addComponent(dateTimePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(analyticalRunEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(instrumentLabel)
                    .addComponent(instrumentComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(analyticalRunEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(locationLabel)
                    .addComponent(storageLocationTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(analyticalRunEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(attachementsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(attachmentsEditButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(attachementsLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(analyticalRunEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(updateButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(analyticalRunEditPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(analyticalRunEditPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel analyticalRunEditPanel;
    private javax.swing.JLabel attachementsLabel;
    private javax.swing.JTextField attachementsTextField;
    private javax.swing.JButton attachmentsEditButton;
    private javax.swing.JButton closeButton;
    private com.compomics.colims.client.compoment.DateTimePicker dateTimePicker;
    private javax.swing.JComboBox instrumentComboBox;
    private javax.swing.JLabel instrumentLabel;
    private javax.swing.JLabel labelLabel;
    private javax.swing.JLabel locationLabel;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JTextField storageLocationTextField;
    private javax.swing.JButton updateButton;
    // End of variables declaration//GEN-END:variables
}
