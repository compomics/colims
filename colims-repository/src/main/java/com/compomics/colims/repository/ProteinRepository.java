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

    /**
     * Find a protein with hibernate search by the sequence.
     *
     * @param sequence the protein sequence
     * @return the found protein
     */
    Protein hibernateSearchFindBySequence(String sequence);

    /**
     * Rebuild the lucene index for the Protein entity class.
     */
    void rebuildIndex();
}
