package com.compomics.colims.repository;

import com.compomics.colims.model.ProteinGroup;
import com.compomics.colims.model.ProteinGroupHasProtein;
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
     * Fetch a paged list of ProteinGroupForRun instances associated with the given analytical runs.
     *
     * @param analyticalRunIds the list of analytical run IDs
     * @param start            the start point in result list
     * @param length           the length of the result page
     * @param orderBy          the column to order results by
     * @param sortDirection    the sort direction
     * @param filter           the filter text (an empty string matches all results)
     * @return the list of protein groups
     */
    List<ProteinGroupDTO> getPagedProteinGroups(List<Long> analyticalRunIds, final int start, final int length, final String orderBy, final SortDirection sortDirection, final String filter);

    /**
     * Fetch a list of {@link ProteinGroupDTO} instances associated with the given analytical runs.
     *
     * @param analyticalRunIds the list of analytical run IDs
     * @return the list of protein group DTO objects
     */
    List<ProteinGroupDTO> getProteinGroupDTOs(List<Long> analyticalRunIds);

    /**
     * Fetch a list of {@link ProteinGroup} instances associated with the given analytical runs.
     *
     * @param analyticalRunIds the list of analytical run IDs
     * @return the list of protein groups
     */
    List<ProteinGroup> getProteinGroups(List<Long> analyticalRunIds);

    /**
     * Count the number of proteins groups related to the given analytical runs, including optional filter term.
     *
     * @param analyticalRunIds the list of analytical run IDs of interest
     * @param filter           the filter string
     * @return the number of protein groups
     */
    long getProteinGroupCount(final List<Long> analyticalRunIds, final String filter);

    /**
     * Cascade save or update the given protein group. We don't use the JPA merge method because of cascading issues.
     *
     * @param proteinGroup the ProteinGroup instance to save or update
     */
    void saveOrUpdate(final ProteinGroup proteinGroup);

    /**
     * Get ambiguity members of the given protein group.
     * if nothing found, return empty list
     *
     * @param proteinGroupId
     * @return list of ambiguity members
     */
    List<ProteinGroupHasProtein> getAmbiguityMembers(final Long proteinGroupId);

    /**
     * Get the {@link ProteinGroupHasProtein} instance of the main protein of the group.
     * Returns null if nothing was found.
     *
     * @param proteinGroupId the protein group ID
     * @return the found {@link ProteinGroupHasProtein} instance
     */
    ProteinGroupHasProtein getMainProteinGroupHasProtein(final Long proteinGroupId);

    /**
     * Get the IDs of the protein groups that are only related to the given runs.
     *
     * @param analyticalRunIds the list of analytical run IDs
     * @return the list of protein group IDs
     */
    List<Long> getConstraintLessProteinGroupIdsForRuns(List<Long> analyticalRunIds);
}
