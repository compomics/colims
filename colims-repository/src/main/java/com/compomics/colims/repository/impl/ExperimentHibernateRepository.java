/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository.impl;

import org.springframework.stereotype.Repository;

import com.compomics.colims.model.Experiment;
import com.compomics.colims.repository.ExperimentRepository;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Niels Hulstaert
 */
@Repository("experimentRepository")
public class ExperimentHibernateRepository extends GenericHibernateRepository<Experiment, Long> implements ExperimentRepository {
    
    @Override
    public Experiment findByTitle(final String title) {
        return findUniqueByCriteria(Restrictions.eq("title", title));
    }

    @Override
    public Experiment findByProjectIdAndTitle(Long projectId, String title) {
        return findUniqueByCriteria(Restrictions.eq("project.id", projectId), Restrictions.eq("title", title));
    }
}
