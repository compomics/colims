package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.distributed.io.maxquant.MaxQuantConstants;
import com.compomics.colims.distributed.io.maxquant.TabularFileLineValuesIterator;
import com.compomics.colims.distributed.io.maxquant.headers.HeaderEnum;
import com.compomics.colims.distributed.io.maxquant.headers.MaxQuantEvidenceHeaders;
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
import java.util.Arrays;
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
    private static final String TITLE_DELIMITER = "--";
    private static final String NOT_A_NUMBER = "nan";
    private static final String NO_INTENSITY = "-1";
    private static final String MSMS_TYPE = "MULTI-SECPEP";

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

    /**
     * MaxQuantSpectra object to store identified and unidentified spectra
     */
    private MaxQuantSpectra maxQuantSpectra = new MaxQuantSpectra();

    /**
     * Get maxQuantSpectra
     * @return maxQuantSpectra
     */
    public MaxQuantSpectra getMaxQuantSpectra() {
        return maxQuantSpectra;
    }


    /**
     * This method parses the msms.txt and .apl spectrum files and returns the result in a map.
     *
     * @param maxQuantDirectory          the MaxQuant parent directory
     * @param includeUnidentifiedSpectra whether or not to include the unidentified spectra
     * @param removedProteinGroupIds removed protein group IDs.
     * @throws IOException
     */
    public void parse(Path maxQuantDirectory, boolean includeUnidentifiedSpectra,  List<String> removedProteinGroupIds) throws IOException {
        Path andromedaDirectory = Paths.get(maxQuantDirectory.toString() + File.separator + MaxQuantConstants.ANDROMEDA_DIRECTORY.value());
        Path msmsFile = Paths.get(maxQuantDirectory.toString() + File.separator + MaxQuantConstants.TXT_DIRECTORY.value() + File.separator + MaxQuantConstants.MSMS_FILE.value());

        //parse the parameter files in the andromeda directory
        maxQuantAndromedaParser.parseParameters(andromedaDirectory);


        //parse the msms.txt file
        parse(msmsFile, maxQuantSpectra, removedProteinGroupIds);

        //parse the apl files containing the spectrum peak lists
        maxQuantAndromedaParser.parseSpectra(maxQuantSpectra, includeUnidentifiedSpectra);

    }

    /**
     * Parse the msms.txt file. This method returns a map with a String instance as key and the partially mapped
     * Spectrum instance as value for each row.
     *
     * @param msmsFile the MaxQuant msms.txt file path
     * @param msmsIds  the msms IDs map (key: apl spectrum header key; value: the msms ID)
     * @param spectra  the spectra map (key: apl spectrum header key; value: the mapped Spectrum instance)
     * @param removedProteinGroupIds removed protein group IDs.
     * @return the mapped spectra
     */
    private void parse(Path msmsFile,MaxQuantSpectra maxQuantSpectra,  List<String> removedProteinGroupIds) throws IOException {
        TabularFileLineValuesIterator valuesIterator = new TabularFileLineValuesIterator(msmsFile.toFile(), mandatoryHeaders);
        Spectrum spectrum = new Spectrum();
        for (Map<String, String> spectrumValues : valuesIterator) {
            if(!removedProteinGroupIds.contains(spectrumValues.get(MaxQuantMSMSHeaders.PROTEIN_GROUP_IDS.getValue()))){
                //concatenate the RAW file name and scan index
                String aplKey = KEY_START + spectrumValues.get(MaxQuantMSMSHeaders.RAW_FILE.getValue())
                    + KEY_MIDDLE
                    + spectrumValues.get(MaxQuantMSMSHeaders.SCAN_NUMBER.getValue());

                //map the spectrum
                if (!maxQuantSpectra.getAplSpectra().containsKey(aplKey)){
                    spectrum = mapMsmsSpectrum(aplKey, spectrumValues);
                    //add to apl spectra map
                    maxQuantSpectra.getAplSpectra().putIfAbsent(aplKey, spectrum);
                    //map the spectrumIDS map where ID numbers are from msms file
                    maxQuantSpectra.getSpectrumIDs().put(spectrum, new ArrayList<Integer>
                        (Arrays.asList(Integer.parseInt(spectrumValues.get(MaxQuantMSMSHeaders.ID.getValue())))));
                }else{
                    // get the spectrum from aplSpectra and find that spectrum instance from spectrumIDs and add id to the list
                    maxQuantSpectra.getSpectrumIDs().get(maxQuantSpectra.getAplSpectra().get(aplKey)).add(Integer.parseInt(spectrumValues.get(MaxQuantMSMSHeaders.ID.getValue())));
                }
            }
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

    /**
     * Clear run data from parser.
     */
    public void clear(){
        maxQuantSpectra.clear();
        maxQuantAndromedaParser.clear();
    }
}
