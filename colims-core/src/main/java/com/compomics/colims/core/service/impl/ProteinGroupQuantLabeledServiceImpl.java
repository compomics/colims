/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.service.ProteinGroupQuantLabeledService;
import com.compomics.colims.model.ProteinGroupQuantLabeled;
import com.compomics.colims.repository.ProteinGroupQuantLabeledRepository;

import java.io.IOException;
import java.util.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author demet
 */
@Service("proteinGroupQuantLabeledService")
@Transactional
public class ProteinGroupQuantLabeledServiceImpl implements ProteinGroupQuantLabeledService {

    private static final Logger LOGGER = Logger.getLogger(ProteinGroupQuantLabeledServiceImpl.class);

    private final ObjectMapper mapper = new ObjectMapper();
    private final ProteinGroupQuantLabeledRepository proteinGroupQuantLabeledRepository;

    @Autowired
    public ProteinGroupQuantLabeledServiceImpl(ProteinGroupQuantLabeledRepository proteinGroupQuantLabeledRepository) {
        this.proteinGroupQuantLabeledRepository = proteinGroupQuantLabeledRepository;
    }

    @Override
    public List<String> getProteinGroupQuantLabelsForRun(Long analyticalRunId, int numberOfLabels) {
        List<String> labels = new ArrayList<>();

        ProteinGroupQuantLabeled proteinGroupQuantLabeledForRun = proteinGroupQuantLabeledRepository.getProteinGroupQuantLabeledForRun(analyticalRunId);
        if (proteinGroupQuantLabeledForRun != null) {
            try {
                //deserialize the JSON file
                LinkedHashMap<String, Double> intensities = mapper.readValue(proteinGroupQuantLabeledForRun.getLabels(), new TypeReference<LinkedHashMap<String, Double>>() {
                });
                Iterator<String> labelIterator = intensities.keySet().iterator();
                for (int i = 0; i < numberOfLabels && labelIterator.hasNext(); i++) {
                    labels.add(labelIterator.next());
                }
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

        return labels;
    }

    @Override
    public ProteinGroupQuantLabeled findById(Long id) {
        return proteinGroupQuantLabeledRepository.findById(id);
    }

    @Override
    public List<ProteinGroupQuantLabeled> findAll() {
        return proteinGroupQuantLabeledRepository.findAll();
    }

    @Override
    public long countAll() {
        return proteinGroupQuantLabeledRepository.countAll();
    }

    @Override
    public void persist(ProteinGroupQuantLabeled entity) {
        proteinGroupQuantLabeledRepository.persist(entity);
    }

    @Override
    public ProteinGroupQuantLabeled merge(ProteinGroupQuantLabeled entity) {
        return proteinGroupQuantLabeledRepository.merge(entity);
    }

    @Override
    public void remove(ProteinGroupQuantLabeled entity) {
        proteinGroupQuantLabeledRepository.remove(entity);
    }

    @Override
    public List<ProteinGroupQuantLabeled> getProteinGroupQuantLabeledForRunAndProteinGroup(Long analyticalRunId, Long proteinGroupId) {
        return proteinGroupQuantLabeledRepository.getProteinGroupQuantLabeledForRunAndProteinGroup(analyticalRunId, proteinGroupId);
    }

}
