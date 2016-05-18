package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.distributed.io.maxquant.MaxQuantConstants;
import com.compomics.colims.model.Spectrum;
import com.compomics.colims.model.SpectrumFile;
import com.compomics.colims.model.enums.FragmentationType;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.zip.GZIPOutputStream;

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
    private static final String APL_HEADER_DELIMITER = "=";
    private static final String APL_MZ = "mz";
    private static final String APL_HEADER = "header";
    private static final String MGF_SPECTRUM_START = "BEGIN IONS";
    private static final String MGF_SPECTRUM_END = "END IONS";
    private static final String MGF_MZ = "PEPMASS=";
    private static final String MGF_RETENTION_TIME = "RTINSECONDS=";
    private static final String MGF_CHARGE = "CHARGE=";

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

        aplFiles = ParseUtils.parseParameters(aplSummaryFile, MaxQuantConstants.PARAM_DELIMITER.value());
        //get the mass analyzer and type
        Optional<String> first = aplFiles.keySet().stream().findFirst();
        if (first.isPresent()) {
            parseMassAnalyzerAndFragmentationType(first.get());
        } else {
            fragmentationType = FragmentationType.UNKNOWN;
            massAnalyzerType = MaxQuantConstants.Analyzer.UNKNOWN;
        }
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
            //get the apl file by the parent directory
            File aplfile = new File(andromedaDirectory, FilenameUtils.getName(aplFilePath));
            if (!aplfile.exists()) {
                throw new FileNotFoundException("The apl spectrum file " + aplFilePath + " could not be found.");
            }
            parseAplFile(aplfile, spectra, includeUnidentifiedSpectra);
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

    /**
     * Parse the give MaqQuant .apl spectrum file and update the spectra map.
     *
     * @param aplFile                    the MaqQuant .apl spectrum file
     * @param spectra                    the spectra map
     * @param includeUnidentifiedSpectra whether or not to include unidentified spectra
     */
    private void parseAplFile(File aplFile, Map<SpectrumKey, Spectrum> spectra, boolean includeUnidentifiedSpectra) {
        try (BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(aplFile.toURI()))) {
            Map<SpectrumKey, Spectrum> unidentifiedSpectra = new HashMap<>();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                //look for a spectrum entry
                if (line.startsWith(APL_SPECTUM_START)) {
                    //go to the next line
                    line = bufferedReader.readLine();
                    //parse spectrum header part
                    Map<String, String> headers = new HashMap<>();
                    while (!Character.isDigit(line.charAt(0))) {
                        String[] split = line.split(APL_HEADER_DELIMITER);
                        headers.put(split[0], split[1]);
                        line = bufferedReader.readLine();
                    }

                    //@// TODO: 18/05/16  remove the " Precursor: 0 _multi_" before looking up the key in the spectra map

                    Spectrum spectrum = null;
                    //check if the spectrum was identified and therefore can be found in the spectra map
                    if (spectra.containsKey(headers.get(APL_HEADER))) {
                        spectrum = spectra.get(headers.get(APL_HEADER));
                    } else if (spectrum == null && includeUnidentifiedSpectra) {
                        //make new Spectrum instance
                        spectrum = new Spectrum();
                    }

                    //parse the spectrum
                    try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                         OutputStreamWriter osw = new OutputStreamWriter(baos, Charset.forName("UTF-8").newEncoder());
                         BufferedWriter bw = new BufferedWriter(osw);
                         ByteArrayOutputStream zbaos = new ByteArrayOutputStream();
                         GZIPOutputStream gzipos = new GZIPOutputStream(zbaos)) {

                        //@// TODO: 18/05/16 check the MGF headers
                        /**
                         * BEGIN IONS
                         TITLE=qExactive01819.4.4. File:"qExactive01819.raw", NativeID:"controllerType=0 controllerNumber=1 scan=4"
                         RTINSECONDS=1.09895904
                         PEPMASS=429.09 577030.375
                         113.6539841 459.7995300293
                         122.5920792 689.1290283203
                         143.8116455 536.5052490234
                         149.0437012 5911.935546875
                         158.0802307 634.6851806641
                         159.1360321 1011.9250488281
                         167.0540314 16568.486328125
                         172.9756622 746.4285888672
                         173.8959961 651.8788452148
                         203.1263275 912.4348144531
                         207.9746094 1486.9996337891
                         208.951416 57437.015625
                         209.9543762 787.7396240234
                         224.9748535 733.3963012695
                         225.0398254 3082.6596679688
                         226.9612579 939.0350341797
                         242.9414215 1557.6287841797
                         266.4727478 974.8828735352
                         END IONS
                         */

                        //write the spectrum in MGF format
                        bw.write(MGF_SPECTRUM_START);
                        while (!line.startsWith(APL_SPECTUM_END)) {
                            bw.newLine();
                            bw.write(line.replace(MaxQuantConstants.PARAM_DELIMITER.value(), " "));
                            line = bufferedReader.readLine();
                        }
                        bw.write(MGF_SPECTRUM_END);
                        bw.flush();

                        //get the bytes from the stream
                        byte[] unzippedBytes = baos.toByteArray();

                        //gzip byte array
                        gzipos.write(unzippedBytes);
                        gzipos.flush();
                        gzipos.finish();

                        SpectrumFile spectrumFile = new SpectrumFile();
                        //set content of the SpectrumFile
                        spectrumFile.setContent(zbaos.toByteArray());

                        //set entity relation between Spectrum and SpectrumFile
                        spectrum.getSpectrumFiles().add(spectrumFile);
                    } catch (IOException ex) {
                        LOGGER.error(ex);
                    }

                    //clear maps
                    headers.clear();
                    unidentifiedSpectra.clear();
                }
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}
