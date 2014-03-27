package com.compomics.colims.client;

import com.compomics.colims.client.controller.ColimsController;
import com.compomics.colims.client.controller.DatabaseLoginController;
import com.compomics.colims.client.storage.QueueManager;
import com.compomics.colims.core.config.ApplicationContextProvider;
import com.compomics.util.gui.waiting.waitinghandlers.ProgressDialogX;
import java.awt.Cursor;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.hibernate.exception.GenericJDBCException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.CannotCreateTransactionException;

/**
 *
 * @author Niels Hulstaert
 */
public class ColimsClientStarter {

    private static final Logger LOGGER = Logger.getLogger(ColimsClientStarter.class);

    public static void main(final String[] args) {
        launch();
    }

    private static void launch() {
        /*
         * Set the Nimbus look and feel
         */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            LOGGER.error(ex.getMessage(), ex);
        } catch (InstantiationException ex) {
            LOGGER.error(ex.getMessage(), ex);
        } catch (IllegalAccessException ex) {
            LOGGER.error(ex.getMessage(), ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        //</editor-fold>

        /*
         * Create and display the form
         */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    DatabaseLoginController databaseLoginController = new DatabaseLoginController();
                    databaseLoginController.init();
                    databaseLoginController.showView();

                    //load application context
                    AbstractApplicationContext clientApplicationContext = new ClassPathXmlApplicationContext(new String[]{"colims-client-context.xml"}, false);

                    //override database properties that require user input by adding a new PropertySource
                    DatabasePropertySource databasePropertySource = new DatabasePropertySource(databaseLoginController.getDbProperties());
                    clientApplicationContext.getEnvironment().getPropertySources().addLast(databasePropertySource);                                                            
                    
                    //refresh the application context
                    clientApplicationContext.refresh();                   
                    
                    //set application context in ApplicationContextProvider
                    ApplicationContextProvider.getInstance().setApplicationContext(clientApplicationContext);

                    //reset database properties for security
                    databaseLoginController.reset();
                    databasePropertySource.reset();

                    //init Colims controller
                    ColimsController colimsController = ApplicationContextProvider.getInstance().getBean("colimsController");
                    colimsController.init();
                } catch (Exception ex) {
                    LOGGER.error(ex.getMessage(), ex);
                    JOptionPane.showMessageDialog(null, "An error occured during startup, please try again."
                            + "\n" + "If the problem persists, contact the administrator.", 
                            "colims startup error", JOptionPane.ERROR_MESSAGE);
                    System.exit(0);
                }

            }
        });
    }
}
