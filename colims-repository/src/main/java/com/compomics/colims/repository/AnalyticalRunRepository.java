package com.compomics.colims.repository;

import com.compomics.colims.model.AnalyticalRun;
import java.util.List;

/**
 *
 * @author Kenneth Verheggen
 */
public interface AnalyticalRunRepository extends GenericRepository<AnalyticalRun, Long> {

    public List<AnalyticalRun> findAnalyticalRunsBySampleId(Long sampleID);

}
