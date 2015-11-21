/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository.impl;

import com.compomics.colims.model.Experiment;
import com.compomics.colims.repository.ExperimentRepository;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

/**
 * @author Niels Hulstaert
 */
@Repository("experimentRepository")
public class ExperimentHibernateRepository extends GenericHibernateRepository<Experiment, Long> implements ExperimentRepository {

    @Override
    public Experiment findByProjectIdAndTitle(Long projectId, String title) {
        return findUniqueByCriteria(Restrictions.eq("project.id", projectId), Restrictions.eq("title", title));
    }
}
