package com.compomics.colims.repository.impl;

import com.compomics.colims.model.QuantificationEngine;
import com.compomics.colims.model.enums.QuantificationEngineType;
import com.compomics.colims.repository.QuantificationEngineRepository;
import java.util.List;
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

    @Override
    public QuantificationEngine findByType(QuantificationEngineType quantificationEngineType) {
        List<QuantificationEngine> quantificationEngines = findByCriteria(Restrictions.eq("quantificationEngineType", quantificationEngineType));
        if (!quantificationEngines.isEmpty()) {
            return quantificationEngines.get(0);
        } else {
            return null;
        }
    }
    
}
