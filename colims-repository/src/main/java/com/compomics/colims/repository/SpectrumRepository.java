package com.compomics.colims.repository;

import com.compomics.colims.model.AnalyticalRun;

import com.compomics.colims.model.Spectrum;

/**
 *
 * @author Niels Hulstaert
 */
public interface SpectrumRepository extends GenericRepository<Spectrum, Long> {
    
    /**
     * Count the spectra associated to the given analytical run
     *
     * @param analyticalRun
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
