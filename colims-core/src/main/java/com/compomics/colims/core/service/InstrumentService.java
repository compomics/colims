package com.compomics.colims.core.service;

import com.compomics.colims.model.Instrument;
import com.compomics.colims.model.InstrumentCvTerm;
import java.util.List;

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

    /**
     * Delete the given experiment but check first if the instrument was used in
     * any analytical run. If not, delete it else do nothing.
     *
     * @param instrument the instrument to delete
     * @return is the instrument used in an analytical run or not
     */
    boolean checkUsageBeforeDeletion(Instrument instrument);
    
}
