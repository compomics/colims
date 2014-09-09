/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.distributed.model;

import com.compomics.colims.distributed.model.enums.NotificationType;

/**
 * Notification sent from the distributed module to
 * the clients.
 *
 * @author Niels Hulstaert
 */
public class Notification extends QueueMessage {
    
    /**
     * The notification type.
     */
    private NotificationType type;
    /**
     * The message ID of the db task.
     */
    private String dbTaskMessageId;

    public Notification(NotificationType type, String dbTaskMessageId) {
        this.type = type;
        this.dbTaskMessageId = dbTaskMessageId;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getDbTaskMessageId() {
        return dbTaskMessageId;
    }

    public void setDbTaskMessageId(String dbTaskMessageId) {
        this.dbTaskMessageId = dbTaskMessageId;
    }        

}
