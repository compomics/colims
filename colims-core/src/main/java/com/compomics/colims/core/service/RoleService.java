/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.service;

import com.compomics.colims.model.Group;
import com.compomics.colims.model.Permission;
import com.compomics.colims.model.Role;
import java.util.List;

/**
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
     * Save the given role. Persist new RoleHasPermissions first if new permissions
     * were assigned to the role.
     *
     * @param role the role to save
     * @param addedPermissions the added permissions
     */
    void saveRole(Role role, List<Permission> addedPermissions);
    
    /**
     * Update the given role. Persist new RoleHasPermissions first if new permissions
     * were assigned to the role.
     *
     * @param role the role to update
     * @param addedRoles the added roles
     */
    void updateRole(Role role, List<Permission> addedPermissions);
    
}
