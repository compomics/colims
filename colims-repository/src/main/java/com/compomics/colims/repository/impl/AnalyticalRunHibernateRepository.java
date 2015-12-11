package com.compomics.colims.repository.impl;

import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.repository.AnalyticalRunRepository;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Niels Hulstaert
 */
@Repository("analyticalRunRepository")
public class AnalyticalRunHibernateRepository extends GenericHibernateRepository<AnalyticalRun, Long> implements AnalyticalRunRepository {

    @Override
    public List<AnalyticalRun> findBySampleId(Long sampleId) {
        return createCriteria(Restrictions.eq("sample.id", sampleId)).list();
    }

    @Override
    public void saveOrUpdate(AnalyticalRun analyticalRun) {
        getCurrentSession().saveOrUpdate(analyticalRun);
    }
}