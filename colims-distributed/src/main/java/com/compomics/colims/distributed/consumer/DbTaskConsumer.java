package com.compomics.colims.distributed.consumer;

import com.compomics.colims.distributed.model.PersistDbTask;
import com.compomics.colims.distributed.model.DbTask;
import com.compomics.colims.distributed.model.DeleteDbTask;
import com.compomics.colims.distributed.model.Notification;
import com.compomics.colims.distributed.model.enums.NotificationType;
import com.compomics.colims.distributed.producer.NotificationProducer;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This class listens to the dbtask queue and handles incoming messages.
 *
 * @author Niels Hulstaert
 */
@Component("dbTaskConsumer")
public class DbTaskConsumer implements MessageListener {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(DbTaskConsumer.class);

    /**
     * The PersistDbTask handler.
     */
    @Autowired
    private PersistDbTaskHandler persistDbTaskHandler;
    /**
     * The DeleteDbTask handler.
     */
    @Autowired
    private DeleteDbTaskHandler deleteDbTaskHandler;
    /**
     * The Notification message sender.
     */
    @Autowired
    private NotificationProducer notificationProducer;

    /**
     * Implementation of <code>MessageListener</code>.
     *
     * @param message the incoming message
     */
    @Override
    public void onMessage(final Message message) {
        try {
            ActiveMQObjectMessage objectMessage;
            if (message instanceof ActiveMQObjectMessage) {
                objectMessage = (ActiveMQObjectMessage) message;
            } else {
                throw new IllegalStateException("The retrieved message is of an incorrect type.");
            }
            DbTask dbTask = (DbTask) objectMessage.getObject();

            String jmsMessageID = objectMessage.getJMSMessageID();
            //set JMS message ID for correlation purposes
            dbTask.setMessageId(jmsMessageID);

            notificationProducer.sendNotification(new Notification(NotificationType.STARTED, jmsMessageID));

            if (dbTask instanceof PersistDbTask) {
                LOGGER.info("Received persist db task message of type " + ((PersistDbTask) dbTask).getPersistMetadata().getStorageType().userFriendlyName());
                persistDbTaskHandler.handlePersistDbTask((PersistDbTask) dbTask);
            } else if (dbTask instanceof DeleteDbTask) {
                LOGGER.info("Received delete db task message of type " + dbTask.getDbEntityClass());
                deleteDbTaskHandler.handleDeleteDbTask((DeleteDbTask) dbTask);
            }

            notificationProducer.sendNotification(new Notification(NotificationType.FINISHED, jmsMessageID));
        } catch (JMSException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}
