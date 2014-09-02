package com.compomics.colims.distributed.producer;

import com.compomics.colims.distributed.model.Notification;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("notificationProducer")
public class NotificationProducer {

    private static final Logger LOGGER = Logger.getLogger(NotificationProducer.class);

    @Autowired
    private JmsTemplate notificationProducerTemplate;

    /**
     * Send the serialized Notification to the completed queue.
     *
     * @param notification the Notification
     * @throws JmsException
     */
    public void sendNotification(final Notification notification) throws JmsException {

        notificationProducerTemplate.send(new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                //set Notification instance as message body
                ObjectMessage notificationMessage = session.createObjectMessage(notification);

                LOGGER.info("Sending notification");

                return notificationMessage;
            }
        });
    }

}
