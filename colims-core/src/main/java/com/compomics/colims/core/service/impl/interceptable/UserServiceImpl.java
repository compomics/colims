/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.service.impl.interceptable;

import com.compomics.colims.core.service.UserService;
import com.compomics.colims.model.User;
import com.compomics.colims.model.enums.DefaultUser;
import com.compomics.colims.repository.UserRepository;
import org.hibernate.LazyInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Niels Hulstaert
 */
@Service("userService")
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User findById(final Long userId) {
        return userRepository.findById(userId);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAllOrderedByUserName();
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
    public Long countByName(final String name) {
        return userRepository.countByName(name);
    }

    @Override
    public User findByName(String name) {
        return userRepository.findByName(name);
    }

    @Override
    public void fetchAuthenticationRelations(final User user) {
        try {
            user.getGroups().size();
        } catch (LazyInitializationException e) {
            //merge the user
            User merge = userRepository.merge(user);
            merge.getGroups().size();
            merge.getGroups().stream().forEach(group -> {
                group.getRoles().size();
                group.getRoles().stream().forEach((role) -> {
                    role.getPermissions().size();
                });
            });
            user.setGroups(merge.getGroups());
        }
    }

    @Override
    public long countAll() {
        return userRepository.countAll();
    }

    @Override
    public void persist(User entity) {
        userRepository.persist(entity);
    }

    @Override
    public User merge(User entity) {
        return userRepository.merge(entity);
    }

    @Override
    public void remove(User entity) {
        User merge = userRepository.merge(entity);
        //remove entity relations
        merge.getProjects().stream().forEach((project) -> {
            project.getUsers().remove(merge);
        });

        userRepository.remove(merge);
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
        if (foundUser != null) {
            userName = foundUser.getName();
        }

        return userName;
    }

    @Override
    public void fetchInstitution(User user) {
        try {
            if (user.getInstitution() != null) {
                user.getInstitution().getId();
            }
        } catch (LazyInitializationException e) {
            //merge the user
            User merge = userRepository.merge(user);
            merge.getInstitution().getId();
            user.setInstitution(merge.getInstitution());
        }
    }

}
