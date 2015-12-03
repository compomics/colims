package com.compomics.colims.repository.impl;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.compomics.colims.model.Protocol;
import com.compomics.colims.repository.ProtocolRepository;
import java.util.List;
import org.hibernate.criterion.Order;

/**
 *
 * @author Niels Hulstaert
 */
@Repository("protocolRepository")
public class ProtocolHibernateRepository extends GenericHibernateRepository<Protocol, Long> implements ProtocolRepository {
    
    @Override
    public Protocol findByName(final String name) {
        return findUniqueByCriteria(Restrictions.eq("name", name));
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
