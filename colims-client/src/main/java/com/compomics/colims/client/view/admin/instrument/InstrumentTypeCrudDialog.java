package com.compomics.colims.client.view.admin.instrument;

import java.awt.Dialog;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author Niels Hulstaert
 */
public class InstrumentTypeCrudDialog extends javax.swing.JDialog {
    
    /**
     * Creates new form InstrumentCvDialog.
     */
    public InstrumentTypeCrudDialog(final Dialog parent, final boolean modal) {
        super(parent, modal);
        
        initComponents();
        
        setLocationRelativeTo(parent);
    }

    public JButton getAddInstrumentTypeButton() {
        return addInstrumentTypeButton;
    }

    public JButton getDeleteInstrumentTypeButton() {
        return deleteInstrumentTypeButton;
    }

    public JTextArea getInstrumentTypeDescriptionTextArea() {
        return instrumentTypeDescriptionTextArea;
    }

    public JList getInstrumentTypeList() {
        return instrumentTypeList;
    }

    public JTextField getInstrumentTypeNameTextField() {
        return instrumentTypeNameTextField;
    }

    public JButton getInstrumentTypeSaveOrUpdateButton() {
        return instrumentTypeSaveOrUpdateButton;
    }

    public JLabel getInstrumentTypeStateInfoLabel() {
        return instrumentTypeStateInfoLabel;
    }        

    public JButton getCloseInstrumentTypeCrudButton() {
        return closeInstrumentTypeCrudButton;
    }        

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        instrumentTypeCrudPanel = new javax.swing.JPanel();
        instrumentTypeOverviewPanel = new javax.swing.JPanel();
        instrumentTypeListScrollPane = new javax.swing.JScrollPane();
        instrumentTypeList = new javax.swing.JList();
        addInstrumentTypeButton = new javax.swing.JButton();
        deleteInstrumentTypeButton = new javax.swing.JButton();
        instrumentTypeDetailPanel = new javax.swing.JPanel();
        instrumentTypeDescriptionLabel = new javax.swing.JLabel();
        instrumentTypeNameLabel = new javax.swing.JLabel();
        instrumentTypeNameTextField = new javax.swing.JTextField();
        instrumentTypeSaveOrUpdateButton = new javax.swing.JButton();
        instrumentTypeStateInfoLabel = new javax.swing.JLabel();
        instrumentTypeDescriptionScrollPane = new javax.swing.JScrollPane();
        instrumentTypeDescriptionTextArea = new javax.swing.JTextArea();
        closeInstrumentTypeCrudButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("instrument type management");

        instrumentTypeCrudPanel.setBackground(new java.awt.Color(255, 255, 255));

        instrumentTypeOverviewPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("overview"));
        instrumentTypeOverviewPanel.setOpaque(false);
        instrumentTypeOverviewPanel.setPreferredSize(new java.awt.Dimension(100, 100));

        instrumentTypeListScrollPane.setOpaque(false);

        instrumentTypeListScrollPane.setViewportView(instrumentTypeList);

        addInstrumentTypeButton.setText("add");
        addInstrumentTypeButton.setMaximumSize(new java.awt.Dimension(80, 25));
        addInstrumentTypeButton.setMinimumSize(new java.awt.Dimension(80, 25));
        addInstrumentTypeButton.setPreferredSize(new java.awt.Dimension(80, 25));

        deleteInstrumentTypeButton.setText("delete");
        deleteInstrumentTypeButton.setMaximumSize(new java.awt.Dimension(80, 25));
        deleteInstrumentTypeButton.setMinimumSize(new java.awt.Dimension(80, 25));
        deleteInstrumentTypeButton.setPreferredSize(new java.awt.Dimension(80, 25));

        javax.swing.GroupLayout instrumentTypeOverviewPanelLayout = new javax.swing.GroupLayout(instrumentTypeOverviewPanel);
        instrumentTypeOverviewPanel.setLayout(instrumentTypeOverviewPanelLayout);
        instrumentTypeOverviewPanelLayout.setHorizontalGroup(
            instrumentTypeOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(instrumentTypeOverviewPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(instrumentTypeListScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(instrumentTypeOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(deleteInstrumentTypeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addInstrumentTypeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        instrumentTypeOverviewPanelLayout.setVerticalGroup(
            instrumentTypeOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(instrumentTypeOverviewPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(instrumentTypeOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(instrumentTypeOverviewPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(addInstrumentTypeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteInstrumentTypeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(instrumentTypeListScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        instrumentTypeDetailPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("detail"));
        instrumentTypeDetailPanel.setOpaque(false);
        instrumentTypeDetailPanel.setPreferredSize(new java.awt.Dimension(100, 100));

        instrumentTypeDescriptionLabel.setText("description");

        instrumentTypeNameLabel.setText("name");
        instrumentTypeNameLabel.setPreferredSize(new java.awt.Dimension(48, 14));

        instrumentTypeSaveOrUpdateButton.setText("save");
        instrumentTypeSaveOrUpdateButton.setMaximumSize(new java.awt.Dimension(80, 25));
        instrumentTypeSaveOrUpdateButton.setMinimumSize(new java.awt.Dimension(80, 25));
        instrumentTypeSaveOrUpdateButton.setPreferredSize(new java.awt.Dimension(80, 25));

        instrumentTypeStateInfoLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        instrumentTypeStateInfoLabel.setForeground(new java.awt.Color(255, 0, 0));
        instrumentTypeStateInfoLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        instrumentTypeStateInfoLabel.setMaximumSize(new java.awt.Dimension(100, 20));
        instrumentTypeStateInfoLabel.setMinimumSize(new java.awt.Dimension(100, 20));
        instrumentTypeStateInfoLabel.setPreferredSize(new java.awt.Dimension(100, 20));

        instrumentTypeDescriptionTextArea.setColumns(20);
        instrumentTypeDescriptionTextArea.setLineWrap(true);
        instrumentTypeDescriptionTextArea.setRows(5);
        instrumentTypeDescriptionScrollPane.setViewportView(instrumentTypeDescriptionTextArea);

        javax.swing.GroupLayout instrumentTypeDetailPanelLayout = new javax.swing.GroupLayout(instrumentTypeDetailPanel);
        instrumentTypeDetailPanel.setLayout(instrumentTypeDetailPanelLayout);
        instrumentTypeDetailPanelLayout.setHorizontalGroup(
            instrumentTypeDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(instrumentTypeDetailPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(instrumentTypeDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(instrumentTypeDetailPanelLayout.createSequentialGroup()
                        .addComponent(instrumentTypeNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(instrumentTypeNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 267, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(instrumentTypeDetailPanelLayout.createSequentialGroup()
                        .addComponent(instrumentTypeDescriptionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(instrumentTypeDescriptionScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 267, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(instrumentTypeStateInfoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 379, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, instrumentTypeDetailPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(instrumentTypeSaveOrUpdateButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        instrumentTypeDetailPanelLayout.setVerticalGroup(
            instrumentTypeDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(instrumentTypeDetailPanelLayout.createSequentialGroup()
                .addComponent(instrumentTypeStateInfoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(instrumentTypeDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(instrumentTypeNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(instrumentTypeNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(instrumentTypeDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(instrumentTypeDescriptionLabel)
                    .addComponent(instrumentTypeDescriptionScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(instrumentTypeSaveOrUpdateButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        closeInstrumentTypeCrudButton.setText("close");
        closeInstrumentTypeCrudButton.setMaximumSize(new java.awt.Dimension(80, 25));
        closeInstrumentTypeCrudButton.setMinimumSize(new java.awt.Dimension(80, 25));
        closeInstrumentTypeCrudButton.setPreferredSize(new java.awt.Dimension(80, 25));

        javax.swing.GroupLayout instrumentTypeCrudPanelLayout = new javax.swing.GroupLayout(instrumentTypeCrudPanel);
        instrumentTypeCrudPanel.setLayout(instrumentTypeCrudPanelLayout);
        instrumentTypeCrudPanelLayout.setHorizontalGroup(
            instrumentTypeCrudPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, instrumentTypeCrudPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(instrumentTypeCrudPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(instrumentTypeOverviewPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 411, Short.MAX_VALUE)
                    .addGroup(instrumentTypeCrudPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(closeInstrumentTypeCrudButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(instrumentTypeDetailPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 411, Short.MAX_VALUE))
                .addContainerGap())
        );
        instrumentTypeCrudPanelLayout.setVerticalGroup(
            instrumentTypeCrudPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, instrumentTypeCrudPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(instrumentTypeOverviewPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(instrumentTypeDetailPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(closeInstrumentTypeCrudButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 431, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(instrumentTypeCrudPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 486, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(instrumentTypeCrudPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addInstrumentTypeButton;
    private javax.swing.JButton closeInstrumentTypeCrudButton;
    private javax.swing.JButton deleteInstrumentTypeButton;
    private javax.swing.JPanel instrumentTypeCrudPanel;
    private javax.swing.JLabel instrumentTypeDescriptionLabel;
    private javax.swing.JScrollPane instrumentTypeDescriptionScrollPane;
    private javax.swing.JTextArea instrumentTypeDescriptionTextArea;
    private javax.swing.JPanel instrumentTypeDetailPanel;
    private javax.swing.JList instrumentTypeList;
    private javax.swing.JScrollPane instrumentTypeListScrollPane;
    private javax.swing.JLabel instrumentTypeNameLabel;
    private javax.swing.JTextField instrumentTypeNameTextField;
    private javax.swing.JPanel instrumentTypeOverviewPanel;
    private javax.swing.JButton instrumentTypeSaveOrUpdateButton;
    private javax.swing.JLabel instrumentTypeStateInfoLabel;
    // End of variables declaration//GEN-END:variables
    
}
