package com.compomics.colims.core.service.impl.interceptable;

import com.compomics.colims.core.service.FastaDbService;
import com.compomics.colims.model.FastaDb;
import com.compomics.colims.model.SearchAndValidationSettings;
import com.compomics.colims.model.enums.FastaDbType;
import com.compomics.colims.repository.FastaDbRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author Niels Hulstaert
 */
@Service("fastaDbService")
@Transactional
public class FastaDbServiceImpl implements FastaDbService {

    private final FastaDbRepository fastaDbRepository;

    @Autowired
    public FastaDbServiceImpl(FastaDbRepository fastaDbRepository) {
        this.fastaDbRepository = fastaDbRepository;
    }

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
        //get a reference to the entity
        FastaDb reference = fastaDbRepository.getReference(entity.getId());
        fastaDbRepository.remove(reference);
    }

    @Override
    public List<FastaDb> findByFastaDbType(List<FastaDbType> fastaDbTypes) {
        return fastaDbRepository.findByFastaDbType(fastaDbTypes);
    }

    @Override
    public List<String> getAllParseRules() {
        return fastaDbRepository.getAllParseRules();
    }

    @Override
    public Map<FastaDb, FastaDbType> findBySearchAndValidationSettings(SearchAndValidationSettings searchAndValidationSettings) {
        return fastaDbRepository.findBySearchAndValidationSettings(searchAndValidationSettings);
    }

}
