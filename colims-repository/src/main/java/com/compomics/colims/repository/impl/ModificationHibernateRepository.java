package com.compomics.colims.repository.impl;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.compomics.colims.model.Modification;
import com.compomics.colims.repository.ModificationRepository;
import java.util.List;
import org.hibernate.Criteria;

/**
 *
 * @author Niels Hulstaert
 */
@Repository("modificationRepository")
public class ModificationHibernateRepository extends GenericHibernateRepository<Modification, Long> implements ModificationRepository {

    @Override
    public Modification findByName(final String name) {
        List<Modification> modifications = findByCriteria(Restrictions.eq("name", name));
        if (!modifications.isEmpty()) {
            return modifications.get(0);
        } else {
            return null;
        }
    }

    @Override
    public Modification findByAccession(final String accession) {
        Criteria criteria = createCriteria(Restrictions.eq("accession", accession));
        criteria.setCacheable(true);
        return (Modification) criteria.uniqueResult();
    }

    @Override
    public Modification findByAlternativeAccession(String alternativeAccession) {
        List<Modification> modifications = findByCriteria(Restrictions.eq("alternativeAccession", alternativeAccession));
        if (!modifications.isEmpty()) {
            return modifications.get(0);
        } else {
            return null;
        }
    }

}
