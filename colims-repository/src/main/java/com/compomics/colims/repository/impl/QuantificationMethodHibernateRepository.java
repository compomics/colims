/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository.impl;

import com.compomics.colims.model.QuantificationMethod;
import com.compomics.colims.model.QuantificationMethodHasReagent;
import com.compomics.colims.model.comparator.QuantificationMethodHasReagentsNameComparator;
import org.springframework.stereotype.Repository;

import com.compomics.colims.repository.QuantificationMethodRepository;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Kenneth Verheggen, demet
 */
@Repository("quantificationMethodRepository")
public class QuantificationMethodHibernateRepository extends GenericHibernateRepository<QuantificationMethod, Long> implements QuantificationMethodRepository {

    @Override
    public List<QuantificationMethod> findByExample(QuantificationMethod exampleInstance){
        List<QuantificationMethod> quantificationMethods = super.findByExample(exampleInstance);
        
        Iterator<QuantificationMethod> iterator = quantificationMethods.iterator();
        // do some additional comparisons
        while(iterator.hasNext()){
            QuantificationMethod quantificationMethod = iterator.next();
            
            //check accession
            if(!exampleInstance.getAccession().equals(quantificationMethod.getAccession())){
                iterator.remove();
                continue;
            }
            
            //check label
            if(!exampleInstance.getLabel().equals(quantificationMethod.getLabel())){
                iterator.remove();
                continue;
            }
            
            //check name
            if(!exampleInstance.getName().equals(quantificationMethod.getName())){
                iterator.remove();
                continue;
            }
            
            //check QuantificationMethodHasReagents size
            if(exampleInstance.getQuantificationMethodHasReagents().size() != quantificationMethod.getQuantificationMethodHasReagents().size()){
                iterator.remove();
                continue;
            }
            
             //sort the lists of QuantificationMethodHasReagents instances
            QuantificationMethodHasReagentsNameComparator reagentsNameComparator = new QuantificationMethodHasReagentsNameComparator();
            //create a temporary list to avoid changes in the database
            List<QuantificationMethodHasReagent> sortedList = quantificationMethod.getQuantificationMethodHasReagents();
            (exampleInstance.getQuantificationMethodHasReagents()).sort(reagentsNameComparator);
            sortedList.sort(reagentsNameComparator);

            if (!exampleInstance.getQuantificationMethodHasReagents().equals(sortedList)) {
                iterator.remove();
            }
        }
        
        return quantificationMethods;
    }
}
