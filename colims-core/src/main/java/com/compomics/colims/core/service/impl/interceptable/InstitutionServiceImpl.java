package com.compomics.colims.core.service.impl.interceptable;

import com.compomics.colims.core.service.InstitutionService;
import com.compomics.colims.model.Institution;
import com.compomics.colims.repository.InstitutionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Niels Hulstaert
 */
@Service("institutionService")
@Transactional
public class InstitutionServiceImpl implements InstitutionService {

    private final InstitutionRepository institutionRepository;

    @Autowired
    public InstitutionServiceImpl(InstitutionRepository institutionRepository) {
        this.institutionRepository = institutionRepository;
    }

    @Override
    public Institution findById(final Long id) {
        return institutionRepository.findById(id);
    }

    @Override
    public List<Institution> findAll() {
        return institutionRepository.findAll();
    }

    @Override
    public long countAll() {
        return institutionRepository.countAll();
    }

    @Override
    public void persist(Institution entity) {
        institutionRepository.persist(entity);
    }

    @Override
    public Institution merge(Institution entity) {
        return institutionRepository.merge(entity);
    }

    @Override
    public void remove(Institution entity) {
        institutionRepository.remove(entity);
    }
}
