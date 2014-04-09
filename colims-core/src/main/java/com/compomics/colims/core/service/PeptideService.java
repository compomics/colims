package com.compomics.colims.core.service;

import com.compomics.colims.model.Peptide;

/**
 *
 * @author Niels Hulstaert
 */
public interface PeptideService extends GenericService<Peptide, Long> {
        
    /*
     * Fetch the peptide peptideHasModifications
     * 
     * @param peptide
     */
    void fetchPeptideHasModificiations(Peptide peptide);
}
