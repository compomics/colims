package com.compomics.colims.client.event.message;

import com.google.common.base.Joiner;
import java.util.List;

/**
 *
 * @author Niels Hulstaert
 */
public class MessageEvent {

    private String messageTitle;
    private String message;
    private int messageType;

    public MessageEvent(final String messageTitle, final String message, final int messageType) {
        this.messageTitle = messageTitle;
        this.message = message;
        this.messageType = messageType;
    }

    public MessageEvent(final String messageTitle, final List<String> messages, final int messageType) {
        this.messageTitle = messageTitle;
        Joiner joiner = Joiner.on("\n");
        String concatenatedMessage = joiner.join(messages);
        this.message = concatenatedMessage;
        this.messageType = messageType;
    }

    public String getMessageTitle() {
        return messageTitle;
    }

    public String getMessage() {
        return message;
    }

    public int getMessageType() {
        return messageType;
    }
}
