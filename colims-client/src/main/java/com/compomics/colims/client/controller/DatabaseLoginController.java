package com.compomics.colims.client.controller;

import com.compomics.colims.client.view.DatabaseLoginDialog;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

/**
 * The database login view controller.
 *
 * @author Niels Hulstaert
 */
public class DatabaseLoginController implements Controllable {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseLoginController.class);

    //model
    private final PropertiesConfiguration propertiesConfiguration;
    private String dbUrl;
    private String dbUserName;
    private char[] dbPassword;
    //view
    private DatabaseLoginDialog databaseLoginDialog;

    /**
     * No-arg constructor that loads the client preferences from the colims-client.properties file.
     *
     * @throws ConfigurationException the ConfigurationException
     * @throws IOException the IOException
     */
    public DatabaseLoginController() throws ConfigurationException, IOException {
        //load client properties file
        Resource clientProperties = getResourceByRelativePath("config/colims-client.properties");
        propertiesConfiguration = new PropertiesConfiguration(clientProperties.getURL());
    }

    /**
     * Get the view of this controller.
     *
     * @return the DatabaseLoginDialog
     */
    public DatabaseLoginDialog getDatabaseLoginDialog() {
        return databaseLoginDialog;
    }

    @Override
    public void init() {
        //init view
        databaseLoginDialog = new DatabaseLoginDialog(null, true);

        //set db url and user name from client properties
        databaseLoginDialog.getDbUrlTextField().setText(propertiesConfiguration.getString("db.url"));
        databaseLoginDialog.getDbUserNameTextField().setText(propertiesConfiguration.getString("db.username"));

        databaseLoginDialog.getDbPasswordTextField().requestFocus();

        databaseLoginDialog.getLoginButton().addActionListener(e -> {
            dbUrl = databaseLoginDialog.getDbUrlTextField().getText();
            dbUserName = databaseLoginDialog.getDbUserNameTextField().getText();
            dbPassword = databaseLoginDialog.getDbPasswordTextField().getPassword();

            if (!dbUrl.isEmpty() && !dbUserName.isEmpty() && dbPassword.length != 0) {
                onLogin();
            } else {
                JOptionPane.showMessageDialog(databaseLoginDialog, "Please provide a database url, user name and password.", "database login validation", JOptionPane.WARNING_MESSAGE);
            }
        });

        databaseLoginDialog.getCloseButton().addActionListener(e -> {
            databaseLoginDialog.dispose();
            System.exit(0);
        });

        databaseLoginDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent we) {
                System.exit(0);
            }
        });
    }

    @Override
    public void showView() {
        databaseLoginDialog.setLocationRelativeTo(null);
        databaseLoginDialog.setVisible(true);
    }

    /**
     * Get the database properties as map (key: property name; value: the
     * property value).
     *
     * @return the map of db properties
     */
    public Map<String, String> getDbProperties() {
        Map<String, String> dbProperties = new HashMap<>();

        dbProperties.put("db.url", dbUrl);
        dbProperties.put("db.username", dbUserName);
        dbProperties.put("db.password", String.valueOf(dbPassword));

        return dbProperties;
    }

    /**
     * Reset the properties after use.
     */
    public void reset() {
        Arrays.fill(dbPassword, '0');
        databaseLoginDialog.getDbPasswordTextField().setText("");
    }

    /**
     * Gets a resource by its relative path. If the resource is not found on the
     * file system, the classpath is searched. If nothing is found, null is
     * returned.
     *
     * @param relativePath the relative path of the resource
     * @return the found resource
     */
    private Resource getResourceByRelativePath(final String relativePath) {
        Resource resource = new FileSystemResource(relativePath);

        if (!resource.exists()) {
            //try to find it on the classpath
            resource = new ClassPathResource(relativePath);

            if (!resource.exists()) {
                resource = null;
            }
        }

        return resource;
    }

    /**
     * Test the datase connection and show a message dialog if unsuccessful.
     */
    private void onLogin() {
//        if (testConnection(propertiesConfiguration.getString("db.driver"), dbUrl, dbUserName, String.valueOf(dbPassword))) {
        if (testConnection("org.mariadb.jdbc.Driver", dbUrl, dbUserName, String.valueOf(dbPassword))) {
            checkForPropertiesUpdates();
            databaseLoginDialog.dispose();
        } else {
            JOptionPane.showMessageDialog(databaseLoginDialog, "The database login attempt failed."
                    + System.lineSeparator() + "Please verify your credentials and connectivity and try again.", "Database login unsuccessful", JOptionPane.WARNING_MESSAGE);
            Arrays.fill(dbPassword, '0');
            databaseLoginDialog.getDbPasswordTextField().selectAll();
            databaseLoginDialog.getDbPasswordTextField().requestFocusInWindow();
        }
    }

    /**
     * Test the connection to the database. Return true if successful.
     *
     * @param driverClassName the db driver class name
     * @param url the db url
     * @param userName the db user name
     * @param password the db password
     * @return is the connection successful
     */
    private boolean testConnection(final String driverClassName, final String url, final String userName, final String password) {
        boolean successful = false;

        Connection connection = null;
        try {
            Class.forName(driverClassName);
            connection = DriverManager.getConnection(url, userName, password);

            successful = true;
        } catch (ClassNotFoundException | SQLException ex) {
            LOGGER.error(ex.getMessage(), ex);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    LOGGER.error(ex.getMessage(), ex);
                }
            }
        }

        return successful;
    }

    /**
     * Check if the database properties in the client properties file have been
     * overwritten by the user. If so, ask the user wether or not to store the
     * changes to the client properties file.
     *
     */
    private void checkForPropertiesUpdates() {
        boolean urlChanged = false;
        boolean userNameChanged = false;

        //check for changes
        if (!propertiesConfiguration.getString("db.url").equals(dbUrl)) {
            urlChanged = true;
        }
        if (!propertiesConfiguration.getString("db.username").equals(dbUserName)) {
            userNameChanged = true;
        }

        //show dialog if necessary
        if (urlChanged || userNameChanged) {
            int result = JOptionPane.showConfirmDialog(databaseLoginDialog, "The database url and/or user name differ from the ones stored"
                    + System.lineSeparator() + "in the client properties file (config/colims-client.properties)."
                    + System.lineSeparator() + "Do you want to save the current changes?", "Store database property", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                //store the changes
                if (urlChanged) {
                    propertiesConfiguration.setProperty("db.url", dbUrl);
                }
                if (userNameChanged) {
                    propertiesConfiguration.setProperty("db.username", dbUserName);
                }
                try {
                    propertiesConfiguration.save();
                } catch (ConfigurationException ex) {
                    LOGGER.error(ex.getMessage(), ex);
                }
            }
        }
    }

}
