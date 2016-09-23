package com.compomics.colims.core.service.impl.interceptable;

import com.compomics.colims.core.service.SampleService;
import com.compomics.colims.model.Material;
import com.compomics.colims.model.Protocol;
import com.compomics.colims.model.Sample;
import com.compomics.colims.model.SampleBinaryFile;
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

    private final SampleRepository sampleRepository;

    @Autowired
    public SampleServiceImpl(SampleRepository sampleRepository) {
        this.sampleRepository = sampleRepository;
    }

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
    public void fetchMaterialsAndBinaryFiles(final Sample sample) {
        try {
            sample.getMaterials().size();
        } catch (LazyInitializationException e) {
            //fetch the materials
            List<Material> materials = sampleRepository.fetchMaterials(sample.getId());
            sample.setMaterials(materials);
        }
        try {
            sample.getBinaryFiles().size();
        } catch (LazyInitializationException e) {
            //fetch the binary files
            List<SampleBinaryFile> binaryFiles = sampleRepository.fetchBinaryFiles(sample.getId());
            sample.setBinaryFiles(binaryFiles);
        }
    }

    @Override
    public Protocol getMostUsedProtocol() {
        return sampleRepository.getMostUsedProtocol();
    }

    @Override
    public Object[] getParentIds(Long sampleId) {
        return sampleRepository.getParentIds(sampleId);
    }
}
