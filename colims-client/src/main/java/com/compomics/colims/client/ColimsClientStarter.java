package com.compomics.colims.client;

import com.compomics.colims.client.controller.ColimsController;
import com.compomics.colims.client.controller.DatabaseLoginController;
import com.compomics.colims.core.config.ApplicationContextProvider;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import org.apache.log4j.Logger;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author Niels Hulstaert
 */
public class ColimsClientStarter {

    private final static Logger LOGGER = Logger.getLogger(ColimsClientStarter.class);

    public static void main(String[] args) {
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
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        //</editor-fold>

        
        launchColimsClient(new String[]{"colims-client-context.xml"});
    }

    private static void launchColimsClient(String[] contextPaths) {
        try {
            //init and show database login dialog for database login credentials
            DatabaseLoginController databaseLoginController = new DatabaseLoginController();
            databaseLoginController.init();
            databaseLoginController.showView();

            //load application context
            AbstractApplicationContext applicationContext = new ClassPathXmlApplicationContext(contextPaths, false);

            //override database properties that require user input by adding a new PropertySource
            DatabasePropertySource databasePropertySource = new DatabasePropertySource(databaseLoginController.getDbProperties());
            applicationContext.getEnvironment().getPropertySources().addLast(databasePropertySource);

            //refresh the application context
            applicationContext.refresh();

            //set application context in ApplicationContextProvider
            ApplicationContextProvider.getInstance().setApplicationContext(applicationContext);

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

}
