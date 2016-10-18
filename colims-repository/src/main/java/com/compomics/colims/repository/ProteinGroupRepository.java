package com.compomics.colims.repository;

import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.ProteinGroup;
import com.compomics.colims.repository.hibernate.ProteinGroupDTO;
import com.compomics.colims.repository.hibernate.SortDirection;

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
     * @param analyticalRunIds the list of analytical run IDs
     * @param start         the start point in result list
     * @param length        the length of the result page
     * @param orderBy       the column to order results by
     * @param sortDirection the sort direction
     * @param filter        the filter text (an empty string matches all results)
     * @return the list of protein groups
     */
    List<ProteinGroupDTO> getPagedProteinGroupsForRun(List<Long> analyticalRunIds, final int start, final int length, final String orderBy, final SortDirection sortDirection, final String filter);
    
    /**
     * Fetch a list of ProteinGroupForRun instances associated with a given analytical run.
     *
     * @param analyticalRunIds the list of analytical run IDs
     * @return the list of protein groups
     */
    List<ProteinGroupDTO> getProteinGroupsForRun(List<Long> analyticalRunIds);

    
    /**
     * Count the number of proteins groups related to a given analytical run, including optional filter term.
     *
     * @param analyticalRunIds the list of analytical run IDs of interest
     * @param filter        the filter string
     * @return the number of protein groups
     */
    long getProteinGroupCountForRun(final List<Long> analyticalRunIds, final String filter);

    /**
     * Get the protein groups projections for the given run (Min and max number of distinct peptide sequences per
     * protein group, and min en max number of spectra per protein group).
     *
     * @param analyticalRun the AnalyticalRun instance
     * @return the protein groups projection values for the given run
     */
    Object[] getProteinGroupsProjections(final AnalyticalRun analyticalRun);

    /**
     * Cascade save or update the given protein group. We don't use the JPA merge method because of cascading issues.
     *
     * @param proteinGroup the ProteinGroup instance to save or update
     */
    void saveOrUpdate(final ProteinGroup proteinGroup);

}
