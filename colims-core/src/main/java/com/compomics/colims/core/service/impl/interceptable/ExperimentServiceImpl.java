/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.service.impl.interceptable;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.compomics.colims.core.service.ExperimentService;
import com.compomics.colims.model.Experiment;
import com.compomics.colims.repository.ExperimentRepository;
import com.compomics.colims.repository.impl.AbstractBinaryFileHibernateRepository;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;

/**
 *
 * @author Niels Hulstaert
 */
@Service("experimentService")
@Transactional
public class ExperimentServiceImpl implements ExperimentService {

    private static final Logger LOGGER = Logger.getLogger(ExperimentServiceImpl.class);
    @Autowired
    private ExperimentRepository experimentRepository;

    @Override
    public Experiment findById(final Long id) {
        return experimentRepository.findById(id);
    }

    @Override
    public List<Experiment> findAll() {
        return experimentRepository.findAll();
    }

    @Override
    public void save(final Experiment entity) {
        experimentRepository.save(entity);
    }

    @Override
    public void delete(final Experiment entity) {
        experimentRepository.delete(entity);
    }

    @Override
    public void update(final Experiment entity) {
        experimentRepository.update(entity);
    }

    @Override
    public void saveOrUpdate(final Experiment entity) {
        experimentRepository.saveOrUpdate(entity);
    }

    @Override
    public long countAll() {
        return experimentRepository.countAll();
    }

    @Override
    public Experiment findByTitle(final String title) {
        return experimentRepository.findByTitle(title);
    }
    
    @Override
    public Experiment findByProjectIdAndTitle(final Long projectId, final String title) {
        return experimentRepository.findByTitle(title);
    }

    @Override
    public void fetchBinaryFiles(final Experiment experiment) {
        try {
            //attach the experiment to the new session
            experimentRepository.saveOrUpdate(experiment);
            if (!Hibernate.isInitialized(experiment.getBinaryFiles())) {
                Hibernate.initialize(experiment.getBinaryFiles());
            }
        } catch (HibernateException hbe) {
            LOGGER.error(hbe, hbe.getCause());
        }
    }
}
