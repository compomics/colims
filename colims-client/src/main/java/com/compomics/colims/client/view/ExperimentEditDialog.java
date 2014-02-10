package com.compomics.colims.client.view;

import java.awt.Frame;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author Niels Hulstaert
 */
public class ExperimentEditDialog extends javax.swing.JDialog {

    /**
     * Dialog constructor
     */
    public ExperimentEditDialog(final Frame parent, final boolean modal) {
        super(parent, modal);

        initComponents();

        samplesTableScrollPane.getViewport().setOpaque(false);
        samplesTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    }

    public JButton getAttachmentsEditButton() {
        return attachmentsEditButton;
    }

    public JButton getCloseButton() {
        return closeButton;
    }

    public JTextArea getDescriptionTextArea() {
        return descriptionTextArea;
    }

    public JTextField getAttachementsTextField() {
        return attachementsTextField;
    }

    public JTextField getNumberTextField() {
        return numberTextField;
    }

    public JButton getSaveOrUpdateButton() {
        return saveOrUpdateButton;
    }

    public JTextField getStorageLocationTextField() {
        return storageLocationTextField;
    }

    public JTextField getTitleTextField() {
        return titleTextField;
    }

    public JButton getAddSampleButton() {
        return addSampleButton;
    }

    public JButton getDeleteSampleButton() {
        return deleteSampleButton;
    }

    public JButton getEditSampleButton() {
        return editSampleButton;
    }

    public JTable getSamplesTable() {
        return samplesTable;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        experimentEditPanel = new javax.swing.JPanel();
        saveOrUpdateButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();
        detailPanel = new javax.swing.JPanel();
        attachementsLabel = new javax.swing.JLabel();
        numberTextField = new javax.swing.JTextField();
        titleLabel = new javax.swing.JLabel();
        storageLocationLabel = new javax.swing.JLabel();
        attachementsTextField = new javax.swing.JTextField();
        storageLocationTextField = new javax.swing.JTextField();
        attachmentsEditButton = new javax.swing.JButton();
        descriptionScrollPanel = new javax.swing.JScrollPane();
        descriptionTextArea = new javax.swing.JTextArea();
        descriptionLabel = new javax.swing.JLabel();
        numberLabel = new javax.swing.JLabel();
        titleTextField = new javax.swing.JTextField();
        samplesPanel = new javax.swing.JPanel();
        samplesTableScrollPane = new javax.swing.JScrollPane();
        samplesTable = new javax.swing.JTable();
        deleteSampleButton = new javax.swing.JButton();
        editSampleButton = new javax.swing.JButton();
        addSampleButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("experiment metadata management");

        experimentEditPanel.setBackground(new java.awt.Color(255, 255, 255));

        saveOrUpdateButton.setText("save");
        saveOrUpdateButton.setToolTipText("");
        saveOrUpdateButton.setMaximumSize(new java.awt.Dimension(80, 25));
        saveOrUpdateButton.setMinimumSize(new java.awt.Dimension(80, 25));
        saveOrUpdateButton.setPreferredSize(new java.awt.Dimension(80, 25));

        closeButton.setText("close");
        closeButton.setToolTipText("");
        closeButton.setMaximumSize(new java.awt.Dimension(80, 25));
        closeButton.setMinimumSize(new java.awt.Dimension(80, 25));
        closeButton.setPreferredSize(new java.awt.Dimension(80, 25));

        detailPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("detail"));
        detailPanel.setOpaque(false);

        attachementsLabel.setText("attachments");

        titleLabel.setText("title");

        storageLocationLabel.setText("storage location");

        attachmentsEditButton.setText("edit...");
        attachmentsEditButton.setToolTipText("edit the binary attachments");
        attachmentsEditButton.setMaximumSize(new java.awt.Dimension(80, 25));
        attachmentsEditButton.setMinimumSize(new java.awt.Dimension(80, 25));
        attachmentsEditButton.setPreferredSize(new java.awt.Dimension(80, 25));

        descriptionTextArea.setColumns(20);
        descriptionTextArea.setLineWrap(true);
        descriptionTextArea.setRows(5);
        descriptionTextArea.setAutoscrolls(false);
        descriptionScrollPanel.setViewportView(descriptionTextArea);

        descriptionLabel.setText("description");

        numberLabel.setText("number");

        javax.swing.GroupLayout detailPanelLayout = new javax.swing.GroupLayout(detailPanel);
        detailPanel.setLayout(detailPanelLayout);
        detailPanelLayout.setHorizontalGroup(
            detailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(detailPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(detailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(detailPanelLayout.createSequentialGroup()
                        .addComponent(descriptionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(descriptionScrollPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 542, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(detailPanelLayout.createSequentialGroup()
                        .addComponent(titleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(titleTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 542, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, detailPanelLayout.createSequentialGroup()
                        .addGroup(detailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(storageLocationLabel)
                            .addComponent(attachementsLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(detailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(storageLocationTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 542, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, detailPanelLayout.createSequentialGroup()
                                .addComponent(attachementsTextField)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(attachmentsEditButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(detailPanelLayout.createSequentialGroup()
                        .addComponent(numberLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(21, 21, 21)
                        .addComponent(numberTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        detailPanelLayout.setVerticalGroup(
            detailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(detailPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(detailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(titleLabel)
                    .addComponent(titleTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(detailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(numberLabel)
                    .addComponent(numberTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(detailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(descriptionLabel)
                    .addComponent(descriptionScrollPanel))
                .addGap(18, 18, 18)
                .addGroup(detailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(storageLocationTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(storageLocationLabel))
                .addGap(18, 18, 18)
                .addGroup(detailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(attachementsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(attachmentsEditButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(attachementsLabel))
                .addContainerGap())
        );

        samplesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("samples"));
        samplesPanel.setOpaque(false);

        samplesTableScrollPane.setOpaque(false);

        samplesTable.setModel(new javax.swing.table.DefaultTableModel(
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
        samplesTableScrollPane.setViewportView(samplesTable);

        deleteSampleButton.setText("delete");
        deleteSampleButton.setToolTipText("delete a sample");
        deleteSampleButton.setMaximumSize(new java.awt.Dimension(80, 25));
        deleteSampleButton.setMinimumSize(new java.awt.Dimension(80, 25));
        deleteSampleButton.setPreferredSize(new java.awt.Dimension(80, 25));

        editSampleButton.setText("edit...");
        editSampleButton.setToolTipText("edit the metadata of an existing sample");
        editSampleButton.setMaximumSize(new java.awt.Dimension(80, 25));
        editSampleButton.setMinimumSize(new java.awt.Dimension(80, 25));
        editSampleButton.setPreferredSize(new java.awt.Dimension(80, 25));

        addSampleButton.setText("add...");
        addSampleButton.setToolTipText("add a new sample");
        addSampleButton.setMaximumSize(new java.awt.Dimension(80, 25));
        addSampleButton.setMinimumSize(new java.awt.Dimension(80, 25));
        addSampleButton.setPreferredSize(new java.awt.Dimension(80, 25));

        javax.swing.GroupLayout samplesPanelLayout = new javax.swing.GroupLayout(samplesPanel);
        samplesPanel.setLayout(samplesPanelLayout);
        samplesPanelLayout.setHorizontalGroup(
            samplesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(samplesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(samplesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(samplesTableScrollPane)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, samplesPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(addSampleButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(editSampleButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteSampleButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        samplesPanelLayout.setVerticalGroup(
            samplesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(samplesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(samplesTableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(samplesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(deleteSampleButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(editSampleButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addSampleButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout experimentEditPanelLayout = new javax.swing.GroupLayout(experimentEditPanel);
        experimentEditPanel.setLayout(experimentEditPanelLayout);
        experimentEditPanelLayout.setHorizontalGroup(
            experimentEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(experimentEditPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(experimentEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, experimentEditPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(saveOrUpdateButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(experimentEditPanelLayout.createSequentialGroup()
                        .addComponent(detailPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(samplesPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        experimentEditPanelLayout.setVerticalGroup(
            experimentEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(experimentEditPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(detailPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(samplesPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(experimentEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(saveOrUpdateButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(experimentEditPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(experimentEditPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addSampleButton;
    private javax.swing.JLabel attachementsLabel;
    private javax.swing.JTextField attachementsTextField;
    private javax.swing.JButton attachmentsEditButton;
    private javax.swing.JButton closeButton;
    private javax.swing.JButton deleteSampleButton;
    private javax.swing.JLabel descriptionLabel;
    private javax.swing.JScrollPane descriptionScrollPanel;
    private javax.swing.JTextArea descriptionTextArea;
    private javax.swing.JPanel detailPanel;
    private javax.swing.JButton editSampleButton;
    private javax.swing.JPanel experimentEditPanel;
    private javax.swing.JLabel numberLabel;
    private javax.swing.JTextField numberTextField;
    private javax.swing.JPanel samplesPanel;
    private javax.swing.JTable samplesTable;
    private javax.swing.JScrollPane samplesTableScrollPane;
    private javax.swing.JButton saveOrUpdateButton;
    private javax.swing.JLabel storageLocationLabel;
    private javax.swing.JTextField storageLocationTextField;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JTextField titleTextField;
    // End of variables declaration//GEN-END:variables
}
