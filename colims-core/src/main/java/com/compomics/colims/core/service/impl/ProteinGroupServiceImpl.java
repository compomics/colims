package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.service.ProteinGroupService;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.ProteinGroup;
import com.compomics.colims.repository.ProteinGroupRepository;
import com.compomics.colims.repository.hibernate.SortDirection;
import com.compomics.colims.repository.hibernate.model.ProteinGroupDTO;
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
    public List<ProteinGroupDTO> getPagedProteinGroupsForRun(AnalyticalRun analyticalRun, int start, int length, String orderBy, SortDirection sortDirection, String filter) {
        return proteinGroupRepository.getPagedProteinGroupsForRun(analyticalRun, start, length, orderBy, sortDirection, filter);
    }

    @Override
    public long getProteinGroupCountForRun(AnalyticalRun analyticalRun, String filter) {
        return proteinGroupRepository.getProteinGroupCountForRun(analyticalRun, filter);
    }

    @Override
    public Object[] getProteinGroupsProjections(AnalyticalRun analyticalRun) {
        return new Object[0];
    }

    @Override
    public ProteinGroup findById(Long aLong) {
        return proteinGroupRepository.findById(aLong);
    }

    @Override
    public List<ProteinGroup> findAll() {
        return proteinGroupRepository.findAll();
    }

    @Override
    public void save(ProteinGroup entity) {
        proteinGroupRepository.save(entity);
    }

    @Override
    public void update(ProteinGroup entity) {
        proteinGroupRepository.update(entity);
    }

    @Override
    public void saveOrUpdate(ProteinGroup entity) {
        proteinGroupRepository.saveOrUpdate(entity);
    }

    @Override
    public void delete(ProteinGroup entity) {
        proteinGroupRepository.delete(entity);
    }

    @Override
    public long countAll() {
        return proteinGroupRepository.countAll();
    }

}
