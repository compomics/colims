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
import com.compomics.colims.model.User;
import com.compomics.colims.repository.GroupRepository;
import org.hibernate.LockOptions;

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
        //attach the group to the new session
        groupRepository.saveOrUpdate(entity);
        //remove entity relations
        for(User user : entity.getUsers()){
            user.getGroups().remove(entity);
        }
        entity.getUsers().clear();        
        
        groupRepository.delete(entity);
    }

    @Override
    public void update(Group entity) {
        //attach the group to the new session
        groupRepository.saveOrUpdate(entity); 
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
    
}
