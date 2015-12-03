/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository.impl;

import com.compomics.colims.model.User;
import com.compomics.colims.repository.UserRepository;
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
@Repository("userRepository")
public class UserHibernateRepository extends GenericHibernateRepository<User, Long> implements UserRepository {

    @Override
    public Long countByName(final String name) {
        Criteria criteria = createCriteria(Restrictions.eq("name", name));

        criteria.setProjection(Projections.rowCount());

        return (Long) criteria.uniqueResult();
    }

    @Override
    public User findByName(String name) {
        return findUniqueByCriteria(Restrictions.eq("name", name));
    }

    @Override
    public List<User> findAllOrderedByUserName() {
        Criteria criteria = createCriteria();

        //join fetch permissions
        criteria.setFetchMode("groups", FetchMode.JOIN);

        //set order
        criteria.addOrder(Order.asc("name"));

        //return distinct results
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

        return criteria.list();
    }

}
