package com.compomics.colims.client.view;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 *
 * @author Niels Hulstaert Hulstaert
 */
public class MainFrame extends javax.swing.JFrame {

    public static final String MANAGMENT_TAB_TITLE = "Management";
    public static final String OVERVIEW_TAB_TITLE = "Overview";
    public static final String TASKS_TAB_TITLE = "Tasks";
    public static final String PROTEINS_TAB_TITLE = "Proteins";

    /**
     * No-arg constructor.
     */
    public MainFrame() {
        initComponents();
    }

    public JPanel getProjectsManagementParentPanel() {
        return projectsManagementParentPanel;
    }

    public JPanel getTasksManagementParentPanel() {
        return tasksManagementParentPanel;
    }

    public JPanel getUserQueryParentPanel() {
        return userQueryParentPanel;
    }

    public JPanel getProteinsParentPanel() { return proteinsParentPanel; }

    public JMenuItem getUserManagementMenuItem() {
        return userManagementMenuItem;
    }

    public JTabbedPane getMainTabbedPane() {
        return mainTabbedPane;
    }

    public JMenuItem getHelpMenuItem() {
        return helpMenuItem;
    }

    public JMenuItem getProjectsManagementMenuItem() {
        return projectsManagementMenuItem;
    }

    public JMenuItem getProjectsOverviewMenuItem() {
        return projectsOverviewMenuItem;
    }

    public JMenu getEditMenu() {
        return editMenu;
    }

    public JMenuItem getInstrumentManagementMenuItem() {
        return instrumentManagementMenuItem;
    }

    public JMenuItem getMaterialManagementMenuItem() {
        return materialManagementMenuItem;
    }

    public JMenuItem getProtocolManagementMenuItem() {
        return protocolManagementMenuItem;
    }

    public JMenu getAdminMenu() {
        return adminMenu;
    }

    public JMenuItem getExitMenuItem() {
        return exitMenuItem;
    }

    public JMenu getFileMenu() {
        return fileMenu;
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
        projectsManagementParentPanel = new javax.swing.JPanel();
        proteinsParentPanel = new javax.swing.JPanel();
        userQueryParentPanel = new javax.swing.JPanel();
        tasksManagementParentPanel = new javax.swing.JPanel();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        exitMenuItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        adminMenu = new javax.swing.JMenu();
        userManagementMenuItem = new javax.swing.JMenuItem();
        instrumentManagementMenuItem = new javax.swing.JMenuItem();
        materialManagementMenuItem = new javax.swing.JMenuItem();
        protocolManagementMenuItem = new javax.swing.JMenuItem();
        viewMenu = new javax.swing.JMenu();
        projectsManagementMenuItem = new javax.swing.JMenuItem();
        projectsOverviewMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        helpMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(700, 700));

        mainTabbedPane.setTabPlacement(javax.swing.JTabbedPane.RIGHT);
        mainTabbedPane.setName("mainTabbedPane"); // NOI18N
        mainTabbedPane.setOpaque(true);

        projectsManagementParentPanel.setBackground(new java.awt.Color(255, 255, 255));
        projectsManagementParentPanel.setLayout(new java.awt.GridBagLayout());
        mainTabbedPane.addTab("Management", projectsManagementParentPanel);
        projectsManagementParentPanel.getAccessibleContext().setAccessibleName("");

        proteinsParentPanel.setLayout(new java.awt.GridBagLayout());
        mainTabbedPane.addTab("Proteins", proteinsParentPanel);

        userQueryParentPanel.setLayout(new java.awt.GridBagLayout());
        mainTabbedPane.addTab("Queries", userQueryParentPanel);

        tasksManagementParentPanel.setLayout(new java.awt.GridBagLayout());
        mainTabbedPane.addTab("Tasks", tasksManagementParentPanel);
        tasksManagementParentPanel.getAccessibleContext().setAccessibleName("");

        fileMenu.setText("File");

        exitMenuItem.setText("Exit");
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        editMenu.setText("Edit");

        adminMenu.setText("Admin");

        userManagementMenuItem.setText("Users...");
        adminMenu.add(userManagementMenuItem);

        instrumentManagementMenuItem.setText("Instruments...");
        adminMenu.add(instrumentManagementMenuItem);

        materialManagementMenuItem.setText("Materials...");
        adminMenu.add(materialManagementMenuItem);

        protocolManagementMenuItem.setText("Protocols...");
        adminMenu.add(protocolManagementMenuItem);

        editMenu.add(adminMenu);

        menuBar.add(editMenu);

        viewMenu.setText("View");

        projectsManagementMenuItem.setText("Projects management");
        viewMenu.add(projectsManagementMenuItem);

        projectsOverviewMenuItem.setText("Projects overview");
        viewMenu.add(projectsOverviewMenuItem);

        menuBar.add(viewMenu);

        helpMenu.setText("Help");

        helpMenuItem.setText("Help...");
        helpMenu.add(helpMenuItem);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 680, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu adminMenu;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JMenuItem helpMenuItem;
    private javax.swing.JMenuItem instrumentManagementMenuItem;
    private javax.swing.JTabbedPane mainTabbedPane;
    private javax.swing.JMenuItem materialManagementMenuItem;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem projectsManagementMenuItem;
    private javax.swing.JPanel projectsManagementParentPanel;
    private javax.swing.JMenuItem projectsOverviewMenuItem;
    private javax.swing.JPanel proteinsParentPanel;
    private javax.swing.JMenuItem protocolManagementMenuItem;
    private javax.swing.JPanel tasksManagementParentPanel;
    private javax.swing.JMenuItem userManagementMenuItem;
    private javax.swing.JPanel userQueryParentPanel;
    private javax.swing.JMenu viewMenu;
    // End of variables declaration//GEN-END:variables
}
