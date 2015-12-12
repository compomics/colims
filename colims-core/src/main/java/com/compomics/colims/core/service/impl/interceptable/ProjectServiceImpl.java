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
        List<Project> projects = projectRepository.findAllWithFetchedExperiments();
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
    public Long countByTitle(final Project project) {
        return projectRepository.countByTitle(project);
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
    public void fetchUsers(Project project) {
        try {
            project.getUsers().size();
        } catch (LazyInitializationException e) {
            //fetch the users
            List<User> users = projectRepository.fetchUsers(project.getId());
            project.setUsers(users);
        }
    }

}
