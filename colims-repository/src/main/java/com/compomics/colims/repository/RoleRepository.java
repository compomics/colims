package com.compomics.colims.repository;

import com.compomics.colims.model.Role;

import java.util.List;

/**
 * This interface provides repository methods for the Role class.
 *
 * @author Niels Hulstaert
 */
public interface RoleRepository extends GenericRepository<Role, Long> {

    /**
     * Count the number of roles by role name.
     *
     * @param name the role name
     * @return the number of found roles
     */
    Long countByName(String name);

    /**
     * Find all roles ordered by name.
     *
     * @return the ordered list of roles
     */
    List<Role> findAllOrderedByName();

}
