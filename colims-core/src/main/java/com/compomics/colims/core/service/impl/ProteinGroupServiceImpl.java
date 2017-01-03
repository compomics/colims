package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.service.ProteinGroupService;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.Protein;
import com.compomics.colims.model.ProteinGroup;
import com.compomics.colims.model.ProteinGroupHasProtein;
import com.compomics.colims.repository.ProteinGroupRepository;
import com.compomics.colims.repository.hibernate.SortDirection;
import com.compomics.colims.repository.hibernate.ProteinGroupDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by Iain on 08/09/2015.
 */
@Service("proteinGroupService")
@Transactional
public class ProteinGroupServiceImpl implements ProteinGroupService {

    final
    ProteinGroupRepository proteinGroupRepository;

    @Autowired
    public ProteinGroupServiceImpl(ProteinGroupRepository proteinGroupRepository) {
        this.proteinGroupRepository = proteinGroupRepository;
    }

    @Override
    public List<ProteinGroupDTO> getPagedProteinGroupsForRuns(List<Long> analyticalRunIds, int start, int length, String orderBy, SortDirection sortDirection, String filter) {
        return proteinGroupRepository.getPagedProteinGroupsForRun(analyticalRunIds, start, length, orderBy, sortDirection, filter);
    }

    @Override
    public long getProteinGroupCountForRuns(List<Long> analyticalRunIds, String filter) {
        return proteinGroupRepository.getProteinGroupCountForRun(analyticalRunIds, filter);
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
    public long countAll() {
        return proteinGroupRepository.countAll();
    }

    @Override
    public void persist(ProteinGroup entity) {
        proteinGroupRepository.persist(entity);
    }

    @Override
    public ProteinGroup merge(ProteinGroup entity) {
        return proteinGroupRepository.merge(entity);
    }

    @Override
    public void remove(ProteinGroup entity) {
        proteinGroupRepository.remove(entity);
    }

    @Override
    public List<ProteinGroupDTO> getProteinGroupsForRuns(List<Long> analyticalRunIds) {
         return proteinGroupRepository.getProteinGroupsForRun(analyticalRunIds);
    }

    @Override
    public List<ProteinGroupHasProtein> getAmbiguityMembers(Long proteinGroupId) {
        return proteinGroupRepository.getAmbiguityMembers(proteinGroupId);
    }

    @Override
    public Map<ProteinGroupHasProtein, Protein> getProteinGroupHasProteinByProteinGroupId(Long proteinGroupId) {
        return proteinGroupRepository.getProteinGroupHasProteinbyProteinGroupId(proteinGroupId);
       
    }

}
