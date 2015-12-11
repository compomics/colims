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
     * Find the runs by sample ID.
     *
     * @param sampleId the sample ID
     * @return the list of analytical runs
     */
    List<AnalyticalRun> findBySampleId(Long sampleId);

}
