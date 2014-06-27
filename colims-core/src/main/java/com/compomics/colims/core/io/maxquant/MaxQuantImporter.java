package com.compomics.colims.core.io.maxquant;

import com.compomics.colims.core.io.DataImport;
import com.compomics.colims.core.io.DataImporter;
import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.SearchSettingsMapper;
import com.compomics.colims.core.io.utilities_to_colims.UtilitiesSpectrumMapper;
import com.compomics.colims.core.util.ResourceUtils;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.Protein;
import com.compomics.colims.model.QuantificationSettings;
import com.compomics.colims.model.SearchAndValidationSettings;
import com.compomics.colims.model.Spectrum;
import com.compomics.util.experiment.identification.SequenceFactory;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.SpectrumFactory;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
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
    private MaxQuantParser maxQuantParser;
    @Autowired
    private MaxQuantUtilitiesAnalyticalRunMapper maxQuantUtilitiesAnalyticalRunMapper;
    @Autowired
    private MaxQuantUtilitiesPsmMapper maxQuantUtilitiesPsmMapper;
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
        this.maxQuantImport = (MaxQuantImport) dataImport;
    }
    
    @Override
    public void clear() {        
        try {
            spectrumFactory.clearFactory();
            sequenceFactory.clearFactory();
            newProteins.clear();
        } catch (IOException | SQLException ex) {
            LOGGER.error(ex);
        }
    }
    
    @Override
    public SearchAndValidationSettings importSearchSettings() throws MappingException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public QuantificationSettings importQuantSettings() throws MappingException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
            maxQuantParser.parseMaxQuantTextFolder(maxQuantImport.getMaxQuantDirectory());
            
            for (MaxQuantAnalyticalRun aParsedRun : maxQuantParser.getRuns()) {
                AnalyticalRun targetRun = new AnalyticalRun();
                
                maxQuantUtilitiesAnalyticalRunMapper.map(aParsedRun, targetRun);
                
                List<Spectrum> mappedSpectra = new ArrayList<>(aParsedRun.getListOfSpectra().size());
                
                for (MSnSpectrum aParsedSpectrum : aParsedRun.getListOfSpectra()) {
                    Spectrum targetSpectrum = new Spectrum();

                    //set entity relation
                    targetSpectrum.setAnalyticalRun(targetRun);

                    //for the spectra we can just use the standard utilities mapper
                    //@TODO get the fragmentation type 
                    utilitiesSpectrumMapper.map(aParsedSpectrum, null, targetSpectrum);
                    mappedSpectra.add(targetSpectrum);

                    //
                    maxQuantUtilitiesPsmMapper.map(aParsedSpectrum, maxQuantParser, targetSpectrum);
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
        try (FileWriter writer = new FileWriter(preparedFile)) {
            try (LineNumberReader originalReader = new LineNumberReader(new FileReader(originalFile))) {
                line = null;
                while ((line = originalReader.readLine()) != null) {
                    writer.write(line + "\n");
                }
            }
            InputStream fileStream = ResourceUtils.getResourceByRelativePath("config/contaminants.fasta").getInputStream();
            LineNumberReader contaminantsReader = new LineNumberReader(new InputStreamReader(fileStream));
            while ((line = contaminantsReader.readLine()) != null) {
                writer.write(line + "\n");
            }
            writer.flush();
        }
        File finalFile = new File(preparedFile.getParentFile(), preparedFile.getName() + "_spiked.fasta");
        if (finalFile.exists()) {
            finalFile.delete();
        }
        LineNumberReader preparedFileReader = new LineNumberReader(new FileReader(preparedFile));
        FileWriter finalWriter = new FileWriter(finalFile);
        StringBuilder normalBuffer = new StringBuilder();
        StringBuilder reverseBuffer = new StringBuilder();
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
        if (normalBuffer.length() != 0 && reverseBuffer.length() != 0) {
            finalWriter.append(normalBuffer.toString()).append("\n");
            finalWriter.append(reverseBuffer.toString()).append("\n");
        }
        preparedFile.delete();
        finalWriter.flush();
        finalWriter.close();
        return finalFile;
    }    
    
}
