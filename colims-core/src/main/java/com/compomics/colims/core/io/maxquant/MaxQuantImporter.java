package com.compomics.colims.core.io.maxquant;

import com.compomics.colims.core.io.*;
import com.compomics.colims.core.io.maxquant.headers.HeaderEnumNotInitialisedException;
import com.compomics.colims.core.io.utilities_to_colims.UtilitiesSpectrumMapper;
import com.compomics.colims.core.util.ResourceUtils;
import com.compomics.colims.model.*;
import com.compomics.colims.model.enums.QuantificationEngineType;
import com.compomics.colims.model.enums.SearchEngineType;
import com.compomics.util.experiment.identification.SequenceFactory;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.SpectrumFactory;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The DataImporter class for MaxQuant projects.
 *
 * @author Davy
 */
@Component("maxQuantImporter")
public class MaxQuantImporter implements DataImporter {

    private static final Logger LOGGER = Logger.getLogger(MaxQuantImporter.class);
    private MaxQuantImport maxQuantImport;
    @Autowired
    private SearchSettingsMapper searchSettingsMapper;
    @Autowired
    private UtilitiesSpectrumMapper utilitiesSpectrumMapper;
    @Autowired
    private MaxQuantParameterParser parameterParser;
    @Autowired
    private MaxQuantParser maxQuantParser;
    @Autowired
    private MaxQuantUtilitiesAnalyticalRunMapper maxQuantUtilitiesAnalyticalRunMapper;
    @Autowired
    private MaxQuantUtilitiesPsmMapper maxQuantUtilitiesPsmMapper;
    @Autowired
    private QuantificationSettingsMapper quantificationSettingsMapper;

    private SpectrumFactory spectrumFactory = SpectrumFactory.getInstance();
    /**
     * Compomics utilities sequence factory
     */
    private SequenceFactory sequenceFactory = SequenceFactory.getInstance();
    /**
     * The map of new proteins (key: protein accession, value: the protein)
     */
    private Map<String, Protein> newProteins = new HashMap<>();

    @Override
    public void initImport(DataImport dataImport) {
        if (dataImport instanceof MaxQuantImport) {
            this.maxQuantImport = (MaxQuantImport) dataImport;
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void clear() {
        try {
            spectrumFactory.clearFactory();
            sequenceFactory.clearFactory();
            newProteins.clear();
            parameterParser.clear();
        } catch (IOException | SQLException ex) {
            LOGGER.error(ex);
        }
    }

    @Override
    public SearchAndValidationSettings importSearchSettings() throws MappingException {
        SearchAndValidationSettings searchAndValidationSettings = null;

        try {
            parameterParser.parse(maxQuantImport.getMaxQuantDirectory());

            List<File> identificationFiles = new ArrayList<>();
            identificationFiles.add(maxQuantImport.getMaxQuantDirectory());

            // TODO: settings for multiple runs
            searchAndValidationSettings = searchSettingsMapper.map(SearchEngineType.MAX_QUANT, parameterParser.getMaxQuantVersion(), maxQuantImport.getFastaDb(), parameterParser.getRunParameters().values().iterator().next(), identificationFiles, false);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (HeaderEnumNotInitialisedException e) {
            e.printStackTrace();
        }

        return searchAndValidationSettings;
    }

    @Override
    public QuantificationSettings importQuantSettings() throws MappingException {
        QuantificationSettings quantificationSettings = null;

        List<File> quantFiles = new ArrayList<>();
        quantFiles.add(new File(maxQuantImport.getMaxQuantDirectory(), "msms.txt"));  // TODO: make a constant also is this the right file?
        QuantificationParameters params = new QuantificationParameters();

        try {
            quantificationSettings = quantificationSettingsMapper.map(QuantificationEngineType.MAX_QUANT, parameterParser.getMaxQuantVersion(), quantFiles, params);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return quantificationSettings;
    }

    @Override
    public List<AnalyticalRun> importInputAndResults(SearchAndValidationSettings searchAndValidationSettings, QuantificationSettings quantificationSettings) throws MappingException {
        LOGGER.info("started mapping folder: " + maxQuantImport.getMaxQuantDirectory().getName());
        List<AnalyticalRun> mappedRuns = new ArrayList<>();
        File preparedFastaFile = null;

        try {
            //just in case
            maxQuantParser.clearParsedProject();
            preparedFastaFile = prepareFasta(maxQuantImport.getFastaDb().getFilePath());
            LOGGER.debug("Start loading FASTA file.");
            sequenceFactory.loadFastaFile(preparedFastaFile, null);
            LOGGER.debug("Finish loading FASTA file.");

            maxQuantParser.parseFolder(maxQuantImport.getMaxQuantDirectory(), parameterParser.getMultiplicity());

            for (MaxQuantAnalyticalRun aParsedRun : maxQuantParser.getRuns()) {
                AnalyticalRun targetRun = new AnalyticalRun();

                maxQuantUtilitiesAnalyticalRunMapper.map(aParsedRun, targetRun);

                List<Spectrum> mappedSpectra = new ArrayList<>(aParsedRun.getListOfSpectra().size());

                for (Map.Entry<Integer, MSnSpectrum> aParsedSpectrum : aParsedRun.getListOfSpectra().entrySet()) {
                    Spectrum targetSpectrum = new Spectrum();

                    //set entity relation
                    targetSpectrum.setAnalyticalRun(targetRun);

                    //for the spectra we can just use the standard utilities mapper
                    utilitiesSpectrumMapper.map(aParsedSpectrum.getValue(), maxQuantParser.getFragmentationType(aParsedSpectrum.getKey()), targetSpectrum);
                    mappedSpectra.add(targetSpectrum);

                    maxQuantUtilitiesPsmMapper.map(aParsedSpectrum.getValue(), maxQuantParser, targetSpectrum);
                }
                targetRun.setSpectrums(mappedSpectra);
                mappedRuns.add(targetRun);
            }

        } catch (IOException | ClassNotFoundException | HeaderEnumNotInitialisedException | UnparseableException | MappingException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new MappingException("there was a problem storing your max quant data, underlying exception: ", ex);
        } finally {
            if (preparedFastaFile != null) {
                preparedFastaFile.delete();
            }
        }
        return mappedRuns;
    }

    private File prepareFasta(String filePath) throws IOException {
        File originalFile = new File(filePath);
        File preparedFile = new File(System.getProperty("java.io.tmpdir") + "/maxquantspikedfastas", originalFile.getName());
        if (!preparedFile.getParentFile().exists()) {
            preparedFile.getParentFile().mkdir();
        }
        if (preparedFile.exists()) {
            preparedFile.delete();
        }
        String line;
        try (FileOutputStream fos = new FileOutputStream(preparedFile);
                OutputStreamWriter osw = new OutputStreamWriter(fos, Charset.forName("UTF-8").newEncoder());
                BufferedWriter bw = new BufferedWriter(osw);
                PrintWriter pw = new PrintWriter(bw)) {
            try (FileInputStream fis = new FileInputStream((originalFile));
                    InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8").newDecoder());
                    LineNumberReader originalReader = new LineNumberReader(isr)) {
                while ((line = originalReader.readLine()) != null) {
                    pw.println(line);
                }
            }
            try (InputStream fileStream = ResourceUtils.getResourceByRelativePath("config/maxquant/contaminants.fasta").getInputStream();
                    InputStreamReader isr = new InputStreamReader(fileStream, Charset.forName("UTF-8").newDecoder());
                    LineNumberReader contaminantsReader = new LineNumberReader(isr)) {
                while ((line = contaminantsReader.readLine()) != null) {
                    pw.println(line);
                }
            }
            pw.flush();
        }
        File finalFile = new File(preparedFile.getParentFile(), preparedFile.getName() + "_spiked.fasta");
        if (finalFile.exists()) {
            finalFile.delete();
        }
        try (FileOutputStream fos = new FileOutputStream(finalFile);
                OutputStreamWriter osw = new OutputStreamWriter(fos, Charset.forName("UTF-8").newEncoder());
                BufferedWriter finalWriter = new BufferedWriter(osw)) {
            StringBuilder normalBuffer = new StringBuilder();
            StringBuilder reverseBuffer = new StringBuilder();
            try (FileInputStream fis = new FileInputStream(preparedFile);
                    InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8").newDecoder());
                    LineNumberReader preparedFileReader = new LineNumberReader(isr)) {
                while ((line = preparedFileReader.readLine()) != null) {
                    if (line.contains(">")) {
                        finalWriter.write(normalBuffer.toString());
                        finalWriter.write(reverseBuffer.toString());
                        normalBuffer = new StringBuilder();
                        reverseBuffer = new StringBuilder();
                        if (line.contains("CON__")) {
                            line = line.replace(">", ">generic|");
                            line = line.replaceFirst(" ", "|");
                            reverseBuffer.append(line.replaceFirst("CON__", "REV__CON__")).append("\n");
                        } else if (line.matches(">.*|")) {
                            reverseBuffer.append(line.replaceFirst("\\|", "|REV__")).append("\n");
                        }
                        normalBuffer.append(line).append("\n");
                    } else {
                        normalBuffer.append(line).append("\n");
                        reverseBuffer.append(line).append("\n");
                    }
                }
            }
            if (normalBuffer.length() != 0 && reverseBuffer.length() != 0) {
                finalWriter.append(normalBuffer.toString()).append("\n");
                finalWriter.append(reverseBuffer.toString()).append("\n");
            }
            preparedFile.delete();
        }
        return finalFile;
    }

}
