package com.compomics.colims.core.service;

import com.compomics.colims.model.Instrument;
import com.compomics.colims.model.InstrumentCvTerm;

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
     * Find an analyzer by accession. Returns null if nothing was found.
     * 
     * @param accession the analyzer accession
     * @return the found analyzer
     */
    InstrumentCvTerm findAnalyzerByAccession(String accession);     
   
}
