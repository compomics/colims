/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository;

import com.compomics.colims.model.ProteinGroupQuantLabeled;

import java.util.List;

/**
 * This interface provides service methods for the ProteinGroupQuantLabeled class.
 *
 * @author demet
 */
public interface ProteinGroupQuantLabeledRepository extends GenericRepository<ProteinGroupQuantLabeled, Long> {

    /**
     * Fetch one ProteinGroupQuantLabeled instance associated with a given analytical run. Returns null if nothing was found.
     *
     * @param analyticalRunId the analytical run ID
     * @return a random {@link ProteinGroupQuantLabeled} instance
     */
    ProteinGroupQuantLabeled getProteinGroupQuantLabeledForRun(Long analyticalRunId);

    /**
     * Fetch a list of ProteinGroupQuantLabeled instances associated with a given analytical run and protein group.
     *
     * @param analyticalRunId the analytical run ID
     * @param proteinGroupId  the protein group ID
     * @return the list of protein group quantification labeled
     */
    List<ProteinGroupQuantLabeled> getProteinGroupQuantLabeledForRunAndProteinGroup(Long analyticalRunId, Long proteinGroupId);
}
