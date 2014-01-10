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

import com.compomics.colims.core.service.PermissionService;
import com.compomics.colims.model.Permission;
import com.compomics.colims.model.Role;
import com.compomics.colims.model.enums.DefaultPermission;
import com.compomics.colims.repository.PermissionRepository;

/**
 *
 * @author Niels Hulstaert
 */
@Service("permissionService")
@Transactional
public class PermissionServiceImpl implements PermissionService {

    private static final Logger LOGGER = Logger.getLogger(PermissionServiceImpl.class);
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
    public void save(final Permission entity) {
        permissionRepository.save(entity);
    }

    @Override
    public void delete(final Permission entity) {
        //attach the permission to the new session
        permissionRepository.saveOrUpdate(entity);
        //remove entity relations
        for(Role role : entity.getRoles()){
            role.getPermissions().remove(entity);
        }
        
        permissionRepository.delete(entity);
    }

    @Override
    public void update(final Permission entity) {        
        //attach the permission to the new session
        permissionRepository.saveOrUpdate(entity);
        permissionRepository.update(entity);        
    }

    @Override
    public void saveOrUpdate(final Permission entity) {
        permissionRepository.saveOrUpdate(entity);
    }

    @Override
    public Permission findByName(final String name) {
        return permissionRepository.findByName(name);
    }

    @Override
    public boolean isDefaultPermission(final Permission permission) {
        boolean isDefaultPermission = false;
        
        for(DefaultPermission defaultPermission : DefaultPermission.values()){
            if(permission.getName().equals(defaultPermission.getDbEntry())){
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
        
}
