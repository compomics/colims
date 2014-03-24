package com.compomics.colims.client.event.message;

import javax.swing.JOptionPane;

/**
 *
 * @author Niels Hulstaert
 */
public class StorageQueuesConnectionErrorMessageEvent extends MessageEvent {

    private static final String CONNECTION_ERROR_MESSAGE = "The storage queues with broker name %s (URL %s and JMX URL %s) could not be reached. "
            + "As a consequence, you can't add runs or monitor the storage queues.";

    public StorageQueuesConnectionErrorMessageEvent(final String brokerName, final String brokerUrl, final String brokerJmxUrl) {
        super("database constraint violation", String.format(CONNECTION_ERROR_MESSAGE, brokerName, brokerUrl, brokerJmxUrl), JOptionPane.ERROR_MESSAGE);
    }
}
