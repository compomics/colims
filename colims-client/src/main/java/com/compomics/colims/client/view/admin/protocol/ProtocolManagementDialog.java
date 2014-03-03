package com.compomics.colims.client.view.admin.protocol;

import java.awt.Frame;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JTable;

/**
 *
 * @author Niels Hulstaert
 */
public class ProtocolManagementDialog extends javax.swing.JDialog {
    
     /**
     * Dialog constructor
     * 
     * @param parent the parent frame
     * @param modal the modal boolean
     */
    public ProtocolManagementDialog(final Frame parent, final boolean modal) {
        super(parent, modal);
        
        initComponents();
        
        protocolDetailsTableScrollPane.getViewport().setOpaque(false);
        setLocationRelativeTo(parent);
    }

    public JButton getAddProtocolButton() {
        return addProtocolButton;
    }

    public JButton getDeleteProtocolButton() {
        return deleteProtocolButton;
    }

    public JList getProtocolList() {
        return protocolList;
    }  

    public JTable getProtocolDetailsTable() {
        return protocolDetailsTable;
    }

    public JButton getEditProtocolButton() {
        return protocolEditButton;
    }        

    public JButton getCloseProtocolManagementButton() {
        return closeProtocolManagementButton;
    }           

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        protocolCrudPanel = new javax.swing.JPanel();
        protocolOverviewPanel = new javax.swing.JPanel();
        protocolListScrollPane = new javax.swing.JScrollPane();
        protocolList = new javax.swing.JList();
        addProtocolButton = new javax.swing.JButton();
        deleteProtocolButton = new javax.swing.JButton();
        protocolEditButton = new javax.swing.JButton();
        protocolDetailPanel = new javax.swing.JPanel();
        protocolDetailsTableScrollPane = new javax.swing.JScrollPane();
        protocolDetailsTable = new javax.swing.JTable();
        closeProtocolManagementButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("protocol management overview");

        protocolCrudPanel.setBackground(new java.awt.Color(255, 255, 255));

        protocolOverviewPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("overview"));
        protocolOverviewPanel.setOpaque(false);
        protocolOverviewPanel.setPreferredSize(new java.awt.Dimension(100, 100));

        protocolListScrollPane.setOpaque(false);

        protocolListScrollPane.setViewportView(protocolList);

        addProtocolButton.setText("add...");
        addProtocolButton.setMaximumSize(new java.awt.Dimension(80, 25));
        addProtocolButton.setMinimumSize(new java.awt.Dimension(80, 25));
        addProtocolButton.setPreferredSize(new java.awt.Dimension(80, 25));

        deleteProtocolButton.setText("delete");
        deleteProtocolButton.setMaximumSize(new java.awt.Dimension(80, 25));
        deleteProtocolButton.setMinimumSize(new java.awt.Dimension(80, 25));
        deleteProtocolButton.setPreferredSize(new java.awt.Dimension(80, 25));

        protocolEditButton.setText("edit...");
        protocolEditButton.setToolTipText("save or update the selected protocol");
        protocolEditButton.setPreferredSize(new java.awt.Dimension(80, 25));

        javax.swing.GroupLayout protocolOverviewPanelLayout = new javax.swing.GroupLayout(protocolOverviewPanel);
        protocolOverviewPanel.setLayout(protocolOverviewPanelLayout);
        protocolOverviewPanelLayout.setHorizontalGroup(
            protocolOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(protocolOverviewPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(protocolListScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(protocolOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(protocolOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(addProtocolButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(deleteProtocolButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(protocolEditButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        protocolOverviewPanelLayout.setVerticalGroup(
            protocolOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(protocolOverviewPanelLayout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(protocolOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(protocolListScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE)
                    .addGroup(protocolOverviewPanelLayout.createSequentialGroup()
                        .addComponent(addProtocolButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(protocolEditButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteProtocolButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        protocolDetailPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("detail"));
        protocolDetailPanel.setOpaque(false);
        protocolDetailPanel.setPreferredSize(new java.awt.Dimension(100, 100));

        protocolDetailsTableScrollPane.setOpaque(false);

        protocolDetailsTable.setOpaque(false);
        protocolDetailsTableScrollPane.setViewportView(protocolDetailsTable);

        javax.swing.GroupLayout protocolDetailPanelLayout = new javax.swing.GroupLayout(protocolDetailPanel);
        protocolDetailPanel.setLayout(protocolDetailPanelLayout);
        protocolDetailPanelLayout.setHorizontalGroup(
            protocolDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(protocolDetailPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(protocolDetailsTableScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 581, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        protocolDetailPanelLayout.setVerticalGroup(
            protocolDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(protocolDetailPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(protocolDetailsTableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
                .addContainerGap())
        );

        closeProtocolManagementButton.setText("close");
        closeProtocolManagementButton.setPreferredSize(new java.awt.Dimension(80, 25));

        javax.swing.GroupLayout protocolCrudPanelLayout = new javax.swing.GroupLayout(protocolCrudPanel);
        protocolCrudPanel.setLayout(protocolCrudPanelLayout);
        protocolCrudPanelLayout.setHorizontalGroup(
            protocolCrudPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(protocolCrudPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(protocolCrudPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(protocolOverviewPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 611, Short.MAX_VALUE)
                    .addComponent(protocolDetailPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 611, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, protocolCrudPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(closeProtocolManagementButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        protocolCrudPanelLayout.setVerticalGroup(
            protocolCrudPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, protocolCrudPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(protocolOverviewPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(protocolDetailPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 239, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(closeProtocolManagementButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 631, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(protocolCrudPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 492, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(protocolCrudPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addProtocolButton;
    private javax.swing.JButton closeProtocolManagementButton;
    private javax.swing.JButton deleteProtocolButton;
    private javax.swing.JPanel protocolCrudPanel;
    private javax.swing.JPanel protocolDetailPanel;
    private javax.swing.JTable protocolDetailsTable;
    private javax.swing.JScrollPane protocolDetailsTableScrollPane;
    private javax.swing.JButton protocolEditButton;
    private javax.swing.JList protocolList;
    private javax.swing.JScrollPane protocolListScrollPane;
    private javax.swing.JPanel protocolOverviewPanel;
    // End of variables declaration//GEN-END:variables
    
}
