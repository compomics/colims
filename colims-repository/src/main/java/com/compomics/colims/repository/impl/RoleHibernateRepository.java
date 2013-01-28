/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository.impl;

import com.compomics.colims.model.Role;
import com.compomics.colims.repository.RoleRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Niels Hulstaert
 */
@Repository("roleRepository")
public class RoleHibernateRepository extends GenericHibernateRepository<Role, Long> implements RoleRepository {
}
