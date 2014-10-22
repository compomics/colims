/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.service;

import com.compomics.colims.model.Role;

/**
 * This interface provides service methods for the Role class.
 *
 * @author Niels Hulstaert
 */
public interface RoleService extends GenericService<Role, Long> {

    /**
     * Find the role by name.
     *
     * @param name the role name
     * @return the found role
     */
    Role findByName(String name);

    /**
     * Check if the role is a default role.
     *
     * @param role the role
     * @return whether or not the role is a default one
     */
    boolean isDefaultRole(Role role);

}
