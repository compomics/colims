package com.compomics.colims.repository;

import com.compomics.colims.model.Group;
import java.util.List;

/**
 * This interface provides repository methods for the Group class.
 *
 * @author Niels Hulstaert
 */
public interface GroupRepository extends GenericRepository<Group, Long> {

    /**
     * Count the number of groups by group name.
     *
     * @param name the group name
     * @return the number of found groups
     */
    Long countByName(String name);

    /**
     * Find all groups ordered by name.
     *
     * @return the ordered list of groups
     */
    List<Group> findAllOrderedByName();

}
