package com.compomics.colims.core.service.impl;

import com.compomics.colims.model.Instrument;
import com.compomics.colims.repository.InstrumentRepository;
import com.compomics.colims.core.service.InstrumentService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        instrumentRepository.delete(entity);
    }

    @Override
    public Instrument findByName(String name) {
        return instrumentRepository.findByName(name);
    }

    @Override
    public void update(Instrument entity) {
        instrumentRepository.update(entity);
    }

    @Override
    public void saveOrUpdate(Instrument entity) {
        instrumentRepository.saveOrUpdate(entity);
    }
}
