package com.compomics.colims.repository.impl;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.compomics.colims.model.Protein;
import com.compomics.colims.repository.ProteinRepository;
import org.hibernate.Criteria;

/**
 *
 * @author Niels Hulstaert
 */
@Repository("proteinRepository")
public class ProteinHibernateRepository extends GenericHibernateRepository<Protein, Long> implements ProteinRepository {
    @Override
    public Protein findByAccession(final String accession) {
        Criteria criteria = createCriteria(Restrictions.eq("accession", accession));
        criteria.setCacheable(true);
        return (Protein) criteria.uniqueResult();
    }
}
