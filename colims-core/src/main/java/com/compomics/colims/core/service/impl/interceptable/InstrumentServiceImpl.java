package com.compomics.colims.core.service.impl.interceptable;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.compomics.colims.core.service.InstrumentService;
import com.compomics.colims.model.Instrument;
import com.compomics.colims.repository.InstrumentRepository;

/**
 *
 * @author Niels Hulstaert
 */
@Service("instrumentService")
@Transactional
public class InstrumentServiceImpl implements InstrumentService {
    
    @Autowired
    private InstrumentRepository instrumentRepository;
    
    @Override
    public Instrument findById(final Long id) {
        return instrumentRepository.findById(id);
    }
    
    @Override
    public List<Instrument> findAll() {
        return instrumentRepository.findAllOrderedByName();
    }
    
    @Override
    public void save(final Instrument entity) {
        instrumentRepository.save(entity);
    }
    
    @Override
    public void delete(final Instrument entity) {
        instrumentRepository.delete(entity);
    }
    
    @Override
    public Instrument findByName(final String name) {
        return instrumentRepository.findByName(name);
    }
    
    @Override
    public void update(final Instrument entity) {
        instrumentRepository.update(entity);
    }
    
    @Override
    public void saveOrUpdate(final Instrument entity) {
        instrumentRepository.saveOrUpdate(entity);
    }   

    @Override
    public long countAll() {
        return instrumentRepository.countAll();
    }
    
}
