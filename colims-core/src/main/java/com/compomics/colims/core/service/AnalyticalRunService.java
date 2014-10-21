/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.service;

import com.compomics.colims.model.AnalyticalRun;

/**
 * This interface provides service methods for the AnalyticalRun class.
 *
 * @author Niels Hulstaert
 */
public interface AnalyticalRunService extends GenericService<AnalyticalRun, Long> {

    /**
     * Fetch the spectra for the given analytical run.
     *
     * @param analyticalRun the AnalyticalRun instance
     */
    void fetchSpectra(AnalyticalRun analyticalRun);
}
