/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository.impl;

import com.compomics.colims.model.QuantificationParameterSettings;
import org.springframework.stereotype.Repository;

import com.compomics.colims.repository.QuantificationParameterSettingsRepository;

/**
 *
 * @author Kenneth Verheggen
 */
@Repository("quantificationParameterSettingsRepository")
public class QuantificationParameterSettingsHibernateRepository extends GenericHibernateRepository<QuantificationParameterSettings, Long> implements QuantificationParameterSettingsRepository {

}
