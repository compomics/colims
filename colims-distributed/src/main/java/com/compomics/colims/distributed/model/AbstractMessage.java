package com.compomics.colims.distributed.model;

/**
 * Abstract parent class for messages that require a message ID.
 *
 * @author Niels Hulstaert
 */
public abstract class AbstractMessage {

    /**
     * The message ID
     */
    protected String messageId;

    public AbstractMessage() {
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

}
