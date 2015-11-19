package com.compomics.colims.distributed;

import com.compomics.colims.core.service.UserService;
import com.compomics.colims.model.User;
import com.compomics.colims.model.UserBean;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.CannotCreateTransactionException;

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
        } catch (CannotCreateTransactionException ex) {
            LOGGER.error(ex.getMessage(), ex);
            System.exit(1);
        }
    }
}
