package com.compomics.colims.repository.impl;

import com.compomics.colims.model.SearchEngine;
import com.compomics.colims.model.enums.SearchEngineType;
import com.compomics.colims.repository.SearchEngineRepository;
import java.util.List;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Niels Hulstaert
 */
@Repository("searchEngineRepository")
public class SearchEngineHibernateRepository extends GenericHibernateRepository<SearchEngine, Long> implements SearchEngineRepository {

    @Override
    public SearchEngine findByType(SearchEngineType searchEngineType) {
        List<SearchEngine> searchEngines = findByCriteria(Restrictions.eq("searchEngineType", searchEngineType));
        if (!searchEngines.isEmpty()) {
            return searchEngines.get(0);
        } else {
            return null;
        }
    }

    @Override
    public SearchEngine findByTypeAndVersion(SearchEngineType searchEngineType, String version) {
        return findUniqueByCriteria(Restrictions.eq("searchEngineType", searchEngineType), Restrictions.eq("version", version));
    }

}
