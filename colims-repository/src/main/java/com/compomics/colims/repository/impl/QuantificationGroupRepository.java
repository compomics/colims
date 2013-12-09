/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository.impl;

import com.compomics.colims.model.QuantificationGroup;
import org.springframework.stereotype.Repository;


/**
 *
 * @author Kenneth Verheggen
 */
@Repository("quantificationGroupRepository")
public class QuantificationGroupRepository extends GenericHibernateRepository<QuantificationGroup, Long> implements com.compomics.colims.repository.QuantificationGroupRepository {

}
