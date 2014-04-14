package com.compomics.colims.client.view;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Niels Hulstaert
 */
public class PeptideShakerDataImportPanel extends javax.swing.JPanel {

    private final JFileChooser cpsFileChooser = new JFileChooser();
    private final JFileChooser mgfFileChooser = new JFileChooser();  

    /**
     * Creates new form PeptideShakerDataImportPanel
     */
    public PeptideShakerDataImportPanel() {
        initComponents();
        
        mgfFilesScrollPane.getViewport().setOpaque(false);
    }  

    public JFileChooser getCpsFileChooser() {
        return cpsFileChooser;
    }

    public JFileChooser getMgfFileChooser() {
        return mgfFileChooser;
    }

    public JButton getAddMgfButton() {
        return addMgfButton;
    }

    public JLabel getCpsFileLabel() {
        return cpsFileLabel;
    }

    public JLabel getFastaDbLabel() {
        return fastaDbLabel;
    }

    public JList getMgfFileList() {
        return mgfFileList;
    }

    public JButton getRemoveMgfButton() {
        return removeMgfButton;
    }

    public JButton getSelectCpsButton() {
        return selectCpsButton;
    }

    public JButton getSelectFastaButton() {
        return selectFastaButton;
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

        leftPanel = new javax.swing.JPanel();
        selectCpsButton = new javax.swing.JButton();
        cpsFileLabel = new javax.swing.JLabel();
        cpsFileSelectionLabel = new javax.swing.JLabel();
        fastaDbLabel = new javax.swing.JLabel();
        selectFastaButton = new javax.swing.JButton();
        fastaFileSelectionLabel = new javax.swing.JLabel();
        rightPanel = new javax.swing.JPanel();
        mgfFileSelectionLabel = new javax.swing.JLabel();
        mgfFilesScrollPane = new javax.swing.JScrollPane();
        mgfFileList = new javax.swing.JList();
        addMgfButton = new javax.swing.JButton();
        removeMgfButton = new javax.swing.JButton();
        separator = new javax.swing.JSeparator();

        setOpaque(false);
        setPreferredSize(new java.awt.Dimension(706, 280));
        setLayout(new java.awt.GridBagLayout());

        leftPanel.setOpaque(false);
        leftPanel.setPreferredSize(new java.awt.Dimension(0, 0));

        selectCpsButton.setText("browse...");
        selectCpsButton.setMaximumSize(new java.awt.Dimension(80, 25));
        selectCpsButton.setMinimumSize(new java.awt.Dimension(80, 25));
        selectCpsButton.setPreferredSize(new java.awt.Dimension(80, 25));

        cpsFileLabel.setBorder(BorderFactory.createCompoundBorder(UIManager.getBorder("TextField.border"), new EmptyBorder(0, 5, 0, 0)));

        cpsFileSelectionLabel.setText("Select a PeptideShaker cps file");

        fastaDbLabel.setBorder(BorderFactory.createCompoundBorder(UIManager.getBorder("TextField.border"), new EmptyBorder(0, 5, 0, 0)));

        selectFastaButton.setText("browse...");
        selectFastaButton.setMaximumSize(new java.awt.Dimension(80, 25));
        selectFastaButton.setMinimumSize(new java.awt.Dimension(80, 25));
        selectFastaButton.setPreferredSize(new java.awt.Dimension(80, 25));

        fastaFileSelectionLabel.setText("Select a FASTA file");

        javax.swing.GroupLayout leftPanelLayout = new javax.swing.GroupLayout(leftPanel);
        leftPanel.setLayout(leftPanelLayout);
        leftPanelLayout.setHorizontalGroup(
            leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(leftPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(leftPanelLayout.createSequentialGroup()
                        .addComponent(fastaDbLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(selectFastaButton, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(fastaFileSelectionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, leftPanelLayout.createSequentialGroup()
                        .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(cpsFileSelectionLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE)
                            .addComponent(cpsFileLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(selectCpsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        leftPanelLayout.setVerticalGroup(
            leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(leftPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cpsFileSelectionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cpsFileLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(selectCpsButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(fastaFileSelectionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fastaDbLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(selectFastaButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(leftPanel, gridBagConstraints);

        rightPanel.setOpaque(false);
        rightPanel.setPreferredSize(new java.awt.Dimension(0, 0));

        mgfFileSelectionLabel.setText("Select MGF file(s)");

        mgfFilesScrollPane.setBorder(BorderFactory.createCompoundBorder(UIManager.getBorder("TextField.border"), new EmptyBorder(0, 5, 0, 0)));
        mgfFilesScrollPane.setOpaque(false);

        mgfFilesScrollPane.setViewportView(mgfFileList);

        addMgfButton.setText("add...");
        addMgfButton.setMaximumSize(new java.awt.Dimension(80, 25));
        addMgfButton.setMinimumSize(new java.awt.Dimension(80, 25));
        addMgfButton.setPreferredSize(new java.awt.Dimension(80, 25));

        removeMgfButton.setText("remove");

        javax.swing.GroupLayout rightPanelLayout = new javax.swing.GroupLayout(rightPanel);
        rightPanel.setLayout(rightPanelLayout);
        rightPanelLayout.setHorizontalGroup(
            rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rightPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(rightPanelLayout.createSequentialGroup()
                        .addComponent(mgfFilesScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(addMgfButton, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(removeMgfButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(mgfFileSelectionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        rightPanelLayout.setVerticalGroup(
            rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rightPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mgfFileSelectionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(mgfFilesScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
                    .addGroup(rightPanelLayout.createSequentialGroup()
                        .addComponent(addMgfButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removeMgfButton)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(rightPanel, gridBagConstraints);

        separator.setOrientation(javax.swing.SwingConstants.VERTICAL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        add(separator, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addMgfButton;
    private javax.swing.JLabel cpsFileLabel;
    private javax.swing.JLabel cpsFileSelectionLabel;
    private javax.swing.JLabel fastaDbLabel;
    private javax.swing.JLabel fastaFileSelectionLabel;
    private javax.swing.JPanel leftPanel;
    private javax.swing.JList mgfFileList;
    private javax.swing.JLabel mgfFileSelectionLabel;
    private javax.swing.JScrollPane mgfFilesScrollPane;
    private javax.swing.JButton removeMgfButton;
    private javax.swing.JPanel rightPanel;
    private javax.swing.JButton selectCpsButton;
    private javax.swing.JButton selectFastaButton;
    private javax.swing.JSeparator separator;
    // End of variables declaration//GEN-END:variables
}
