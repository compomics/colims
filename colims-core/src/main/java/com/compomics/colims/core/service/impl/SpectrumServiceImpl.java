package com.compomics.colims.core.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.compomics.colims.core.io.IOManager;
import com.compomics.colims.core.io.model.MascotGenericFile;
import com.compomics.colims.core.service.SpectrumService;
import com.compomics.colims.model.Spectrum;
import com.compomics.colims.model.SpectrumFile;
import com.compomics.colims.repository.SpectrumRepository;

/**
 *
 * @author Niels Hulstaert
 */
@Service("spectrumService")
@Transactional
public class SpectrumServiceImpl implements SpectrumService {

    private static final Logger LOGGER = Logger.getLogger(SpectrumServiceImpl.class);
    @Autowired
    private SpectrumRepository spectrumRepository;
    @Autowired
    private IOManager iOManager;

    @Override
    public List<Spectrum> findSpectraByAnalyticalRunId(Long analyticalRunId) {
        List<Spectrum> spectra = spectrumRepository.findSpectraByAnalyticalRunId(analyticalRunId);
        //initialize peptides        
        for (Spectrum spectrum : spectra) {
            //Hibernate.initialize(spectrum.getPeptides());
            spectrum.getPeptides().size();
        }
        return spectra;
    }

    @Override
    public Spectrum findById(Long id) {
        return spectrumRepository.findById(id);
    }

    @Override
    public List<Spectrum> findAll() {
        return spectrumRepository.findAll();
    }

    @Override
    public void save(Spectrum entity) {
        spectrumRepository.save(entity);
    }

    @Override
    public void update(Spectrum entity) {
        spectrumRepository.update(entity);
    }

    @Override
    public void saveOrUpdate(Spectrum entity) {
        spectrumRepository.saveOrUpdate(entity);
    }

    @Override
    public void delete(Spectrum entity) {
        spectrumRepository.delete(entity);
    }

    @Override
    public Map<Double, Double> getSpectrumPeaks(Long spectrumId) {
        Map<Double, Double> spectrumPeaks = null;

        Spectrum spectrum = spectrumRepository.findById(spectrumId);

        Hibernate.initialize(spectrum.getSpectrumFiles());
        if (!spectrum.getSpectrumFiles().isEmpty()) {
            try {
                SpectrumFile spectrumFile = spectrum.getSpectrumFiles().get(0);                
                byte[] unzippedBytes = iOManager.unzip(spectrumFile.getContent());
                
                MascotGenericFile mascotGenericFile = new MascotGenericFile("testMGFFile", new String(unzippedBytes));

                spectrumPeaks = mascotGenericFile.getPeaks();
            } catch (IOException ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }

        return spectrumPeaks;
    }
}
