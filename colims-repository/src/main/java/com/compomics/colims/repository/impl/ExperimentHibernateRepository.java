/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository.impl;

import com.compomics.colims.model.Experiment;
import com.compomics.colims.model.ExperimentBinaryFile;
import com.compomics.colims.repository.ExperimentRepository;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Niels Hulstaert
 */
@Repository("experimentRepository")
public class ExperimentHibernateRepository extends GenericHibernateRepository<Experiment, Long> implements ExperimentRepository {

    @Override
    public Long countByProjectIdAndTitle(Long projectId, String title) {
        Criteria criteria = createCriteria(Restrictions.eq("project.id", projectId), Restrictions.eq("title", title));

        criteria.setProjection(Projections.rowCount());

        return (Long) criteria.uniqueResult();
    }

    @Override
    public List<ExperimentBinaryFile> fetchBinaryFiles(Long experimentId) {
        Criteria criteria = getCurrentSession().createCriteria(ExperimentBinaryFile.class);

        criteria.add(Restrictions.eq("experiment.id", experimentId));

        return criteria.list();
    }

    @Override
    public Experiment findByIdWithFetchedSamples(Long experimentId) {
        Query query = getCurrentSession().getNamedQuery("Experiment.findByIdWithFetchedSamples");

        query.setLong("experimentId", experimentId);

        return (Experiment) query.uniqueResult();
    }
}
