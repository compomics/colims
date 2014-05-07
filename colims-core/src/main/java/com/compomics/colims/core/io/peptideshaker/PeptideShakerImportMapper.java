package com.compomics.colims.core.io.peptideshaker;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.utilities_to_colims.UtilitiesPsmMapper;
import com.compomics.colims.core.io.utilities_to_colims.UtilitiesSpectrumMapper;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.Spectrum;
import com.compomics.util.db.ObjectsCache;
import com.compomics.util.experiment.MsExperiment;
import com.compomics.util.experiment.ProteomicAnalysis;
import com.compomics.util.experiment.SampleAnalysisSet;
import com.compomics.util.experiment.identification.IdentificationMethod;
import com.compomics.util.experiment.identification.SequenceFactory;
import com.compomics.util.experiment.identification.identifications.Ms2Identification;
import com.compomics.util.experiment.identification.matches.SpectrumMatch;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.SpectrumFactory;
import com.compomics.util.preferences.GenePreferences;
import com.compomics.util.preferences.IdFilter;
import com.compomics.util.preferences.PTMScoringPreferences;
import com.compomics.util.preferences.ProcessingPreferences;
import eu.isas.peptideshaker.myparameters.PSParameter;
import eu.isas.peptideshaker.myparameters.PSSettings;
import eu.isas.peptideshaker.myparameters.PeptideShakerSettings;
import java.io.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Niels Hulstaert
 */
@Component("peptideShakerImportMapper")
@Transactional
public class PeptideShakerImportMapper {

    private static final Logger LOGGER = Logger.getLogger(PeptideShakerImportMapper.class);
    @Autowired
    private UtilitiesSpectrumMapper utilitiesSpectrumMapper;
    @Autowired
    private UtilitiesPsmMapper utilitiesPsmMapper;
    /**
     * Compomics utilities spectrum factory.
     */
    private final SpectrumFactory spectrumFactory = SpectrumFactory.getInstance();
    /**
     * Compomics utilities sequence factory.
     */
    private final SequenceFactory sequenceFactory = SequenceFactory.getInstance();
    /**
     * The cache used to store objects.
     */
    private ObjectsCache objectsCache;
    /**
     * The PeptideShaker experiment settings
     */
    private PeptideShakerSettings experimentSettings;

    public List<AnalyticalRun> map(UnpackedPsDataImport unpackedPsDataImport) throws MappingException {
        //the analytical runs onto the utilities replicates will be mapped
        List<AnalyticalRun> analyticalRuns = new ArrayList<>();

        try {
            LOGGER.info("Start mapping PeptideShaker experiment " + unpackedPsDataImport.getMsExperiment().getReference() + " on domain model Experiment class");

            //get the MsExperiment object
            MsExperiment msExperiment = unpackedPsDataImport.getMsExperiment();

            //load experiment settings
            loadExperimentSettings(msExperiment);

            //load fasta resource in sequence factory        
            loadFastaFile(unpackedPsDataImport.getFastaDb().getFilePath());

            //iterate over samples
            for (com.compomics.util.experiment.biology.Sample sourceSample : msExperiment.getSamples().values()) {
                SampleAnalysisSet sampleAnalysisSet = msExperiment.getAnalysisSet(sourceSample);
                ArrayList<Integer> replicateNumbers = sampleAnalysisSet.getReplicateNumberList();
                //iterate over replicates/analytical runs
                for (Integer replicateNumber : replicateNumbers) {
                    ProteomicAnalysis proteomicAnalysis = sampleAnalysisSet.getProteomicAnalysis(replicateNumber);
                    //make new analytical run
                    AnalyticalRun analyticalRun = new AnalyticalRun();
                    //@todo check if there is a more suitable candidate as accession number
                    analyticalRun.setName(replicateNumber.toString());

                    //get (Ms2)Identification
                    //@todo find out what the identification number is                 
                    Ms2Identification ms2Identification = (Ms2Identification) proteomicAnalysis.getIdentification(IdentificationMethod.MS2_IDENTIFICATION);

                    if (ms2Identification.isDB()) {
                        try {
                            //connect to the db                            
                            ms2Identification.establishConnection(unpackedPsDataImport.getDbDirectory().getAbsolutePath(), false, objectsCache);
                        } catch (SQLException ex) {
                            LOGGER.error(ex);
                            throw new MappingException(ex.getMessage(), ex.getCause());
                        }
                    } else {
                        throw new IllegalStateException("The Ms2Identification should have a db backend.");
                    }

                    //@todo is this necessary?
                    //load spectrum, peptide and protein matches                   
                    loadSpectrumMatches(ms2Identification, unpackedPsDataImport);
                    loadPeptideMatches(ms2Identification);
                    loadProteinMatches(ms2Identification);

                    List<Spectrum> spectrums = new ArrayList<>();
                    //iterate over spectrum files
                    for (String spectrumFileName : ms2Identification.getSpectrumFiles()) {
                        //iterate over the spectrum identifications
                        for (String spectrumKey : ms2Identification.getSpectrumIdentification(spectrumFileName)) {
                            //get spectrum by key
                            MSnSpectrum sourceSpectrum = (MSnSpectrum) spectrumFactory.getSpectrum(spectrumKey);

                            Spectrum targetSpectrum = new Spectrum();

                            //check if an identification match exists
                            boolean matchExists = ms2Identification.matchExists(spectrumKey);
                            if (matchExists) {
                                SpectrumMatch spectrumMatch = ms2Identification.getSpectrumMatch(spectrumKey);
                                utilitiesPsmMapper.map(ms2Identification, spectrumMatch, targetSpectrum);
                            } else {
                                LOGGER.debug("No PSM was found for spectrum " + spectrumKey);
                            }

                            //map MSnSpectrum to model Spectrum
                            //@todo get fragmentation type from peptideshaker
                            utilitiesSpectrumMapper.map(sourceSpectrum, null, targetSpectrum);
                            spectrums.add(targetSpectrum);
                            //set entity relations
                            targetSpectrum.setAnalyticalRun(analyticalRun);
                        }
                    }

                    analyticalRuns.add(analyticalRun);
                    //set entity relations
                    analyticalRun.setSpectrums(spectrums);
                }
            }
        } catch (IOException | SQLException | ClassNotFoundException | InterruptedException | IllegalArgumentException | MzMLUnmarshallerException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new MappingException(ex);
        }
        LOGGER.info("Finished mapping PeptideShaker experiment " + unpackedPsDataImport.getMsExperiment().getReference() + " on a list of analytical runs");

        return analyticalRuns;
    }

    /**
     * Clear the mapping resources: reset the SpectrumFactory, SequenceFactory,
     * ...
     *
     * @throws java.io.IOException
     * @throws java.sql.SQLException
     */
    public void clear() throws IOException, SQLException {
        spectrumFactory.clearFactory();
        sequenceFactory.clearFactory();
        objectsCache = new ObjectsCache();
        objectsCache.setAutomatedMemoryManagement(true);
        utilitiesPsmMapper.clear();
        utilitiesPsmMapper.clear();
    }

    /**
     * Load the fasta file in the SequenceFactory.
     *
     * @param fastaFilePath the fasta file path
     */
    private void loadFastaFile(String fastaFilePath) throws FileNotFoundException, IOException, ClassNotFoundException {
        LOGGER.debug("Start loading FASTA file.");
        sequenceFactory.loadFastaFile(new File(fastaFilePath), null);
        LOGGER.debug("Finish loading FASTA file.");
    }

    /**
     * Load the spectrum matches from the Ms2Identification.
     *
     * @param ms2Identification the Ms2Identification
     * @param source
     * @param psmProbabilities
     * @throws SQLException
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws InterruptedException
     */
    private void loadSpectrumMatches(Ms2Identification ms2Identification, UnpackedPsDataImport source) throws SQLException, IOException, ClassNotFoundException, InterruptedException {
        for (String spectrumFileName : ms2Identification.getSpectrumFiles()) {
            loadSpectraFromMgfFile(source.getMgfFileByName(spectrumFileName));
            ms2Identification.loadSpectrumMatches(spectrumFileName, null);
            ms2Identification.loadSpectrumMatchParameters(spectrumFileName, new PSParameter(), null);
        }
    }

    /**
     * Load spectra from a given mfg file in the Utilities SpectrumFactory.
     *
     * @param mgfFile the mgf file
     * @throws SQLException
     * @throws IOException
     * @throws InterruptedException
     */
    private void loadSpectraFromMgfFile(File mgfFile) throws FileNotFoundException, IOException, ClassNotFoundException {
        LOGGER.debug("Start importing spectra from file " + mgfFile.getName() + " into the utilities SpectrumFactory.");
        spectrumFactory.addSpectra(mgfFile, null);
        LOGGER.debug("Finish importing spectra from file " + mgfFile.getName() + " into the utilities SpectrumFactory.");
    }

    /**
     * Load the peptide matches from the Ms2Identification.
     *
     * @param ms2Identification the Ms2Identification
     * @throws SQLException
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws InterruptedException
     */
    private void loadPeptideMatches(Ms2Identification ms2Identification) throws SQLException, IOException, ClassNotFoundException, InterruptedException {
        ms2Identification.loadPeptideMatches(null);
        ms2Identification.loadPeptideMatchParameters(new PSParameter(), null);
    }

    /**
     * Load the protein matches from the Ms2Identification.
     *
     * @param ms2Identification the Ms2Identification
     * @throws SQLException
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws InterruptedException
     */
    private void loadProteinMatches(Ms2Identification ms2Identification) throws SQLException, IOException, ClassNotFoundException, InterruptedException {
        ms2Identification.loadProteinMatches(null);
        ms2Identification.loadProteinMatchParameters(new PSParameter(), null);
    }

    /**
     * Load the PeptideShaker settings.
     *
     * @param msExperiment
     */
    private void loadExperimentSettings(MsExperiment msExperiment) {
        experimentSettings = new PeptideShakerSettings();

        if (msExperiment.getUrParam(experimentSettings) instanceof PSSettings) {

            // convert old settings files using utilities version 3.10.68 or older
            // convert the old ProcessingPreferences object
            PSSettings tempSettings = (PSSettings) msExperiment.getUrParam(experimentSettings);
            ProcessingPreferences tempProcessingPreferences = new ProcessingPreferences();
            tempProcessingPreferences.setProteinFDR(tempSettings.getProcessingPreferences().getProteinFDR());
            tempProcessingPreferences.setPeptideFDR(tempSettings.getProcessingPreferences().getPeptideFDR());
            tempProcessingPreferences.setPsmFDR(tempSettings.getProcessingPreferences().getPsmFDR());

            // convert the old PTMScoringPreferences object
            PTMScoringPreferences tempPTMScoringPreferences = new PTMScoringPreferences();
            tempPTMScoringPreferences.setaScoreCalculation(tempSettings.getPTMScoringPreferences().aScoreCalculation());
            tempPTMScoringPreferences.setaScoreNeutralLosses(tempSettings.getPTMScoringPreferences().isaScoreNeutralLosses());
            tempPTMScoringPreferences.setFlrThreshold(tempSettings.getPTMScoringPreferences().getFlrThreshold());

            experimentSettings = new PeptideShakerSettings(tempSettings.getSearchParameters(), tempSettings.getAnnotationPreferences(),
                    tempSettings.getSpectrumCountingPreferences(), tempSettings.getProjectDetails(), tempSettings.getFilterPreferences(),
                    tempSettings.getDisplayPreferences(),
                    tempSettings.getMetrics(), tempProcessingPreferences, tempSettings.getIdentificationFeaturesCache(),
                    tempPTMScoringPreferences, new GenePreferences(), new IdFilter());

        } else {
            experimentSettings = (PeptideShakerSettings) msExperiment.getUrParam(experimentSettings);
        }
    }
}