
package com.compomics.colims.distributed.model;

/**
 *
 * @author Niels Hulstaert
 */
public abstract class AbstractMessage {
    
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
