package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.service.ProteinService;
import com.compomics.colims.model.Protein;
import com.compomics.colims.repository.ProteinRepository;
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
    public long countAll() {
        return proteinRepository.countAll();
    }

    @Override
    public void persist(Protein entity) {
        proteinRepository.persist(entity);
    }

    @Override
    public Protein merge(Protein entity) {
        return proteinRepository.merge(entity);
    }

    @Override
    public void remove(Protein entity) {
        proteinRepository.remove(entity);
    }

    @Override
    public Protein fetchAccessions(Protein protein) {
        try {
            protein.getProteinAccessions().size();
            return protein;
        } catch (LazyInitializationException e) {
            //merge the protein
            Protein merge = proteinRepository.merge(protein);
            merge.getProteinAccessions().size();
            return merge;
        }
    }
}