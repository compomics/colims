package com.compomics.colims.repository;

import com.compomics.colims.model.AnalyticalRun;
import java.util.List;

import com.compomics.colims.model.Spectrum;

/**
 *
 * @author Niels Hulstaert
 */
public interface SpectrumRepository extends GenericRepository<Spectrum, Long> {

    /**
     * Find the spectra by analytical run id
     *
     * @param analyticalRunId the analytical run id
     * @return list of spectra for analyticalRun, that may be empty
     */
    List<Spectrum> findSpectraByAnalyticalRunId(final Long analyticalRunId);
    
    /**
     * Count the spectra associated to the given analytical run
     *
     * @param analyticalRunId the analytical run id
     * @return the number of spectra
     */
    Long countSpectraByAnalyticalRun(final AnalyticalRun analyticalRun);
}
