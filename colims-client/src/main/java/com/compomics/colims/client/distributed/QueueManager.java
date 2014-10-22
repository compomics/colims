package com.compomics.colims.client.distributed;

import com.compomics.colims.distributed.model.QueueMessage;
import com.compomics.colims.distributed.model.DbTaskError;
import java.util.List;
import javax.jms.JMSException;
import javax.management.MalformedObjectNameException;

/**
 *
 * @author Niels Hulstaert
 */
public interface QueueManager {

    /**
     * Monitor the queue with the given queue name. Returns a list of objects of
     * class T.
     *
     * @param <T> a class the extends QueueMessage
     * @param queueName the queue name
     * @return the list of messages on the queue
     * @throws JMSException thrown in case of a JMS related exception
     */
    <T extends QueueMessage> List<T> monitorQueue(String queueName) throws JMSException;

    /**
     * Redirect the given StorageError to the given queue.
     *
     * @param queueName the queue name
     * @param storageError the StorageError
     * @throws Exception exception parent class, ugly way to catch all
     * exceptions types
     */
    void redirectStorageError(String queueName, DbTaskError storageError) throws Exception;

    /**
     * Delete the given message from the given queue.
     *
     * @param queueName the queue name
     * @param messageId the message ID
     * @throws Exception exception parent class, ugly way to catch all
     * exceptions types
     */
    void deleteMessage(String queueName, String messageId) throws Exception;

    /**
     * Remove all messages from the given queue.
     *
     * @param queueName the queue name
     * @throws Exception exception parent class, ugly way to catch all
     * exceptions types
     */
    void purgeMessages(String queueName) throws Exception;

    /**
     * Test the connection to the queues.
     *
     * @return whether or not a connection could be made
     */
    boolean testConnection();

    /**
     * Get the broker name.
     *
     * @return the broker name
     */
    String getBrokerName();

    /**
     * Get the broker URL.
     *
     * @return the broker URL
     */
    String getBrokerUrl();

    /**
     * Get the broker JMX URL.
     *
     * @return the broker JMX URL
     */
    String getBrokerJmxUrl();

}
