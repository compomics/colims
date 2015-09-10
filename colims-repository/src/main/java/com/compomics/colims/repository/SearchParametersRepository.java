/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository;

import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.SearchParameters;

import java.util.List;

/**
 * This interface provides repository methods for the SearchParameters class.
 *
 * @author Niels Hulstaert
 */
public interface SearchParametersRepository extends GenericRepository<SearchParameters, Long> {

    /**
     * Get the search parameters IDs for the given analytical run.
     *
     * @param analyticalRun the AnalyticalRun instance
     * @return the list of search parameters IDs
     */
    List<Long> getSearchParameterIdsForRun(AnalyticalRun analyticalRun);

}
