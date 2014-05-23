package com.compomics.colims.repository;

import com.compomics.colims.model.SearchEngine;
import com.compomics.colims.model.enums.SearchEngineType;

/**
 *
 * @author Niels Hulstaert
 */
public interface SearchEngineRepository extends GenericRepository<SearchEngine, Long> {

    /**
     * Find the search engine by type and version. Returns null if nothing was
     * found.
     *
     * @param searchEngineType the search engine type
     * @param version the search engine version
     * @return the found SearchEngine
     */
    SearchEngine findByNameAndVersion(SearchEngineType searchEngineType, String version);

}
