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
import com.compomics.colims.model.GroupHasRole;
import com.compomics.colims.model.Permission;
import com.compomics.colims.model.Role;
import com.compomics.colims.model.RoleHasPermission;
import com.compomics.colims.repository.RoleRepository;
import java.util.Iterator;

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
    public void saveRole(Role role, List<Permission> addedPermissions) {
        //roleRepository.save(role);
        updateRoleHasPermissions(role, addedPermissions);
        //roleRepository.update(role);
    }

    @Override
    public void updateRole(Role role, List<Permission> addedPermissions) {
        //attach the role to the new session
        //roleRepository.lock(role, LockOptions.NONE);
        updateRoleHasPermissions(role, addedPermissions);
        //roleRepository.update(role);
    }

    /**
     * Update the roleHasPermissions for the given role; 
     * ->for an exisiting role: persist newly added RoleHasPermission enitities, delete removed ones
     * ->for a new role: persist the RoleHasPermission entities 
     *
     * @param role the given role
     * @param addedPermissions the list of permissions to add
     */
    private void updateRoleHasPermissions(Role role, List<Permission> addedPermissions) {
        //first, add permissions if necessary
        for (Permission addedPermission : addedPermissions) {
            //check if the permission already belongs to the given role
            RoleHasPermission roleHasPermission = role.getRoleHasPermissionByPermission(addedPermission);

            if (roleHasPermission == null) {
                roleHasPermission = new RoleHasPermission();
                roleHasPermission.setPermission(addedPermission);
                roleHasPermission.setRole(role);                

                //save the RoleHasPermission entity
                roleRepository.saveRoleHasPermission(roleHasPermission);
                role.getRoleHasPermissions().add(roleHasPermission);
            }
        }

        //second, check for permissions to remove
        Iterator<RoleHasPermission> iterator = role.getRoleHasPermissions().iterator();
        while (iterator.hasNext()) {
            RoleHasPermission roleHasPermission = iterator.next();
            if (!addedPermissions.contains(roleHasPermission.getRole())) {                
                //remove RoleHasPermission from roleHasPermissions
                iterator.remove();
            }
        }
    }
}
