package com.compomics.colims.distributed.consumer;

import com.compomics.colims.distributed.model.DbTaskError;
import com.compomics.colims.distributed.producer.StorageErrorProducer;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ErrorHandler;

/**
 *
 * @author Niels Hulstaert
 */
@Component("storageTaskConsumerErrorHandler")
public class StorageTaskConsumerErrorHandler implements ErrorHandler {

    private static final Logger LOGGER = Logger.getLogger(StorageTaskConsumerErrorHandler.class);

    @Override
    public void handleError(Throwable thrwbl) {
        LOGGER.error(thrwbl.getMessage(), thrwbl);
    }

}
