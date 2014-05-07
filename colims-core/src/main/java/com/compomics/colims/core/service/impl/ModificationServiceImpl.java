package com.compomics.colims.core.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.compomics.colims.core.service.ModificationService;
import com.compomics.colims.model.Modification;
import com.compomics.colims.repository.ModificationRepository;

/**
 *
 * @author Niels Hulstaert
 */
@Service("modificationService")
@Transactional
public class ModificationServiceImpl implements ModificationService {

    @Autowired
    private ModificationRepository modificationRepository;

    @Override
    public Modification findById(final Long id) {
        return modificationRepository.findById(id);
    }

    @Override
    public List<Modification> findAll() {
        return modificationRepository.findAll();
    }

    @Override
    public void save(final Modification entity) {
        modificationRepository.save(entity);
    }

    @Override
    public void update(final Modification entity) {
        modificationRepository.update(entity);
    }

    @Override
    public void saveOrUpdate(final Modification entity) {
        modificationRepository.saveOrUpdate(entity);
    }

    @Override
    public void delete(final Modification entity) {
        modificationRepository.delete(entity);
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
    public Modification findByAlternativeAccession(String alternativeAccession) {
        return modificationRepository.findByAlternativeAccession(alternativeAccession);
    }

}
