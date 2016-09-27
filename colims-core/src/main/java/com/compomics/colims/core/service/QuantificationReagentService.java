/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.service;

import com.compomics.colims.model.QuantificationReagent;
        
/**
 * This interface provides service methods for the Quantification Reagent class.
 * 
 * @author demet
 */
public interface QuantificationReagentService extends GenericService<QuantificationReagent, Long>{
    
    /**
     * Get the QuantificationReagent by example from the database. If nothing was found, store the given
     * QuantificationReagent and return them.
     *
     * @param quantificationReagent the QuantificationReagent instance
     * @return the found QuantificationReagent
     */
    QuantificationReagent getQuantificationReagent(QuantificationReagent quantificationReagent);
}
