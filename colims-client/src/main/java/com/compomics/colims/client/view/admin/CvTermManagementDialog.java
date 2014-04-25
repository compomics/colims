package com.compomics.colims.client.view.admin;

import java.awt.Frame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author Niels Hulstaert
 */
public class CvTermManagementDialog extends javax.swing.JDialog {
    
     /**
     * Dialog constructor
     * 
     * @param parent the parent frame
     * @param modal the modal boolean
     */
    public CvTermManagementDialog(final Frame parent, final boolean modal) {
        super(parent, modal);
        
        initComponents();
        
        cvTermTableScrollPane.getViewport().setOpaque(false);        
        setLocationRelativeTo(parent);
    }    

    public JTable getCvTermTable() {
        return cvTermTable;
    }

    public JTextField getAccessionTextField() {
        return accessionTextField;
    }

    public JButton getAddCvTermButton() {
        return addCvTermButton;
    }

    public JButton getCancelButton() {
        return cancelButton;
    }

    public JLabel getCvTermStateInfoLabel() {
        return cvTermStateInfoLabel;
    }

    public JButton getDeleteCvTermButton() {
        return deleteCvTermButton;
    }

    public JTextField getNameTextField() {
        return nameTextField;
    }

    public JTextField getOntologyLabelTextField() {
        return ontologyLabelTextField;
    }

    public JTextField getOntologyTextField() {
        return ontologyTextField;
    }

    public JButton getSaveOrUpdateButton() {
        return saveOrUpdateButton;
    }    

    public JTextArea getDefinitionTextArea() {
        return definitionTextArea;
    }

    public JButton getEditUsingOlsCvTermButton() {
        return editUsingOlsCvTermButton;
    }        

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cvTermCrudPanel = new javax.swing.JPanel();
        cvTermOverviewPanel = new javax.swing.JPanel();
        addCvTermButton = new javax.swing.JButton();
        deleteCvTermButton = new javax.swing.JButton();
        cvTermTableScrollPane = new javax.swing.JScrollPane();
        cvTermTable = new javax.swing.JTable();
        cvTermDetailPanel = new javax.swing.JPanel();
        ontologyLabel = new javax.swing.JLabel();
        ontologyTextField = new javax.swing.JTextField();
        ontologyLabelLabel = new javax.swing.JLabel();
        ontologyLabelTextField = new javax.swing.JTextField();
        accessionLabel = new javax.swing.JLabel();
        nameLabel = new javax.swing.JLabel();
        accessionTextField = new javax.swing.JTextField();
        nameTextField = new javax.swing.JTextField();
        saveOrUpdateButton = new javax.swing.JButton();
        cvTermStateInfoLabel = new javax.swing.JLabel();
        definitionLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        definitionTextArea = new javax.swing.JTextArea();
        editUsingOlsCvTermButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("CV term management");

        cvTermCrudPanel.setBackground(new java.awt.Color(255, 255, 255));

        cvTermOverviewPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Overview"));
        cvTermOverviewPanel.setOpaque(false);
        cvTermOverviewPanel.setPreferredSize(new java.awt.Dimension(100, 100));

        addCvTermButton.setText("add...");
        addCvTermButton.setToolTipText("add a CV term with the OLS dialog");
        addCvTermButton.setMaximumSize(new java.awt.Dimension(80, 25));
        addCvTermButton.setMinimumSize(new java.awt.Dimension(80, 25));
        addCvTermButton.setPreferredSize(new java.awt.Dimension(80, 25));

        deleteCvTermButton.setText("delete");
        deleteCvTermButton.setMaximumSize(new java.awt.Dimension(80, 25));
        deleteCvTermButton.setMinimumSize(new java.awt.Dimension(80, 25));
        deleteCvTermButton.setPreferredSize(new java.awt.Dimension(80, 25));

        cvTermTableScrollPane.setOpaque(false);

        cvTermTable.setModel(new javax.swing.table.DefaultTableModel(
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
        cvTermTable.setOpaque(false);
        cvTermTableScrollPane.setViewportView(cvTermTable);

        javax.swing.GroupLayout cvTermOverviewPanelLayout = new javax.swing.GroupLayout(cvTermOverviewPanel);
        cvTermOverviewPanel.setLayout(cvTermOverviewPanelLayout);
        cvTermOverviewPanelLayout.setHorizontalGroup(
            cvTermOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cvTermOverviewPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(cvTermOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, cvTermOverviewPanelLayout.createSequentialGroup()
                        .addGap(0, 336, Short.MAX_VALUE)
                        .addComponent(addCvTermButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteCvTermButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(cvTermTableScrollPane))
                .addContainerGap())
        );
        cvTermOverviewPanelLayout.setVerticalGroup(
            cvTermOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cvTermOverviewPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cvTermTableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(cvTermOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addCvTermButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(deleteCvTermButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        cvTermDetailPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Detail"));
        cvTermDetailPanel.setOpaque(false);
        cvTermDetailPanel.setPreferredSize(new java.awt.Dimension(100, 100));

        ontologyLabel.setText("Ontology Name");

        ontologyTextField.setEnabled(false);

        ontologyLabelLabel.setText("Ontology Label");

        ontologyLabelTextField.setEnabled(false);

        accessionLabel.setText("Accession");

        nameLabel.setText("Name");

        accessionTextField.setEnabled(false);

        nameTextField.setEnabled(false);

        saveOrUpdateButton.setText("save");
        saveOrUpdateButton.setMaximumSize(new java.awt.Dimension(80, 25));
        saveOrUpdateButton.setMinimumSize(new java.awt.Dimension(80, 25));
        saveOrUpdateButton.setPreferredSize(new java.awt.Dimension(80, 25));

        cvTermStateInfoLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        cvTermStateInfoLabel.setForeground(new java.awt.Color(255, 0, 0));
        cvTermStateInfoLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        cvTermStateInfoLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        cvTermStateInfoLabel.setMaximumSize(new java.awt.Dimension(100, 20));
        cvTermStateInfoLabel.setMinimumSize(new java.awt.Dimension(100, 20));
        cvTermStateInfoLabel.setPreferredSize(new java.awt.Dimension(100, 20));

        definitionLabel.setText("Definition");

        definitionTextArea.setEditable(false);
        definitionTextArea.setColumns(20);
        definitionTextArea.setLineWrap(true);
        definitionTextArea.setRows(5);
        definitionTextArea.setEnabled(false);
        jScrollPane1.setViewportView(definitionTextArea);

        editUsingOlsCvTermButton.setText("edit...");
        editUsingOlsCvTermButton.setToolTipText("add a CV term with the OLS dialog");
        editUsingOlsCvTermButton.setMaximumSize(new java.awt.Dimension(80, 25));
        editUsingOlsCvTermButton.setMinimumSize(new java.awt.Dimension(80, 25));
        editUsingOlsCvTermButton.setPreferredSize(new java.awt.Dimension(80, 25));

        javax.swing.GroupLayout cvTermDetailPanelLayout = new javax.swing.GroupLayout(cvTermDetailPanel);
        cvTermDetailPanel.setLayout(cvTermDetailPanelLayout);
        cvTermDetailPanelLayout.setHorizontalGroup(
            cvTermDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cvTermDetailPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(cvTermDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(cvTermDetailPanelLayout.createSequentialGroup()
                        .addComponent(cvTermStateInfoLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(editUsingOlsCvTermButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(saveOrUpdateButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(cvTermDetailPanelLayout.createSequentialGroup()
                        .addGroup(cvTermDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(nameLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(accessionLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(ontologyLabelLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(ontologyLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE)
                            .addComponent(definitionLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(cvTermDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(accessionTextField)
                            .addComponent(nameTextField)
                            .addComponent(ontologyTextField)
                            .addComponent(ontologyLabelTextField))))
                .addContainerGap())
        );
        cvTermDetailPanelLayout.setVerticalGroup(
            cvTermDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cvTermDetailPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(cvTermDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ontologyTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ontologyLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(cvTermDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ontologyLabelTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ontologyLabelLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(cvTermDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(accessionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(accessionLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(cvTermDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nameLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(cvTermDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE)
                    .addGroup(cvTermDetailPanelLayout.createSequentialGroup()
                        .addComponent(definitionLabel)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(cvTermDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(cvTermDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(saveOrUpdateButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(editUsingOlsCvTermButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(cvTermStateInfoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        cancelButton.setText("cancel");
        cancelButton.setMaximumSize(new java.awt.Dimension(80, 25));
        cancelButton.setMinimumSize(new java.awt.Dimension(80, 25));
        cancelButton.setPreferredSize(new java.awt.Dimension(80, 25));

        javax.swing.GroupLayout cvTermCrudPanelLayout = new javax.swing.GroupLayout(cvTermCrudPanel);
        cvTermCrudPanel.setLayout(cvTermCrudPanelLayout);
        cvTermCrudPanelLayout.setHorizontalGroup(
            cvTermCrudPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cvTermCrudPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(cvTermCrudPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, cvTermCrudPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(cvTermOverviewPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 534, Short.MAX_VALUE)
                    .addComponent(cvTermDetailPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 534, Short.MAX_VALUE))
                .addContainerGap())
        );
        cvTermCrudPanelLayout.setVerticalGroup(
            cvTermCrudPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cvTermCrudPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cvTermOverviewPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cvTermDetailPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(cvTermCrudPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(cvTermCrudPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel accessionLabel;
    private javax.swing.JTextField accessionTextField;
    private javax.swing.JButton addCvTermButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel cvTermCrudPanel;
    private javax.swing.JPanel cvTermDetailPanel;
    private javax.swing.JPanel cvTermOverviewPanel;
    private javax.swing.JLabel cvTermStateInfoLabel;
    private javax.swing.JTable cvTermTable;
    private javax.swing.JScrollPane cvTermTableScrollPane;
    private javax.swing.JLabel definitionLabel;
    private javax.swing.JTextArea definitionTextArea;
    private javax.swing.JButton deleteCvTermButton;
    private javax.swing.JButton editUsingOlsCvTermButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JLabel ontologyLabel;
    private javax.swing.JLabel ontologyLabelLabel;
    private javax.swing.JTextField ontologyLabelTextField;
    private javax.swing.JTextField ontologyTextField;
    private javax.swing.JButton saveOrUpdateButton;
    // End of variables declaration//GEN-END:variables
    
}
