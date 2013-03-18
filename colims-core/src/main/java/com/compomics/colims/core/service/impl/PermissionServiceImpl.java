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
    public Permission findById(Long id) {
        return permissionRepository.findById(id);
    }

    @Override
    public List<Permission> findAll() {
        return permissionRepository.findAll();
    }

    @Override
    public void save(Permission entity) {
        permissionRepository.save(entity);
    }

    @Override
    public void delete(Permission entity) {
        permissionRepository.delete(entity);
    }

    @Override
    public void update(Permission entity) {
        permissionRepository.update(entity);
    }

    @Override
    public void saveOrUpdate(Permission entity) {
        permissionRepository.saveOrUpdate(entity);
    }
}
