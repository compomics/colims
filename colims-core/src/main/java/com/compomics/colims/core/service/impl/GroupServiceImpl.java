/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.compomics.colims.core.service.GroupService;
import com.compomics.colims.model.Group;
import com.compomics.colims.model.Role;
import com.compomics.colims.repository.GroupRepository;

/**
 *
 * @author Niels Hulstaert
 */
@Service("groupService")
@Transactional
public class GroupServiceImpl implements GroupService {

    private static final Logger LOGGER = Logger.getLogger(GroupServiceImpl.class);
    @Autowired
    private GroupRepository groupRepository;

    @Override
    public Group findById(Long id) {
        return groupRepository.findById(id);
    }

    @Override
    public List<Group> findAll() {
        return groupRepository.findAll();
    }

    @Override
    public void save(Group entity) {
        groupRepository.save(entity);
    }

    @Override
    public void delete(Group entity) {
        groupRepository.delete(entity);
    }

    @Override
    public void update(Group entity) {
        groupRepository.update(entity);
    }

    @Override
    public void saveOrUpdate(Group entity) {
        groupRepository.saveOrUpdate(entity);
    }

    @Override
    public Group findByName(String name) {
        return groupRepository.findByName(name);
    }

    @Override
    public void saveGroup(Group group, List<Role> addedRoles) {
        //groupRepository.save(group);
        updateGroupHasRoles(group, addedRoles);
        //groupRepository.update(group);
    }

    @Override
    public void updateGroup(Group group, List<Role> addedRoles) {
        //attach the group to the new session
        //groupRepository.lock(group, LockOptions.NONE);
        updateGroupHasRoles(group, addedRoles);
        //groupRepository.update(group);
    }

    /**
     * Update the groupHasRoles for the given group; 
     * ->for an exisiting group: persist newly added UserHasGroup enitities, delete removed ones
     * ->for a new user: persist the UserHasGroup entities 
     *
     * @param group the given group
     * @param addedRoles the list of roles to add
     */
    private void updateGroupHasRoles(Group group, List<Role> addedRoles) {
//        //first, add roles if necessary
//        for (Role addedRole : addedRoles) {
//            //check if the role already belongs to the given group
//            GroupHasRole groupHasRole = group.getGroupHasRoleByRole(addedRole);
//
//            if (groupHasRole == null) {
//                groupHasRole = new GroupHasRole();
//                groupHasRole.setRole(addedRole);
//                groupHasRole.setGroup(group);
//
//                //save the GroupHasRole entity
//                groupRepository.saveGroupHasRole(groupHasRole);
//                group.getGroupHasRoles().add(groupHasRole);
//            }
//        }
//
//        //second, check for roles to remove
//        Iterator<GroupHasRole> iterator = group.getGroupHasRoles().iterator();
//        while (iterator.hasNext()) {
//            GroupHasRole groupHasRole = iterator.next();
//            if (!addedRoles.contains(groupHasRole.getRole())) {
//                //userHasGroup.setUser(null);
//                //remove UserHasGroup from userHasGroups
//                iterator.remove();
//            }
//        }
    }
}
