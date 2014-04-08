/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.service;

import com.compomics.colims.model.AnalyticalRun;

/**
 *
 * @author Kenneth Verheggen
 */
public interface AnalyticalRunService extends GenericService<AnalyticalRun, Long> {    
    
    /**
     * Fetch the spectra
     *
     * @param analyticalRun
     */
    void fetchSpectra(AnalyticalRun analyticalRun);        
}
