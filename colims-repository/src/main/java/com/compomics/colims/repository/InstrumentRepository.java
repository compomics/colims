package com.compomics.colims.repository;

import com.compomics.colims.model.Instrument;
import java.util.List;

/**
 *
 * @author Niels Hulstaert
 */
public interface InstrumentRepository extends GenericRepository<Instrument, Long> {
    
    /**
     * Find the instrument by the instrument name, returns null if no instrument
     * was found.
     *
     * @param name the instrument name
     * @return the found instrument
     */
    Instrument findByName(String name);
    
    /**
     * Find all instruments ordered by name.
     * 
     * @return the ordered list of instruments
     */
    List<Instrument> findAllOrderedByName();

}
