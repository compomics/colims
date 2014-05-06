package com.compomics.colims.distributed.consumer;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.ErrorHandler;

/**
 *
 * @author Niels Hulstaert
 */
@Component("dbTaskConsumerErrorHandler")
public class DbTaskConsumerErrorHandler implements ErrorHandler {

    private static final Logger LOGGER = Logger.getLogger(DbTaskConsumerErrorHandler.class);

    @Override
    public void handleError(Throwable thrwbl) {
        LOGGER.error(thrwbl.getMessage(), thrwbl);
    }

}
