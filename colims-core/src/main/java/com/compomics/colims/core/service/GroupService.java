/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.service;

import com.compomics.colims.model.Group;
import com.compomics.colims.model.Role;
import java.util.List;

/**
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
     * Save the given group. Persist new GroupHasRoles first if new roles
     * were assigned to the group.
     *
     * @param group the group to save
     * @param addedRoles the added roles
     */
    void saveGroup(Group group, List<Role> addedRoles);
    
    /**
     * Update the given group. Persist new GroupHasRoles first if new roles
     * were assigned to the group.
     *
     * @param group the group to update
     * @param addedGroups the added groups
     */
    void updateGroup(Group group, List<Role> addedRoles);
}
