package com.compomics.colims.distributed.consumer;

import com.compomics.colims.distributed.model.StorageTask;
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
@Component("storageTaskConsumer")
public class StorageTaskConsumer implements MessageListener {

    private static final Logger LOGGER = Logger.getLogger(StorageTaskConsumer.class);

    /**
     * Implementation of <code>MessageListener</code>.
     * @param message
     */
    @Override
    public void onMessage(Message message) {
        try {
            ActiveMQObjectMessage objectMessage = (ActiveMQObjectMessage) message;
            StorageTask storageTask = (StorageTask) objectMessage.getObject();

            LOGGER.info("Received storage task message");
        } catch (JMSException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}
