package com.compomics.colims.repository.impl;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.compomics.colims.model.InstrumentCvTerm;
import com.compomics.colims.repository.AnalyzerRepository;

/**
 *
 * @author Niels Hulstaert
 */
@Repository("analyzerRepository")
public class AnalyzerHibernateRepository extends GenericHibernateRepository<InstrumentCvTerm, Long> implements AnalyzerRepository {
    
    @Override
    public InstrumentCvTerm findByAccession(final String accession) {
        return findUniqueByCriteria(Restrictions.eq("accession", accession));
    }
        
}
