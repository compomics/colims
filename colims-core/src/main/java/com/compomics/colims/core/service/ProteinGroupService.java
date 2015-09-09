package com.compomics.colims.core.service;

import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.ProteinGroup;

import java.util.List;

/**
 * Created by Iain on 08/09/2015.
 */
public interface ProteinGroupService extends GenericService<ProteinGroup, Long> {
    /**
     * Get all proteins for a given analytical run in suitable manner for a paged table.
     *
     * @param analyticalRun The run
     * @return A list of proteins
     */
    List<ProteinGroup> getPagedProteinGroupsForRun(AnalyticalRun analyticalRun, final int start, final int length, final String orderBy, final String direction, final String filter);

    /**
     * Count the number of proteins related to a given analytical run, including optional filter term.
     *
     * @param analyticalRun Run of interest
     * @param filter        Filter string
     * @return the number of proteins
     */
    int getProteinGroupCountForRun(final AnalyticalRun analyticalRun, final String filter);
}