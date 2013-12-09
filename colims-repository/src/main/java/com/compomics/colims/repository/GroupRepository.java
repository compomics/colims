package com.compomics.colims.repository;

import com.compomics.colims.model.Group;
import java.util.List;

/**
 *
 * @author Niels Hulstaert
 */
public interface GroupRepository extends GenericRepository<Group, Long> {   
    
    /**
     * Find the group by the group name.
     *
     * @param name the group name
     * @return the found group
     */
    Group findByName(String name);
    
    /**
     * Find all groups ordered by name.
     * 
     * @return the ordered list of groups
     */
    List<Group> findAllOrderedByName();
    
}
