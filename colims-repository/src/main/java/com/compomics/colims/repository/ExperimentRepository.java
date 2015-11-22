/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository;

import com.compomics.colims.model.Experiment;
import com.compomics.colims.model.ExperimentBinaryFile;

import java.util.List;

/**
 * This interface provides repository methods for the Experiment class.
 *
 * @author Niels Hulstaert
 */
public interface ExperimentRepository extends GenericRepository<Experiment, Long> {

    /**
     * Find the experiment by title and project ID.
     *
     * @param projectId the project ID
     * @param title     the experiment title
     * @return the found experiment
     */
    Experiment findByProjectIdAndTitle(Long projectId, String title);

    /**
     * Fetch the binary files for the given experiment.
     *
     * @param experimentId the experiment ID
     * @return the experiment binary files
     */
    List<ExperimentBinaryFile> fetchBinaryFiles(Long experimentId);
}
