/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository;

import com.compomics.colims.model.SearchParameters;
import com.compomics.colims.model.SearchParametersHasModification;

import java.util.List;

/**
 * This interface provides repository methods for the SearchParameters class.
 *
 * @author Niels Hulstaert
 */
public interface SearchParametersRepository extends GenericRepository<SearchParameters, Long> {

    /**
     * Get the IDs of the search parameters that are only related to the given runs..
     *
     * @param analyticalRunIds the list of analytical run IDs
     * @return the list of search parameters IDs
     */
    List<Long> getConstraintLessSearchParameterIdsForRuns(List<Long> analyticalRunIds);

    /**
     * Fetch the search modifications associated with the search parameters.
     *
     * @param searchParametersId the search parameters ID
     * @return the associated search modification join entities
     */
    List<SearchParametersHasModification> fetchSearchModifications(Long searchParametersId);

    /**
     * Cascade save or update the given search parameters. We don't use the JPA merge method because
     * it doesn't work well with detached child entities.
     *
     * @param searchParameters the search parameters to save or update
     */
    void saveOrUpdate(final SearchParameters searchParameters);

}
