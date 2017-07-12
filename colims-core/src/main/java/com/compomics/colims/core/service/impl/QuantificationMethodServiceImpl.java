/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.service.QuantificationMethodService;
import com.compomics.colims.model.QuantificationMethod;
import com.compomics.colims.model.QuantificationMethodHasReagent;
import com.compomics.colims.repository.QuantificationMethodRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author demet
 */
@Service("quantificationMethodService")
@Transactional
public class QuantificationMethodServiceImpl implements QuantificationMethodService {

    private final QuantificationMethodRepository quantificationMethodRepository;

    @Autowired
    public QuantificationMethodServiceImpl(QuantificationMethodRepository quantificationMethodRepository) {
        this.quantificationMethodRepository = quantificationMethodRepository;
    }

    @Override
    public List<QuantificationMethodHasReagent> fetchQuantificationMethodHasReagents(QuantificationMethod quantificationMethod) {
        return quantificationMethodRepository.fetchQuantificationMethodHasReagents(quantificationMethod.getId());
    }

    @Override
    public QuantificationMethod findById(Long id) {
        return quantificationMethodRepository.findById(id);
    }

    @Override
    public List<QuantificationMethod> findAll() {
        return quantificationMethodRepository.findAll();
    }

    @Override
    public long countAll() {
        return quantificationMethodRepository.countAll();
    }

    @Override
    public void persist(QuantificationMethod entity) {
        quantificationMethodRepository.persist(entity);
    }

    @Override
    public QuantificationMethod merge(QuantificationMethod entity) {
        return quantificationMethodRepository.merge(entity);
    }

    @Override
    public void remove(QuantificationMethod entity) {
        quantificationMethodRepository.remove(entity);
    }

}
