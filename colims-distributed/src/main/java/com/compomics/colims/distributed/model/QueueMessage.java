package com.compomics.colims.distributed.model;

import java.io.Serializable;

/**
 * Abstract parent class for messages that require a message ID for monitoring
 * purposes.
 *
 * @author Niels Hulstaert
 */
public abstract class QueueMessage implements Serializable {

    private static final long serialVersionUID = 5987756507350846636L;

    /**
     * The message ID
     */
    protected String messageId;

    public QueueMessage() {
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }    

}
