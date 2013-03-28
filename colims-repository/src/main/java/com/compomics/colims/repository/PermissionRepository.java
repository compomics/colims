package com.compomics.colims.repository;

import com.compomics.colims.model.Permission;

/**
 *
 * @author Niels Hulstaert
 */
public interface PermissionRepository extends GenericRepository<Permission, Long> {
    
    /**
     * Find the permission by the permission name.
     *
     * @param name the permission name
     * @return the found permission
     */
    Permission findByName(String name);
    
}
