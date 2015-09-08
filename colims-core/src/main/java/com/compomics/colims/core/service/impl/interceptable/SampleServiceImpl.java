package com.compomics.colims.core.service.impl.interceptable;

import com.compomics.colims.core.service.SampleService;
import com.compomics.colims.model.Protocol;
import com.compomics.colims.model.Sample;
import com.compomics.colims.repository.SampleRepository;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.LazyInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Kenneth Verheggen
 */
@Service("sampleService")
@Transactional
public class SampleServiceImpl implements SampleService {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(SampleServiceImpl.class);

    @Autowired
    private SampleRepository sampleRepository;

    @Override
    public Sample findById(final Long id) {
        return sampleRepository.findById(id);
    }

    @Override
    public List<Sample> findAll() {
        return sampleRepository.findAll();
    }

    @Override
    public void save(final Sample entity) {
        sampleRepository.save(entity);
    }

    @Override
    public void update(final Sample entity) {
        sampleRepository.update(entity);
    }

    @Override
    public void saveOrUpdate(final Sample entity) {
        sampleRepository.saveOrUpdate(entity);
    }

    @Override
    public void delete(final Sample entity) {
        sampleRepository.delete(entity);
    }

    @Override
    public long countAll() {
        return sampleRepository.countAll();
    }

    @Override
    public void fetchBinaryFiles(final Sample sample) {
        try {
            sample.getBinaryFiles().size();
        } catch (LazyInitializationException e) {
            //attach the sample to the new session
            sampleRepository.saveOrUpdate(sample);
            if (!Hibernate.isInitialized(sample.getBinaryFiles())) {
                Hibernate.initialize(sample.getBinaryFiles());
            }
        }
    }

    @Override
    public Protocol getMostUsedProtocol() {
        return sampleRepository.getMostUsedProtocol();
    }

    @Override
    public void fetchMaterials(final Sample sample) {
        try {
            sample.getMaterials().size();
        } catch (LazyInitializationException e) {
            //attach the sample to the new session
            sampleRepository.saveOrUpdate(sample);
            if (!Hibernate.isInitialized(sample.getMaterials())) {
                Hibernate.initialize(sample.getMaterials());
            }
        }
    }

    @Override
    public void deleteById(Long id) {
        Sample sampleToDelete = sampleRepository.findById(id);
        delete(sampleToDelete);
    }

}
