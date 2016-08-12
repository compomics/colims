package com.compomics.colims.repository.impl;

import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.AnalyticalRunBinaryFile;
import com.compomics.colims.repository.AnalyticalRunRepository;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 * @author Niels Hulstaert
 */
@Repository("analyticalRunRepository")
public class AnalyticalRunHibernateRepository extends GenericHibernateRepository<AnalyticalRun, Long> implements AnalyticalRunRepository {

    @Override
    public List<AnalyticalRun> findBySampleId(Long sampleId) {
        Query query = getCurrentSession().getNamedQuery("AnalyticalRun.findBySampleId");

        query.setLong("sampleId", sampleId);

        return query.list();
    }

    @Override
    public void saveOrUpdate(AnalyticalRun analyticalRun) {
        getCurrentSession().saveOrUpdate(analyticalRun);
    }

    @Override
    public List<AnalyticalRunBinaryFile> fetchBinaryFiles(Long analyticalRunId) {
        Criteria criteria = getCurrentSession().createCriteria(AnalyticalRunBinaryFile.class);

        criteria.add(Restrictions.eq("analyticalRun.id", analyticalRunId));

        return criteria.list();
    }
}