package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.service.QuantificationSettingsService;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.QuantificationEngine;
import com.compomics.colims.model.QuantificationMethod;
import com.compomics.colims.model.QuantificationSettings;
import com.compomics.colims.model.enums.QuantificationEngineType;
import com.compomics.colims.repository.QuantificationEngineRepository;
import com.compomics.colims.repository.QuantificationMethodRepository;
import com.compomics.colims.repository.QuantificationSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Niels Hulstaert
 */
@Service("quantificationSettingsService")
@Transactional
public class QuantificationSettingsServiceImpl implements QuantificationSettingsService {

    private final QuantificationSettingsRepository quantificationSettingsRepository;
    private final QuantificationEngineRepository quantificationEngineRepository;
    private final QuantificationMethodRepository quantificationMethodRepository;

    @Autowired
    public QuantificationSettingsServiceImpl(QuantificationSettingsRepository quantificationSettingsRepository, QuantificationEngineRepository quantificationEngineRepository, QuantificationMethodRepository quantificationMethodRepository) {
        this.quantificationSettingsRepository = quantificationSettingsRepository;
        this.quantificationEngineRepository = quantificationEngineRepository;
        this.quantificationMethodRepository = quantificationMethodRepository;
    }

    @Override
    public QuantificationSettings findById(final Long id) {
        return quantificationSettingsRepository.findById(id);
    }

    @Override
    public List<QuantificationSettings> findAll() {
        return quantificationSettingsRepository.findAll();
    }

    @Override
    public long countAll() {
        return quantificationSettingsRepository.countAll();
    }

    @Override
    public void persist(QuantificationSettings entity) {
        quantificationSettingsRepository.persist(entity);
    }

    @Override
    public QuantificationSettings merge(QuantificationSettings entity) {
        return quantificationSettingsRepository.merge(entity);
    }

    @Override
    public void remove(QuantificationSettings entity) {
        quantificationSettingsRepository.remove(entity);
    }

    @Override
    public QuantificationEngine getQuantificationEngine(QuantificationEngineType quantificationEngineType, String version) {
        QuantificationEngine quantificationEngine = quantificationEngineRepository.findByNameAndVersion(quantificationEngineType, version);

        if (quantificationEngine == null) {
            //check if the search engine can be found by type
            quantificationEngine = quantificationEngineRepository.findByType(quantificationEngineType);

            if (quantificationEngine != null) {
                //copy the found SearchEngine fields and the given version onto a new instance
                quantificationEngine = new QuantificationEngine(quantificationEngine, version);
            } else {
                //create a new instance with the type and version
                quantificationEngine = new QuantificationEngine(quantificationEngineType, version);
            }

            quantificationEngineRepository.persist(quantificationEngine);
        }
        
        return quantificationEngine;
    }

    @Override
    public QuantificationMethod getQuantificationMethodCvParams(QuantificationMethod quantificationMethod) {
        //find QuantificationMethodCvParam by example
        List<QuantificationMethod> quantificationMethods = quantificationMethodRepository.findByExample(quantificationMethod);
        if (!quantificationMethods.isEmpty()) {
            return quantificationMethods.get(0);
        } else {
            //save the given instance
            quantificationMethodRepository.persist(quantificationMethod);
            return quantificationMethod;
        }
    }

    @Override
    public QuantificationSettings getbyAnalyticalRun(AnalyticalRun analyticalRun) {
        return quantificationSettingsRepository.findbyAnalyticalRunId(analyticalRun.getId());
    }

}
