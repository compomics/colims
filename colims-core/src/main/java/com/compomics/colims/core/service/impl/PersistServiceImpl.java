/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.service.PersistService;
import com.compomics.colims.model.*;
import com.compomics.colims.repository.AnalyticalRunRepository;
import com.compomics.colims.repository.ProteinGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementation of the DataStorageService interface.
 *
 * @author Niels Hulstaert
 */
@Service("persistService")
@Transactional
public class PersistServiceImpl implements PersistService {

    @Autowired
    private AnalyticalRunRepository analyticalRunRepository;
    @Autowired
    ProteinGroupRepository proteinGroupRepository;

    @Override
    public void persist(List<AnalyticalRun> analyticalRuns, Sample sample, Instrument instrument, String userName, Date startDate) {

        for (AnalyticalRun analyticalRun : analyticalRuns) {
            Date auditDate = new Date();

            SearchAndValidationSettings searchAndValidationSettings = analyticalRun.getSearchAndValidationSettings();
            if (searchAndValidationSettings != null) {
                //set the audit fields for the SearchAndValidationSettings
                searchAndValidationSettings.setCreationDate(auditDate);
                searchAndValidationSettings.setModificationDate(auditDate);
                searchAndValidationSettings.setUserName(userName);
            }

            QuantificationSettings quantificationSettings = analyticalRun.getQuantificationSettings();
            if (quantificationSettings != null) {
                //set the audit fields for the QuantificationSettings
                quantificationSettings.setCreationDate(auditDate);
                quantificationSettings.setModificationDate(auditDate);
                quantificationSettings.setUserName(userName);
            }

            Set<ProteinGroup> proteinGroups = new HashSet<>();
            //collect all unique ProteinGroup instances
            //@todo to this smarter, whitout having to iterate over everything
            for (Spectrum spectrum : analyticalRun.getSpectrums()) {
                for (Peptide peptide : spectrum.getPeptides()) {
                    for (PeptideHasProteinGroup peptideHasProteinGroup : peptide.getPeptideHasProteinGroups()) {
                        proteinGroups.add(peptideHasProteinGroup.getProteinGroup());
                    }
                }
            }
            //and save them
            for (ProteinGroup proteinGroup : proteinGroups) {
                proteinGroupRepository.save(proteinGroup);
            }

            analyticalRun.setCreationDate(auditDate);
            analyticalRun.setModificationDate(auditDate);
            analyticalRun.setUserName(userName);
            analyticalRun.setStartDate(startDate);
            analyticalRun.setSample(sample);
            analyticalRun.setInstrument(instrument);
            analyticalRunRepository.saveOrUpdate(analyticalRun);
        }
    }

}
