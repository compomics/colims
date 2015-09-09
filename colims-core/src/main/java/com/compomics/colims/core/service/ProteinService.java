package com.compomics.colims.core.service;

import com.compomics.colims.model.Protein;


/**
 * This interface provides service methods for the Protein class.
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

    /**
     * Fetch the protein accessions.
     *
     * @param protein the given Protein instance
     */
    void fetchAccessions(Protein protein);
}
