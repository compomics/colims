package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.distributed.io.maxquant.MaxQuantConstants;
import com.compomics.colims.distributed.io.maxquant.TabularFileLineValuesIterator;
import com.compomics.colims.distributed.io.maxquant.headers.HeaderEnum;
import com.compomics.colims.distributed.io.maxquant.headers.MaxQuantMSMSHeaders;
import com.compomics.colims.model.Spectrum;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Parser for the MaxQuant msms.txt output and .apl files that creates {@link Spectrum} and {@link
 * com.compomics.colims.model.SpectrumFile} instances.
 * <p/>
 *
 * @author niels
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
    private static final String KEY_START = "RawFile: ";
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
     * The map of identified spectra (key: the msms.txt ID; value: the mapped Spectrum instance).
     */
    private Map<Integer, Spectrum> identifiedSpectra = new HashMap<>();
    /**
     * The list of unidentified spectra.
     */
    private List<Spectrum> unidentifiedSpectra = new ArrayList<>();
    /**
     * The MaxQuantAndromedaParser for parsing the .apl spectra files.
     */
    @Autowired
    private MaxQuantAndromedaParser maxQuantAndromedaParser;

    public Map<Integer, Spectrum> getIdentifiedSpectra() {
        return identifiedSpectra;
    }

    public List<Spectrum> getUnidentifiedSpectra() {
        return unidentifiedSpectra;
    }

    /**
     * This method parses the msms.txt and .apl spectrum files and returns the result in a map.
     *
     * @param maxQuantDirectory          the MaxQuant parent directory
     * @param includeUnidentifiedSpectra whether or not to include the unidentified spectra
     * @return
     * @throws IOException
     */
    public void parse(Path maxQuantDirectory, boolean includeUnidentifiedSpectra) throws IOException {
        Path andromedaDirectory = Paths.get(maxQuantDirectory.toString() + File.separator + MaxQuantConstants.ANDROMEDA_DIRECTORY.value());
        Path msmsFile = Paths.get(maxQuantDirectory.toString() + File.separator + MaxQuantConstants.TXT_DIRECTORY.value() + File.separator + MaxQuantConstants.MSMS_FILE.value());

        //parse the parameter files in the andromeda directory
        maxQuantAndromedaParser.parseParameters(andromedaDirectory);

        Map<String, Integer> msmsIds = new HashMap<>();
        Map<String, Spectrum> spectra = new HashMap<>();
        //parse the msms.txt file
        parse(msmsFile, msmsIds, spectra);

        //parse the apl files containing the spectrum peak lists
        maxQuantAndromedaParser.parseSpectra(spectra, includeUnidentifiedSpectra);

        //clear the map before adding the identified spectra
        identifiedSpectra.clear();
        spectra.entrySet().stream().forEach(e -> identifiedSpectra.put(msmsIds.get(e.getKey()), e.getValue()));
    }

    /**
     * Clear the resources of this parser.
     */
    public void clear() {
        maxQuantAndromedaParser.clear();
        identifiedSpectra.clear();
        unidentifiedSpectra.clear();
    }

    /**
     * Parse the msms.txt file. This method returns a map with a String instance as key and the partially mapped
     * Spectrum instance as value for each row.
     *
     * @param msmsFile the MaxQuant msms.txt file path
     * @param msmsIds  the msms IDs map (key: apl spectrum header key; value: the msms ID)
     * @param spectra  the spectra map (key: apl spectrum header key; value: the mapped Spectrum instance)
     * @return the mapped spectra
     */
    private void parse(Path msmsFile, Map<String, Integer> msmsIds, Map<String, Spectrum> spectra) throws IOException {
        TabularFileLineValuesIterator valuesIterator = new TabularFileLineValuesIterator(msmsFile.toFile(), mandatoryHeaders);
        for (Map<String, String> spectrumValues : valuesIterator) {
            //concatenate the RAW file name and scan index
            String aplKey = KEY_START + spectrumValues.get(MaxQuantMSMSHeaders.RAW_FILE.getValue())
                    + KEY_MIDDLE
                    + spectrumValues.get(MaxQuantMSMSHeaders.SCAN_NUMBER.getValue());

            //add to msmsIds map
            msmsIds.putIfAbsent(aplKey, Integer.parseInt(spectrumValues.get(MaxQuantMSMSHeaders.ID.getValue())));
            //map the spectrum
            Spectrum spectrum = mapMsmsSpectrum(aplKey, spectrumValues);
            //add to spectra map
            spectra.putIfAbsent(aplKey, spectrum);
        }
    }

    /**
     * Map the spectrum values from a parsed msms.txt row entry onto a Colims {@link Spectrum}.
     *
     * @param aplKey         the apl key to use as spectrum accession
     * @param spectrumValues the map of spectrum values (key: column header; value: column value)
     * @return the mapped Spectrum instance
     */
    private Spectrum mapMsmsSpectrum(String aplKey, Map<String, String> spectrumValues) {
        String spectrumTitle = spectrumValues.get(MaxQuantMSMSHeaders.RAW_FILE.getValue())
                + TITLE_DELIMITER
                + spectrumValues.get(MaxQuantMSMSHeaders.SCAN_NUMBER.getValue());
        String intensity = spectrumValues.get(MaxQuantMSMSHeaders.PRECURSOR_INTENSITY.getValue());
        if (intensity.equalsIgnoreCase(NOT_A_NUMBER)) {
            intensity = NO_INTENSITY;
        }

        //create Colims Spectrum instance and map the fields
        Spectrum spectrum = new com.compomics.colims.model.Spectrum();
        spectrum.setAccession(aplKey);
        spectrum.setCharge(Integer.valueOf(spectrumValues.get(MaxQuantMSMSHeaders.CHARGE.getValue())));
        spectrum.setFragmentationType(maxQuantAndromedaParser.getFragmentationType());
        spectrum.setIntensity(Double.parseDouble(intensity));
        spectrum.setMzRatio(Double.valueOf(spectrumValues.get(MaxQuantMSMSHeaders.M_Z.getValue())));
        spectrum.setRetentionTime(Double.valueOf(spectrumValues.get(MaxQuantMSMSHeaders.RETENTION_TIME.getValue())));
        spectrum.setScanNumber(spectrumValues.get(MaxQuantMSMSHeaders.SCAN_NUMBER.getValue()));
        //use the scan event number for the scan time field
        spectrum.setScanTime(Double.valueOf(spectrumValues.get(MaxQuantMSMSHeaders.SCAN_EVENT_NUMBER.getValue())));
        spectrum.setTitle(spectrumTitle);

        return spectrum;
    }
}
