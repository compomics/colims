package com.compomics.colims.core.service.impl.interceptable;

import com.compomics.colims.core.service.InstrumentService;
import com.compomics.colims.model.Instrument;
import com.compomics.colims.repository.InstrumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
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
    public Instrument findByName(final String name) {
        return instrumentRepository.findByName(name);
    }

    @Override
    public long countAll() {
        return instrumentRepository.countAll();
    }

    @Override
    public void persist(Instrument entity) {
        instrumentRepository.persist(entity);
    }

    @Override
    public Instrument merge(Instrument entity) {
        return instrumentRepository.merge(entity);
    }

    @Override
    public void remove(Instrument entity) {
        instrumentRepository.remove(entity);
    }
}
