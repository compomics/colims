package com.compomics.colims.client.view.admin;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;

/**
 *
 * @author Niels Hulstaert
 */
public class InstitutionManagementDialog extends javax.swing.JDialog {

    /**
     * Dialog constructor.
     *
     * @param parent the parent frame
     * @param modal the modal boolean
     */
    public InstitutionManagementDialog(final JDialog parent, final boolean modal) {
        super(parent, modal);

        initComponents();
    }

    public JTextField getAbbreviationTextField() {
        return abbreviationTextField;
    }

    public JButton getAddButton() {
        return addButton;
    }

    public JButton getCloseButton() {
        return closeButton;
    }

    public JTextField getCityTextField() {
        return cityTextField;
    }

    public JTextField getCountryTextField() {
        return countryTextField;
    }

    public JButton getDeleteButton() {
        return deleteButton;
    }

    public JList getInstitutionList() {
        return institutionList;
    }

    public JTextField getNameTextField() {
        return nameTextField;
    }

    public JTextField getNumberTextField() {
        return numberTextField;
    }

    public JTextField getPostalCodeTextField() {
        return postalCodeTextField;
    }

    public JButton getSaveOrUpdateButton() {
        return saveOrUpdateButton;
    }

    public JTextField getStreetTextField() {
        return streetTextField;
    }

    public JLabel getInstitutionStateInfoLabel() {
        return institutionStateInfoLabel;
    }

    public JTextField getEmailTextField() {
        return emailTextField;
    }

    public JTextField getUrlTextField() {
        return urlTextField;
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

        institutionManagementParentPanel = new javax.swing.JPanel();
        closeButton = new javax.swing.JButton();
        institutionManagementPanel = new javax.swing.JPanel();
        institutionOverviewPanel = new javax.swing.JPanel();
        institutionListScrollPane = new javax.swing.JScrollPane();
        institutionList = new javax.swing.JList();
        addButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        institutionDetailParentPanel = new javax.swing.JPanel();
        institutionDetailPanel = new javax.swing.JPanel();
        nameTextField = new javax.swing.JTextField();
        nameLabel = new javax.swing.JLabel();
        abbreviationLabel = new javax.swing.JLabel();
        abbreviationTextField = new javax.swing.JTextField();
        streetLabel = new javax.swing.JLabel();
        streetTextField = new javax.swing.JTextField();
        versionLabel = new javax.swing.JLabel();
        numberTextField = new javax.swing.JTextField();
        cityLabel = new javax.swing.JLabel();
        cityTextField = new javax.swing.JTextField();
        postalCodeLabel = new javax.swing.JLabel();
        postalCodeTextField = new javax.swing.JTextField();
        institutionStateInfoLabel = new javax.swing.JLabel();
        saveOrUpdateButton = new javax.swing.JButton();
        countryLabel = new javax.swing.JLabel();
        countryTextField = new javax.swing.JTextField();
        urlLabel = new javax.swing.JLabel();
        urlTextField = new javax.swing.JTextField();
        emailLabel = new javax.swing.JLabel();
        emailTextField = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Institution management");
        setModal(true);

        institutionManagementParentPanel.setBackground(new java.awt.Color(255, 255, 255));

        closeButton.setText("close");
        closeButton.setMaximumSize(new java.awt.Dimension(80, 25));
        closeButton.setMinimumSize(new java.awt.Dimension(80, 25));
        closeButton.setPreferredSize(new java.awt.Dimension(80, 25));

        institutionManagementPanel.setOpaque(false);
        institutionManagementPanel.setLayout(new java.awt.GridBagLayout());

        institutionOverviewPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Overview"));
        institutionOverviewPanel.setOpaque(false);
        institutionOverviewPanel.setPreferredSize(new java.awt.Dimension(100, 100));

        institutionListScrollPane.setViewportView(institutionList);

        addButton.setText("add");
        addButton.setMaximumSize(new java.awt.Dimension(80, 25));
        addButton.setMinimumSize(new java.awt.Dimension(80, 25));
        addButton.setPreferredSize(new java.awt.Dimension(80, 25));

        deleteButton.setText("delete");
        deleteButton.setMaximumSize(new java.awt.Dimension(80, 25));
        deleteButton.setMinimumSize(new java.awt.Dimension(80, 25));
        deleteButton.setPreferredSize(new java.awt.Dimension(80, 25));

        javax.swing.GroupLayout institutionOverviewPanelLayout = new javax.swing.GroupLayout(institutionOverviewPanel);
        institutionOverviewPanel.setLayout(institutionOverviewPanelLayout);
        institutionOverviewPanelLayout.setHorizontalGroup(
            institutionOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, institutionOverviewPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(institutionListScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(institutionOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(addButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(deleteButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        institutionOverviewPanelLayout.setVerticalGroup(
            institutionOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(institutionOverviewPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(institutionOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(institutionListScrollPane)
                    .addGroup(institutionOverviewPanelLayout.createSequentialGroup()
                        .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 322, Short.MAX_VALUE)))
                .addContainerGap())
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.4;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        institutionManagementPanel.add(institutionOverviewPanel, gridBagConstraints);

        institutionDetailParentPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Detail"));
        institutionDetailParentPanel.setOpaque(false);
        institutionDetailParentPanel.setPreferredSize(new java.awt.Dimension(100, 100));
        institutionDetailParentPanel.setLayout(new java.awt.GridBagLayout());

        institutionDetailPanel.setOpaque(false);
        institutionDetailPanel.setPreferredSize(new java.awt.Dimension(40, 40));

        nameLabel.setText("Name*");
        nameLabel.setPreferredSize(new java.awt.Dimension(48, 14));

        abbreviationLabel.setText("Abbreviation*");

        streetLabel.setText("Street*");

        versionLabel.setText("Number*");
        versionLabel.setPreferredSize(new java.awt.Dimension(48, 14));

        cityLabel.setText("City*");

        postalCodeLabel.setText("Postal Code");
        postalCodeLabel.setPreferredSize(new java.awt.Dimension(48, 14));

        institutionStateInfoLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        institutionStateInfoLabel.setForeground(new java.awt.Color(255, 0, 0));
        institutionStateInfoLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        institutionStateInfoLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        institutionStateInfoLabel.setMaximumSize(new java.awt.Dimension(100, 20));
        institutionStateInfoLabel.setMinimumSize(new java.awt.Dimension(100, 20));
        institutionStateInfoLabel.setPreferredSize(new java.awt.Dimension(100, 20));

        saveOrUpdateButton.setText("save");
        saveOrUpdateButton.setMaximumSize(new java.awt.Dimension(80, 25));
        saveOrUpdateButton.setMinimumSize(new java.awt.Dimension(80, 25));
        saveOrUpdateButton.setPreferredSize(new java.awt.Dimension(80, 25));

        countryLabel.setText("Country*");
        countryLabel.setPreferredSize(new java.awt.Dimension(48, 14));

        urlLabel.setText("urlLabel");
        urlLabel.setPreferredSize(new java.awt.Dimension(48, 14));

        emailLabel.setText("Email");
        emailLabel.setPreferredSize(new java.awt.Dimension(48, 14));

        javax.swing.GroupLayout institutionDetailPanelLayout = new javax.swing.GroupLayout(institutionDetailPanel);
        institutionDetailPanel.setLayout(institutionDetailPanelLayout);
        institutionDetailPanelLayout.setHorizontalGroup(
            institutionDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(institutionDetailPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(institutionDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(institutionDetailPanelLayout.createSequentialGroup()
                        .addGroup(institutionDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(nameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(abbreviationLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(institutionDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(abbreviationTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 398, Short.MAX_VALUE)
                            .addComponent(nameTextField)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, institutionDetailPanelLayout.createSequentialGroup()
                        .addComponent(institutionStateInfoLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(saveOrUpdateButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(institutionDetailPanelLayout.createSequentialGroup()
                        .addGroup(institutionDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(emailLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(urlLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(postalCodeLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE)
                            .addComponent(cityLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(versionLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(countryLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(streetLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(12, 12, 12)
                        .addGroup(institutionDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(urlTextField, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(countryTextField, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(postalCodeTextField, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(cityTextField, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(numberTextField, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(streetTextField)
                            .addComponent(emailTextField))))
                .addContainerGap())
        );
        institutionDetailPanelLayout.setVerticalGroup(
            institutionDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(institutionDetailPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(institutionDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(institutionDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(abbreviationLabel)
                    .addComponent(abbreviationTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(institutionDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(streetLabel)
                    .addComponent(streetTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(institutionDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(numberTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(versionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(institutionDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cityTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cityLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(institutionDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(postalCodeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(postalCodeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(institutionDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(countryLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(countryTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(institutionDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(urlLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(urlTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(institutionDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(emailLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(emailTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(institutionDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(saveOrUpdateButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(institutionStateInfoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        institutionDetailParentPanel.add(institutionDetailPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.6;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        institutionManagementPanel.add(institutionDetailParentPanel, gridBagConstraints);

        javax.swing.GroupLayout institutionManagementParentPanelLayout = new javax.swing.GroupLayout(institutionManagementParentPanel);
        institutionManagementParentPanel.setLayout(institutionManagementParentPanelLayout);
        institutionManagementParentPanelLayout.setHorizontalGroup(
            institutionManagementParentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, institutionManagementParentPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(institutionManagementPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 939, Short.MAX_VALUE)
        );
        institutionManagementParentPanelLayout.setVerticalGroup(
            institutionManagementParentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(institutionManagementParentPanelLayout.createSequentialGroup()
                .addComponent(institutionManagementPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 434, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(institutionManagementParentPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(institutionManagementParentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel abbreviationLabel;
    private javax.swing.JTextField abbreviationTextField;
    private javax.swing.JButton addButton;
    private javax.swing.JLabel cityLabel;
    private javax.swing.JTextField cityTextField;
    private javax.swing.JButton closeButton;
    private javax.swing.JLabel countryLabel;
    private javax.swing.JTextField countryTextField;
    private javax.swing.JButton deleteButton;
    private javax.swing.JLabel emailLabel;
    private javax.swing.JTextField emailTextField;
    private javax.swing.JPanel institutionDetailPanel;
    private javax.swing.JPanel institutionDetailParentPanel;
    private javax.swing.JList institutionList;
    private javax.swing.JScrollPane institutionListScrollPane;
    private javax.swing.JPanel institutionManagementPanel;
    private javax.swing.JPanel institutionManagementParentPanel;
    private javax.swing.JPanel institutionOverviewPanel;
    private javax.swing.JLabel institutionStateInfoLabel;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JTextField numberTextField;
    private javax.swing.JLabel postalCodeLabel;
    private javax.swing.JTextField postalCodeTextField;
    private javax.swing.JButton saveOrUpdateButton;
    private javax.swing.JLabel streetLabel;
    private javax.swing.JTextField streetTextField;
    private javax.swing.JLabel urlLabel;
    private javax.swing.JTextField urlTextField;
    private javax.swing.JLabel versionLabel;
    // End of variables declaration//GEN-END:variables
}
