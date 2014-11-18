package com.compomics.colims.client.event.admin;

import com.compomics.colims.client.event.EntityChangeEvent;

/**
 * @author Niels Hulstaert
 */
public class MaterialChangeEvent extends EntityChangeEvent {

    /**
     * Constructor.
     *
     * @param type the change type
     */
    public MaterialChangeEvent(final Type type) {
        super(type);
    }    
    
}
