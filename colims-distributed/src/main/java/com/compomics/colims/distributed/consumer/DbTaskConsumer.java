package com.compomics.colims.distributed.consumer;

import com.compomics.colims.distributed.model.PersistDbTask;
import com.compomics.colims.distributed.model.DbTask;
import com.compomics.colims.distributed.model.DeleteDbTask;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("dbTaskConsumer")
public class DbTaskConsumer implements MessageListener {

    private static final Logger LOGGER = Logger.getLogger(DbTaskConsumer.class);

    @Autowired
    private PersistDbTaskHandler persistDbTaskHandler;
    @Autowired
    private DeleteDbTaskHandler deleteDbTaskHandler;

    /**
     * Implementation of <code>MessageListener</code>.
     *
     * @param message
     */
    @Override
    public void onMessage(Message message) {
        try {            
            ActiveMQObjectMessage objectMessage = (ActiveMQObjectMessage) message;
            DbTask dbTask = (DbTask) objectMessage.getObject();

            if (dbTask instanceof PersistDbTask) {               
                LOGGER.info("Received persist db task message of type " + ((PersistDbTask) dbTask).getPersistMetadata().getStorageType().userFriendlyName());
                persistDbTaskHandler.handlePersistDbTask((PersistDbTask) dbTask);
            } else if (dbTask instanceof DeleteDbTask) {
                LOGGER.info("Received delete db task message of type " + ((DeleteDbTask) dbTask).getDbEntityClass());
                deleteDbTaskHandler.handleDeleteDbTask((DeleteDbTask) dbTask);
            }
        } catch (JMSException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}
