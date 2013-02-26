package com.compomics.colims.repository.impl;

import java.util.List;

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
        // XXX An alternative GHR method 'getByCriteria' that calls Criteria#uniqueResult() would make this a one-liner:
        // return getByCriteria(...)
        // TODO With this alternative implementation we can remove the hbm/protein.hbm.xml resource file entirely
        List<Protein> list = findByCriteria(Restrictions.eq("accession", accession));
        return list.isEmpty() ? null : list.get(0);
    }
}
