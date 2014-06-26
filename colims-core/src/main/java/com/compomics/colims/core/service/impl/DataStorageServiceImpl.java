/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.io.MappedDataImport;
import com.compomics.colims.core.service.DataStorageService;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.Experiment;
import com.compomics.colims.model.Instrument;
import com.compomics.colims.model.Sample;
import com.compomics.colims.model.SearchAndValidationSettings;
import com.compomics.colims.repository.AnalyticalRunRepository;
import com.compomics.colims.repository.SearchAndValidationSettingsRepository;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Niels Hulstaert
 */
@Service("dataStorageService")
@Transactional
public class DataStorageServiceImpl implements DataStorageService {

    @Autowired
    private SearchAndValidationSettingsRepository searchAndValidationSettingsRepository;
    @Autowired
    private AnalyticalRunRepository analyticalRunRepository;

    @Override
    public void storeMappedData(MappedDataImport mappedDataImport, Sample sample, Instrument instrument, String userName, Date startDate) {
        //get experiment for sample
        Experiment experiment = sample.getExperiment();

        //first store the SearchAndValidationSettings
        SearchAndValidationSettings searchAndValidationSettings = mappedDataImport.getSearchAndValidationSettings();
        searchAndValidationSettings.setExperiment(experiment);
        searchAndValidationSettingsRepository.save(searchAndValidationSettings);

        for (AnalyticalRun analyticalRun : mappedDataImport.getAnalyticalRuns()) {
            analyticalRun.setCreationDate(new Date());
            analyticalRun.setModificationDate(new Date());
            analyticalRun.setUserName(userName);
            analyticalRun.setStartDate(startDate);
            analyticalRun.setSample(sample);
            analyticalRun.setInstrument(instrument);
            analyticalRunRepository.saveOrUpdate(analyticalRun);
            //throw new IllegalArgumentException("test");
        }
    }

}
