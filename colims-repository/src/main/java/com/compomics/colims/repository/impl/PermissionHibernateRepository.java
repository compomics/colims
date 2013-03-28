/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository.impl;

import com.compomics.colims.model.Group;
import org.springframework.stereotype.Repository;

import com.compomics.colims.model.Permission;
import com.compomics.colims.repository.PermissionRepository;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Niels Hulstaert
 */
@Repository("permissionRepository")
public class PermissionHibernateRepository extends GenericHibernateRepository<Permission, Long> implements PermissionRepository {
    
    @Override
    public Permission findByName(String name) {
        return findUniqueByCriteria(Restrictions.eq("name", name));
    }
    
}
