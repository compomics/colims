/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository;

import com.compomics.colims.model.Experiment;
import com.compomics.colims.model.ExperimentBinaryFile;
import com.compomics.colims.model.Project;

import java.util.List;

/**
 * This interface provides repository methods for the Experiment class.
 *
 * @author Niels Hulstaert
 */
public interface ExperimentRepository extends GenericRepository<Experiment, Long> {

    /**
     * Count the experiments by title and project ID.
     *
     * @param projectId the project ID
     * @param experiment the Experiment instance
     * @return the number of found experiments
     */
    Long countByProjectIdAndTitle(Long projectId, Experiment experiment);

    /**
     * Fetch the binary files for the given experiment.
     *
     * @param experimentId the experiment ID
     * @return the experiment binary files
     */
    List<ExperimentBinaryFile> fetchBinaryFiles(Long experimentId);

    /**
     * Find the experiment by ID and fetch the associated samples.
     *
     * @param experimentId the experiment ID
     * @return the found experiment
     */
    Experiment findByIdWithFetchedSamples(Long experimentId);
}
