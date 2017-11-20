/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.service;

import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.SearchAndValidationSettings;
import com.compomics.colims.model.SearchEngine;
import com.compomics.colims.model.SearchParameters;
import com.compomics.colims.model.enums.SearchEngineType;

/**
 * @author Niels Hulstaert
 */
public interface SearchAndValidationSettingsService extends GenericService<SearchAndValidationSettings, Long> {

    /**
     * Get the SearchEngine by type and version from the database. If no search engine was found, store a new one with
     * the given parameters and return it.
     *
     * @param searchEngineType the search engine type
     * @param version          the search engine version
     * @return the found SearchEngine
     */
    SearchEngine getSearchEngine(SearchEngineType searchEngineType, String version);

    /**
     * Get the SearchParameters by example from the database. If nothing was found, store the given
     * SearchParameters and return them.
     *
     * @param foundSearchParameters the identical search parameters that were found in the database.
     *                              This object is null if nothing was found.
     * @param searchParameters      the search parameters to look for
     * @return the found SearchParameters or the the newly persisted ones
     */
    SearchParameters getSearchParameters(SearchParameters foundSearchParameters, SearchParameters searchParameters);

    /**
     * Fetch the associated of the {@link com.compomics.colims.model.FastaDb} relations.
     *
     * @param searchAndValidationSettings the search and validations settings
     */
    void fetchSearchSettingsHasFastaDb(SearchAndValidationSettings searchAndValidationSettings);

    /**
     * Get the SearchAndValidationSettings by analytical run instance from database.
     * if nothing was found, return null.
     *
     * @param analyticalRun the {@link AnalyticalRun} instance
     * @return the found SearchAndValidationSettings
     */
    SearchAndValidationSettings getByAnalyticalRun(AnalyticalRun analyticalRun);

    /**
     * Clear the resources used by this resource.
     */
    void clear();
}
