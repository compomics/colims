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
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.PeptideHasProtein;
import com.compomics.colims.model.Protein;
import com.compomics.colims.model.Sample;
import com.compomics.colims.model.Spectrum;
import com.compomics.util.db.ObjectsCache;
import com.compomics.util.experiment.MsExperiment;
import com.compomics.util.experiment.ProteomicAnalysis;
import com.compomics.util.experiment.SampleAnalysisSet;
import com.compomics.util.experiment.identification.IdentificationMethod;
import com.compomics.util.experiment.identification.PeptideAssumption;
import com.compomics.util.experiment.identification.SearchParameters;
import com.compomics.util.experiment.identification.SequenceFactory;
import com.compomics.util.experiment.identification.identifications.Ms2Identification;
import com.compomics.util.experiment.identification.matches.ProteinMatch;
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
    private Mapper utilitiesSpectrumMapper;
    @Autowired
    private Mapper utilitiesPeptideMapper;
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
                            objectsCache = new ObjectsCache();
                            objectsCache.setAutomatedMemoryManagement(true);
                            ms2Identification.establishConnection(source.getDbDirectory().getAbsolutePath(), false, objectsCache);
                        } catch (SQLException ex) {
                            LOGGER.error(ex);
                            throw new MappingException(ex.getMessage(), ex.getCause());
                        }
                    } else {
                        throw new IllegalStateException("The Ms2Identification should have a db backend.");
                    }

                    //load spectrum, peptide and protein matches
                    PSParameter psmProbabilities = new PSParameter();
                    PSParameter peptideProbabilities = new PSParameter();
                    PSParameter proteinProbabilities = new PSParameter();
                    loadSpectrumMatches(ms2Identification, source, psmProbabilities);
                    loadPeptideMatches(ms2Identification, peptideProbabilities);
                    loadProteinMatches(ms2Identification, proteinProbabilities);

                    List<Spectrum> spectrums = new ArrayList<>();
                    //iterate over spectrum files
                    for (String spectrumFileName : ms2Identification.getSpectrumFiles()) {
                        //get spectrum identification keys by mgf file name
                        List<String> spectrumIdentificationKeys = ms2Identification.getSpectrumIdentification(spectrumFileName);
                        //iterate over psms
                        for (String spectrumIdentificationKey : spectrumIdentificationKeys) {
                            try {
                                SpectrumMatch spectrumMatch = ms2Identification.getSpectrumMatch(spectrumIdentificationKey);
                                //get spectrum by SpectrumMatch key
                                MSnSpectrum sourceSpectrum = (MSnSpectrum) spectrumFactory.getSpectrum(spectrumMatch.getKey());

                                Spectrum targetSpectrum = new Spectrum();
                                //map MSnSpectrum to model Spectrum
                                utilitiesSpectrumMapper.map(sourceSpectrum, targetSpectrum);

                                spectrums.add(targetSpectrum);
                                //set entity relations
                                targetSpectrum.setAnalyticalRun(analyticalRun);

                                mapSpectrumRelations(ms2Identification, spectrumMatch, targetSpectrum);
                            } catch (MzMLUnmarshallerException | IllegalArgumentException | SQLException | IOException | ClassNotFoundException ex) {
                                LOGGER.error(ex);
                                throw new MappingException(ex.getMessage(), ex.getCause());
                            }
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
        } catch (ClassNotFoundException | SQLException | InterruptedException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    /**
     * Map the Spectrum relations.
     *
     * @param ms2Identification the Ms2Identification
     * @param spectrumMatch the SpectrumMatch
     * @param targetSpectrum the target spectrum
     * @throws MappingException
     */
    private void mapSpectrumRelations(Ms2Identification ms2Identification, SpectrumMatch spectrumMatch, Spectrum targetSpectrum) throws MappingException {
        //get best assumption
        PeptideAssumption peptideAssumption = spectrumMatch.getBestAssumption();
        //check if peptide assumption is decoy
        //if (!peptideAssumption.getDecoy) {
        com.compomics.util.experiment.biology.Peptide sourcePeptide = peptideAssumption.getPeptide();

        Peptide targetPeptide = new Peptide();
        utilitiesPeptideMapper.map(sourcePeptide, targetPeptide);
        //set entity relations
        targetSpectrum.getPeptides().add(targetPeptide);
        targetPeptide.setSpectrum(targetSpectrum);

        //get protein matches keys
        List<String> proteinKeys = sourcePeptide.getParentProteins();
        List<PeptideHasProtein> peptideHasProteins = new ArrayList<>();
        //iterate over protein keys
        for (String proteinKey : proteinKeys) {
            try {
                ProteinMatch proteinMatch = ms2Identification.getProteinMatch(proteinKey);
                //get best/main match
                if (proteinMatch != null) {
                    com.compomics.util.experiment.biology.Protein sourceProtein = sequenceFactory.getProtein(proteinMatch.getMainMatch());
                    if (!sourceProtein.isDecoy()) {
                        PeptideHasProtein peptideHasProtein = new PeptideHasProtein();
                        //check if protein is found in the db or in the newProteins
                        //@todo configure hibernate cache and check performance
                        Protein targetProtein = proteinService.findByAccession(sourceProtein.getAccession());
                        if (targetProtein != null) {
                        } else if (newProteins.containsKey(sourceProtein.getAccession())) {
                            targetProtein = newProteins.get(sourceProtein.getAccession());
                        } else {
                            targetProtein = new Protein(sourceProtein.getAccession(), sourceProtein.getSequence(), sourceProtein.getDatabaseType());
                        }
                        peptideHasProteins.add(peptideHasProtein);
                        //set entity relations
                        peptideHasProtein.setProtein(targetProtein);
                        peptideHasProtein.setPeptide(targetPeptide);
                    }
                }
            } catch (InterruptedException | IllegalArgumentException | SQLException | IOException | ClassNotFoundException ex) {
                LOGGER.error(ex);
                throw new MappingException(ex.getMessage(), ex.getCause());
            }
        }
        targetPeptide.setPeptideHasProteins(peptideHasProteins);
        //}
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
    private void loadSpectrumMatches(Ms2Identification ms2Identification, PeptideShakerImport source, PSParameter psmProbabilities) throws SQLException, IOException, ClassNotFoundException, InterruptedException {
        for (String spectrumFileName : ms2Identification.getSpectrumFiles()) {
            loadSpectraFromMgfFile(source.getMgfFileByName(spectrumFileName));
            ms2Identification.loadSpectrumMatches(spectrumFileName, null);
            ms2Identification.loadSpectrumMatchParameters(spectrumFileName, psmProbabilities, null);
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
    private void loadPeptideMatches(Ms2Identification ms2Identification, PSParameter peptideProbabilities) throws SQLException, IOException, ClassNotFoundException, InterruptedException {
        ms2Identification.loadPeptideMatches(null);
        ms2Identification.loadPeptideMatchParameters(peptideProbabilities, null);
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
    private void loadProteinMatches(Ms2Identification ms2Identification, PSParameter proteinProbabilities) throws SQLException, IOException, ClassNotFoundException, InterruptedException {
        ms2Identification.loadProteinMatches(null);
        ms2Identification.loadProteinMatchParameters(proteinProbabilities, null);
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
