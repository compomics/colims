/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository.impl;

import org.springframework.stereotype.Repository;

import com.compomics.colims.model.Role;
import com.compomics.colims.repository.RoleRepository;

/**
 *
 * @author Niels Hulstaert
 */
@Repository("roleRepository")
public class RoleHibernateRepository extends GenericHibernateRepository<Role, Long> implements RoleRepository {
}
