/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository.impl;

import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.SearchParameters;
import com.compomics.colims.repository.SearchParametersRepository;
import org.hibernate.type.LongType;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Niels Hulstaert
 */
@Repository("searchParameterRepository")
public class SearchParameterHibernateRepository extends GenericHibernateRepository<SearchParameters, Long> implements SearchParametersRepository {

    public static final String SEARCH_PARAMETERS_IDS_QUERY = "SELECT DISTINCT search_parameters.id FROM search_parameters"
            + " LEFT JOIN search_and_validation_settings search_and_val_settings ON search_and_val_settings.l_search_parameters_id = search_parameters.id"
            + " WHERE search_and_val_settings.l_analytical_run_id = %1$d";

    @Override
    public List<Long> getSearchParameterIdsForRun(AnalyticalRun analyticalRun) {

        List<Long> searchParametersIds = getCurrentSession()
                .createSQLQuery(String.format(SEARCH_PARAMETERS_IDS_QUERY, analyticalRun.getId()))
                .addScalar("search_parameters.id", LongType.INSTANCE)
                .list();

        return searchParametersIds;
    }
}
