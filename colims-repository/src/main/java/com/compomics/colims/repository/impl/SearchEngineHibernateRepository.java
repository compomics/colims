package com.compomics.colims.repository.impl;

import com.compomics.colims.model.SearchEngine;
import com.compomics.colims.model.enums.SearchEngineType;
import com.compomics.colims.repository.SearchEngineRepository;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;


/**
 *
 * @author Niels Hulstaert
 */
@Repository("searchEngineRepository")
public class SearchEngineHibernateRepository extends GenericHibernateRepository<SearchEngine, Long> implements SearchEngineRepository {

    @Override
    public SearchEngine findByNameAndVersion(SearchEngineType searchEngineType, String version) {        
        return findUniqueByCriteria(Restrictions.eq("searchEngineType", searchEngineType), Restrictions.eq("version", version));
    }
    
}
