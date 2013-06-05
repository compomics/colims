package com.compomics.colims.client.view.admin;

/**
 *
 * @author niels
 */
public class InstrumentCvDialog extends javax.swing.JDialog {

    /**
     * Creates new form InstrumentCvDialog
     */
    public InstrumentCvDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        instrumentCvTabbedPane = new javax.swing.JTabbedPane();
        sourceCvCrudPanel = new javax.swing.JPanel();
        sourceCvTermOverviewPanel = new javax.swing.JPanel();
        sourceCvTermListScrollPane = new javax.swing.JScrollPane();
        sourceCvTermList = new javax.swing.JList();
        addSourceCvTermButton = new javax.swing.JButton();
        deleteSourceCvTermButton = new javax.swing.JButton();
        sourceCvTermDetailPanel = new javax.swing.JPanel();
        ontologyLabelLabel = new javax.swing.JLabel();
        lastNameLabel = new javax.swing.JLabel();
        emailLabel = new javax.swing.JLabel();
        passwordLabel = new javax.swing.JLabel();
        labelTextField = new javax.swing.JTextField();
        lastNameTextField = new javax.swing.JTextField();
        emailTextField = new javax.swing.JTextField();
        passwordTextField = new javax.swing.JPasswordField();
        ontologyLabel = new javax.swing.JLabel();
        ontologyTextField = new javax.swing.JTextField();
        userSaveOrUpdateButton = new javax.swing.JButton();
        userStateInfoLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        sourceCvTermOverviewPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Overview"));
        sourceCvTermOverviewPanel.setOpaque(false);
        sourceCvTermOverviewPanel.setPreferredSize(new java.awt.Dimension(100, 100));

        sourceCvTermListScrollPane.setViewportView(sourceCvTermList);

        addSourceCvTermButton.setText("add");
        addSourceCvTermButton.setMaximumSize(new java.awt.Dimension(80, 25));
        addSourceCvTermButton.setMinimumSize(new java.awt.Dimension(80, 25));
        addSourceCvTermButton.setPreferredSize(new java.awt.Dimension(80, 25));

        deleteSourceCvTermButton.setText("delete");
        deleteSourceCvTermButton.setMaximumSize(new java.awt.Dimension(80, 25));
        deleteSourceCvTermButton.setMinimumSize(new java.awt.Dimension(80, 25));
        deleteSourceCvTermButton.setPreferredSize(new java.awt.Dimension(80, 25));

        javax.swing.GroupLayout sourceCvTermOverviewPanelLayout = new javax.swing.GroupLayout(sourceCvTermOverviewPanel);
        sourceCvTermOverviewPanel.setLayout(sourceCvTermOverviewPanelLayout);
        sourceCvTermOverviewPanelLayout.setHorizontalGroup(
            sourceCvTermOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sourceCvTermOverviewPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(sourceCvTermListScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(sourceCvTermOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(addSourceCvTermButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(deleteSourceCvTermButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        sourceCvTermOverviewPanelLayout.setVerticalGroup(
            sourceCvTermOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sourceCvTermOverviewPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(sourceCvTermOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(sourceCvTermOverviewPanelLayout.createSequentialGroup()
                        .addGap(0, 101, Short.MAX_VALUE)
                        .addComponent(addSourceCvTermButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(deleteSourceCvTermButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(sourceCvTermListScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        sourceCvTermDetailPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Detail"));
        sourceCvTermDetailPanel.setOpaque(false);
        sourceCvTermDetailPanel.setPreferredSize(new java.awt.Dimension(100, 100));

        ontologyLabelLabel.setText("label");

        lastNameLabel.setText("accession");

        emailLabel.setText("email");

        passwordLabel.setText("password");

        ontologyLabel.setText("ontology");
        ontologyLabel.setPreferredSize(new java.awt.Dimension(48, 14));

        userSaveOrUpdateButton.setText("save");
        userSaveOrUpdateButton.setMaximumSize(new java.awt.Dimension(80, 25));
        userSaveOrUpdateButton.setMinimumSize(new java.awt.Dimension(80, 25));
        userSaveOrUpdateButton.setPreferredSize(new java.awt.Dimension(80, 25));

        userStateInfoLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        userStateInfoLabel.setForeground(new java.awt.Color(255, 0, 0));
        userStateInfoLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        userStateInfoLabel.setMaximumSize(new java.awt.Dimension(100, 20));
        userStateInfoLabel.setMinimumSize(new java.awt.Dimension(100, 20));
        userStateInfoLabel.setPreferredSize(new java.awt.Dimension(100, 20));

        javax.swing.GroupLayout sourceCvTermDetailPanelLayout = new javax.swing.GroupLayout(sourceCvTermDetailPanel);
        sourceCvTermDetailPanel.setLayout(sourceCvTermDetailPanelLayout);
        sourceCvTermDetailPanelLayout.setHorizontalGroup(
            sourceCvTermDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sourceCvTermDetailPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(sourceCvTermDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, sourceCvTermDetailPanelLayout.createSequentialGroup()
                        .addComponent(passwordLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(passwordTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(sourceCvTermDetailPanelLayout.createSequentialGroup()
                        .addComponent(userStateInfoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 376, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(sourceCvTermDetailPanelLayout.createSequentialGroup()
                        .addGroup(sourceCvTermDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(sourceCvTermDetailPanelLayout.createSequentialGroup()
                                .addComponent(ontologyLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ontologyTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(sourceCvTermDetailPanelLayout.createSequentialGroup()
                                .addComponent(ontologyLabelLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(labelTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(sourceCvTermDetailPanelLayout.createSequentialGroup()
                                .addComponent(lastNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lastNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(sourceCvTermDetailPanelLayout.createSequentialGroup()
                                .addComponent(emailLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emailTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))))
            .addGroup(sourceCvTermDetailPanelLayout.createSequentialGroup()
                .addGap(307, 307, 307)
                .addComponent(userSaveOrUpdateButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        sourceCvTermDetailPanelLayout.setVerticalGroup(
            sourceCvTermDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sourceCvTermDetailPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(userStateInfoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(sourceCvTermDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ontologyLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ontologyTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(sourceCvTermDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ontologyLabelLabel)
                    .addComponent(labelTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(sourceCvTermDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lastNameLabel)
                    .addComponent(lastNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(sourceCvTermDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(emailLabel)
                    .addComponent(emailTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(sourceCvTermDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(passwordLabel)
                    .addComponent(passwordTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(userSaveOrUpdateButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout sourceCvCrudPanelLayout = new javax.swing.GroupLayout(sourceCvCrudPanel);
        sourceCvCrudPanel.setLayout(sourceCvCrudPanelLayout);
        sourceCvCrudPanelLayout.setHorizontalGroup(
            sourceCvCrudPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sourceCvTermOverviewPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 409, Short.MAX_VALUE)
            .addComponent(sourceCvTermDetailPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 409, Short.MAX_VALUE)
        );
        sourceCvCrudPanelLayout.setVerticalGroup(
            sourceCvCrudPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, sourceCvCrudPanelLayout.createSequentialGroup()
                .addComponent(sourceCvTermOverviewPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sourceCvTermDetailPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        instrumentCvTabbedPane.addTab("Source", sourceCvCrudPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 408, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(instrumentCvTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 408, Short.MAX_VALUE)
                    .addGap(0, 0, 0)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 502, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(instrumentCvTabbedPane))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addSourceCvTermButton;
    private javax.swing.JButton deleteSourceCvTermButton;
    private javax.swing.JLabel emailLabel;
    private javax.swing.JTextField emailTextField;
    private javax.swing.JTabbedPane instrumentCvTabbedPane;
    private javax.swing.JTextField labelTextField;
    private javax.swing.JLabel lastNameLabel;
    private javax.swing.JTextField lastNameTextField;
    private javax.swing.JLabel ontologyLabel;
    private javax.swing.JLabel ontologyLabelLabel;
    private javax.swing.JTextField ontologyTextField;
    private javax.swing.JLabel passwordLabel;
    private javax.swing.JPasswordField passwordTextField;
    private javax.swing.JPanel sourceCvCrudPanel;
    private javax.swing.JPanel sourceCvTermDetailPanel;
    private javax.swing.JList sourceCvTermList;
    private javax.swing.JScrollPane sourceCvTermListScrollPane;
    private javax.swing.JPanel sourceCvTermOverviewPanel;
    private javax.swing.JButton userSaveOrUpdateButton;
    private javax.swing.JLabel userStateInfoLabel;
    // End of variables declaration//GEN-END:variables
}
