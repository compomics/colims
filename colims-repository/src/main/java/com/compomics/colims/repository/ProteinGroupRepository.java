package com.compomics.colims.repository;

import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.PeptideHasProteinGroup;
import com.compomics.colims.model.ProteinGroup;
import com.compomics.colims.repository.hibernate.SortDirection;
import com.compomics.colims.repository.hibernate.model.ProteinGroupDTO;

import java.util.List;

/**
 * This interface provides repository methods for the ProteinGroup class.
 *
 * @author Niels Hulstaert
 */
public interface ProteinGroupRepository extends GenericRepository<ProteinGroup, Long> {

    /**
     * Fetch a paged list of ProteinGroupForRun instances associated with a given analytical run.
     *
     * @param analyticalRun the analytical run
     * @param start         the start point in result list
     * @param length        the length of the result page
     * @param orderBy       the column to order results by
     * @param sortDirection the sort direction
     * @param filter        the filter text (an empty string matches all results)
     * @return the list of protein groups
     */
    List<ProteinGroupDTO> getPagedProteinGroupsForRun(AnalyticalRun analyticalRun, final int start, final int length, final String orderBy, final SortDirection sortDirection, final String filter);

    /**
     * Count the number of proteins groups related to a given analytical run, including optional filter term.
     *
     * @param analyticalRun the run of interest
     * @param filter        the filter string
     * @return the number of protein groups
     */
    long getProteinGroupCountForRun(final AnalyticalRun analyticalRun, final String filter);

    /**
     * Get the protein groups projections for the given run (Min and max number of distinct peptide sequences per protein group, and min en max
     * number of spectra per protein group).
     *
     * @param analyticalRun the AnalyticalRun instance
     * @return the protein groups projection values for the given run
     */
    Object[] getProteinGroupsProjections(final AnalyticalRun analyticalRun);

}
