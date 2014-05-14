package com.compomics.colims.client.controller;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import com.compomics.colims.client.controller.admin.user.UserManagementParentController;
import com.compomics.colims.client.controller.admin.CvTermManagementController;
import com.compomics.colims.client.controller.admin.InstrumentManagementController;
import com.compomics.colims.client.controller.admin.MaterialManagementController;
import com.compomics.colims.client.controller.admin.ProtocolManagementController;
import com.compomics.colims.client.event.EntityChangeEvent;
import com.compomics.colims.client.event.admin.InstrumentChangeEvent;
import com.compomics.colims.client.event.admin.MaterialChangeEvent;
import com.compomics.colims.client.event.admin.ProtocolChangeEvent;
import com.compomics.colims.client.event.message.MessageEvent;
import com.compomics.colims.client.util.GuiUtils;
import com.compomics.colims.client.view.UserLoginDialog;
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
import java.awt.Toolkit;
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
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.jdesktop.beansbinding.ELProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    private final EventList<Project> projects = new BasicEventList<>();
    //views
    private ColimsFrame colimsFrame;
    private UserLoginDialog userLoginDialog;
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
    private UserManagementParentController userManagementParentController;
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

    /**
     *
     * @return
     */
    public ColimsFrame getColimsFrame() {
        return colimsFrame;
    }

    /**
     *
     * @return
     */
    public EventList<Project> getProjects() {
        return projects;
    }

    /**
     * Controller init method.
     */
    @Override
    public void init() {
        //set uncaught exception handler
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                LOGGER.error(e.getMessage(), e);
                //check for permission exceptions
                if (e instanceof PermissionException) {
                    showPermissionErrorDialog(e.getMessage());
                } else if (e instanceof ArrayIndexOutOfBoundsException) {
                    showMessageDialog("OLS dialog problem", "Something went wrong in the OLS dialog, please try again.", JOptionPane.INFORMATION_MESSAGE);
                } else if (e instanceof EncryptionOperationNotPossibleException) {
                    showMessageDialog("password encryption error", "The password for the jasypt encryption framework is not correct. "
                            + "\n" + "Check if the 'jasypt.password' property in the colims client config file contains the correct value.", JOptionPane.ERROR_MESSAGE);
                    System.exit(0);
                } else {
                    showUnexpectedErrorDialog(e.getMessage());
                    System.exit(0);
                }
            }
        });

        //register to event bus
        eventBus.register(this);

        //init views       
        colimsFrame = new ColimsFrame();
        colimsFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/colims_icon.png")));
        colimsFrame.setTitle("Colims " + version);
        userLoginDialog = new UserLoginDialog(colimsFrame, true);
        mainHelpDialog = new MainHelpDialog(colimsFrame, true);

        //workaround for better beansbinding logging issue
        org.jdesktop.beansbinding.util.logging.Logger.getLogger(ELProperty.class.getName()).setLevel(Level.SEVERE);

        //find all projects
        projects.addAll(projectService.findAllWithEagerFetching());

        //init child controllers
        projectManagementController.init();
        projectOverviewController.init();
        storageMonitoringController.init();
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
        colimsFrame.getHelpMenuItem().addActionListener(this);

        userLoginDialog.getLoginButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!userLoginDialog.getUserNameTextField().getText().isEmpty() && userLoginDialog.getUserPasswordTextField().getPassword().length != 0) {
                    onLogin();
                } else {
                    showMessageDialog("login validation fail", "Please provide an user name and password.", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        userLoginDialog.getCancelButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userLoginDialog.dispose();
                System.exit(0);
            }
        });

        userLoginDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
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
//            colimsFrame.getAdminMenu().setEnabled(false);
//        }
//        showView();
    }

    @Override
    public void showView() {
        colimsFrame.setExtendedState(colimsFrame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        colimsFrame.setLocationRelativeTo(null);
        colimsFrame.setVisible(true);
        //show login dialog
        userLoginDialog.setLocationRelativeTo(colimsFrame);
        userLoginDialog.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String menuItemLabel = e.getActionCommand();

        if (menuItemLabel.equals(colimsFrame.getExitMenuItem().getText())) {
            System.exit(0);
        } else if (menuItemLabel.equals(colimsFrame.getProjectsManagementMenuItem().getText())) {
            colimsFrame.getMainTabbedPane().setSelectedComponent(colimsFrame.getProjectsManagementParentPanel());
        } else if (menuItemLabel.equals(colimsFrame.getProjectsOverviewMenuItem().getText())) {
            colimsFrame.getMainTabbedPane().setSelectedComponent(colimsFrame.getProjectsOverviewParentPanel());
        } else if (menuItemLabel.equals(colimsFrame.getStorageMonitoringMenuItem().getText())) {
            storageMonitoringController.showView();
        } else if (menuItemLabel.equals(colimsFrame.getUserManagementMenuItem().getText())) {
            userManagementParentController.showView();
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
        showMessageDialog("Permission warning", "A permission warning occured: "
                + "\n" + message
                + "\n" + "Please contact the admin if you want to change your user permissions.", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Listen to an MaterialChangeEvent.
     *
     * @param materialChangeEvent the material change event
     */
    @Subscribe
    public void onMaterialChangeEvent(MaterialChangeEvent materialChangeEvent) {
        updateProjects(materialChangeEvent);
    }

    /**
     * Listen to an InstrumentChangeEvent.
     *
     * @param instrumentChangeEvent the instrument change event
     */
    @Subscribe
    public void onInstrumentChangeEvent(InstrumentChangeEvent instrumentChangeEvent) {
        updateProjects(instrumentChangeEvent);
    }

    /**
     * Listen to an ProtocolChangeEvent.
     *
     * @param protocolChangeEvent the protocol change event
     */
    @Subscribe
    public void onProtocolChangeEvent(ProtocolChangeEvent protocolChangeEvent) {
        updateProjects(protocolChangeEvent);
    }

    /**
     * Reload all projects from the database if necessary.
     */
    private void updateProjects(EntityChangeEvent entityChangeEvent) {
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
                colimsFrame.getAdminMenu().setEnabled(false);
            }
        } else {
            showMessageDialog("login fail", "No user with the given credentials could be found, please try again.", JOptionPane.ERROR_MESSAGE);
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
    private void showMessageDialog(String title, String message, int messageType) {
        if (messageType == JOptionPane.ERROR_MESSAGE) {
            //add message to JTextArea
            JTextArea textArea = new JTextArea(message);
            //put JTextArea in JScrollPane
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(600, 200));
            textArea.setEditable(false);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);

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
        userManagementParentController.init();
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
