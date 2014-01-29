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
     * Dialog constructor
     * 
     * @param parent
     * @param modal
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
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        projectEditPanel = new javax.swing.JPanel();
        nameTextField = new javax.swing.JTextField();
        nameLabel = new javax.swing.JLabel();
        labelLabel = new javax.swing.JLabel();
        instrumentLabel = new javax.swing.JLabel();
        instrumentComboBox = new javax.swing.JComboBox();
        updateButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();
        dateTimePicker = new com.compomics.colims.client.compoment.DateTimePicker();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("project metadata management");

        projectEditPanel.setBackground(new java.awt.Color(255, 255, 255));

        nameLabel.setText("name");

        labelLabel.setText("start date");

        instrumentLabel.setText("instrument");

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

        javax.swing.GroupLayout projectEditPanelLayout = new javax.swing.GroupLayout(projectEditPanel);
        projectEditPanel.setLayout(projectEditPanelLayout);
        projectEditPanelLayout.setHorizontalGroup(
            projectEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(projectEditPanelLayout.createSequentialGroup()
                .addGroup(projectEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(projectEditPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(updateButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(projectEditPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(projectEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(projectEditPanelLayout.createSequentialGroup()
                                .addGroup(projectEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(labelLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(instrumentLabel))
                                .addGap(18, 18, 18)
                                .addGroup(projectEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(instrumentComboBox, 0, 255, Short.MAX_VALUE)
                                    .addComponent(dateTimePicker, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(projectEditPanelLayout.createSequentialGroup()
                                .addComponent(nameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        projectEditPanelLayout.setVerticalGroup(
            projectEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(projectEditPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(projectEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameLabel)
                    .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(projectEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelLabel)
                    .addComponent(dateTimePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(projectEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(instrumentLabel)
                    .addComponent(instrumentComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(projectEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(updateButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(projectEditPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(projectEditPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton closeButton;
    private com.compomics.colims.client.compoment.DateTimePicker dateTimePicker;
    private javax.swing.JComboBox instrumentComboBox;
    private javax.swing.JLabel instrumentLabel;
    private javax.swing.JLabel labelLabel;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JPanel projectEditPanel;
    private javax.swing.JButton updateButton;
    // End of variables declaration//GEN-END:variables
}
