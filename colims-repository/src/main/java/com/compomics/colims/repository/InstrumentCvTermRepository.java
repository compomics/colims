package com.compomics.colims.repository;

import com.compomics.colims.model.InstrumentCvTerm;
import com.compomics.colims.model.enums.InstrumentCvProperty;

/**
 *
 * @author Niels Hulstaert
 */
public interface InstrumentCvTermRepository extends GenericRepository<InstrumentCvTerm, Long> {
    
    /**
     * Find an instrument CV term by accession. Returns null if nothing was found.
     * 
     * @param accession the instrument CV term accession
     * @return the found CV term
     */
    InstrumentCvTerm findByAccession(String accession, InstrumentCvProperty instrumentCvProperty);
    
}
