package com.compomics.colims.repository.impl;

import com.compomics.colims.model.Peptide;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.compomics.colims.repository.PeptideRepository;

/**
 *
 * @author Kenneth Verheggen
 */
@Repository("peptideRepository")
public class PeptideHibernateRepository extends GenericHibernateRepository<Peptide, Long> implements PeptideRepository {
   
    @Override
    public List<Peptide> findPeptideBySpectrumId(final Long spectrumId) {
        // XXX Consider replacing with 'AnalyticalRun AnalyticalRunRepository#findById(Long)' and call 'AnalyticalRun#getSpectra()'
        Criteria subCriteria = createCriteria().createCriteria("l_spectrum_id");
        @SuppressWarnings("unchecked")
        List<Peptide> list = subCriteria.add(Restrictions.eq("id", spectrumId)).list();
        return list;
    }

}
