/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository.impl;

import com.compomics.colims.model.SearchModification;
import com.compomics.colims.model.SearchParameters;
import com.compomics.colims.model.SearchParametersHasModification;
import com.compomics.colims.repository.SearchParametersRepository;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.LongType;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Niels Hulstaert
 */
@Repository("searchParameterRepository")
public class SearchParameterHibernateRepository extends GenericHibernateRepository<SearchParameters, Long> implements SearchParametersRepository {

    @Override
    public List<Long> getConstraintLessSearchParameterIdsForRuns(List<Long> analyticalRunIds) {
        SQLQuery sqlQuery = (SQLQuery) getCurrentSession().getNamedQuery("SearchParameters.getConstraintLessSearchParameterIdsForRuns");
        sqlQuery.setParameterList("ids", analyticalRunIds);
        sqlQuery.addScalar("search_parameters.id", LongType.INSTANCE);

        return sqlQuery.list();
    }

    @Override
    public List<SearchParametersHasModification> fetchSearchModifications(Long searchParametersId) {
        Criteria criteria = getCurrentSession().createCriteria(SearchParametersHasModification.class);

        criteria.add(Restrictions.eq("searchParameter.id", searchParametersId));

        return criteria.list();
    }
}
