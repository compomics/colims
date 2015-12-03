package com.compomics.colims.client.distributed.consumer;

import com.compomics.colims.client.event.NotificationEvent;
import com.compomics.colims.core.distributed.model.Notification;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.EventBus;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.io.IOException;

/**
 * This class listens to the notification topic and handles incoming messages.
 *
 * @author Niels Hulstaert
 */
@Component("notificationConsumer")
public class NotificationConsumer implements MessageListener {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(NotificationConsumer.class);

    /**
     * Mapper for converting JSON constructs from the queue to corresponding java objects.
     */
    private ObjectMapper objectMapper = new ObjectMapper();
    /**
     * The guava EventBus instance.
     */
    @Autowired
    private EventBus eventBus;

    /**
     * Implementation of <code>MessageListener</code>.
     *
     * @param message the incoming JMS message
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
            Notification notification = objectMapper.readValue(jsonConstruct.getText(), Notification.class);

            LOGGER.info("received notification message");

            //post notification on the event bus
            eventBus.post(new NotificationEvent(notification));
        } catch (JMSException | IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
