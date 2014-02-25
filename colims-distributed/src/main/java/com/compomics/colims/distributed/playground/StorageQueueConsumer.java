package com.compomics.colims.distributed.playground;

import java.util.concurrent.atomic.AtomicInteger;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("storageQueueConsumer")
public class StorageQueueConsumer implements MessageListener {

    private static final Logger LOGGER = Logger.getLogger(StorageQueueConsumer.class);

    @Autowired
    private AtomicInteger counter = null;

    /**
     * Implementation of <code>MessageListener</code>.
     */
    @Override
    public void onMessage(Message message) {
        try {
            int messageCount = message.getIntProperty(StorageQueueProducer.MESSAGE_COUNT);

//            if (message instanceof TextMessage) {
//                TextMessage tm = (TextMessage) message;
//                String msg = tm.getText();
//
//                LOGGER.info("Processed message " + messageCount + "value = " + msg);
//
//                counter.incrementAndGet();
//            }
            
             if (message instanceof ActiveMQObjectMessage) {
                TestBean tm = (TestBean)((ActiveMQObjectMessage) message).getObject();                

                LOGGER.info("Processed message " + messageCount + "value = " + tm);

                counter.incrementAndGet();
            }
        } catch (JMSException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}
