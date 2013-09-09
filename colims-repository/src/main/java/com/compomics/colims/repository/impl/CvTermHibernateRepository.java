package com.compomics.colims.repository.impl;

import com.compomics.colims.model.CvTerm;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.compomics.colims.model.enums.CvTermProperty;
import com.compomics.colims.repository.CvTermRepository;
import java.util.List;

/**
 *
 * @author Niels Hulstaert
 */
@Repository("cvTermRepository")
public class CvTermHibernateRepository extends GenericHibernateRepository<CvTerm, Long> implements CvTermRepository {
    
    @Override
    public CvTerm findByAccession(final String accession, final CvTermProperty cvTermProperty) {
        return findUniqueByCriteria(Restrictions.eq("accession", accession), Restrictions.eq("cvTermProperty", cvTermProperty));
    }

    @Override
    public List<CvTerm> findByCvTermProperty(CvTermProperty cvTermProperty) {
        return findByCriteria(Restrictions.eq("cvTermProperty", cvTermProperty));
    }
        
}
