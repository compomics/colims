package com.compomics.colims.core.io.maxquant;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.utilities_to_colims.UtilitiesSpectrumMapper;
import com.compomics.colims.core.io.peptideshaker.PeptideShakerImportMapper;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.Protein;
import com.compomics.colims.model.Spectrum;
import com.compomics.util.db.ObjectsCache;
import com.compomics.util.experiment.identification.SequenceFactory;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.SpectrumFactory;
import java.io.File;
import java.io.FileNotFoundException;
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
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

/**
 *
 * @author Davy
 */
@Component("maxQuantImportMapper")
public class MaxQuantImportMapper {

    private static final Logger LOGGER = Logger.getLogger(PeptideShakerImportMapper.class);
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
    /**
     * The cache used to store objects.
     */
    private ObjectsCache objectsCache;

    /**
     * method to import a max quant search into colims
     *
     * @param aMaxQuantImport the resulting files from the max quant search and
     * the fasta ran against
     * @param sampleRanThroughMaxQuant the sample we should store against since
     * there is no way of inferring which sample was run from the files alone
     * @return the list of analytical runs that were mapped to the sample
     * @throws IOException if something went wrong while trying to read files
     * from the folder
     * @throws UnparseableException if a file is missing crucial information
     * @throws HeaderEnumNotInitialisedException this means that a header was
     * added to the header enum that did not have a possible header name
     * @throws MappingException if a mapping could not be completed
     */
    public List<AnalyticalRun> map(MaxQuantDataImport aMaxQuantImport) throws MappingException, FileNotFoundException, ClassNotFoundException {
        LOGGER.info("started mapping folder: " + aMaxQuantImport.getMaxQuantDirectory().getName());
        List<AnalyticalRun> mappedRuns = new ArrayList<>();
        File preparedFastaFile = null;
        try {
            //just in case
            maxQuantParser.clearParsedProject();
            clearMappingResources();
            preparedFastaFile = prepareFasta(aMaxQuantImport.getFastaDb().getFilePath());
            LOGGER.debug("Start loading FASTA file.");
            sequenceFactory.loadFastaFile(preparedFastaFile, null);
            LOGGER.debug("Finish loading FASTA file.");
            maxQuantParser.parseMaxQuantTextFolder(aMaxQuantImport.getMaxQuantDirectory());

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
        } catch (IOException | ClassNotFoundException | SQLException | HeaderEnumNotInitialisedException | UnparseableException | MappingException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new MappingException("there was a problem storing your max quant data, underlying exception: ", ex);
        } finally {
            if (preparedFastaFile != null) {
                preparedFastaFile.delete();
            }
        }
        return mappedRuns;

    }

    private void clearMappingResources() throws IOException, SQLException {
        spectrumFactory.clearFactory();
        sequenceFactory.clearFactory();
        objectsCache = new ObjectsCache();
        objectsCache.setAutomatedMemoryManagement(true);
        newProteins.clear();
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
            LineNumberReader originalReader = new LineNumberReader(new FileReader(originalFile));
            line = null;
            while ((line = originalReader.readLine()) != null) {
                writer.write(line + "\n");
            }
            originalReader.close();
            InputStream fileStream = new ClassPathResource("config/contaminants.fasta").getInputStream();
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
