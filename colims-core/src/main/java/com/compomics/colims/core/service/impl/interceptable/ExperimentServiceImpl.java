/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.service.impl.interceptable;

import com.compomics.colims.core.service.ExperimentService;
import com.compomics.colims.model.Experiment;
import com.compomics.colims.model.ExperimentBinaryFile;
import com.compomics.colims.repository.ExperimentRepository;
import org.hibernate.LazyInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Niels Hulstaert
 */
@Service("experimentService")
@Transactional
public class ExperimentServiceImpl implements ExperimentService {

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
    public long countAll() {
        return experimentRepository.countAll();
    }

    @Override
    public void persist(Experiment entity) {
        experimentRepository.persist(entity);
    }

    @Override
    public Experiment merge(Experiment entity) {
        return experimentRepository.merge(entity);
    }

    @Override
    public void remove(Experiment entity) {
        experimentRepository.remove(entity);
    }

    @Override
    public Long countByProjectIdAndTitle(final Long projectId, final Experiment experiment) {
        return experimentRepository.countByProjectIdAndTitle(projectId, experiment);
    }

    @Override
    public void fetchBinaryFiles(final Experiment experiment) {
        try {
            experiment.getBinaryFiles().size();
        } catch (LazyInitializationException e) {
            //fetch the binary files
            List<ExperimentBinaryFile> binaryFiles = experimentRepository.fetchBinaryFiles(experiment.getId());
            experiment.setBinaryFiles(binaryFiles);
        }
    }

    @Override
    public Experiment findByIdWithEagerFetching(Long experimentId) {
        Experiment experiment = experimentRepository.findByIdWithFetchedSamples(experimentId);
        experiment.getSamples().stream()
                .forEach(sample -> sample.getAnalyticalRuns().size());

        return experiment;
    }
}
