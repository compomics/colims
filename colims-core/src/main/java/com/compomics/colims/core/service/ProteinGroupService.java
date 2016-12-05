package com.compomics.colims.core.service;

import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.Protein;
import com.compomics.colims.model.ProteinGroup;
import com.compomics.colims.model.ProteinGroupHasProtein;
import com.compomics.colims.repository.hibernate.SortDirection;
import com.compomics.colims.repository.hibernate.ProteinGroupDTO;

import java.util.List;
import java.util.Map;

/**
 * This interface provides service methods for the ProteinGroup class.
 * 
 * Created by Iain on 08/09/2015.
 */
public interface ProteinGroupService extends GenericService<ProteinGroup, Long> {

    /**
     * Fetch a paged list of protein groups associated with the given analytical runs.
     *
     * @param analyticalRunIds the list of analytical run IDs
     * @param start         start point in result list
     * @param length        length of result page
     * @param orderBy       column to order results by
     * @param sortDirection the sort direction
     * @param filter        the filter text (an empty string matches all results)
     * @return the list of protein groups
     */
    List<ProteinGroupDTO> getPagedProteinGroupsForRuns(List<Long> analyticalRunIds, final int start, final int length, final String orderBy, final SortDirection sortDirection, final String filter);

    /**
     * Fetch a list of protein groups associated with the given analytical runs.
     *
     * @param analyticalRunIds the list of analytical run IDs
     * @return the list of protein groups
     */
    List<ProteinGroupDTO> getProteinGroupsForRuns(List<Long> analyticalRunIds);

    
    /**
     * Count the number of proteins groups related to the given analytical runs, including optional filter term.
     *
     * @param analyticalRunIds the list of analytical run IDs of interest
     * @param filter        the filter string
     * @return the number of protein groups
     */
    long getProteinGroupCountForRuns(final List<Long> analyticalRunIds, final String filter);

    /**
     * Get the protein groups projections for the given run (Min and max number of distinct peptide sequences per protein group, and min en max
     * number of spectra per protein group).
     *
     * @param analyticalRun the AnalyticalRun instance
     * @return the protein groups projection values for the given run
     */
    Object[] getProteinGroupsProjections(final AnalyticalRun analyticalRun);
    
    /**
     * Get ambiguity members of the given protein group.
     * if nothing found, return empty list
     * @param proteinGroupId
     * @return list of ambiguity members
     */
    List<ProteinGroupHasProtein> getAmbiguityMembers(final Long proteinGroupId);
        
    /**
     * Get ProteinGroupHasProtein object by proteinGroupId
     * if nothing found return null
     * @param proteinGroupId
     * @return ProteinGroupHasProtein and Protein
     */
    Map<ProteinGroupHasProtein, Protein> getProteinGroupHasProteinbyProteinGroupId (final Long proteinGroupId);
}