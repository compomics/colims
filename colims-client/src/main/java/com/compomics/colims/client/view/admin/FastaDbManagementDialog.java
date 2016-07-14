package com.compomics.colims.client.view.admin;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author Niels Hulstaert
 */
public class FastaDbManagementDialog extends javax.swing.JDialog {

    /**
     * Dialog constructor.
     *
     * @param parent the parent frame
     * @param modal the modal boolean
     */
    public FastaDbManagementDialog(final JDialog parent, final boolean modal) {
        super(parent, modal);

        initComponents();
    }


    public JButton getAddButton() {
        return addButton;
    }

    public JButton getCloseButton() {
        return closeButton;
    }

    public JButton getDeleteButton() {
        return deleteButton;
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

    public JList getFastaDbList() {
        return fastaDbList;
    }

    public JTextField getVersionTextField() {
        return versionTextField;
    }

    public JTextField getTaxonomyTextField() {
        return taxonomyTextField;
    }

    public JButton getSelectButton() {
        return selectButton;
    }

    public JCheckBox getAdditionalCheckBox() {
        return additionalCheckBox;
    }

    public JCheckBox getContaminantsCheckBox() {
        return contaminantsCheckBox;
    }

    public JCheckBox getPrimaryCheckBox() {
        return primaryCheckBox;
    }

    public JTextField getHeaderParseRuleTextField() {
        return headerParseRuleTextField;
    }

    public FastaDbSaveUpdatePanel getFastaDbSaveUpdatePanel() {
        return fastaDbSaveUpdatePanel;
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public JButton getUpdateButton() {
        return updateButton;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        mainPanel = new javax.swing.JPanel();
        fastaDbManagmentParentPanel = new javax.swing.JPanel();
        closeButton = new javax.swing.JButton();
        selectButton = new javax.swing.JButton();
        fastaDbManagementPanel = new javax.swing.JPanel();
        fastaDbOverviewPanel = new javax.swing.JPanel();
        fastaDbListScrollPane = new javax.swing.JScrollPane();
        fastaDbList = new javax.swing.JList();
        addButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        descriptionLabel = new javax.swing.JLabel();
        fastaDbTypesPanel = new javax.swing.JPanel();
        primaryCheckBox = new javax.swing.JCheckBox();
        additionalCheckBox = new javax.swing.JCheckBox();
        contaminantsCheckBox = new javax.swing.JCheckBox();
        fastaDbTypesLabel = new javax.swing.JLabel();
        updateButton = new javax.swing.JButton();
        fastaDbDetailParentPanel = new javax.swing.JPanel();
        fastaDbDetailPanel = new javax.swing.JPanel();
        nameTextField = new javax.swing.JTextField();
        nameLabel = new javax.swing.JLabel();
        fileNameLabel = new javax.swing.JLabel();
        fileNameTextField = new javax.swing.JTextField();
        filePathLabel = new javax.swing.JLabel();
        filePathTextField = new javax.swing.JTextField();
        versionLabel = new javax.swing.JLabel();
        versionTextField = new javax.swing.JTextField();
        taxonomyLabel = new javax.swing.JLabel();
        headerParseRuleTextField = new javax.swing.JTextField();
        HeaderParseRuleLabel = new javax.swing.JLabel();
        taxonomyTextField = new javax.swing.JTextField();
        fastaDbSaveUpdatePanel = new com.compomics.colims.client.view.admin.FastaDbSaveUpdatePanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Fasta database management");
        setModal(true);

        mainPanel.setLayout(new java.awt.CardLayout());

        fastaDbManagmentParentPanel.setBackground(new java.awt.Color(255, 255, 255));

        closeButton.setText("close");
        closeButton.setMaximumSize(new java.awt.Dimension(80, 25));
        closeButton.setMinimumSize(new java.awt.Dimension(80, 25));
        closeButton.setPreferredSize(new java.awt.Dimension(80, 25));

        selectButton.setText("select");
        selectButton.setMaximumSize(new java.awt.Dimension(80, 25));
        selectButton.setMinimumSize(new java.awt.Dimension(80, 25));
        selectButton.setPreferredSize(new java.awt.Dimension(80, 25));

        fastaDbManagementPanel.setOpaque(false);
        fastaDbManagementPanel.setLayout(new java.awt.GridBagLayout());

        fastaDbOverviewPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Overview"));
        fastaDbOverviewPanel.setOpaque(false);
        fastaDbOverviewPanel.setPreferredSize(new java.awt.Dimension(100, 100));

        fastaDbListScrollPane.setViewportView(fastaDbList);

        addButton.setText("add");
        addButton.setToolTipText("add a new fasta db");
        addButton.setMaximumSize(new java.awt.Dimension(80, 25));
        addButton.setMinimumSize(new java.awt.Dimension(80, 25));
        addButton.setPreferredSize(new java.awt.Dimension(80, 25));

        deleteButton.setText("delete");
        deleteButton.setToolTipText("remove an existing fasta db");
        deleteButton.setMaximumSize(new java.awt.Dimension(80, 25));
        deleteButton.setMinimumSize(new java.awt.Dimension(80, 25));
        deleteButton.setPreferredSize(new java.awt.Dimension(80, 25));

        descriptionLabel.setText("<html>Please select a fasta DB from the list. If necessary,<br>add a new one. You can select a file and taxonomy by<br>clicking the \"browse\" buttons.</html> ");

        fastaDbTypesPanel.setBorder(null);
        fastaDbTypesPanel.setOpaque(false);

        primaryCheckBox.setText("primary");
        primaryCheckBox.setToolTipText("show all primary FASTA files used in searches (e.g. UnitProt ones)");

        additionalCheckBox.setText("additional");
        additionalCheckBox.setToolTipText("show FASTA files used in searches that contain some additional sequences");

        contaminantsCheckBox.setText("contaminants");
        contaminantsCheckBox.setToolTipText("show all FASTA files used in searches with contaminants sequences");

        fastaDbTypesLabel.setText("FASTA types");
        fastaDbTypesLabel.setToolTipText("Filter on FASTA file type; if nothing is selected, all FASTA files are shown");

        javax.swing.GroupLayout fastaDbTypesPanelLayout = new javax.swing.GroupLayout(fastaDbTypesPanel);
        fastaDbTypesPanel.setLayout(fastaDbTypesPanelLayout);
        fastaDbTypesPanelLayout.setHorizontalGroup(
            fastaDbTypesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fastaDbTypesPanelLayout.createSequentialGroup()
                .addComponent(fastaDbTypesLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(primaryCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(additionalCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(contaminantsCheckBox)
                .addContainerGap())
        );
        fastaDbTypesPanelLayout.setVerticalGroup(
            fastaDbTypesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fastaDbTypesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(fastaDbTypesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(primaryCheckBox)
                    .addComponent(additionalCheckBox)
                    .addComponent(contaminantsCheckBox)
                    .addComponent(fastaDbTypesLabel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        updateButton.setText("update");
        updateButton.setToolTipText("update the selected fasta db");
        updateButton.setEnabled(false);

        javax.swing.GroupLayout fastaDbOverviewPanelLayout = new javax.swing.GroupLayout(fastaDbOverviewPanel);
        fastaDbOverviewPanel.setLayout(fastaDbOverviewPanelLayout);
        fastaDbOverviewPanelLayout.setHorizontalGroup(
            fastaDbOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fastaDbOverviewPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(fastaDbOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, fastaDbOverviewPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(updateButton, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(descriptionLabel)
                    .addComponent(fastaDbTypesPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(fastaDbListScrollPane, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        fastaDbOverviewPanelLayout.setVerticalGroup(
            fastaDbOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fastaDbOverviewPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(descriptionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fastaDbTypesPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fastaDbListScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(fastaDbOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(fastaDbOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(deleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(updateButton, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.4;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        fastaDbManagementPanel.add(fastaDbOverviewPanel, gridBagConstraints);

        fastaDbDetailParentPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Detail"));
        fastaDbDetailParentPanel.setOpaque(false);
        fastaDbDetailParentPanel.setPreferredSize(new java.awt.Dimension(100, 100));
        fastaDbDetailParentPanel.setLayout(new java.awt.GridBagLayout());

        fastaDbDetailPanel.setOpaque(false);
        fastaDbDetailPanel.setPreferredSize(new java.awt.Dimension(40, 40));

        nameTextField.setEnabled(false);
        nameTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nameTextFieldActionPerformed(evt);
            }
        });

        nameLabel.setText("Name*");
        nameLabel.setPreferredSize(new java.awt.Dimension(48, 14));

        fileNameLabel.setText("File Name*");

        fileNameTextField.setEnabled(false);

        filePathLabel.setText("File Path*");

        filePathTextField.setEnabled(false);

        versionLabel.setText("Version*");
        versionLabel.setPreferredSize(new java.awt.Dimension(48, 14));

        versionTextField.setEnabled(false);

        taxonomyLabel.setText("Taxonomy");

        headerParseRuleTextField.setEnabled(false);

        HeaderParseRuleLabel.setText("Parse rule");

        taxonomyTextField.setEnabled(false);

        javax.swing.GroupLayout fastaDbDetailPanelLayout = new javax.swing.GroupLayout(fastaDbDetailPanel);
        fastaDbDetailPanel.setLayout(fastaDbDetailPanelLayout);
        fastaDbDetailPanelLayout.setHorizontalGroup(
            fastaDbDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fastaDbDetailPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(fastaDbDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(fastaDbDetailPanelLayout.createSequentialGroup()
                        .addGroup(fastaDbDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(taxonomyLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE)
                            .addComponent(versionLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(HeaderParseRuleLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(fastaDbDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(versionTextField)
                            .addComponent(headerParseRuleTextField)
                            .addComponent(taxonomyTextField)))
                    .addGroup(fastaDbDetailPanelLayout.createSequentialGroup()
                        .addGroup(fastaDbDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(nameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(fileNameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(filePathLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(fastaDbDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(nameTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE)
                            .addComponent(filePathTextField)
                            .addComponent(fileNameTextField))))
                .addContainerGap())
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
                    .addComponent(fileNameLabel)
                    .addComponent(fileNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(fastaDbDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(filePathTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(fastaDbDetailPanelLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(filePathLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(fastaDbDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(versionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(versionLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(fastaDbDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(taxonomyLabel)
                    .addComponent(taxonomyTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(fastaDbDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(HeaderParseRuleLabel)
                    .addComponent(headerParseRuleTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        fastaDbDetailParentPanel.add(fastaDbDetailPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.6;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        fastaDbManagementPanel.add(fastaDbDetailParentPanel, gridBagConstraints);

        javax.swing.GroupLayout fastaDbManagmentParentPanelLayout = new javax.swing.GroupLayout(fastaDbManagmentParentPanel);
        fastaDbManagmentParentPanel.setLayout(fastaDbManagmentParentPanelLayout);
        fastaDbManagmentParentPanelLayout.setHorizontalGroup(
            fastaDbManagmentParentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, fastaDbManagmentParentPanelLayout.createSequentialGroup()
                .addContainerGap(780, Short.MAX_VALUE)
                .addComponent(selectButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(fastaDbManagementPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        fastaDbManagmentParentPanelLayout.setVerticalGroup(
            fastaDbManagmentParentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fastaDbManagmentParentPanelLayout.createSequentialGroup()
                .addComponent(fastaDbManagementPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 316, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(fastaDbManagmentParentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(selectButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        mainPanel.add(fastaDbManagmentParentPanel, "fastaDbManagementParentPanel");

        fastaDbSaveUpdatePanel.setBackground(new java.awt.Color(255, 255, 255));
        mainPanel.add(fastaDbSaveUpdatePanel, "fastaDbSaveUpdatePanel");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void nameTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nameTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_nameTextFieldActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel HeaderParseRuleLabel;
    private javax.swing.JButton addButton;
    private javax.swing.JCheckBox additionalCheckBox;
    private javax.swing.JButton closeButton;
    private javax.swing.JCheckBox contaminantsCheckBox;
    private javax.swing.JButton deleteButton;
    private javax.swing.JLabel descriptionLabel;
    private javax.swing.JPanel fastaDbDetailPanel;
    private javax.swing.JPanel fastaDbDetailParentPanel;
    private javax.swing.JList fastaDbList;
    private javax.swing.JScrollPane fastaDbListScrollPane;
    private javax.swing.JPanel fastaDbManagementPanel;
    private javax.swing.JPanel fastaDbManagmentParentPanel;
    private javax.swing.JPanel fastaDbOverviewPanel;
    private com.compomics.colims.client.view.admin.FastaDbSaveUpdatePanel fastaDbSaveUpdatePanel;
    private javax.swing.JLabel fastaDbTypesLabel;
    private javax.swing.JPanel fastaDbTypesPanel;
    private javax.swing.JLabel fileNameLabel;
    private javax.swing.JTextField fileNameTextField;
    private javax.swing.JLabel filePathLabel;
    private javax.swing.JTextField filePathTextField;
    private javax.swing.JTextField headerParseRuleTextField;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JCheckBox primaryCheckBox;
    private javax.swing.JButton selectButton;
    private javax.swing.JLabel taxonomyLabel;
    private javax.swing.JTextField taxonomyTextField;
    private javax.swing.JButton updateButton;
    private javax.swing.JLabel versionLabel;
    private javax.swing.JTextField versionTextField;
    // End of variables declaration//GEN-END:variables
}
