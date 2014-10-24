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
     * Find the role by the role name.
     *
     * @param name the role name
     * @return the found role
     */
    Role findByName(String name);

    /**
     * Find all roles ordered by name.
     *
     * @return the ordered list of roles
     */
    List<Role> findAllOrderedByName();

}
