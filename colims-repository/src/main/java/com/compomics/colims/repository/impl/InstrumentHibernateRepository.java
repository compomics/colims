package com.compomics.colims.repository.impl;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.compomics.colims.model.Instrument;
import com.compomics.colims.repository.InstrumentRepository;
import java.util.List;
import org.hibernate.criterion.Order;

/**
 *
 * @author Niels Hulstaert
 */
@Repository("instrumentRepository")
public class InstrumentHibernateRepository extends GenericHibernateRepository<Instrument, Long> implements InstrumentRepository {
    
    @Override
    public Instrument findByName(final String name) {
        return findUniqueByCriteria(Restrictions.eq("name", name));
    }    

    @Override
    public List<Instrument> findAllOrderedByName() {
        return createCriteria().addOrder(Order.asc("name")).list();
    }
    
}
