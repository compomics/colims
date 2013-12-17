package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.service.SampleService;
import com.compomics.colims.model.Protocol;
import com.compomics.colims.model.Sample;
import com.compomics.colims.repository.SampleRepository;
import java.util.List;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kenneth Verheggen
 */
@Service("sampleService")
@Transactional
public class SampleServiceImpl implements SampleService {

    private static final Logger LOGGER = Logger.getLogger(SampleServiceImpl.class);

    @Autowired
    private SampleRepository sampleRepository;

    @Override
    public List<Sample> findSampleByExperimentId(Long experimentId) {
        return sampleRepository.findSampleByExperimentId(experimentId);
    }

    @Override
    public Sample findById(Long id) {
        return sampleRepository.findById(id);
    }

    @Override
    public List<Sample> findAll() {
        return sampleRepository.findAll();
    }

    @Override
    public void save(Sample entity) {
        sampleRepository.save(entity);
    }

    @Override
    public void update(Sample entity) {
        sampleRepository.update(entity);
    }

    @Override
    public void saveOrUpdate(Sample entity) {
        sampleRepository.saveOrUpdate(entity);
    }

    @Override
    public void delete(Sample entity) {
        sampleRepository.delete(entity);
    }

    @Override
    public long countAll() {
        return sampleRepository.countAll();
    }

    @Override
    public void fetchBinaryFiles(Sample sample) {
        try {
            //attach the experiment to the new session
            sampleRepository.saveOrUpdate(sample);
            if (!Hibernate.isInitialized(sample.getBinaryFiles())) {
                Hibernate.initialize(sample.getBinaryFiles());
            }
        } catch (HibernateException hbe) {
            LOGGER.error(hbe, hbe.getCause());
        }
    }

    @Override
    public Protocol getMostUsedProtocol() {
        return sampleRepository.getMostUsedProtocol();
    }
    
}
