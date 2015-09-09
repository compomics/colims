package com.compomics.colims.repository;

import com.compomics.colims.model.AnalyticalRun;
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
     * Get the protein IDs for the given analytical run.
     *
     * @param analyticalRun the AnalyticalRun instance
     * @return the list of protein IDs
     */
    List<Long> getProteinIdsForRun(AnalyticalRun analyticalRun);

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
