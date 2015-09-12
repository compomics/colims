/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository.impl;

import com.compomics.colims.model.SearchParameters;
import com.compomics.colims.repository.SearchParametersRepository;
import org.hibernate.SQLQuery;
import org.hibernate.type.LongType;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Niels Hulstaert
 */
@Repository("searchParameterRepository")
public class SearchParameterHibernateRepository extends GenericHibernateRepository<SearchParameters, Long> implements SearchParametersRepository {

    public static final String SEARCH_PARAMETERS_IDS_QUERY = new StringBuilder()
            .append("SELECT ")
            .append("DISTINCT search_parameters.id ")
            .append("FROM search_parameters ")
            .append("LEFT JOIN search_and_validation_settings ON search_and_validation_settings.l_search_parameters_id = search_parameters.id ")
            .append("AND search_and_validation_settings.id NOT IN ")
            .append("( ")
            .append("   SELECT ")
            .append("   s_and_v_s.id ")
            .append("   FROM search_and_validation_settings s_and_v_s ")
            .append("   WHERE s_and_v_s.l_analytical_run_id IN (:ids) ")
            .append(") ")
            .append("WHERE search_and_validation_settings.l_search_parameters_id IS NULL ")
            .append("; ")
            .toString();

    @Override
    public List<Long> getConstraintLessSearchParameterIdsForRuns(List<Long> analyticalRunIds) {
        SQLQuery sqlQuery = getCurrentSession().createSQLQuery(SEARCH_PARAMETERS_IDS_QUERY);
        sqlQuery.setParameterList("ids", analyticalRunIds);
        sqlQuery.addScalar("search_parameters.id", LongType.INSTANCE);

        return sqlQuery.list();
    }
}
