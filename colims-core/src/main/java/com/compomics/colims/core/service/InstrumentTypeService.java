package com.compomics.colims.core.service;

import com.compomics.colims.model.InstrumentType;

/**
 *
 * @author niels
 */
public interface InstrumentTypeService extends GenericService<InstrumentType, Long> {
    
    /**
     * Find the instrument type by name, return null if no instrument was found.
     *
     * @param name the instrument type name
     * @return the found instrument type
     */
    InstrumentType findByName(String name);    
   
}
