package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.distributed.io.maxquant.MaxQuantConstants;
import com.compomics.colims.model.Spectrum;
import com.compomics.colims.model.enums.FragmentationType;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
    private static final String APL_SPECTUM_START = "peaklist start";
    private static final String APL_SPECTUM_END = "peaklist end";

    /**
     * The MaxQuant andromeda directory.
     */
    private File andromedaDirectory;
    /**
     * The apl spectrum files map (key: apl file name; value: apl param file name);
     */
    private Map<String, String> aplFiles;
    /**
     * The fragmentation type used.
     */
    private FragmentationType fragmentationType;
    /**
     * The mass analyzer type.
     */
    private MaxQuantConstants.Analyzer massAnalyzerType;

    public FragmentationType getFragmentationType() {
        return fragmentationType;
    }

    /**
     * Initialize the parser. This method parses the apl summary file.
     *
     * @param andromedaDirectory the MaxQuant andromeda directory
     * @throws IOException thrown in case of an I/O related problem
     */
    public void init(final File andromedaDirectory) throws IOException {

        /**
         * Parse the apl summary file 'aplfiles.txt' to extract the location of the apl spectrum files
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

        aplFiles = ParseUtils.parseParameters(aplSummaryFile, false);
        //get the mass analyzer and type
        Optional<String> first = aplFiles.keySet().stream().findFirst();
        if (first.isPresent()) {
            parseMassAnalyzerAndFragmentationType(first.get());
        }
        System.out.println("");
    }

    /**
     * Parse the spectrum files and map them onto {@link com.compomics.colims.model.SpectrumFile} instances. Parse also
     * unidentified spectra if specified.
     *
     * @param spectra                    the map of spectra (key: SpectrumKey object for linking purposes; value: the
     *                                   Colims Spectrum instance)
     * @param includeUnidentifiedSpectra whether or not to include the unidentified spectra
     */
    public void parse(Map<SpectrumKey, Spectrum> spectra, boolean includeUnidentifiedSpectra) throws FileNotFoundException {
        for (String aplFilePath : aplFiles.keySet()) {
            File aplfile = new File(andromedaDirectory, aplFilePath);
            if (!aplfile.exists()) {
                throw new FileNotFoundException("The apl spectrum file " + aplFilePath + " could not be found.");
            }
            parseAplFile(aplfile, spectra, false);

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
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    private void parseAplFile(File aplFile, Map<SpectrumKey, Spectrum> spectra, boolean includeUnidentifiedSpectra) {
        try (BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(aplFile.toURI()))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.equals(APL_SPECTUM_START)) {
                    //
                    String mzValue = bufferedReader.readLine();

                    String charge;
                    //store m/z value and charge in case it's an unidentified spectrum
                    //and these need to be stored as well
                    if (includeUnidentifiedSpectra) {

                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}
