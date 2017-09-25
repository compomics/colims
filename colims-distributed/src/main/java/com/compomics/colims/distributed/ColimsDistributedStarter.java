package com.compomics.colims.distributed;

import com.compomics.colims.core.service.UserService;
import com.compomics.colims.distributed.consumer.PersistDbTaskHandler;
import com.compomics.colims.model.User;
import com.compomics.colims.model.UserBean;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.CannotCreateTransactionException;

import java.net.ConnectException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The Colims distributed module starter class.
 *
 * @author Niels Hulstaert
 */
public final class ColimsDistributedStarter {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(ColimsDistributedStarter.class);

    /**
     * private no-arg constructor.
     */
    private ColimsDistributedStarter() {
    }

    /**
     * Public launcher method for the distributed module.
     *
     * @param args the String arg array
     */
    public static void main(final String[] args) {
        launch();
    }

    /**
     * Init the application context and set the default distributed user in the
     * UserBean .
     */
    private static void launch() {
        try {
            ApplicationContext applicationContext = new ClassPathXmlApplicationContext("colims-distributed-context.xml");
            UserService userService = applicationContext.getBean("userService", UserService.class);
            UserBean userBean = applicationContext.getBean("userBean", UserBean.class);

            //set the default distributed user in the authentication bean
            User distributedUser = userService.findByName("distributed");
            userService.fetchAuthenticationRelations(distributedUser);
            userBean.setCurrentUser(distributedUser);

            //check if the experiments and FASTA DBs locations exist
            PersistDbTaskHandler persistDbTaskHandler = applicationContext.getBean("persistDbTaskHandler", PersistDbTaskHandler.class);
            Path experimentsDirectory = Paths.get(persistDbTaskHandler.getExperimentsPath());
            if (!Files.exists(experimentsDirectory)) {
                throw new IllegalArgumentException("The experiments directory " + experimentsDirectory + " doesn't exist."
                        + System.lineSeparator()
                        + "Please make sure that the 'experiments.path' property in the /config/colims-distributed.config is correct and mapped in the file system.");
            }
            Path fastasDirectory = Paths.get(persistDbTaskHandler.getFastasPath());
            if (!Files.exists(fastasDirectory)) {
                throw new IllegalArgumentException("The FASTA DBs directory " + fastasDirectory + " doesn't exist." +
                        System.lineSeparator()
                        + "Please make sure that the 'fastas.path' property in the /config/colims-distributed.config is correct and mapped in the file system.");
            }
        } catch (CannotCreateTransactionException ex) {
            System.out.println("-----------------------------------");
            System.out.println("Couldn't connect to the database.");
            System.out.println("Please make sure that the database connection parameters in the /config/colims-distributed.config file are correct.");
            System.out.println("----------------------------------");
            LOGGER.error(ex.getMessage(), ex);
        } catch (Exception ex) {
            System.out.println("-----------------------------------");
            System.out.println("An unexpected exception occurred.");
            System.out.println("-----------------------------------");
            LOGGER.error(ex.getMessage(), ex);
        }
    }
}
