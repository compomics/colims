/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.service;

import com.compomics.colims.model.Group;

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
     * Check if the group is a default group.
     *
     * @param group the group
     * @return
     */
    boolean isDefaultGroup(Group group);
        
}
