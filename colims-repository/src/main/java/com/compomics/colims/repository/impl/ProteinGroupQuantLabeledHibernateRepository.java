/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository.impl;

import com.compomics.colims.model.ProteinGroupQuantLabeled;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import com.compomics.colims.repository.ProteinGroupQuantLabeledRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author demet
 */
@Repository("proteinGroupQuantLabeledRepository")
public class ProteinGroupQuantLabeledHibernateRepository extends GenericHibernateRepository<ProteinGroupQuantLabeled, Long> implements ProteinGroupQuantLabeledRepository{

    @Override
    public ProteinGroupQuantLabeled getProteinGroupQuantLabeledForRun(Long analyticalRunId) {
        Criteria criteria = getCurrentSession().createCriteria(ProteinGroupQuantLabeled.class);

        criteria.add(Restrictions.eq("analyticalRun.id", analyticalRunId));
        criteria.setFirstResult(0);
        criteria.setMaxResults(1);

        List<ProteinGroupQuantLabeled> labels = criteria.list();
        if(!labels.isEmpty()){
            return labels.get(0);
        }
        else {
            return null;
        }
    }

    @Override
    public List<ProteinGroupQuantLabeled> getProteinGroupQuantLabeledForRunAndProteinGroup(Long analyticalRunId, Long proteinGroupId) {
        Criteria criteria = getCurrentSession().createCriteria(ProteinGroupQuantLabeled.class);

        criteria.add(Restrictions.eq("analyticalRun.id", analyticalRunId));
        criteria.add(Restrictions.eq("proteinGroup.id",  proteinGroupId));

        return criteria.list();
    }
    
}
