/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.service;
        
import com.compomics.colims.model.QuantificationMethod;
import com.compomics.colims.model.QuantificationMethodHasReagent;
import java.util.List;

/**
 * This interface provides service methods for the Quantification Method class.
 * 
 * @author demet
 */
public interface QuantificationMethodService extends GenericService<QuantificationMethod, Long>{
    
    /**
     * Fetch quantificationMethodHasReagent class by given quantification method. 
     * Returns empty list if nothing was found.
     * 
     * @param quantificationMethod quantification method
     * @return list of QuantificationMethodHasReagent
     */
    List<QuantificationMethodHasReagent> fetchQuantificationMethodHasReagents(QuantificationMethod quantificationMethod);
}
