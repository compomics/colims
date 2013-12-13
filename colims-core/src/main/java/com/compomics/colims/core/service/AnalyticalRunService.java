/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.service;

import com.compomics.colims.model.AnalyticalRun;
import java.util.List;

/**
 *
 * @author Kenneth Verheggen
 */
public interface AnalyticalRunService extends GenericService<AnalyticalRun, Long> {

    public List<AnalyticalRun> findAnalyticalRunsBySampleId(Long sampleId);
}
