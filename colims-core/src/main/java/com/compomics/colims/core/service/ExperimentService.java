/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.service;


import com.compomics.colims.model.Experiment;

/**
 * This interface provides service methods for the Experiment class.
 *
 * @author Niels Hulstaert
 */
public interface ExperimentService extends GenericService<Experiment, Long> {

    /**
     * Count the experiments by title and project ID.
     *
     * @param projectId  the project ID
     * @param experiment the Experiment instance
     * @return the number of found experiments
     */
    Long countByProjectIdAndTitle(Long projectId, Experiment experiment);

    /**
     * Fetch the experiment binary files.
     *
     * @param experiment the Experiment instance
     */
    void fetchBinaryFiles(Experiment experiment);

    /**
     * Find an experiment by ID and fetch the associated samples and runs.
     *
     * @param experimentId the experiment ID
     * @return the found experiment
     */
    Experiment findByIdWithEagerFetching(Long experimentId);
}