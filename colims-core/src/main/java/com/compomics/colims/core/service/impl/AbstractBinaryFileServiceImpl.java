package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.service.AbstractBinaryFileService;
import com.compomics.colims.model.AbstractBinaryFile;
import com.compomics.colims.repository.AbstractBinaryFileRepository;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kenneth Verheggen
 */
@Service("abstractBinaryFileService")
@Transactional
public class AbstractBinaryFileServiceImpl implements AbstractBinaryFileService {

    private static final Logger LOGGER = Logger.getLogger(AbstractBinaryFileServiceImpl.class);

    @Autowired
    private AbstractBinaryFileRepository abstractBinaryFileRepository;

    @Override
    public AbstractBinaryFile findById(final Long id) {
        return abstractBinaryFileRepository.findById(id);
    }

    @Override
    public List<AbstractBinaryFile> findAll() {
        return abstractBinaryFileRepository.findAll();
    }

    @Override
    public void save(final AbstractBinaryFile entity) {
        abstractBinaryFileRepository.save(entity);
    }

    @Override
    public void update(final AbstractBinaryFile entity) {
        abstractBinaryFileRepository.update(entity);
    }

    @Override
    public void saveOrUpdate(final AbstractBinaryFile entity) {
        abstractBinaryFileRepository.saveOrUpdate(entity);
    }

    @Override
    public void delete(final AbstractBinaryFile entity) {
        abstractBinaryFileRepository.delete(entity);
    }

    @Override
    public long countAll() {
        return abstractBinaryFileRepository.countAll();
    }

}
