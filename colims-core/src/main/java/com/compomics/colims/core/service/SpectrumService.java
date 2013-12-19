package com.compomics.colims.core.service;

import com.compomics.colims.model.AnalyticalRun;
import java.util.List;
import java.util.Map;

import com.compomics.colims.model.Spectrum;
import com.compomics.colims.model.SpectrumFile;
import java.io.IOException;

/**
 *
 * @author Niels Hulstaert
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
    
     /**
     * Count the spectra associated to the given analytical run
     *
     * @param analyticalRunId the analytical run id
     * @return the number of spectra
     */
    Long countSpectraByAnalyticalRun(final AnalyticalRun analyticalRun);
    
    /**
     * Get the minimum retention time of spectra associated to the given
     * analytical run
     *
     * @param analyticalRun
     * @return
     */
    Double getMinimumRetentionTime(final AnalyticalRun analyticalRun);

    /**
     * Get the maximum retention time of spectra associated to the given
     * analytical run
     *
     * @param analyticalRun
     * @return
     */
    Double getMaximumRetentionTime(final AnalyticalRun analyticalRun);

    /**
     * Get the minimum M/Z ratio of spectra associated to the given analytical
     * run
     *
     * @param analyticalRun
     * @return
     */
    Double getMinimumMzRatio(final AnalyticalRun analyticalRun);

    /**
     * Get the maximum M/Z ratio of spectra associated to the given analytical
     * run
     *
     * @param analyticalRun
     * @return
     */
    Double getMaximumMzRatio(final AnalyticalRun analyticalRun);

    /**
     * Get the minimum charge of spectra associated to the given analytical run
     *
     * @param analyticalRun
     * @return
     */
    Integer getMinimumCharge(final AnalyticalRun analyticalRun);

    /**
     * Get the maximum charge of spectra associated to the given analytical run
     *
     * @param analyticalRun
     * @return
     */
    Integer getMaximumCharge(final AnalyticalRun analyticalRun);
}
