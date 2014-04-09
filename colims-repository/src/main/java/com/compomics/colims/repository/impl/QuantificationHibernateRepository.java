/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository.impl;

import com.compomics.colims.model.Quantification;
import com.compomics.colims.repository.QuantificationRepository;
import org.springframework.stereotype.Repository;


/**
 *
 * @author Kenneth Verheggen
 */
@Repository("quantificationRepository")
public class QuantificationHibernateRepository extends GenericHibernateRepository<Quantification, Long> implements QuantificationRepository {
    
}
