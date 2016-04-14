package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.distributed.io.maxquant.TabularFileLineValuesIterator;
import com.compomics.colims.distributed.io.maxquant.headers.HeaderEnum;
import com.compomics.colims.distributed.io.maxquant.headers.MaxQuantMSMSHeaders;
import com.compomics.colims.model.Spectrum;
import com.compomics.colims.model.SpectrumFile;
import com.compomics.colims.model.enums.FragmentationType;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.GZIPOutputStream;

/**
 * MaxQuant
 */
@Component("maxQuantSpectrumFileParser")
public class MaxQuantSpectrumFileParser {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(MaxQuantSpectrumFileParser.class);

    private static final String TITLE_DELIMITER = "-";

    private static final HeaderEnum[] mandatoryHeaders = new HeaderEnum[]{
            MaxQuantMSMSHeaders.ID,
            MaxQuantMSMSHeaders.FRAGMENTATION,
            MaxQuantMSMSHeaders.MATCHES,
            MaxQuantMSMSHeaders.INTENSITIES,
            MaxQuantMSMSHeaders.MASSES
    };

    public Map<Spectrum, Integer> parse(File msmsFile) throws IOException {
        Map<Spectrum, Integer> spectra = new HashMap<>();

        TabularFileLineValuesIterator valuesIterator = new TabularFileLineValuesIterator(msmsFile, mandatoryHeaders);

        for (Map<String, String> values : valuesIterator) {
            Integer id = Integer.parseInt(values.get(MaxQuantMSMSHeaders.ID.getDefaultColumnName()));

            spectra.put(parseSpectrum(values), id);
        }

        return spectra;
    }

    private Spectrum parseSpectrum(Map<String, String> values) {
        String spectrumTitle = values.get(MaxQuantMSMSHeaders.RAW_FILE.getDefaultColumnName()) + TITLE_DELIMITER + values.get(MaxQuantMSMSHeaders.SCAN_NUMBER.getDefaultColumnName());
        String intensity = values.get(MaxQuantMSMSHeaders.PRECURSOR_INTENSITY.getDefaultColumnName());
        if (intensity.equalsIgnoreCase("nan")) {
            intensity = "-1";
        }

        //create a Colims Spectrum instance
        Spectrum spectrum = new Spectrum();
        // is null checking required here? check other parsers
        spectrum.setAccession(values.get(MaxQuantMSMSHeaders.ID.getDefaultColumnName()));
        spectrum.setCharge(Integer.valueOf(values.get(MaxQuantMSMSHeaders.CHARGE.getDefaultColumnName())));
        //TODO: 19/01/16 get fragmentationtype from aplFiles file?
        spectrum.setFragmentationType(FragmentationType.valueOf(values.get(MaxQuantMSMSHeaders.FRAGMENTATION.getDefaultColumnName())));
        spectrum.setIntensity(Double.parseDouble(intensity));
        spectrum.setMzRatio(Double.valueOf(values.get(MaxQuantMSMSHeaders.M_Z.getDefaultColumnName())));
        spectrum.setRetentionTime(Double.valueOf(values.get(MaxQuantMSMSHeaders.RETENTION_TIME.getDefaultColumnName())));
        spectrum.setScanNumber(values.get(MaxQuantMSMSHeaders.SCAN_NUMBER.getDefaultColumnName()));
//        spectrum.setScanTime(); TODO: missing in original method but used in mapper
        spectrum.setTitle(spectrumTitle);

        Map<Double, Double> peakList = parsePeakList(values.get(MaxQuantMSMSHeaders.MATCHES.getDefaultColumnName()),
                values.get(MaxQuantMSMSHeaders.INTENSITIES.getDefaultColumnName()),
                values.get(MaxQuantMSMSHeaders.MASSES.getDefaultColumnName())
        );

        spectrum.setSpectrumFiles(new ArrayList<>());
//        spectrum.getSpectrumFiles().add(spectrumToMGF(spectrum, scanNumber, peakList));

        return spectrum;
    }

    /**
     * Parse a series of strings (separated with ;) (not winky face) and create some mad peaks
     *
     * @param peaks       String of peaks
     * @param intensities String of intensities
     * @param masses      String of masses
     * @return A map of peaks keyed with m/z
     */
    public Map<Double, Double> parsePeakList(String peaks, String intensities, String masses) throws IllegalArgumentException {
        Map<Double, Double> peakMap = new TreeMap<>();

        if (!peaks.isEmpty() && !intensities.isEmpty() && !masses.isEmpty()) {
            String[] peakList = peaks.split(";");
            String[] intensityList = intensities.split(";");
            String[] massList = masses.split(";");

            if (intensityList.length != peakList.length || massList.length != peakList.length) {
                throw new IllegalArgumentException("Input lists are not equal length");
            }

            for (int i = 0; i < peakList.length; i++) {
                int charge = 1;

                Double mZ = Double.parseDouble(massList[i]) / charge;
                peakMap.put(mZ, Double.parseDouble(intensityList[i]));
            }
        }

        return peakMap;
    }

    private SpectrumFile spectrumToMGF(Spectrum spectrum, String scanNumber, Map<Double, Double> peakList) {
        StringBuilder results = new StringBuilder();
        String newLine = System.getProperty("line.separator");

        results.append("BEGIN IONS").append(newLine)
                .append("TITLE=").append(spectrum.getTitle()).append(newLine)
                .append("PEPMASS=").append(spectrum.getMzRatio()).append("\t").append(spectrum.getIntensity()).append(newLine);

        if (spectrum.getRetentionTime() != -1) {
            results.append("RTINSECONDS=").append(spectrum.getRetentionTime()).append(newLine);
        }

        if (spectrum.getCharge() != null) {
            // TODO: add as method param and do loop in calling method
            results.append("CHARGE=").append(spectrum.getCharge()).append(newLine);
        }

        if (scanNumber != null && !scanNumber.equals("")) {
            results.append("SCANS=").append(scanNumber).append(newLine);
        }

        peakList.entrySet().stream().forEach((entry) -> {
            results.append(entry.getKey()).append(" ").append(entry.getValue()).append(newLine);
        });

        results.append("END IONS").append(newLine).append(newLine);

        SpectrumFile spectrumFile = new SpectrumFile();
        byte[] mgfBytes = results.toString().getBytes(Charset.forName("UTF-8"));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            GZIPOutputStream gzipStream = new GZIPOutputStream(outputStream);
            gzipStream.write(mgfBytes);
            gzipStream.flush();
            gzipStream.finish();

            spectrumFile.setContent(outputStream.toByteArray());
        } catch (IOException e) {

        }

        return spectrumFile;
    }
}
