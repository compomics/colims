package com.compomics.colims.distributed.consumer;

import com.compomics.colims.core.distributed.model.DbTask;
import com.compomics.colims.core.distributed.model.DeleteDbTask;
import com.compomics.colims.core.distributed.model.Notification;
import com.compomics.colims.core.distributed.model.PersistDbTask;
import com.compomics.colims.distributed.producer.NotificationProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.io.IOException;

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
    private static final Logger LOGGER = LoggerFactory.getLogger(DbTaskConsumer.class);

    private static final String STARTED_MESSAGE = "started processing task ";
    private static final String FINISHED_MESSAGE = "finished processing task ";
    /**
     * Mapper for converting JSON constructs from the queue to corresponding java objects.
     */
    private final ObjectMapper objectMapper = new ObjectMapper();
    /**
     * The PersistDbTask handler.
     */
    private final PersistDbTaskHandler persistDbTaskHandler;
    /**
     * The DeleteDbTask handler.
     */
    private final DeleteDbTaskHandler deleteDbTaskHandler;
    /**
     * The Notification message sender.
     */
    private final NotificationProducer notificationProducer;

    @Autowired
    public DbTaskConsumer(PersistDbTaskHandler persistDbTaskHandler, DeleteDbTaskHandler deleteDbTaskHandler, NotificationProducer notificationProducer) {
        this.persistDbTaskHandler = persistDbTaskHandler;
        this.deleteDbTaskHandler = deleteDbTaskHandler;
        this.notificationProducer = notificationProducer;
    }

    /**
     * Implementation of <code>MessageListener</code>.
     *
     * @param message the incoming message
     */
    @Override
    public void onMessage(final Message message) {
        try {
            TextMessage jsonConstruct;
            if (message instanceof TextMessage) {
                jsonConstruct = (TextMessage) message;
            } else {
                throw new IllegalStateException("The retrieved message is of an incorrect type.");
            }

            DbTask dbTask = objectMapper.readValue(jsonConstruct.getText(), DbTask.class);

            String jmsMessageID = jsonConstruct.getJMSMessageID();
            //set JMS message ID for correlation purposes
            dbTask.setMessageId(jmsMessageID);

            notificationProducer.sendNotification(new Notification(STARTED_MESSAGE, jmsMessageID));

            if (dbTask instanceof PersistDbTask) {
                LOGGER.info("Received persist db task message of type " + ((PersistDbTask) dbTask).getPersistMetadata().getPersistType().userFriendlyName());
                persistDbTaskHandler.handlePersistDbTask((PersistDbTask) dbTask);
            } else if (dbTask instanceof DeleteDbTask) {
                LOGGER.info("Received delete db task message of type " + dbTask.getDbEntityClass());
                deleteDbTaskHandler.handleDeleteDbTask((DeleteDbTask) dbTask);
            }

            notificationProducer.sendNotification(new Notification(FINISHED_MESSAGE, jmsMessageID));
        } catch (IOException | JMSException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}
