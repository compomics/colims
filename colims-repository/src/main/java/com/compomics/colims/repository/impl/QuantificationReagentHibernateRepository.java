/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository.impl;


import com.compomics.colims.model.QuantificationReagent;
import com.compomics.colims.repository.QuantificationReagentRepository;
import org.hibernate.SQLQuery;
import org.hibernate.type.LongType;
import org.springframework.stereotype.Repository;

import java.util.Iterator;
import java.util.List;

/**
 * This interface provides repository methods for the QuantificationReagent class.
 *
 * @author demet
 */
@Repository("quantificationReagentRepository")
public class QuantificationReagentHibernateRepository extends GenericHibernateRepository<QuantificationReagent, Long> implements QuantificationReagentRepository {

    @Override
    public List<QuantificationReagent> findByExample(QuantificationReagent exampleInstance) {
        List<QuantificationReagent> quantificationReagents = super.findByExample(exampleInstance);

        Iterator<QuantificationReagent> iterator = quantificationReagents.iterator();
        // do some additional comparisons
        while (iterator.hasNext()) {
            QuantificationReagent quantificationReagent = iterator.next();

            //check accession
            if (!exampleInstance.getAccession().equals(quantificationReagent.getAccession())) {
                iterator.remove();
                continue;
            }

            //check label
            if (!exampleInstance.getLabel().equals(quantificationReagent.getLabel())) {
                iterator.remove();
                continue;
            }

            //check name
            if (!exampleInstance.getName().equals(quantificationReagent.getName())) {
                iterator.remove();
            }
        }

        return quantificationReagents;
    }

    @Override
    public List<Long> getConstraintLessQuantReagentIdsForQuantMethods(List<Long> quantificationMethodIds) {
        SQLQuery sqlQuery = (SQLQuery) getCurrentSession().getNamedQuery("QuantificationReagent.getConstraintLessQuantReagentIdsForQuantMethods");
        sqlQuery.setParameterList("ids", quantificationMethodIds);
        sqlQuery.addScalar("quantification_reagent.id", LongType.INSTANCE);

        return sqlQuery.list();
    }
}
