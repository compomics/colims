/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.service;

import com.compomics.colims.model.ProteinGroupQuant;

/**
 * This interface provides service methods for the ProteinGroupQuantLabeled class.
 * 
 * @author demet
 */
public interface ProteinGroupQuantService extends GenericService<ProteinGroupQuant, Long>{
        
    /**
     * Fetch ProteinGroupQuant instance associated with a given analytical run and protein group.
     *
     * @param analyticalRunId the analytical run ID
     * @param proteinGroupId the protein group ID
     * @return protein group quantification labeled instance
     */
    ProteinGroupQuant getProteinGroupQuantForRunAndProteinGroup(Long analyticalRunId, Long proteinGroupId);
}
