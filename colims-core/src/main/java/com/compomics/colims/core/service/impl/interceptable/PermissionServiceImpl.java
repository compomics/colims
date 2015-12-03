/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.service.impl.interceptable;

import com.compomics.colims.core.service.PermissionService;
import com.compomics.colims.model.Permission;
import com.compomics.colims.model.Role;
import com.compomics.colims.model.enums.DefaultPermission;
import com.compomics.colims.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Niels Hulstaert
 */
@Service("permissionService")
@Transactional
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    @Override
    public Permission findById(final Long id) {
        return permissionRepository.findById(id);
    }

    @Override
    public List<Permission> findAll() {
        return permissionRepository.findAllOrderedByName();
    }

    @Override
    public Long countByName(final String name) {
        return permissionRepository.countByName(name);
    }

    @Override
    public boolean isDefaultPermission(final Permission permission) {
        boolean isDefaultPermission = false;

        for (DefaultPermission defaultPermission : DefaultPermission.values()) {
            if (permission.getName().equals(defaultPermission.dbEntry())) {
                isDefaultPermission = true;
                break;
            }
        }

        return isDefaultPermission;
    }

    @Override
    public long countAll() {
        return permissionRepository.countAll();
    }

    @Override
    public void persist(Permission entity) {
        permissionRepository.persist(entity);
    }

    @Override
    public Permission merge(Permission entity) {
        return permissionRepository.merge(entity);
    }

    @Override
    public void remove(Permission entity) {
        //merge the permission
        Permission merge = permissionRepository.merge(entity);
        //remove entity relations
        for (Role role : merge.getRoles()) {
            role.getPermissions().remove(merge);
        }

        permissionRepository.remove(merge);
    }

}
