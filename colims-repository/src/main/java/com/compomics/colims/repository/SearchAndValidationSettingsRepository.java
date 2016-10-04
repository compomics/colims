/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository;

import com.compomics.colims.model.SearchAndValidationSettings;

/**
 * This interface provides repository methods for the SearchAndValidationSettings class.
 *
 * @author Niels Hulstaert
 */
public interface SearchAndValidationSettingsRepository extends GenericRepository<SearchAndValidationSettings, Long> {

    /**
     * Get the SearchAndValidationSettings by analytical run id from database.
     * if nothing was found, send null value.
     * @param analyticalRunId
     * @return the found SearchAndValidationSettings
     */
    SearchAndValidationSettings findbyAnalyticalRunId(Long analyticalRunId);
}
