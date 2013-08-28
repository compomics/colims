package com.compomics.colims.core.service;

import com.compomics.colims.model.InstrumentCvTerm;
import com.compomics.colims.model.enums.InstrumentCvProperty;
import java.util.List;

/**
 *
 * @author niels
 */
public interface InstrumentCvTermService extends GenericService<InstrumentCvTerm, Long> {
    
    /**
     * Find an analyzer by accession. Returns null if nothing was found.
     * 
     * @param accession the instrument CV term accession
     * @param instrumentCvProperty  the instrument CV term property
     * @return the found instrument CV term
     */
    InstrumentCvTerm findByAccession(String accession, InstrumentCvProperty instrumentCvProperty); 
    
    /**
     * Find instrument CV terms by property. Returns null if nothing was found.
     * 
     * @param accession the analyzer accession
     * @return the found instrument CV terms
     */
    List<InstrumentCvTerm> findByInstrumentCvProperty(InstrumentCvProperty instrumentCvProperty); 
   
}
