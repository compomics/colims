package com.compomics.colims.repository;

import java.util.List;

import com.compomics.colims.model.Spectrum;

/**
 *
 * @author niels
 */
public interface SpectrumRepository extends GenericRepository<Spectrum, Long> {

    /**
     * Find the spectra by analytical run id
     *
     * @param analyticalRunId the analytical run id
     * @return the found spectra
     */
    List<Spectrum> findSpectraByAnalyticalRunId(Long analyticalRunId);
}
