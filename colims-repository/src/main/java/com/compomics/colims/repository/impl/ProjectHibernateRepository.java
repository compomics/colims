/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository.impl;

import com.compomics.colims.model.Project;
import com.compomics.colims.model.User;
import com.compomics.colims.repository.ProjectRepository;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Niels Hulstaert
 */
@Repository("projectRepository")
public class ProjectHibernateRepository extends GenericHibernateRepository<Project, Long> implements ProjectRepository {

    @Override
    public Long countByTitle(final String title) {
        Criteria criteria = createCriteria(Restrictions.eq("title", title));

        criteria.setProjection(Projections.rowCount());

        return (Long) criteria.uniqueResult();
    }

    @Override
    public List<Project> findAllWithFetchedExperiments() {
        Query query = getCurrentSession().getNamedQuery("Project.findAllWithFetchedExperiments");

        return query.list();
    }

    @Override
    public Project findByIdWithFetchedExperiments(Long projectId) {
        Query query = getCurrentSession().getNamedQuery("Project.findByIdWithFetchedExperiments");

        query.setLong("projectId", projectId);

        return (Project) query.uniqueResult();
    }

    @Override
    public User getUserWithMostProjectOwns() {
        Criteria criteria = createCriteria();

        ProjectionList projectionList = Projections.projectionList();
        projectionList.add(Projections.rowCount(), "projectCountByOwner");
        projectionList.add(Projections.groupProperty("owner"));

        //add order
        criteria.addOrder(Order.desc("projectCountByOwner"));

        //get results
        List criteriaResults = criteria.setProjection(projectionList).list();

        User user = null;
        if (!criteriaResults.isEmpty()) {
            user = (User) ((Object[]) criteriaResults.get(0))[1];
        }

        return user;
    }

    @Override
    public List<User> fetchUsers(Long projectId) {
        Criteria criteria = getCurrentSession().createCriteria(User.class);

        criteria.createAlias("projects", "project");
        criteria.add(Restrictions.eq("project.id", projectId));

        return criteria.list();
    }
}
