/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository.impl;

import org.springframework.stereotype.Repository;

import com.compomics.colims.model.SearchParameters;
import com.compomics.colims.repository.SearchParametersRepository;

/**
 *
 * @author Kenneth Verheggen
 */
@Repository("searchParameterRepository")
public class SearchParameterHibernateRepository extends GenericHibernateRepository<SearchParameters, Long> implements SearchParametersRepository {

}
