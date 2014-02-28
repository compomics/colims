package com.compomics.colims.distributed.storage.consumer;

import com.compomics.colims.distributed.playground.TestBean;
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
     */
    @Override
    public void onMessage(Message message) {
        try {            
             if (message instanceof ActiveMQObjectMessage) {
                TestBean tm = (TestBean)((ActiveMQObjectMessage) message).getObject();                

                LOGGER.info("Processed message value = " + tm);               
            }
        } catch (JMSException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}
