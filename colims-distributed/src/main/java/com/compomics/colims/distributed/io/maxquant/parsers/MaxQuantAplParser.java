package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.distributed.io.maxquant.MaxQuantConstants;
import com.compomics.colims.model.Spectrum;
import com.compomics.colims.model.SpectrumFile;
import com.compomics.colims.model.enums.FragmentationType;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

/**
 * Parser for the MaxQuant apl output files; the apl summary file and the actual
 * apl files containing the spectra.
 * <p/>
 *
 * @author Niels Hulstaert
 */
@Component("maxQuantAplParser")
public class MaxQuantAplParser {

    private static final String APL_SPECTRUM_START = "peaklist start";
    private static final String APL_SPECTRUM_END = "peaklist end";
    private static final String APL_HEADER_DELIMITER = "=";
    private static final String APL_MZ = "mz";
    private static final String APL_HEADER = "header";
    private static final String APL_CHARGE = "charge";
    private static final String APL_FRAGMENTATION = "fragmentation";
    private static final String APL_PRECURSOR = " Precursor";
    private static final String APL_INDEX = "Index: ";
    private static final String MGF_SPECTRUM_START = "BEGIN IONS";
    private static final String MGF_SPECTRUM_END = "END IONS";
    private static final String MGF_MZ = "PEPMASS=";
    private static final String MGF_RETENTION_TIME = "RTINSECONDS=";
    private static final String MGF_CHARGE = "CHARGE=";
    private static final String MGF_CHARGE_PLUS = "+";
    private static final String MGF_TITLE = "TITLE=";
    private static final String MGF_DELIMITER = " ";
    private static final String ENCODING = "UTF-8";
    private static final String HEADER_RAW_FILE = "RawFile: ";
    private static final String HEADER_INDEX = " Index: ";

    /**
     * Parse the give MaqQuant .apl spectrum file and update the spectra map.
     *
     * @param aplFilePath the MaqQuant .apl spectrum file path
     * @param maxQuantSpectra the spectra map
     * @param includeUnidentifiedSpectra whether or not to include unidentified
     * spectra
     */
    public void parseAplFile(Path aplFilePath, MaxQuantSpectra maxQuantSpectra, boolean includeUnidentifiedSpectra) throws IOException {
        try (BufferedReader bufferedReader = Files.newBufferedReader(aplFilePath)) {
            String line;

            Map<String, String> headers = new HashMap<>();
            while ((line = bufferedReader.readLine()) != null) {
                //look for a spectrum entry
                if (line.startsWith(APL_SPECTRUM_START)) {
                    //go to the next line
                    line = bufferedReader.readLine();
                    //parse spectrum header part
                    while (!Character.isDigit(line.charAt(0))) {
                        String[] split = line.split(APL_HEADER_DELIMITER);
                        headers.put(split[0], split[1]);
                        line = bufferedReader.readLine();
                    }
                    String completeHeader = headers.get(APL_HEADER);
                    //" Precursor: 0 _multi_" is removed before looking up the key in the spectra map
                    String header = org.apache.commons.lang3.StringUtils.substringBefore(headers.get(APL_HEADER), APL_PRECURSOR);
                    Spectrum spectrum = null;
                    //check if the spectrum was identified and therefore can be found in the spectra map
                    if (maxQuantSpectra.getSpectra().containsKey(header)) {
                        spectrum = maxQuantSpectra.getSpectra().get(header);
                    } else if (spectrum == null && includeUnidentifiedSpectra && !maxQuantSpectra.getOmittedSpectrumKeys().contains(header)) {
                        //make new Spectrum instance and add it to the unidentified ones
                        spectrum = new Spectrum();
                        spectrum.setAccession(header);
                        spectrum.setMzRatio(Double.valueOf(headers.get(APL_MZ)));
                        spectrum.setFragmentationType(FragmentationType.valueOf(headers.get(APL_FRAGMENTATION)));
                        spectrum.setCharge(Integer.valueOf(headers.get(APL_CHARGE)));
                        spectrum.setScanIndex(Long.valueOf(org.apache.commons.lang3.StringUtils.substringAfter(header, APL_INDEX)));

                        //get the RAW file name from the header
                        String rawFileName = org.apache.commons.lang3.StringUtils.substringBetween(header, HEADER_RAW_FILE, HEADER_INDEX);
                        if (!maxQuantSpectra.getUnidentifiedSpectra().containsKey(rawFileName)) {
                            List<Spectrum> spectra = new ArrayList<>();
                            spectra.add(spectrum);
                            maxQuantSpectra.getUnidentifiedSpectra().put(rawFileName, spectra);
                        } else {
                            maxQuantSpectra.getUnidentifiedSpectra().get(rawFileName).add(spectrum);
                        }
                    }

                    if (spectrum != null) {
                        //set the complete spectrum header as title
                        spectrum.setTitle(completeHeader);

                        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                OutputStreamWriter osw = new OutputStreamWriter(baos, Charset.forName(ENCODING).newEncoder());
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
                            bw.write(MGF_CHARGE + headers.get(APL_CHARGE) + MGF_CHARGE_PLUS);
                            while (!line.startsWith(APL_SPECTRUM_END)) {
                                bw.newLine();
                                bw.write(line.replace(MaxQuantConstants.PARAM_TAB_DELIMITER.value(), MGF_DELIMITER));
                                line = bufferedReader.readLine();
                            }
                            bw.newLine();
                            bw.write(MGF_SPECTRUM_END);
                            bw.flush();

                            //get the bytes from the stream
                            byte[] unzippedBytes = baos.toByteArray();

                            //gzip byte array
                            gzipos.write(unzippedBytes);
                            gzipos.flush();
                            gzipos.finish();

                            //create new SpectrumFile instance and set the content
                            SpectrumFile spectrumFile = new SpectrumFile();
                            spectrumFile.setContent(zbaos.toByteArray());
                            spectrumFile.setSpectrum(spectrum);

                            //set entity relation between Spectrum and SpectrumFile
                            spectrum.getSpectrumFiles().add(spectrumFile);
                        }
                    }
                    //clear headers map
                    headers.clear();
                }
            }
        }
    }
}
