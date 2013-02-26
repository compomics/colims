/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository;

import com.compomics.colims.model.User;

/**
 *
 * @author Niels Hulstaert
 */
public interface UserRepository extends GenericRepository<User, Long> {

    /**
     * Find the user by the user name.
     *
     * @param name the user name
     * @return the found user
     */
    User findByName(String name);        
}
