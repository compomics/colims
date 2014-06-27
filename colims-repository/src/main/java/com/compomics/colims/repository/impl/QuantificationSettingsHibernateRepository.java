/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository.impl;

import com.compomics.colims.model.QuantificationSettings;
import com.compomics.colims.repository.QuantificationSettingsRepository;
import org.springframework.stereotype.Repository;


/**
 *
 * @author Niels Hulstaert
 */
@Repository("quantificationSettingsRepository")
public class QuantificationSettingsHibernateRepository extends GenericHibernateRepository<QuantificationSettings, Long> implements QuantificationSettingsRepository {

}
