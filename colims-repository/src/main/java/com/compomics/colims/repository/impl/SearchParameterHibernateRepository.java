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

    public static final String SEARCH_PARAMETERS_IDS_QUERY =
            "SELECT "
            + "DISTINCT search_parameters.id "
            + "FROM search_parameters "
            + "LEFT JOIN search_and_validation_settings ON search_and_validation_settings.l_search_parameters_id = search_parameters.id "
            + "AND search_and_validation_settings.id NOT IN "
            + "( "
            + "   SELECT "
            + "   s_and_v_s.id "
            + "   FROM search_and_validation_settings s_and_v_s "
            + "   WHERE s_and_v_s.l_analytical_run_id IN (:ids) "
            + ") "
            + "WHERE search_and_validation_settings.l_search_parameters_id IS NULL "
            + "; ";

    @Override
    public List<Long> getConstraintLessSearchParameterIdsForRuns(List<Long> analyticalRunIds) {
        SQLQuery sqlQuery = (SQLQuery) getCurrentSession().getNamedQuery("SearchParameters.getConstraintLessSearchParameterIdsForRuns");
        sqlQuery.setParameterList("ids", analyticalRunIds);
        sqlQuery.addScalar("search_parameters.id", LongType.INSTANCE);

        return sqlQuery.list();
    }
}
