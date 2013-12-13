package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.service.AnalyticalRunService;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.repository.AnalyticalRunRepository;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kenneth Verheggen
 */
@Service("analyticalRunService")
@Transactional
public class AnalyticalRunServiceImpl implements AnalyticalRunService {

    private static final Logger LOGGER = Logger.getLogger(AnalyticalRunServiceImpl.class);

    @Autowired
    private AnalyticalRunRepository analyticalRunRepository;

    @Override
    public List<AnalyticalRun> findAnalyticalRunsBySampleId(Long sampleId) {
        return analyticalRunRepository.findAnalyticalRunsBySampleId(sampleId);
    }

    @Override
    public AnalyticalRun findById(Long id) {
        return analyticalRunRepository.findById(id);
    }

    @Override
    public List<AnalyticalRun> findAll() {
        return analyticalRunRepository.findAll();
    }

    @Override
    public void save(AnalyticalRun entity) {
        analyticalRunRepository.save(entity);
    }

    @Override
    public void update(AnalyticalRun entity) {
        analyticalRunRepository.update(entity);
    }

    @Override
    public void saveOrUpdate(AnalyticalRun entity) {
        analyticalRunRepository.saveOrUpdate(entity);
    }

    @Override
    public void delete(AnalyticalRun entity) {
        analyticalRunRepository.delete(entity);
    }

    @Override
    public long countAll() {
        return analyticalRunRepository.countAll();
    }

}
