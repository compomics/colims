package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.distributed.model.DeleteDbTask;
import com.compomics.colims.core.service.DeleteService;
import com.compomics.colims.model.*;
import com.compomics.colims.repository.*;
import org.hibernate.Hibernate;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
        //delete the analytical runs
        for (AnalyticalRun analyticalRun : analyticalRuns) {
            deleteAnalyticalRun(analyticalRun);
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

    /**
     * Delete the analytical run entry from the database with cascading of related child entities.
     *
     * @param analyticalRun the AnalyticalRun instance to delete
     */
    private void deleteAnalyticalRun(AnalyticalRun analyticalRun) {
        //get the IDs of the proteins linked to the run
        List<Long> proteinIds = proteinRepository.getProteinIdsForRun(analyticalRun);
        //get the IDs of the modifications linked to this run
        List<Long> modificationIds = modificationRepository.getModificationIdsForRun(analyticalRun);
        //get the IDs of the search parameters linked to this run
        List<Long> searchParametersIds = searchParametersRepository.getSearchParameterIdsForRun(analyticalRun);

        //cascade delete the run
        analyticalRunRepository.delete(analyticalRun);

        //now try to delete the proteins, modifications and search parameters
        //catch the ConstraintViolationException thrown if an entity is still linked to other entities
        //iterate over the protein IDs and try to delete them
        for (Long proteinId : proteinIds) {
            Protein protein = proteinRepository.findById(proteinId);
            try {
                proteinRepository.delete(protein);
            } catch (ConstraintViolationException e) {
                //do nothing
            }
        }
        //iterate over the modification IDs and try to delete them
        for (Long modificationId : modificationIds) {
            Modification modification = modificationRepository.findById(modificationId);
            try {
                modificationRepository.delete(modification);
            } catch (ConstraintViolationException e) {
                //do nothing
            }
        }
        //iterate over the search parameter IDs and try to delete them
        for (Long searchParameterId : searchParametersIds) {
            SearchParameters searchParameters = searchParametersRepository.findById(searchParameterId);
            try {
                searchParametersRepository.delete(searchParameters);
            } catch (ConstraintViolationException e) {
                //do nothing
            }
        }

    }
}
