/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.service;

import com.compomics.colims.model.QuantificationEngine;
import com.compomics.colims.model.QuantificationMethodCvParam;
import com.compomics.colims.model.QuantificationSettings;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.enums.QuantificationEngineType;

/**
 * This interface provides service methods for the QuantificationSettings class.
 *
 * @author Niels Hulstaert
 */
public interface QuantificationSettingsService extends GenericService<QuantificationSettings, Long> {

    /**
     * Get the QuantificationEngine by type and version from the database. If no quantification engine was found, store
     * a new one with the given parameters and return it.
     *
     * @param quantificationEngineType the quantification engine type
     * @param version                  the quantification engine version
     * @return the found QuantificationEngine instance
     */
    QuantificationEngine getQuantificationEngine(QuantificationEngineType quantificationEngineType, String version);

    /**
     * Get the QuantificationMethodCvParam by example from the database. If nothing was found, store the given
     * QuantificationMethodCvParam and return them.
     *
     * @param quantificationMethodCvParam the QuantificationMethodCvParam instance
     * @return the found QuantificationMethodCvParam
     */
    QuantificationMethodCvParam getQuantificationMethodCvParams(QuantificationMethodCvParam quantificationMethodCvParam);

    /**
     * Get the QuantificationSettings by analytical run instance from database.
     * if nothing was found, send null value.
     * @param analyticalRun
     * @return the found QuantificationSettings
     */
    QuantificationSettings getbyAnalyticalRun(AnalyticalRun analyticalRun);
}
