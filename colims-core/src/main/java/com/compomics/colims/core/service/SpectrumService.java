package com.compomics.colims.core.service;

import java.util.List;
import java.util.Map;

import com.compomics.colims.model.Spectrum;
import com.compomics.colims.model.SpectrumFile;
import java.io.IOException;

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
     * Get the spectrum peaks as a map (key: mz ratio, value: intensity) by the
     * spectrum id.
     *
     * @param spectrumId the spectrum id
     * @return
     */
    Map<Double, Double> getSpectrumPeaks(Long spectrumId) throws IOException;

    /**
     * Get the spectrum peaks as a map (key: mz ratio, value: intensity) from
     * the SpectrumFile.
     *
     * @param spectrumFile the SpectrumFile
     * @return
     */
    Map<Double, Double> getSpectrumPeaks(SpectrumFile spectrumFile) throws IOException;
}
