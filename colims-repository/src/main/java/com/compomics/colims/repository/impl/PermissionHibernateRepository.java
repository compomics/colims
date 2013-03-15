/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository.impl;

import org.springframework.stereotype.Repository;

import com.compomics.colims.model.Permission;
import com.compomics.colims.repository.PermissionRepository;

/**
 *
 * @author Niels Hulstaert
 */
@Repository("permissionRepository")
public class PermissionHibernateRepository extends GenericHibernateRepository<Permission, Long> implements PermissionRepository {
}
