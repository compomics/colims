package com.compomics.colims.repository.impl;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.compomics.colims.model.Protein;
import com.compomics.colims.repository.ProteinRepository;

/**
 *
 * @author Niels Hulstaert
 */
@Repository("proteinRepository")
public class ProteinHibernateRepository extends GenericHibernateRepository<Protein, Long> implements ProteinRepository {
    @Override
    public Protein findByAccession(final String accession) {
        return findUniqueByCriteria(Restrictions.eq("accession", accession));
    }
}
