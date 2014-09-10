/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.distributed.model;

import com.compomics.colims.distributed.model.enums.NotificationType;
import java.util.Objects;

/**
 * Notification sent from the distributed module to
 * the clients.
 *
 * @author Niels Hulstaert
 */
public class Notification extends QueueMessage {
    
    private static final long serialVersionUID = 2770398844385424822L;
    
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

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + Objects.hashCode(this.type);
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
        if (this.type != other.type) {
            return false;
        }
        if (!Objects.equals(this.dbTaskMessageId, other.dbTaskMessageId)) {
            return false;
        }
        return true;
    }        

}
