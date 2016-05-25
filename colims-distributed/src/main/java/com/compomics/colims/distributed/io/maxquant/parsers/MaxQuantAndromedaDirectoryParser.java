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
import java.util.*;
import java.util.zip.GZIPOutputStream;

/**
 * Parser for the MaxQuant andromeda directory; the apl summary file, the actual apl files containing the spectra and
 * the spectrum parameter file (.apar).
 * <p/>
 *
 * @author Niels Hulstaert
 */
@Component("maxQuantAndromedaDirectoryParser")
public class MaxQuantAndromedaDirectoryParser {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(MaxQuantAndromedaDirectoryParser.class);

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
    private Map<String, String> aplFiles;
    /**
     * The spectrum parameters (key: parameter name; value: parameter value).
     */
    private Map<String, String> spectrumParameters;
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
     * Get the spectrum parameters parsed from the .apar file.
     *
     * @return the spectrum parameters map
     */
    public Map<String, String> getSpectrumParameters() {
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
        aplFiles = ParseUtils.parseParameters(aplSummaryFile, MaxQuantConstants.PARAM_TAB_DELIMITER.value());

        //get the first entry
        Optional<Map.Entry<String, String>> first = aplFiles.entrySet().stream().findFirst();
        if (first.isPresent()) {
            //parse the mass analyzer and fragmentation type
            parseMassAnalyzerAndFragmentationType(first.get().getKey());

            //parse the spectrum parameters
            String spectrumParametersFileName = FilenameUtils.getName(first.get().getValue());
            File spectrumParametersFile = new File(andromedaDirectory, spectrumParametersFileName);
            if (!spectrumParametersFile.exists()) {
                throw new FileNotFoundException("The spectrum parameters file " + spectrumParametersFileName + " could not be found.");
            }
            spectrumParameters = ParseUtils.parseParameters(spectrumParametersFile, MaxQuantConstants.PARAM_EQUALS_DELIMITER.value());
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
    private void parseAplFile(File aplFile, Map<String, Spectrum> spectra, boolean includeUnidentifiedSpectra) {
        try (BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(aplFile.toURI()))) {
            Map<String, Spectrum> unidentifiedSpectra = new HashMap<>();
            List<String> spectrumKeys = new ArrayList<>();
            spectra.forEach((k, v) -> spectrumKeys.add(k));
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
                    //" Precursor: 0 _multi_" is removed before looking up the key in the spectra map
                    String header = org.apache.commons.lang3.StringUtils.substringBefore(headers.get(APL_HEADER), " Precursor");
                    Spectrum spectrum = null;
                    //check if the spectrum was identified and therefore can be found in the spectra map
                    if (spectrumKeys.contains(header)) {
                        spectrum = spectra.get(header);
                    } else if (spectrum == null && includeUnidentifiedSpectra) {
                        //make new Spectrum instance
                        spectrum = new Spectrum();
                    }

                    //parse the spectrum
                    if (spectrum != null) {
                        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                             OutputStreamWriter osw = new OutputStreamWriter(baos, Charset.forName("UTF-8").newEncoder());
                             BufferedWriter bw = new BufferedWriter(osw);
                             ByteArrayOutputStream zbaos = new ByteArrayOutputStream();
                             GZIPOutputStream gzipos = new GZIPOutputStream(zbaos)) {

                            //write the spectrum in MGF format
                            bw.write(MGF_SPECTRUM_START);
                            bw.newLine();
                            bw.write(MGF_TITLE + headers.get(APL_HEADER));
                            if (!includeUnidentifiedSpectra) {
                                bw.newLine();
                                bw.write(MGF_RETENTION_TIME + spectrum.getRetentionTime());
                            }
                            bw.newLine();
                            bw.write(MGF_MZ + headers.get(APL_MZ));
                            bw.newLine();
                            bw.write(MGF_CHARGE + headers.get(APL_CHARGE) + "+");
                            while (!line.startsWith(APL_SPECTUM_END)) {
                                bw.newLine();
                                bw.write(line.replace(MaxQuantConstants.PARAM_TAB_DELIMITER.value(), " "));
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
                            spectra.put(header, spectrum);
                        } catch (IOException ex) {
                            LOGGER.error(ex);
                        }
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
