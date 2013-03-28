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
import com.compomics.colims.model.Group;
import com.compomics.colims.model.User;
import com.compomics.colims.model.UserHasGroup;
import com.compomics.colims.repository.GroupRepository;
import com.compomics.colims.repository.PermissionRepository;
import com.compomics.colims.repository.RoleRepository;
import com.compomics.colims.repository.UserRepository;
import java.util.Iterator;

/**
 *
 * @author Niels Hulstaert
 */
@Service("userService")
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PermissionRepository permissionRepository;

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
        //attach the user to the new session
        userRepository.lock(user, LockOptions.NONE);
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
        //attach the user to the new session
        userRepository.lock(user, LockOptions.NONE);
        if (!Hibernate.isInitialized(user.getUserHasGroups())) {
            Hibernate.initialize(user.getUserHasGroups());
        }
    }

    @Override
    public void saveUser(User user, List<Group> addedGroups) {
        //userRepository.save(user);
        updateUserhasGroups(user, addedGroups);
        //userRepository.update(user);
    }

    @Override
    public void updateUser(User user, List<Group> addedGroups) {
        //attach the user to the new session
        userRepository.lock(user, LockOptions.NONE);
        updateUserhasGroups(user, addedGroups);
        //userRepository.update(user);
    }

    /**
     * Update the userHasGroups for the given user;
     *  for an exisiting user: persist newly added UserHasGroup enitities, delete removed ones
     *  for a new user: persist the UserHasGroup entities
     *
     * @param user the given user
     * @param addedGroups the list of groups to add
     */
    private void updateUserhasGroups(User user, List<Group> addedGroups) { 
        //first, add groups if necessary
        for (Group addedGroup : addedGroups) {
            //check if the user already belongs to the given group
            UserHasGroup userHasGroup = user.getUserHasGroupByGroup(addedGroup);

            if (userHasGroup == null) {
                userHasGroup = new UserHasGroup();
                userHasGroup.setGroup(addedGroup);
                userHasGroup.setUser(user);

                //save the UserHasGroup entity
                userRepository.saveUserHasGroup(userHasGroup);                
                user.getUserHasGroups().add(userHasGroup);
            }            
        }
        
        //second, check for groups to remove
        Iterator<UserHasGroup> iterator = user.getUserHasGroups().iterator();
        while(iterator.hasNext()){
            UserHasGroup userHasGroup = iterator.next();
            if(!addedGroups.contains(userHasGroup.getGroup())){
                //userHasGroup.setUser(null);
                //remove UserHasGroup from userHasGroups
                iterator.remove();
            }
        }
    }
}
