package com.compomics.colims.repository.impl;

import com.compomics.colims.model.Modification;
import com.compomics.colims.repository.ModificationRepository;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.LongType;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Niels Hulstaert
 */
@Repository("modificationRepository")
public class ModificationHibernateRepository extends GenericHibernateRepository<Modification, Long> implements ModificationRepository {

    public static final String MODIFICATION_IDS_QUERY = "SELECT "
            + "DISTINCT modification.id "
            + "FROM modification "
            + "LEFT JOIN peptide_has_modification ON peptide_has_modification.l_modification_id = modification.id "
            + "AND peptide_has_modification.id NOT IN "
            + "( "
            + "   SELECT " + "   pep_has_mod.id " + "   FROM peptide_has_modification pep_has_mod "
            + "   JOIN peptide pep ON pep.id = pep_has_mod.l_peptide_id "
            + "   JOIN spectrum sp ON sp.id = pep.l_spectrum_id "
            + "   WHERE sp.l_analytical_run_id IN (:ids) "
            + ") "
            + "WHERE peptide_has_modification.l_modification_id IS NULL "
            + "; ";

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
    public Modification findByUtilitiesPtmName(String utilitiesPtmName) {
        List<Modification> modifications = findByCriteria(Restrictions.eq("utilitiesName", utilitiesPtmName));
        if (!modifications.isEmpty()) {
            return modifications.get(0);
        } else {
            return null;
        }
    }

    @Override
    public List<Long> getConstraintLessModificationIdsForRuns(List<Long> analyticalRunIds) {
        SQLQuery sqlQuery = getCurrentSession().createSQLQuery(MODIFICATION_IDS_QUERY);
        sqlQuery.setParameterList("ids", analyticalRunIds);
        sqlQuery.addScalar("modification.id", LongType.INSTANCE);

        return sqlQuery.list();
    }

}
