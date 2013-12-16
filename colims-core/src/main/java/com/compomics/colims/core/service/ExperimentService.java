/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.service;

import java.util.List;

import com.compomics.colims.model.Experiment;
import com.compomics.colims.model.ExperimentBinaryFile;

/**
 *
 * @author Niels Hulstaert
 */
public interface ExperimentService extends GenericService<Experiment, Long> {

    /**
     * Gets the experiments from the given project
     *
     * @param mzMlFiles the given mzML files
     * @return the list of experiments
     */
    List<Experiment> getExperimentsByProjectId(Long projectId);

    /**
     * Find the experiment by title.
     *
     * @param title the experiment title
     * @return the found experiment
     */
    Experiment findByTitle(String title);

    /**
     * Fetch the experiment binary files
     *
     * @param experiment
     */
    void fetchBinaryFiles(Experiment experiment);

    /**
     * Save the experiment binary file
     *
     * @param experimentBinaryFile
     */
    void saveBinaryFile(ExperimentBinaryFile experimentBinaryFile);
    
    /**
     * Delete the experiment binary file
     *
     * @param experimentBinaryFile
     */
    void deleteBinaryFile(ExperimentBinaryFile experimentBinaryFile);
}