/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository.impl;

import com.compomics.colims.model.Group;
import com.compomics.colims.repository.GroupRepository;
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
@Repository("groupRepository")
public class GroupHibernateRepository extends GenericHibernateRepository<Group, Long> implements GroupRepository {

    @Override
    public Long countByName(final String name) {
        Criteria criteria = createCriteria(Restrictions.eq("name", name));

        criteria.setProjection(Projections.rowCount());

        return (Long) criteria.uniqueResult();
    }

    @Override
    public List<Group> findAllOrderedByName() {
        Criteria criteria = createCriteria();

        //join fetch roles
        criteria.setFetchMode("roles", FetchMode.JOIN);

        //add order
        criteria.addOrder(Order.asc("name"));

        //return distinct results
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

        return criteria.list();
    }

}
