/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.service.impl;

import com.compomics.colims.model.User;
import com.compomics.colims.repository.UserRepository;
import com.compomics.colims.core.service.UserService;
import com.compomics.colims.model.Group;
import com.compomics.colims.model.UserHasGroup;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.LockOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Niels Hulstaert
 */
@Service("userService")
@Transactional
public class UserServiceImpl implements UserService {

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
        return userRepository.findAll();
    }

    @Override
    public void delete(User user) {
        userRepository.save(user);
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
        userRepository.update(entity);
    }

    @Override
    public void saveOrUpdate(User entity) {
        userRepository.saveOrUpdate(entity);
    }

    @Override
    public void fetchAuthenticationRelations(User user) {
        //attach to user to the current session and fetch the children
        userRepository.lock(user, LockOptions.NONE);
        if (!Hibernate.isInitialized(user.getUserHasGroups())) {
            Hibernate.initialize(user.getUserHasGroups());
        }
    }

    @Override
    public void saveUser(User user, List<Group> addedGroups) {
    }

    @Override
    public void updateUser(User user, List<Group> addedGroups) {
    }

    private void saveNewGroupRelations(User user, List<Group> addedGroups) {
        List<Group> currentGroups = user.getGroups();
        for (Group addedGroup : addedGroups) {
            if (!currentGroups.contains(addedGroup)) {
                UserHasGroup userHasGroup = new UserHasGroup();
                userHasGroup.setGroup(addedGroup);
                userHasGroup.setUser(user);

                //save the UserHasGroup entity
                userRepository.saveUserHasGroup(userHasGroup);

                user.getUserHasGroups().add(userHasGroup);
            }
        }
    }
}
