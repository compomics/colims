package com.compomics.colims.repository;

import com.compomics.colims.model.Protocol;
import com.compomics.colims.model.Sample;

/**
 *
 * @author Kenneth Verheggen
 */
public interface SampleRepository extends GenericRepository<Sample, Long> {
    
    /**
     * Get the most used protocol.
     *
     * @return
     */
    Protocol getMostUsedProtocol();

}
