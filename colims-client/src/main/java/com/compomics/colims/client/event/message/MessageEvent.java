package com.compomics.colims.client.event.message;

import com.google.common.base.Joiner;
import java.util.List;

/**
 *
 * @author Niels Hulstaert
 */
public class MessageEvent {

    /**
     * The title of the message.
     */
    private final String messageTitle;
    /**
     * The message.
     */
    private final String message;
    /**
     * The message type.
     */
    private final int messageType;

    /**
     * Constructor.
     *
     * @param messageTitle the message title
     * @param message the message
     * @param messageType the message type
     */
    public MessageEvent(final String messageTitle, final String message, final int messageType) {
        this.messageTitle = messageTitle;
        this.message = message;
        this.messageType = messageType;
    }

    /**
     * Constructor.
     *
     * @param messageTitle the message title
     * @param messages the list of messages
     * @param messageType the message type
     */
    public MessageEvent(final String messageTitle, final List<String> messages, final int messageType) {
        this.messageTitle = messageTitle;
        Joiner joiner = Joiner.on(System.lineSeparator());
        this.message = joiner.join(messages);
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
