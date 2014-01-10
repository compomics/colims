package com.compomics.colims.client;

import com.compomics.colims.client.controller.ColimsController;
import com.compomics.colims.client.spring.ApplicationContextProvider;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import org.apache.log4j.Logger;
import org.hibernate.exception.GenericJDBCException;
import org.springframework.context.ApplicationContext;
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
                    ApplicationContext applicationContext = ApplicationContextProvider.getInstance().getApplicationContext();
                    ColimsController colimsController = (ColimsController) applicationContext.getBean("colimsController");
                    colimsController.init();
                } catch (CannotCreateTransactionException ex) {
                    if (ex.getCause() instanceof GenericJDBCException) {
                        JOptionPane.showMessageDialog(null, "Cannot establish a connection to the database, the application will not start."
                                + "\n" + "Make sure your connection parameters in the config/colims-client.properties."
                                + "\n" + "file are correct.", "Colims startup error", JOptionPane.ERROR_MESSAGE);                        
                    }
                    System.exit(0);
                }
            }
        });
    }
}
