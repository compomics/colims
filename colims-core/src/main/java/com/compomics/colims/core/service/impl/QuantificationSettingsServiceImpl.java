package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.service.QuantificationSettingsService;
import com.compomics.colims.model.QuantificationEngine;
import com.compomics.colims.model.QuantificationParameterSettings;
import com.compomics.colims.model.QuantificationSettings;
import com.compomics.colims.model.enums.QuantificationEngineType;
import com.compomics.colims.repository.QuantificationEngineRepository;
import com.compomics.colims.repository.QuantificationParameterSettingsRepository;
import com.compomics.colims.repository.QuantificationSettingsRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
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
    private QuantificationParameterSettingsRepository quantificationParameterSettingsRepository;

    @Override
    public QuantificationSettings findById(final Long id) {
        return quantificationSettingsRepository.findById(id);
    }

    @Override
    public List<QuantificationSettings> findAll() {
        return quantificationSettingsRepository.findAll();
    }

    @Override
    public void save(final QuantificationSettings entity) {
        quantificationSettingsRepository.save(entity);
    }

    @Override
    public void update(final QuantificationSettings entity) {
        quantificationSettingsRepository.update(entity);
    }

    @Override
    public void saveOrUpdate(final QuantificationSettings entity) {
        quantificationSettingsRepository.saveOrUpdate(entity);
    }

    @Override
    public void delete(final QuantificationSettings entity) {
        quantificationSettingsRepository.delete(entity);
    }

    @Override
    public long countAll() {
        return quantificationSettingsRepository.countAll();
    }

    @Override
    public QuantificationEngine getQuantificationEngine(QuantificationEngineType quantificationEngineType, String version) {
        QuantificationEngine quantificationEngine = quantificationEngineRepository.findByNameAndVersion(quantificationEngineType, version);

        if (quantificationEngine == null) {
            quantificationEngine = new QuantificationEngine(quantificationEngineType, version);
            quantificationEngineRepository.save(quantificationEngine);
        }

        return quantificationEngine;
    }

    @Override
    public QuantificationParameterSettings getQuantificationParamterSettings(QuantificationParameterSettings quantificationParameterSettings) {
        //find QuantificationParameterSettings by example
        List<QuantificationParameterSettings> quantificationParameterSettingses = quantificationParameterSettingsRepository.findByExample(quantificationParameterSettings);
        if (!quantificationParameterSettingses.isEmpty()) {
            return quantificationParameterSettingses.get(0);
        } else {
            //save the given instance
            quantificationParameterSettingsRepository.save(quantificationParameterSettings);
            return quantificationParameterSettings;
        }
    }

}
