/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.service;

import com.compomics.colims.model.Group;
import com.compomics.colims.model.Permission;
import com.compomics.colims.model.Role;
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

    /**
     * Save the given user. Persist new UserHasGroups first if new groups
     * were assigned to the user.
     *
     * @param user the user to save
     * @param addedGroups the added groups
     */
    void saveUser(User user, List<Group> addedGroups);
    
    /**
     * Update the given user. Persist new UserHasGroups first if new groups
     * were assigned to the user.
     *
     * @param user the user to update
     * @param addedGroups the added groups
     */
    void updateUser(User user, List<Group> addedGroups);
}
