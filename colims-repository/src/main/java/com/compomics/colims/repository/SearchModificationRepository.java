package com.compomics.colims.repository;

import com.compomics.colims.model.SearchModification;

import java.util.List;

/**
 * This interface provides repository methods for the SearchModification class.
 *
 * @author Niels Hulstaert
 */
public interface SearchModificationRepository extends GenericRepository<SearchModification, Long> {

    /**
     * Find a search modification by the modification name. Returns the first modification found, null if none were
     * found.
     *
     * @param name the search modification name
     * @return the found modification
     */
    SearchModification findByName(String name);

    /**
     * Find a search modification by the modification accession. Returns null if nothing was found.
     *
     * @param accession the search modification accession
     * @return the found modification
     */
    SearchModification findByAccession(String accession);

    /**
     * Find a search modification by the Utilities PTM name. Returns the first search modification found, null if none
     * were found.
     *
     * @param utilitiesPtmName the utilities PTM name
     * @return the found search modification
     */
    SearchModification findByUtilitiesPtmName(String utilitiesPtmName);

    /**
     * Get the IDs of the search modifications that are only related to the given search parameters.
     *
     * @param searchParametersIds the list of search parameters IDs
     * @return the list of search modification IDs
     */
    List<Long> getConstraintLessSearchModIdsForSearchParams(List<Long> searchParametersIds);

}
