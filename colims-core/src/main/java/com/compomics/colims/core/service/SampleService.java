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
     * Fetch the sample binary files.
     *
     * @param sample the Sample entity
     */
    void fetchBinaryFiles(Sample sample);

    /**
     * Fetch the sample materials.
     *
     * @param sample the Sample entity
     */
    void fetchMaterials(Sample sample);

    /**
     * Get the most used protocol.
     *
     * @return the most used protocol
     */
    Protocol getMostUsedProtocol();
}
