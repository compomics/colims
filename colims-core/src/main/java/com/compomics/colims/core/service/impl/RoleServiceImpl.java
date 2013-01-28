/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.service.GroupService;
import com.compomics.colims.core.service.RoleService;
import com.compomics.colims.model.Group;
import com.compomics.colims.model.Role;
import com.compomics.colims.repository.GroupRepository;
import com.compomics.colims.repository.RoleRepository;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return roleRepository.findAll();
    }

    @Override
    public void save(Role entity) {
        roleRepository.save(entity);
    }

    @Override
    public void delete(Role entity) {
        roleRepository.delete(entity);
    }

    @Override
    public void update(Role entity) {
        update(entity);
    }

    @Override
    public void saveOrUpdate(Role entity) {
        saveOrUpdate(entity);
    }
}
