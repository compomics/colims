package com.compomics.colims.client.controller;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import com.compomics.colims.client.controller.admin.CvParamManagementController;
import com.compomics.colims.client.controller.admin.InstrumentManagementController;
import com.compomics.colims.client.controller.admin.MaterialManagementController;
import com.compomics.colims.client.controller.admin.ProtocolManagementController;
import com.compomics.colims.client.controller.admin.user.UserManagementParentController;
import com.compomics.colims.client.distributed.QueueManager;
import com.compomics.colims.client.event.EntityChangeEvent;
import com.compomics.colims.client.event.admin.InstrumentChangeEvent;
import com.compomics.colims.client.event.admin.MaterialChangeEvent;
import com.compomics.colims.client.event.admin.ProtocolChangeEvent;
import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.event.message.StorageQueuesConnectionErrorMessageEvent;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.MainFrame;
import com.compomics.colims.client.view.MainHelpDialog;
import com.compomics.colims.client.view.UserLoginDialog;
import com.compomics.colims.core.authorization.PermissionException;
import com.compomics.colims.core.service.ProjectService;
import com.compomics.colims.core.service.UserService;
import com.compomics.colims.model.Project;
import com.compomics.colims.model.User;
import com.compomics.colims.repository.AuthenticationBean;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.apache.log4j.Logger;
import org.jdesktop.beansbinding.ELProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.logging.Level;
import org.springframework.core.io.ClassPathResource;

/**
 * @author Niels Hulstaert
 */
@Component("colimsController")
public class MainController implements Controllable, ActionListener {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(MainController.class);

    //model
    @Value("${colims-client.version}")
    private String version = "unknown";
    @Autowired
    private AuthenticationBean authenticationBean;
    /**
     * The project EventList that is used as table model in the project
     * management and overview tabs.
     */
    private final EventList<Project> projects = new BasicEventList<>();
    //views
    private MainFrame mainFrame;
    private UserLoginDialog userLoginDialog;
    private MainHelpDialog mainHelpDialog;
    //child controllers
    @Autowired
    private ProjectManagementController projectManagementController;
    @Autowired
    private ProjectOverviewController projectOverviewController;
    @Autowired
    private TaskManagementController taskManagementController;
    @Autowired
    private ProtocolManagementController protocolManagementController;
    @Autowired
    private UserManagementParentController userManagementParentController;
    @Autowired
    private CvParamManagementController cvParamManagementController;
    @Autowired
    private InstrumentManagementController instrumentManagementController;
    @Autowired
    private MaterialManagementController materialManagementController;
    //services
    @Autowired
    private UserService userService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private EventBus eventBus;
    @Autowired
    private QueueManager queueManager;

    /**
     * Get the main view of this controller.
     *
     * @return the MainFrame instance
     */
    public MainFrame getMainFrame() {
        return mainFrame;
    }

    /**
     * Get the list of loaded projects.
     *
     * @return the projects EventList
     */
    public EventList<Project> getProjects() {
        return projects;
    }

    @Override
    public void init() {
        //set uncaught exception handler
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(final Thread t, final Throwable e) {
                LOGGER.error(e.getMessage(), e);
                //check for permission exceptions
                if (e instanceof PermissionException) {
                    showPermissionErrorDialog(e.getMessage());
                } else if (e instanceof ArrayIndexOutOfBoundsException) {
                    showMessageDialog("OLS dialog problem", "Something went wrong in the OLS dialog, please try again.", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    showUnexpectedErrorDialog(e.getMessage());
                    System.exit(0);
                }
            }
        });

        //register to event bus
        eventBus.register(this);

        //init views
        mainFrame = new MainFrame();
        try {
            mainFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(new ClassPathResource("/icons/colims_icon.png").getURL()));
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new IllegalStateException();
        }
        mainFrame.setTitle("Colims " + version);
        userLoginDialog = new UserLoginDialog(mainFrame, true);
        mainHelpDialog = new MainHelpDialog(mainFrame, true);

        //workaround for better beansbinding logging issue
        org.jdesktop.beansbinding.util.logging.Logger.getLogger(ELProperty.class.getName()).setLevel(Level.SEVERE);

        //set close behaviour of main frame
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //find all projects
        projects.addAll(projectService.findAllWithEagerFetching());

        //init child controllers
        projectManagementController.init();
        projectOverviewController.init();
        taskManagementController.init();
        cvParamManagementController.init();

        //add panel components
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;

        mainFrame.getProjectsManagementParentPanel().add(projectManagementController.getProjectManagementPanel(), gridBagConstraints);
        mainFrame.getProjectsOverviewParentPanel().add(projectOverviewController.getProjectOverviewPanel(), gridBagConstraints);
        mainFrame.getTasksManagementParentPanel().add(taskManagementController.getTaskManagementPanel(), gridBagConstraints);

        //add action listeners
        //add menu item action listener
        mainFrame.getExitMenuItem().addActionListener(this);
        mainFrame.getProjectsManagementMenuItem().addActionListener(this);
        mainFrame.getProjectsOverviewMenuItem().addActionListener(this);
        mainFrame.getHelpMenuItem().addActionListener(this);

        userLoginDialog.getLoginButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (!userLoginDialog.getUserNameTextField().getText().isEmpty() && userLoginDialog.getUserPasswordTextField().getPassword().length != 0) {
                    onLogin();
                } else {
                    showMessageDialog("Login validation fail", "Please provide a user name and password.", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        userLoginDialog.getCancelButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                userLoginDialog.dispose();
                System.exit(0);
            }
        });

        userLoginDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent we) {
                System.exit(0);
            }
        });

        //while developing, set a default user in the AuthenticationBean
//        User currentUser = userService.findByName("admin");
//        userService.fetchAuthenticationRelations(currentUser);
//        authenticationBean.setCurrentUser(currentUser);
//        if (authenticationBean.isAdmin()) {
//            initAdminSection();
//        } else {
//            //disable admin menu
//            mainFrame.getAdminMenu().setEnabled(false);
//        }
//        showView();
        //add change listener to tabbed pane
        mainFrame.getMainTabbedPane().addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(final ChangeEvent e) {
                if (getSelectedTabTitle().equals(MainFrame.TASKS_TAB_TITLE)) {
                    //check connection to distributed queues
                    if (queueManager.testConnection()) {
                        taskManagementController.updateMonitoringTables();

                        taskManagementController.getTaskManagementPanel().setVisible(true);
                    } else {
                        eventBus.post(new StorageQueuesConnectionErrorMessageEvent(queueManager.getBrokerName(), queueManager.getBrokerUrl(), queueManager.getBrokerJmxUrl()));
                    }
                }
            }
        });
    }

    @Override
    public void showView() {
        mainFrame.setExtendedState(mainFrame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
        //show login dialog
        userLoginDialog.setLocationRelativeTo(mainFrame);
        userLoginDialog.setVisible(true);
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        String menuItemLabel = e.getActionCommand();

        if (menuItemLabel.equals(mainFrame.getExitMenuItem().getText())) {
            mainFrame.dispatchEvent(new WindowEvent(mainFrame, WindowEvent.WINDOW_CLOSING));
        } else if (menuItemLabel.equals(mainFrame.getProjectsManagementMenuItem().getText())) {
            mainFrame.getMainTabbedPane().setSelectedComponent(mainFrame.getProjectsManagementParentPanel());
        } else if (menuItemLabel.equals(mainFrame.getProjectsOverviewMenuItem().getText())) {
            mainFrame.getMainTabbedPane().setSelectedComponent(mainFrame.getProjectsOverviewParentPanel());
        } else if (menuItemLabel.equals(mainFrame.getUserManagementMenuItem().getText())) {
            userManagementParentController.showView();
        } else if (menuItemLabel.equals(mainFrame.getInstrumentManagementMenuItem().getText())) {
            instrumentManagementController.showView();
        } else if (menuItemLabel.equals(mainFrame.getMaterialManagementMenuItem().getText())) {
            materialManagementController.showView();
        } else if (menuItemLabel.equals(mainFrame.getProtocolManagementMenuItem().getText())) {
            protocolManagementController.showView();
        } else if (menuItemLabel.equals(mainFrame.getHelpMenuItem().getText())) {
            GuiUtils.centerDialogOnComponent(mainFrame, mainHelpDialog);
            mainHelpDialog.setVisible(true);
        }
    }

    /**
     * Listen to a MesaggeEvent.
     *
     * @param messageEvent the message event
     */
    @Subscribe
    public void onMessageEvent(final MessageEvent messageEvent) {
        showMessageDialog(messageEvent.getMessageTitle(), messageEvent.getMessage(), messageEvent.getMessageType());
    }

    /**
     * In case of an unexpected error, show error dialog with the error message.
     *
     * @param message the error message
     */
    public void showUnexpectedErrorDialog(final String message) {
        showMessageDialog("Unexpected error", "An unexpected error occured: "
                + System.lineSeparator() + message
                + System.lineSeparator() + "please try to rerun the application.", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * In case of a permission error, show permission error dialog with the
     * error message.
     *
     * @param message the error message
     */
    public void showPermissionErrorDialog(final String message) {
        showMessageDialog("Permission warning", "A permission warning occured: "
                + System.lineSeparator() + message
                + System.lineSeparator() + "Please contact the admin if you want to change your user permissions.", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Listen to an MaterialChangeEvent.
     *
     * @param materialChangeEvent the material change event
     */
    @Subscribe
    public void onMaterialChangeEvent(final MaterialChangeEvent materialChangeEvent) {
        updateProjects(materialChangeEvent);
    }

    /**
     * Listen to an InstrumentChangeEvent.
     *
     * @param instrumentChangeEvent the instrument change event
     */
    @Subscribe
    public void onInstrumentChangeEvent(final InstrumentChangeEvent instrumentChangeEvent) {
        updateProjects(instrumentChangeEvent);
    }

    /**
     * Listen to an ProtocolChangeEvent.
     *
     * @param protocolChangeEvent the protocol change event
     */
    @Subscribe
    public void onProtocolChangeEvent(final ProtocolChangeEvent protocolChangeEvent) {
        updateProjects(protocolChangeEvent);
    }

    /**
     * Get the selected tab title of the main tabbed pane.
     *
     * @return the selected tab title
     */
    public String getSelectedTabTitle() {
        JTabbedPane mainTabbedPane = mainFrame.getMainTabbedPane();
        return mainTabbedPane.getTitleAt(mainTabbedPane.getSelectedIndex());
    }

    /**
     * Reload all projects from the database if necessary.
     *
     * @param entityChangeEvent the entity change event
     */
    private void updateProjects(final EntityChangeEvent entityChangeEvent) {
        if (entityChangeEvent.getType().equals(EntityChangeEvent.Type.UPDATED)) {
            projects.clear();
            projects.addAll(projectService.findAllWithEagerFetching());
        }
    }

    /**
     * Check the user credentials and init the admin section if necessary. If
     * unsuccessful, show a message dialog and reset the input fields.
     */
    private void onLogin() {
        //check if a user with given user name and password is found in the db
        LOGGER.info("Login attempt with user name: " + userLoginDialog.getUserNameTextField().getText());
        User currentUser = userService.findByLoginCredentials(userLoginDialog.getUserNameTextField().getText(), String.valueOf(userLoginDialog.getUserPasswordTextField().getPassword()));
        if (currentUser != null) {
            LOGGER.info("User " + userLoginDialog.getUserNameTextField().getText() + " successfully logged in.");
            userLoginDialog.dispose();

            //set current user in authentication bean
            userService.fetchAuthenticationRelations(currentUser);
            authenticationBean.setCurrentUser(currentUser);

            if (authenticationBean.isAdmin()) {
                initAdminSection();
            } else {
                //disable admin menu
                mainFrame.getAdminMenu().setEnabled(false);
            }
        } else {
            showMessageDialog("Login fail", "No user with the given credentials could be found, please try again.", JOptionPane.ERROR_MESSAGE);
            userLoginDialog.getUserNameTextField().setText("");
            userLoginDialog.getUserPasswordTextField().setText("");
        }
    }

    /**
     * Shows a message dialog.
     *
     * @param title the dialog title
     * @param message the dialog message
     * @param messageType the dialog message type
     */
    private void showMessageDialog(final String title, final String message, final int messageType) {
        if (messageType == JOptionPane.ERROR_MESSAGE) {
            //add message to JTextArea
            JTextArea textArea = new JTextArea(message);
            //put JTextArea in JScrollPane
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(600, 200));
            scrollPane.getViewport().setOpaque(false);
            textArea.setEditable(false);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);

            JOptionPane.showMessageDialog(mainFrame.getContentPane(), scrollPane, title, messageType);
        } else {
            JOptionPane.showMessageDialog(mainFrame.getContentPane(), message, title, messageType);
        }
    }

    /**
     * Inits the admin section. This method is only called if the user is an
     * admin user.
     */
    private void initAdminSection() {
        //init admin controllers
        userManagementParentController.init();
        instrumentManagementController.init();
        materialManagementController.init();
        protocolManagementController.init();

        //add action listeners
        mainFrame.getUserManagementMenuItem().addActionListener(this);
        mainFrame.getInstrumentManagementMenuItem().addActionListener(this);
        mainFrame.getMaterialManagementMenuItem().addActionListener(this);
        mainFrame.getProtocolManagementMenuItem().addActionListener(this);
    }

}