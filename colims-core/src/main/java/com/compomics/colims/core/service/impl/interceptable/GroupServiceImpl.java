/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.service.impl.interceptable;

import com.compomics.colims.core.service.GroupService;
import com.compomics.colims.model.Group;
import com.compomics.colims.model.User;
import com.compomics.colims.model.enums.DefaultGroup;
import com.compomics.colims.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Niels Hulstaert
 */
@Service("groupService")
@Transactional
public class GroupServiceImpl implements GroupService {

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
    public Group findByName(final String name) {
        return groupRepository.findByName(name);
    }

    @Override
    public boolean isDefaultGroup(final Group group) {
        boolean isDefaultGroup = false;

        for (DefaultGroup defaultGroup : DefaultGroup.values()) {
            if (group.getName().equals(defaultGroup.dbEntry())) {
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

    @Override
    public void persist(Group entity) {
        groupRepository.persist(entity);
    }

    @Override
    public Group merge(Group entity) {
        return groupRepository.merge(entity);
    }

    @Override
    public void remove(Group entity) {
        //merge the group
        Group merge = groupRepository.merge(entity);
        //remove entity relations
        for (User user : merge.getUsers()) {
            user.getGroups().remove(merge);
        }

        groupRepository.remove(merge);
    }
}
