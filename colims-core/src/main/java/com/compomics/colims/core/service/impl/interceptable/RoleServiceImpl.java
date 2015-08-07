/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.service.impl.interceptable;

import com.compomics.colims.core.service.RoleService;
import com.compomics.colims.model.Group;
import com.compomics.colims.model.Role;
import com.compomics.colims.model.enums.DefaultRole;
import com.compomics.colims.repository.RoleRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Niels Hulstaert
 */
@Service("roleService")
@Transactional
public class RoleServiceImpl implements RoleService {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(RoleServiceImpl.class);

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public Role findById(final Long id) {
        return roleRepository.findById(id);
    }

    @Override
    public List<Role> findAll() {
        return roleRepository.findAllOrderedByName();
    }

    @Override
    public void save(final Role entity) {
        roleRepository.save(entity);
    }

    @Override
    public void delete(final Role entity) {
        //attach the role to the new session
        roleRepository.saveOrUpdate(entity);
        //remove entity relations
        for (Group group : entity.getGroups()) {
            group.getRoles().remove(entity);
        }

        roleRepository.delete(entity);
    }

    @Override
    public void update(final Role entity) {
        roleRepository.update(entity);
    }

    @Override
    public void saveOrUpdate(final Role entity) {
        roleRepository.saveOrUpdate(entity);
    }

    @Override
    public Role findByName(final String name) {
        return roleRepository.findByName(name);
    }

    @Override
    public long countAll() {
        return roleRepository.countAll();
    }

    @Override
    public boolean isDefaultRole(final Role role) {
        boolean isDefaultRole = false;

        for (DefaultRole defaultRole : DefaultRole.values()) {
            if (role.getName().equals(defaultRole.dbEntry())) {
                isDefaultRole = true;
                break;
            }
        }

        return isDefaultRole;
    }

}
