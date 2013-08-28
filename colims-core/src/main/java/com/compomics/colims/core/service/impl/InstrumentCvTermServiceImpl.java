package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.service.InstrumentCvTermService;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.compomics.colims.core.service.InstrumentService;
import com.compomics.colims.model.Instrument;
import com.compomics.colims.model.InstrumentCvTerm;
import com.compomics.colims.model.enums.InstrumentCvProperty;
import com.compomics.colims.repository.InstrumentCvTermRepository;
import com.compomics.colims.repository.InstrumentRepository;

/**
 *
 * @author Niels Hulstaert
 */
@Service("instrumentCvTermService")
@Transactional
public class InstrumentCvTermServiceImpl implements InstrumentCvTermService {
    
    @Autowired
    private InstrumentCvTermRepository instrumentCvTermRepository;

    @Override
    public InstrumentCvTerm findById(Long id) {
        return instrumentCvTermRepository.findById(id);
    }

    @Override
    public List<InstrumentCvTerm> findAll() {
        return instrumentCvTermRepository.findAll();
    }

    @Override
    public void save(InstrumentCvTerm entity) {
        instrumentCvTermRepository.save(entity);
    }

    @Override
    public void update(InstrumentCvTerm entity) {
        instrumentCvTermRepository.update(entity);
    }

    @Override
    public void saveOrUpdate(InstrumentCvTerm entity) {
        instrumentCvTermRepository.saveOrUpdate(entity);
    }

    @Override
    public void delete(InstrumentCvTerm entity) {
        instrumentCvTermRepository.delete(entity);
    }        

    @Override
    public InstrumentCvTerm findByAccession(String accession, InstrumentCvProperty instrumentCvProperty) {
        return instrumentCvTermRepository.findByAccession(accession, instrumentCvProperty);
    }

    @Override
    public List<InstrumentCvTerm> findByInstrumentCvProperty(InstrumentCvProperty instrumentCvProperty) {
        return instrumentCvTermRepository.findByInstrumentCvProperty(instrumentCvProperty);
    }
}
