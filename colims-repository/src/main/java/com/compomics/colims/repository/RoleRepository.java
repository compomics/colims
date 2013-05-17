package com.compomics.colims.repository;

import com.compomics.colims.model.Role;

/**
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
    
}
