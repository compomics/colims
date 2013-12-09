/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository.impl;

import com.compomics.colims.model.Quantification;
import org.springframework.stereotype.Repository;


/**
 *
 * @author Kenneth Verheggen
 */
@Repository("quantificationRepository")
public class QuantificationRepository extends GenericHibernateRepository<Quantification, Long> implements com.compomics.colims.repository.QuantificationRepository {

}
