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
        // XXX Consider replacing with 'AnalyticalRun AnalyticalRunRepository#findById(Long)' and call 'AnalyticalRun#getSpectra()'
        Criteria subCriteria = createCriteria().createCriteria("analyticalRun");
        @SuppressWarnings("unchecked")
        List<Spectrum> list = subCriteria.add(Restrictions.eq("id", analyticalRunId)).list();
        return list;
    }
}
