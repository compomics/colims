/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository.impl;

import com.compomics.colims.model.ProteinGroupQuant;
import com.compomics.colims.repository.ProteinGroupQuantRepository;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author demet
 */
@Repository("proteinGroupQuantRepository")
public class ProteinGroupQuantHibernateRepository extends GenericHibernateRepository<ProteinGroupQuant, Long> implements ProteinGroupQuantRepository {

    @Override
    public ProteinGroupQuant getProteinGroupQuantForRunAndProteinGroup(Long analyticalRunId, Long proteinGroupId) {
        Criteria criteria = getCurrentSession().createCriteria(ProteinGroupQuant.class);

        criteria.add(Restrictions.eq("analyticalRun.id", analyticalRunId));
        criteria.add(Restrictions.eq("proteinGroup.id", proteinGroupId));

        return (ProteinGroupQuant) criteria.uniqueResult();
    }

    @Override
    public ProteinGroupQuant getProteinGroupQuantLabeledForRun(Long analyticalRunId) {
        Criteria criteria = getCurrentSession().createCriteria(ProteinGroupQuant.class);

        criteria.add(Restrictions.eq("analyticalRun.id", analyticalRunId));
        criteria.setFirstResult(0);
        criteria.setMaxResults(1);

        List<ProteinGroupQuant> labels = criteria.list();
        if (!labels.isEmpty()) {
            return labels.get(0);
        } else {
            return null;
        }
    }

}
