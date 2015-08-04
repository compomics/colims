package com.compomics.colims.client.view.admin.protocol;

import com.compomics.colims.client.compoment.DualList;
import com.compomics.colims.model.ProtocolCvParam;
import java.awt.Dialog;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;

/**
 *
 * @author Niels Hulstaert
 */
public class ProtocolEditDialog extends javax.swing.JDialog {

    /**
     * Dialog constructor
     *
     * @param parent the parent dialog
     * @param modal the modal boolean
     */
    public ProtocolEditDialog(final Dialog parent, final boolean modal) {
        super(parent, modal);

        initComponents();

        setLocationRelativeTo(parent);
    }

    public JButton getCancelProtocolEditButton() {
        return cancelProtocolEditButton;
    }

    public DualList<ProtocolCvParam> getCvParamDualList() {
        return cvParamDualList;
    }

    public JList getCvParamSummaryList() {
        return cvParamSummaryList;
    }

    public JButton getProtocolCvParamsCrudButton() {
        return protocolCvParamsCrudButton;
    }

    public JButton getProtocolSaveOrUpdateButton() {
        return protocolSaveOrUpdateButton;
    }

    public JTextField getNameTextField() {
        return nameTextField;
    }

    public JLabel getProtocolStateInfoLabel() {
        return protocolStateInfoLabel;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        protocolEditPanel = new javax.swing.JPanel();
        cancelProtocolEditButton = new javax.swing.JButton();
        protocolSaveOrUpdateButton = new javax.swing.JButton();
        cvParamsPanel = new javax.swing.JPanel();
        cvParamSummaryScrollPane = new javax.swing.JScrollPane();
        cvParamSummaryList = new javax.swing.JList();
        cvParamDualList = new com.compomics.colims.client.compoment.DualList<>();
        protocolCvParamsCrudButton = new javax.swing.JButton();
        nameLabel = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        protocolStateInfoLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("protocol management");
        setPreferredSize(new java.awt.Dimension(720, 377));

        protocolEditPanel.setBackground(new java.awt.Color(255, 255, 255));

        cancelProtocolEditButton.setText("cancel");
        cancelProtocolEditButton.setMaximumSize(new java.awt.Dimension(80, 25));
        cancelProtocolEditButton.setMinimumSize(new java.awt.Dimension(80, 25));
        cancelProtocolEditButton.setPreferredSize(new java.awt.Dimension(80, 25));

        protocolSaveOrUpdateButton.setText("save");
        protocolSaveOrUpdateButton.setMaximumSize(new java.awt.Dimension(80, 25));
        protocolSaveOrUpdateButton.setMinimumSize(new java.awt.Dimension(80, 25));
        protocolSaveOrUpdateButton.setPreferredSize(new java.awt.Dimension(80, 25));

        cvParamsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("CV params"));
        cvParamsPanel.setOpaque(false);

        cvParamSummaryScrollPane.setViewportView(cvParamSummaryList);

        protocolCvParamsCrudButton.setText("edit...");
        protocolCvParamsCrudButton.setToolTipText("edit the available CV params of the selected CV param type");
        protocolCvParamsCrudButton.setMaximumSize(new java.awt.Dimension(80, 25));
        protocolCvParamsCrudButton.setMinimumSize(new java.awt.Dimension(80, 25));
        protocolCvParamsCrudButton.setPreferredSize(new java.awt.Dimension(80, 25));

        javax.swing.GroupLayout cvParamsPanelLayout = new javax.swing.GroupLayout(cvParamsPanel);
        cvParamsPanel.setLayout(cvParamsPanelLayout);
        cvParamsPanelLayout.setHorizontalGroup(
            cvParamsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, cvParamsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cvParamSummaryScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(cvParamsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(protocolCvParamsCrudButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cvParamDualList, javax.swing.GroupLayout.DEFAULT_SIZE, 470, Short.MAX_VALUE))
                .addContainerGap())
        );
        cvParamsPanelLayout.setVerticalGroup(
            cvParamsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cvParamsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(cvParamsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cvParamSummaryScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE)
                    .addComponent(cvParamDualList, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(protocolCvParamsCrudButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        nameLabel.setText("Name*");

        protocolStateInfoLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        protocolStateInfoLabel.setForeground(new java.awt.Color(255, 0, 0));
        protocolStateInfoLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        javax.swing.GroupLayout protocolEditPanelLayout = new javax.swing.GroupLayout(protocolEditPanel);
        protocolEditPanel.setLayout(protocolEditPanelLayout);
        protocolEditPanelLayout.setHorizontalGroup(
            protocolEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(protocolEditPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(protocolEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cvParamsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, protocolEditPanelLayout.createSequentialGroup()
                        .addComponent(protocolStateInfoLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(protocolSaveOrUpdateButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelProtocolEditButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, protocolEditPanelLayout.createSequentialGroup()
                        .addComponent(nameLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(nameTextField)))
                .addContainerGap())
        );
        protocolEditPanelLayout.setVerticalGroup(
            protocolEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(protocolEditPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(protocolEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(protocolEditPanelLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(nameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cvParamsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(protocolEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(protocolEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(protocolSaveOrUpdateButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cancelProtocolEditButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(protocolStateInfoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 720, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(protocolEditPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 375, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(protocolEditPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelProtocolEditButton;
    private com.compomics.colims.client.compoment.DualList<com.compomics.colims.model.ProtocolCvParam> cvParamDualList;
    private javax.swing.JList cvParamSummaryList;
    private javax.swing.JScrollPane cvParamSummaryScrollPane;
    private javax.swing.JPanel cvParamsPanel;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JButton protocolCvParamsCrudButton;
    private javax.swing.JPanel protocolEditPanel;
    private javax.swing.JButton protocolSaveOrUpdateButton;
    private javax.swing.JLabel protocolStateInfoLabel;
    // End of variables declaration//GEN-END:variables

}
