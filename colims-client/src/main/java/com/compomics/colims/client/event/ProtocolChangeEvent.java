package com.compomics.colims.client.event;

/**
 * @author Niels Hulstaert
 */
public class ProtocolChangeEvent extends EntityChangeEvent {        

    /**
     *
     * @param type
     */
    public ProtocolChangeEvent(final Type type) {
        super(type);
    }
    
}
