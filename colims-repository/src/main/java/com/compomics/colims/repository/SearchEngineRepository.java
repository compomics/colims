package com.compomics.colims.repository;

import com.compomics.colims.model.SearchEngine;
import com.compomics.colims.model.enums.SearchEngineType;

/**
 * This interface provides repository methods for the SearchEngine class.
 *
 * @author Niels Hulstaert
 */
public interface SearchEngineRepository extends GenericRepository<SearchEngine, Long> {

    /**
     * Find the search engine by type. If multiple were found (different
     * versions), a random one is returned. Returns null if nothing was found.
     *
     * @param searchEngineType the search engine type
     * @return the found SearchEngine
     */
    SearchEngine findByType(SearchEngineType searchEngineType);

    /**
     * Find the search engine by type and version. Returns null if nothing was
     * found.
     *
     * @param searchEngineType the search engine type
     * @param version the search engine version
     * @return the found SearchEngine
     */
    SearchEngine findByTypeAndVersion(SearchEngineType searchEngineType, String version);

}
