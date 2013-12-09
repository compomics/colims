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
    public Modification findById(Long id) {
        return modificationRepository.findById(id);
    }

    @Override
    public List<Modification> findAll() {
        return modificationRepository.findAll();
    }

    @Override
    public void save(Modification entity) {
        modificationRepository.save(entity);
    }

    @Override
    public void update(Modification entity) {
        modificationRepository.update(entity);
    }

    @Override
    public void saveOrUpdate(Modification entity) {
        modificationRepository.saveOrUpdate(entity);
    }

    @Override
    public void delete(Modification entity) {
        modificationRepository.delete(entity);
    }

    @Override
    public Modification findByName(String name) {
        return modificationRepository.findByName(name);
    }

    @Override
    public Modification findByAccession(String accession) {
        return modificationRepository.findByAccession(accession);
    }
}
