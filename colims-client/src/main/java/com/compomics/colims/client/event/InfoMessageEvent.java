package com.compomics.colims.client.event;

/**
 *
 * @author Niels Hulstaert
 */
public class InfoMessageEvent {

    private String infoMessage;

    public InfoMessageEvent(String infoMessage) {
        this.infoMessage = infoMessage;
    }

    public String getInfoMessage() {
        return infoMessage;
    }
        
}
