package com.compomics.colims.repository.impl;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.compomics.colims.model.InstrumentCvTerm;
import com.compomics.colims.model.enums.InstrumentCvProperty;
import com.compomics.colims.repository.InstrumentCvTermRepository;

/**
 *
 * @author Niels Hulstaert
 */
@Repository("instrumentCvTermRepository")
public class InstrumentCvTermHibernateRepository extends GenericHibernateRepository<InstrumentCvTerm, Long> implements InstrumentCvTermRepository {
    
    @Override
    public InstrumentCvTerm findByAccession(final String accession, final InstrumentCvProperty instrumentCvProperty) {
        return findUniqueByCriteria(Restrictions.eq("accession", accession), Restrictions.eq("instrumentCvProperty", instrumentCvProperty));
    }
        
}
