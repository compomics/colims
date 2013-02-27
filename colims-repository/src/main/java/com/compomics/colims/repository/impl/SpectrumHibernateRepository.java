package com.compomics.colims.repository.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.compomics.colims.model.Spectrum;
import com.compomics.colims.repository.SpectrumRepository;

/**
 *
 * @author Niels Hulstaert
 */
@Repository("spectrumRepository")
public class SpectrumHibernateRepository extends GenericHibernateRepository<Spectrum, Long> implements SpectrumRepository {
    @Override
    public List<Spectrum> findSpectraByAnalyticalRunId(final Long analyticalRunId) {
        Criteria subCriteria = createCriteria().createCriteria("analyticalRun");
        return subCriteria.add(Restrictions.eq("id", analyticalRunId)).list();
    }
}
