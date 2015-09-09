package com.compomics.colims.repository;

import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.ProteinGroup;

import java.util.List;

/**
 * This interface provides repository methods for the ProteinGroup class.
 *
 * @author Niels Hulstaert
 */
public interface ProteinGroupRepository extends GenericRepository<ProteinGroup, Long> {
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
