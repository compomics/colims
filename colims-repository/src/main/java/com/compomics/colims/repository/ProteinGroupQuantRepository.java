/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository;

import com.compomics.colims.model.ProteinGroupQuant;

/**
 * This interface provides repository methods for the ProteinGroupQuant class.
 *
 * @author demet
 */
public interface ProteinGroupQuantRepository extends GenericRepository<ProteinGroupQuant, Long> {

    /**
     * Fetch a list of ProteinGroupQuant instances associated with a given analytical run and protein group.
     *
     * @param analyticalRunId the analytical run ID
     * @param proteinGroupId  the protein group ID
     * @return protein group quantification instance
     */
    ProteinGroupQuant getProteinGroupQuantForRunAndProteinGroup(Long analyticalRunId, Long proteinGroupId);

    /**
     * Fetch one {@link ProteinGroupQuant} instance associated with a given analytical run. Returns null if nothing was found.
     *
     * @param analyticalRunId the analytical run ID
     * @return a random {@link ProteinGroupQuant} instance
     */
    ProteinGroupQuant getProteinGroupQuantLabeledForRun(Long analyticalRunId);

}
