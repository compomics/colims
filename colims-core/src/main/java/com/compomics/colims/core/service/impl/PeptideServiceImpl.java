package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.service.PeptideService;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.repository.PeptideRepository;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Niels Hulstaert
 */
@Service("peptideService")
@Transactional
public class PeptideServiceImpl implements PeptideService {

    @Autowired
    PeptideRepository peptideRepository;

    @Override
    public Peptide findById(Long id) {
        return peptideRepository.findById(id);
    }

    @Override
    public List<Peptide> findAll() {
        return peptideRepository.findAll();
    }

    @Override
    public void save(Peptide entity) {
        peptideRepository.save(entity);
    }

    @Override
    public void update(Peptide entity) {
        peptideRepository.update(entity);
    }

    @Override
    public void saveOrUpdate(Peptide entity) {
        peptideRepository.saveOrUpdate(entity);
    }

    @Override
    public void delete(Peptide entity) {
        peptideRepository.delete(entity);
    }

    @Override
    public long countAll() {
        return peptideRepository.countAll();
    }

    @Override
    public Peptide findBySpectrumId(long spectrumId) {
        return peptideRepository.findPeptideBySpectrumId(spectrumId);
    }

}
