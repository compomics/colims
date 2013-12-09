package com.compomics.colims.client.view.admin.instrument;

import com.compomics.colims.client.compoment.DualList;
import com.compomics.colims.model.InstrumentCvTerm;
import java.awt.Frame;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JTextField;

/**
 *
 * @author niels
 */
public class InstrumentEditDialog extends javax.swing.JDialog {
    
    /**
     * Creates new form InstrumentCvDialog
     */
    public InstrumentEditDialog(Frame parent, boolean modal) {
        super(parent, modal);
        
        initComponents();
                
        setLocationRelativeTo(parent);
    }

    public JButton getCancelInstrumentEditButton() {
        return cancelInstrumentEditButton;
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        instrumentEditPanel = new javax.swing.JPanel();
        nameLabel = new javax.swing.JLabel();
        typeLabel = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        typeComboBox = new javax.swing.JComboBox();
        instrumentTypesCrudButton = new javax.swing.JButton();
        cancelInstrumentEditButton = new javax.swing.JButton();
        instrumentSaveOrUpdateButton = new javax.swing.JButton();
        cvTermsPanel = new javax.swing.JPanel();
        cvTermSummaryScrollPane = new javax.swing.JScrollPane();
        cvTermSummaryList = new javax.swing.JList();
        cvTermDualList = new com.compomics.colims.client.compoment.DualList<InstrumentCvTerm>();
        instrumentCvTermsCrudButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("instrument management");
        setPreferredSize(new java.awt.Dimension(1100, 500));

        instrumentEditPanel.setBackground(new java.awt.Color(255, 255, 255));

        nameLabel.setText("name");

        typeLabel.setText("type");

        instrumentTypesCrudButton.setText("edit...");
        instrumentTypesCrudButton.setMaximumSize(new java.awt.Dimension(80, 25));
        instrumentTypesCrudButton.setMinimumSize(new java.awt.Dimension(80, 25));
        instrumentTypesCrudButton.setPreferredSize(new java.awt.Dimension(80, 25));

        cancelInstrumentEditButton.setText("cancel");
        cancelInstrumentEditButton.setMaximumSize(new java.awt.Dimension(80, 25));
        cancelInstrumentEditButton.setMinimumSize(new java.awt.Dimension(80, 25));
        cancelInstrumentEditButton.setPreferredSize(new java.awt.Dimension(80, 25));

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
            .addGroup(cvTermsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cvTermSummaryScrollPane)
                .addGap(18, 18, 18)
                .addGroup(cvTermsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(instrumentCvTermsCrudButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cvTermDualList, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        cvTermsPanelLayout.setVerticalGroup(
            cvTermsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cvTermsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(cvTermsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cvTermSummaryScrollPane)
                    .addComponent(cvTermDualList, javax.swing.GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE))
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
                    .addGroup(instrumentEditPanelLayout.createSequentialGroup()
                        .addGroup(instrumentEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(nameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE)
                            .addComponent(typeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(instrumentEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(nameTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE)
                            .addComponent(typeComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(instrumentTypesCrudButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, instrumentEditPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(instrumentSaveOrUpdateButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelInstrumentEditButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        instrumentEditPanelLayout.setVerticalGroup(
            instrumentEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(instrumentEditPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(instrumentEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameLabel)
                    .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(instrumentEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(typeLabel)
                    .addComponent(typeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(instrumentTypesCrudButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(cvTermsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(instrumentEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(instrumentSaveOrUpdateButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cancelInstrumentEditButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 964, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(instrumentEditPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 433, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(instrumentEditPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelInstrumentEditButton;
    private com.compomics.colims.client.compoment.DualList<InstrumentCvTerm> cvTermDualList;
    private javax.swing.JList cvTermSummaryList;
    private javax.swing.JScrollPane cvTermSummaryScrollPane;
    private javax.swing.JPanel cvTermsPanel;
    private javax.swing.JButton instrumentCvTermsCrudButton;
    private javax.swing.JPanel instrumentEditPanel;
    private javax.swing.JButton instrumentSaveOrUpdateButton;
    private javax.swing.JButton instrumentTypesCrudButton;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JComboBox typeComboBox;
    private javax.swing.JLabel typeLabel;
    // End of variables declaration//GEN-END:variables
    
}
