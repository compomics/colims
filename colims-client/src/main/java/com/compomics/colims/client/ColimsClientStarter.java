package com.compomics.colims.client;

import com.compomics.colims.client.controller.DatabaseLoginController;
import com.compomics.colims.client.controller.MainController;
import com.compomics.colims.core.config.ApplicationContextProvider;
import org.apache.log4j.Logger;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import java.awt.*;

/**
 * This class starts the client application.
 *
 * @author Niels Hulstaert
 */
public final class ColimsClientStarter {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(ColimsClientStarter.class);
    /**
     * The startup error message.
     */
    private static final String ERROR_MESSAGE = "An error occured during startup, please try again."
            + System.lineSeparator() + "If the problem persists, contact your administrator or post an issue on the google code page.";

    /**
     * Private constructor.
     *
     * @param contextPaths the spring context paths
     */
    private ColimsClientStarter(final String[] contextPaths) {
        launchColimsClient(contextPaths);
    }

    /**
     * Main method.
     *
     * @param args the main method arguments
     */
    public static void main(final String[] args) {
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
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        //</editor-fold>

        //set background color for JOptionPane and JPanel instances
        UIManager.getLookAndFeelDefaults().put("OptionPane.background", Color.WHITE);
        UIManager.getLookAndFeelDefaults().put("Panel.background", Color.WHITE);
        UIManager.getLookAndFeelDefaults().put("FileChooser.background", Color.WHITE);
        //set background color for JFileChooser instances
        UIManager.getLookAndFeelDefaults().put("FileChooser[Enabled].backgroundPainter",
                (Painter<JFileChooser>) (g, object, width, height) -> {
                    g.setColor(Color.WHITE);
                    g.draw(object.getBounds());
                });

        ColimsClientStarter colimsClientStarter = new ColimsClientStarter(new String[]{"colims-client-context.xml"});
    }

    /**
     * Launch the client.
     *
     * @param contextPaths the spring context paths
     */
    private void launchColimsClient(final String[] contextPaths) {
        try {
            //init and show database login dialog for database login credentials
            DatabaseLoginController databaseLoginController = new DatabaseLoginController();
            databaseLoginController.init();
            databaseLoginController.showView();

            //load application context
            AbstractApplicationContext applicationContext = new ClassPathXmlApplicationContext(contextPaths, false);

            //override database properties that require user input by adding a new PropertySource
            DatabasePropertySource databasePropertySource = new DatabasePropertySource(databaseLoginController.getDbProperties());
            applicationContext.getEnvironment().getPropertySources().addFirst(databasePropertySource);

            //refresh the application context
            applicationContext.refresh();

            //reset database properties for security
            databaseLoginController.reset();
            databasePropertySource.reset();

            //set application context in ApplicationContextProvider
            ApplicationContextProvider.getInstance().setApplicationContext(applicationContext);

            MainController mainController = ApplicationContextProvider.getInstance().getBean("mainController");
            SplashScreen splashScreen = ApplicationContextProvider.getInstance().getBean("splashScreen");

            //set final progress bar step
            splashScreen.setProgressLabel("Loading GUI...", splashScreen.getMaximum() - 1);
            mainController.init();
            splashScreen.dispose();

            mainController.showView();
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            //add message to JTextArea
            JTextArea textArea = new JTextArea(ERROR_MESSAGE + System.lineSeparator() + System.lineSeparator() + ex.getMessage());
            //put JTextArea in JScrollPane
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(600, 200));
            textArea.setEditable(false);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);

            JOptionPane.showMessageDialog(null, scrollPane, "Colims startup error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

}
