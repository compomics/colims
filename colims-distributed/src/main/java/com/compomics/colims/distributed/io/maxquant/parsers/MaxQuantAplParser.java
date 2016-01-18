package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.distributed.io.maxquant.MaxQuantConstants;
import com.compomics.colims.model.enums.FragmentationType;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

/**
 * Parser for the MaxQuant apl output files; the apl summary file and the actual apl files containing the spectra.
 * <p/>
 *
 * @author Niels Hulstaert
 */
@Component("maxQuantAplParser")
public class MaxQuantAplParser {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(MaxQuantAplParser.class);

    private static final String ALL_SPECTRA = "allSpectra.";
    private static final String ISO = ".iso";
    private static final String ANALYZER_TYPE_DELIMITER = "\\.";

    /**
     * The fragmentation type used.
     */
    private FragmentationType fragmentationType;
    /**
     * The mass analyzer type;
     */
    private MaxQuantConstants.Analyzer massAnalyzerType;

    public void parse(final File andromedaDirectory) throws IOException {
        //parse the apl summary file 'aplfiles'
        File aplSummaryFile = new File(andromedaDirectory, MaxQuantConstants.APL_SUMMARY_FILE.value());
        if (!aplSummaryFile.exists()) {
            throw new FileNotFoundException("The apl summary file " + MaxQuantConstants.APL_SUMMARY_FILE + " could not be found.");
        }

        final Map<String, String> aplFiles = ParseUtils.parseParameters(aplSummaryFile, false);
        //get the mass analyzer and type
        Optional<String> first = aplFiles.keySet().stream().findFirst();
        if (first.isPresent()) {
            parseMassAnalyzerAndFragmentationType(first.get());
        }


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
                //do nothing
            }
        }
    }

}
