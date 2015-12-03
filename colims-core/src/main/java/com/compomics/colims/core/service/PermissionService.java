/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.service;

import com.compomics.colims.model.Permission;

/**
 * This interface provides service methods for the Permission class.
 *
 * @author Niels Hulstaert
 */
public interface PermissionService extends GenericService<Permission, Long> {

    /**
     * Count the number of permissions by permission name.
     *
     * @param name the permission name
     * @return the number of found permissions
     */
    Long countByName(String name);

    /**
     * Check if the permission is a default permission.
     *
     * @param permission the permission
     * @return whether or not the permission is a default one
     */
    boolean isDefaultPermission(Permission permission);
}
