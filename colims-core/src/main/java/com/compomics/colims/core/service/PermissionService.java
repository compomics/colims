/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.service;

import com.compomics.colims.model.Permission;

/**
 *
 * @author Niels Hulstaert
 */
public interface PermissionService extends GenericService<Permission, Long> {

    /**
     * Find the permission by name.
     *
     * @param name the permission name
     * @return the found permission
     */
    Permission findByName(String name);

    /**
     * Check if the permission is a default permission.
     *
     * @param permission the permission
     * @return
     */
    boolean isDefaultPermission(Permission permission);
}
