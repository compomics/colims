/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.client.view.admin;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 *
 * @author demet
 */
public class FastaDbSaveUpdatePanel extends javax.swing.JPanel {

     /**
     * The fasta file chooser.
     */
    private final JFileChooser fastaFileChooser = new JFileChooser();



    /**
     * Creates new form FastaDbSaveUpdate
     */
    public FastaDbSaveUpdatePanel() {
        initComponents();
    }

    public JFileChooser getFastaFileChooser() {
        return fastaFileChooser;
    }
    public JButton getBrowseFastaButton() {
        return browseFastaButton;
    }

    public JButton getBrowseTaxonomyButton() {
        return browseTaxonomyButton;
    }

    public JButton getBackButton() {
        return backButton;
    }

    public JTextField getFileNameTextField() {
        return fileNameTextField;
    }

    public JTextField getFilePathTextField() {
        return filePathTextField;
    }

    public JTextField getNameTextField() {
        return nameTextField;
    }

    public JButton getSaveOrUpdateButton() {
        return saveOrUpdateButton;
    }

    public JComboBox<String> getTaxomomyComboBox() {
        return taxomomyComboBox;
    }

    public JTextField getVersionTextField() {
        return versionTextField;
    }

    public JLabel getFastaDbStateInfoLabel() {
        return fastaDbStateInfoLabel;
    }

    public JComboBox<String> getHeaderParseRuleComboBox() {
        return headerParseRuleComboBox;
    }

    public JButton getAddHeaderParseRuleButtton() {
        return addHeaderParseRuleButtton;
    }

    public JComboBox<String> getDatabaseComboBox() {
        return databaseComboBox;
    }

    public JButton getTestHeaderParseRuleButtton() {
        return testHeaderParseRuleButtton;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fastaDbDetailPanel = new javax.swing.JPanel();
        nameTextField = new javax.swing.JTextField();
        nameLabel = new javax.swing.JLabel();
        fileNameLabel = new javax.swing.JLabel();
        fileNameTextField = new javax.swing.JTextField();
        filePathLabel = new javax.swing.JLabel();
        filePathTextField = new javax.swing.JTextField();
        browseFastaButton = new javax.swing.JButton();
        versionLabel = new javax.swing.JLabel();
        versionTextField = new javax.swing.JTextField();
        taxonomyLabel = new javax.swing.JLabel();
        browseTaxonomyButton = new javax.swing.JButton();
        fastaDbStateInfoLabel = new javax.swing.JLabel();
        HeaderParseRuleLabel = new javax.swing.JLabel();
        taxomomyComboBox = new javax.swing.JComboBox<>();
        headerParseRuleComboBox = new javax.swing.JComboBox<>();
        addHeaderParseRuleButtton = new javax.swing.JButton();
        databaseLabel = new javax.swing.JLabel();
        databaseComboBox = new javax.swing.JComboBox<>();
        testHeaderParseRuleButtton = new javax.swing.JButton();
        backButton = new javax.swing.JButton();
        saveOrUpdateButton = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(564, 361));

        fastaDbDetailPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        fastaDbDetailPanel.setOpaque(false);
        fastaDbDetailPanel.setPreferredSize(new java.awt.Dimension(10, 10));

        nameLabel.setText("Name*");
        nameLabel.setPreferredSize(new java.awt.Dimension(48, 14));

        fileNameLabel.setText("File Name*");

        filePathLabel.setText("File Path*");

        browseFastaButton.setText("browse");
        browseFastaButton.setToolTipText("select a fasta file to set the file name and the file path text fields");
        browseFastaButton.setMaximumSize(new java.awt.Dimension(80, 25));
        browseFastaButton.setMinimumSize(new java.awt.Dimension(80, 25));
        browseFastaButton.setPreferredSize(new java.awt.Dimension(80, 25));

        versionLabel.setText("Version");
        versionLabel.setPreferredSize(new java.awt.Dimension(48, 14));

        versionTextField.setToolTipText("Important field for Mztab export. If you don't have version, type N/A");

        taxonomyLabel.setText("Taxonomy");

        browseTaxonomyButton.setText("browse");
        browseTaxonomyButton.setToolTipText("select taxonomy ID and species from the NCBI taxonomy ontology");
        browseTaxonomyButton.setMaximumSize(new java.awt.Dimension(80, 25));
        browseTaxonomyButton.setMinimumSize(new java.awt.Dimension(80, 25));
        browseTaxonomyButton.setPreferredSize(new java.awt.Dimension(80, 25));
        browseTaxonomyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseTaxonomyButtonActionPerformed(evt);
            }
        });

        fastaDbStateInfoLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        fastaDbStateInfoLabel.setForeground(new java.awt.Color(255, 0, 0));
        fastaDbStateInfoLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        fastaDbStateInfoLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        fastaDbStateInfoLabel.setMaximumSize(new java.awt.Dimension(100, 20));
        fastaDbStateInfoLabel.setMinimumSize(new java.awt.Dimension(100, 20));
        fastaDbStateInfoLabel.setPreferredSize(new java.awt.Dimension(100, 20));

        HeaderParseRuleLabel.setText("Parse rule");

        taxomomyComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        headerParseRuleComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        addHeaderParseRuleButtton.setText("add");
        addHeaderParseRuleButtton.setMaximumSize(new java.awt.Dimension(80, 25));
        addHeaderParseRuleButtton.setMinimumSize(new java.awt.Dimension(80, 25));
        addHeaderParseRuleButtton.setPreferredSize(new java.awt.Dimension(80, 25));

        databaseLabel.setText("Database");

        databaseComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        testHeaderParseRuleButtton.setText("test");
        testHeaderParseRuleButtton.setMaximumSize(new java.awt.Dimension(80, 25));
        testHeaderParseRuleButtton.setMinimumSize(new java.awt.Dimension(80, 25));
        testHeaderParseRuleButtton.setPreferredSize(new java.awt.Dimension(80, 25));

        javax.swing.GroupLayout fastaDbDetailPanelLayout = new javax.swing.GroupLayout(fastaDbDetailPanel);
        fastaDbDetailPanel.setLayout(fastaDbDetailPanelLayout);
        fastaDbDetailPanelLayout.setHorizontalGroup(
            fastaDbDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fastaDbDetailPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(fastaDbDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(fastaDbDetailPanelLayout.createSequentialGroup()
                        .addComponent(fastaDbStateInfoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 1, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(525, Short.MAX_VALUE))
                    .addGroup(fastaDbDetailPanelLayout.createSequentialGroup()
                        .addGroup(fastaDbDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(fastaDbDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(nameLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(fileNameLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 74, Short.MAX_VALUE)
                                .addComponent(filePathLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(databaseLabel)
                            .addComponent(versionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(taxonomyLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(HeaderParseRuleLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(fastaDbDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(fastaDbDetailPanelLayout.createSequentialGroup()
                                .addComponent(fileNameTextField)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(browseFastaButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(nameTextField)
                            .addComponent(filePathTextField)
                            .addComponent(databaseComboBox, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(versionTextField, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, fastaDbDetailPanelLayout.createSequentialGroup()
                                .addGroup(fastaDbDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(taxomomyComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(fastaDbDetailPanelLayout.createSequentialGroup()
                                        .addComponent(headerParseRuleComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(testHeaderParseRuleButtton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(fastaDbDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(browseTaxonomyButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(addHeaderParseRuleButtton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))))
        );
        fastaDbDetailPanelLayout.setVerticalGroup(
            fastaDbDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fastaDbDetailPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(fastaDbDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(fastaDbDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fileNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(browseFastaButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fileNameLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(fastaDbDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(filePathLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filePathTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(fastaDbDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(databaseComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(databaseLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(fastaDbDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(versionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(versionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(fastaDbDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(taxomomyComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(browseTaxonomyButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(taxonomyLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(fastaDbDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(headerParseRuleComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addHeaderParseRuleButtton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(HeaderParseRuleLabel)
                    .addComponent(testHeaderParseRuleButtton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(fastaDbStateInfoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11))
        );

        backButton.setText("back");
        backButton.setToolTipText("go back to the fasta database overview panel");
        backButton.setMaximumSize(new java.awt.Dimension(80, 25));
        backButton.setMinimumSize(new java.awt.Dimension(80, 25));
        backButton.setPreferredSize(new java.awt.Dimension(80, 25));

        saveOrUpdateButton.setText("save");
        saveOrUpdateButton.setToolTipText("save a new fasta db or update an existing one");
        saveOrUpdateButton.setMaximumSize(new java.awt.Dimension(80, 25));
        saveOrUpdateButton.setMinimumSize(new java.awt.Dimension(80, 25));
        saveOrUpdateButton.setPreferredSize(new java.awt.Dimension(80, 25));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fastaDbDetailPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 552, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(backButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(saveOrUpdateButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(fastaDbDetailPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 319, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(backButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(saveOrUpdateButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void browseTaxonomyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseTaxonomyButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_browseTaxonomyButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel HeaderParseRuleLabel;
    private javax.swing.JButton addHeaderParseRuleButtton;
    private javax.swing.JButton backButton;
    private javax.swing.JButton browseFastaButton;
    private javax.swing.JButton browseTaxonomyButton;
    private javax.swing.JComboBox<String> databaseComboBox;
    private javax.swing.JLabel databaseLabel;
    private javax.swing.JPanel fastaDbDetailPanel;
    private javax.swing.JLabel fastaDbStateInfoLabel;
    private javax.swing.JLabel fileNameLabel;
    private javax.swing.JTextField fileNameTextField;
    private javax.swing.JLabel filePathLabel;
    private javax.swing.JTextField filePathTextField;
    private javax.swing.JComboBox<String> headerParseRuleComboBox;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JButton saveOrUpdateButton;
    private javax.swing.JComboBox<String> taxomomyComboBox;
    private javax.swing.JLabel taxonomyLabel;
    private javax.swing.JButton testHeaderParseRuleButtton;
    private javax.swing.JLabel versionLabel;
    private javax.swing.JTextField versionTextField;
    // End of variables declaration//GEN-END:variables

}
