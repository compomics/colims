package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.distributed.io.maxquant.MaxQuantConstants;
import com.compomics.colims.distributed.io.maxquant.headers.MaxQuantSpectrumParameterHeaders;
import com.compomics.colims.model.Spectrum;
import com.compomics.colims.model.SpectrumFile;
import com.compomics.colims.model.enums.FragmentationType;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.GZIPOutputStream;

/**
 * Parser for the MaxQuant andromeda directory; the apl summary file, the actual apl files containing the spectra and
 * the spectrum parameter file (.apar).
 * <p/>
 *
 * @author Niels Hulstaert
 */
@Component("maxQuantAndromedaParser")
public class MaxQuantAndromedaParser {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(MaxQuantAndromedaParser.class);

    private static final String ALL_SPECTRA = "allSpectra.";
    private static final String ISO = ".iso";
    private static final String ANALYZER_TYPE_DELIMITER = "\\.";
    private static final String APL_SPECTUM_START = "peaklist start";
    private static final String APL_SPECTUM_END = "peaklist end";
    private static final String APL_HEADER_DELIMITER = "=";
    private static final String APL_MZ = "mz";
    private static final String APL_HEADER = "header";
    private static final String APL_CHARGE = "charge";
    private static final String MGF_SPECTRUM_START = "BEGIN IONS";
    private static final String MGF_SPECTRUM_END = "END IONS";
    private static final String MGF_MZ = "PEPMASS=";
    private static final String MGF_RETENTION_TIME = "RTINSECONDS=";
    private static final String MGF_CHARGE = "CHARGE=";
    private static final String MGF_TITLE = "TITLE=";

    /**
     * The MaxQuant andromeda directory.
     */
    private File andromedaDirectory;
    /**
     * The apl spectrum files map (key: apl file name; value: apl param file name);
     */
    private Map<String, String> aplFiles = new HashMap<>();
    /**
     * The spectrum parameters (key: parameter enum; value: parameter value).
     */
    private EnumMap<MaxQuantSpectrumParameterHeaders, String> spectrumParameters = new EnumMap<>(MaxQuantSpectrumParameterHeaders.class);
    /**
     * The fragmentation type used.
     */
    private FragmentationType fragmentationType;
    /**
     * The mass analyzer type.
     */
    private MaxQuantConstants.Analyzer massAnalyzerType;
    /**
     * The .apl spectrum parser.
     */
    @Autowired
    private MaxQuantAplParser maxQuantAplParser;

    /**
     * Get the fragmentation type.
     *
     * @return the fragmentation type
     */
    public FragmentationType getFragmentationType() {
        return fragmentationType;
    }

    /**
     * Get the mass analyzer type
     *
     * @return the mass analyzer type
     */
    public MaxQuantConstants.Analyzer getMassAnalyzerType() {
        return massAnalyzerType;
    }

    /**
     * Get the apl files map (key: .apl file path; value: .apar file path)
     *
     * @return the apl files map
     */
    public Map<String, String> getAplFiles() {
        return aplFiles;
    }

    /**
     * Get the spectrum parameters parsed from the .apar file.
     *
     * @return the spectrum parameters map
     */
    public EnumMap<MaxQuantSpectrumParameterHeaders, String> getSpectrumParameters() {
        return spectrumParameters;
    }

    /**
     * Parse the parameter related files of the andromeda directory. This method parses the apl summary file and the
     * .apar file.
     *
     * @param andromedaDirectory the MaxQuant andromeda directory
     * @throws IOException thrown in case of an I/O related problem
     */
    public void parseParameters(final File andromedaDirectory) throws IOException {
        /**
         * Parse the apl summary file 'aplfiles.txt' to extract the apl spectrum file paths, the spectrum parameter file paths
         * and the mass analyzer and fragmentation type.
         */
        if (!andromedaDirectory.exists()) {
            throw new FileNotFoundException("The andromeda directory " + andromedaDirectory.getPath() + " could not be found.");
        }
        this.andromedaDirectory = andromedaDirectory;

        File aplSummaryFile = new File(andromedaDirectory, MaxQuantConstants.APL_SUMMARY_FILE.value());
        if (!aplSummaryFile.exists()) {
            throw new FileNotFoundException("The apl summary file " + MaxQuantConstants.APL_SUMMARY_FILE + " could not be found.");
        }
        aplFiles = ParseUtils.parseParameters(aplSummaryFile, MaxQuantConstants.PARAM_TAB_DELIMITER.value());

        //get the first entry
        Optional<Map.Entry<String, String>> first = aplFiles.entrySet().stream().findFirst();
        if (first.isPresent()) {
            //parse the mass analyzer and fragmentation type
            parseMassAnalyzerAndFragmentationType(first.get().getKey());

            //parse the spectrum parameters
            parseSpectrumParameters(first.get().getValue());
        } else {
            fragmentationType = FragmentationType.UNKNOWN;
            massAnalyzerType = MaxQuantConstants.Analyzer.UNKNOWN;
        }
    }

    /**
     * Parse the spectrum files and map them onto {@link SpectrumFile} instances. Parse also unidentified spectra if
     * specified.
     *
     * @param spectra                    the map of spectra (key: String apl header for linking purposes; value: the
     *                                   Colims Spectrum instance)
     * @param includeUnidentifiedSpectra whether or not to include the unidentified spectra
     */
    public void parseSpectra(Map<String, Spectrum> spectra, boolean includeUnidentifiedSpectra) throws FileNotFoundException {
        for (String aplFilePath : aplFiles.keySet()) {
            //get the apl file by the parent directory
            File aplfile = new File(andromedaDirectory, FilenameUtils.getName(aplFilePath));
            if (!aplfile.exists()) {
                throw new FileNotFoundException("The apl spectrum file " + aplFilePath + " could not be found.");
            }
            maxQuantAplParser.parseAplFile(aplfile, spectra, includeUnidentifiedSpectra);
        }
    }

    /**
     * Clear the resources used by the parser.
     */
    public void clear() {

    }

    /**
     * Parse the mass analyzer and fragmentation type.
     *
     * @param aplFilesPath the aplFiles file path
     */
    private void parseMassAnalyzerAndFragmentationType(String aplFilesPath) {
        fragmentationType = FragmentationType.UNKNOWN;
        massAnalyzerType = MaxQuantConstants.Analyzer.UNKNOWN;

        String analyzerAndType = aplFilesPath.substring(aplFilesPath.lastIndexOf(ALL_SPECTRA) + ALL_SPECTRA.length(), aplFilesPath.lastIndexOf(ISO));
        //get the fragmentation type
        String[] split = analyzerAndType.split(ANALYZER_TYPE_DELIMITER);
        if (split.length == 2) {
            try {
                fragmentationType = FragmentationType.valueOf(split[0].toUpperCase());
                massAnalyzerType = MaxQuantConstants.Analyzer.valueOf(split[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Parse the spectrum parameters.
     *
     * @param spectrumParametersFilePath the spectrum parameters file path
     */
    private void parseSpectrumParameters(String spectrumParametersFilePath) throws IOException {
        String spectrumParametersFileName = FilenameUtils.getName(spectrumParametersFilePath);
        File spectrumParametersFile = new File(andromedaDirectory, spectrumParametersFileName);
        if (!spectrumParametersFile.exists()) {
            throw new FileNotFoundException("The spectrum parameters file " + spectrumParametersFileName + " could not be found.");
        }

        //parse the parameters
        Map<String, String> spectrumStringParameters = ParseUtils.parseParameters(spectrumParametersFile, MaxQuantConstants.PARAM_EQUALS_DELIMITER.value(), true);
        //put them in an EnumMap
        for (MaxQuantSpectrumParameterHeaders spectrumParameterHeader : MaxQuantSpectrumParameterHeaders.values()) {
            Optional<String> header = spectrumParameterHeader.getPossibleValues()
                    .stream()
                    .filter(spectrumStringParameters::containsKey)
                    .findFirst();

            if (header.isPresent()) {
                spectrumParameterHeader.setParsedValue(spectrumParameterHeader.getPossibleValues().indexOf(header.get()));
                spectrumParameters.put(spectrumParameterHeader, spectrumStringParameters.get(header.get()));
            }
        }
    }

}
