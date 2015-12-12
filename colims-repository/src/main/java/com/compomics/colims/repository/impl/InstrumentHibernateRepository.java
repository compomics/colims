package com.compomics.colims.repository.impl;

import com.compomics.colims.model.Instrument;
import com.compomics.colims.repository.InstrumentRepository;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Niels Hulstaert
 */
@Repository("instrumentRepository")
public class InstrumentHibernateRepository extends GenericHibernateRepository<Instrument, Long> implements InstrumentRepository {

    @Override
    public Long countByName(final Instrument instrument) {
        Criteria criteria = createCriteria(Restrictions.eq("name", instrument.getName()));

        //in case of an existing instrument, exclude it
        if (instrument.getId() != null) {
            criteria.add(Restrictions.ne("id", instrument.getId()));
        }

        criteria.setProjection(Projections.rowCount());

        return (Long) criteria.uniqueResult();
    }

    @Override
    public List<Instrument> findAllOrderedByName() {
        Criteria criteria = createCriteria();

        //join fetch permissions
        criteria.setFetchMode("analyzers", FetchMode.JOIN);

        //set order
        criteria.addOrder(Order.asc("name"));

        //return distinct results
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

        return criteria.list();
    }

}
