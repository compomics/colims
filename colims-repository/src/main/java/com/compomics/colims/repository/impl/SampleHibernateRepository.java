package com.compomics.colims.repository.impl;

import com.compomics.colims.model.Sample;
import com.compomics.colims.model.Spectrum;
import org.springframework.stereotype.Repository;

import com.compomics.colims.repository.SampleRepository;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Kenneth Verheggen
 */
@Repository("sampleRepository")
public class SampleHibernateRepository extends GenericHibernateRepository<Sample, Long> implements SampleRepository {

    @Override
    public List<Sample> findSampleByExperimentId(final Long experimentId) {
        Criteria subCriteria = createCriteria().createCriteria("l_experiment_id");
        @SuppressWarnings("unchecked")
        List<Sample> list = subCriteria.add(Restrictions.eq("id", experimentId)).list();
        return list;
    }
}
