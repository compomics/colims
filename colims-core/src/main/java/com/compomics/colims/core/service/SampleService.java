/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.service;

import com.compomics.colims.model.Protocol;
import com.compomics.colims.model.Sample;

/**
 * This interface provides service methods for the Sample class.
 *
 * @author Niels Hulstaert
 */
public interface SampleService extends GenericService<Sample, Long> {

    /**
     * Fetch the sample materials and binary files.
     *
     * @param sample the Sample entity
     */
    void fetchMaterialsAndBinaryFiles(Sample sample);

    /**
     * Get the most used protocol.
     *
     * @return the most used protocol
     */
    Protocol getMostUsedProtocol();

    /**
     * Find the sample by ID and fetch the sample runs.
     *
     * @param sampleId the sample ID
     */
    Sample findByIdAndFetchRuns(Long sampleId);

}
