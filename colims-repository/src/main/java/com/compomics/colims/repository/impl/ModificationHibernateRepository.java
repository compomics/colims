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
        SQLQuery sqlQuery = (SQLQuery) getCurrentSession().getNamedQuery("Modification.getConstraintLessModificationIdsForRuns");
        sqlQuery.setParameterList("ids", analyticalRunIds);
        sqlQuery.addScalar("modification.id", LongType.INSTANCE);

        return sqlQuery.list();
    }

}
