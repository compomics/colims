package com.compomics.colims.distributed.producer;

import com.compomics.colims.distributed.model.StorageError;
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
@Component("storageErrorProducer")
public class StorageErrorProducer {

    private static final Logger LOGGER = Logger.getLogger(StorageErrorProducer.class);

    @Autowired
    private JmsTemplate storageErrorProducerTemplate;

    /**
     * Send the serialized StorageError to the error queue.
     *
     * @param storageError the StorageError
     * @throws JmsException
     */
    public void sendStorageError(final StorageError storageError) throws JmsException {

        storageErrorProducerTemplate.send(new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                //set StorageError instance as message body
                ObjectMessage storageErrorMessage = session.createObjectMessage();

                LOGGER.info("Sending storage error");

                return storageErrorMessage;
            }
        });
    }

}
