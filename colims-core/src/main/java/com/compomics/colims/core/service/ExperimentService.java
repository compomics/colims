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
     * Find the experiment by title and project ID.
     *
     * @param projectId the project ID
     * @param title     the experiment title
     * @return the found experiment
     */
    Experiment findByProjectIdAndTitle(Long projectId, String title);

    /**
     * Fetch the experiment binary files.
     *
     * @param experiment the Experiment instance
     * @return the experiment with fetched binary files
     */
    Experiment fetchBinaryFiles(Experiment experiment);
}