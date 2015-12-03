package com.compomics.colims.repository;

import com.compomics.colims.model.Permission;
import java.util.List;

/**
 * This interface provides repository methods for the Permission class.
 *
 * @author Niels Hulstaert
 */
public interface PermissionRepository extends GenericRepository<Permission, Long> {

    /**
     * Count the number of permissions by permission name.
     *
     * @param name the permission name
     * @return the number of found permissions
     */
    Long countByName(String name);

    /**
     * Find all permissions ordered by name.
     *
     * @return the ordered list of permissions
     */
    List<Permission> findAllOrderedByName();

}
