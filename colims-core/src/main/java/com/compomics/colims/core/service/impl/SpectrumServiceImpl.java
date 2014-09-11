package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.service.SpectrumService;
import com.compomics.colims.core.util.IOUtils;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.Spectrum;
import com.compomics.colims.model.SpectrumFile;
import com.compomics.colims.repository.SpectrumRepository;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Niels Hulstaert
 */
@Service("spectrumService")
@Transactional
public class SpectrumServiceImpl implements SpectrumService {

    private static final Logger LOGGER = Logger.getLogger(SpectrumServiceImpl.class);
    /**
     * This constant defines the start tag for the ions.
     */
    private static final String IONS_START = "BEGIN IONS";
    /**
     * This constant defines the ernd tag for the ions.
     */
    private static final String IONS_END = "END IONS";
    @Autowired
    private SpectrumRepository spectrumRepository;

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
    public Map<Double, Double> getSpectrumPeaks(Long spectrumId) throws IOException {
        Map<Double, Double> spectrumPeaks = new HashMap<>();

        Spectrum spectrum = spectrumRepository.findById(spectrumId);

        Hibernate.initialize(spectrum.getSpectrumFiles());
        if (!spectrum.getSpectrumFiles().isEmpty()) {
            SpectrumFile spectrumFile = spectrum.getSpectrumFiles().get(0);
            spectrumPeaks = getSpectrumPeaks(spectrumFile);
        }

        return spectrumPeaks;
    }

    @Override
    public Map<Double, Double> getSpectrumPeaks(SpectrumFile spectrumFile) throws IOException {
        byte[] unzippedBytes = IOUtils.unzip(spectrumFile.getContent());

        Map<Double, Double> spectrumPeaks = new HashMap<>();
        try (ByteArrayInputStream bais = new ByteArrayInputStream(unzippedBytes);
                InputStreamReader isr = new InputStreamReader(bais, Charset.forName("UTF-8").newDecoder());
                BufferedReader br = new BufferedReader(isr)) {
            boolean inSpectrum = false;
            String line;
            while ((line = br.readLine()) != null) {
                //Delete leading/trailing spaces.
                line = line.trim();
                if (line.startsWith(IONS_START)) {
                    inSpectrum = true;
                } else if (line.startsWith(IONS_END)) {
                    break;
                } else if (inSpectrum && (line.indexOf("=") < 0)) {
                    // We're inside the spectrum, with no '=' in the line, so it should be
                    // a peak line.
                    // A peak line should be either of the following two:
                    // 234.56 789
                    // 234.56 789   1+
                    String[] splits = line.split("[ \t]");
                    if (splits.length == 2 || splits.length == 3) {
                        Double mass = Double.parseDouble(splits[0]);
                        Double intensity = Double.parseDouble(splits[1]);
                        spectrumPeaks.put(mass, intensity);
                    } else {
                        LOGGER.error("Unrecognized line while parsing peaks from spectrum " + spectrumFile.getSpectrum().getAccession() + " in MGF format.");
                    }
                }
            }
        } catch (IOException ex) {
            LOGGER.error(ex);
            throw ex;
        }

        return spectrumPeaks;
    }

    @Override
    public long countAll() {
        return spectrumRepository.countAll();
    }

    @Override
    public Long countSpectraByAnalyticalRun(AnalyticalRun analyticalRun) {
        return spectrumRepository.countSpectraByAnalyticalRun(analyticalRun);
    }

    @Override
    public Double getMinimumRetentionTime(AnalyticalRun analyticalRun) {
        return spectrumRepository.getMinimumRetentionTime(analyticalRun);
    }

    @Override
    public Double getMaximumRetentionTime(AnalyticalRun analyticalRun) {
        return spectrumRepository.getMaximumRetentionTime(analyticalRun);
    }

    @Override
    public Double getMinimumMzRatio(AnalyticalRun analyticalRun) {
        return spectrumRepository.getMinimumMzRatio(analyticalRun);
    }

    @Override
    public Double getMaximumMzRatio(AnalyticalRun analyticalRun) {
        return spectrumRepository.getMaximumMzRatio(analyticalRun);
    }

    @Override
    public Integer getMinimumCharge(AnalyticalRun analyticalRun) {
        return spectrumRepository.getMinimumCharge(analyticalRun);
    }

    @Override
    public Integer getMaximumCharge(AnalyticalRun analyticalRun) {
        return spectrumRepository.getMaximumCharge(analyticalRun);
    }

    @Override
    public void fetchSpectrumFiles(Spectrum spectrum) {
        try {
            //attach the spectrum to the new session
            spectrumRepository.saveOrUpdate(spectrum);
            if (!Hibernate.isInitialized(spectrum.getSpectrumFiles())) {
                Hibernate.initialize(spectrum.getSpectrumFiles());
            }
        } catch (HibernateException hbe) {
            LOGGER.error(hbe, hbe.getCause());
        }
    }

}
