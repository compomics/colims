/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.service.DataStorageService;
import com.compomics.colims.model.*;
import com.compomics.colims.repository.AnalyticalRunRepository;
import com.compomics.colims.repository.QuantificationSettingsRepository;
import com.compomics.colims.repository.SearchAndValidationSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Implementation of the DataStorageService interface.
 *
 * @author Niels Hulstaert
 */
@Service("dataStorageService")
@Transactional
public class DataStorageServiceImpl implements DataStorageService {

    @Autowired
    private AnalyticalRunRepository analyticalRunRepository;

    @Override
    public void store(List<AnalyticalRun> analyticalRuns, Sample sample, Instrument instrument, String userName, Date startDate) {

        for (AnalyticalRun analyticalRun : analyticalRuns) {
            Date auditDate = new Date();

            SearchAndValidationSettings searchAndValidationSettings = analyticalRun.getSearchAndValidationSettings();
            if(searchAndValidationSettings != null) {
                //set the audit fields for the SearchAndValidationSettings
                searchAndValidationSettings.setCreationDate(auditDate);
                searchAndValidationSettings.setModificationDate(auditDate);
                searchAndValidationSettings.setUserName(userName);
            }

            QuantificationSettings quantificationSettings = analyticalRun.getQuantificationSettings();
            if(quantificationSettings != null) {
                //set the audit fields for the QuantificationSettings
                quantificationSettings.setCreationDate(auditDate);
                quantificationSettings.setModificationDate(auditDate);
                quantificationSettings.setUserName(userName);
            }

            analyticalRun.setCreationDate(auditDate);
            analyticalRun.setModificationDate(auditDate);
            analyticalRun.setUserName(userName);
            analyticalRun.setStartDate(startDate);
            analyticalRun.setSample(sample);
            analyticalRun.setInstrument(instrument);
            analyticalRunRepository.saveOrUpdate(analyticalRun);
            //throw new IllegalArgumentException("test");
        }
    }

}
