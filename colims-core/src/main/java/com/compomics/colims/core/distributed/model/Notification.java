/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.distributed.model;

import java.util.Objects;

/**
 * Notification sent from the distributed module to the clients.
 *
 * @author Niels Hulstaert
 */
public class Notification extends QueueMessage {

    private static final long serialVersionUID = 2770398844385424822L;

    /**
     * The message.
     */
    private String message;
    /**
     * The message ID of the db task.
     */
    private String dbTaskMessageId;

    /**
     * No-arg constructor.
     */
    public Notification() {
    }

    /**
     * Constructor.
     *
     * @param message            the message
     * @param dbTaskMessageId the message ID string
     */
    public Notification(String message, String dbTaskMessageId) {
        this.message = message;
        this.dbTaskMessageId = dbTaskMessageId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDbTaskMessageId() {
        return dbTaskMessageId;
    }

    public void setDbTaskMessageId(String dbTaskMessageId) {
        this.dbTaskMessageId = dbTaskMessageId;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + Objects.hashCode(this.message);
        hash = 83 * hash + Objects.hashCode(this.dbTaskMessageId);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Notification other = (Notification) obj;
        if (!this.message.equals(other.message)) {
            return false;
        }
        return Objects.equals(this.dbTaskMessageId, other.dbTaskMessageId);
    }

}
