package com.compomics.colims.client.distributed.consumer;

import com.compomics.colims.distributed.model.Notification;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("notificationConsumer")
public class NotificationConsumer implements MessageListener {

    private static final Logger LOGGER = Logger.getLogger(NotificationConsumer.class);
   

    /**
     * Implementation of <code>MessageListener</code>.
     *
     * @param message
     */
    @Override
    public void onMessage(Message message) {
        try {            
            ActiveMQObjectMessage objectMessage = (ActiveMQObjectMessage) message;
            Notification notification = (Notification) objectMessage.getObject();

            LOGGER.info("received notification message");
            
        } catch (JMSException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}
