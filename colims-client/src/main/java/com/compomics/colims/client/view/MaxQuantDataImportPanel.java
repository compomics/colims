package com.compomics.colims.client.view;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JTextField;

/**
 *
 * @author Niels Hulstaert
 */
public class MaxQuantDataImportPanel extends javax.swing.JPanel {

    private final JFileChooser parameterDirectoryChooser = new JFileChooser();
    private final JFileChooser combinedFolderChooser = new JFileChooser();
    private final JFileChooser primaryFastaFileChooser = new JFileChooser();
    private final JFileChooser additionalFastaFileChooser = new JFileChooser();
    private final JFileChooser contaminantsFastaFileChooser = new JFileChooser();

    /**
     * Creates new form PeptideShakerDataImportPanel
     */
    public MaxQuantDataImportPanel() {
        initComponents();
    }

    public JFileChooser getParameterDirectoryChooser() {
        return parameterDirectoryChooser;
    }

    public JFileChooser getCombinedFolderChooser() {
        return combinedFolderChooser;
    }

    public JFileChooser getPrimaryFastaFileChooser() {
        return primaryFastaFileChooser;
    }

    public JFileChooser getAdditionalFastaFileChooser() {
        return additionalFastaFileChooser;
    }

    public JFileChooser getContaminantsFastaFileChooser() {
        return contaminantsFastaFileChooser;
    }

    public JTextField getContaminantsFastaDbTextField() {
        return contaminantsFastaDbTextField;
    }

    public JTextField getParameterDirectoryTextField() {
        return parameterDirectoryTextField;
    }

    public JTextField getCombinedFolderDirectoryTextField() {
        return combinedFolderDirectoryTextField;
    }

    public JTextField getPrimaryFastaDbTextField() {
        return primaryFastaDbTextField;
    }

    public JList<String> getAdditionalFastaFileList() {
        return additionalFastaFileList;
    }

    public JButton getRemoveAdditionalFastaDbButton() {
        return removeAdditionalFastaDbButton;
    }

    public JButton getSelectAdditionalFastaDbButton() {
        return selectAdditionalFastaDbButton;
    }

    public JButton getSelectContaminantsFastaDbButton() {
        return selectContaminantsFastaDbButton;
    }

    public JButton getSelectParameterDirectoryButton() {
        return selectParameterDirectoryButton;
    }

    public JButton getSelectCombinedFolderButton() {
        return selectCombinedFolderButton;
    }

    public JButton getSelectPrimaryFastaDbButton() {
        return selectPrimaryFastaDbButton;
    }

    public JCheckBox getContaminantsCheckBox() {
        return contaminantsCheckBox;
    }

    public JCheckBox getUnidentifiedSpectraCheckBox() {
        return unidentifiedSpectraCheckBox;
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

        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        leftPanel = new javax.swing.JPanel();
        combinedFolderDirectorySelectionLabel = new javax.swing.JLabel();
        combinedFolderDirectoryTextField = new javax.swing.JTextField();
        selectCombinedFolderButton = new javax.swing.JButton();
        maxQuantDirectorySelectionLabel = new javax.swing.JLabel();
        selectParameterDirectoryButton = new javax.swing.JButton();
        parameterDirectoryTextField = new javax.swing.JTextField();
        contaminantsCheckBox = new javax.swing.JCheckBox();
        unidentifiedSpectraCheckBox = new javax.swing.JCheckBox();
        separator = new javax.swing.JSeparator();
        rightPanel = new javax.swing.JPanel();
        selectPrimaryFastaDbButton = new javax.swing.JButton();
        primaryFastaFileSelectionLabel = new javax.swing.JLabel();
        primaryFastaDbTextField = new javax.swing.JTextField();
        contaminantsFastaFileSelectionLabel = new javax.swing.JLabel();
        contaminantsFastaDbTextField = new javax.swing.JTextField();
        selectContaminantsFastaDbButton = new javax.swing.JButton();
        additionalFastaFileSelectionLabel = new javax.swing.JLabel();
        additionalFastaDbScrollPane = new javax.swing.JScrollPane();
        additionalFastaFileList = new javax.swing.JList<>();
        selectAdditionalFastaDbButton = new javax.swing.JButton();
        removeAdditionalFastaDbButton = new javax.swing.JButton();

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        combinedFolderDirectorySelectionLabel.setText("Select the combined folder directory*:");

        combinedFolderDirectoryTextField.setEditable(false);

        selectCombinedFolderButton.setText("browse...");
        selectCombinedFolderButton.setMaximumSize(new java.awt.Dimension(90, 25));
        selectCombinedFolderButton.setMinimumSize(new java.awt.Dimension(90, 25));
        selectCombinedFolderButton.setPreferredSize(new java.awt.Dimension(90, 25));

        maxQuantDirectorySelectionLabel.setText("Select the parameter file (mqpar) directory*:");

        selectParameterDirectoryButton.setText("browse...");
        selectParameterDirectoryButton.setMaximumSize(new java.awt.Dimension(80, 25));
        selectParameterDirectoryButton.setMinimumSize(new java.awt.Dimension(80, 25));
        selectParameterDirectoryButton.setPreferredSize(new java.awt.Dimension(80, 25));

        parameterDirectoryTextField.setEditable(false);

        contaminantsCheckBox.setText("Import proteins from contaminants file");

        unidentifiedSpectraCheckBox.setText("Import unidentified spectra");

        javax.swing.GroupLayout leftPanelLayout = new javax.swing.GroupLayout(leftPanel);
        leftPanel.setLayout(leftPanelLayout);
        leftPanelLayout.setHorizontalGroup(
            leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(leftPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(leftPanelLayout.createSequentialGroup()
                        .addComponent(parameterDirectoryTextField)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(selectParameterDirectoryButton, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(leftPanelLayout.createSequentialGroup()
                        .addComponent(combinedFolderDirectoryTextField)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(selectCombinedFolderButton, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(leftPanelLayout.createSequentialGroup()
                        .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(combinedFolderDirectorySelectionLabel)
                            .addComponent(maxQuantDirectorySelectionLabel)
                            .addComponent(contaminantsCheckBox)
                            .addComponent(unidentifiedSpectraCheckBox))
                        .addGap(0, 228, Short.MAX_VALUE)))
                .addContainerGap())
        );
        leftPanelLayout.setVerticalGroup(
            leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(leftPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(combinedFolderDirectorySelectionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(combinedFolderDirectoryTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(selectCombinedFolderButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(maxQuantDirectorySelectionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(parameterDirectoryTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(selectParameterDirectoryButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14)
                .addComponent(contaminantsCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(unidentifiedSpectraCheckBox)
                .addContainerGap(76, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(leftPanel, gridBagConstraints);

        separator.setOrientation(javax.swing.SwingConstants.VERTICAL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        add(separator, gridBagConstraints);

        rightPanel.setPreferredSize(new java.awt.Dimension(480, 175));

        selectPrimaryFastaDbButton.setText("browse...");
        selectPrimaryFastaDbButton.setMaximumSize(new java.awt.Dimension(90, 25));
        selectPrimaryFastaDbButton.setMinimumSize(new java.awt.Dimension(90, 25));
        selectPrimaryFastaDbButton.setPreferredSize(new java.awt.Dimension(90, 25));

        primaryFastaFileSelectionLabel.setText("Select a primary FASTA file*:");

        primaryFastaDbTextField.setEditable(false);

        contaminantsFastaFileSelectionLabel.setText("Select a contaminants FASTA file*:");

        contaminantsFastaDbTextField.setEditable(false);

        selectContaminantsFastaDbButton.setText("browse...");
        selectContaminantsFastaDbButton.setMaximumSize(new java.awt.Dimension(80, 25));
        selectContaminantsFastaDbButton.setMinimumSize(new java.awt.Dimension(80, 25));
        selectContaminantsFastaDbButton.setPreferredSize(new java.awt.Dimension(80, 25));

        additionalFastaFileSelectionLabel.setText("Select an additional FASTA file (with e.g. some additional sequences):");

        additionalFastaDbScrollPane.setMaximumSize(new java.awt.Dimension(32767222, 32767222));

        additionalFastaFileList.setMaximumSize(new java.awt.Dimension(545465566, 545465566));
        additionalFastaFileList.setMinimumSize(new java.awt.Dimension(25, 25));
        additionalFastaFileList.setPreferredSize(new java.awt.Dimension(25, 2));
        additionalFastaDbScrollPane.setViewportView(additionalFastaFileList);

        selectAdditionalFastaDbButton.setText("browse...");
        selectAdditionalFastaDbButton.setMaximumSize(new java.awt.Dimension(80, 25));
        selectAdditionalFastaDbButton.setMinimumSize(new java.awt.Dimension(80, 25));
        selectAdditionalFastaDbButton.setPreferredSize(new java.awt.Dimension(80, 25));

        removeAdditionalFastaDbButton.setText("remove");
        removeAdditionalFastaDbButton.setMaximumSize(new java.awt.Dimension(90, 25));
        removeAdditionalFastaDbButton.setMinimumSize(new java.awt.Dimension(90, 25));
        removeAdditionalFastaDbButton.setPreferredSize(new java.awt.Dimension(90, 25));

        javax.swing.GroupLayout rightPanelLayout = new javax.swing.GroupLayout(rightPanel);
        rightPanel.setLayout(rightPanelLayout);
        rightPanelLayout.setHorizontalGroup(
            rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rightPanelLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(rightPanelLayout.createSequentialGroup()
                        .addComponent(contaminantsFastaDbTextField)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(selectContaminantsFastaDbButton, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(rightPanelLayout.createSequentialGroup()
                        .addComponent(primaryFastaDbTextField)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(selectPrimaryFastaDbButton, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7))
                    .addGroup(rightPanelLayout.createSequentialGroup()
                        .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(contaminantsFastaFileSelectionLabel)
                            .addComponent(primaryFastaFileSelectionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))))
            .addGroup(rightPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(additionalFastaFileSelectionLabel)
                    .addGroup(rightPanelLayout.createSequentialGroup()
                        .addComponent(additionalFastaDbScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 447, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(selectAdditionalFastaDbButton, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(removeAdditionalFastaDbButton, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(7, 7, 7))
        );
        rightPanelLayout.setVerticalGroup(
            rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rightPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(primaryFastaFileSelectionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(primaryFastaDbTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(selectPrimaryFastaDbButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(contaminantsFastaFileSelectionLabel)
                .addGap(6, 6, 6)
                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(contaminantsFastaDbTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(selectContaminantsFastaDbButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(additionalFastaFileSelectionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(rightPanelLayout.createSequentialGroup()
                        .addComponent(selectAdditionalFastaDbButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removeAdditionalFastaDbButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 39, Short.MAX_VALUE))
                    .addComponent(additionalFastaDbScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 93, Short.MAX_VALUE))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(rightPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane additionalFastaDbScrollPane;
    private javax.swing.JList<String> additionalFastaFileList;
    private javax.swing.JLabel additionalFastaFileSelectionLabel;
    private javax.swing.JLabel combinedFolderDirectorySelectionLabel;
    private javax.swing.JTextField combinedFolderDirectoryTextField;
    private javax.swing.JCheckBox contaminantsCheckBox;
    private javax.swing.JTextField contaminantsFastaDbTextField;
    private javax.swing.JLabel contaminantsFastaFileSelectionLabel;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel leftPanel;
    private javax.swing.JLabel maxQuantDirectorySelectionLabel;
    private javax.swing.JTextField parameterDirectoryTextField;
    private javax.swing.JTextField primaryFastaDbTextField;
    private javax.swing.JLabel primaryFastaFileSelectionLabel;
    private javax.swing.JButton removeAdditionalFastaDbButton;
    private javax.swing.JPanel rightPanel;
    private javax.swing.JButton selectAdditionalFastaDbButton;
    private javax.swing.JButton selectCombinedFolderButton;
    private javax.swing.JButton selectContaminantsFastaDbButton;
    private javax.swing.JButton selectParameterDirectoryButton;
    private javax.swing.JButton selectPrimaryFastaDbButton;
    private javax.swing.JSeparator separator;
    private javax.swing.JCheckBox unidentifiedSpectraCheckBox;
    // End of variables declaration//GEN-END:variables
}
