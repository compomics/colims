package com.compomics.colims.client.event.message;

import javax.swing.JOptionPane;

/**
 *
 * @author Niels Hulstaert
 */
public class StorageQueuesConnectionErrorMessageEvent extends MessageEvent {

    private static final String CONNECTION_ERROR_MESSAGE = "The storage queueing module with broker name %s (URL: %s and JMX URL: %s) cannot be reached. "
            + System.lineSeparator() + System.lineSeparator() + "As a consequence, you can't add runs or monitor the queueing module. "
            + System.lineSeparator() + System.lineSeparator() + "Please check if the storage queueing module is up and running and try again.";

    /**
     *
     * @param brokerName
     * @param brokerUrl
     * @param brokerJmxUrl
     */
    public StorageQueuesConnectionErrorMessageEvent(final String brokerName, final String brokerUrl, final String brokerJmxUrl) {
        super("Storage queueing connection error", String.format(CONNECTION_ERROR_MESSAGE, brokerName, brokerUrl, brokerJmxUrl), JOptionPane.ERROR_MESSAGE);
    }
}
