package com.compomics.colims.repository.impl;

import java.util.List;

import org.hibernate.Query;
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
    public Protein findByAccession(String accession) {
        Query namedQuery = getCurrentSession().getNamedQuery("Protein.findByAccession");
        namedQuery.setParameter("accession", accession);
        List<Protein> resultList = namedQuery.list();
        if (!resultList.isEmpty()) {
            return resultList.get(0);
        } else {
            return null;
        }
    }
}
