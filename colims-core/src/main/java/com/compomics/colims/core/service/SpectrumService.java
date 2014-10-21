package com.compomics.colims.core.service;

import com.compomics.colims.model.AnalyticalRun;
import java.util.Map;

import com.compomics.colims.model.Spectrum;
import com.compomics.colims.model.SpectrumFile;
import java.io.IOException;

/**
 * This interface provides service methods for the Spectrum class.
 *
 * @author Niels Hulstaert
 */
public interface SpectrumService extends GenericService<Spectrum, Long> {

    /**
     * Get the spectrum peaks as a map (key: mz ratio, value: intensity) by the
     * spectrum id.
     *
     * @param spectrumId the spectrum id
     * @return the peak map
     * @throws java.io.IOException the IOException
     */
    Map<Double, Double> getSpectrumPeaks(Long spectrumId) throws IOException;

    /**
     * Get the spectrum peaks as a map (key: mz ratio, value: intensity) from
     * the SpectrumFile.
     *
     * @param spectrumFile the SpectrumFile
     * @return the peak map
     * @throws java.io.IOException the IOException
     */
    Map<Double, Double> getSpectrumPeaks(SpectrumFile spectrumFile) throws IOException;

    /**
     * Count the spectra associated to the given analytical run.
     *
     * @param analyticalRun the AnalyticalRun instance
     * @return the number of spectra
     */
    Long countSpectraByAnalyticalRun(final AnalyticalRun analyticalRun);

    /**
     * Get the minimum retention time of spectra associated to the given
     * analytical run.
     *
     * @param analyticalRun the AnalyticalRun instance
     * @return the minimum retention time value
     */
    Double getMinimumRetentionTime(final AnalyticalRun analyticalRun);

    /**
     * Get the maximum retention time of spectra associated to the given
     * analytical run.
     *
     * @param analyticalRun the AnalyticalRun instance
     * @return the maximum retention time value
     */
    Double getMaximumRetentionTime(final AnalyticalRun analyticalRun);

    /**
     * Get the minimum M/Z ratio of spectra associated to the given analytical
     * run.
     *
     * @param analyticalRun the AnalyticalRun instance
     * @return the minimum M/Z ratio value
     */
    Double getMinimumMzRatio(final AnalyticalRun analyticalRun);

    /**
     * Get the maximum M/Z ratio of spectra associated to the given analytical
     * run.
     *
     * @param analyticalRun the AnalyticalRun instance
     * @return the maximum M/Z ratio value
     */
    Double getMaximumMzRatio(final AnalyticalRun analyticalRun);

    /**
     * Get the minimum charge of spectra associated to the given analytical run.
     *
     * @param analyticalRun the AnalyticalRun instance
     * @return the minimum charge value
     */
    Integer getMinimumCharge(final AnalyticalRun analyticalRun);

    /**
     * Get the maximum charge of spectra associated to the given analytical run.
     *
     * @param analyticalRun the AnalyticalRun instance
     * @return the maximum charge value
     */
    Integer getMaximumCharge(final AnalyticalRun analyticalRun);

    /**
     * Fetch the spectrum spectrumFiles.
     *
     * @param spectrum the Spectrum instance
     */
    void fetchSpectrumFiles(Spectrum spectrum);
}
