package com.compomics.colims.repository.impl;

import com.compomics.colims.model.Protocol;
import com.compomics.colims.repository.ProtocolRepository;
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
@Repository("protocolRepository")
public class ProtocolHibernateRepository extends GenericHibernateRepository<Protocol, Long> implements ProtocolRepository {

    @Override
    public Long countByName(final String name) {
        Criteria criteria = createCriteria(Restrictions.eq("name", name));

        criteria.setProjection(Projections.rowCount());

        return (Long) criteria.uniqueResult();
    }

    @Override
    public List<Protocol> findAllOrderedByName() {
        Criteria criteria = createCriteria();

        //join fetch chemical labels
        criteria.setFetchMode("chemicalLabels", FetchMode.JOIN);

        //add order
        criteria.addOrder(Order.asc("name"));

        //return distinct results
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

        return criteria.list();
    }

}
