/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.compomics.colims.core.service.ProjectService;
import com.compomics.colims.model.Experiment;
import com.compomics.colims.model.Project;
import com.compomics.colims.model.Sample;
import com.compomics.colims.model.User;
import com.compomics.colims.repository.ProjectRepository;
import org.hibernate.Hibernate;

/**
 *
 * @author Niels Hulstaert
 */
@Service("projectService")
@Transactional
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Override
    public Project findById(final Long id) {
        return projectRepository.findById(id);
    }

    @Override
    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    @Override
    public void save(final Project entity) {
        projectRepository.save(entity);
    }

    @Override
    public void delete(final Project entity) {
        projectRepository.delete(entity);
    }

    @Override
    public void update(final Project entity) {
        projectRepository.update(entity);
    }

    @Override
    public void saveOrUpdate(final Project entity) {
        projectRepository.saveOrUpdate(entity);
    }

    @Override
    public List<Project> findAllWithEagerFetching() {
        List<Project> projects = projectRepository.findAll();

        //fetch collections
        for (Project project : projects) {
            Hibernate.initialize(project.getExperiments());
            for (Experiment experiment : project.getExperiments()) {
                Hibernate.initialize(experiment.getSamples());
                for (Sample sample : experiment.getSamples()) {
                    Hibernate.initialize(sample.getAnalyticalRuns());
                }
            }
        }

        return projects;
    }

    @Override
    public User getUserWithMostProjectOwns() {
        return projectRepository.getUserWithMostProjectOwns();
    }

    @Override
    public Project findByTitle(final String title) {
        return projectRepository.findByTitle(title);
    }

    @Override
    public long countAll() {
        return projectRepository.countAll();
    }
}
