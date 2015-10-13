package com.compomics.colims.repository.impl;

import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.ProteinAccession;
import com.compomics.colims.model.ProteinGroup;
import com.compomics.colims.repository.ProteinAccessionRepository;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
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

    @Override
    public List<String> getAccessionsForProteinGroup(ProteinGroup proteinGroup) {
        SQLQuery sqlQuery = (SQLQuery) getCurrentSession().getNamedQuery("ProteinAccession.getAccessionsForProteinGroup");

        return sqlQuery.setParameter("proteinGroupId", proteinGroup.getId()).list();
    }

    @Override
    public List<String> getProteinAccessionsForPeptide(Peptide peptide) {
        SQLQuery sqlQuery = (SQLQuery) getCurrentSession().getNamedQuery("ProteinAccession.getAccessionsForPeptide");

        return sqlQuery.setParameter("peptideId", peptide.getId()).list();
    }
}
