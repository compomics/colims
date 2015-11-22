package com.compomics.colims.core.service.impl.interceptable;

import com.compomics.colims.core.service.FastaDbService;
import com.compomics.colims.model.FastaDb;
import com.compomics.colims.repository.FastaDbRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
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
    public long countAll() {
        return fastaDbRepository.countAll();
    }

    @Override
    public void persist(FastaDb entity) {
        fastaDbRepository.persist(entity);
    }

    @Override
    public FastaDb merge(FastaDb entity) {
        return fastaDbRepository.merge(entity);
    }

    @Override
    public void remove(FastaDb entity) {
        fastaDbRepository.remove(entity);
    }

}
