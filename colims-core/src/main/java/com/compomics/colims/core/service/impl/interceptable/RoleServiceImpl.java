/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.service.impl.interceptable;

import com.compomics.colims.core.service.RoleService;
import com.compomics.colims.model.Role;
import com.compomics.colims.model.enums.DefaultRole;
import com.compomics.colims.repository.RoleRepository;
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

    private final RoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role findById(final Long id) {
        return roleRepository.findById(id);
    }

    @Override
    public List<Role> findAll() {
        return roleRepository.findAllOrderedByName();
    }

    @Override
    public Long countByName(final String name) {
        return roleRepository.countByName(name);
    }

    @Override
    public long countAll() {
        return roleRepository.countAll();
    }

    @Override
    public void persist(Role entity) {
        roleRepository.persist(entity);
    }

    @Override
    public Role merge(Role entity) {
        return roleRepository.merge(entity);
    }

    @Override
    public void remove(Role entity) {
        //merge the role
        Role merge = roleRepository.merge(entity);
        //remove entity relations
        merge.getGroups().stream().forEach((group) -> group.getRoles().remove(merge));

        roleRepository.remove(merge);
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
