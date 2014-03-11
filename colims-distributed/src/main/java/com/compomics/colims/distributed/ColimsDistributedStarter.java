package com.compomics.colims.distributed;

import com.compomics.colims.core.config.ApplicationContextProvider;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import org.hibernate.exception.GenericJDBCException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.CannotCreateTransactionException;

/**
 *
 * @author Niels Hulstaert
 */
public class ColimsDistributedStarter {

    private static final Logger LOGGER = Logger.getLogger(ColimsDistributedStarter.class);

    public static void main(final String[] args) {
        launch();
    }

    private static void launch() {
        try {
            ApplicationContext distributedApplicationContext = new ClassPathXmlApplicationContext("colims-distributed-context.xml");
            ApplicationContextProvider.getInstance().setApplicationContext(distributedApplicationContext);            
        } catch (CannotCreateTransactionException ex) {
            LOGGER.error(ex.getMessage(), ex);
            System.exit(0);
        }
    }
}
