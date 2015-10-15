package com.compomics.colims.core.service;

import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.ProteinGroup;
import com.compomics.colims.repository.hibernate.SortDirection;
import com.compomics.colims.repository.hibernate.model.ProteinGroupForRun;

import java.util.List;

/**
 * This interface provides service methods for the ProteinGroup class.
 * <p/>
 * Created by Iain on 08/09/2015.
 */
public interface ProteinGroupService extends GenericService<ProteinGroup, Long> {

    /**
     * Fetch a paged list of protein groups associated with a given analytical run.
     *
     * @param analyticalRun the analytical run
     * @param start         start point in result list
     * @param length        length of result page
     * @param orderBy       column to order results by
     * @param sortDirection the sort direction
     * @param filter        the filter text (an empty string matches all results)
     * @return the list of protein groups
     */
    List<ProteinGroupForRun> getPagedProteinGroupsForRun(AnalyticalRun analyticalRun, final int start, final int length, final String orderBy, final SortDirection sortDirection, final String filter);

    /**
     * Count the number of proteins groups related to a given analytical run, including optional filter term.
     *
     * @param analyticalRun the run of interest
     * @param filter        the filter string
     * @return the number of protein groups
     */
    long getProteinGroupCountForRun(final AnalyticalRun analyticalRun, final String filter);

    /**
     * Get the sequence of the main group protein of the given protein group.
     *
     * @param proteinGroup the specified protein group
     * @return the main protein group sequence
     */
    String getMainProteinSequence(ProteinGroup proteinGroup);

    /**
     * Find the ProteinGroup entity by ID and fetch the associated relations up to the peptide level.
     *
     * @param id the protein group ID
     * @return the ProteinGroup instance
     */
    ProteinGroup findByIdAndFetchAssociations(Long id);

    /**
     * Get all accession strings associated with the specified group of proteins.
     *
     * @param proteinGroup the protein group
     * @return the list of protein accession strings
     */
    List<String> getAccessionsForProteinGroup(ProteinGroup proteinGroup);
}