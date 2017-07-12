/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.service.QuantificationReagentService;
import com.compomics.colims.model.QuantificationReagent;
import com.compomics.colims.repository.QuantificationReagentRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author demet
 */
@Service("quantificationReagentService")
@Transactional
public class QuantificationReagentServiceImpl implements QuantificationReagentService {

    private final QuantificationReagentRepository quantificationReagentRepository;

    @Autowired
    public QuantificationReagentServiceImpl(QuantificationReagentRepository quantificationReagentRepository) {
        this.quantificationReagentRepository = quantificationReagentRepository;
    }

    @Override
    public QuantificationReagent getQuantificationReagent(QuantificationReagent quantificationReagent) {
        //find by example
        List<QuantificationReagent> quantificationReagents = quantificationReagentRepository.findByExample(quantificationReagent);

        if (!quantificationReagents.isEmpty()) {
            return quantificationReagents.get(0);
        } else {
            //save the given instance
            quantificationReagentRepository.persist(quantificationReagent);
            return quantificationReagent;
        }
    }

    @Override
    public QuantificationReagent findById(Long id) {
        return quantificationReagentRepository.findById(id);
    }

    @Override
    public List<QuantificationReagent> findAll() {
        return quantificationReagentRepository.findAll();
    }

    @Override
    public long countAll() {
        return quantificationReagentRepository.countAll();
    }

    @Override
    public void persist(QuantificationReagent entity) {
        quantificationReagentRepository.persist(entity);
    }

    @Override
    public QuantificationReagent merge(QuantificationReagent entity) {
        return quantificationReagentRepository.merge(entity);
    }

    @Override
    public void remove(QuantificationReagent entity) {
        quantificationReagentRepository.remove(entity);
    }

}
