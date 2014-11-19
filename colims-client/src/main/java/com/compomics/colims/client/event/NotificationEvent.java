/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.compomics.colims.client.event;

import com.compomics.colims.distributed.model.Notification;

/**
 * Event thrown when a Notification from the distributed module is received.
 *
 * @author Niels Hulstaert
 */
public class NotificationEvent {
    
    private final Notification notification;

    public NotificationEvent(Notification notification) {
        this.notification = notification;
    }

    public Notification getNotification() {
        return notification;
    }            

}