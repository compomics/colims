package com.compomics.colims.repository.impl;

import com.compomics.colims.model.ProteinAccession;
import com.compomics.colims.repository.ProteinAccessionRepository;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Niels Hulstaert
 */
@Repository("proteinAccessionRepository")
public class ProteinAccessionHibernateRepository extends GenericHibernateRepository<ProteinAccession, Long> implements ProteinAccessionRepository {

    @Override
    public List<ProteinAccession> findByAccession(final String accession) {
        Criteria criteria = createCriteria(Restrictions.eq("accession", accession));
        criteria.setCacheable(true);

        return criteria.list();
    }
}
