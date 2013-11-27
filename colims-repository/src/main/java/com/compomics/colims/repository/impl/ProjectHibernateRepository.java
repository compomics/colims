/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository.impl;

import org.springframework.stereotype.Repository;

import com.compomics.colims.model.Project;
import com.compomics.colims.model.User;
import com.compomics.colims.repository.ProjectRepository;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;

/**
 *
 * @author Niels Hulstaert
 */
@Repository("projectRepository")
public class ProjectHibernateRepository extends GenericHibernateRepository<Project, Long> implements ProjectRepository {

    @Override
    public User getUserWithMostProjectOwns() {
        Criteria criteria = createCriteria();

        ProjectionList projectionList = Projections.projectionList();
        projectionList.add(Projections.rowCount(), "projectCountByOwner");
        projectionList.add(Projections.groupProperty("owner"));

        //get results
        List criteriaResults = criteria.setProjection(projectionList).addOrder(Order.desc("projectCountByOwner")).list();

        User user = null;
        if (!criteriaResults.isEmpty()) {
            user = (User) ((Object[]) criteriaResults.get(0))[1];
        }

        return user;
    }
}
