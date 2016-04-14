package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.distributed.io.maxquant.MaxQuantConstants;
import com.compomics.colims.distributed.io.maxquant.TabularFileLineValuesIterator;
import com.compomics.colims.distributed.io.maxquant.headers.HeaderEnum;
import com.compomics.colims.distributed.io.maxquant.headers.MaxQuantMSMSHeaders;
import com.compomics.colims.model.Spectrum;
import com.compomics.colims.model.SpectrumFile;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.GZIPOutputStream;

/**
 * Parser for the MaxQuant msms.txt output files that creates {@link Spectrum} instances.
 * <p/>
 * This class uses the {@link TabularFileLineValuesIterator} to actually parse the files into Map<String,String>
 * records.
 */
@Component("maxQuantSpectraParser")
public class MaxQuantSpectraParser {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(MaxQuantSpectraParser.class);

    /**
     * The start of the spectrum header in the apl file.
     */
    private static final String KEY_START = "header=RawFile: ";
    private static final String KEY_MIDDLE = " Index: ";
    private static final String TITLE_DELIMITER = "-";
    private static final String NOT_A_NUMBER = "nan";
    private static final String NO_INTENSITY = "-1";

    private static final HeaderEnum[] mandatoryHeaders = new HeaderEnum[]{
            MaxQuantMSMSHeaders.ID,
            MaxQuantMSMSHeaders.MATCHES,
            MaxQuantMSMSHeaders.INTENSITIES,
            MaxQuantMSMSHeaders.MASSES,
            MaxQuantMSMSHeaders.SCAN_EVENT_NUMBER,
            MaxQuantMSMSHeaders.SCAN_INDEX
    };

    /**
     * The MaxQuantAplParser for parsing the actual spectra.
     */
    @Autowired
    private MaxQuantAplParser maxQuantAplParser;

    public Map<SpectrumKey, Spectrum> parse(File msmsFile, File aplFiles, boolean includeUnidentifiedSpectra) throws IOException {
        Map<SpectrumKey, Spectrum> spectra;

        //parse the aplFiles summary file
        maxQuantAplParser.init(new File(MaxQuantConstants.ANDROMEDA_DIRECTORY.value()));

        //parse the msms.txt file
        spectra = parseMsmsFile(msmsFile);

        //parse the apl files containing the spectrum peak lists
        maxQuantAplParser.parse(spectra, includeUnidentifiedSpectra);

        return spectra;
    }

    /**
     * Parse the msms.txt file. This method returns a map with the String concatenation of RAW file name and scan
     * sequence number as key and the partially mapped Spectrum instance as value for each row.
     *
     * @param msmsFile the MaxQuant msms.txt file
     * @return the mapped spectra
     */
    private Map<SpectrumKey, Spectrum> parseMsmsFile(File msmsFile) throws IOException {
        Map<SpectrumKey, Spectrum> spectra = new HashMap<>();

        TabularFileLineValuesIterator valuesIterator = new TabularFileLineValuesIterator(msmsFile, mandatoryHeaders);
        for (Map<String, String> spectrumValues : valuesIterator) {
            String idString = spectrumValues.get(MaxQuantMSMSHeaders.ID.getDefaultColumnName());
            Long id = Long.parseLong(idString);
            //concatenate the RAW file name and scan index
            String aplKey = KEY_START + spectrumValues.get(MaxQuantMSMSHeaders.RAW_FILE.getDefaultColumnName())
                    + KEY_MIDDLE
                    + spectrumValues.get(MaxQuantMSMSHeaders.SCAN_INDEX.getDefaultColumnName());

//            Spectrum spectrum = mapMsmsSpectrum(spectrumValues);
//            spectra.put(new SpectrumKey(id, aplKey), spectrum);
        }

        return spectra;
    }

    /**
     * Map the spectrum values from a parsed msms.txt row entry onto a Colims {@link Spectrum}.
     *
     * @param aplKey         the apl key to use as spectrum accession
     * @param spectrumValues the map of spectrum values (key: column header; value: column value)
     * @return the mapped Spectrum instance
     */
    private Spectrum mapMsmsSpectrum(String aplKey, Map<String, String> spectrumValues) {
        String spectrumTitle = spectrumValues.get(MaxQuantMSMSHeaders.RAW_FILE.getDefaultColumnName())
                + TITLE_DELIMITER
                + spectrumValues.get(MaxQuantMSMSHeaders.SCAN_NUMBER.getDefaultColumnName());
        String intensity = spectrumValues.get(MaxQuantMSMSHeaders.PRECURSOR_INTENSITY.getDefaultColumnName());
        if (intensity.equalsIgnoreCase(NOT_A_NUMBER)) {
            intensity = NO_INTENSITY;
        }

        //create Colims Spectrum instance and map the fields
        Spectrum spectrum = new com.compomics.colims.model.Spectrum();
        spectrum.setAccession(aplKey);
        spectrum.setCharge(Integer.valueOf(spectrumValues.get(MaxQuantMSMSHeaders.CHARGE.getDefaultColumnName())));
        spectrum.setFragmentationType(maxQuantAplParser.getFragmentationType());
        spectrum.setIntensity(Double.parseDouble(intensity));
        spectrum.setMzRatio(Double.valueOf(spectrumValues.get(MaxQuantMSMSHeaders.M_Z.getDefaultColumnName())));
        spectrum.setRetentionTime(Double.valueOf(spectrumValues.get(MaxQuantMSMSHeaders.RETENTION_TIME.getDefaultColumnName())));
        spectrum.setScanNumber(spectrumValues.get(MaxQuantMSMSHeaders.SCAN_NUMBER.getDefaultColumnName()));
        //use the scan event number for the scan time field
        spectrum.setScanTime(Double.valueOf(spectrumValues.get(MaxQuantMSMSHeaders.SCAN_EVENT_NUMBER.getDefaultColumnName())));
        spectrum.setTitle(spectrumTitle);

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
