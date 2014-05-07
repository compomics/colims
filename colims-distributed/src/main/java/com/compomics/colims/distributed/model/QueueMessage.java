package com.compomics.colims.distributed.model;

import java.io.Serializable;

/**
 * Abstract parent class for messages that require a message ID.
 *
 * @author Niels Hulstaert
 */
public abstract class QueueMessage implements Serializable {
    
    private static final long serialVersionUID = 1L;

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