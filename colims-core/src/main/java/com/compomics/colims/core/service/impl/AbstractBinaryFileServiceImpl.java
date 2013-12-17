package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.service.AbstractBinaryFileService;
import com.compomics.colims.core.service.AnalyticalRunService;
import com.compomics.colims.model.AbstractBinaryFile;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.repository.AbstractBinaryFileRepository;
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
@Service("abstractBinaryFileService")
@Transactional
public class AbstractBinaryFileServiceImpl implements AbstractBinaryFileService {

    private static final Logger LOGGER = Logger.getLogger(AbstractBinaryFileServiceImpl.class);

    @Autowired
    private AbstractBinaryFileRepository abstractBinaryFileRepository;

    @Override
    public AbstractBinaryFile findById(Long id) {
        return abstractBinaryFileRepository.findById(id);
    }

    @Override
    public List<AbstractBinaryFile> findAll() {
        return abstractBinaryFileRepository.findAll();
    }

    @Override
    public void save(AbstractBinaryFile entity) {
        abstractBinaryFileRepository.save(entity);
    }

    @Override
    public void update(AbstractBinaryFile entity) {
        abstractBinaryFileRepository.update(entity);
    }

    @Override
    public void saveOrUpdate(AbstractBinaryFile entity) {
        abstractBinaryFileRepository.saveOrUpdate(entity);
    }

    @Override
    public void delete(AbstractBinaryFile entity) {
        abstractBinaryFileRepository.delete(entity);
    }

    @Override
    public long countAll() {
        return abstractBinaryFileRepository.countAll();
    }

}
