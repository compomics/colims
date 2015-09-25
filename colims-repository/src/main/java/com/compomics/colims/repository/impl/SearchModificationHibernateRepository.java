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

    public static final String SEARCH_MODIFICATION_IDS_QUERY = new StringBuilder()
            .append("SELECT ")
            .append("DISTINCT search_modification.id ")
            .append("FROM search_modification ")
            .append("LEFT JOIN search_params_has_modification ON search_params_has_modification.id = search_modification.id ")
            .append("AND search_params_has_modification.id NOT IN ")
            .append("( ")
            .append("SELECT ")
            .append("s_p_has_mod.id ")
            .append("FROM search_params_has_modification s_p_has_mod ")
            .append("JOIN search_parameters s_p ON s_p.id = s_p_has_mod.l_search_parameters_id ")
            .append("JOIN search_and_validation_settings s_and_v_s ON s_and_v_s.l_search_parameters_id = s_p.id ")
            .append("WHERE s_and_v_s.l_analytical_run_id IN (:ids) ")
            .append(") ")
            .append("WHERE search_params_has_modification.l_search_modification_id IS NULL ")
            .append("; ").toString();

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
        SQLQuery sqlQuery = getCurrentSession().createSQLQuery(SEARCH_MODIFICATION_IDS_QUERY);
        sqlQuery.setParameterList("ids", analyticalRunIds);
        sqlQuery.addScalar("search_modification.id", LongType.INSTANCE);

        return sqlQuery.list();
    }
}
