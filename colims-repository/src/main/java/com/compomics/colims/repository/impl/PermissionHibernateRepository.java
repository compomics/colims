/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository.impl;

import org.springframework.stereotype.Repository;

import com.compomics.colims.model.Permission;
import com.compomics.colims.repository.PermissionRepository;
import java.util.List;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Niels Hulstaert
 */
@Repository("permissionRepository")
public class PermissionHibernateRepository extends GenericHibernateRepository<Permission, Long> implements PermissionRepository {
    
    @Override
    public Permission findByName(final String name) {
        return findUniqueByCriteria(Restrictions.eq("name", name));
    }

    @Override
    public List<Permission> findAllOrderedByName() {
        return createCriteria().addOrder(Order.asc("name")).list();
    }
    
}
