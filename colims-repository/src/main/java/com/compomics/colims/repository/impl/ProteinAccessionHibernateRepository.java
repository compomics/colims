package com.compomics.colims.repository.impl;

import com.compomics.colims.model.ProteinGroup;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.LongType;
import org.springframework.stereotype.Repository;

import com.compomics.colims.model.ProteinAccession;
import com.compomics.colims.repository.ProteinAccessionRepository;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.Criteria;

/**
 *
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
    public List<ProteinAccession> getAccessionsForProteinGroup(ProteinGroup proteinGroup) {
        List<ProteinAccession> proteinAccessions = new ArrayList<>();

        String query = "SELECT DISTINCT protein_accession.id FROM protein_accession"
            + " LEFT JOIN protein_group_has_protein ON protein_accession.l_protein_id = protein_group_has_protein.l_protein_id"
            + " WHERE protein_group_has_protein.l_protein_group_id = " + proteinGroup.getId();

        List<Long> idList = getCurrentSession()
            .createSQLQuery(query)
            .addScalar("protein_accession.id", LongType.INSTANCE)
            .list();

        if (idList.size() > 0) {
            proteinAccessions = createCriteria().add(Restrictions.in("id", idList)).list();
        }

        return proteinAccessions;
    }

}
