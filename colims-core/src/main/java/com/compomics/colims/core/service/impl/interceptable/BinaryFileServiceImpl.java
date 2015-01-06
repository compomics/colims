package com.compomics.colims.core.service.impl.interceptable;

import com.compomics.colims.core.service.BinaryFileService;
import com.compomics.colims.model.BinaryFile;
import com.compomics.colims.repository.BinaryFileRepository;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
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
    public void save(final BinaryFile entity) {
        binaryFileRepository.save(entity);
    }

    @Override
    public void update(final BinaryFile entity) {
        binaryFileRepository.update(entity);
    }

    @Override
    public void saveOrUpdate(final BinaryFile entity) {
        binaryFileRepository.saveOrUpdate(entity);
    }

    @Override
    public void delete(final BinaryFile entity) {
        binaryFileRepository.delete(entity);
    }

    @Override
    public long countAll() {
        return binaryFileRepository.countAll();
    }

}
