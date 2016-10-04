/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository;

import com.compomics.colims.model.QuantificationSettings;

/**
 * This interface provides repository methods for the QuantificationSettings class.
 *
 * @author Niels Hulstaert
 */
public interface QuantificationSettingsRepository extends GenericRepository<QuantificationSettings, Long> {

    /**
     * Get the QuantificationSettings by analytical run id from database.
     * if nothing was found, send null value.
     * @param analyticalRunId
     * @return the found QuantificationSettings
     */
    QuantificationSettings findbyAnalyticalRunId(Long analyticalRunId);
}
