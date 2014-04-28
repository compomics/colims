package com.compomics.colims.client.event;

/**
 * @author Niels Hulstaert
 */
public class MaterialChangeEvent extends EntityChangeEvent {

    /**
     *
     * @param type
     */
    public MaterialChangeEvent(final Type type) {
        super(type);
    }    
    
}
