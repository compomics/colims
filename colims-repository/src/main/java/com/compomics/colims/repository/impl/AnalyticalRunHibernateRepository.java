package com.compomics.colims.repository.impl;

import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.Sample;
import com.compomics.colims.repository.AnalyticalRunRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Kenneth Verheggen
 */
@Repository("analyticalRunRepository")
public class AnalyticalRunHibernateRepository extends GenericHibernateRepository<AnalyticalRun, Long> implements AnalyticalRunRepository {

    @Override
    public List<AnalyticalRun> findAnalyticalRunsBySampleId(final Long experimentId) {
        Criteria subCriteria = createCriteria().createCriteria("l_sample_id");
        @SuppressWarnings("unchecked")
        List<AnalyticalRun> list = subCriteria.add(Restrictions.eq("id", experimentId)).list();
        return list;
    }
}
