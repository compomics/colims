package com.compomics.colims.repository.impl;

import com.compomics.colims.model.QuantificationEngine;
import com.compomics.colims.model.enums.QuantificationEngineType;
import com.compomics.colims.repository.QuantificationEngineRepository;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;


/**
 *
 * @author Niels Hulstaert
 */
@Repository("quantificationEngineRepository")
public class QuantificationEngineHibernateRepository extends GenericHibernateRepository<QuantificationEngine, Long> implements QuantificationEngineRepository {

    @Override
    public QuantificationEngine findByNameAndVersion(QuantificationEngineType quantificationEngineType, String version) {        
        return findUniqueByCriteria(Restrictions.eq("quantificationEngineType", quantificationEngineType), Restrictions.eq("version", version));
    }
    
}
