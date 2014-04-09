/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository.impl;

import com.compomics.colims.model.SearchAndValidationSettings;
import org.springframework.stereotype.Repository;

import com.compomics.colims.repository.SearchAndValidationSettingsRepository;

/**
 *
 * @author Niels Hulstaert
 */
@Repository("searchAndValidationSettingsRepository")
public class SearchAndValidationSettingsHibernateRepository extends GenericHibernateRepository<SearchAndValidationSettings, Long> implements SearchAndValidationSettingsRepository {

}
