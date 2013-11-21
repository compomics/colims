package com.compomics.colims.core.mapper.impl;

import com.compomics.colims.core.mapper.Mapper;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

import com.compomics.colims.core.exception.MappingException;
import com.compomics.colims.core.io.peptideshaker.model.PeptideShakerImport;
import com.compomics.colims.core.service.ProteinService;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.Experiment;
import com.compomics.colims.model.Protein;
import com.compomics.colims.model.Sample;
import com.compomics.colims.model.Spectrum;
import com.compomics.util.db.ObjectsCache;
import com.compomics.util.experiment.MsExperiment;
import com.compomics.util.experiment.ProteomicAnalysis;
import com.compomics.util.experiment.SampleAnalysisSet;
import com.compomics.util.experiment.identification.IdentificationMethod;
import com.compomics.util.experiment.identification.SearchParameters;
import com.compomics.util.experiment.identification.SequenceFactory;
import com.compomics.util.experiment.identification.identifications.Ms2Identification;
import com.compomics.util.experiment.identification.matches.SpectrumMatch;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.SpectrumFactory;
import com.google.common.eventbus.EventBus;
import eu.isas.peptideshaker.myparameters.PSParameter;
import eu.isas.peptideshaker.myparameters.PeptideShakerSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Niels Hulstaert
 */
@Component("utilitiesExperimentMapper")
@Transactional
public class UtilitiesExperimentMapper implements Mapper<PeptideShakerImport, Experiment> {

    private static final Logger LOGGER = Logger.getLogger(UtilitiesExperimentMapper.class);
    @Autowired
    private UtilitiesSpectrumMapper utilitiesSpectrumMapper;
    @Autowired
    private UtilitiesPsmMapper utilitiesPsmMapper;
    @Autowired
    private ProteinService proteinService;
    @Autowired
    private EventBus eventBus;
    /**
     * Compomics utilities spectrum factory
     */
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
     * The PeptideShaker experiment settings
     */
    private PeptideShakerSettings experimentSettings;

    @Override
    public void map(PeptideShakerImport source, Experiment target) throws MappingException {
        if (source == null || target == null) {
            throw new IllegalArgumentException("The source and/or target of the mapping are null");
        }
        try {
            //clear mapping resources
            clearMappingResources();

            LOGGER.info("Start mapping PeptideShaker experiment " + source.getMsExperiment().getReference() + " on domain model Experiment class");

            //get the MsExperiment object
            MsExperiment msExperiment = source.getMsExperiment();
            //set title
            target.setTitle(msExperiment.getReference());

            //load experiment settings
            experimentSettings = loadExperimentSettings(msExperiment);

            //load fasta file in sequence factory        
            loadFastaFile(source.getFastaFile());

            //add samples        
            List<Sample> samples = new ArrayList<>();
            //iterate over samples
            for (com.compomics.util.experiment.biology.Sample sourceSample : msExperiment.getSamples().values()) {
                Sample sample = new Sample();
                //@todo does reference map to name?
                sample.setName(sourceSample.getReference());

                //add analytical runs
                //a replicate is mapped to an AnalyticalRun
                List<AnalyticalRun> analyticalRuns = new ArrayList<>();
                SampleAnalysisSet sampleAnalysisSet = msExperiment.getAnalysisSet(sourceSample);
                ArrayList<Integer> replicateNumbers = sampleAnalysisSet.getReplicateNumberList();
                //iterate over replicates/analytical runs
                for (Integer replicateNumber : replicateNumbers) {
                    ProteomicAnalysis proteomicAnalysis = sampleAnalysisSet.getProteomicAnalysis(replicateNumber);
                    AnalyticalRun analyticalRun = new AnalyticalRun();
                    //@todo check if there is a more suitable candidate as accession number
                    analyticalRun.setName(replicateNumber.toString());

                    //get (Ms2)Identification
                    //@todo find out what the identification number is                 
                    Ms2Identification ms2Identification = (Ms2Identification) proteomicAnalysis.getIdentification(IdentificationMethod.MS2_IDENTIFICATION);

                    if (ms2Identification.isDB()) {
                        try {
                            //connect to derby db                            
                            ms2Identification.establishConnection(source.getDbDirectory().getAbsolutePath(), false, objectsCache);
                        } catch (SQLException ex) {
                            LOGGER.error(ex);
                            throw new MappingException(ex.getMessage(), ex.getCause());
                        }
                    } else {
                        throw new IllegalStateException("The Ms2Identification should have a db backend.");
                    }

                    //@todo is this necessary?
                    //load spectrum, peptide and protein matches
                    loadSpectrumMatches(ms2Identification, source);
                    loadPeptideMatches(ms2Identification);
                    loadProteinMatches(ms2Identification);

                    List<Spectrum> spectrums = new ArrayList<>();
                    //iterate over spectrum files
                    for (String spectrumFileName : ms2Identification.getSpectrumFiles()) {
                        //iterate over the spectrum titles in the SpectrumFactory for the given file
                        for (String spectrumTitle : spectrumFactory.getSpectrumTitles(spectrumFileName)) {
                            //get the spectrum key
                            String spectrumKey = com.compomics.util.experiment.massspectrometry.Spectrum.getSpectrumKey(spectrumFileName, spectrumTitle);

                            //get spectrum by key
                            MSnSpectrum sourceSpectrum = (MSnSpectrum) spectrumFactory.getSpectrum(spectrumKey);

                            Spectrum targetSpectrum = new Spectrum();

                            //check if an identification match exists
                            boolean matchExists = ms2Identification.matchExists(spectrumKey);
                            int charge = 0;
                            if (matchExists) {
                                SpectrumMatch spectrumMatch = ms2Identification.getSpectrumMatch(spectrumKey);
                                charge = spectrumMatch.getBestAssumption().getIdentificationCharge().value;
                                utilitiesPsmMapper.map(ms2Identification, spectrumMatch, targetSpectrum);
                            } else {
                                LOGGER.debug("No PSM was found for spectrum " + spectrumKey);
                                if (!sourceSpectrum.getPrecursor().getPossibleCharges().isEmpty()) {
                                    charge = sourceSpectrum.getPrecursor().getPossibleCharges().get(0).value;
                                }
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
                    analyticalRun.setSample(sample);
                }
                samples.add(sample);
                //set entity relations                        
                sample.setExperiment(target);
                sample.setAnalyticalRuns(analyticalRuns);
            }
            //set enitity relations
            target.setSamples(samples);
            LOGGER.info("Finished mapping PeptideShaker experiment " + source.getMsExperiment().getReference() + " on domain model Experiment");
        } catch (FileNotFoundException ex) {
            LOGGER.error(ex.getMessage(), ex);
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
        } catch (ClassNotFoundException | SQLException | InterruptedException | IllegalArgumentException | MzMLUnmarshallerException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    /**
     * Clear the mapping resources: reset the SpectrumFactory, ...
     */
    private void clearMappingResources() throws IOException {
        spectrumFactory.clearFactory();
        sequenceFactory.clearFactory();
        objectsCache = new ObjectsCache();
        objectsCache.setAutomatedMemoryManagement(true);
        newProteins.clear();
    }

    /**
     * Load the fasta file in the SequenceFactory.
     *
     * @param fastaFile the fasta file
     */
    private void loadFastaFile(File fastaFile) throws FileNotFoundException, IOException, ClassNotFoundException {
        LOGGER.debug("Start loading FASTA file.");
        sequenceFactory.loadFastaFile(fastaFile);
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
    private void loadSpectrumMatches(Ms2Identification ms2Identification, PeptideShakerImport source) throws SQLException, IOException, ClassNotFoundException, InterruptedException {
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
        spectrumFactory.addSpectra(mgfFile);
        LOGGER.debug("Finish importing spectra from file " + mgfFile.getName() + " into the utilities SpectrumFactory.");
    }

    /**
     * Load the peptide matches from the Ms2Identification.
     *
     * @param ms2Identification the Ms2Identification
     * @param peptideProbabilities
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
     * @param proteinProbabilities
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
     * @return
     */
    private PeptideShakerSettings loadExperimentSettings(MsExperiment msExperiment) {
        PeptideShakerSettings experimentSettings = new PeptideShakerSettings();

        experimentSettings = (PeptideShakerSettings) msExperiment.getUrParam(experimentSettings);
        SearchParameters searchParameters = experimentSettings.getSearchParameters();

        return experimentSettings;
    }
}
