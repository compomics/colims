package com.compomics.colims.client.event.admin;

import com.compomics.colims.client.event.EntityChangeEvent;

/**
 * @author Niels Hulstaert
 */
public class ProtocolChangeEvent extends EntityChangeEvent {

    /**
     * Constructor.
     *
     * @param type the change type
     */
    public ProtocolChangeEvent(final Type type) {
        super(type);
    }
    
}
