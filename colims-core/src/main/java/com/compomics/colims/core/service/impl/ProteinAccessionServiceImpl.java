package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.service.ProteinAccessionService;
import com.compomics.colims.model.ProteinAccession;
import com.compomics.colims.model.ProteinGroup;
import com.compomics.colims.repository.ProteinAccessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Iain on 14/09/2015.
 */
@Service("proteinAccessionService")
@Transactional
public class ProteinAccessionServiceImpl implements ProteinAccessionService {
    @Autowired
    ProteinAccessionRepository proteinAccessionRepository;

    @Override
    public List<ProteinAccession> getAccessionsForProteinGroup(ProteinGroup proteinGroup) {
        return proteinAccessionRepository.getAccessionsForProteinGroup(proteinGroup);
    }

    @Override
    public ProteinAccession findById(Long aLong) {
        return null;
    }

    @Override
    public List<ProteinAccession> findAll() {
        return null;
    }

    @Override
    public void save(ProteinAccession entity) {

    }

    @Override
    public void update(ProteinAccession entity) {

    }

    @Override
    public void saveOrUpdate(ProteinAccession entity) {

    }

    @Override
    public void delete(ProteinAccession entity) {

    }

    @Override
    public long countAll() {
        return 0;
    }
}