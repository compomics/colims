package com.compomics.colims.repository.impl;

import com.compomics.colims.model.TypedCvTerm;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.compomics.colims.model.enums.CvTermType;
import com.compomics.colims.repository.CvTermRepository;
import java.util.List;

/**
 *
 * @author Niels Hulstaert
 */
@Repository("cvTermRepository")
public class CvTermHibernateRepository extends GenericHibernateRepository<TypedCvTerm, Long> implements CvTermRepository {
    @Override
    public TypedCvTerm findByAccession(final String accession, final CvTermType cvTermType) {
        return findUniqueByCriteria(Restrictions.eq("accession", accession), Restrictions.eq("cvTermType", cvTermType));
    }

    @Override
    public List<TypedCvTerm> findByCvTermType(final CvTermType cvTermType) {
        return findByCriteria(Restrictions.eq("cvTermType", cvTermType));
    }
}
