package com.compomics.colims.client.storage;

import com.compomics.colims.distributed.model.StorageTask;
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
@Component("storageTaskProducer")
public class StorageTaskProducer {

    private static final Logger LOGGER = Logger.getLogger(StorageTaskProducer.class);

    @Autowired
    private JmsTemplate storageTaskProducerTemplate;

    /**
     * Send the serialized StorageTask to the storage queue.
     *
     * @param storageTask the StorageTask
     * @throws JmsException
     */
    public void sendStorageTask(final StorageTask storageTask) throws JmsException {
        
        storageTaskProducerTemplate.send(new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                //set StorageTask instance as message body
                ObjectMessage storageTaskMessage = session.createObjectMessage(storageTask);

                LOGGER.info("Sending storage task");

                return storageTaskMessage;
            }
        });
    }

}
