package com.compomics.colims.core.service;

import com.compomics.colims.model.Spectrum;
import java.util.List;
import java.util.Map;

/**
 *
 * @author niels
 */
public interface SpectrumService extends GenericService<Spectrum, Long> {
    
    /**
     * Find the spectra by analytical run id.
     * 
     * @param analyticalRunId the analytical run id
     * @return the found spectra
     */
    List<Spectrum> findSpectraByAnalyticalRunId(Long analyticalRunId);
    
    /**
     * Get the spectrum peaks as a map (key: mz ratio, value: intensity) by the spectrum id.
     * 
     * @param spectrumId the spectrum id
     * @return 
     */
    Map<Double, Double> getSpectrumPeaks(Long spectrumId);
    
}
