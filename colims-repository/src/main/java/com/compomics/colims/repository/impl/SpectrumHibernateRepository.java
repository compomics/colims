package com.compomics.colims.repository.impl;

import com.compomics.colims.model.Instrument;
import com.compomics.colims.model.Spectrum;
import com.compomics.colims.repository.SpectrumRepository;
import java.util.List;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Niels Hulstaert
 */
@Repository("spectrumRepository")
public class SpectrumHibernateRepository extends GenericHibernateRepository<Spectrum, Long> implements SpectrumRepository {

    @Override
    public List<Spectrum> findSpectraByAnalyticalRunId(Long analyticalRunId) {
        Query namedQuery = getCurrentSession().getNamedQuery("Spectrum.findByAnalyticalRunId");
        namedQuery.setParameter("analyticalRunId", analyticalRunId);
        List<Spectrum> resultList = namedQuery.list();
        if (!resultList.isEmpty()) {
            return resultList;
        } else {
            return null;
        }
    }
}
