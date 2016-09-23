package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.service.ModificationService;
import com.compomics.colims.model.Modification;
import com.compomics.colims.repository.ModificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Niels Hulstaert
 */
@Service("modificationService")
@Transactional
public class ModificationServiceImpl implements ModificationService {

    private final ModificationRepository modificationRepository;

    @Autowired
    public ModificationServiceImpl(ModificationRepository modificationRepository) {
        this.modificationRepository = modificationRepository;
    }

    @Override
    public Modification findById(final Long id) {
        return modificationRepository.findById(id);
    }

    @Override
    public List<Modification> findAll() {
        return modificationRepository.findAll();
    }

    @Override
    public Modification findByName(final String name) {
        return modificationRepository.findByName(name);
    }

    @Override
    public Modification findByAccession(final String accession) {
        return modificationRepository.findByAccession(accession);
    }

    @Override
    public long countAll() {
        return modificationRepository.countAll();
    }

    @Override
    public void persist(Modification entity) {
        modificationRepository.persist(entity);
    }

    @Override
    public Modification merge(Modification entity) {
        return modificationRepository.merge(entity);
    }

    @Override
    public void remove(Modification entity) {
        modificationRepository.remove(entity);
    }
}
