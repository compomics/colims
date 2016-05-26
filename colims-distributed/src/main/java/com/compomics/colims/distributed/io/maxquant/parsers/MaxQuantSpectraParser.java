package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.distributed.io.maxquant.TabularFileLineValuesIterator;
import com.compomics.colims.distributed.io.maxquant.headers.HeaderEnum;
import com.compomics.colims.distributed.io.maxquant.headers.MaxQuantMSMSHeaders;
import com.compomics.colims.model.Spectrum;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
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
     * The MaxQuantAndromedaParser for parsing the .apl spectra files.
     */
    @Autowired
    private MaxQuantAndromedaParser maxQuantAndromedaParser;

    public Map<String, Spectrum> parse(File maxQuantDirectory, boolean includeUnidentifiedSpectra) throws IOException {
        Map<String, Spectrum> spectra;

        // TODO: 26/05/16 fix this
        File msmsFile = new FileSystemResource(maxQuantDirectory.getPath() + File.separator + "txt" + File.separator + "msms.txt").getFile();
        //parse the aplFiles summary file
//        maxQuantAndromedaParser.init(maxQuantDirectory);

        //parse the msms.txt file
        spectra = parse(msmsFile);

        //parse the apl files containing the spectrum peak lists
        maxQuantAndromedaParser.parseSpectra(spectra, includeUnidentifiedSpectra);

        return spectra;
    }

    /**
     * Parse the msms.txt file. This method returns a map with a String instance as key and the partially mapped
     * Spectrum instance as value for each row.
     *
     * @param msmsFile the MaxQuant msms.txt file
     * @return the mapped spectra
     */
    private Map<String, Spectrum> parse(File msmsFile) throws IOException {
        Map<String, Spectrum> spectra = new HashMap<>();

        TabularFileLineValuesIterator valuesIterator = new TabularFileLineValuesIterator(msmsFile, mandatoryHeaders);
        for (Map<String, String> spectrumValues : valuesIterator) {
            //concatenate the RAW file name and scan index
            String aplKey = KEY_START + spectrumValues.get(MaxQuantMSMSHeaders.RAW_FILE.getValue())
                    + KEY_MIDDLE
                    + spectrumValues.get(MaxQuantMSMSHeaders.SCAN_NUMBER.getValue());

            Spectrum spectrum = mapMsmsSpectrum(aplKey, spectrumValues);
            spectra.putIfAbsent(aplKey, spectrum);
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
