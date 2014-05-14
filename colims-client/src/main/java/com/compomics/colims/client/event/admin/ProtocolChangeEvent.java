package com.compomics.colims.client.event.admin;

import com.compomics.colims.client.event.EntityChangeEvent;

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
