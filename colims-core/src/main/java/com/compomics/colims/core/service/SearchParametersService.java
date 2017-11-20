package com.compomics.colims.core.service;

import com.compomics.colims.model.SearchParameters;

/**
 * This interface provides service methods for the SearchParameters class.
 *
 * @author Niels Hulstaert
 */
public interface SearchParametersService extends GenericService<SearchParameters, Long> {

    /**
     * Fetch the search modifications associated with the search parameters.
     *
     * @param searchParameters the SearchParameters instance
     */
    void fetchSearchModifications(SearchParameters searchParameters);

    /**
     * Find the given {@link SearchParameters} by example;
     *
     * @param searchParameters the given search parameters
     * @return the found search parameters, null of nothing was found
     */
    SearchParameters findByExample(SearchParameters searchParameters);

}
