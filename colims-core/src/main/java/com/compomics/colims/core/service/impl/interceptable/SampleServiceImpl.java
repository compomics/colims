package com.compomics.colims.core.service.impl.interceptable;

import com.compomics.colims.core.service.SampleService;
import com.compomics.colims.model.Protocol;
import com.compomics.colims.model.Sample;
import com.compomics.colims.repository.SampleRepository;
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
    public long countAll() {
        return sampleRepository.countAll();
    }

    @Override
    public void persist(Sample entity) {
        sampleRepository.persist(entity);
    }

    @Override
    public Sample merge(Sample entity) {
        return sampleRepository.merge(entity);
    }

    @Override
    public void remove(Sample entity) {
        sampleRepository.remove(entity);
    }

    @Override
    public Sample fetchBinaryFiles(final Sample sample) {
        try {
            sample.getBinaryFiles().size();
            return sample;
        } catch (LazyInitializationException e) {
            //merge the sample
            Sample merge = sampleRepository.merge(sample);
            sample.getBinaryFiles().size();
            return merge;
        }
    }

    @Override
    public Protocol getMostUsedProtocol() {
        return sampleRepository.getMostUsedProtocol();
    }

    @Override
    public Sample fetchMaterials(final Sample sample) {
        try {
            sample.getMaterials().size();
            return sample;
        } catch (LazyInitializationException e) {
            //merge the sample
            Sample merge = sampleRepository.merge(sample);
            sample.getMaterials().size();
            return merge;
        }
    }

}
