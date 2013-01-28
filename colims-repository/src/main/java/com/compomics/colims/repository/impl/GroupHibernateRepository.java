/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository.impl;

import com.compomics.colims.model.Group;
import com.compomics.colims.repository.GroupRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Niels Hulstaert
 */
@Repository("groupRepository")
public class GroupHibernateRepository extends GenericHibernateRepository<Group, Long> implements GroupRepository {
}
