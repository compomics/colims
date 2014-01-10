/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository.impl;

import org.springframework.stereotype.Repository;

import com.compomics.colims.model.Experiment;
import com.compomics.colims.repository.ExperimentRepository;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Niels Hulstaert
 */
@Repository("experimentRepository")
public class ExperimentHibernateRepository extends GenericHibernateRepository<Experiment, Long> implements ExperimentRepository {

    @Override
    public List<Experiment> getExperimentsByProjectId(final Long projectId) {
        Criteria subCriteria = createCriteria().createCriteria("l_project_id");
        @SuppressWarnings("unchecked")
        List<Experiment> list = subCriteria.add(Restrictions.eq("id", projectId)).list();
        return list;
    }
    
    @Override
    public Experiment findByTitle(final String title) {
        return findUniqueByCriteria(Restrictions.eq("title", title));
    }
}
