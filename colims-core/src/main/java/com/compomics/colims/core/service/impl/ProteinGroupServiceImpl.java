package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.service.ProteinGroupService;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.ProteinGroup;
import com.compomics.colims.repository.ProteinGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Iain on 08/09/2015.
 */
@Service("proteinGroupService")
@Transactional
public class ProteinGroupServiceImpl implements ProteinGroupService {
    @Autowired
    ProteinGroupRepository proteinGroupRepository;

    @Override
    public List<ProteinGroup> getPagedProteinGroupsForRun(AnalyticalRun analyticalRun, int start, int length, String orderBy, String direction, String filter) {
        return proteinGroupRepository.getPagedProteinGroupsForRun(analyticalRun, start, length, orderBy, direction, filter);
    }

    @Override
    public int getProteinGroupCountForRun(AnalyticalRun analyticalRun, String filter) {
        return proteinGroupRepository.getProteinGroupCountForRun(analyticalRun, filter);
    }

    @Override
    public ProteinGroup findById(Long aLong) {
        return null;
    }

    @Override
    public List<ProteinGroup> findAll() {
        return null;
    }

    @Override
    public void save(ProteinGroup entity) {

    }

    @Override
    public void update(ProteinGroup entity) {

    }

    @Override
    public void saveOrUpdate(ProteinGroup entity) {

    }

    @Override
    public void delete(ProteinGroup entity) {

    }

    @Override
    public long countAll() {
        return 0;
    }
}
