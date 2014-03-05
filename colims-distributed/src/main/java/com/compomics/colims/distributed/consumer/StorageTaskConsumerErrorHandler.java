package com.compomics.colims.distributed.consumer;

import com.compomics.colims.distributed.model.StorageError;
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

    @Autowired
    private StorageErrorProducer storageErrorProducer;

    @Override
    public void handleError(Throwable thrwbl) {
        LOGGER.error(thrwbl.getMessage(), thrwbl);

        //wrap the StorageTask in a StorageError and send it to the error queue
        storageErrorProducer.sendStorageError(new StorageError(null, (Exception) thrwbl));
    }

}
