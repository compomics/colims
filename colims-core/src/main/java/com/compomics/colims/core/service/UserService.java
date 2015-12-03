/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.service;

import com.compomics.colims.model.User;

/**
 * This interface provides service methods for the User class.
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
     * Count the number of user by user name.
     *
     * @param name the user name
     * @return the number of found users
     */
    Long countByName(String name);

    /**
     * Find the user by name. Returns null if nothing was found.
     *
     * @param name the user name
     * @return the found user
     */
    User findByName(String name);

    /**
     * Fetch the user relations for authentication purposes; groups, roles and permissions.
     *
     * @param user the given user
     */
    void fetchAuthenticationRelations(User user);

    /**
     * Check if the user is a default user.
     *
     * @param user the user
     * @return whether or not the user is a default user
     */
    boolean isDefaultUser(User user);

    /**
     * Find the user name by the given user ID. Returns null is no user with the given ID was found.
     *
     * @param userId the user ID
     * @return the found user name
     */
    String findUserNameById(Long userId);

    /**
     * Fetch the institution association.
     *
     * @param user the given user
     */
    void fetchInstitution(User user);
}
