package com.compomics.colims.core.service.impl.interceptable;

import com.compomics.colims.core.service.FastaDbService;
import com.compomics.colims.model.FastaDb;
import com.compomics.colims.repository.FastaDbRepository;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Niels Hulstaert
 */
@Service("fastaDbService")
@Transactional
public class FastaDbServiceImpl implements FastaDbService {

    @Autowired
    private FastaDbRepository fastaDbRepository;

    @Override
    public FastaDb findById(final Long id) {
        return fastaDbRepository.findById(id);
    }

    @Override
    public List<FastaDb> findAll() {
        return fastaDbRepository.findAll();
    }

    @Override
    public void save(final FastaDb entity) {
        fastaDbRepository.save(entity);
    }

    @Override
    public void update(final FastaDb entity) {
        fastaDbRepository.update(entity);
    }

    @Override
    public void saveOrUpdate(final FastaDb entity) {
        fastaDbRepository.saveOrUpdate(entity);
    }

    @Override
    public void delete(final FastaDb entity) {
        fastaDbRepository.delete(entity);
    }

    @Override
    public long countAll() {
        return fastaDbRepository.countAll();
    }

}
