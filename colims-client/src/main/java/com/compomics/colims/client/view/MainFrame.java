package com.compomics.colims.client.view;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;

/**
 *
 * @author Niels Hulstaert Hulstaert
 */
public class MainFrame extends javax.swing.JFrame {

    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
        initComponents();
    }

    public JPanel getHomeParentPanel() {
        return homeParentPanel;
    }

    public JPanel getProjectSetupParentPanel() {
        return projectSetupParentPanel;
    }

    public JMenuItem getUserManagementMenuItem() {
        return userManagementMenuItem;
    }

    public JTabbedPane getMainTabbedPane() {
        return mainTabbedPane;
    }

    public JMenu getHelpMenu() {
        return helpMenu;
    }

    public JMenuItem getHomeMenuItem() {
        return homeMenuItem;
    }

    public JMenu getAdminMenu() {
        return adminMenu;
    }
      
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainTabbedPane = new javax.swing.JTabbedPane();
        homeParentPanel = new javax.swing.JPanel();
        projectSetupParentPanel = new javax.swing.JPanel();
        menuBar = new javax.swing.JMenuBar();
        viewMenu = new javax.swing.JMenu();
        homeMenuItem = new javax.swing.JMenuItem();
        adminMenu = new javax.swing.JMenu();
        userManagementMenuItem = new javax.swing.JMenuItem();
        cvMenu = new javax.swing.JMenu();
        instrumentCvMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(700, 700));
        setPreferredSize(new java.awt.Dimension(700, 700));

        mainTabbedPane.setName("mainTabbedPane"); // NOI18N

        homeParentPanel.setLayout(new java.awt.GridBagLayout());
        mainTabbedPane.addTab("Home", homeParentPanel);

        projectSetupParentPanel.setLayout(new java.awt.GridBagLayout());
        mainTabbedPane.addTab("Project setup", projectSetupParentPanel);

        viewMenu.setText("View");

        homeMenuItem.setText("home");
        viewMenu.add(homeMenuItem);

        menuBar.add(viewMenu);

        adminMenu.setText("Admin");

        userManagementMenuItem.setText("user management");
        adminMenu.add(userManagementMenuItem);

        cvMenu.setText("controlled vocabulary");

        instrumentCvMenuItem.setText("instrument");
        cvMenu.add(instrumentCvMenuItem);

        adminMenu.add(cvMenu);

        menuBar.add(adminMenu);

        helpMenu.setText("Help");
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 421, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 328, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu adminMenu;
    private javax.swing.JMenu cvMenu;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JMenuItem homeMenuItem;
    private javax.swing.JPanel homeParentPanel;
    private javax.swing.JMenuItem instrumentCvMenuItem;
    private javax.swing.JTabbedPane mainTabbedPane;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JPanel projectSetupParentPanel;
    private javax.swing.JMenuItem userManagementMenuItem;
    private javax.swing.JMenu viewMenu;
    // End of variables declaration//GEN-END:variables
}
