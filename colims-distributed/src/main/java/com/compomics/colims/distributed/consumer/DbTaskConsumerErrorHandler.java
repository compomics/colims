package com.compomics.colims.distributed.consumer;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.ErrorHandler;

/**
 * This class handles the errors thrown in the dbTaskConsumer JMS listener.
 *
 * @author Niels Hulstaert
 */
@Component("dbTaskConsumerErrorHandler")
public class DbTaskConsumerErrorHandler implements ErrorHandler {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(DbTaskConsumerErrorHandler.class);

    @Override
    public void handleError(final Throwable thrwbl) {
        LOGGER.error(thrwbl.getMessage(), thrwbl);
    }

}
