/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository.impl;

import com.compomics.colims.model.QuantificationGroup;
import com.compomics.colims.repository.QuantificationGroupRepository;
import org.springframework.stereotype.Repository;


/**
 *
 * @author Kenneth Verheggen
 */
@Repository("quantificationGroupRepository")
public class QuantificationGroupHibernateRepository extends GenericHibernateRepository<QuantificationGroup, Long> implements QuantificationGroupRepository {

}
