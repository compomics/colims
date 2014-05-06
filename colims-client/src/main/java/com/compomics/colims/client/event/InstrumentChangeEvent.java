package com.compomics.colims.client.event;

/**
 * @author Niels Hulstaert
 */
public class InstrumentChangeEvent extends EntityChangeEvent {    

    /**
     *
     * @param type
     */
    public InstrumentChangeEvent(final Type type) {
        super(type);        
    }
    
}
