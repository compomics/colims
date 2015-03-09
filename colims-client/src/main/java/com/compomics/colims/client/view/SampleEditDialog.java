package com.compomics.colims.client.view;

import com.compomics.colims.client.compoment.DualList;

import javax.swing.*;

import org.jmol.export.dialog.FileChooser;

/**
 *
 * @author Niels Hulstaert
 */
public class SampleEditDialog extends javax.swing.JDialog {

    private final JFileChooser exportDirectoryChooser = new FileChooser();

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

        analyticalRunsTableScrollPane.getViewport().setOpaque(false);
        analyticalRunsTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    }

    public JButton getDeleteAnalyticalRunButton() {
        return deleteAnalyticalRunButton;
    }

    public JButton getEditAnalyticalRunButton() {
        return editAnalyticalRunButton;
    }

    public DualList getMaterialDualList() {
        return materialDualList;
    }

    public JTable getAnalyticalRunsTable() {
        return analyticalRunsTable;
    }

    public JTextField getAttachementsTextField() {
        return attachementsTextField;
    }

    public JButton getAttachmentsEditButton() {
        return attachmentsEditButton;
    }

    public JButton getCancelButton() {
        return cancelButton;
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

    public JButton getExportAnalyticalRunButton() {
        return exportAnalyticalRunButton;
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
        cancelButton = new javax.swing.JButton();
        sampleParentPanel = new javax.swing.JPanel();
        analyticalRunsPanel = new javax.swing.JPanel();
        analyticalRunsTableScrollPane = new javax.swing.JScrollPane();
        analyticalRunsTable = new javax.swing.JTable();
        deleteAnalyticalRunButton = new javax.swing.JButton();
        editAnalyticalRunButton = new javax.swing.JButton();
        exportAnalyticalRunButton = new javax.swing.JButton();
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

        cancelButton.setText("cancel");
        cancelButton.setToolTipText("");
        cancelButton.setMaximumSize(new java.awt.Dimension(80, 25));
        cancelButton.setMinimumSize(new java.awt.Dimension(80, 25));
        cancelButton.setPreferredSize(new java.awt.Dimension(80, 25));

        sampleParentPanel.setOpaque(false);

        analyticalRunsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Analytical runs"));
        analyticalRunsPanel.setOpaque(false);

        analyticalRunsTable.setModel(new javax.swing.table.DefaultTableModel(
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
        analyticalRunsTableScrollPane.setViewportView(analyticalRunsTable);

        deleteAnalyticalRunButton.setText("delete");
        deleteAnalyticalRunButton.setToolTipText("delete a run");
        deleteAnalyticalRunButton.setMaximumSize(new java.awt.Dimension(80, 25));
        deleteAnalyticalRunButton.setMinimumSize(new java.awt.Dimension(80, 25));
        deleteAnalyticalRunButton.setPreferredSize(new java.awt.Dimension(80, 25));

        editAnalyticalRunButton.setText("edit...");
        editAnalyticalRunButton.setToolTipText("edit the metadata of an existing run");
        editAnalyticalRunButton.setMaximumSize(new java.awt.Dimension(80, 25));
        editAnalyticalRunButton.setMinimumSize(new java.awt.Dimension(80, 25));
        editAnalyticalRunButton.setPreferredSize(new java.awt.Dimension(80, 25));

        exportAnalyticalRunButton.setText("export...");
        exportAnalyticalRunButton.setToolTipText("export the selected analytical run in mzTab format");
        exportAnalyticalRunButton.setMaximumSize(new java.awt.Dimension(80, 25));
        exportAnalyticalRunButton.setMinimumSize(new java.awt.Dimension(80, 25));
        exportAnalyticalRunButton.setPreferredSize(new java.awt.Dimension(80, 25));

        javax.swing.GroupLayout analyticalRunsPanelLayout = new javax.swing.GroupLayout(analyticalRunsPanel);
        analyticalRunsPanel.setLayout(analyticalRunsPanelLayout);
        analyticalRunsPanelLayout.setHorizontalGroup(
            analyticalRunsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, analyticalRunsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(analyticalRunsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(analyticalRunsTableScrollPane)
                    .addGroup(analyticalRunsPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(exportAnalyticalRunButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(editAnalyticalRunButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteAnalyticalRunButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        analyticalRunsPanelLayout.setVerticalGroup(
            analyticalRunsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(analyticalRunsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(analyticalRunsTableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(analyticalRunsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(deleteAnalyticalRunButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(editAnalyticalRunButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(exportAnalyticalRunButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        detailPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Detail"));
        detailPanel.setOpaque(false);

        attachementsLabel.setText("Attachments");

        nameLabel.setText("Name*");

        storageLocationLabel.setText("Storage location");

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
            .addComponent(analyticalRunsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        sampleParentPanelLayout.setVerticalGroup(
            sampleParentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sampleParentPanelLayout.createSequentialGroup()
                .addComponent(detailPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(analyticalRunsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout sampleEditPanelLayout = new javax.swing.GroupLayout(sampleEditPanel);
        sampleEditPanel.setLayout(sampleEditPanelLayout);
        sampleEditPanelLayout.setHorizontalGroup(
            sampleEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sampleEditPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(saveOrUpdateButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                    .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
    private javax.swing.JPanel analyticalRunsPanel;
    private javax.swing.JTable analyticalRunsTable;
    private javax.swing.JScrollPane analyticalRunsTableScrollPane;
    private javax.swing.JLabel attachementsLabel;
    private javax.swing.JLabel attachementsLabel1;
    private javax.swing.JTextField attachementsTextField;
    private javax.swing.JButton attachmentsEditButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel conditionLabel;
    private javax.swing.JTextField conditionTextField;
    private javax.swing.JButton deleteAnalyticalRunButton;
    private javax.swing.JPanel detailPanel;
    private javax.swing.JButton editAnalyticalRunButton;
    private javax.swing.JButton exportAnalyticalRunButton;
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
