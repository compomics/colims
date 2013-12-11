package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.service.InstrumentTypeService;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.compomics.colims.model.InstrumentType;
import com.compomics.colims.repository.InstrumentTypeRepository;
import org.hibernate.LockOptions;

/**
 *
 * @author Niels Hulstaert
 */
@Service("instrumentTypeService")
@Transactional
public class InstrumentTypeServiceImpl implements InstrumentTypeService {

    @Autowired
    private InstrumentTypeRepository instrumentTypeRepository;
    
    @Override
    public InstrumentType findById(Long id) {
        return instrumentTypeRepository.findById(id);
    }

    @Override
    public List<InstrumentType> findAll() {
        return instrumentTypeRepository.findAll();
    }

    @Override
    public void save(InstrumentType entity) {
        instrumentTypeRepository.save(entity);
    }

    @Override
    public void delete(InstrumentType entity) {
        instrumentTypeRepository.delete(entity);
    }

    @Override
    public InstrumentType findByName(String name) {
        return instrumentTypeRepository.findByName(name);
    }

    @Override
    public void update(InstrumentType entity) {
        instrumentTypeRepository.update(entity);
    }

    @Override
    public void saveOrUpdate(InstrumentType entity) {
        instrumentTypeRepository.saveOrUpdate(entity);
    }

    @Override
    public long countAll() {
        return instrumentTypeRepository.countAll();
    }
    
}
