package com.compomics.colims.repository.impl;

import com.compomics.colims.model.FastaDb;
import com.compomics.colims.model.SearchAndValidationSettings;
import com.compomics.colims.model.SearchSettingsHasFastaDb;
import com.compomics.colims.model.enums.FastaDbType;
import com.compomics.colims.repository.FastaDbRepository;

import java.util.HashMap;

import java.util.List;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Niels Hulstaert
 */
@Repository("fastaDbRepository")
public class FastaDbHibernateRepository extends GenericHibernateRepository<FastaDb, Long> implements FastaDbRepository {

    @Override
    public List<FastaDb> findByFastaDbType(List<FastaDbType> fastaDbTypes) {
        Query query = getCurrentSession().getNamedQuery("FastaDb.findByFastaDbType");

        query.setParameterList("fastaDbTypeOrdinals", fastaDbTypes);

        return query.list();
    }

    @Override
    public List<String> getAllParseRules() {
        Criteria criteria = getCurrentSession().createCriteria(FastaDb.class, "fastaDb");
        
        criteria.setProjection(Projections.distinct(Projections.property("headerParseRule")));
        
        return criteria.list();
    }

    @Override
    public Map<FastaDb, FastaDbType> findBySearchAndValidationSettings(SearchAndValidationSettings searchAndValidationSettings) {
        Map<FastaDb, FastaDbType> fastaDbs = new HashMap<>();
        
        Criteria criteria = getCurrentSession().createCriteria(SearchSettingsHasFastaDb.class);
        
        criteria.add(Restrictions.eq("searchAndValidationSettings.id", searchAndValidationSettings.getId()));
        
        List<SearchSettingsHasFastaDb> searchSettingsHasFastaDbs = criteria.list();
        
        searchSettingsHasFastaDbs.forEach(searchSettingsHasFastaDb -> {
            fastaDbs.put(searchSettingsHasFastaDb.getFastaDb(), searchSettingsHasFastaDb.getFastaDbType());
        });
        
        return fastaDbs;
    }

}
