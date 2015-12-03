/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository.impl;

import com.compomics.colims.model.Role;
import com.compomics.colims.repository.RoleRepository;
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
@Repository("roleRepository")
public class RoleHibernateRepository extends GenericHibernateRepository<Role, Long> implements RoleRepository {

    @Override
    public Long countByName(final String name) {
        Criteria criteria = createCriteria(Restrictions.eq("name", name));

        criteria.setProjection(Projections.rowCount());

        return (Long) criteria.uniqueResult();
    }

    @Override
    public List<Role> findAllOrderedByName() {
        Criteria criteria = createCriteria();

        //join fetch permissions
        criteria.setFetchMode("permissions", FetchMode.JOIN);

        //set order
        criteria.addOrder(Order.asc("name"));

        //return distinct results
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

        return criteria.list();
    }

}
