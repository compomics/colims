/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository.impl;

import com.compomics.colims.model.Permission;
import com.compomics.colims.repository.PermissionRepository;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Niels Hulstaert
 */
@Repository("permissionRepository")
public class PermissionHibernateRepository extends GenericHibernateRepository<Permission, Long> implements PermissionRepository {

    @Override
    public Long countByName(final String name) {
        Criteria criteria = createCriteria(Restrictions.eq("name", name));

        criteria.setProjection(Projections.rowCount());

        return (Long) criteria.uniqueResult();
    }

    @Override
    public List<Permission> findAllOrderedByName() {
        return createCriteria().addOrder(Order.asc("name")).list();
    }

}
