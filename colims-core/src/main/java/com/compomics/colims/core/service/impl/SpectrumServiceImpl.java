package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.service.SpectrumService;
import com.compomics.colims.core.util.IOUtils;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.Spectrum;
import com.compomics.colims.model.SpectrumFile;
import com.compomics.colims.repository.SpectrumRepository;
import org.apache.log4j.Logger;
import org.hibernate.LazyInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Niels Hulstaert
 */
@Service("spectrumService")
@Transactional
public class SpectrumServiceImpl implements SpectrumService {

    /**
     * Logger instance.
     */
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
                } else if (inSpectrum && (!line.contains("="))) {
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
    public void persist(Spectrum entity) {
        spectrumRepository.persist(entity);
    }

    @Override
    public Spectrum merge(Spectrum entity) {
        return spectrumRepository.merge(entity);
    }

    @Override
    public void remove(Spectrum entity) {
        spectrumRepository.remove(entity);
    }

    @Override
    public Long countSpectraByAnalyticalRun(AnalyticalRun analyticalRun) {
        return spectrumRepository.countSpectraByAnalyticalRun(analyticalRun);
    }

    @Override
    public Double getMinimumRetentionTime(List<Long> analyticalRunIds) {
        return spectrumRepository.getMinimumRetentionTime(analyticalRunIds);
    }

    @Override
    public Double getMaximumRetentionTime(List<Long> analyticalRunIds) {
        return spectrumRepository.getMaximumRetentionTime(analyticalRunIds);
    }

    @Override
    public Double getMinimumMzRatio(List<Long> analyticalRunIds) {
        return spectrumRepository.getMinimumMzRatio(analyticalRunIds);
    }

    @Override
    public Double getMaximumMzRatio(List<Long> analyticalRunIds) {
        return spectrumRepository.getMaximumMzRatio(analyticalRunIds);
    }

    @Override
    public Integer getMinimumCharge(List<Long> analyticalRunIds) {
        return spectrumRepository.getMinimumCharge(analyticalRunIds);
    }

    @Override
    public Integer getMaximumCharge(List<Long> analyticalRunIds) {
        return spectrumRepository.getMaximumCharge(analyticalRunIds);
    }

    @Override
    public void fetchSpectrumFiles(Spectrum spectrum) {
        try {
            spectrum.getSpectrumFiles().size();
        } catch (LazyInitializationException e) {
            //fetch the spectrum files
            List<SpectrumFile> spectrumFiles = spectrumRepository.fetchSpectrumFiles(spectrum.getId());
            spectrum.setSpectrumFiles(spectrumFiles);
        }
    }

    @Override
    public List getPagedSpectra(AnalyticalRun analyticalRun, int start, int length, String orderBy, String direction, String filter) {
        return spectrumRepository.getPagedSpectra(analyticalRun, start, length, orderBy, direction, filter);
    }

    @Override
    public int getSpectraCountForRun(AnalyticalRun analyticalRun, String orderBy, String filter) {
        return spectrumRepository.getSpectraCountForRun(analyticalRun, orderBy, filter);
    }

    @Override
    public Peptide getRepresentativePeptide(Spectrum spectrum) {
        return spectrumRepository.getRepresentativePeptide(spectrum);
    }

    @Override
    public Object[] getSpectraProjections(List<Long> analyticalRunIds) {
        return spectrumRepository.getSpectraProjections(analyticalRunIds);
    }
}
