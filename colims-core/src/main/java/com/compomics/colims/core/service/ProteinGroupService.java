package com.compomics.colims.core.service;

import com.compomics.colims.model.ProteinGroup;
import com.compomics.colims.model.ProteinGroupHasProtein;
import com.compomics.colims.repository.hibernate.ProteinGroupDTO;
import com.compomics.colims.repository.hibernate.SortDirection;

import java.util.List;

/**
 * This interface provides service methods for the ProteinGroup class.
 * <p>
 * Created by Iain on 08/09/2015.
 */
public interface ProteinGroupService extends GenericService<ProteinGroup, Long> {

    /**
     * Fetch a paged list of protein groups associated with the given analytical
     * runs.
     *
     * @param analyticalRunIds the list of analytical run IDs
     * @param start            start point in result list
     * @param length           length of result page
     * @param orderBy          column to order results by
     * @param sortDirection    the sort direction
     * @param filter           the filter text (an empty string matches all results)
     * @return the list of {@link ProteinGroupDTO} instances
     */
    List<ProteinGroupDTO> getPagedProteinGroupsForRuns(List<Long> analyticalRunIds, final int start, final int length, final String orderBy, final SortDirection sortDirection, final String filter);

    /**
     * Fetch a list of protein group DTO instances associated with the given
     * analytical runs.
     *
     * @param analyticalRunIds the list of analytical run IDs
     * @return the list of {@link ProteinGroupDTO} instances
     */
    List<ProteinGroupDTO> getProteinGroupDTOsForRuns(List<Long> analyticalRunIds);

    /**
     * Fetch a list of protein group instances associated with the given
     * analytical runs.
     *
     * @param analyticalRunIds the list of analytical run IDs
     * @return the list of {@link ProteinGroup} instances
     */
    List<ProteinGroup> getProteinGroupsForRuns(List<Long> analyticalRunIds);

    /**
     * Count the number of proteins groups related to the given analytical runs,
     * including optional filter term.
     *
     * @param analyticalRunIds the list of analytical run IDs of interest
     * @param filter           the filter string
     * @return the number of protein groups
     */
    long getProteinGroupCountForRuns(final List<Long> analyticalRunIds, final String filter);

    /**
     * Get the ambiguity members of the given protein group. Return an empty
     * list if nothing was found.
     *
     * @param proteinGroupId the protein group ID
     * @return list of ambiguity members
     */
    List<ProteinGroupHasProtein> getAmbiguityMembers(final Long proteinGroupId);

//    /**
//     * Get ProteinGroupHasProtein object by proteinGroupId. Returns null if
//     * nothing was found.
//     *
//     * @param proteinGroupId the protein group ID
//     * @return ProteinGroupHasProtein and Protein
//     */
//    Map<ProteinGroupHasProtein, Protein> getProteinGroupHasProteinByProteinGroupId(final Long proteinGroupId);

    /**
     * Get the {@link ProteinGroupHasProtein} instance of the main protein of the group.
     * Returns null if nothing was found.
     *
     * @param proteinGroupId the protein group ID
     * @return the found {@link ProteinGroupHasProtein} instance
     */
    ProteinGroupHasProtein getMainProteinGroupHasProtein(final Long proteinGroupId);
}
