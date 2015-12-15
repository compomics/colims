package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.distributed.model.DeleteDbTask;
import com.compomics.colims.core.service.DeleteService;
import com.compomics.colims.model.*;
import com.compomics.colims.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This service cascade deletes an entity (Project, Experiment, Sample,
 * AnalyticalRun) from the database.
 * <p/>
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
    private SearchModificationRepository searchModificationRepository;
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
        //fetch the analytical runs of the entity to delete
        List<AnalyticalRun> analyticalRuns = fetchAnalyticalRuns(deleteDbTask.getDbEntityClass(), deleteDbTask.getEnitityId());
        //cascade delete the entity up onto the analytical run level
        if (!deleteDbTask.getDbEntityClass().equals(AnalyticalRun.class)) {
            deleteEntity(deleteDbTask.getDbEntityClass(), deleteDbTask.getEnitityId());
        }

        //collect the IDs of the constraint less proteins, modifications, search parameters and search parameters
        //that can be deleted after the deletion of the analytical runs
        List<Long> runIds = analyticalRuns.stream().map(AnalyticalRun::getId).collect(Collectors.toList());

        List<Long> proteinIds = new ArrayList<>();
        List<Long> modificationIds = new ArrayList<>();
        List<Long> searchParametersIds = new ArrayList<>();
        List<Long> searchModificationIds = new ArrayList<>();
        if (!runIds.isEmpty()) {
            proteinIds = proteinRepository.getConstraintLessProteinIdsForRuns(runIds);
            modificationIds = modificationRepository.getConstraintLessModificationIdsForRuns(runIds);
            searchParametersIds = searchParametersRepository.getConstraintLessSearchParameterIdsForRuns(runIds);
            searchModificationIds = searchModificationRepository.getConstraintLessSearchModificationIdsForRuns(runIds);
        }

        //delete the analytical runs
        analyticalRuns.forEach(analyticalRunRepository::remove);

        //delete the proteins
        for (Long proteinId : proteinIds) {
            Protein proteinToDelete = proteinRepository.findById(proteinId);
            proteinRepository.remove(proteinToDelete);
        }
        //delete the modifications
        for (Long modificationId : modificationIds) {
            Modification modificationToDelete = modificationRepository.findById(modificationId);
            modificationRepository.remove(modificationToDelete);
        }
        //delete the search parameters
        for (Long searchParametersId : searchParametersIds) {
            SearchParameters searchParametersToDelete = searchParametersRepository.findById(searchParametersId);
            searchParametersRepository.remove(searchParametersToDelete);
        }
        //delete the search modifications
        for (Long searchModificationId : searchModificationIds) {
            SearchModification searchModificationToDelete = searchModificationRepository.findById(searchModificationId);
            searchModificationRepository.remove(searchModificationToDelete);
        }
    }

    /**
     * Cascade delete the given entity from the database.
     *
     * @param entityClass the class of the enitity to delete
     * @param entityId the entity ID
     */
    private void deleteEntity(Class entityClass, Long entityId) {
        //get the entity from the database
        if (entityClass.equals(Project.class)) {
            Project projectToDelete = projectRepository.findById(entityId);
            if (projectToDelete != null) {
                projectRepository.remove(projectToDelete);
            }
        } else if (entityClass.equals(Experiment.class)) {
            Experiment experimentToDelete = experimentRepository.findById(entityId);
            if (experimentToDelete != null) {
                experimentRepository.remove(experimentToDelete);
            }
        } else if (entityClass.equals(Sample.class)) {
            Sample sampleToDelete = sampleRepository.findById(entityId);
            if (sampleToDelete != null) {
                sampleRepository.remove(sampleToDelete);
            }
        } else {
            throw new IllegalArgumentException("Unsupported entity class passed to delete: " + entityClass.getSimpleName());
        }
    }

    /**
     * Fetch the analytical runs associated with the given entity. Returns an
     * empty list of none were found.
     *
     * @param entityClass the class of the entity to delete
     * @param entityId the ID of the entity to delete
     * @return the list of analytical runs attached to the given entity
     */
    private List<AnalyticalRun> fetchAnalyticalRuns(Class entityClass, Long entityId) {
        List<AnalyticalRun> analyticalRuns = new ArrayList<>();

        //get the entity from the database and fetch the children up to the analytical run level
        if (entityClass.equals(Project.class)) {
            Project projectToDelete = projectRepository.findByIdWithFetchedExperiments(entityId);
            //fetch samples and runs
            projectToDelete.getExperiments().stream().forEach(experiment -> {
                experiment.getSamples().size();
                experiment.getSamples().stream().forEach(sample -> {
                    sample.getAnalyticalRuns().size();
                    analyticalRuns.addAll(sample.getAnalyticalRuns());
                });
            });
        } else if (entityClass.equals(Experiment.class)) {
            Experiment experimentToDelete = experimentRepository.findByIdWithFetchedSamples(entityId);
            //fetch runs
            experimentToDelete.getSamples().stream().forEach(sample -> {
                sample.getAnalyticalRuns().size();
                analyticalRuns.addAll(sample.getAnalyticalRuns());
            });
        } else if (entityClass.equals(Sample.class)) {
            Sample sampleToDelete = sampleRepository.findByIdWithFetchedRuns(entityId);
            analyticalRuns.addAll(sampleToDelete.getAnalyticalRuns());
        } else if (entityClass.equals(AnalyticalRun.class)) {
            analyticalRuns.add(analyticalRunRepository.findById(entityId));
        } else {
            throw new IllegalArgumentException("Unsupported entity class passed to delete: " + entityClass.getSimpleName());
        }

        return analyticalRuns;
    }
}
