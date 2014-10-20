/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository.impl;

import com.compomics.colims.model.QuantificationParameters;
import org.springframework.stereotype.Repository;

import com.compomics.colims.repository.QuantificationParametersRepository;

/**
 *
 * @author Kenneth Verheggen
 */
@Repository("quantificationParametersRepository")
public class QuantificationParametersHibernateRepository extends GenericHibernateRepository<QuantificationParameters, Long> implements QuantificationParametersRepository {

}
