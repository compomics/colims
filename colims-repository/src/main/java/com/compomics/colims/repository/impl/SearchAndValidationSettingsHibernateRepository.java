/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository.impl;

import com.compomics.colims.model.SearchAndValidationSettings;
import org.springframework.stereotype.Repository;

import com.compomics.colims.repository.SearchAndValidationSettingsRepository;
import java.util.List;
import org.hibernate.Query;

/**
 *
 * @author Niels Hulstaert
 */
@Repository("searchAndValidationSettingsRepository")
public class SearchAndValidationSettingsHibernateRepository extends GenericHibernateRepository<SearchAndValidationSettings, Long> implements SearchAndValidationSettingsRepository {

    @Override
    public SearchAndValidationSettings findbyAnalyticalRunId(Long analyticalRunId) {
        Query query = getCurrentSession().getNamedQuery("SearchAndValidationSettings.findByAnalyticalRunId");

        query.setLong("analyticalRunId", analyticalRunId);

        List<SearchAndValidationSettings> searchAndValidationSettingses = query.list();
        if (!searchAndValidationSettingses.isEmpty()) {
            return searchAndValidationSettingses.get(0);
        } else {
            return null;
        }
    }

}
