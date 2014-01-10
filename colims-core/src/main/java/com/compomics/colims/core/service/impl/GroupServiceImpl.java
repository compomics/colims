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
import com.compomics.colims.model.User;
import com.compomics.colims.model.enums.DefaultGroup;
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
    public Group findById(final Long id) {
        return groupRepository.findById(id);
    }

    @Override
    public List<Group> findAll() {
        return groupRepository.findAllOrderedByName();
    }

    @Override
    public void save(final Group entity) {
        groupRepository.save(entity);
    }

    @Override
    public void delete(final Group entity) {
        //attach the group to the new session
        groupRepository.saveOrUpdate(entity);
        //remove entity relations
        for (User user : entity.getUsers()) {
            user.getGroups().remove(entity);
        }

        groupRepository.delete(entity);
    }

    @Override
    public void update(final Group entity) {
        groupRepository.update(entity);
    }

    @Override
    public void saveOrUpdate(final Group entity) {
        groupRepository.saveOrUpdate(entity);
    }

    @Override
    public Group findByName(final String name) {
        return groupRepository.findByName(name);
    }

    @Override
    public boolean isDefaultGroup(final Group group) {
        boolean isDefaultGroup = false;
        
        for(DefaultGroup defaultGroup : DefaultGroup.values()){
            if(group.getName().equals(defaultGroup.getDbEntry())){
                isDefaultGroup = true;
                break;
            }
        }
        
        return isDefaultGroup;
    }

    @Override
    public long countAll() {
        return groupRepository.countAll();
    }
   
}
