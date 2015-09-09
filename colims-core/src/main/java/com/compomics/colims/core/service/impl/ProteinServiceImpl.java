package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.service.ProteinService;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.Protein;
import com.compomics.colims.repository.ProteinRepository;
import org.hibernate.Hibernate;
import org.hibernate.LazyInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Niels Hulstaert
 */
@Service("proteinService")
@Transactional
public class ProteinServiceImpl implements ProteinService {

    @Autowired
    private ProteinRepository proteinRepository;

    @Override
    public Protein findBySequence(final String sequence) {
        return proteinRepository.findBySequence(sequence);
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
    public void fetchAccessions(Protein protein) {
        try {
            protein.getProteinAccessions().size();
        } catch (LazyInitializationException e) {
            //attach the protein to the new session
            proteinRepository.saveOrUpdate(protein);
            if (!Hibernate.isInitialized(protein.getProteinAccessions())) {
                Hibernate.initialize(protein.getProteinAccessions());
            }
        }
    }
}