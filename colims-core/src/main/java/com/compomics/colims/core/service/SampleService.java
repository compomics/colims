/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.service;

import com.compomics.colims.model.Protocol;
import com.compomics.colims.model.Sample;
import java.util.List;

/**
 *
 * @author Kenneth Verheggen
 */
public interface SampleService extends GenericService<Sample, Long> {

    public List<Sample> findSampleByExperimentId(Long experimentId);
    
    /**
     * Fetch the experiment binary files
     *
     * @param experiment
     */
    void fetchBinaryFiles(Sample sample);
    
    /**
     * Get the most used protocol.
     *
     * @return
     */
    Protocol getMostUsedProtocol();
}
