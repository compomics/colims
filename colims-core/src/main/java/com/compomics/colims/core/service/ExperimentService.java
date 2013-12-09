/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.compomics.colims.model.Experiment;

/**
 *
 * @author Niels Hulstaert
 */
public interface ExperimentService extends GenericService<Experiment, Long> {

    /**
     * Gets the experiments from the given files and stores them in the db.
     *
     * @param mzMlFiles the given mzML files
     * @return the list of experiments
     */
    void importMzMlExperiments(List<File> mzMlFiles) throws IOException;
}
