package com.compomics.colims.distributed.producer;

import com.compomics.colims.core.distributed.model.Notification;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

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
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationProducer.class);

    /**
     * Mapper for converting a DbTaskError object to the matching JSON construct.
     */
    private final ObjectMapper objectMapper = new ObjectMapper();
    /**
     * The JMS template instance.
     */
    private final JmsTemplate notificationProducerTemplate;

    @Autowired
    public NotificationProducer(JmsTemplate notificationProducerTemplate) {
        this.notificationProducerTemplate = notificationProducerTemplate;
    }

    /**
     * Send the serialized Notification to the completed queue.
     *
     * @param notification the Notification
     * @throws IOException if an I/O related problem occurs
     */
    public void sendNotification(final Notification notification) throws IOException {
        //map to JSON construct
        String jsonNotification = objectMapper.writeValueAsString(notification);

        notificationProducerTemplate.send(session -> {
            TextMessage notificationTextMessage = session.createTextMessage(jsonNotification);

            LOGGER.info("Sending notification");

            return notificationTextMessage;
        });
    }

}
