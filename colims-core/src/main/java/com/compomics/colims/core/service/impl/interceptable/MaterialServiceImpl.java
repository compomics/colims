package com.compomics.colims.core.service.impl.interceptable;

import com.compomics.colims.core.service.MaterialService;
import com.compomics.colims.model.Material;
import com.compomics.colims.repository.MaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Niels Hulstaert
 */
@Service("materialService")
@Transactional
public class MaterialServiceImpl implements MaterialService {

    private final MaterialRepository materialRepository;

    @Autowired
    public MaterialServiceImpl(MaterialRepository materialRepository) {
        this.materialRepository = materialRepository;
    }

    @Override
    public Material findById(final Long id) {
        return materialRepository.findById(id);
    }

    @Override
    public List<Material> findAll() {
        return materialRepository.findAllOrderedByName();
    }

    @Override
    public Long countByName(final Material material) {
        return materialRepository.countByName(material);
    }

    @Override
    public long countAll() {
        return materialRepository.countAll();
    }

    @Override
    public void persist(Material entity) {
        materialRepository.persist(entity);
    }

    @Override
    public Material merge(Material entity) {
        return materialRepository.merge(entity);
    }

    @Override
    public void remove(Material entity) {
        //get a reference to the entity
        Material reference = materialRepository.getReference(entity.getId());
        materialRepository.remove(reference);
    }

}
