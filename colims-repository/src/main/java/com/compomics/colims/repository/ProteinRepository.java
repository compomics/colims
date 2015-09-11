package com.compomics.colims.repository;

import com.compomics.colims.model.Protein;

import java.util.List;

/**
 * This interface provides repository methods for the Protein class.
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
     * Get the IDs of the proteins that are only related to the given runs.
     *
     * @param analyticalRunIds the list of analytical run IDs
     * @return the list of protein IDs
     */
    List<Long> getConstraintLessProteinIdsForRuns(List<Long> analyticalRunIds);

//    /**
//     * Find a protein with hibernate search by the sequence.
//     *
//     * @param sequence the protein sequence
//     * @return the found protein
//     */
//    Protein hibernateSearchFindBySequence(String sequence);
//
//    /**
//     * Rebuild the lucene index for the Protein entity class.
//     */
//    void rebuildIndex();
}
