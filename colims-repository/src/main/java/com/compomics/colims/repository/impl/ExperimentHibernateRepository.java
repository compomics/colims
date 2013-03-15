/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.repository.impl;

import org.springframework.stereotype.Repository;

import com.compomics.colims.model.Experiment;
import com.compomics.colims.repository.ExperimentRepository;

/**
 *
 * @author Niels Hulstaert
 */
@Repository("experimentRepository")
public class ExperimentHibernateRepository extends GenericHibernateRepository<Experiment, Long> implements ExperimentRepository {
}
