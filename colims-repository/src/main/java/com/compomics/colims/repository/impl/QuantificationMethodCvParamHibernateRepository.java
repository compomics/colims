/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository.impl;

import com.compomics.colims.model.QuantificationMethodCvParam;
import com.compomics.colims.model.QuantificationMethodHasReagent;
import com.compomics.colims.model.comparator.QuantificationMethodHasReagentsNameComparator;
import org.springframework.stereotype.Repository;

import com.compomics.colims.repository.QuantificationMethodCvParamRepository;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Kenneth Verheggen, demet
 */
@Repository("quantificationParametersRepository")
public class QuantificationMethodCvParamHibernateRepository extends GenericHibernateRepository<QuantificationMethodCvParam, Long> implements QuantificationMethodCvParamRepository {

    @Override
    public List<QuantificationMethodCvParam> findByExample(QuantificationMethodCvParam exampleInstance){
        List<QuantificationMethodCvParam> quantificationMethodCvParams = super.findByExample(exampleInstance);
        
        Iterator<QuantificationMethodCvParam> iterator = quantificationMethodCvParams.iterator();
        // do some additional comparisons
        while(iterator.hasNext()){
            QuantificationMethodCvParam quantificationMethodCvParam = iterator.next();
            
            //check accession
            if(!exampleInstance.getAccession().equals(quantificationMethodCvParam.getAccession())){
                iterator.remove();
                continue;
            }
            
            //check label
            if(!exampleInstance.getLabel().equals(quantificationMethodCvParam.getLabel())){
                iterator.remove();
                continue;
            }
            
            //check name
            if(!exampleInstance.getName().equals(quantificationMethodCvParam.getName())){
                iterator.remove();
                continue;
            }
            
            //check value
            if(!(exampleInstance.getValue() == null && quantificationMethodCvParam.getValue() == null) && !exampleInstance.getValue().equals(quantificationMethodCvParam.getValue())){
                iterator.remove();
                continue;
            }
            
            //check QuantificationMethodHasReagents size
            if(exampleInstance.getQuantificationMethodHasReagents().size() != quantificationMethodCvParam.getQuantificationMethodHasReagents().size()){
                iterator.remove();
                continue;
            }
            
             //sort the lists of QuantificationMethodHasReagents instances
            QuantificationMethodHasReagentsNameComparator reagentsNameComparator = new QuantificationMethodHasReagentsNameComparator();
            //create a temporary list to avoid changes in the database
            List<QuantificationMethodHasReagent> sortedList = quantificationMethodCvParam.getQuantificationMethodHasReagents();
            Collections.sort(exampleInstance.getQuantificationMethodHasReagents(), reagentsNameComparator);
            Collections.sort(sortedList, reagentsNameComparator);

            if (!exampleInstance.getQuantificationMethodHasReagents().equals(sortedList)) {
                iterator.remove();
            }
        }
        
        return quantificationMethodCvParams;
    }
}
