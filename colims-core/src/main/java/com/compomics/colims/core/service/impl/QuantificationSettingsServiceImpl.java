package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.service.QuantificationSettingsService;
import com.compomics.colims.model.QuantificationEngine;
import com.compomics.colims.model.QuantificationMethodCvParam;
import com.compomics.colims.model.QuantificationSettings;
import com.compomics.colims.model.enums.QuantificationEngineType;
import com.compomics.colims.repository.QuantificationEngineRepository;
import com.compomics.colims.repository.QuantificationSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import com.compomics.colims.repository.QuantificationMethodCvParamRepository;

/**
 * @author Niels Hulstaert
 */
@Service("quantificationSettingsService")
@Transactional
public class QuantificationSettingsServiceImpl implements QuantificationSettingsService {

    @Autowired
    private QuantificationSettingsRepository quantificationSettingsRepository;
    @Autowired
    private QuantificationEngineRepository quantificationEngineRepository;
    @Autowired
    private QuantificationMethodCvParamRepository quantificationMethodCvParamRepository;

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
            quantificationEngine = new QuantificationEngine(quantificationEngineType, version);
            quantificationEngineRepository.persist(quantificationEngine);
        }

        return quantificationEngine;
    }

    @Override
    public QuantificationMethodCvParam getQuantificationMethodCvParams(QuantificationMethodCvParam quantificationMethodCvParam) {
        //find QuantificationMethodCvParam by example
        List<QuantificationMethodCvParam> quantificationMethodCvParams = quantificationMethodCvParamRepository.findByExample(quantificationMethodCvParam);
        if (!quantificationMethodCvParams.isEmpty()) {
            return quantificationMethodCvParams.get(0);
        } else {
            //save the given instance
            quantificationMethodCvParamRepository.persist(quantificationMethodCvParam);
            return quantificationMethodCvParam;
        }
    }

}
