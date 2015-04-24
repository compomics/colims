package com.compomics.colims.repository;

import com.compomics.colims.model.AnalyticalRun;

import com.compomics.colims.model.Spectrum;

import java.util.List;

/**
 * This interface provides repository methods for the Spectrum class.
 *
 * @author Niels Hulstaert
 */
public interface SpectrumRepository extends GenericRepository<Spectrum, Long> {

    List getPagedSpectra(AnalyticalRun analyticalRun, int start, int length, String orderBy, String direction, String filter);

    int getSpectraCount(AnalyticalRun analyticalRun);

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
}
