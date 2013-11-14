package com.compomics.colims.client.view;

import javax.swing.*;

/**
 *
 * @author Niels Hulstaert
 */
public class ProjectSetupPanel extends javax.swing.JPanel {
    
    private JFileChooser cpsFileChooser;
    private JFileChooser mgfFileChooser;
    private JFileChooser fastaFileChooser;
    
    /**
     * Creates new form ProjectSetupPanel
     */
    public ProjectSetupPanel() {
        initComponents();
        cpsFileChooser = new JFileChooser();
        mgfFileChooser = new JFileChooser();
        fastaFileChooser = new JFileChooser();
    }

    public JTextArea getDescriptionTextArea() {
        return descriptionTextArea;
    }

    public JTextField getTitleTextField() {
        return titleTextField;
    }

    public JComboBox getUserComboBox() {
        return userComboBox;
    }

    public JButton getBackButton() {
        return backButton;
    }

    public JButton getProceedButton() {
        return proceedButton;
    }

    public JPanel getTopPanel() {
        return topPanel;
    }

    public JButton getFinishButton() {
        return finishButton;
    }

    public JLabel getProceedInfoLabel() {
        return proceedInfoLabel;
    }   

    public JFileChooser getCpsFileChooser() {
        return cpsFileChooser;
    }

    public JFileChooser getMgfFileChooser() {
        return mgfFileChooser;
    }

    public JFileChooser getFastaFileChooser() {
        return fastaFileChooser;
    }

    public JButton getSelectCpsButton() {
        return selectCpsButton;
    }

    public JButton getSelectFastaButton() {
        return selectFastaButton;
    }

    public JButton getSelectMgfButton() {
        return selectMgfButton;
    }

    public JLabel getCpsFileLabel() {
        return cpsFileLabel;
    }

    public JLabel getFastaFileLabel() {
        return fastaFileLabel;
    }

    public JLabel getMgfFileLabel() {
        return mgfFileLabel;
    }

    public JTextField getLabelTextField() {
        return labelTextField;
    }        

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        topPanel = new javax.swing.JPanel();
        projectMetaDataPanel = new javax.swing.JPanel();
        titleTextField = new javax.swing.JTextField();
        titleLabel = new javax.swing.JLabel();
        descriptionLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        descriptionTextArea = new javax.swing.JTextArea();
        userLabel = new javax.swing.JLabel();
        userComboBox = new javax.swing.JComboBox();
        labelTextField = new javax.swing.JTextField();
        titleLabel1 = new javax.swing.JLabel();
        dataImportPanel = new javax.swing.JPanel();
        fileSelectionLabel = new javax.swing.JLabel();
        fileSelectionLabel1 = new javax.swing.JLabel();
        fileSelectionLabel2 = new javax.swing.JLabel();
        cpsFileLabel = new javax.swing.JLabel();
        fastaFileLabel = new javax.swing.JLabel();
        mgfFileLabel = new javax.swing.JLabel();
        selectCpsButton = new javax.swing.JButton();
        selectMgfButton = new javax.swing.JButton();
        selectFastaButton = new javax.swing.JButton();
        bottomPanel = new javax.swing.JPanel();
        backButton = new javax.swing.JButton();
        proceedButton = new javax.swing.JButton();
        proceedInfoLabel = new javax.swing.JLabel();
        finishButton = new javax.swing.JButton();

        setOpaque(false);

        topPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        topPanel.setOpaque(false);
        topPanel.setPreferredSize(new java.awt.Dimension(400, 285));
        topPanel.setLayout(new java.awt.CardLayout());

        projectMetaDataPanel.setName("projectMetaDataPanel"); // NOI18N
        projectMetaDataPanel.setOpaque(false);

        titleLabel.setText("title");
        titleLabel.setPreferredSize(new java.awt.Dimension(48, 14));

        descriptionLabel.setText("description");
        descriptionLabel.setPreferredSize(new java.awt.Dimension(48, 14));

        descriptionTextArea.setColumns(20);
        descriptionTextArea.setRows(5);
        jScrollPane1.setViewportView(descriptionTextArea);

        userLabel.setText("user");
        userLabel.setPreferredSize(new java.awt.Dimension(48, 14));

        titleLabel1.setText("label");
        titleLabel1.setPreferredSize(new java.awt.Dimension(48, 14));

        javax.swing.GroupLayout projectMetaDataPanelLayout = new javax.swing.GroupLayout(projectMetaDataPanel);
        projectMetaDataPanel.setLayout(projectMetaDataPanelLayout);
        projectMetaDataPanelLayout.setHorizontalGroup(
            projectMetaDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(projectMetaDataPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(projectMetaDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(titleLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(descriptionLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE)
                    .addComponent(userLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(projectMetaDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(userComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(labelTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 309, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1))
                .addContainerGap())
            .addGroup(projectMetaDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(projectMetaDataPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(titleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addComponent(titleTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 309, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        projectMetaDataPanelLayout.setVerticalGroup(
            projectMetaDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(projectMetaDataPanelLayout.createSequentialGroup()
                .addGap(51, 51, 51)
                .addGroup(projectMetaDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(titleLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(projectMetaDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(projectMetaDataPanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(descriptionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(141, 141, 141))
                    .addGroup(projectMetaDataPanelLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(projectMetaDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(userComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(userLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(62, Short.MAX_VALUE))))
            .addGroup(projectMetaDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(projectMetaDataPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(projectMetaDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(titleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(titleTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(254, Short.MAX_VALUE)))
        );

        topPanel.add(projectMetaDataPanel, "projectMetaDataPanel");
        projectMetaDataPanel.getAccessibleContext().setAccessibleName("");

        dataImportPanel.setName("dataImportPanel"); // NOI18N
        dataImportPanel.setOpaque(false);

        fileSelectionLabel.setText("Select a PeptideShaker .cps file");

        fileSelectionLabel1.setText("Select an .mgf file");

        fileSelectionLabel2.setText("Select a FASTA file");

        cpsFileLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        fastaFileLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        mgfFileLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        selectCpsButton.setText("Browse");

        selectMgfButton.setText("Browse");

        selectFastaButton.setText("Browse");

        javax.swing.GroupLayout dataImportPanelLayout = new javax.swing.GroupLayout(dataImportPanel);
        dataImportPanel.setLayout(dataImportPanelLayout);
        dataImportPanelLayout.setHorizontalGroup(
            dataImportPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dataImportPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(dataImportPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cpsFileLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                    .addComponent(fileSelectionLabel1)
                    .addComponent(mgfFileLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(fileSelectionLabel2)
                    .addComponent(fastaFileLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(fileSelectionLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(dataImportPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(selectFastaButton, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(selectMgfButton, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(selectCpsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        dataImportPanelLayout.setVerticalGroup(
            dataImportPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dataImportPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(fileSelectionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(dataImportPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cpsFileLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(selectCpsButton))
                .addGap(13, 13, 13)
                .addComponent(fileSelectionLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(dataImportPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(mgfFileLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(selectMgfButton))
                .addGap(18, 18, 18)
                .addComponent(fileSelectionLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(dataImportPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fastaFileLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(selectFastaButton))
                .addContainerGap(86, Short.MAX_VALUE))
        );

        topPanel.add(dataImportPanel, "dataImportPanel");
        dataImportPanel.getAccessibleContext().setAccessibleName("");

        bottomPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        bottomPanel.setOpaque(false);

        backButton.setText("back");
        backButton.setMaximumSize(new java.awt.Dimension(80, 25));
        backButton.setMinimumSize(new java.awt.Dimension(80, 25));
        backButton.setPreferredSize(new java.awt.Dimension(80, 25));

        proceedButton.setText("proceed");
        proceedButton.setMaximumSize(new java.awt.Dimension(80, 25));
        proceedButton.setMinimumSize(new java.awt.Dimension(80, 25));
        proceedButton.setPreferredSize(new java.awt.Dimension(80, 25));

        proceedInfoLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        proceedInfoLabel.setMaximumSize(new java.awt.Dimension(34, 20));
        proceedInfoLabel.setMinimumSize(new java.awt.Dimension(34, 20));
        proceedInfoLabel.setName(""); // NOI18N
        proceedInfoLabel.setPreferredSize(new java.awt.Dimension(34, 20));

        finishButton.setText("finish");
        finishButton.setMaximumSize(new java.awt.Dimension(80, 25));
        finishButton.setMinimumSize(new java.awt.Dimension(80, 25));
        finishButton.setPreferredSize(new java.awt.Dimension(80, 25));

        javax.swing.GroupLayout bottomPanelLayout = new javax.swing.GroupLayout(bottomPanel);
        bottomPanel.setLayout(bottomPanelLayout);
        bottomPanelLayout.setHorizontalGroup(
            bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, bottomPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(proceedInfoLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(14, 14, 14)
                .addComponent(backButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(proceedButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(finishButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        bottomPanelLayout.setVerticalGroup(
            bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bottomPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(proceedInfoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(proceedButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(backButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(finishButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(topPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 426, Short.MAX_VALUE)
            .addComponent(bottomPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(topPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bottomPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton backButton;
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JLabel cpsFileLabel;
    private javax.swing.JPanel dataImportPanel;
    private javax.swing.JLabel descriptionLabel;
    private javax.swing.JTextArea descriptionTextArea;
    private javax.swing.JLabel fastaFileLabel;
    private javax.swing.JLabel fileSelectionLabel;
    private javax.swing.JLabel fileSelectionLabel1;
    private javax.swing.JLabel fileSelectionLabel2;
    private javax.swing.JButton finishButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField labelTextField;
    private javax.swing.JLabel mgfFileLabel;
    private javax.swing.JButton proceedButton;
    private javax.swing.JLabel proceedInfoLabel;
    private javax.swing.JPanel projectMetaDataPanel;
    private javax.swing.JButton selectCpsButton;
    private javax.swing.JButton selectFastaButton;
    private javax.swing.JButton selectMgfButton;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JLabel titleLabel1;
    private javax.swing.JTextField titleTextField;
    private javax.swing.JPanel topPanel;
    private javax.swing.JComboBox userComboBox;
    private javax.swing.JLabel userLabel;
    // End of variables declaration//GEN-END:variables
}
