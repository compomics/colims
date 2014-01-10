package com.compomics.colims.client.view.admin.instrument;

import com.compomics.colims.client.compoment.DualList;
import com.compomics.colims.model.InstrumentCvTerm;
import java.awt.Dialog;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;

/**
 *
 * @author Niels Hulstaert
 */
public class InstrumentEditDialog extends javax.swing.JDialog {
    
    /**
     * Creates new form InstrumentCvDialog
     */
    public InstrumentEditDialog(final Dialog parent, final boolean modal) {
        super(parent, modal);
        
        initComponents();
                
        setLocationRelativeTo(parent);
    }

    public JButton getCloseInstrumentEditButton() {
        return closeInstrumentEditButton;
    }

    public DualList<InstrumentCvTerm> getCvTermDualList() {
        return cvTermDualList;
    }

    public JList getCvTermSummaryList() {
        return cvTermSummaryList;
    }

    public JButton getInstrumentCvTermsCrudButton() {
        return instrumentCvTermsCrudButton;
    }

    public JButton getInstrumentSaveOrUpdateButton() {
        return instrumentSaveOrUpdateButton;
    }

    public JTextField getNameTextField() {
        return nameTextField;
    }

    public JComboBox getTypeComboBox() {
        return typeComboBox;
    }

    public JButton getInstrumentTypesCrudButton() {
        return instrumentTypesCrudButton;
    }     

    public JLabel getInstrumentStateInfoLabel() {
        return instrumentStateInfoLabel;
    }        

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        instrumentEditPanel = new javax.swing.JPanel();
        instrumentStateInfoLabel = new javax.swing.JLabel();
        nameLabel = new javax.swing.JLabel();
        typeLabel = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        typeComboBox = new javax.swing.JComboBox();
        instrumentTypesCrudButton = new javax.swing.JButton();
        closeInstrumentEditButton = new javax.swing.JButton();
        instrumentSaveOrUpdateButton = new javax.swing.JButton();
        cvTermsPanel = new javax.swing.JPanel();
        cvTermSummaryScrollPane = new javax.swing.JScrollPane();
        cvTermSummaryList = new javax.swing.JList();
        cvTermDualList = new com.compomics.colims.client.compoment.DualList<InstrumentCvTerm>();
        instrumentCvTermsCrudButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("instrument management");
        setPreferredSize(new java.awt.Dimension(720, 400));

        instrumentEditPanel.setBackground(new java.awt.Color(255, 255, 255));

        instrumentStateInfoLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        instrumentStateInfoLabel.setForeground(new java.awt.Color(250, 0, 0));
        instrumentStateInfoLabel.setMaximumSize(new java.awt.Dimension(0, 20));
        instrumentStateInfoLabel.setMinimumSize(new java.awt.Dimension(0, 20));
        instrumentStateInfoLabel.setPreferredSize(new java.awt.Dimension(0, 20));

        nameLabel.setText("name");

        typeLabel.setText("type");

        instrumentTypesCrudButton.setText("edit...");
        instrumentTypesCrudButton.setMaximumSize(new java.awt.Dimension(80, 25));
        instrumentTypesCrudButton.setMinimumSize(new java.awt.Dimension(80, 25));
        instrumentTypesCrudButton.setPreferredSize(new java.awt.Dimension(80, 25));

        closeInstrumentEditButton.setText("close");
        closeInstrumentEditButton.setMaximumSize(new java.awt.Dimension(80, 25));
        closeInstrumentEditButton.setMinimumSize(new java.awt.Dimension(80, 25));
        closeInstrumentEditButton.setPreferredSize(new java.awt.Dimension(80, 25));

        instrumentSaveOrUpdateButton.setText("save");
        instrumentSaveOrUpdateButton.setMaximumSize(new java.awt.Dimension(80, 25));
        instrumentSaveOrUpdateButton.setMinimumSize(new java.awt.Dimension(80, 25));
        instrumentSaveOrUpdateButton.setPreferredSize(new java.awt.Dimension(80, 25));

        cvTermsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("CV terms"));
        cvTermsPanel.setOpaque(false);

        cvTermSummaryScrollPane.setOpaque(false);

        cvTermSummaryScrollPane.setViewportView(cvTermSummaryList);

        instrumentCvTermsCrudButton.setText("edit...");
        instrumentCvTermsCrudButton.setToolTipText("edit the available CV terms of the selected CV term type");
        instrumentCvTermsCrudButton.setMaximumSize(new java.awt.Dimension(80, 25));
        instrumentCvTermsCrudButton.setMinimumSize(new java.awt.Dimension(80, 25));
        instrumentCvTermsCrudButton.setPreferredSize(new java.awt.Dimension(80, 25));

        javax.swing.GroupLayout cvTermsPanelLayout = new javax.swing.GroupLayout(cvTermsPanel);
        cvTermsPanel.setLayout(cvTermsPanelLayout);
        cvTermsPanelLayout.setHorizontalGroup(
            cvTermsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, cvTermsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cvTermSummaryScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(cvTermsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(instrumentCvTermsCrudButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cvTermDualList, javax.swing.GroupLayout.DEFAULT_SIZE, 470, Short.MAX_VALUE))
                .addContainerGap())
        );
        cvTermsPanelLayout.setVerticalGroup(
            cvTermsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cvTermsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(cvTermsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cvTermSummaryScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                    .addComponent(cvTermDualList, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(instrumentCvTermsCrudButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout instrumentEditPanelLayout = new javax.swing.GroupLayout(instrumentEditPanel);
        instrumentEditPanel.setLayout(instrumentEditPanelLayout);
        instrumentEditPanelLayout.setHorizontalGroup(
            instrumentEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(instrumentEditPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(instrumentEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cvTermsPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, instrumentEditPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(instrumentSaveOrUpdateButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(closeInstrumentEditButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(instrumentStateInfoLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(instrumentEditPanelLayout.createSequentialGroup()
                        .addGroup(instrumentEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(nameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                            .addComponent(typeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(instrumentEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(nameTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE)
                            .addComponent(typeComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(instrumentTypesCrudButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        instrumentEditPanelLayout.setVerticalGroup(
            instrumentEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(instrumentEditPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(instrumentStateInfoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(instrumentEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameLabel)
                    .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(instrumentEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(instrumentTypesCrudButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(typeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(typeLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cvTermsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(instrumentEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(closeInstrumentEditButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(instrumentSaveOrUpdateButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 722, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(instrumentEditPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(instrumentEditPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton closeInstrumentEditButton;
    private com.compomics.colims.client.compoment.DualList<InstrumentCvTerm> cvTermDualList;
    private javax.swing.JList cvTermSummaryList;
    private javax.swing.JScrollPane cvTermSummaryScrollPane;
    private javax.swing.JPanel cvTermsPanel;
    private javax.swing.JButton instrumentCvTermsCrudButton;
    private javax.swing.JPanel instrumentEditPanel;
    private javax.swing.JButton instrumentSaveOrUpdateButton;
    private javax.swing.JLabel instrumentStateInfoLabel;
    private javax.swing.JButton instrumentTypesCrudButton;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JComboBox typeComboBox;
    private javax.swing.JLabel typeLabel;
    // End of variables declaration//GEN-END:variables
    
}
