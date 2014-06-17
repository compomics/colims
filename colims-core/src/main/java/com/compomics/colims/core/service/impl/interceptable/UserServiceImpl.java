/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.service.impl.interceptable;

import java.util.List;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.compomics.colims.core.service.UserService;
import com.compomics.colims.model.User;
import com.compomics.colims.model.enums.DefaultUser;
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
    public User findById(final Long userId) {
        return userRepository.findById(userId);
    }

    @Override
    public void save(final User user) {
        userRepository.save(user);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAllOrderedByUserName();
    }

    @Override
    public void delete(final User user) {
        userRepository.delete(user);
    }

    @Override
    public User findByLoginCredentials(final String name, final String password) {
        User user = userRepository.findByName(name);
        if (user != null) {
            //check the password digest
            if (!user.checkPassword(password)) {
                user = null;
            }
        }
        return user;
    }

    @Override
    public User findByName(final String name) {
        return userRepository.findByName(name);
    }

    @Override
    public void update(final User entity) {
        //attach the user to the new session
        //userRepository.saveOrUpdate(entity);
        userRepository.saveOrUpdate(entity);
    }

    @Override
    public void saveOrUpdate(final User entity) {
        userRepository.saveOrUpdate(entity);
    }

    @Override
    public void fetchAuthenticationRelations(final User user) {
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

    @Override
    public boolean isDefaultUser(final User user) {
        boolean isDefaultUser = false;

        for (DefaultUser defaultUser : DefaultUser.values()) {
            if (user.getName().equals(defaultUser.dbEntry())) {
                isDefaultUser = true;
                break;
            }
        }

        return isDefaultUser;
    }

    @Override
    public String findUserNameById(Long userId) {
        String userName = null;
        
        User foundUser = userRepository.findById(userId);  
        if(foundUser != null){
            userName = foundUser.getName();
        }
        
        return userName;
    }
}
