package com.compomics.colims.repository;

import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.ProteinAccession;
import com.compomics.colims.model.ProteinGroup;

import java.util.List;

/**
 * This interface provides repository methods for the ProteinGroup class.
 *
 * @author Niels Hulstaert
 */
public interface ProteinGroupRepository extends GenericRepository<ProteinGroup, Long> {
    /**
     * Fetch a paged list of protein groups associated with a given analytical run
     * @param analyticalRun The analytical run
     * @param start         Start point in result list
     * @param length        Length of result page
     * @param orderBy       Column to order results by
     * @param direction     Ordering direction
     * @param filter        Filter text (an empty string matches all results)
     * @return List of protein groups
     */
    List<ProteinGroup> getPagedProteinGroupsForRun(AnalyticalRun analyticalRun, final int start, final int length, final String orderBy, final String direction, final String filter);

    /**
     * Count the number of proteins related to a given analytical run, including optional filter term.
     *
     * @param analyticalRun Run of interest
     * @param filter        Filter string
     * @return the number of proteins
     */
    int getProteinGroupCountForRun(final AnalyticalRun analyticalRun, final String filter);

    /**
     * Get the sequence of the main group protein
     *
     * @param proteinGroup The protein group
     * @return The sequence
     */
    String getMainProteinSequence(ProteinGroup proteinGroup);
}
