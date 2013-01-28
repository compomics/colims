package com.compomics.colims.client;

import com.compomics.colims.client.controller.MainController;
import com.compomics.colims.client.spring.ApplicationContextProvider;
import javax.swing.UIManager;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author Niels Hulstaert
 */
public class CoLimsClientStarter {

    private static final Logger LOGGER = Logger.getLogger(CoLimsClientStarter.class);

    public static void main(String[] args) {
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
                ApplicationContext applicationContext = ApplicationContextProvider.getInstance().getApplicationContext();
                MainController mainController = (MainController) applicationContext.getBean("mainController");
                mainController.init();
            }
        });
    }
}
