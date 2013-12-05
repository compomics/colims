package com.compomics.colims.client.view.admin.material;

import java.awt.Frame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;

/**
 *
 * @author niels
 */
public class MaterialManagementDialog extends javax.swing.JDialog {
    
    /**
     * Creates new form MaterialCvDialog
     */
    public MaterialManagementDialog(Frame parent, boolean modal) {
        super(parent, modal);
        
        initComponents();
        
        materialDetailsTableScrollPane.getViewport().setOpaque(false);
        setLocationRelativeTo(parent);
    }

    public JButton getAddMaterialButton() {
        return addMaterialButton;
    }

    public JButton getDeleteMaterialButton() {
        return deleteMaterialButton;
    }

    public JList getMaterialList() {
        return materialList;
    }  

    public JTable getMaterialDetailsTable() {
        return materialDetailsTable;
    }

    public JButton getEditMaterialButton() {
        return materialEditButton;
    }        

    public JButton getCancelMaterialManagementButton() {
        return cancelMaterialManagementButton;
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

        materialCrudPanel = new javax.swing.JPanel();
        materialOverviewPanel = new javax.swing.JPanel();
        materialListScrollPane = new javax.swing.JScrollPane();
        materialList = new javax.swing.JList();
        addMaterialButton = new javax.swing.JButton();
        deleteMaterialButton = new javax.swing.JButton();
        materialDetailPanel = new javax.swing.JPanel();
        materialDetailsTableScrollPane = new javax.swing.JScrollPane();
        materialDetailsTable = new javax.swing.JTable();
        materialEditButton = new javax.swing.JButton();
        materialStateInfoLabel = new javax.swing.JLabel();
        cancelMaterialManagementButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("material management overview");

        materialCrudPanel.setBackground(new java.awt.Color(255, 255, 255));

        materialOverviewPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("overview"));
        materialOverviewPanel.setOpaque(false);
        materialOverviewPanel.setPreferredSize(new java.awt.Dimension(100, 100));

        materialListScrollPane.setOpaque(false);

        materialListScrollPane.setViewportView(materialList);

        addMaterialButton.setText("add");
        addMaterialButton.setMaximumSize(new java.awt.Dimension(80, 25));
        addMaterialButton.setMinimumSize(new java.awt.Dimension(80, 25));
        addMaterialButton.setPreferredSize(new java.awt.Dimension(80, 25));

        deleteMaterialButton.setText("delete");
        deleteMaterialButton.setMaximumSize(new java.awt.Dimension(80, 25));
        deleteMaterialButton.setMinimumSize(new java.awt.Dimension(80, 25));
        deleteMaterialButton.setPreferredSize(new java.awt.Dimension(80, 25));

        javax.swing.GroupLayout materialOverviewPanelLayout = new javax.swing.GroupLayout(materialOverviewPanel);
        materialOverviewPanel.setLayout(materialOverviewPanelLayout);
        materialOverviewPanelLayout.setHorizontalGroup(
            materialOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(materialOverviewPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(materialListScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(materialOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(addMaterialButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(deleteMaterialButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        materialOverviewPanelLayout.setVerticalGroup(
            materialOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(materialOverviewPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(materialOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(materialOverviewPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(addMaterialButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteMaterialButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(materialListScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE))
                .addContainerGap())
        );

        materialDetailPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("detail"));
        materialDetailPanel.setOpaque(false);
        materialDetailPanel.setPreferredSize(new java.awt.Dimension(100, 100));

        materialDetailsTableScrollPane.setOpaque(false);

        materialDetailsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        materialDetailsTable.setOpaque(false);
        materialDetailsTableScrollPane.setViewportView(materialDetailsTable);

        materialEditButton.setText("edit...");
        materialEditButton.setToolTipText("save or update the selected material");
        materialEditButton.setPreferredSize(new java.awt.Dimension(80, 25));

        materialStateInfoLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        materialStateInfoLabel.setForeground(new java.awt.Color(255, 0, 0));

        javax.swing.GroupLayout materialDetailPanelLayout = new javax.swing.GroupLayout(materialDetailPanel);
        materialDetailPanel.setLayout(materialDetailPanelLayout);
        materialDetailPanelLayout.setHorizontalGroup(
            materialDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(materialDetailPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(materialDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(materialStateInfoLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(materialDetailPanelLayout.createSequentialGroup()
                        .addGroup(materialDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(materialDetailPanelLayout.createSequentialGroup()
                                .addGap(501, 501, 501)
                                .addComponent(materialEditButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(materialDetailsTableScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 581, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        materialDetailPanelLayout.setVerticalGroup(
            materialDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(materialDetailPanelLayout.createSequentialGroup()
                .addComponent(materialStateInfoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(materialDetailsTableScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(materialEditButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        cancelMaterialManagementButton.setText("cancel");
        cancelMaterialManagementButton.setPreferredSize(new java.awt.Dimension(80, 25));

        javax.swing.GroupLayout materialCrudPanelLayout = new javax.swing.GroupLayout(materialCrudPanel);
        materialCrudPanel.setLayout(materialCrudPanelLayout);
        materialCrudPanelLayout.setHorizontalGroup(
            materialCrudPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(materialCrudPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(materialCrudPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(materialOverviewPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 611, Short.MAX_VALUE)
                    .addComponent(materialDetailPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 611, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, materialCrudPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(cancelMaterialManagementButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        materialCrudPanelLayout.setVerticalGroup(
            materialCrudPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(materialCrudPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(materialOverviewPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 182, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(materialDetailPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancelMaterialManagementButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 631, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(materialCrudPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 538, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(materialCrudPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addMaterialButton;
    private javax.swing.JButton cancelMaterialManagementButton;
    private javax.swing.JButton deleteMaterialButton;
    private javax.swing.JPanel materialCrudPanel;
    private javax.swing.JPanel materialDetailPanel;
    private javax.swing.JTable materialDetailsTable;
    private javax.swing.JScrollPane materialDetailsTableScrollPane;
    private javax.swing.JButton materialEditButton;
    private javax.swing.JList materialList;
    private javax.swing.JScrollPane materialListScrollPane;
    private javax.swing.JPanel materialOverviewPanel;
    private javax.swing.JLabel materialStateInfoLabel;
    // End of variables declaration//GEN-END:variables
    
}
