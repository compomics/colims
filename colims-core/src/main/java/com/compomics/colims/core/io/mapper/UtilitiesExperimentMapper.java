package com.compomics.colims.core.io.mapper;

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
import com.compomics.colims.core.io.model.PeptideShakerImport;
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
import com.compomics.util.experiment.identification.PeptideAssumption;
import com.compomics.util.experiment.identification.SequenceFactory;
import com.compomics.util.experiment.identification.identifications.Ms2Identification;
import com.compomics.util.experiment.identification.matches.ProteinMatch;
import com.compomics.util.experiment.identification.matches.SpectrumMatch;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.SpectrumFactory;
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

    @Override     
    public void map(PeptideShakerImport source, Experiment target) throws MappingException {        
        if (source == null || target == null) {
            throw new IllegalArgumentException("The source and/or target of the mapping are null");
        }
        LOGGER.info("Start mapping PeptideShaker experiment " + source.getMsExperiment().getReference() + " on domain model Experiment");

        MsExperiment msExperiment = source.getMsExperiment();
        //set title
        target.setTitle(msExperiment.getReference());

        //load experiment settings
        PeptideShakerSettings experimentSettings = loadExperimentSettings(msExperiment);
        
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
                Ms2Identification ms2Identification = (Ms2Identification) proteomicAnalysis.getIdentification(1);

                if (ms2Identification.isDB()) {
                    try {
                        //connect to derby db
                        ms2Identification.establishConnection(source.getDbDirectory().getAbsolutePath(), false, new ObjectsCache());
                    } catch (SQLException ex) {
                        LOGGER.error(ex);
                        throw new MappingException(ex.getMessage(), ex.getCause());
                    }
                } else {
                    throw new IllegalStateException("The Ms2Identification should have a db backend.");
                }

                //load protein matches
                loadProteinMatches(ms2Identification);

                List<Spectrum> spectrums = new ArrayList<>();
                //iterate over spectrum files
                for (String spectrumFileName : ms2Identification.getSpectrumFiles()) {
                    boolean loadedSuccessfully = loadSpectraFromMgfFile(source.getMgfFileByName(spectrumFileName));

                    if (loadedSuccessfully) {
                        loadSpectrumMatches(ms2Identification, spectrumFileName);
                    }

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
        if (!peptideAssumption.isDecoy()) {
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
        }
    }

    /**
     * Load the fasta file in the SequenceFactory.
     *
     * @param fastaFile the fasta file
     */
    private void loadFastaFile(File fastaFile) throws MappingException {        
        try {
            LOGGER.debug("Start loading FASTA file.");
            sequenceFactory.loadFastaFile(fastaFile);
            LOGGER.debug("Finish loading FASTA file.");
        } catch (FileNotFoundException ex) {
            LOGGER.error(ex);
            throw new MappingException(ex.getMessage(), ex.getCause());
        } catch (IOException | ClassNotFoundException ex) {
            LOGGER.error(ex);
            throw new MappingException(ex.getMessage(), ex.getCause());
        }
    }

    /**
     * Load spectra from a given mfg file in the Utilities SpectrumFactory.
     * Returns true if the spectra could be loaded.
     *
     * @param mgfFile the mgf file
     * @return true if the spectra could be loaded
     */
    private boolean loadSpectraFromMgfFile(File mgfFile) throws MappingException {
        boolean loadedSuccessfully = false;

        if (mgfFile != null) {
            try {
                LOGGER.debug("Start importing spectra from file " + mgfFile.getName() + " into the utilities SpectrumFactory.");
                spectrumFactory.addSpectra(mgfFile);
                LOGGER.debug("Finish importing spectra from file " + mgfFile.getName() + " into the utilities SpectrumFactory.");
                loadedSuccessfully = true;
            } catch (FileNotFoundException ex) {
                LOGGER.error(ex);
            } catch (IOException | ClassNotFoundException ex) {
                LOGGER.error(ex);
            }
        }

        return loadedSuccessfully;
    }

    /**
     * Load the spectrum matches from the Ms2Identification from spectra in the
     * given file.
     *
     * @param ms2Identification the Ms2Identification
     * @param spectrumFileName the spectrum file name
     * @throws MappingException the mapping exception
     */
    private void loadSpectrumMatches(Ms2Identification ms2Identification, String spectrumFileName) throws MappingException {
        try {
            //load psms
            ms2Identification.loadSpectrumMatches(spectrumFileName, null);
        } catch (SQLException | IOException | ClassNotFoundException ex) {
            LOGGER.error(ex);
            throw new MappingException(ex.getMessage(), ex.getCause());
        }
    }

    /**
     * Load the protein matches from the Ms2Identification.
     *
     * @param ms2Identification the Ms2Identification
     * @throws MappingException the mapping exception
     */
    private void loadProteinMatches(Ms2Identification ms2Identification) throws MappingException {
        try {
            ms2Identification.loadProteinMatches(null);
        } catch (SQLException | IOException | ClassNotFoundException ex) {
            LOGGER.error(ex);
            throw new MappingException(ex.getMessage(), ex.getCause());
        }
    }
        
    private PeptideShakerSettings loadExperimentSettings(MsExperiment msExperiment) {        
        PeptideShakerSettings experimentSettings = new PeptideShakerSettings();
                
        experimentSettings = (PeptideShakerSettings) msExperiment.getUrParam(experimentSettings);
        experimentSettings.getSearchParameters();

        return experimentSettings;
    }
}
