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
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<AnalyticalRun> map(MaxQuantDataImport aMaxQuantImport) throws MappingException {
        LOGGER.info("started mapping folder: " + aMaxQuantImport.getMaxQuantDirectory().getName());
        List<AnalyticalRun> mappedRuns = new ArrayList<>();

        try {
            //just in case
            maxQuantParser.clearParsedProject();
            clearMappingResources();
            loadFastaFile(aMaxQuantImport.getFastaDb().getFilePath());

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
        } catch (IOException | SQLException | ClassNotFoundException | HeaderEnumNotInitialisedException | UnparseableException | MappingException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new MappingException(ex);
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

    /**
     * Load the fasta file in the SequenceFactory.
     *
     * @param fastaFilePath  the fasta file path
     */
    private void loadFastaFile(String fastaFilePath) throws FileNotFoundException, IOException, ClassNotFoundException {
        LOGGER.debug("Start loading FASTA file.");
        sequenceFactory.loadFastaFile(new File(fastaFilePath), null);
        LOGGER.debug("Finish loading FASTA file.");
    }
}
