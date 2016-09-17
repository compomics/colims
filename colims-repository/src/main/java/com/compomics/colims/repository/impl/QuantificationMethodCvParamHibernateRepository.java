/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository.impl;

import com.compomics.colims.model.QuantificationMethodCvParam;
import org.springframework.stereotype.Repository;

import com.compomics.colims.repository.QuantificationMethodCvParamRepository;

/**
 *
 * @author Kenneth Verheggen
 */
@Repository("quantificationParametersRepository")
public class QuantificationMethodCvParamHibernateRepository extends GenericHibernateRepository<QuantificationMethodCvParam, Long> implements QuantificationMethodCvParamRepository {

}
