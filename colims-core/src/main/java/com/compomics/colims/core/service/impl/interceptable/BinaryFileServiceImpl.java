package com.compomics.colims.core.service.impl.interceptable;

import com.compomics.colims.core.service.BinaryFileService;
import com.compomics.colims.model.BinaryFile;
import com.compomics.colims.model.SampleBinaryFile;
import com.compomics.colims.repository.BinaryFileRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Niels Hulstaert
 */
@Service("binaryFileService")
@Transactional
public class BinaryFileServiceImpl implements BinaryFileService {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(BinaryFileServiceImpl.class);

    @Autowired
    private BinaryFileRepository binaryFileRepository;

    @Override
    public BinaryFile findById(final Long id) {
        return binaryFileRepository.findById(id);
    }

    @Override
    public List<BinaryFile> findAll() {
        return binaryFileRepository.findAll();
    }

    @Override
    public long countAll() {
        return binaryFileRepository.countAll();
    }

    @Override
    public void persist(BinaryFile entity) {
        binaryFileRepository.persist(entity);
    }

    @Override
    public BinaryFile merge(BinaryFile entity) {
        return binaryFileRepository.merge(entity);
    }

    @Override
    public void remove(BinaryFile entity) {
        BinaryFile merge = binaryFileRepository.merge(entity);
        binaryFileRepository.remove(merge);
    }

}
