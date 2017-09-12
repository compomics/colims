/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.service;

import com.compomics.colims.model.ProteinGroupQuantLabeled;

import java.util.List;

/**
 * This interface provides service methods for the ProteinGroupQuantLabeled class.
 *
 * @author demet
 */
public interface ProteinGroupQuantLabeledService extends GenericService<ProteinGroupQuantLabeled, Long> {

    /**
     * Fetch a list of quantification labels associated with a given analytical run. Returns an empty list if nothing was found.
     *
     * @param analyticalRunId the analytical run ID
     * @param numberOfLabels  the number of quantification labels to include
     * @return the list of quantification labels
     */
    List<String> getProteinGroupQuantLabelsForRun(Long analyticalRunId, int numberOfLabels);

    /**
     * Fetch a list of ProteinGroupQuantLabeled instances associated with a given analytical run and protein group.
     *
     * @param analyticalRunId the analytical run ID
     * @param proteinGroupId  the protein group ID
     * @return the list of protein group quantification labeled
     */
    List<ProteinGroupQuantLabeled> getProteinGroupQuantLabeledForRunAndProteinGroup(Long analyticalRunId, Long proteinGroupId);
}
