package com.compomics.colims.core.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.compomics.colims.core.service.ProteinService;
import com.compomics.colims.model.Protein;
import com.compomics.colims.repository.ProteinRepository;

/**
 *
 * @author Niels Hulstaert
 */
@Service("proteinService")
@Transactional
public class ProteinServiceImpl implements ProteinService {

    @Autowired
    private ProteinRepository proteinRepository;

    @Override
    public Protein findByAccession(final String accession) {
        return proteinRepository.findByAccession(accession);
    }

    @Override
    public Protein findById(final Long id) {
        return proteinRepository.findById(id);
    }

    @Override
    public List<Protein> findAll() {
        return proteinRepository.findAll();
    }

    @Override
    public void save(final Protein entity) {
        proteinRepository.save(entity);
    }

    @Override
    public void update(final Protein entity) {
        proteinRepository.update(entity);
    }

    @Override
    public void saveOrUpdate(final Protein entity) {
        proteinRepository.saveOrUpdate(entity);
    }

    @Override
    public void delete(final Protein entity) {
        proteinRepository.delete(entity);
    }

    @Override
    public long countAll() {
        return proteinRepository.countAll();
    }

    @Override
    public Protein findByAccessionSequenceDigest(String accessionSequenceDigest) {
        return proteinRepository.findByAccessionSequenceDigest(accessionSequenceDigest);
    }
}