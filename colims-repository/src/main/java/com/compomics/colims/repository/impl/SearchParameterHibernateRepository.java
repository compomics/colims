/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository.impl;

import com.compomics.colims.model.SearchCvParam;
import com.compomics.colims.model.SearchParameters;
import com.compomics.colims.model.SearchParametersHasModification;
import com.compomics.colims.model.comparator.CvParamNameComparator;
import com.compomics.colims.model.comparator.SearchParameterHasModNameComparator;
import com.compomics.colims.repository.SearchParametersRepository;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.LongType;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

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

        criteria.add(Restrictions.eq("searchParameters.id", searchParametersId));

        return criteria.list();
    }

    @Override
    public List<SearchParameters> findByExample(SearchParameters exampleInstance) {
        List<SearchParameters> searchParameterses = super.findByExample(exampleInstance);

        Iterator<SearchParameters> iterator = searchParameterses.iterator();
        //do some additional comparisons
        while (iterator.hasNext()) {
            SearchParameters searchParameters = iterator.next();
            //check search type
            if (!Objects.equals(exampleInstance.getSearchType(), searchParameters.getSearchType())) {
                iterator.remove();
                continue;
            }

            /**
             * Check search modifications equality.
             */
            //check the peptideHasModifications size
            if (exampleInstance.getSearchParametersHasModifications().size() != searchParameters.getSearchParametersHasModifications().size()) {
                iterator.remove();
                continue;
            }

            //sort the lists of SearchParametersHasModifications instances
            SearchParameterHasModNameComparator modificationNameComparator = new SearchParameterHasModNameComparator();
            //create a temporary list to avoid changes in the database
            List<SearchParametersHasModification> sortedList = searchParameters.getSearchParametersHasModifications();
            (exampleInstance.getSearchParametersHasModifications()).sort(modificationNameComparator);
            sortedList.sort(modificationNameComparator);

            if (!exampleInstance.getSearchParametersHasModifications().equals(sortedList)) {
                iterator.remove();
                continue;
            }

            /**
             * Check additional parameters equality.
             */
            //check the additionalCvParams size
            if (exampleInstance.getAdditionalCvParams().size() != searchParameters.getAdditionalCvParams().size()) {
                iterator.remove();
                continue;
            }

            //sort the lists of AdditionalCvParams instances
            CvParamNameComparator nameComparator = new CvParamNameComparator();
            //create a temporary list to avoid changes in the database
            List<SearchCvParam> sortedList2 = searchParameters.getAdditionalCvParams();
            (exampleInstance.getAdditionalCvParams()).sort(nameComparator);
            sortedList2.sort(nameComparator);

            if (!exampleInstance.getAdditionalCvParams().equals(sortedList2)) {
                iterator.remove();
            }
        }

        return searchParameterses;
    }
}
