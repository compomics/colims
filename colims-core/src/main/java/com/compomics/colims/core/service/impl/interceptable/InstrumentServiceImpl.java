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
        Instrument instrument = instrumentRepository.findById(id);

        //fetch analyzers
        instrument.getAnalyzers().size();

        return instrument;
    }

    @Override
    public List<Instrument> findAll() {
        return instrumentRepository.findAllOrderedByName();
    }

    @Override
    public Long countByName(final String name) {
        return instrumentRepository.countByName(name);
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
        Instrument merge = instrumentRepository.merge(entity);

        //fetch analyzers
        merge.getAnalyzers().size();

        return merge;
    }

    @Override
    public void remove(Instrument entity) {
        //get a reference to the entity
        Instrument reference = instrumentRepository.getReference(entity.getId());
        instrumentRepository.remove(reference);
    }
}
