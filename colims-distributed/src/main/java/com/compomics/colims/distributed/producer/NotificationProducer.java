package com.compomics.colims.distributed.producer;

import com.compomics.colims.core.distributed.model.Notification;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.io.IOException;

/**
 * This class sends Notification instances to the notification queue.
 *
 * @author Niels Hulstaert
 */
@Component("notificationProducer")
public class NotificationProducer {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(NotificationProducer.class);

    /**
     * Mapper for converting a DbTaskError object to the matching JSON construct.
     */
    private ObjectMapper objectMapper = new ObjectMapper();
    /**
     * The JMS template instance.
     */
    @Autowired
    private JmsTemplate notificationProducerTemplate;

    /**
     * Send the serialized Notification to the completed queue.
     *
     * @param notification the Notification
     * @throws IOException if an I/O related problem occurs
     */
    public void sendNotification(final Notification notification) throws IOException {
        //map to JSON construct
        String jsonNotification = objectMapper.writeValueAsString(notification);

        notificationProducerTemplate.send(new MessageCreator() {
            @Override
            public Message createMessage(final Session session) throws JMSException {
                TextMessage notificationTextMessage = session.createTextMessage(jsonNotification);

                LOGGER.info("Sending notification");

                return notificationTextMessage;
            }
        });
    }

}
