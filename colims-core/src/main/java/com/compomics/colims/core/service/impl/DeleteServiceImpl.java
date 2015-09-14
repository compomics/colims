package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.distributed.model.DeleteDbTask;
import com.compomics.colims.core.service.DeleteService;
import com.compomics.colims.model.*;
import com.compomics.colims.repository.*;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Niels Hulstaert on 9/09/15.
 */
@Service("deleteService")
@Transactional
public class DeleteServiceImpl implements DeleteService {

    @Autowired
    private ProteinRepository proteinRepository;
    @Autowired
    private ModificationRepository modificationRepository;
    @Autowired
    private SearchParametersRepository searchParametersRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ExperimentRepository experimentRepository;
    @Autowired
    private SampleRepository sampleRepository;
    @Autowired
    private AnalyticalRunRepository analyticalRunRepository;

    @Override
    public void delete(DeleteDbTask deleteDbTask) {
        //get the analytical runs of the entity to delete
        List<AnalyticalRun> analyticalRuns = getAnalyticalRuns(deleteDbTask.getDbEntityClass(), deleteDbTask.getEnitityId());
        //cascade delete the entity up onto the analytical run level
        if (!deleteDbTask.getDbEntityClass().equals(AnalyticalRun.class)) {
            deleteEntity(deleteDbTask.getDbEntityClass(), deleteDbTask.getEnitityId());
        }

        //collect the IDs of the constraint less proteins, modifications and search parameters
        //that can be deleted after the deletion of the analytical runs
        List<Long> runIds = analyticalRuns.stream().map(AnalyticalRun::getId).collect(Collectors.toList());
        List<Long> proteinIds = proteinRepository.getConstraintLessProteinIdsForRuns(runIds);
        List<Long> modificationIds = modificationRepository.getConstraintLessModificationIdsForRuns(runIds);
        List<Long> searchParametersIds = searchParametersRepository.getConstraintLessSearchParameterIdsForRuns(runIds);

        //delete the analytical runs
        analyticalRuns.forEach(analyticalRunRepository::delete);

        //delete the proteins
        for (Long proteinId : proteinIds) {
            Protein proteinToDelete = proteinRepository.findById(proteinId);
            proteinRepository.delete(proteinToDelete);
        }
        //delete the modifications
        for (Long modificationId : modificationIds) {
            Modification modificationToDelete = modificationRepository.findById(modificationId);
            modificationRepository.delete(modificationToDelete);
        }
        //delete the search parameters
        for (Long searchParametersId : searchParametersIds) {
            SearchParameters searchParametersToDelete = searchParametersRepository.findById(searchParametersId);
            searchParametersRepository.delete(searchParametersToDelete);
        }
    }

    /**
     * Cascade delete the given entity from the database.
     *
     * @param entityClass the class of the enitity to delete
     * @param entityId    the entity ID
     */
    private void deleteEntity(Class entityClass, Long entityId) {
        //get the enity from the database
        if (entityClass.equals(Project.class)) {
            Project projectToDelete = projectRepository.findById(entityId);
            projectRepository.delete(projectToDelete);
        } else if (entityClass.equals(Experiment.class)) {
            Experiment experimentToDelete = experimentRepository.findById(entityId);
            experimentRepository.delete(experimentToDelete);
        } else if (entityClass.equals(Sample.class)) {
            Sample sampleToDelete = sampleRepository.findById(entityId);
            sampleRepository.delete(sampleToDelete);
        } else {
            throw new IllegalArgumentException("Unsupported enity class passed to deleted: " + entityClass.getSimpleName());
        }
    }

    /**
     * @param entityClass the class of the entity to delete
     * @param entityId    the ID of the entity to delete
     * @return the list of analytical runs attached to the given entity
     */
    private List<AnalyticalRun> getAnalyticalRuns(Class entityClass, Long entityId) {
        List<AnalyticalRun> analyticalRuns = new ArrayList<>();

        //get the enity from the database and fetch the children up to the analytical run level
        if (entityClass.equals(Project.class)) {
            Project projectToDelete = projectRepository.findById(entityId);
            //fetch children
            Hibernate.initialize(projectToDelete.getExperiments());
            for (Experiment experiment : projectToDelete.getExperiments()) {
                Hibernate.initialize(experiment.getSamples());
                for (Sample sample : experiment.getSamples()) {
                    Hibernate.initialize(sample.getAnalyticalRuns());
                    analyticalRuns.addAll(sample.getAnalyticalRuns());
                }
            }
        } else if (entityClass.equals(Experiment.class)) {
            Experiment experimentToDelete = experimentRepository.findById(entityId);
            //fetch children
            Hibernate.initialize(experimentToDelete.getSamples());
            for (Sample sample : experimentToDelete.getSamples()) {
                Hibernate.initialize(sample.getAnalyticalRuns());
                analyticalRuns.addAll(sample.getAnalyticalRuns());
            }
        } else if (entityClass.equals(Sample.class)) {
            Sample sampleToDelete = sampleRepository.findById(entityId);
            //fetch children
            Hibernate.initialize(analyticalRuns);
            analyticalRuns.addAll(sampleToDelete.getAnalyticalRuns());
        } else if (entityClass.equals(AnalyticalRun.class)) {
            analyticalRuns.add(analyticalRunRepository.findById(entityId));
        } else {
            throw new IllegalArgumentException("Unsupported enity class passed to deleted: " + entityClass.getSimpleName());
        }

        return analyticalRuns;
    }
}
