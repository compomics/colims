package com.compomics.colims.client.controller;

import com.compomics.colims.client.controller.admin.UserManagementController;
import com.compomics.colims.client.bean.AuthenticationBean;
import com.compomics.colims.client.controller.admin.MetadataManagementController;
import com.compomics.colims.client.event.MessageEvent;
import com.compomics.colims.client.view.LoginDialog;
import com.compomics.colims.client.view.MainFrame;
import com.compomics.colims.model.User;
import com.compomics.colims.core.service.UserService;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import org.apache.log4j.Logger;
import org.jdesktop.beansbinding.ELProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("mainController")
public class MainController implements ActionListener {

    private static final Logger LOGGER = Logger.getLogger(MainController.class);
    //model
    @Autowired
    private AuthenticationBean authenticationBean;
    //views
    private MainFrame mainFrame;
    private LoginDialog loginDialog;
    //child controllers
    @Autowired
    private UserManagementController userManagementController;
    @Autowired
    private ProjectSetupController projectSetupController;
    @Autowired
    private HomeController homeController;
    @Autowired
    private MetadataManagementController metadataManagementController;
    //services
    @Autowired
    private UserService userService;
    @Autowired
    private EventBus eventBus;
//    @Autowired
//    private LocalSessionFactoryBean sessionFactoryBean;

    public MainFrame getMainFrame() {
        return mainFrame;
    }

    /**
     * Controller init method.
     */
    public void init() {
//        SchemaExport schemaExport = new SchemaExport(sessionFactoryBean.getConfiguration());
//        schemaExport.setOutputFile("C:\\Users\\niels\\Desktop\\testing.txt");
//        schemaExport.setFormat(true);
//        schemaExport.setDelimiter(";");
//        schemaExport.execute(true, false, false, true);
                        
        //set uncaught exception handler
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                LOGGER.error(e.getMessage(), e);
                showUnexpectedErrorDialog(e.getMessage());
            }
        });

        //register to event bus
        eventBus.register(this);

        //init login view       
        mainFrame = new MainFrame();
        loginDialog = new LoginDialog(mainFrame, true);

        //workaround for better beansbinding logging issue
        org.jdesktop.beansbinding.util.logging.Logger.getLogger(ELProperty.class.getName()).setLevel(Level.SEVERE);

        //init child controllers
        projectSetupController.init();
        homeController.init();        

        //add panel components                        
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;

        mainFrame.getProjectSetupParentPanel().add(projectSetupController.getProjectSetupPanel(), gridBagConstraints);
        mainFrame.getHomeParentPanel().add(homeController.getHomePanel(), gridBagConstraints);

        //add action listeners                
        //add menu item action listeners
        mainFrame.getHomeMenuItem().addActionListener(this);

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
        loginDialog.setLocationRelativeTo(null);
        loginDialog.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String menuItemLabel = e.getActionCommand();

        if (menuItemLabel.equals(mainFrame.getUserManagementMenuItem().getText())) {
            userManagementController.getUserManagementDialog().setVisible(true);
        }
        else if (menuItemLabel.equals(mainFrame.getMetaDataManagementMenuItem().getText())) {
            metadataManagementController.getMetaDataManagementDialog().setVisible(true);
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
        showMessageDialog("Unexpected Error", "An unexpected error occured: "
                + "\n" + message
                + "\n" + "please try to rerun the application.", JOptionPane.ERROR_MESSAGE);
    }

    private void onLogin() {
        //check if a user with given user name and password is found in the db    
        LOGGER.info("Login attempt with user name: " + loginDialog.getUserNameTextField().getText());
        User currentUser = userService.findByLoginCredentials(loginDialog.getUserNameTextField().getText(), String.valueOf(loginDialog.getPasswordTextField().getPassword()));
        if (currentUser != null) {
            LOGGER.info("User " + loginDialog.getUserNameTextField().getText() + " successfully logged in.");
            loginDialog.setVisible(false);

            //@todo change this to the new user management 
            //check if the current user has Role.ADMIN.
            //If so, init the admin section
            //if (currentUser.getRole().equals(Role.ADMIN)) {
            initAdminSection();
            //} else {
            //set admin menu invisible
            //mainFrame.getAdminMenu().setVisible(false);
            //}

            //set current user in authentication bean    
            authenticationBean.setCurrentUser(currentUser);

            mainFrame.setLocationRelativeTo(null);
            mainFrame.setVisible(true);
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

            JOptionPane.showMessageDialog(mainFrame.getContentPane(), scrollPane, title, messageType);
        } else {
            JOptionPane.showMessageDialog(mainFrame.getContentPane(), message, title, messageType);
        }
    }

    /**
     * Inits the admin section. This method is only called if the user is an
     * admin.
     */
    private void initAdminSection() {
        //init admin controllers
        userManagementController.init();
        metadataManagementController.init();

        //add action listeners                
        mainFrame.getUserManagementMenuItem().addActionListener(this);
        mainFrame.getMetaDataManagementMenuItem().addActionListener(this);
    }
}
