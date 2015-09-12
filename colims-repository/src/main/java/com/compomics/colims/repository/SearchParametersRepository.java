/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository;

import com.compomics.colims.model.SearchParameters;

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

}
