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
     * Get all proteins for a given analytical run in suitable manner for a paged table.
     *
     * @param analyticalRun The run
     * @return A list of proteins
     */
    List<Protein> getPagedProteinsForRun(AnalyticalRun analyticalRun, final int start, final int length, final String orderBy, final String direction, final String filter);

    /**
     * Count the number of proteins related to a given analytical run, including optional filter term.
     *
     * @param analyticalRun Run of interest
     * @param filter        Filter string
     * @return the number of proteins
     */
    int getProteinCountForRun(final AnalyticalRun analyticalRun, final String filter);

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
