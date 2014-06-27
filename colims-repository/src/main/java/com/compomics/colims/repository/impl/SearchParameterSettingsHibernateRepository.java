/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository.impl;

import org.springframework.stereotype.Repository;

import com.compomics.colims.model.SearchParameterSettings;
import com.compomics.colims.repository.SearchParameterSettingsRepository;

/**
 *
 * @author Kenneth Verheggen
 */
@Repository("searchParameterSettingsRepository")
public class SearchParameterSettingsHibernateRepository extends GenericHibernateRepository<SearchParameterSettings, Long> implements SearchParameterSettingsRepository {

}
