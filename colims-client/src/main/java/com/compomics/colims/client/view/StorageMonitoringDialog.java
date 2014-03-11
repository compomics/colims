package com.compomics.colims.client.view;

import java.awt.Color;
import java.awt.Frame;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JTextArea;

/**
 *
 * @author Niels Hulstaert
 */
public class StorageMonitoringDialog extends javax.swing.JDialog {

    /**
     * Dialog constructor
     *
     * @param parent the parent frame
     * @param modal the modal boolean
     */
    public StorageMonitoringDialog(final Frame parent, final boolean modal) {
        super(parent, modal);

        initComponents();

        storageQueueScrollPane.getViewport().setOpaque(false);
        errorQueueScrollPane.getViewport().setOpaque(false);
        storedQueueScrollPane.getViewport().setOpaque(false);
        this.getContentPane().setBackground(Color.WHITE);
    }

    public JTable getStorageQueueTable() {
        return storageQueueTable;
    }

    public JTable getErrorQueueTable() {
        return errorQueueTable;
    }

    public JButton getCloseButton() {
        return closeButton;
    }

    public JButton getRefreshButton() {
        return refreshButton;
    }

    public JTable getStoredQueueTable() {
        return storedQueueTable;
    }

    public JTextArea getErrorDetailTextArea() {
        return errorDetailTextArea;
    }        

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        storageMonitoringTabbedPane = new javax.swing.JTabbedPane();
        storageQueuePanel = new javax.swing.JPanel();
        storageQueueScrollPane = new javax.swing.JScrollPane();
        storageQueueTable = new javax.swing.JTable();
        storedQueuePanel = new javax.swing.JPanel();
        storedQueueScrollPane = new javax.swing.JScrollPane();
        storedQueueTable = new javax.swing.JTable();
        exceptionQueuePanel = new javax.swing.JPanel();
        errorQueueScrollPane = new javax.swing.JScrollPane();
        errorQueueTable = new javax.swing.JTable();
        errorDetailLabel = new javax.swing.JLabel();
        errorDetailTextAreaScrollPane = new javax.swing.JScrollPane();
        errorDetailTextArea = new javax.swing.JTextArea();
        closeButton = new javax.swing.JButton();
        refreshButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("storage monitoring");

        storageQueuePanel.setOpaque(false);

        storageQueueScrollPane.setOpaque(false);

        storageQueueTable.setModel(new javax.swing.table.DefaultTableModel(
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
        storageQueueTable.setOpaque(false);
        storageQueueScrollPane.setViewportView(storageQueueTable);

        javax.swing.GroupLayout storageQueuePanelLayout = new javax.swing.GroupLayout(storageQueuePanel);
        storageQueuePanel.setLayout(storageQueuePanelLayout);
        storageQueuePanelLayout.setHorizontalGroup(
            storageQueuePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(storageQueuePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(storageQueueScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 671, Short.MAX_VALUE)
                .addContainerGap())
        );
        storageQueuePanelLayout.setVerticalGroup(
            storageQueuePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(storageQueuePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(storageQueueScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 348, Short.MAX_VALUE)
                .addContainerGap())
        );

        storageMonitoringTabbedPane.addTab("storage tasks", storageQueuePanel);

        storedQueuePanel.setOpaque(false);

        storedQueueScrollPane.setOpaque(false);

        storedQueueTable.setModel(new javax.swing.table.DefaultTableModel(
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
        storedQueueTable.setToolTipText("click on a row to see the error message");
        storedQueueTable.setOpaque(false);
        storedQueueScrollPane.setViewportView(storedQueueTable);

        javax.swing.GroupLayout storedQueuePanelLayout = new javax.swing.GroupLayout(storedQueuePanel);
        storedQueuePanel.setLayout(storedQueuePanelLayout);
        storedQueuePanelLayout.setHorizontalGroup(
            storedQueuePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(storedQueuePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(storedQueueScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 671, Short.MAX_VALUE)
                .addContainerGap())
        );
        storedQueuePanelLayout.setVerticalGroup(
            storedQueuePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(storedQueuePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(storedQueueScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 348, Short.MAX_VALUE)
                .addContainerGap())
        );

        storageMonitoringTabbedPane.addTab("stored tasks", storedQueuePanel);

        exceptionQueuePanel.setOpaque(false);

        errorQueueScrollPane.setOpaque(false);

        errorQueueTable.setModel(new javax.swing.table.DefaultTableModel(
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
        errorQueueTable.setToolTipText("select a row to see the error details");
        errorQueueTable.setOpaque(false);
        errorQueueScrollPane.setViewportView(errorQueueTable);

        errorDetailLabel.setText("error detail");

        errorDetailTextArea.setEditable(false);
        errorDetailTextArea.setColumns(20);
        errorDetailTextArea.setLineWrap(true);
        errorDetailTextArea.setRows(5);
        errorDetailTextArea.setWrapStyleWord(true);
        errorDetailTextAreaScrollPane.setViewportView(errorDetailTextArea);

        javax.swing.GroupLayout exceptionQueuePanelLayout = new javax.swing.GroupLayout(exceptionQueuePanel);
        exceptionQueuePanel.setLayout(exceptionQueuePanelLayout);
        exceptionQueuePanelLayout.setHorizontalGroup(
            exceptionQueuePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(exceptionQueuePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(exceptionQueuePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(errorQueueScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 671, Short.MAX_VALUE)
                    .addGroup(exceptionQueuePanelLayout.createSequentialGroup()
                        .addComponent(errorDetailLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(errorDetailTextAreaScrollPane))
                .addContainerGap())
        );
        exceptionQueuePanelLayout.setVerticalGroup(
            exceptionQueuePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(exceptionQueuePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(errorQueueScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(errorDetailLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(errorDetailTextAreaScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(72, Short.MAX_VALUE))
        );

        storageMonitoringTabbedPane.addTab("storage errors", exceptionQueuePanel);

        closeButton.setText("close");
        closeButton.setMaximumSize(new java.awt.Dimension(80, 25));
        closeButton.setMinimumSize(new java.awt.Dimension(80, 25));
        closeButton.setPreferredSize(new java.awt.Dimension(80, 25));

        refreshButton.setText("refresh");
        refreshButton.setMaximumSize(new java.awt.Dimension(80, 25));
        refreshButton.setMinimumSize(new java.awt.Dimension(80, 25));
        refreshButton.setPreferredSize(new java.awt.Dimension(80, 25));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(storageMonitoringTabbedPane)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(refreshButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(storageMonitoringTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 398, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(refreshButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton closeButton;
    private javax.swing.JLabel errorDetailLabel;
    private javax.swing.JTextArea errorDetailTextArea;
    private javax.swing.JScrollPane errorDetailTextAreaScrollPane;
    private javax.swing.JScrollPane errorQueueScrollPane;
    private javax.swing.JTable errorQueueTable;
    private javax.swing.JPanel exceptionQueuePanel;
    private javax.swing.JButton refreshButton;
    private javax.swing.JTabbedPane storageMonitoringTabbedPane;
    private javax.swing.JPanel storageQueuePanel;
    private javax.swing.JScrollPane storageQueueScrollPane;
    private javax.swing.JTable storageQueueTable;
    private javax.swing.JPanel storedQueuePanel;
    private javax.swing.JScrollPane storedQueueScrollPane;
    private javax.swing.JTable storedQueueTable;
    // End of variables declaration//GEN-END:variables
}
