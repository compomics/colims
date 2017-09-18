/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.client.view;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

/**
 *
 * @author demet
 */
public class SampleRunsDialog extends javax.swing.JDialog {

    /**
     * Constructor.
     *
     * @param parent the parent dialog
     * @param modal is the dialog modal
     */
    public SampleRunsDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        analyticalRunsTableScrollPane.getViewport().setOpaque(false);
        analyticalRunsTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    }

    public JTable getAnalyticalRunsTable() {
        return analyticalRunsTable;
    }

    public JTextField getAttachmentsTextField() {
        return attachmentsTextField;
    }

    public JButton getDeleteAnalyticalRunButton() {
        return deleteAnalyticalRunButton;
    }

    public JButton getEditAnalyticalRunButton() {
        return editAnalyticalRunButton;
    }

    public JTextField getFastaNameTextField() {
        return fastaNameTextField;
    }

    public JTextField getInstrumentTextField() {
        return instrumentTextField;
    }

    public JTextField getLocationTextField() {
        return locationTextField;
    }

    public JTextField getEnzymeTextField() {
        return enzymeTextField;
    }

    public JTextField getMaxMissedCleTextField() {
        return maxMissedCleTextField;
    }

    public JTextField getNameTextField() {
        return nameTextField;
    }

    public JTextField getPreMasTolTextField() {
        return preMasTolTextField;
    }

    public JTextField getSearchEngineTextField() {
        return searchEngineTextField;
    }

    public JTextField getStartDateTextField() {
        return startDateTextField;
    }

    public JButton getCloseButton() {
        return closeButton;
    }

    public JButton getExportMzIdentMlButton() {
        return exportMzIdentMlButton;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        analyticalRunsSearchSettingsParentPanel = new javax.swing.JPanel();
        analyticalRunsPanel = new javax.swing.JPanel();
        analyticalRunsTableScrollPane = new javax.swing.JScrollPane();
        analyticalRunsTable = new javax.swing.JTable();
        deleteAnalyticalRunButton = new javax.swing.JButton();
        editAnalyticalRunButton = new javax.swing.JButton();
        exportMzIdentMlButton = new javax.swing.JButton();
        searchSettingsAndRunDetails = new javax.swing.JPanel();
        nameLabel = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        startDateLabel = new javax.swing.JLabel();
        startDateTextField = new javax.swing.JTextField();
        instrumentLabel = new javax.swing.JLabel();
        instrumentTextField = new javax.swing.JTextField();
        locationLabel = new javax.swing.JLabel();
        locationTextField = new javax.swing.JTextField();
        attachmentsLabel = new javax.swing.JLabel();
        attachmentsTextField = new javax.swing.JTextField();
        searchEngineLabel = new javax.swing.JLabel();
        searchEngineTextField = new javax.swing.JTextField();
        fastaNameLabel = new javax.swing.JLabel();
        fastaNameTextField = new javax.swing.JTextField();
        enzymeLabel = new javax.swing.JLabel();
        enzymeTextField = new javax.swing.JTextField();
        maxMissedCleLabel = new javax.swing.JLabel();
        maxMissedCleTextField = new javax.swing.JTextField();
        preMasTolLabel = new javax.swing.JLabel();
        preMasTolTextField = new javax.swing.JTextField();
        closeButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Sample analytical runs management");

        analyticalRunsSearchSettingsParentPanel.setBackground(new java.awt.Color(255, 255, 255));
        analyticalRunsSearchSettingsParentPanel.setPreferredSize(new java.awt.Dimension(695, 691));

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

        exportMzIdentMlButton.setText("export...");
        exportMzIdentMlButton.setToolTipText("export one or more runs to mzIdentML");
        exportMzIdentMlButton.setMaximumSize(new java.awt.Dimension(80, 25));
        exportMzIdentMlButton.setMinimumSize(new java.awt.Dimension(80, 25));
        exportMzIdentMlButton.setPreferredSize(new java.awt.Dimension(80, 25));

        javax.swing.GroupLayout analyticalRunsPanelLayout = new javax.swing.GroupLayout(analyticalRunsPanel);
        analyticalRunsPanel.setLayout(analyticalRunsPanelLayout);
        analyticalRunsPanelLayout.setHorizontalGroup(
            analyticalRunsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, analyticalRunsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(analyticalRunsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(analyticalRunsTableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 549, Short.MAX_VALUE)
                    .addGroup(analyticalRunsPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(exportMzIdentMlButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                .addComponent(analyticalRunsTableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(analyticalRunsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(deleteAnalyticalRunButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(editAnalyticalRunButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(exportMzIdentMlButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        searchSettingsAndRunDetails.setBackground(new java.awt.Color(255, 255, 255));
        searchSettingsAndRunDetails.setBorder(javax.swing.BorderFactory.createTitledBorder("Details & search settings"));

        nameLabel.setText("Name");

        nameTextField.setEditable(false);
        nameTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nameTextFieldActionPerformed(evt);
            }
        });

        startDateLabel.setText("Start date");

        startDateTextField.setEditable(false);
        startDateTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startDateTextFieldActionPerformed(evt);
            }
        });

        instrumentLabel.setText("Instrument");

        instrumentTextField.setEditable(false);
        instrumentTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                instrumentTextFieldActionPerformed(evt);
            }
        });

        locationLabel.setText("Location");

        locationTextField.setEditable(false);
        locationTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                locationTextFieldActionPerformed(evt);
            }
        });

        attachmentsLabel.setText("Attachments");

        attachmentsTextField.setEditable(false);
        attachmentsTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                attachmentsTextFieldActionPerformed(evt);
            }
        });

        searchEngineLabel.setText("Search engine");

        searchEngineTextField.setEditable(false);
        searchEngineTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchEngineTextFieldActionPerformed(evt);
            }
        });

        fastaNameLabel.setText("Fasta DB name");

        fastaNameTextField.setEditable(false);
        fastaNameTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fastaNameTextFieldActionPerformed(evt);
            }
        });

        enzymeLabel.setText("Enzyme");

        enzymeTextField.setEditable(false);
        enzymeTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enzymeTextFieldActionPerformed(evt);
            }
        });

        maxMissedCleLabel.setText("Max missed cleavages");

        maxMissedCleTextField.setEditable(false);
        maxMissedCleTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maxMissedCleTextFieldActionPerformed(evt);
            }
        });

        preMasTolLabel.setText("Pre. mass tolerance");

        preMasTolTextField.setEditable(false);
        preMasTolTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                preMasTolTextFieldActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout searchSettingsAndRunDetailsLayout = new javax.swing.GroupLayout(searchSettingsAndRunDetails);
        searchSettingsAndRunDetails.setLayout(searchSettingsAndRunDetailsLayout);
        searchSettingsAndRunDetailsLayout.setHorizontalGroup(
            searchSettingsAndRunDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchSettingsAndRunDetailsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(searchSettingsAndRunDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(searchSettingsAndRunDetailsLayout.createSequentialGroup()
                        .addGroup(searchSettingsAndRunDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(attachmentsLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
                            .addComponent(locationLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(6, 6, 6)
                        .addGroup(searchSettingsAndRunDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(attachmentsTextField)
                            .addComponent(locationTextField)))
                    .addGroup(searchSettingsAndRunDetailsLayout.createSequentialGroup()
                        .addGroup(searchSettingsAndRunDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(startDateLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(instrumentLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
                            .addComponent(nameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(6, 6, 6)
                        .addGroup(searchSettingsAndRunDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(startDateTextField)
                            .addComponent(instrumentTextField)
                            .addComponent(nameTextField)))
                    .addGroup(searchSettingsAndRunDetailsLayout.createSequentialGroup()
                        .addGroup(searchSettingsAndRunDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(fastaNameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(enzymeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(searchEngineLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE))
                        .addGap(6, 6, 6)
                        .addGroup(searchSettingsAndRunDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(enzymeTextField, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(fastaNameTextField, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(searchEngineTextField, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addGroup(searchSettingsAndRunDetailsLayout.createSequentialGroup()
                        .addGroup(searchSettingsAndRunDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(maxMissedCleLabel)
                            .addComponent(preMasTolLabel))
                        .addGap(12, 12, 12)
                        .addGroup(searchSettingsAndRunDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(maxMissedCleTextField)
                            .addComponent(preMasTolTextField))))
                .addContainerGap())
        );
        searchSettingsAndRunDetailsLayout.setVerticalGroup(
            searchSettingsAndRunDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchSettingsAndRunDetailsLayout.createSequentialGroup()
                .addGroup(searchSettingsAndRunDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(searchSettingsAndRunDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(startDateLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(startDateTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(searchSettingsAndRunDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(instrumentLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(instrumentTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(searchSettingsAndRunDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(locationLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(locationTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(searchSettingsAndRunDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(attachmentsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(attachmentsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(searchSettingsAndRunDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(searchEngineLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchEngineTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(searchSettingsAndRunDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fastaNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fastaNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(searchSettingsAndRunDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(enzymeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(enzymeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(searchSettingsAndRunDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(maxMissedCleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(maxMissedCleTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(searchSettingsAndRunDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(preMasTolLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(preMasTolTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout analyticalRunsSearchSettingsParentPanelLayout = new javax.swing.GroupLayout(analyticalRunsSearchSettingsParentPanel);
        analyticalRunsSearchSettingsParentPanel.setLayout(analyticalRunsSearchSettingsParentPanelLayout);
        analyticalRunsSearchSettingsParentPanelLayout.setHorizontalGroup(
            analyticalRunsSearchSettingsParentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(analyticalRunsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(searchSettingsAndRunDetails, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        analyticalRunsSearchSettingsParentPanelLayout.setVerticalGroup(
            analyticalRunsSearchSettingsParentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(analyticalRunsSearchSettingsParentPanelLayout.createSequentialGroup()
                .addComponent(analyticalRunsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(searchSettingsAndRunDetails, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        closeButton.setText("close");
        closeButton.setMaximumSize(new java.awt.Dimension(80, 25));
        closeButton.setMinimumSize(new java.awt.Dimension(80, 25));
        closeButton.setPreferredSize(new java.awt.Dimension(80, 25));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(analyticalRunsSearchSettingsParentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 589, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(analyticalRunsSearchSettingsParentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 607, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void nameTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nameTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_nameTextFieldActionPerformed

    private void startDateTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startDateTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_startDateTextFieldActionPerformed

    private void instrumentTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_instrumentTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_instrumentTextFieldActionPerformed

    private void locationTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_locationTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_locationTextFieldActionPerformed

    private void attachmentsTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_attachmentsTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_attachmentsTextFieldActionPerformed

    private void searchEngineTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchEngineTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_searchEngineTextFieldActionPerformed

    private void fastaNameTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fastaNameTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fastaNameTextFieldActionPerformed

    private void enzymeTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enzymeTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_enzymeTextFieldActionPerformed

    private void maxMissedCleTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maxMissedCleTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_maxMissedCleTextFieldActionPerformed

    private void preMasTolTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_preMasTolTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_preMasTolTextFieldActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel analyticalRunsPanel;
    private javax.swing.JPanel analyticalRunsSearchSettingsParentPanel;
    private javax.swing.JTable analyticalRunsTable;
    private javax.swing.JScrollPane analyticalRunsTableScrollPane;
    private javax.swing.JLabel attachmentsLabel;
    private javax.swing.JTextField attachmentsTextField;
    private javax.swing.JButton closeButton;
    private javax.swing.JButton deleteAnalyticalRunButton;
    private javax.swing.JButton editAnalyticalRunButton;
    private javax.swing.JLabel enzymeLabel;
    private javax.swing.JTextField enzymeTextField;
    private javax.swing.JButton exportMzIdentMlButton;
    private javax.swing.JLabel fastaNameLabel;
    private javax.swing.JTextField fastaNameTextField;
    private javax.swing.JLabel instrumentLabel;
    private javax.swing.JTextField instrumentTextField;
    private javax.swing.JLabel locationLabel;
    private javax.swing.JTextField locationTextField;
    private javax.swing.JLabel maxMissedCleLabel;
    private javax.swing.JTextField maxMissedCleTextField;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JLabel preMasTolLabel;
    private javax.swing.JTextField preMasTolTextField;
    private javax.swing.JLabel searchEngineLabel;
    private javax.swing.JTextField searchEngineTextField;
    private javax.swing.JPanel searchSettingsAndRunDetails;
    private javax.swing.JLabel startDateLabel;
    private javax.swing.JTextField startDateTextField;
    // End of variables declaration//GEN-END:variables
}
