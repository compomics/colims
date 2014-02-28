package com.compomics.colims.client.storage;

import com.compomics.colims.distributed.storage.model.StorageTask;
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
    private JmsTemplate storageProducerTemplate;

    /**
     * Send the serialized StorageTask to the storage queue. For queue
     * monitoring purposes, the storage type, submission timestamp, description,
     * user name and sample name are set a jms message properties.
     *
     * @param storageTask the StorageTask
     * @throws JmsException
     */
    public void sendStorageTask(final StorageTask storageTask) throws JmsException {
        
        storageProducerTemplate.send(new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                ObjectMessage storageTaskMessage = session.createObjectMessage(storageTask);

                storageTaskMessage.setStringProperty(StorageTask.STORAGE_TYPE, storageTask.getStorageType().userFriendlyName());
                storageTaskMessage.setLongProperty(StorageTask.SUBMISSION_TIMESTAMP, storageTask.getSubmissionTimestamp());
                storageTaskMessage.setStringProperty(StorageTask.DESCRIPTION, storageTask.getDescription());
                storageTaskMessage.setStringProperty(StorageTask.USER_NAME, storageTask.getUserName());
                storageTaskMessage.setStringProperty(StorageTask.SAMPLE_NAME, storageTask.getSample().getName());

                LOGGER.info("Sending message: " + storageTask.getUserName());

                return storageTaskMessage;
            }
        });
    }

}
