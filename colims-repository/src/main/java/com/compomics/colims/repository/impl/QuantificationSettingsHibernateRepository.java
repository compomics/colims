/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository.impl;

import com.compomics.colims.model.QuantificationSettings;
import com.compomics.colims.repository.QuantificationSettingsRepository;
import java.util.List;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;


/**
 *
 * @author Niels Hulstaert
 */
@Repository("quantificationSettingsRepository")
public class QuantificationSettingsHibernateRepository extends GenericHibernateRepository<QuantificationSettings, Long> implements QuantificationSettingsRepository {

    @Override
    public QuantificationSettings findbyAnalyticalRunId(Long analyticalRunId) {
        Query query = getCurrentSession().getNamedQuery("QuantificationSettings.findByAnalyticalRunId");

        query.setLong("analyticalRunId", analyticalRunId);

        List<QuantificationSettings> quantificationSettingses = query.list();
        if (!quantificationSettingses.isEmpty()) {
            return quantificationSettingses.get(0);
        } else {
            return null;
        }
    }
}
