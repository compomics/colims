package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.distributed.io.maxquant.MaxQuantConstants;
import com.compomics.colims.model.Spectrum;
import com.compomics.colims.model.SpectrumFile;
import com.compomics.colims.model.enums.FragmentationType;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
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

    private static final String APL_SPECTUM_START = "peaklist start";
    private static final String APL_SPECTUM_END = "peaklist end";
    private static final String APL_HEADER_DELIMITER = "=";
    private static final String APL_MZ = "mz";
    private static final String APL_HEADER = "header";
    private static final String APL_CHARGE = "charge";
    private static final String APL_FRAGMENTATION = "fragmentation";
    private static final String MGF_SPECTRUM_START = "BEGIN IONS";
    private static final String MGF_SPECTRUM_END = "END IONS";
    private static final String MGF_MZ = "PEPMASS=";
    private static final String MGF_RETENTION_TIME = "RTINSECONDS=";
    private static final String MGF_CHARGE = "CHARGE=";
    private static final String MGF_TITLE = "TITLE=";


    /**
     * Parse the give MaqQuant .apl spectrum file and update the spectra map.
     *
     * @param aplFilePath                the MaqQuant .apl spectrum file path
     * @param maxQuantSpectra            the spectra map
     * @param includeUnidentifiedSpectra whether or not to include unidentified spectra
     */
    public void parseAplFile(Path aplFilePath, MaxQuantSpectra maxQuantSpectra, boolean includeUnidentifiedSpectra) throws IOException {
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
                    Spectrum spectrum = null;
                    //check if the spectrum was identified and therefore can be found in the spectra map
                    if (maxQuantSpectra.getAplSpectra().containsKey(header)) {
                        spectrum = maxQuantSpectra.getAplSpectra().get(header);
                    } else if (spectrum == null && includeUnidentifiedSpectra && !maxQuantSpectra.getOmmittedSpectraKeys().contains(header)) {
                        //make new Spectrum instance and add it to the unidentified ones
                        spectrum = new Spectrum();
                        spectrum.setAccession(header);
                        spectrum.setMzRatio(Double.valueOf(headers.get(APL_MZ)));
                        spectrum.setFragmentationType(FragmentationType.valueOf(headers.get(APL_FRAGMENTATION)));
                        spectrum.setCharge(Integer.valueOf(headers.get(APL_CHARGE)));
                        spectrum.setScanNumber(org.apache.commons.lang3.StringUtils.substringAfter(header, "Index: "));
                        maxQuantSpectra.getUnidentifiedSpectra().add(spectrum);
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
