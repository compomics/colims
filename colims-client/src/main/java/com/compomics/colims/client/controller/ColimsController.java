package com.compomics.colims.client.controller;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import com.compomics.colims.client.controller.admin.user.UserManagementController;
import com.compomics.colims.client.controller.admin.CvTermManagementController;
import com.compomics.colims.client.controller.admin.InstrumentManagementController;
import com.compomics.colims.client.controller.admin.MaterialManagementController;
import com.compomics.colims.client.controller.admin.ProtocolManagementController;
import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.LoginDialog;
import com.compomics.colims.client.view.ColimsFrame;
import com.compomics.colims.client.view.MainHelpDialog;
import com.compomics.colims.core.authorization.PermissionException;
import com.compomics.colims.core.service.ProjectService;
import com.compomics.colims.model.User;
import com.compomics.colims.core.service.UserService;
import com.compomics.colims.model.Project;
import com.compomics.colims.repository.AuthenticationBean;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Level;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import org.apache.log4j.Logger;
import org.jdesktop.beansbinding.ELProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("colimsController")
public class ColimsController implements Controllable, ActionListener {

    private static final Logger LOGGER = Logger.getLogger(ColimsController.class);
    //model
    @Value("${colims-client.version}")
    private String version = "unknown";
    @Autowired
    private AuthenticationBean authenticationBean;
    /**
     * The project EventList that is used as table model in the project
     * management and overview tabs.
     */
    private EventList<Project> projects = new BasicEventList<>();
    //views
    private ColimsFrame colimsFrame;
    private LoginDialog loginDialog;
    private MainHelpDialog mainHelpDialog;
    //child controllers    
    @Autowired
    private ProjectManagementController projectManagementController;
    @Autowired
    private ProjectOverviewController projectOverviewController;
    @Autowired
    private StorageMonitoringController storageMonitoringController;
    @Autowired
    private ProtocolManagementController protocolManagementController;
    @Autowired
    private AnalyticalRunSetupController analyticalRunSetupController;
    @Autowired
    private UserManagementController userManagementController;
    @Autowired
    private CvTermManagementController cvTermManagementController;
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
//    @Autowired
//    private LocalSessionFactoryBean sessionFactory;

    public ColimsFrame getColimsFrame() {
        return colimsFrame;
    }

    public EventList<Project> getProjects() {
        return projects;
    }

    /**
     * Controller init method.
     */
    @Override
    public void init() {
//        SchemaExport schemaExport = new SchemaExport(sessionFactory.getConfiguration());
//        schemaExport.setOutputFile("C:\\Users\\niels\\Desktop\\testing.txt");
//        schemaExport.setFormat(true);
//        schemaExport.setDelimiter(";");
//        schemaExport.execute(true, false, false, true);

        //set uncaught exception handler
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                LOGGER.error(e.getMessage(), e);
                //check for permission exceptions
                if (e instanceof PermissionException) {
                    showPermissionErrorDialog(e.getMessage());
                } else {
                    showUnexpectedErrorDialog(e.getMessage());
                }
            }
        });

        //register to event bus
        eventBus.register(this);

        //init views       
        colimsFrame = new ColimsFrame();
        colimsFrame.setTitle("Colims " + version);
        loginDialog = new LoginDialog(colimsFrame, true);
        mainHelpDialog = new MainHelpDialog(colimsFrame, true);

        //workaround for better beansbinding logging issue
        org.jdesktop.beansbinding.util.logging.Logger.getLogger(ELProperty.class.getName()).setLevel(Level.SEVERE);

        //find all projects
        projects.addAll(projectService.findAllWithEagerFetching());

        //init child controllers
        projectManagementController.init();
        projectOverviewController.init();
        storageMonitoringController.init();
        analyticalRunSetupController.init();
        cvTermManagementController.init();

        //add panel components                        
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;

        colimsFrame.getProjectsManagementParentPanel().add(projectManagementController.getProjectManagementPanel(), gridBagConstraints);
        colimsFrame.getProjectsOverviewParentPanel().add(projectOverviewController.getProjectOverviewPanel(), gridBagConstraints);

        //add action listeners                
        //add menu item action listeners
        colimsFrame.getProjectsManagementMenuItem().addActionListener(this);
        colimsFrame.getProjectsOverviewMenuItem().addActionListener(this);
        colimsFrame.getStorageMonitoringMenuItem().addActionListener(this);
        colimsFrame.getNewRunMenuItem().addActionListener(this);
        colimsFrame.getHelpMenuItem().addActionListener(this);

        loginDialog.getLoginButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!(loginDialog.getUserNameTextField().getText().isEmpty() && loginDialog.getPasswordTextField().getPassword().length == 0)) {
                    onLogin();
                } else {
                    showMessageDialog("login validation fail", "please provide an user name and password", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        loginDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });

        //show login dialog
//        loginDialog.setLocationRelativeTo(null);
//        loginDialog.setVisible(true);
        //while developing, set a default user in the AuthenticationBean
        User currentUser = userService.findByName("admin1");
        userService.fetchAuthenticationRelations(currentUser);
        authenticationBean.setCurrentUser(currentUser);
        if (authenticationBean.isAdmin()) {
            initAdminSection();
        } else {
            //disable admin menu
            colimsFrame.getAdminMenu().setEnabled(false);
        }
        showView();
    }

    @Override
    public void showView() {
        colimsFrame.setExtendedState(colimsFrame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        colimsFrame.setLocationRelativeTo(null);
        colimsFrame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String menuItemLabel = e.getActionCommand();

        if (menuItemLabel.equals(colimsFrame.getProjectsManagementMenuItem().getText())) {
            colimsFrame.getMainTabbedPane().setSelectedComponent(colimsFrame.getProjectsManagementParentPanel());
        } else if (menuItemLabel.equals(colimsFrame.getProjectsOverviewMenuItem().getText())) {
            colimsFrame.getMainTabbedPane().setSelectedComponent(colimsFrame.getProjectsOverviewParentPanel());
        } else if (menuItemLabel.equals(colimsFrame.getStorageMonitoringMenuItem().getText())) {
            storageMonitoringController.showView();
        } else if (menuItemLabel.equals(colimsFrame.getNewRunMenuItem().getText())) {
            analyticalRunSetupController.showView();
        } else if (menuItemLabel.equals(colimsFrame.getUserManagementMenuItem().getText())) {
            userManagementController.showView();
        } else if (menuItemLabel.equals(colimsFrame.getInstrumentManagementMenuItem().getText())) {
            instrumentManagementController.showView();
        } else if (menuItemLabel.equals(colimsFrame.getMaterialManagementMenuItem().getText())) {
            materialManagementController.showView();
        } else if (menuItemLabel.equals(colimsFrame.getProtocolManagementMenuItem().getText())) {
            protocolManagementController.showView();
        } else if (menuItemLabel.equals(colimsFrame.getHelpMenuItem().getText())) {
            GuiUtils.centerDialogOnComponent(colimsFrame, mainHelpDialog);
            mainHelpDialog.setVisible(true);
        }
    }

    /**
     * Listen to an MesaggeEvent.
     *
     * @param messageEvent the message event
     */
    @Subscribe
    public void onMessageEvent(MessageEvent messageEvent) {
        showMessageDialog(messageEvent.getMessageTitle(), messageEvent.getMessage(), messageEvent.getMessageType());
    }

    /**
     * In case of an unexpected error, show error dialog with the error message.
     *
     * @param message the error message
     */
    public void showUnexpectedErrorDialog(String message) {
        showMessageDialog("unexpected error", "An unexpected error occured: "
                + "\n" + message
                + "\n" + "please try to rerun the application.", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * In case of a permission error, show permission error dialog with the
     * error message.
     *
     * @param message the error message
     */
    public void showPermissionErrorDialog(String message) {
        showMessageDialog("permission warning", "A permission warning occured: "
                + "\n" + message
                + "\n" + "please contact the admin if you want to change your user permissions.", JOptionPane.WARNING_MESSAGE);
    }

    private void onLogin() {
        //check if a user with given user name and password is found in the db    
        LOGGER.info("Login attempt with user name: " + loginDialog.getUserNameTextField().getText());
        User currentUser = userService.findByLoginCredentials(loginDialog.getUserNameTextField().getText(), String.valueOf(loginDialog.getPasswordTextField().getPassword()));
        if (currentUser != null) {
            LOGGER.info("User " + loginDialog.getUserNameTextField().getText() + " successfully logged in.");
            loginDialog.dispose();

            //set current user in authentication bean 
            userService.fetchAuthenticationRelations(currentUser);
            authenticationBean.setCurrentUser(currentUser);

            if (authenticationBean.isAdmin()) {
                initAdminSection();
            } else {
                //disable admin menu
                colimsFrame.getAdminMenu().setEnabled(false);
            }

            showView();
        } else {
            showMessageDialog("login fail", "No user with the given credentials could be found, please try again.", JOptionPane.ERROR_MESSAGE);
            loginDialog.getUserNameTextField().setText("");
            loginDialog.getPasswordTextField().setText("");
        }
    }

    /**
     * Shows a message dialog.
     *
     * @param title the dialog title
     * @param message the dialog message
     * @param messageType the dialog message type
     */
    private void showMessageDialog(String title, String message, int messageType) {
        if (messageType == JOptionPane.ERROR_MESSAGE) {
            //add message to JTextArea
            JTextArea textArea = new JTextArea(message);
            //put JTextArea in JScrollPane
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(600, 200));
            textArea.setEditable(false);

            JOptionPane.showMessageDialog(colimsFrame.getContentPane(), scrollPane, title, messageType);
        } else {
            JOptionPane.showMessageDialog(colimsFrame.getContentPane(), message, title, messageType);
        }
    }

    /**
     * Inits the admin section. This method is only called if the user is an
     * admin.
     */
    private void initAdminSection() {
        //init admin controllers
        userManagementController.init();
        instrumentManagementController.init();
        materialManagementController.init();
        protocolManagementController.init();

        //add action listeners                
        colimsFrame.getUserManagementMenuItem().addActionListener(this);
        colimsFrame.getInstrumentManagementMenuItem().addActionListener(this);
        colimsFrame.getMaterialManagementMenuItem().addActionListener(this);
        colimsFrame.getProtocolManagementMenuItem().addActionListener(this);
    }
}
