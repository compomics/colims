package com.compomics.colims.core.service;

import com.compomics.colims.model.AnalyticalRun;

import java.util.List;

/**
 * This interface provides service methods for the AnalyticalRun class.
 *
 * @author Niels Hulstaert
 */
public interface AnalyticalRunService extends GenericService<AnalyticalRun, Long> {

    /**
     * Find the runs by sample ID. The sample and instrument associations are fetched eagerly.
     *
     * @param sampleId the sample ID
     * @return the list of analytical runs
     */
    List<AnalyticalRun> findBySampleId(Long sampleId);

    /**
     * Fetch the instrument (if necessary) of the given analytical run.
     *
     * @param analyticalRun the AnalyticalRun instance
     */
    void fetchInstrument(AnalyticalRun analyticalRun);

    /**
     * Fetch the analytical run binary files.
     *
     * @param analyticalRun the AnalyticalRun instance
     */
    void fetchBinaryFiles(AnalyticalRun analyticalRun);

    /**
     * Save or update the analytical run.
     *
     * @param analyticalRun the given analytical run
     */
    void saveOrUpdate(AnalyticalRun analyticalRun);
}
