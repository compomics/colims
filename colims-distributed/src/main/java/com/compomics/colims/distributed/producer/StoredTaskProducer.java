package com.compomics.colims.distributed.producer;

import com.compomics.colims.distributed.model.StorageError;
import com.compomics.colims.distributed.model.StoredTask;
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
@Component("storedTaskProducer")
public class StoredTaskProducer {

    private static final Logger LOGGER = Logger.getLogger(StoredTaskProducer.class);

    @Autowired
    private JmsTemplate storedTaskProducerTemplate;

    /**
     * Send the serialized StoredTask to the stored queue.
     *
     * @param storedTask the StoredTask
     * @throws JmsException
     */
    public void sendStoredTask(final StoredTask storedTask) throws JmsException {

        storedTaskProducerTemplate.send(new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                //set StorageError instance as message body
                ObjectMessage storageErrorMessage = session.createObjectMessage(storedTask);

                LOGGER.info("Sending stored task");

                return storageErrorMessage;
            }
        });
    }

}
