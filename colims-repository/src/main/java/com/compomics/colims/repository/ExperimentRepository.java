/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository;

import com.compomics.colims.model.Experiment;

/**
 * This interface provides repository methods for the Experiment class.
 *
 * @author Niels Hulstaert
 */
public interface ExperimentRepository extends GenericRepository<Experiment, Long> {

    /**
     * Find the experiment by title.
     *
     * @param title the experiment title
     * @return the found experiment
     */
    Experiment findByTitle(String title);

     /**
     * Find the experiment by title and project ID.
     *
     * @param projectId the project ID
     * @param title the experiment title
     * @return the found experiment
     */
    Experiment findByProjectIdAndTitle(Long projectId, String title);
}
