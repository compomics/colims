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
     * @return the search parameters with fetched search modifications
     */
    SearchParameters fetchSearchModifications(SearchParameters searchParameters);

}
