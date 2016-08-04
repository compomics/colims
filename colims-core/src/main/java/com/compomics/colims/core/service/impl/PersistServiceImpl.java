/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.io.MappedData;
import com.compomics.colims.core.service.PersistService;
import com.compomics.colims.model.*;
import com.compomics.colims.repository.AnalyticalRunRepository;
import com.compomics.colims.repository.PeptideRepository;
import com.compomics.colims.repository.ProteinGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

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
    @Autowired
    PeptideRepository peptideRepository;

    @Override
    public void persist(MappedData mappedData, Sample sample, Instrument instrument, String userName, Date startDate) {
        mappedData.getAnalyticalRuns().stream().forEach(analyticalRun -> {

            SearchAndValidationSettings searchAndValidationSettings = analyticalRun.getSearchAndValidationSettings();
            if (searchAndValidationSettings != null) {
                //set the audit user name for the SearchAndValidationSettings
                searchAndValidationSettings.setUserName(userName);
            }

            QuantificationSettings quantificationSettings = analyticalRun.getQuantificationSettings();
            if (quantificationSettings != null) {
                //set the audit user name for the QuantificationSettings
                quantificationSettings.setUserName(userName);
            }

            //first, cascade save or update the protein groups
            mappedData.getProteinGroups().stream().forEach(proteinGroupRepository::saveOrUpdate);

            //second,  cascade save or update the analytical run
            analyticalRun.setUserName(userName);
            analyticalRun.setStartDate(startDate);
            analyticalRun.setSample(sample);
            analyticalRun.setInstrument(instrument);
            analyticalRunRepository.saveOrUpdate(analyticalRun);
        });
    }
}
