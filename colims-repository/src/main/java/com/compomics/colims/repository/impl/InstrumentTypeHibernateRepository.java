package com.compomics.colims.repository.impl;

import org.springframework.stereotype.Repository;

import com.compomics.colims.model.InstrumentType;
import com.compomics.colims.repository.InstrumentTypeRepository;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Niels Hulstaert
 */
@Repository("instrumentTypeRepository")
public class InstrumentTypeHibernateRepository extends GenericHibernateRepository<InstrumentType, Long> implements InstrumentTypeRepository {
          
    @Override
    public InstrumentType findByName(final String name) {
        return findUniqueByCriteria(Restrictions.eq("name", name));
    }  
    
}
