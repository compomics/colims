package com.compomics.colims.client.distributed;

import com.compomics.colims.core.distributed.model.DbTaskError;
import com.compomics.colims.core.distributed.model.QueueMessage;

import java.util.List;

/**
 * @author Niels Hulstaert
 */
public interface QueueManager {

    /**
     * Monitor the queue with the given queue name. Returns a list of objects of class T.
     *
     * @param <T>       a class that extends QueueMessage
     * @param queueName the queue name
     * @param clazz     the class T object necessary for converting the JSON constructs on the queue to the
     *                  corresponding T instances
     * @return the list of messages on the queue
     */
    <T extends QueueMessage> List<T> monitorQueue(String queueName, Class<T> clazz);

    /**
     * Redirect the given StorageError to the given queue.
     *
     * @param queueName    the queue name
     * @param storageError the StorageError
     * @throws Exception exception parent class, ugly way to catch all exceptions types
     */
    void redirectStorageError(String queueName, DbTaskError storageError) throws Exception;

    /**
     * Delete the given message from the given queue.
     *
     * @param queueName the queue name
     * @param messageId the message ID
     * @throws Exception exception parent class, ugly way to catch all exceptions types
     */
    void deleteMessage(String queueName, String messageId) throws Exception;

    /**
     * Remove all messages from the given queue.
     *
     * @param queueName the queue name
     * @throws Exception exception parent class, ugly way to catch all exceptions types
     */
    void purgeMessages(String queueName) throws Exception;

    /**
     * Test the connection to the queues. Returns true if the connection was successful.
     *
     * @return whether or not a connection could be made
     */
    boolean isReachable();

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
