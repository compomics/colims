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
     *
     * @param messageTitle
     * @param message
     * @param messageType
     */
    public MessageEvent(final String messageTitle, final String message, final int messageType) {
        this.messageTitle = messageTitle;
        this.message = message;
        this.messageType = messageType;
    }

    /**
     *
     * @param messageTitle
     * @param messages
     * @param messageType
     */
    public MessageEvent(final String messageTitle, final List<String> messages, final int messageType) {
        this.messageTitle = messageTitle;
        Joiner joiner = Joiner.on(System.lineSeparator());
        String concatenatedMessage = joiner.join(messages);
        this.message = concatenatedMessage;
        this.messageType = messageType;
    }

    /**
     *
     * @return
     */
    public String getMessageTitle() {
        return messageTitle;
    }

    /**
     *
     * @return
     */
    public String getMessage() {
        return message;
    }

    /**
     *
     * @return
     */
    public int getMessageType() {
        return messageType;
    }
}
