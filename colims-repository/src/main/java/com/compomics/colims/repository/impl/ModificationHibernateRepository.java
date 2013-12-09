package com.compomics.colims.repository.impl;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.compomics.colims.model.Modification;
import com.compomics.colims.repository.ModificationRepository;

/**
 *
 * @author Niels Hulstaert
 */
@Repository("modificationRepository")
public class ModificationHibernateRepository extends GenericHibernateRepository<Modification, Long> implements ModificationRepository {
    
    @Override
    public Modification findByName(final String name) {
        return findUniqueByCriteria(Restrictions.eq("name", name));
    }
    
    @Override
    public Modification findByAccession(final String accession) {
        return findUniqueByCriteria(Restrictions.eq("accession", accession));
    }
}
