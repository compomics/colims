package com.compomics.colims.client.view;

import com.compomics.colims.client.compoment.DualList;
import java.awt.Frame;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author Niels Hulstaert
 */
public class ProjectEditDialog extends javax.swing.JDialog {

    /**
     * Dialog constructor
     * 
     * @param parent the parent frame
     * @param modal the modal boolean
     */
    public ProjectEditDialog(final Frame parent, final boolean modal) {
        super(parent, modal);        
        initComponents();
    } 

    public JButton getCancelButton() {
        return cancelButton;
    }

    public JTextField getLabelTextField() {
        return labelTextField;
    }

    public JComboBox getOwnerComboBox() {
        return ownerComboBox;
    }

    public JTextArea getDescriptionTextArea() {
        return descriptionTextArea;
    }

    public JButton getSaveOrUpdateButton() {
        return saveOrUpdateButton;
    }

    public JTextField getTitleTextField() {
        return titleTextField;
    }

    public DualList getUserDualList() {
        return userDualList;
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
        descriptionLabel = new javax.swing.JLabel();
        labelTextField = new javax.swing.JTextField();
        titleTextField = new javax.swing.JTextField();
        titleLabel = new javax.swing.JLabel();
        descriptionScrollPanel = new javax.swing.JScrollPane();
        descriptionTextArea = new javax.swing.JTextArea();
        labelLabel = new javax.swing.JLabel();
        ownerLabel = new javax.swing.JLabel();
        ownerComboBox = new javax.swing.JComboBox();
        userDualList = new com.compomics.colims.client.compoment.DualList();
        userDualListLabel = new javax.swing.JLabel();
        saveOrUpdateButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Project metadata management");

        projectEditPanel.setBackground(new java.awt.Color(255, 255, 255));

        descriptionLabel.setText("Description");

        titleLabel.setText("Title*");

        descriptionTextArea.setColumns(20);
        descriptionTextArea.setLineWrap(true);
        descriptionTextArea.setRows(5);
        descriptionTextArea.setAutoscrolls(false);
        descriptionScrollPanel.setViewportView(descriptionTextArea);

        labelLabel.setText("Label*");

        ownerLabel.setText("Owner");

        userDualListLabel.setText("Users");

        saveOrUpdateButton.setText("save");
        saveOrUpdateButton.setToolTipText("");
        saveOrUpdateButton.setMaximumSize(new java.awt.Dimension(80, 25));
        saveOrUpdateButton.setMinimumSize(new java.awt.Dimension(80, 25));
        saveOrUpdateButton.setPreferredSize(new java.awt.Dimension(80, 25));

        cancelButton.setText("cancel");
        cancelButton.setToolTipText("");
        cancelButton.setMaximumSize(new java.awt.Dimension(80, 25));
        cancelButton.setMinimumSize(new java.awt.Dimension(80, 25));
        cancelButton.setPreferredSize(new java.awt.Dimension(80, 25));

        javax.swing.GroupLayout projectEditPanelLayout = new javax.swing.GroupLayout(projectEditPanel);
        projectEditPanel.setLayout(projectEditPanelLayout);
        projectEditPanelLayout.setHorizontalGroup(
            projectEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(projectEditPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(projectEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(projectEditPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(saveOrUpdateButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(projectEditPanelLayout.createSequentialGroup()
                        .addGroup(projectEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(ownerLabel)
                            .addComponent(descriptionLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(labelLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(userDualListLabel)
                            .addComponent(titleLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(projectEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelTextField)
                            .addGroup(projectEditPanelLayout.createSequentialGroup()
                                .addGroup(projectEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(titleTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 528, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(userDualList, javax.swing.GroupLayout.PREFERRED_SIZE, 541, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(projectEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(descriptionScrollPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 542, Short.MAX_VALUE)
                                        .addComponent(ownerComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        projectEditPanelLayout.setVerticalGroup(
            projectEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(projectEditPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(projectEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(titleLabel)
                    .addComponent(titleTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(projectEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelLabel)
                    .addComponent(labelTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(projectEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(descriptionScrollPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE)
                    .addComponent(descriptionLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(projectEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ownerComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ownerLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(projectEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(userDualList, javax.swing.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
                    .addComponent(userDualListLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(projectEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(saveOrUpdateButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel descriptionLabel;
    private javax.swing.JScrollPane descriptionScrollPanel;
    private javax.swing.JTextArea descriptionTextArea;
    private javax.swing.JLabel labelLabel;
    private javax.swing.JTextField labelTextField;
    private javax.swing.JComboBox ownerComboBox;
    private javax.swing.JLabel ownerLabel;
    private javax.swing.JPanel projectEditPanel;
    private javax.swing.JButton saveOrUpdateButton;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JTextField titleTextField;
    private com.compomics.colims.client.compoment.DualList userDualList;
    private javax.swing.JLabel userDualListLabel;
    // End of variables declaration//GEN-END:variables
}
