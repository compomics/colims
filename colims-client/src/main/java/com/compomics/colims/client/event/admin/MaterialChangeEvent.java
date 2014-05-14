package com.compomics.colims.client.event.admin;

import com.compomics.colims.client.event.EntityChangeEvent;

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
