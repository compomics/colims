package com.compomics.colims.core.service.impl.interceptable;

import com.compomics.colims.core.service.InstitutionService;
import com.compomics.colims.model.Institution;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.compomics.colims.repository.InstitutionRepository;

/**
 *
 * @author Niels Hulstaert
 */
@Service("institutionService")
@Transactional
public class InstitutionServiceImpl implements InstitutionService {

    @Autowired
    private InstitutionRepository institutionRepository;
    
    @Override
    public Institution findById(final Long id) {
        return institutionRepository.findById(id);
    }

    @Override
    public List<Institution> findAll() {
        return institutionRepository.findAll();
    }

    @Override
    public void save(final Institution entity) {
        institutionRepository.save(entity);
    }

    @Override
    public void delete(final Institution entity) {
        institutionRepository.delete(entity);
    }

    @Override
    public void update(final Institution entity) {
        institutionRepository.update(entity);
    }

    @Override
    public void saveOrUpdate(final Institution entity) {
        institutionRepository.saveOrUpdate(entity);
    }

    @Override
    public long countAll() {
        return institutionRepository.countAll();
    }
    
}
