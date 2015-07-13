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
     * Get all proteins for a given analytical run
     * @param analyticalRun The run
     * @return A list of proteins
     */
    List<Protein> getPagedProteinsForRun(AnalyticalRun analyticalRun, final int start, final int length, final String orderBy, final String direction, final String filter);

    /**
     * Count the number of proteins related to a given analytical run
     * @param analyticalRun Run of interest
     * @param orderBy Column to order by
     * @param filter Filter string
     * @return
     */
    int getProteinCountForRun(final AnalyticalRun analyticalRun, final String orderBy, final String filter);

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
