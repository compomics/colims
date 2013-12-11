/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.service.impl;

import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.LockOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.compomics.colims.core.service.UserService;
import com.compomics.colims.model.User;
import com.compomics.colims.repository.UserRepository;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;

/**
 *
 * @author Niels Hulstaert
 */
@Service("userService")
@Transactional
public class UserServiceImpl implements UserService {
    private static final Logger LOGGER = Logger.getLogger(UserServiceImpl.class);
    
    @Autowired
    private UserRepository userRepository;

    @Override
    public User findById(Long userId) {
        return userRepository.findById(userId);
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAllOrderedByUserName();
    }

    @Override
    public void delete(User user) {
        userRepository.delete(user);
    }

    @Override
    public User findByLoginCredentials(String name, String password) {
        User user = userRepository.findByName(name);
        if (user != null) {
            if (user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public User findByName(String name) {
        return userRepository.findByName(name);
    }

    @Override
    public void update(User entity) {
        //attach the user to the new session
        //userRepository.saveOrUpdate(entity);
        userRepository.saveOrUpdate(entity);
    }

    @Override
    public void saveOrUpdate(User entity) {
        userRepository.saveOrUpdate(entity);
    }

    @Override
    public void fetchAuthenticationRelations(User user) {
        try {
            //attach the user to the new session
            userRepository.saveOrUpdate(user);
            if (!Hibernate.isInitialized(user.getGroups())) {
                Hibernate.initialize(user.getGroups());
            }
        } catch (HibernateException hbe) {
            LOGGER.error(hbe, hbe.getCause());
        }
    }

    @Override
    public long countAll() {
        return userRepository.countAll();
    }
    
}
