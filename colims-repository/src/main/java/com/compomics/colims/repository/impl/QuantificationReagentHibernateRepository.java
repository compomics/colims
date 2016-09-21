/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository.impl;

import com.compomics.colims.model.QuantificationReagent;
import com.compomics.colims.repository.QuantificationReagentRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author demet
 */
@Repository("quantificationReagentRepository")
public class QuantificationReagentHibernateRepository extends GenericHibernateRepository<QuantificationReagent, Long> implements QuantificationReagentRepository{
    
}
