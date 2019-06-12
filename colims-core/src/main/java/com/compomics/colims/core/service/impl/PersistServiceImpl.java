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
import com.compomics.colims.repository.ProteinGroupRepository;
import com.compomics.colims.repository.SearchParametersRepository;
import com.compomics.colims.repository.SpectrumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Implementation of the PersistService interface.
 *
 * @author Niels Hulstaert
 */
@Service("persistService")
@Transactional
public class PersistServiceImpl implements PersistService {

    private final AnalyticalRunRepository analyticalRunRepository;
    private final ProteinGroupRepository proteinGroupRepository;
    private final SpectrumRepository spectrumRepository;
    private final SearchParametersRepository searchParametersRepository;

    @Autowired
    public PersistServiceImpl(AnalyticalRunRepository analyticalRunRepository, ProteinGroupRepository proteinGroupRepository, SpectrumRepository spectrumRepository, SearchParametersRepository searchParametersRepository) {
        this.analyticalRunRepository = analyticalRunRepository;
        this.proteinGroupRepository = proteinGroupRepository;
        this.spectrumRepository = spectrumRepository;
        this.searchParametersRepository = searchParametersRepository;
    }

    @Override
    public void persist(MappedData mappedData, Sample sample, Instrument instrument, String userName, Date startDate) {
        // first, cascade save or update the analytical runs
        mappedData.getAnalyticalRuns().stream().filter(analyticalRun -> analyticalRun.getId() == null)
                .forEach(analyticalRun -> {
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
                    //second,  cascade save or update the analytical run
                    analyticalRun.setUserName(userName);
                    analyticalRun.setStartDate(startDate);
                    analyticalRun.setSample(sample);
                    analyticalRun.setInstrument(instrument);
                    analyticalRunRepository.saveOrUpdate(analyticalRun);
                });

        //cascade save or update the protein groups
        mappedData.getProteinGroups().stream().forEach(proteinGroup -> {
            if (proteinGroup.getId() == null) {
                proteinGroupRepository.saveOrUpdate(proteinGroup);
            }
        });

        //cascade save or update the spectra
        mappedData.getAnalyticalRuns().stream().forEach(analyticalRun -> analyticalRun.getSpectrums().stream().forEach(spectrumRepository::saveOrUpdate));
    }
}
