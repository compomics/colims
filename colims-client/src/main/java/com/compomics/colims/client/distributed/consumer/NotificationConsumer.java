package com.compomics.colims.client.distributed.consumer;

import com.compomics.colims.client.event.NotificationEvent;
import com.compomics.colims.distributed.model.Notification;
import com.google.common.eventbus.EventBus;
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
@Component("notificationConsumer")
public class NotificationConsumer implements MessageListener {

    private static final Logger LOGGER = Logger.getLogger(NotificationConsumer.class);
   
    @Autowired
    private EventBus eventBus;       
    
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
            
            //set JMS message ID
            //notification.getDbTask().setMessageId(objectMessage.getJMSMessageID());
            
            eventBus.post(new NotificationEvent(notification));            
        } catch (JMSException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}
