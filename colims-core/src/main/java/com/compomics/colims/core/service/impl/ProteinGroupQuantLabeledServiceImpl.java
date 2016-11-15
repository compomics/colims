/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.service.ProteinGroupQuantLabeledService;
import com.compomics.colims.model.ProteinGroupQuantLabeled;
import com.compomics.colims.repository.ProteinGroupQuantLabeledRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author demet
 */
@Service("proteinGroupQuantLabeledService")
@Transactional
public class ProteinGroupQuantLabeledServiceImpl implements ProteinGroupQuantLabeledService{

    final ProteinGroupQuantLabeledRepository proteinGroupQuantLabeledRepository;

    @Autowired
    public ProteinGroupQuantLabeledServiceImpl(ProteinGroupQuantLabeledRepository proteinGroupQuantLabeledRepository) {
        this.proteinGroupQuantLabeledRepository = proteinGroupQuantLabeledRepository;
    }

    
    @Override
    public List<ProteinGroupQuantLabeled> getProteinGroupQuantLabeledForRun(Long analyticalRunId) {
        return proteinGroupQuantLabeledRepository.getProteinGroupQuantLabeledForRun(analyticalRunId);
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
