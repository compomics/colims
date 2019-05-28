/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository;

import com.compomics.colims.model.QuantificationMethod;
import com.compomics.colims.model.QuantificationMethodHasReagent;
import java.util.List;

/**
 * This interface provides repository methods for the QuantificationMethodCvParam class.
 *
 * @author Niels Hulstaert
 */
public interface QuantificationMethodRepository extends GenericRepository<QuantificationMethod, Long> {

    /**
     * Fetch quantificationMethodHasReagent class by given quantification method. 
     * Returns empty list if nothing was found.
     * 
     * @param quantificationMethodId quantification method id
     * @return list of QuantificationMethodHasReagent
     */
    List<QuantificationMethodHasReagent> fetchQuantificationMethodHasReagents(Long quantificationMethodId);

    /**
     * Get the IDs of the quantification methods that are only related to the given runs..
     *
     * @param analyticalRunIds the list of analytical run IDs
     * @return the list of quantification methods IDs
     */
    List<Long> getConstraintLessQuantMethodIdsForRuns(List<Long> analyticalRunIds);
}
