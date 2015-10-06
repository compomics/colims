package com.compomics.colims.distributed.consumer;

import com.compomics.colims.core.distributed.model.CompletedDbTask;
import com.compomics.colims.core.distributed.model.DbTaskError;
import com.compomics.colims.core.distributed.model.DeleteDbTask;
import com.compomics.colims.core.service.DeleteService;
import com.compomics.colims.distributed.producer.CompletedTaskProducer;
import com.compomics.colims.distributed.producer.DbTaskErrorProducer;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * This class handles a DeleteDbTask.
 *
 * @author Niels Hulstaert
 */
@Component("deleteDbTaskHandler")
public class DeleteDbTaskHandler {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(DeleteDbTaskHandler.class);

    @Autowired
    private CompletedTaskProducer completedTaskProducer;
    @Autowired
    private DbTaskErrorProducer dbTaskErrorProducer;
    @Autowired
    private DeleteService deleteService;

    public void handleDeleteDbTask(DeleteDbTask deleteDbTask) {
        Long started = System.currentTimeMillis();

        try {
            deleteService.delete(deleteDbTask);

            //wrap the DeleteDbTask in a CompletedTask and send it to the completed task queue
            completedTaskProducer.sendCompletedDbTask(new CompletedDbTask(started, System.currentTimeMillis(), deleteDbTask));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            //wrap the DeleteDbTask in a DbTaskError and send it to the error queue
            try {
                dbTaskErrorProducer.sendDbTaskError(new DbTaskError(started, System.currentTimeMillis(), deleteDbTask, e));
            } catch (IOException e1) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

}
