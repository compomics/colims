package com.compomics.colims.repository;

import com.compomics.colims.model.InstrumentCvTerm;

/**
 *
 * @author Niels Hulstaert
 */
public interface AnalyzerRepository extends GenericRepository<InstrumentCvTerm, Long> {
    
    /**
     * Find an analyzer by accession. Returns null if nothing was found.
     * 
     * @param accession the analyzer accession
     * @return the found analyzer
     */
    InstrumentCvTerm findByAccession(String accession);
    
}
