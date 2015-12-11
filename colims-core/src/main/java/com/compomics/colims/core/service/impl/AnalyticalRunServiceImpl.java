package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.service.AnalyticalRunService;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.repository.AnalyticalRunRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Kenneth Verheggen
 */
@Service("analyticalRunService")
@Transactional
public class AnalyticalRunServiceImpl implements AnalyticalRunService {

    @Autowired
    private AnalyticalRunRepository analyticalRunRepository;

    @Override
    public AnalyticalRun findById(final Long id) {
        return analyticalRunRepository.findById(id);
    }

    @Override
    public List<AnalyticalRun> findAll() {
        return analyticalRunRepository.findAll();
    }

    @Override
    public long countAll() {
        return analyticalRunRepository.countAll();
    }

    @Override
    public void persist(AnalyticalRun entity) {
        analyticalRunRepository.persist(entity);
    }

    @Override
    public AnalyticalRun merge(AnalyticalRun entity) {
        return analyticalRunRepository.merge(entity);
    }

    @Override
    public void remove(AnalyticalRun entity) {
        analyticalRunRepository.remove(entity);
    }

    @Override
    public List<AnalyticalRun> findBySampleId(Long sampleId) {
        return analyticalRunRepository.findBySampleId(sampleId);
    }
}
