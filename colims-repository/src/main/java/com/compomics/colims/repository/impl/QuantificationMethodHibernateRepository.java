/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository.impl;

import com.compomics.colims.model.QuantificationMethod;
import com.compomics.colims.model.QuantificationMethodHasReagent;
import com.compomics.colims.model.comparator.QuantificationMethodHasReagentsNameComparator;
import com.compomics.colims.repository.QuantificationMethodRepository;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.LongType;
import org.springframework.stereotype.Repository;

import java.util.Iterator;
import java.util.List;

/**
 * @author Kenneth Verheggen, demet
 */
@Repository("quantificationMethodRepository")
public class QuantificationMethodHibernateRepository extends GenericHibernateRepository<QuantificationMethod, Long> implements QuantificationMethodRepository {

    @Override
    public List<QuantificationMethod> findByExample(QuantificationMethod exampleInstance) {
        List<QuantificationMethod> quantificationMethods = super.findByExample(exampleInstance);

        Iterator<QuantificationMethod> iterator = quantificationMethods.iterator();
        // do some additional comparisons
        while (iterator.hasNext()) {
            QuantificationMethod quantificationMethod = iterator.next();

            //check accession
            if (!exampleInstance.getAccession().equals(quantificationMethod.getAccession())) {
                iterator.remove();
                continue;
            }

            //check label
            if (!exampleInstance.getLabel().equals(quantificationMethod.getLabel())) {
                iterator.remove();
                continue;
            }

            //check name
            if (!exampleInstance.getName().equals(quantificationMethod.getName())) {
                iterator.remove();
                continue;
            }

            //check QuantificationMethodHasReagents size
            if (exampleInstance.getQuantificationMethodHasReagents().size() != quantificationMethod.getQuantificationMethodHasReagents().size()) {
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

    @Override
    public List<QuantificationMethodHasReagent> fetchQuantificationMethodHasReagents(Long quantificationMethodId) {
        Criteria criteria = getCurrentSession().createCriteria(QuantificationMethodHasReagent.class);
        criteria.add(Restrictions.eq("quantificationMethod.id", quantificationMethodId));

        return criteria.list();
    }

    @Override
    public List<Long> getConstraintLessQuantMethodIdsForRuns(List<Long> analyticalRunIds) {
        SQLQuery sqlQuery = (SQLQuery) getCurrentSession().getNamedQuery("QuantificationMethod.getConstraintLessSearchParameterIdsForRuns");
        sqlQuery.setParameterList("ids", analyticalRunIds);
        sqlQuery.addScalar("quantification_method.id", LongType.INSTANCE);

        return sqlQuery.list();
    }
}
