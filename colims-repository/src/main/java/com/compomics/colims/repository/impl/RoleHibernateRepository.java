/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository.impl;

import org.springframework.stereotype.Repository;

import com.compomics.colims.model.Role;
import com.compomics.colims.repository.RoleRepository;
import java.util.List;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Niels Hulstaert
 */
@Repository("roleRepository")
public class RoleHibernateRepository extends GenericHibernateRepository<Role, Long> implements RoleRepository {
    
    @Override
    public Role findByName(String name) {
        return findUniqueByCriteria(Restrictions.eq("name", name));
    }    

    @Override
    public List<Role> findAllOrderedByName() {
        return createCriteria().addOrder(Order.asc("name")).list();
    }
    
}
