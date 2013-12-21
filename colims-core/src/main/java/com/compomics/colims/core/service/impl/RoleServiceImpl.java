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

import com.compomics.colims.core.service.RoleService;
import com.compomics.colims.model.Group;
import com.compomics.colims.model.Role;
import com.compomics.colims.model.enums.DefaultRole;
import com.compomics.colims.repository.RoleRepository;

/**
 *
 * @author Niels Hulstaert
 */
@Service("roleService")
@Transactional
public class RoleServiceImpl implements RoleService {

    private static final Logger LOGGER = Logger.getLogger(RoleServiceImpl.class);
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public Role findById(Long id) {
        return roleRepository.findById(id);
    }

    @Override
    public List<Role> findAll() {
        return roleRepository.findAllOrderedByName();
    }

    @Override
    public void save(Role entity) {
        roleRepository.save(entity);
    }

    @Override
    public void delete(Role entity) {
        //attach the role to the new session
        roleRepository.saveOrUpdate(entity);
        //remove entity relations
        for(Group group : entity.getGroups()){
            group.getRoles().remove(entity);
        }
        
        roleRepository.delete(entity);
    }

    @Override
    public void update(Role entity) {
        roleRepository.update(entity);
    }

    @Override
    public void saveOrUpdate(Role entity) {
        roleRepository.saveOrUpdate(entity);
    }

    @Override
    public Role findByName(String name) {
        return roleRepository.findByName(name);
    }

    @Override
    public long countAll() {
        return roleRepository.countAll();
    }

    @Override
    public boolean isDefaultRole(Role role) {
         boolean isDefaultRole = false;
        
        for(DefaultRole defaultRole : DefaultRole.values()){
            if(role.getName().equals(defaultRole.getDbEntry())){
                isDefaultRole = true;
                break;
            }
        }
        
        return isDefaultRole;
    }
       
}
