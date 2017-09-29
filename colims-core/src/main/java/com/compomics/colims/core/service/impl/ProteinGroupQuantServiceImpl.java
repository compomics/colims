/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.service.ProteinGroupQuantService;
import com.compomics.colims.model.ProteinGroupQuant;
import com.compomics.colims.repository.ProteinGroupQuantRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author demet
 */
@Service("proteinGroupQuantService")
@Transactional
public class ProteinGroupQuantServiceImpl implements ProteinGroupQuantService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProteinGroupQuantServiceImpl.class);

    private final ObjectMapper mapper = new ObjectMapper();
    private final ProteinGroupQuantRepository proteinGroupQuantRepository;

    @Autowired
    public ProteinGroupQuantServiceImpl(ProteinGroupQuantRepository proteinGroupQuantRepository) {
        this.proteinGroupQuantRepository = proteinGroupQuantRepository;
    }

    @Override
    public ProteinGroupQuant findById(Long id) {
        return proteinGroupQuantRepository.findById(id);
    }

    @Override
    public List<ProteinGroupQuant> findAll() {
        return proteinGroupQuantRepository.findAll();
    }

    @Override
    public long countAll() {
        return proteinGroupQuantRepository.countAll();
    }

    @Override
    public void persist(ProteinGroupQuant entity) {
        proteinGroupQuantRepository.persist(entity);
    }

    @Override
    public ProteinGroupQuant merge(ProteinGroupQuant entity) {
        return proteinGroupQuantRepository.merge(entity);
    }

    @Override
    public void remove(ProteinGroupQuant entity) {
        proteinGroupQuantRepository.remove(entity);
    }

    @Override
    public ProteinGroupQuant getProteinGroupQuantForRunAndProteinGroup(Long analyticalRunId, Long proteinGroupId) {
        return proteinGroupQuantRepository.getProteinGroupQuantForRunAndProteinGroup(analyticalRunId, proteinGroupId);
    }

    @Override
    public List<String> getProteinGroupQuantLabelsForRun(Long analyticalRunId, int numberOfLabels) {
        List<String> labels = new ArrayList<>();

        ProteinGroupQuant proteinGroupQuantLabeledForRun = proteinGroupQuantRepository.getProteinGroupQuantLabeledForRun(analyticalRunId);
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
}
