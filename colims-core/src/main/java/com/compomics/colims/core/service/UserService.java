/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.service;

import com.compomics.colims.model.Group;
import com.compomics.colims.model.User;
import java.util.List;

/**
 *
 * @author Niels Hulstaert
 */
public interface UserService extends GenericService<User, Long> {

    /**
     * Find the user by login credentials.
     *
     * @param userName the user name
     * @param password the user password
     * @return the found user
     */
    User findByLoginCredentials(String userName, String password);

    /**
     * Find the user by name.
     *
     * @param name the user name
     * @return the found user
     */
    User findByName(String name);

    /**
     * Fetch the user relations for authentication purposes; groups, roles and
     * permissions.
     *
     * @param user the given user
     */
    void fetchAuthenticationRelations(User user);    
    
}
