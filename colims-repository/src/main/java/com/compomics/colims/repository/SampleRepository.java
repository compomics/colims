package com.compomics.colims.repository;

import com.compomics.colims.model.Protocol;
import com.compomics.colims.model.Sample;

/**
 * This interface provides repository methods for the Sample class.
 *
 * @author Niels Hulstaert
 */
public interface SampleRepository extends GenericRepository<Sample, Long> {

    /**
     * Get the most used protocol.
     *
     * @return the most used Protocol instance.
     */
    Protocol getMostUsedProtocol();

}
