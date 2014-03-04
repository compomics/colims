package com.compomics.colims.core.service.impl.interceptable;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.compomics.colims.core.service.MaterialService;
import com.compomics.colims.model.Material;
import com.compomics.colims.repository.MaterialRepository;

/**
 *
 * @author Niels Hulstaert
 */
@Service("materialService")
@Transactional
public class MaterialServiceImpl implements MaterialService {
    
    @Autowired
    private MaterialRepository materialRepository;
    
    @Override
    public Material findById(final Long id) {
        return materialRepository.findById(id);
    }
    
    @Override
    public List<Material> findAll() {
        return materialRepository.findAllOrderedByName();
    }
    
    @Override
    public void save(final Material entity) {
        materialRepository.save(entity);
    }
    
    @Override
    public void delete(final Material entity) {
        materialRepository.delete(entity);
    }
    
    @Override
    public Material findByName(final String name) {
        return materialRepository.findByName(name);
    }
    
    @Override
    public void update(final Material entity) {
        materialRepository.update(entity);
    }
    
    @Override
    public void saveOrUpdate(final Material entity) {
        materialRepository.saveOrUpdate(entity);
    }       

    @Override
    public long countAll() {
        return materialRepository.countAll();
    }
    
}
