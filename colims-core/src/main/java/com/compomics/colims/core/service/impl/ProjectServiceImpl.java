/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.service.impl;

import com.compomics.colims.model.Project;
import com.compomics.colims.repository.ProjectRepository;
import com.compomics.colims.core.service.ProjectService;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.Experiment;
import com.compomics.colims.model.Sample;
import java.io.Serializable;
import java.util.List;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Project findById(Long id) {
        return projectRepository.findById(id);
    }

    @Override
    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    @Override
    public void save(Project entity) {
        projectRepository.save(entity);
    }

    @Override
    public void delete(Project entity) {
        projectRepository.delete(entity);
    }

    @Override
    public void update(Project entity) {
        update(entity);
    }

    @Override
    public void saveOrUpdate(Project entity) {
        saveOrUpdate(entity);
    }

    @Override
    public List<Project> findAllWithEagerFetching() {
        List<Project> projects = projectRepository.findAll();
        
        //fetch collections
        for (Project project : projects) {
            //Hibernate.initialize(project.getExperiments());
            project.getExperiments().size();
            for(Experiment experiment : project.getExperiments()){
                //Hibernate.initialize(experiment.getSamples());
                experiment.getSamples().size();
                for(Sample sample : experiment.getSamples()){
                    //Hibernate.initialize(sample.getAnalyticalRuns());                    
                    sample.getAnalyticalRuns().size();
                }
            }
        }

        return projects;
    }
}
