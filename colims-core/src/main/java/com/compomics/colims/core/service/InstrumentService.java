package com.compomics.colims.core.service;

import com.compomics.colims.model.Instrument;

/**
 *
 * @author niels
 */
public interface InstrumentService extends GenericService<Instrument, Long> {
    
    /**
     * Find the instrument by name, return null if no instrument was found.
     *
     * @param name the instrument by name
     * @return the found instrument
     */
    Instrument findByName(String name);
    
}
