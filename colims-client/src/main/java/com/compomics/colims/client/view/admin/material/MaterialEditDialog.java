package com.compomics.colims.client.view.admin.material;

import com.compomics.colims.client.compoment.DualList;
import com.compomics.colims.model.MaterialCvTerm;
import java.awt.Frame;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JTextField;

/**
 *
 * @author niels
 */
public class MaterialEditDialog extends javax.swing.JDialog {
    
    /**
     * Creates new form InstrumentCvDialog
     */
    public MaterialEditDialog(Frame parent, boolean modal) {
        super(parent, modal);
        
        initComponents();
        
        setLocationRelativeTo(parent);
    }

    public JButton getCancelMaterialEditButton() {
        return cancelMaterialEditButton;
    }

    public DualList<MaterialCvTerm> getCvTermDualList() {
        return cvTermDualList;
    }

    public JList getCvTermSummaryList() {
        return cvTermSummaryList;
    }

    public JButton getMaterialCvTermsCrudButton() {
        return materialCvTermsCrudButton;
    }

    public JButton getMaterialSaveOrUpdateButton() {
        return materialSaveOrUpdateButton;
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

        materialEditPanel = new javax.swing.JPanel();
        cancelMaterialEditButton = new javax.swing.JButton();
        materialSaveOrUpdateButton = new javax.swing.JButton();
        cvTermsPanel = new javax.swing.JPanel();
        cvTermSummaryScrollPane = new javax.swing.JScrollPane();
        cvTermSummaryList = new javax.swing.JList();
        cvTermDualList = new com.compomics.colims.client.compoment.DualList<MaterialCvTerm>();
        materialCvTermsCrudButton = new javax.swing.JButton();
        nameLabel = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("material management");

        materialEditPanel.setBackground(new java.awt.Color(255, 255, 255));

        cancelMaterialEditButton.setText("cancel");
        cancelMaterialEditButton.setMaximumSize(new java.awt.Dimension(80, 25));
        cancelMaterialEditButton.setMinimumSize(new java.awt.Dimension(80, 25));
        cancelMaterialEditButton.setPreferredSize(new java.awt.Dimension(80, 25));

        materialSaveOrUpdateButton.setText("save");
        materialSaveOrUpdateButton.setMaximumSize(new java.awt.Dimension(80, 25));
        materialSaveOrUpdateButton.setMinimumSize(new java.awt.Dimension(80, 25));
        materialSaveOrUpdateButton.setPreferredSize(new java.awt.Dimension(80, 25));

        cvTermsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("CV terms"));
        cvTermsPanel.setOpaque(false);

        cvTermSummaryScrollPane.setOpaque(false);

        cvTermSummaryScrollPane.setViewportView(cvTermSummaryList);

        materialCvTermsCrudButton.setText("edit...");
        materialCvTermsCrudButton.setToolTipText("edit the available CV terms of the selected CV term type");
        materialCvTermsCrudButton.setMaximumSize(new java.awt.Dimension(80, 25));
        materialCvTermsCrudButton.setMinimumSize(new java.awt.Dimension(80, 25));
        materialCvTermsCrudButton.setPreferredSize(new java.awt.Dimension(80, 25));

        javax.swing.GroupLayout cvTermsPanelLayout = new javax.swing.GroupLayout(cvTermsPanel);
        cvTermsPanel.setLayout(cvTermsPanelLayout);
        cvTermsPanelLayout.setHorizontalGroup(
            cvTermsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cvTermsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cvTermSummaryScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 385, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(cvTermsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(materialCvTermsCrudButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cvTermDualList, javax.swing.GroupLayout.DEFAULT_SIZE, 464, Short.MAX_VALUE))
                .addContainerGap())
        );
        cvTermsPanelLayout.setVerticalGroup(
            cvTermsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cvTermsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(cvTermsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cvTermSummaryScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cvTermDualList, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(materialCvTermsCrudButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        nameLabel.setText("name");

        javax.swing.GroupLayout materialEditPanelLayout = new javax.swing.GroupLayout(materialEditPanel);
        materialEditPanel.setLayout(materialEditPanelLayout);
        materialEditPanelLayout.setHorizontalGroup(
            materialEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(materialEditPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(materialEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cvTermsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, materialEditPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(materialSaveOrUpdateButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelMaterialEditButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(materialEditPanelLayout.createSequentialGroup()
                        .addComponent(nameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        materialEditPanelLayout.setVerticalGroup(
            materialEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(materialEditPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(materialEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameLabel)
                    .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(cvTermsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                .addGroup(materialEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(materialSaveOrUpdateButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cancelMaterialEditButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 919, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(materialEditPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 397, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(materialEditPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelMaterialEditButton;
    private com.compomics.colims.client.compoment.DualList<MaterialCvTerm> cvTermDualList;
    private javax.swing.JList cvTermSummaryList;
    private javax.swing.JScrollPane cvTermSummaryScrollPane;
    private javax.swing.JPanel cvTermsPanel;
    private javax.swing.JButton materialCvTermsCrudButton;
    private javax.swing.JPanel materialEditPanel;
    private javax.swing.JButton materialSaveOrUpdateButton;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JTextField nameTextField;
    // End of variables declaration//GEN-END:variables
    
}
