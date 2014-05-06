package com.compomics.colims.core.service;

import com.compomics.colims.model.Protein;

/**
 *
 * @author Niels Hulstaert
 */
public interface ProteinService extends GenericService<Protein, Long> {
             
    /**
     * Find a protein by the sequence.
     *
     * @param sequence the protein sequence
     * @return the found protein
     */
    Protein findBySequence(String sequence);
}
