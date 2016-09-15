/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.distributed.playground;

import com.compomics.colims.distributed.io.maxquant.MaxQuantConstants;
import com.compomics.colims.distributed.io.maxquant.TabularFileLineValuesIterator2;
import com.compomics.colims.distributed.io.maxquant.headers.AbstractMaxQuantHeaders;
import com.compomics.colims.distributed.io.maxquant.headers.MaxQuantMSMSHeaders;
import com.compomics.colims.distributed.io.maxquant.parsers.MaxQuantAplParser;
import com.compomics.colims.distributed.io.maxquant.parsers.ParseUtils;
import com.compomics.util.experiment.massspectrometry.Peak;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Parse MSMS and APL files for given MSMS IDs and keep the peak points.
 *
 * @author demet
 */
@Component("annotatedSpectraParser")
public class AnnotatedSpectraParser {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(MaxQuantAplParser.class);
    /**
     * Spectrum peaks from APL files. key: APL key of the spectrum, value :list of peaks.
     */
    private Map<String, List<Peak>> spectrumPeaks = new HashMap<>();
    /**
     * Annotations from MSMS file
     * key:APL key of the spectrum, value map( key : Peak that keeps mass and intensity, value : match)
     */
    private Map<String, Map<Peak, String>> annotations = new HashMap<>();
    /**
     * List of aplKeys from MSMS file
     */
    private List<String> aplKeys = new ArrayList<>();
    /**
     * The apl spectrum file paths map (key: apl file path; value: apl param file path);
     */
    private Map<Path, Path> aplFilePaths = new HashMap<>();
    @Autowired
    private AbstractMaxQuantHeaders maxQuantHeaders;

    /**
     * The start of the spectrum header in the apl file.
     */
    private static final String KEY_START = "RawFile: ";
    private static final String KEY_MIDDLE = " Index: ";
    private static final String APL_SPECTUM_START = "peaklist start";
    private static final String APL_SPECTUM_END = "peaklist end";
    private static final String APL_HEADER_DELIMITER = "=";
    private static final String APL_HEADER = "header";

    /**
     * Parse spectra for given MSMS IDs.
     *
     * @param msmsFile
     * @param andromedaDirectory
     * @param msmsIDs
     * @throws IOException
     */
    public void parseSpectra(Path msmsFile, Path andromedaDirectory, List<String> msmsIDs) throws IOException {
        parseMSMS(msmsFile, msmsIDs);
        parseAplFilePaths(andromedaDirectory);
        parseAplFile();
    }

    /**
     * Parse msms file only for given ID numbers
     *
     * @param msmsFile
     * @param msmsIDs
     */
    private void parseMSMS(Path msmsFile, List<String> msmsIDs) throws IOException {
        TabularFileLineValuesIterator2 valuesIterator = new TabularFileLineValuesIterator2(msmsFile, maxQuantHeaders.getMandatoryHeaders(AbstractMaxQuantHeaders.MaxQuantFile.MSMS));
        for (Map<String, String> spectrumValues : valuesIterator) {
            if (msmsIDs.contains(spectrumValues.get(MaxQuantMSMSHeaders.ID.getValue()))) {
                //concatenate the RAW file name and scan index
                String aplKey = KEY_START + spectrumValues.get(MaxQuantMSMSHeaders.RAW_FILE.getValue())
                        + KEY_MIDDLE
                        + spectrumValues.get(MaxQuantMSMSHeaders.SCAN_NUMBER.getValue());

                //map the spectrum
                if (!aplKeys.contains(aplKey)) {
                    Map<Peak, String> annotatedPeakList = parsePeakList(spectrumValues.get(MaxQuantMSMSHeaders.MATCHES.getValue()),
                            spectrumValues.get(MaxQuantMSMSHeaders.INTENSITIES.getValue()),
                            spectrumValues.get(MaxQuantMSMSHeaders.MASSES.getValue()));
                    annotations.put(aplKey, annotatedPeakList);
                    aplKeys.add(aplKey);

                }
            }
        }
    }

    /**
     * Parse peakList
     *
     * @param matches
     * @param intensities
     * @param masses
     */
    private Map<Peak, String> parsePeakList(String matches, String intensities, String masses) {
        Map<Peak, String> annotatedPeakList = new HashMap<>();
        if (!matches.isEmpty() && !intensities.isEmpty() && !masses.isEmpty()) {
            String[] matchList = matches.split(";");
            String[] intensityList = intensities.split(";");
            String[] massList = masses.split(";");

            if (intensityList.length != matchList.length || massList.length != matchList.length) {
                throw new IllegalArgumentException("Input lists are not equal length");
            }
            for (int i = 0; i < matchList.length; i++) {
                int charge = 1;
                Double mass = Double.parseDouble(massList[i]) / charge;
                Peak peak = new Peak(mass, Double.parseDouble(intensityList[i]));

                annotatedPeakList.put(peak, matchList[i]);
            }

        }
        return annotatedPeakList;
    }

    /**
     * Parse APL File Paths. Put all the apl files to be used in aplFilePaths list.
     *
     * @param andromedaDirectory
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void parseAplFilePaths(Path andromedaDirectory) throws FileNotFoundException, IOException {
        /**
         * Parse the apl summary file 'aplfiles.txt' to extract the apl spectrum file paths, the spectrum parameter file paths
         * and the mass analyzer and fragmentation type.
         */
        if (!Files.exists(andromedaDirectory)) {
            throw new FileNotFoundException("The andromeda directory " + andromedaDirectory.toString() + " could not be found.");
        }

        Path aplSummaryPath = Paths.get(andromedaDirectory.toString(), MaxQuantConstants.APL_SUMMARY_FILE.value());
        if (!Files.exists(aplSummaryPath)) {
            throw new FileNotFoundException("The apl summary file " + MaxQuantConstants.APL_SUMMARY_FILE + " could not be found.");
        }
        Map<String, String> allAplFilePaths = ParseUtils.parseParameters(aplSummaryPath, MaxQuantConstants.PARAM_TAB_DELIMITER.value());
        allAplFilePaths.entrySet().stream().forEach(entry -> {
            //use paths relative to the andromeda directory
            Path relativeAplfilePath = Paths.get(andromedaDirectory.toString(), FilenameUtils.getName(entry.getKey()));
            Path relativeSpectrumParametersfilePath = Paths.get(andromedaDirectory.toString(), FilenameUtils.getName(entry.getValue()));
            this.aplFilePaths.put(relativeAplfilePath, relativeSpectrumParametersfilePath);
        });

    }


    /**
     * Parse the APL files for given aplKeys and put the peaks in the spectrumPeaks list
     */
    private void parseAplFile() throws IOException {
        for (Path aplFilePath : aplFilePaths.keySet()) {
            if (!Files.exists(aplFilePath)) {
                throw new FileNotFoundException("The apl spectrum file " + aplFilePath.toString() + " could not be found.");
            }
            try (BufferedReader bufferedReader = Files.newBufferedReader(aplFilePath)) {
                String line;
                Map<String, String> headers = new HashMap<>();

                while ((line = bufferedReader.readLine()) != null) {
                    //look for a spectrum entry
                    if (line.startsWith(APL_SPECTUM_START)) {
                        //go to the next line
                        line = bufferedReader.readLine();
                        //parse spectrum header part
                        while (!Character.isDigit(line.charAt(0))) {
                            String[] split = line.split(APL_HEADER_DELIMITER);
                            headers.put(split[0], split[1]);
                            line = bufferedReader.readLine();
                        }
                        //" Precursor: 0 _multi_" is removed before looking up the key in the spectra map
                        String header = org.apache.commons.lang3.StringUtils.substringBefore(headers.get(APL_HEADER), " Precursor");
                        //check if the spectrum was identified and therefore can be found in the spectra map
                        if (aplKeys.contains(header)) {
                            List<Peak> peakList = new ArrayList<>();
                            while (!line.startsWith(APL_SPECTUM_END)) {
                                String[] splitLine = line.split(MaxQuantConstants.PARAM_TAB_DELIMITER.value());
                                Peak peak = new Peak(Double.parseDouble(splitLine[0]), Double.parseDouble(splitLine[1]));

                                peakList.add(peak);
                                line = bufferedReader.readLine();
                            }
                            spectrumPeaks.put(header, peakList);
                        }
                        //clear headers map
                        headers.clear();
                    }
                }
            }
        }
    }
}
