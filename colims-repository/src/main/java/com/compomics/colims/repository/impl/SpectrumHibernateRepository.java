package com.compomics.colims.repository.impl;

import com.compomics.colims.model.AnalyticalRun;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.compomics.colims.model.Spectrum;
import com.compomics.colims.repository.SpectrumRepository;
import org.hibernate.criterion.Projections;

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

    @Override
    public Long countSpectraByAnalyticalRun(AnalyticalRun analyticalRun) {
        Long numberOfSpectra = 0L;
        
        Criteria criteria = createCriteria().add(Restrictions.eq("analyticalRun", analyticalRun));
        numberOfSpectra = ((Number)criteria.setProjection(Projections.rowCount()).uniqueResult()).longValue();
        
        return numberOfSpectra;
    }
}
