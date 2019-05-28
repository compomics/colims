/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository;

import com.compomics.colims.model.QuantificationReagent;

import java.util.List;

/**
 * This interface provides repository methods for the QuantificationReagent class.
 * 
 * @author demet
 */
public interface QuantificationReagentRepository extends GenericRepository<QuantificationReagent, Long>{

    /**
     * Get the IDs of the search modifications that are only related to the given search parameters.
     *
     * @param quantificationMethodIds the list of quantification methods IDs
     * @return the list of search modification IDs
     */
    List<Long> getConstraintLessQuantReagentIdsForQuantMethods(List<Long> quantificationMethodIds);
    
}
