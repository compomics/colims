package com.compomics.colims.client.view.admin.material;

import com.compomics.colims.client.compoment.DualList;
import com.compomics.colims.model.MaterialCvParam;
import java.awt.Dialog;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;

/**
 *
 * @author Niels Hulstaert
 */
public class MaterialEditDialog extends javax.swing.JDialog {

    /**
     * Dialog constructor
     *
     * @param parent the parent dialog
     * @param modal the modal boolean
     */
    public MaterialEditDialog(final Dialog parent, final boolean modal) {
        super(parent, modal);

        initComponents();

        setLocationRelativeTo(parent);
    }

    public JButton getCloseMaterialEditButton() {
        return closeMaterialEditButton;
    }

    public DualList<MaterialCvParam> getCvParamDualList() {
        return cvParamDualList;
    }

    public JList getCvParamSummaryList() {
        return cvParamSummaryList;
    }

    public JButton getMaterialCvParamsCrudButton() {
        return materialCvParamsCrudButton;
    }

    public JButton getMaterialSaveOrUpdateButton() {
        return materialSaveOrUpdateButton;
    }

    public JTextField getNameTextField() {
        return nameTextField;
    }

    public JLabel getMaterialStateInfoLabel() {
        return materialStateInfoLabel;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        materialEditPanel = new javax.swing.JPanel();
        closeMaterialEditButton = new javax.swing.JButton();
        materialSaveOrUpdateButton = new javax.swing.JButton();
        cvParamsPanel = new javax.swing.JPanel();
        cvParamSummaryScrollPane = new javax.swing.JScrollPane();
        cvParamSummaryList = new javax.swing.JList();
        cvParamDualList = new com.compomics.colims.client.compoment.DualList<>();
        materialCvParamsCrudButton = new javax.swing.JButton();
        nameLabel = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        materialStateInfoLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("material management");

        materialEditPanel.setBackground(new java.awt.Color(255, 255, 255));

        closeMaterialEditButton.setText("close");
        closeMaterialEditButton.setMaximumSize(new java.awt.Dimension(80, 25));
        closeMaterialEditButton.setMinimumSize(new java.awt.Dimension(80, 25));
        closeMaterialEditButton.setPreferredSize(new java.awt.Dimension(80, 25));

        materialSaveOrUpdateButton.setText("save");
        materialSaveOrUpdateButton.setMaximumSize(new java.awt.Dimension(80, 25));
        materialSaveOrUpdateButton.setMinimumSize(new java.awt.Dimension(80, 25));
        materialSaveOrUpdateButton.setPreferredSize(new java.awt.Dimension(80, 25));

        cvParamsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("CV params"));
        cvParamsPanel.setOpaque(false);

        cvParamSummaryScrollPane.setViewportView(cvParamSummaryList);

        materialCvParamsCrudButton.setText("edit...");
        materialCvParamsCrudButton.setToolTipText("edit the available CV params of the selected CV param type");
        materialCvParamsCrudButton.setMaximumSize(new java.awt.Dimension(80, 25));
        materialCvParamsCrudButton.setMinimumSize(new java.awt.Dimension(80, 25));
        materialCvParamsCrudButton.setPreferredSize(new java.awt.Dimension(80, 25));

        javax.swing.GroupLayout cvParamsPanelLayout = new javax.swing.GroupLayout(cvParamsPanel);
        cvParamsPanel.setLayout(cvParamsPanelLayout);
        cvParamsPanelLayout.setHorizontalGroup(
            cvParamsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, cvParamsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cvParamSummaryScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(cvParamsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(materialCvParamsCrudButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cvParamDualList, javax.swing.GroupLayout.DEFAULT_SIZE, 470, Short.MAX_VALUE))
                .addContainerGap())
        );
        cvParamsPanelLayout.setVerticalGroup(
            cvParamsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cvParamsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(cvParamsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cvParamSummaryScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE)
                    .addComponent(cvParamDualList, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(materialCvParamsCrudButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        nameLabel.setText("Name*");

        materialStateInfoLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        materialStateInfoLabel.setForeground(new java.awt.Color(255, 0, 0));
        materialStateInfoLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        javax.swing.GroupLayout materialEditPanelLayout = new javax.swing.GroupLayout(materialEditPanel);
        materialEditPanel.setLayout(materialEditPanelLayout);
        materialEditPanelLayout.setHorizontalGroup(
            materialEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(materialEditPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(materialEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cvParamsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, materialEditPanelLayout.createSequentialGroup()
                        .addComponent(materialStateInfoLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(materialSaveOrUpdateButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(closeMaterialEditButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(materialEditPanelLayout.createSequentialGroup()
                        .addComponent(nameLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(nameTextField)))
                .addContainerGap())
        );
        materialEditPanelLayout.setVerticalGroup(
            materialEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(materialEditPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(materialEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nameLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cvParamsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(materialEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(materialEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(materialSaveOrUpdateButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(closeMaterialEditButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(materialStateInfoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 720, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(materialEditPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 377, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(materialEditPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton closeMaterialEditButton;
    private com.compomics.colims.client.compoment.DualList<com.compomics.colims.model.MaterialCvParam> cvParamDualList;
    private javax.swing.JList cvParamSummaryList;
    private javax.swing.JScrollPane cvParamSummaryScrollPane;
    private javax.swing.JPanel cvParamsPanel;
    private javax.swing.JButton materialCvParamsCrudButton;
    private javax.swing.JPanel materialEditPanel;
    private javax.swing.JButton materialSaveOrUpdateButton;
    private javax.swing.JLabel materialStateInfoLabel;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JTextField nameTextField;
    // End of variables declaration//GEN-END:variables

}
