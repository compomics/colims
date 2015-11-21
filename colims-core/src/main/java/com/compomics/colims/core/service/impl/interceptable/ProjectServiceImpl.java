/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.service.impl.interceptable;

import com.compomics.colims.core.service.ProjectService;
import com.compomics.colims.model.Experiment;
import com.compomics.colims.model.Project;
import com.compomics.colims.model.Sample;
import com.compomics.colims.model.User;
import com.compomics.colims.repository.ProjectRepository;
import org.hibernate.LazyInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
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
    public List<Project> findAllWithEagerFetching() {
        List<Project> projects = projectRepository.findAllWithEagerFetching();
        for (Project project : projects) {
            for (Experiment experiment : project.getExperiments()) {
                for (Sample sample : experiment.getSamples()) {
                    sample.getAnalyticalRuns().size();
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

    @Override
    public void persist(Project entity) {
        projectRepository.persist(entity);
    }

    @Override
    public Project merge(Project entity) {
        return projectRepository.merge(entity);
    }

    @Override
    public void remove(Project entity) {
        projectRepository.remove(entity);
    }

    @Override
    public Project fetchUsers(Project project) {
        try {
            project.getUsers().size();
            return project;
        } catch (LazyInitializationException e) {
            //merge the project
            Project merge = projectRepository.merge(project);
            merge.getUsers().size();
            return merge;
        }
    }

    @Override
    public void saveOrUpdate(Project project) {
        projectRepository.saveOrUpdate(project);
    }
}
