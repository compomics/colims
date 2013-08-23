package com.compomics.colims.repository;

import com.compomics.colims.model.InstrumentType;

/**
 *
 * @author Niels Hulstaert
 */
public interface InstrumentTypeRepository extends GenericRepository<InstrumentType, Long> {        
    
    /**
     * Find the instrument type by name, return null if no instrument was found.
     *
     * @param name the instrument type name
     * @return the found instrument type
     */
    InstrumentType findByName(String name);    
    
}
