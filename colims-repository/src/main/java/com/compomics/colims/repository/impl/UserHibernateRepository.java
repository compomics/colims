/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository.impl;

import com.compomics.colims.model.Group;
import com.compomics.colims.model.User;
import com.compomics.colims.repository.UserRepository;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Niels Hulstaert
 */
@Repository("userRepository")
public class UserHibernateRepository extends GenericHibernateRepository<User, Long> implements UserRepository {

    @Override
    public User findByName(String name) {
        User user = null;
        List<User> users = findByCriteria(Restrictions.eq("name", name));
        if (!users.isEmpty()) {
            user = users.get(0);
        }

        return user;
    }

//    @Override
//    public void fetchAuthenticationRelations(User user) {
//        Query namedQuery = getCurrentSession().getNamedQuery("User.fetchAuthenticationRelations");
//        namedQuery.setParameter("userId", userId);
//        List<User> resultList = namedQuery.list();
//                
//        return resultList.get(0);        
//    }          
}
