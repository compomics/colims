/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository.impl;

import org.springframework.stereotype.Repository;

import com.compomics.colims.model.Group;
import com.compomics.colims.model.GroupHasRole;
import com.compomics.colims.repository.GroupRepository;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Niels Hulstaert
 */
@Repository("groupRepository")
public class GroupHibernateRepository extends GenericHibernateRepository<Group, Long> implements GroupRepository {

    @Override
    public Group findByName(String name) {
        return findUniqueByCriteria(Restrictions.eq("name", name));
    }

    @Override
    public void saveGroupHasRole(GroupHasRole groupHasRole) {
        getCurrentSession().persist(groupHasRole);
    }
        
}
