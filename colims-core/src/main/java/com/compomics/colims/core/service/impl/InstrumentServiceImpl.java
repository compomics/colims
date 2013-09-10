package com.compomics.colims.core.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.compomics.colims.core.service.InstrumentService;
import com.compomics.colims.model.Instrument;
import com.compomics.colims.repository.InstrumentRepository;
import org.hibernate.Hibernate;
import org.hibernate.LockOptions;

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
    public Instrument findById(Long id) {
        return instrumentRepository.findById(id);
    }
    
    @Override
    public List<Instrument> findAll() {
        return instrumentRepository.findAll();
    }
    
    @Override
    public void save(Instrument entity) {
        instrumentRepository.save(entity);
    }
    
    @Override
    public void delete(Instrument entity) {
        //attach the instrument to the session
        instrumentRepository.lock(entity, LockOptions.NONE);
        instrumentRepository.delete(entity);
    }
    
    @Override
    public Instrument findByName(String name) {
        return instrumentRepository.findByName(name);
    }
    
    @Override
    public void update(Instrument entity) {
        //attach the instrument to the session
        instrumentRepository.saveOrUpdate(entity);
        instrumentRepository.update(entity);
    }
    
    @Override
    public void saveOrUpdate(Instrument entity) {
        instrumentRepository.saveOrUpdate(entity);
    }
    
    @Override
    public boolean checkUsageBeforeDeletion(Instrument instrument) {
        boolean deleted = false;

        //attach the instrument to the session
        instrumentRepository.lock(instrument, LockOptions.NONE);
        //fetch the analytical runs
        if (!Hibernate.isInitialized(instrument.getAnalyticalRuns())) {
            Hibernate.initialize(instrument.getAnalyticalRuns());
            if (instrument.getAnalyticalRuns().isEmpty()) {
                //delete the instrument
                delete(instrument);
                deleted = true;
            }
        }
        
        return deleted;
    }
}
