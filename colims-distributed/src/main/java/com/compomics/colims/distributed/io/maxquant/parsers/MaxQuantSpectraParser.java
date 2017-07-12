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
import java.util.*;

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
    private static final String NOT_A_NUMBER = "nan";
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
     * Constructor.
     *
     * @throws IOException in case of an Input/Output related problem while
     *                     parsing the headers.
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
     * @param includeUnidentifiedSpectra whether or not to include the
     *                                   unidentified spectra
     * @param omittedProteinGroupIds     removed protein group IDs
     * @throws IOException in case of an Input/Output related problem
     */
    public void parse(Path maxQuantDirectory, boolean includeUnidentifiedSpectra, Set<Integer> omittedProteinGroupIds) throws IOException {
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
        parse(msmsFile, maxQuantSpectra, omittedProteinGroupIds);

        //parse the apl files containing the spectrum peak lists
        maxQuantAndromedaParser.parseSpectra(maxQuantSpectra, includeUnidentifiedSpectra);
    }

    /**
     * Parse the msms.txt file.
     *
     * @param msmsFile               the MaxQuant msms.txt file path
     * @param omittedProteinGroupIds the set of omitted protein group IDs
     */
    private void parse(Path msmsFile, MaxQuantSpectra maxQuantSpectra, Set<Integer> omittedProteinGroupIds) throws IOException {
        FixedTabularFileIterator<MsmsHeader> valuesIterator = new FixedTabularFileIterator(msmsFile, msmsHeaders);

        AnnotatedSpectrum spectrum;
        EnumMap<MsmsHeader, String> msmsEntry;
        while (valuesIterator.hasNext()) {
            msmsEntry = valuesIterator.next();
            String[] proteinGroupsIds = msmsEntry.get(MsmsHeader.PROTEIN_GROUP_IDS).split(PROTEIN_GROUP_ID_DELIMITER);

            Optional<Integer> anyNonOmittedProteinId = Arrays.stream(proteinGroupsIds).map(Integer::valueOf).filter(proteinGroupId -> !omittedProteinGroupIds.contains(proteinGroupId)).findAny();

            //concatenate the RAW file name and scan index
            String rawFileName = msmsEntry.get(MsmsHeader.RAW_FILE);
            String aplKey = KEY_START + rawFileName
                    + KEY_MIDDLE
                    + msmsEntry.get(MsmsHeader.SCAN_NUMBER);
            if (anyNonOmittedProteinId.isPresent()) {
                //map the spectrum
                if (!maxQuantSpectra.getSpectra().containsKey(aplKey)) {
                    spectrum = mapMsmsSpectrum(aplKey, msmsEntry);
                    //add the apl key to it's corresponding run
                    if (!maxQuantSpectra.getRunToSpectrums().containsKey(rawFileName)) {
                        Set<String> aplKeys = new HashSet<>();
                        aplKeys.add(aplKey);
                        maxQuantSpectra.getRunToSpectrums().put(rawFileName, aplKeys);
                    } else {
                        maxQuantSpectra.getRunToSpectrums().get(rawFileName).add(aplKey);
                    }
                    //add the Spectrum instance to the spectra with the aplKey as a key
                    maxQuantSpectra.getSpectra().put(aplKey, spectrum);
                    //add the msms.txt ID to the spectrumToPsms map with the aplKey as a key
                    Set<Integer> msmsIds = new HashSet<>();
                    msmsIds.add(Integer.parseInt(msmsEntry.get(MsmsHeader.ID)));
                    Set<Integer> put = maxQuantSpectra.getSpectrumToPsms().put(aplKey, msmsIds);
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
     * @param spectrumValues the map of spectrum values (key: {@link MsmsHeader}
     *                       instance; value: column value)
     * @return the mapped Spectrum instance
     */
    private AnnotatedSpectrum mapMsmsSpectrum(String aplKey, Map<MsmsHeader, String> spectrumValues) {
        //create Colims Spectrum instance and map the fields
        Spectrum spectrum = new com.compomics.colims.model.Spectrum();
        spectrum.setAccession(aplKey);
        spectrum.setCharge(Integer.valueOf(spectrumValues.get(MsmsHeader.CHARGE)));
        spectrum.setFragmentationType(maxQuantAndromedaParser.getFragmentationType());
        String intensity = spectrumValues.get(MsmsHeader.PRECURSOR_INTENSITY);
        if (!intensity.equalsIgnoreCase(NOT_A_NUMBER)) {
            spectrum.setIntensity(Double.parseDouble(intensity));
        }
        spectrum.setMzRatio(Double.valueOf(spectrumValues.get(MsmsHeader.M_Z)));
        spectrum.setRetentionTime(Double.valueOf(spectrumValues.get(MsmsHeader.RETENTION_TIME)));
        spectrum.setScanNumber(Long.valueOf(spectrumValues.get(MsmsHeader.SCAN_NUMBER)));
        spectrum.setScanIndex(Long.valueOf(spectrumValues.get(MsmsHeader.SCAN_INDEX)));
        //use the scan event number for the scan time field
        spectrum.setScanTime(Double.valueOf(spectrumValues.get(MsmsHeader.SCAN_EVENT_NUMBER)));

        //get the fragment ions and masses
        String ionMatches = spectrumValues.get(MsmsHeader.ION_MATCHES);
        String masses = spectrumValues.get(MsmsHeader.MASSES);

        return new AnnotatedSpectrum(spectrum, ionMatches, masses);
    }

}
