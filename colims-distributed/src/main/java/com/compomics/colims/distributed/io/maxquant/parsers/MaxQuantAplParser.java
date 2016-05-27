package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.distributed.io.maxquant.MaxQuantConstants;
import com.compomics.colims.model.Spectrum;
import com.compomics.colims.model.SpectrumFile;
import org.apache.log4j.Logger;
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
    private static final String MGF_SPECTRUM_START = "BEGIN IONS";
    private static final String MGF_SPECTRUM_END = "END IONS";
    private static final String MGF_MZ = "PEPMASS=";
    private static final String MGF_RETENTION_TIME = "RTINSECONDS=";
    private static final String MGF_CHARGE = "CHARGE=";
    private static final String MGF_TITLE = "TITLE=";

    /**
     * The list of unidentified spectra.
     */
    private List<Spectrum> unidentifiedSpectra = new ArrayList<>();

    /**
     * Get the list of unidentified spectra.
     *
     * @return the unidentified spectra
     */
    public List<Spectrum> getUnidentifiedSpectra() {
        return unidentifiedSpectra;
    }

    /**
     * Parse the give MaqQuant .apl spectrum file and update the spectra map.
     *
     * @param aplFilePath                the MaqQuant .apl spectrum file path
     * @param spectra                    the spectra map
     * @param includeUnidentifiedSpectra whether or not to include unidentified spectra
     */
    public void parseAplFile(Path aplFilePath, Map<String, Spectrum> spectra, boolean includeUnidentifiedSpectra) throws IOException {
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
                    if (spectra.containsKey(header)) {
                        spectrum = spectra.get(header);
                    } else if (spectrum == null && includeUnidentifiedSpectra) {
                        //make new Spectrum instance and add it to the unidentified ones
                        spectrum = new Spectrum();
                        unidentifiedSpectra.add(spectrum);
                    }

                    //parse the spectrum
                    if (spectrum != null) {
                        // TODO: 27/05/16 for an unidentief spectrum, fill in as much fields as possible in the Spectrum instance 
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

                            //create new SpectrumFile instance and set the content
                            SpectrumFile spectrumFile = new SpectrumFile();
                            spectrumFile.setContent(zbaos.toByteArray());

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

    /**
     * Clear the resources of the parser.
     */
    public void clear() {
        unidentifiedSpectra.clear();
    }

}
