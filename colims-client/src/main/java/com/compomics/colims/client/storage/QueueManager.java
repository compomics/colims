
package com.compomics.colims.client.storage;

import com.compomics.colims.distributed.model.AbstractMessage;
import com.compomics.colims.distributed.model.StorageError;
import java.util.List;
import javax.jms.InvalidSelectorException;
import javax.jms.JMSException;
import javax.management.MalformedObjectNameException;
import javax.management.openmbean.OpenDataException;

/**
 *
 * @author Niels Hulstaert
 */
public interface QueueManager {
    
    /**
     * Monitor the queue with the given queue name. Returns a list of objects of
     * class T.
     *
     * @param <T>
     * @param queueName the queue name
     * @return
     * @throws JMSException
     */
    <T extends AbstractMessage> List<T> monitorQueue(String queueName) throws JMSException;
    
    /**
     * Redirect the given StorageError to the given queue.
     * 
     * @param queueName the queue name
     * @param storageError the StorageError
     * @throws JMSException 
     * @throws javax.management.MalformedObjectNameException 
     */
    void redirectStorageError(String queueName, StorageError storageError) throws JMSException, MalformedObjectNameException, Exception;
    
    /**
     * Delete the given message from the given queue.
     *
     * @param queueName the queue name
     * @param messageId the message ID
     * @throws javax.management.MalformedObjectNameException
     */
    void deleteMessage(String queueName, String messageId) throws MalformedObjectNameException, Exception;
    
    /**
     * Remove all messages from the given queue
     *
     * @param queueName the queue name
     * @throws javax.management.MalformedObjectNameException
     */
    void purgeMessages(String queueName) throws MalformedObjectNameException, Exception;       
    
}
