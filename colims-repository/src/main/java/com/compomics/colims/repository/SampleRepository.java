package com.compomics.colims.repository;

import com.compomics.colims.model.Sample;
import java.util.List;

/**
 *
 * @author Kenneth Verheggen
 */
public interface SampleRepository extends GenericRepository<Sample, Long> {

    public List<Sample> findSampleByExperimentId(Long experimentId);

}
