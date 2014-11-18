package com.compomics.colims.client.event.admin;

import com.compomics.colims.client.event.EntityChangeEvent;

/**
 * @author Niels Hulstaert
 */
public class InstrumentChangeEvent extends EntityChangeEvent {    

    /**
     * Constructor.
     *
     * @param type the change type
     */
    public InstrumentChangeEvent(final Type type) {
        super(type);        
    }
    
}
