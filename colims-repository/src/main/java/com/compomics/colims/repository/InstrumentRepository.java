package com.compomics.colims.repository;

import com.compomics.colims.model.Instrument;

import java.util.List;

/**
 * This interface provides repository methods for the Instrument class.
 *
 * @author Niels Hulstaert
 */
public interface InstrumentRepository extends GenericRepository<Instrument, Long> {

    /**
     * Count the number of instruments by instrument name.
     *
     * @param instrument the Instrument instance
     * @return the number of found instruments
     */
    Long countByName(Instrument instrument);

    /**
     * Find all instruments ordered by name.
     *
     * @return the ordered list of instruments
     */
    List<Instrument> findAllOrderedByName();

}
