
package com.compomics.colims.core.service;

import com.compomics.colims.model.SearchEngine;

/**
 *
 * @author Niels Hulstaert
 */
public interface SearchEngineService extends GenericService<SearchEngine, Long> {
    
    /**
     * Find the search engine by name and version. Returns null if nothing was
     * found.
     *
     * @param name the search engine name
     * @param version the search engine version
     * @return the found SearchEngine
     */
    SearchEngine findByNameAndVersion(String name, String version);
    
}
