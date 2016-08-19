package com.compomics.colims.repository;

import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.AnalyticalRunBinaryFile;

import java.util.List;

/**
 * This interface provides repository methods for the AnalyticalRun class.
 *
 * @author Niels Hulstaert
 */
public interface AnalyticalRunRepository extends GenericRepository<AnalyticalRun, Long> {

    /**
     * Find the runs by sample ID. The sample and instrument associations are fetched eagerly.
     *
     * @param sampleId the sample ID
     * @return the list of analytical runs
     */
    List<AnalyticalRun> findBySampleId(Long sampleId);

    /**
     * Cascade save or update the given analytical run. We don't use the JPA merge method because of consistency with
     * saveOrUpdate the protein groups in the PersistService.
     *
     * @param analyticalRun the AnalyticalRun instance to save or update
     */
    void saveOrUpdate(final AnalyticalRun analyticalRun);

    
    /**
     * Fetch the binary files for the given analyticalRun.
     *
     * @param analyticalRunId the analyticalRun ID
     * @return the analyticalRun binary files
     */
    List<AnalyticalRunBinaryFile> fetchBinaryFiles(Long analyticalRunId);
}
