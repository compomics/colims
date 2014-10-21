package com.compomics.colims.client.view;

import java.awt.Desktop;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.log4j.Logger;

/**
 * The Colims help dialog.
 *
 * @author Niels Hulstaert
 */
public class MainHelpDialog extends javax.swing.JDialog {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(MainHelpDialog.class);
    /**
     * The Colims google code page url.
     */
    private URI uri;

    /**
     * Dialog constructor.
     *
     * @param parent the parent dialog
     * @param modal is the dialog modal
     */
    public MainHelpDialog(final Frame parent, final boolean modal) {
        super(parent, modal);

        try {
            uri = new URI("https://code.google.com/p/colims/");
        } catch (URISyntaxException ex) {
            LOGGER.error(ex);
        }

        initComponents();

        helpMessageScrollPane.getViewport().setOpaque(false);

        init();
    }

    /**
     * Init the dialog.
     */
    private void init() {
        uriButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().browse(uri);
                    } catch (IOException ex) {
                        LOGGER.error(ex);
                    }
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                MainHelpDialog.this.dispose();
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        parentPanel = new javax.swing.JPanel();
        cancelButton = new javax.swing.JButton();
        helpPanel = new javax.swing.JPanel();
        helpMessageScrollPane = new javax.swing.JScrollPane();
        helpMessageTextArea = new javax.swing.JTextArea();
        uriButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Colims help");

        parentPanel.setBackground(new java.awt.Color(255, 255, 255));

        cancelButton.setText("cancel");
        cancelButton.setMaximumSize(new java.awt.Dimension(80, 25));
        cancelButton.setMinimumSize(new java.awt.Dimension(80, 25));
        cancelButton.setPreferredSize(new java.awt.Dimension(80, 25));

        helpPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        helpPanel.setOpaque(false);

        helpMessageScrollPane.setBorder(null);
        helpMessageScrollPane.setOpaque(false);

        helpMessageTextArea.setEditable(false);
        helpMessageTextArea.setBackground(new java.awt.Color(240, 240, 240));
        helpMessageTextArea.setColumns(20);
        helpMessageTextArea.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        helpMessageTextArea.setRows(2);
        helpMessageTextArea.setText("For help or issues, please go to\nthe colims google code page.");
        helpMessageTextArea.setAutoscrolls(false);
        helpMessageTextArea.setBorder(null);
        helpMessageTextArea.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        helpMessageTextArea.setEnabled(false);
        helpMessageTextArea.setOpaque(false);
        helpMessageScrollPane.setViewportView(helpMessageTextArea);

        uriButton.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        uriButton.setText("<HTML>Click this <U>link</U>.</HTML>");
        uriButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        uriButton.setBorderPainted(false);
        uriButton.setContentAreaFilled(false);
        uriButton.setFocusPainted(false);
        uriButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        uriButton.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        javax.swing.GroupLayout helpPanelLayout = new javax.swing.GroupLayout(helpPanel);
        helpPanel.setLayout(helpPanelLayout);
        helpPanelLayout.setHorizontalGroup(
            helpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(helpPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(helpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(uriButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(helpMessageScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        helpPanelLayout.setVerticalGroup(
            helpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(helpPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(helpMessageScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(uriButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout parentPanelLayout = new javax.swing.GroupLayout(parentPanel);
        parentPanel.setLayout(parentPanelLayout);
        parentPanelLayout.setHorizontalGroup(
            parentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(parentPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(parentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(helpPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        parentPanelLayout.setVerticalGroup(
            parentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(parentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(helpPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(parentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(parentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JScrollPane helpMessageScrollPane;
    private javax.swing.JTextArea helpMessageTextArea;
    private javax.swing.JPanel helpPanel;
    private javax.swing.JPanel parentPanel;
    private javax.swing.JButton uriButton;
    // End of variables declaration//GEN-END:variables
}
