package com.compomics.colims.repository;

import com.compomics.colims.model.Protein;

/**
 *
 * @author Niels Hulstaert
 */
public interface ProteinRepository extends GenericRepository<Protein, Long> {
    
    /**
     * Find a protein by the sequence.
     *
     * @param sequence the protein sequence
     * @return the found protein
     */
    Protein findBySequence(String sequence);
}
