package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.distributed.io.maxquant.MaxQuantConstants;
import com.compomics.colims.model.SpectrumFile;
import com.compomics.colims.model.enums.FragmentationType;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Parser for the MaxQuant andromeda directory; the apl summary file, the actual
 * apl files containing the spectra and the spectrum parameter file (.apar).
 * <p/>
 *
 * @author Niels Hulstaert
 */
@Component("maxQuantAndromedaParser")
public class MaxQuantAndromedaParser {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MaxQuantAndromedaParser.class);

    private static final String ALL_SPECTRA = "allSpectra.";
    private static final String ANALYZER_TYPE_DELIMITER = "\\.";

    /**
     * The apl spectrum file paths map (key: apl file path; value: apl param
     * file path);
     */
    private final Map<Path, Path> aplFilePaths = new HashMap<>();
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
    private final MaxQuantAplParser maxQuantAplParser;

    @Autowired
    public MaxQuantAndromedaParser(MaxQuantAplParser maxQuantAplParser) {
        this.maxQuantAplParser = maxQuantAplParser;
    }

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
     * Get the apl file paths map (key: .apl file path; value: .apar file path)
     *
     * @return the apl files map
     */
    public Map<Path, Path> getAplFilePaths() {
        return aplFilePaths;
    }

    /**
     * Clear run data from parser.
     */
    public void clear() {
        aplFilePaths.clear();
    }

    /**
     * Parse the parameter related files of the andromeda directory. This method
     * parses the apl summary file and the .apar file.
     *
     * @param andromedaDirectory the MaxQuant andromeda directory
     * @throws IOException thrown in case of an I/O related problem
     */
    public void parseParameters(final Path andromedaDirectory) throws IOException {

        /**
         * Parse the apl summary file 'aplfiles.txt' to extract the apl spectrum
         * file paths, the spectrum parameter file paths and the mass analyzer
         * and fragmentation type.
         */
        if (!Files.exists(andromedaDirectory)) {
            throw new FileNotFoundException("The andromeda directory " + andromedaDirectory.toString() + " could not be found.");
        }

        Path aplSummaryPath = Paths.get(andromedaDirectory.toString(), MaxQuantConstants.APL_SUMMARY_FILE.value());
        if (!Files.exists(aplSummaryPath)) {
            throw new FileNotFoundException("The apl summary file " + MaxQuantConstants.APL_SUMMARY_FILE + " could not be found.");
        }
        Map<String, String> aplFilePaths = ParseUtils.parseParameters(aplSummaryPath, MaxQuantConstants.PARAM_TAB_DELIMITER.value());
        aplFilePaths.entrySet().stream().forEach(entry -> {
            //use paths relative to the andromeda directory
            Path relativeAplfilePath = Paths.get(andromedaDirectory.toString(), FilenameUtils.getName(entry.getKey()));
            Path relativeSpectrumParametersfilePath = Paths.get(andromedaDirectory.toString(), FilenameUtils.getName(entry.getValue()));
            this.aplFilePaths.put(relativeAplfilePath, relativeSpectrumParametersfilePath);
        });

        //get the first entry
        Optional<Map.Entry<Path, Path>> first = this.aplFilePaths.entrySet().stream().findFirst();
        if (first.isPresent()) {
            //parse the mass analyzer and fragmentation type
            parseMassAnalyzerAndFragmentationType(first.get().getKey());
        } else {
            fragmentationType = FragmentationType.UNKNOWN;
            massAnalyzerType = MaxQuantConstants.Analyzer.UNKNOWN;
        }
    }

    /**
     * Parse the spectrum files and map them onto {@link SpectrumFile}
     * instances. Parse also unidentified spectra if specified.
     *
     * @param rawFileRunName             the raw file name of the run
     * @param maxQuantSpectra            MaxQuantSpectra object
     * @param includeUnidentifiedSpectra whether or not to include the unidentified spectra
     * @throws java.io.IOException in case of an Input/Output related problem
     */
    public void parseSpectra(String rawFileRunName, MaxQuantSpectra maxQuantSpectra, boolean includeUnidentifiedSpectra) throws IOException {
        for (Path aplFilePath : aplFilePaths.keySet()) {
            if (!Files.exists(aplFilePath)) {
                throw new FileNotFoundException("The apl spectrum file " + aplFilePath.toString() + " could not be found.");
            }
            maxQuantAplParser.parseAplFile(aplFilePath, rawFileRunName, maxQuantSpectra, includeUnidentifiedSpectra);
        }
    }

    /**
     * Parse the mass analyzer and fragmentation type.
     *
     * @param aplFilesPath the aplFilePaths file path
     */
    private void parseMassAnalyzerAndFragmentationType(Path aplFilesPath) {
        fragmentationType = FragmentationType.UNKNOWN;
        massAnalyzerType = MaxQuantConstants.Analyzer.UNKNOWN;

        String aplFilePathString = aplFilesPath.toString();
        //   String analyzerAndType = aplFilePathString.substring(aplFilePathString.lastIndexOf(ALL_SPECTRA) + ALL_SPECTRA.length(), aplFilePathString.lastIndexOf(ISO));
        String analyzerAndType = org.apache.commons.lang3.StringUtils.substringAfter(aplFilePathString, ALL_SPECTRA);
        //get the fragmentation type
        String[] split = analyzerAndType.split(ANALYZER_TYPE_DELIMITER);
        if (split.length >= 2) {
            try {
                fragmentationType = FragmentationType.valueOf(split[0].toUpperCase());
                massAnalyzerType = MaxQuantConstants.Analyzer.valueOf(split[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

}
