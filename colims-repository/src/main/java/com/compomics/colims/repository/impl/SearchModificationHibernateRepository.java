package com.compomics.colims.repository.impl;

import com.compomics.colims.model.SearchModification;
import com.compomics.colims.repository.SearchModificationRepository;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.LongType;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Niels Hulstaert
 */
@Repository("searchModificationRepository")
public class SearchModificationHibernateRepository extends GenericHibernateRepository<SearchModification, Long> implements SearchModificationRepository {

    @Override
    public SearchModification findByName(final String name) {
        List<SearchModification> modifications = findByCriteria(Restrictions.eq("name", name));
        if (!modifications.isEmpty()) {
            return modifications.get(0);
        } else {
            return null;
        }
    }

    @Override
    public SearchModification findByAccession(final String accession) {
        Criteria criteria = createCriteria(Restrictions.eq("accession", accession));
        criteria.setCacheable(true);
        return (SearchModification) criteria.uniqueResult();
    }

    @Override
    public SearchModification findByUtilitiesPtmName(String utilitiesPtmName) {
        List<SearchModification> modifications = findByCriteria(Restrictions.eq("utilitiesName", utilitiesPtmName));
        if (!modifications.isEmpty()) {
            return modifications.get(0);
        } else {
            return null;
        }
    }

    @Override
    public List<Long> getConstraintLessSearchModificationIdsForRuns(List<Long> analyticalRunIds) {
        SQLQuery sqlQuery = (SQLQuery) getCurrentSession().getNamedQuery("SearchModification.getConstraintLessSearchModificationIdsForRuns");
        sqlQuery.setParameterList("ids", analyticalRunIds);
        sqlQuery.addScalar("search_modification.id", LongType.INSTANCE);

        return sqlQuery.list();
    }
}
