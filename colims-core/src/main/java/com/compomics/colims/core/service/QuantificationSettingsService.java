/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.service;

import com.compomics.colims.model.QuantificationEngine;
import com.compomics.colims.model.QuantificationParameters;
import com.compomics.colims.model.QuantificationSettings;
import com.compomics.colims.model.enums.QuantificationEngineType;

/**
 *
 * @author Niels Hulstaert
 */
public interface QuantificationSettingsService extends GenericService<QuantificationSettings, Long> {

    /**
     * Get the QuantificationEngine by type and version from the database. If no quantification
     * engine was found, store a new one with the given parameters and return
     * it.
     *
     * @param quantificationEngineType the quantification engine type
     * @param version the quantification engine version
     * @return
     */
    QuantificationEngine getQuantificationEngine(QuantificationEngineType quantificationEngineType, String version);

    /**
     * Get the QuantificationParameterSettings by example from the database. If nothing
     * was found, store the given QuantificationParameterSettings and return them.
     *
     * @param quantificationParameterSettings
     * @return
     */
    QuantificationParameters getQuantificationParamterSettings(QuantificationParameters quantificationParameterSettings);

}
