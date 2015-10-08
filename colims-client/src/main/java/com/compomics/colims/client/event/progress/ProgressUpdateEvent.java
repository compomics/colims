/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.client.event.progress;

/**
 * Event thrown when a progress dialog should update.
 *
 * @author Niels Hulstaert
 */
public class ProgressUpdateEvent {

    /**
     * The progress message.
     */
    private String message;

    /**
     * Constructor.
     *
     * @param message the progress update message
     */
    public ProgressUpdateEvent(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
