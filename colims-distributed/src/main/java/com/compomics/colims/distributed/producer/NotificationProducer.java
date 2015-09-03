package com.compomics.colims.distributed.producer;

import com.compomics.colims.core.distributed.model.Notification;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

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
     * The JMS template instance.
     */
    @Autowired
    private JmsTemplate notificationProducerTemplate;

    /**
     * Send the serialized Notification to the completed queue.
     *
     * @param notification the Notification
     */
    public void sendNotification(final Notification notification) {

        notificationProducerTemplate.send(new MessageCreator() {
            @Override
            public Message createMessage(final Session session) throws JMSException {
                //set Notification instance as message body
                ObjectMessage notificationMessage = session.createObjectMessage(notification);

                LOGGER.info("Sending notification");

                return notificationMessage;
            }
        });
    }

}
