package com.compomics.colims.core.service;

import com.compomics.colims.model.Instrument;

/**
 * This interface provides service methods for the Instrument class.
 *
 * @author Niels Hulstaert
 */
public interface InstrumentService extends GenericService<Instrument, Long> {

    /**
     * Count the number of instruments by instrument name.
     *
     * @param name the instrument name
     * @return the number of found instruments
     */
    Long countByName(String name);

}
