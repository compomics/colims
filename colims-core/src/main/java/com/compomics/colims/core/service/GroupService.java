package com.compomics.colims.core.service;

import com.compomics.colims.model.Group;

/**
 * This interface provides service methods for the Group class.
 *
 * @author Niels Hulstaert
 */
public interface GroupService extends GenericService<Group, Long> {

    /**
     * Find the group by name.
     *
     * @param name the group name
     * @return the found group
     */
    Group findByName(String name);

    /**
     * Check if the group is a default group.
     *
     * @param group the group
     * @return whether or not the group is a default one
     */
    boolean isDefaultGroup(Group group);

}
