package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.distributed.io.maxquant.FixedTabularFileIterator;
import com.compomics.colims.distributed.io.maxquant.MaxQuantConstants;
import com.compomics.colims.distributed.io.maxquant.headers.MsmsHeader;
import com.compomics.colims.distributed.io.maxquant.headers.MsmsHeaders;
import com.compomics.colims.model.Spectrum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Parser for the MaxQuant msms.txt output and .apl files that creates
 * {@link Spectrum} and {@link
 * com.compomics.colims.model.SpectrumFile} instances.
 * <p/>
 *
 * @author niels
 */
@Component("maxQuantSpectraParser")
public class MaxQuantSpectraParser {

    /**
     * The start of the spectrum header in the apl file.
     */
    private static final String KEY_START = "RawFile: ";
    private static final String KEY_MIDDLE = " Index: ";
    private static final String TITLE_DELIMITER = "--";
    private static final String NOT_A_NUMBER = "nan";
    private static final String NO_INTENSITY = "-1";
    private static final String PROTEIN_GROUP_ID_DELIMITER = ";";

    /**
     * MaxQuantSpectra object to store identified and unidentified spectra.
     */
    private MaxQuantSpectra maxQuantSpectra = new MaxQuantSpectra();
    /**
     * The msms.txt headers.
     */
    private final MsmsHeaders msmsHeaders;
    /**
     * The MaxQuantAndromedaParser for parsing the .apl spectra files.
     */
    private final MaxQuantAndromedaParser maxQuantAndromedaParser;

    /**
     * No-arg constructor.
     *
     * @throws IOException in case of an Input/Output related problem while parsing the headers.
     */
    @Autowired
    public MaxQuantSpectraParser(MaxQuantAndromedaParser maxQuantAndromedaParser) throws IOException {
        msmsHeaders = new MsmsHeaders();
        this.maxQuantAndromedaParser = maxQuantAndromedaParser;
    }

    /**
     * Clear run data from parser.
     */
    public void clear() {
        maxQuantSpectra = new MaxQuantSpectra();
        maxQuantAndromedaParser.clear();
    }

    /**
     * Get maxQuantSpectra.
     *
     * @return maxQuantSpectra
     */
    public MaxQuantSpectra getMaxQuantSpectra() {
        return maxQuantSpectra;
    }

    /**
     * This method parses the msms.txt and .apl spectrum files and returns the
     * result in a map.
     *
     * @param maxQuantDirectory          the MaxQuant parent directory
     * @param includeUnidentifiedSpectra whether or not to include the unidentified spectra
     * @param removedProteinGroupIds     removed protein group IDs.
     * @throws IOException in case of an Input/Output related problem
     */
    public void parse(Path maxQuantDirectory, boolean includeUnidentifiedSpectra, List<String> removedProteinGroupIds) throws IOException {
        Path andromedaDirectory = Paths.get(maxQuantDirectory.toString() + File.separator + MaxQuantConstants.ANDROMEDA_DIRECTORY.value());
        if (!Files.exists(andromedaDirectory)) {
            throw new FileNotFoundException("The andromeda directory " + andromedaDirectory.toString() + " was not found.");
        }
        Path msmsFile = Paths.get(maxQuantDirectory.toString() + File.separator + MaxQuantConstants.TXT_DIRECTORY.value() + File.separator + MaxQuantConstants.MSMS_FILE.value());
        if (!Files.exists(msmsFile)) {
            throw new FileNotFoundException("The msms.txt file " + msmsFile.toString() + " was not found.");
        }

        //parse the parameter files in the andromeda directory
        maxQuantAndromedaParser.parseParameters(andromedaDirectory);

        //parse the msms.txt file
        parse(msmsFile, maxQuantSpectra, removedProteinGroupIds);

        //parse the apl files containing the spectrum peak lists
        maxQuantAndromedaParser.parseSpectra(maxQuantSpectra, includeUnidentifiedSpectra);
    }

    /**
     * Parse the msms.txt file.
     *
     * @param msmsFile               the MaxQuant msms.txt file path
     * @param omittedProteinGroupIds the list of omitted protein group IDs
     */
    private void parse(Path msmsFile, MaxQuantSpectra maxQuantSpectra, List<String> omittedProteinGroupIds) throws IOException {
        FixedTabularFileIterator valuesIterator = new FixedTabularFileIterator(msmsFile, msmsHeaders);

        Spectrum spectrum;
        EnumMap<MsmsHeader, String> msmsEntry;
        while (valuesIterator.hasNext()) {
            msmsEntry = valuesIterator.next();
            String[] proteinGroupsIds = msmsEntry.get(MsmsHeader.PROTEIN_GROUP_IDS).split(PROTEIN_GROUP_ID_DELIMITER);

            boolean omittedSpectrum = true;
            for (String proteinGroupId : proteinGroupsIds) {
                if (!omittedProteinGroupIds.contains(proteinGroupId)) {
                    omittedSpectrum = false;
                }
            }
            //concatenate the RAW file name and scan index
            String rawFileName = msmsEntry.get(MsmsHeader.RAW_FILE);
            String aplKey = KEY_START + rawFileName
                    + KEY_MIDDLE
                    + msmsEntry.get(MsmsHeader.SCAN_NUMBER);
            if (!omittedSpectrum) {
                //map the spectrum
                if (!maxQuantSpectra.getSpectra().containsKey(aplKey)) {
                    spectrum = mapMsmsSpectrum(aplKey, msmsEntry);
                    //add the apl key to it's corresponding run
                    if (!maxQuantSpectra.getRunToSpectrums().containsKey(rawFileName)) {
                        List<String> aplKeys = new ArrayList<>();
                        aplKeys.add(aplKey);
                        maxQuantSpectra.getRunToSpectrums().put(rawFileName, aplKeys);
                    } else {
                        maxQuantSpectra.getRunToSpectrums().get(rawFileName).add(aplKey);
                    }
                    //add the Spectrum instance to the aplKeyToSpectrums with the aplKey as a key
                    maxQuantSpectra.getSpectra().put(aplKey, spectrum);
                    //add the msms.txt ID to the aplKeyToMsmsIds map with the aplKey as a key
                    List<Integer> msmsIds = new ArrayList<>();
                    msmsIds.add(Integer.parseInt(msmsEntry.get(MsmsHeader.ID)));
                    List<Integer> put = maxQuantSpectra.getSpectrumToPsms().put(aplKey, msmsIds);
                    if (put != null) {
                        throw new IllegalStateException("The same spectrum has already been added to the map.");
                    }
                } else {
                    //add the msms.txt ID to the list of already existing msms.txt IDs for the given apl key
                    maxQuantSpectra.getSpectrumToPsms().get(aplKey).add(Integer.parseInt(msmsEntry.get(MsmsHeader.ID)));
                }
            } else {
                maxQuantSpectra.getOmittedSpectrumKeys().add(aplKey);
            }
        }
    }

    /**
     * Map the spectrum values from a parsed msms.txt row entry onto a Colims
     * {@link Spectrum}.
     *
     * @param aplKey         the apl key to use as spectrum accession
     * @param spectrumValues the map of spectrum values (key: {@link MsmsHeader} instance; value: column value)
     * @return the mapped Spectrum instance
     */
    private Spectrum mapMsmsSpectrum(String aplKey, Map<MsmsHeader, String> spectrumValues) {
        String spectrumTitle = spectrumValues.get(MsmsHeader.RAW_FILE)
                + TITLE_DELIMITER
                + spectrumValues.get(MsmsHeader.SCAN_NUMBER);
        String intensity = spectrumValues.get(MsmsHeader.PRECURSOR_INTENSITY);
        if (intensity.equalsIgnoreCase(NOT_A_NUMBER)) {
            intensity = NO_INTENSITY;
        }

        //create Colims Spectrum instance and map the fields
        Spectrum spectrum = new com.compomics.colims.model.Spectrum();
        spectrum.setAccession(aplKey);
        spectrum.setCharge(Integer.valueOf(spectrumValues.get(MsmsHeader.CHARGE)));
        spectrum.setFragmentationType(maxQuantAndromedaParser.getFragmentationType());
        spectrum.setIntensity(Double.parseDouble(intensity));
        spectrum.setMzRatio(Double.valueOf(spectrumValues.get(MsmsHeader.M_Z)));
        spectrum.setRetentionTime(Double.valueOf(spectrumValues.get(MsmsHeader.RETENTION_TIME)));
        spectrum.setScanNumber(spectrumValues.get(MsmsHeader.SCAN_NUMBER));
        //use the scan event number for the scan time field
        spectrum.setScanTime(Double.valueOf(spectrumValues.get(MsmsHeader.SCAN_EVENT_NUMBER)));
        spectrum.setTitle(spectrumTitle);

        return spectrum;
    }

}
