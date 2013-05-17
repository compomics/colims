/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository.impl;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.compomics.colims.model.User;
import com.compomics.colims.repository.UserRepository;

/**
 *
 * @author Niels Hulstaert
 */
@Repository("userRepository")
public class UserHibernateRepository extends GenericHibernateRepository<User, Long> implements UserRepository {
    
    @Override
    public User findByName(final String name) {
        return findUniqueByCriteria(Restrictions.eq("name", name));
    }             
}
