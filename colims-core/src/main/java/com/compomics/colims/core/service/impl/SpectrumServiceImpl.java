package com.compomics.colims.core.service.impl;

import com.compomics.colims.core.service.SpectrumService;
import com.compomics.colims.core.util.IOUtils;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.Spectrum;
import com.compomics.colims.model.SpectrumFile;
import com.compomics.colims.repository.SpectrumRepository;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.TreeMap;

/**
 * @author Niels Hulstaert
 */
@Service("spectrumService")
@Transactional
public class SpectrumServiceImpl implements SpectrumService {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SpectrumServiceImpl.class);

    /**
     * This constant defines the start tag for the ions.
     */
    private static final String IONS_START = "BEGIN IONS";
    /**
     * This constant defines the ernd tag for the ions.
     */
    private static final String IONS_END = "END IONS";
    private final SpectrumRepository spectrumRepository;

    @Autowired
    public SpectrumServiceImpl(SpectrumRepository spectrumRepository) {
        this.spectrumRepository = spectrumRepository;
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
    public Map<Double, Double> getSpectrumPeaks(SpectrumFile spectrumFile) throws IOException {
        byte[] unzippedBytes = IOUtils.unzip(spectrumFile.getContent());

        Map<Double, Double> spectrumPeaks = new HashMap<>();
        populatePeakMap(unzippedBytes, spectrumFile.getSpectrum().getAccession(), spectrumPeaks);

        return spectrumPeaks;
    }

    @Override
    public TreeMap<Double, Double> getSortedSpectrumPeaks(SpectrumFile spectrumFile) throws IOException {
        byte[] unzippedBytes = IOUtils.unzip(spectrumFile.getContent());

        TreeMap<Double, Double> spectrumPeaks = new TreeMap<>();
        populatePeakMap(unzippedBytes, spectrumFile.getSpectrum().getAccession(), spectrumPeaks);

        return spectrumPeaks;
    }

    /**
     * Populate the given map with the spectrum peaks.
     *
     * @param bytes     the unzipped MGF byte array
     * @param accession the spectrum accession for logging purposes
     * @param peakMap   the map of spectrum peaks
     * @throws IOException in case of a spectrum file read problem
     */
    private void populatePeakMap(byte[] bytes, String accession, Map<Double, Double> peakMap) throws IOException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
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
                        peakMap.put(mass, intensity);
                    } else {
                        LOGGER.error("Unrecognized line while parsing peaks from spectrum " + accession + " in MGF format.");
                    }
                }
            }
        }
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
        if (!Hibernate.isInitialized(spectrum.getSpectrumFiles())) {
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
