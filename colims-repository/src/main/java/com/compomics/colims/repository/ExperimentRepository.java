/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository;

import com.compomics.colims.model.Experiment;
import java.util.List;

/**
 *
 * @author Niels Hulstaert
 */
public interface ExperimentRepository extends GenericRepository<Experiment, Long> {

    List<Experiment> getExperimentsByProjectId(Long projectId);
    
    /**
     * Find the experiment by title.
     *
     * @param title the experiment title
     * @return the found experiment
     */
    Experiment findByTitle(String title);
}
