package com.compomics.colims.distributed.io.peptideshaker;

import com.compomics.colims.core.io.MappedData;
import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.service.FastaDbService;
import com.compomics.colims.distributed.io.DataMapper;
import com.compomics.colims.distributed.io.utilities_to_colims.UtilitiesPeptideMapper;
import com.compomics.colims.distributed.io.utilities_to_colims.UtilitiesProteinGroupMapper;
import com.compomics.colims.distributed.io.utilities_to_colims.UtilitiesSearchSettingsMapper;
import com.compomics.colims.distributed.io.utilities_to_colims.UtilitiesSpectrumMapper;
import com.compomics.colims.model.*;
import com.compomics.colims.model.enums.FastaDbType;
import com.compomics.colims.model.enums.ScoreType;
import com.compomics.colims.model.enums.SearchEngineType;
import com.compomics.util.experiment.MsExperiment;
import com.compomics.util.experiment.identification.identifications.Ms2Identification;
import com.compomics.util.experiment.identification.matches.PeptideMatch;
import com.compomics.util.experiment.identification.matches.ProteinMatch;
import com.compomics.util.experiment.identification.matches.SpectrumMatch;
import com.compomics.util.experiment.identification.matches_iterators.PeptideMatchesIterator;
import com.compomics.util.experiment.identification.matches_iterators.ProteinMatchesIterator;
import com.compomics.util.experiment.identification.matches_iterators.PsmIterator;
import com.compomics.util.experiment.identification.protein_sequences.SequenceFactory;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.SpectrumFactory;
import com.compomics.util.experiment.personalization.UrParameter;
import eu.isas.peptideshaker.parameters.PSParameter;
import eu.isas.peptideshaker.scoring.PSMaps;
import eu.isas.peptideshaker.scoring.maps.ProteinMap;
import eu.isas.peptideshaker.scoring.targetdecoy.TargetDecoyMap;
import eu.isas.peptideshaker.scoring.targetdecoy.TargetDecoyResults;
import eu.isas.peptideshaker.utils.CpsParent;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.*;

/**
 * The DataImporter implementation class for PeptideShaker projects.
 *
 * @author Niels Hulstaert
 */
@Component("peptideShakerMapper")
public class PeptideShakerMapper implements DataMapper<UnpackedPeptideShakerImport> {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PeptideShakerMapper.class);
    private static final String ANALYTICAL_RUN_NAME_SEPARATOR = ":";

    /**
     * Compomics Utilities spectrum factory.
     */
    private final SpectrumFactory spectrumFactory = SpectrumFactory.getInstance();
    /**
     * Compomics Utilities sequence factory.
     */
    private final SequenceFactory sequenceFactory = SequenceFactory.getInstance();
    /**
     * The utilities to Colims search settings mapper.
     */
    private final UtilitiesSearchSettingsMapper utilitiesSearchSettingsMapper;
    /**
     * The Compomics Utilities to Colims spectrum mapper.
     */
    private final UtilitiesSpectrumMapper utilitiesSpectrumMapper;
    /**
     * The Compomics Utilities to Colims protein group mapper.
     */
    private final UtilitiesProteinGroupMapper utilitiesProteinGroupMapper;
    /**
     * The Compomics Utilities to Colims peptide mapper.
     */
    private final UtilitiesPeptideMapper utilitiesPeptideMapper;
    /**
     * The fasta db service instance.
     */
    private final FastaDbService fastaDbService;

    @Autowired
    public PeptideShakerMapper(UtilitiesSearchSettingsMapper utilitiesSearchSettingsMapper,
                               UtilitiesSpectrumMapper utilitiesSpectrumMapper,
                               UtilitiesProteinGroupMapper utilitiesProteinGroupMapper,
                               UtilitiesPeptideMapper utilitiesPeptideMapper,
                               FastaDbService fastaDbService) {
        this.utilitiesSearchSettingsMapper = utilitiesSearchSettingsMapper;
        this.utilitiesSpectrumMapper = utilitiesSpectrumMapper;
        this.utilitiesProteinGroupMapper = utilitiesProteinGroupMapper;
        this.utilitiesPeptideMapper = utilitiesPeptideMapper;
        this.fastaDbService = fastaDbService;
    }

    /**
     * Clear the mapping resources: reset the SpectrumFactory, SequenceFactory, ...
     *
     * @throws IOException          thrown in case of an IO related problem
     * @throws SQLException         thrown in case of an SQL related problem
     * @throws InterruptedException thrown in case of thread related problem
     */
    @Override
    public void clear() throws IOException, SQLException, InterruptedException {
        spectrumFactory.clearFactory();
        sequenceFactory.clearFactory();
        //call clear methods on child mappers
        utilitiesPeptideMapper.clear();
        utilitiesProteinGroupMapper.clear();
        utilitiesSearchSettingsMapper.clear();
    }

    @Override
    public MappedData mapData(UnpackedPeptideShakerImport dataImport, Path experimentsDirectory, Path fastasDirectory) throws MappingException {
        List<AnalyticalRun> analyticalRuns = new ArrayList<>();
        Set<ProteinGroup> proteinGroups = new HashSet<>();

        try {
            AnalyticalRun analyticalRun = new AnalyticalRun();

            //set the path of the .cps archive
            analyticalRun.setStorageLocation(dataImport.getPeptideShakerCpsArchive().getCanonicalPath());

            //first, map the search settings
            //get the CpsParent instance for accessing the .cpsx file
            CpsParent cpsParent = dataImport.getCpsParent();
            //get the MsExperiment instance
            MsExperiment msExperiment = cpsParent.getExperiment();

            //get the Ms2Identification instance
            Ms2Identification identification = (Ms2Identification) cpsParent.getIdentification();

            PSMaps psMaps = new PSMaps();
            psMaps = (PSMaps) identification.getUrParam(psMaps);
            ProteinMap proteinMap = psMaps.getProteinMap();
            TargetDecoyMap targetDecoyMap = proteinMap.getTargetDecoyMap();
            TargetDecoyResults targetDecoyResults = targetDecoyMap.getTargetDecoyResults();

            double proteinThreshold = targetDecoyResults.getUserInput() / 100;
            int proteinThresholdType = targetDecoyResults.getInputType();
            ScoreType proteinScoreType;

            switch (proteinThresholdType) {
                case 0:
                    proteinScoreType = ScoreType.CONFIDENCE;
                    break;
                case 1:
                    proteinScoreType = ScoreType.FDR;
                    break;
                case 2:
                    proteinScoreType = ScoreType.FNR;
                    break;
                default:
                    throw new IllegalArgumentException("Should not be able to get here.");
            }

            //load the (primary, there's only one) FASTA DB file
            FastaDb fastaDb = fastaDbService.findById(dataImport.getFastaDbIds().get(FastaDbType.PRIMARY).get(0));
            //get the fasta file and make the path absolute
            String fastaDbFilePath = FilenameUtils.separatorsToSystem(fastaDb.getFilePath());
            Path absoluteFastaDbPath = fastasDirectory.resolve(fastaDbFilePath);
            //check if the Path exists
            if (!Files.exists(absoluteFastaDbPath)) {
                throw new IllegalArgumentException("The FASTA DB " + absoluteFastaDbPath.toString() + " doesn't exist.");
            }
            File fastaDbFile = absoluteFastaDbPath.toFile();

            LOGGER.info("Start mapping search settings for PeptideShaker experiment " + msExperiment.getReference());
            SearchAndValidationSettings searchAndValidationSettings = mapSearchSettings(dataImport, analyticalRun, fastaDb, proteinScoreType, proteinThreshold);
            LOGGER.info("Finished mapping search settings for PeptideShaker experiment " + msExperiment.getReference());

            LOGGER.info("Start mapping PeptideShaker experiment " + msExperiment.getReference());
            cpsParent.loadFastaFile(fastaDbFile.getParentFile(), null);

            //load the spectrum files, peptide en protein matches
            cpsParent.loadSpectrumFiles(null);

            //We don't need to iterate over the samples in the .cpsx file because
            //for the moment, because PeptideShaker .cpsx files contain only one sample.
            //We don't need to iterate over the replicates/analytical runs in the .cps file
            //for the moment, because there's only one replicate per sample.
            //instantiate new analytical run
            //@todo check if there is a more suitable candidate as accession number
            analyticalRun.setName(cpsParent.getSample().getReference() + ANALYTICAL_RUN_NAME_SEPARATOR + cpsParent.getReplicateNumber());

            ArrayList<UrParameter> parameters = new ArrayList<>(1);
            parameters.add(new PSParameter());

            //iterate over the protein matches
            ProteinMatchesIterator proteinMatchesIterator = identification.getProteinMatchesIterator(parameters, true, parameters, true, parameters, null);
            ProteinMatch proteinMatch;
            while ((proteinMatch = proteinMatchesIterator.next()) != null) {
                String proteinMatchKey = proteinMatch.getKey();

                PSParameter proteinGroupScore = new PSParameter();
                proteinGroupScore = (PSParameter) (identification.getProteinMatchParameter(proteinMatchKey, proteinGroupScore));

                //only consider non decoy and validated proteins
                try {
                    if (!ProteinMatch.isDecoy(proteinMatchKey) && proteinGroupScore.getMatchValidationLevel().isValidated()) {
                        ProteinGroup proteinGroup = new ProteinGroup();

                        //map the Utilities ProteinMatch instance onto the ProteinGroup instance
                        utilitiesProteinGroupMapper.map(proteinMatch, proteinGroupScore, proteinGroup);

                        //add the protein group to the map
                        proteinGroups.add(proteinGroup);

                        //iterate over the peptide matches
                        PeptideMatchesIterator peptideMatchesIterator = identification.getPeptideMatchesIterator(proteinMatch.getPeptideMatchesKeys(), parameters, false, parameters, null);
                        PeptideMatch peptideMatch;
                        while ((peptideMatch = peptideMatchesIterator.next()) != null) {
                            String peptideMatchKey = peptideMatch.getKey();

                            PSParameter peptideScore = new PSParameter();
                            peptideScore = (PSParameter) identification.getPeptideMatchParameter(peptideMatchKey, peptideScore);

                            //iterate over the spectrum matches
                            PsmIterator psmIterator = identification.getPsmIterator(peptideMatch.getSpectrumMatchesKeys(), parameters, true, null);
                            SpectrumMatch spectrumMatch;
                            while ((spectrumMatch = psmIterator.next()) != null) {
                                String spectrumMatchKey = spectrumMatch.getKey();

                                PSParameter spectrumScore = new PSParameter();
                                spectrumScore = (PSParameter) identification.getSpectrumMatchParameter(spectrumMatchKey, spectrumScore);

                                //only consider validated PSMs
                                if (spectrumScore.getMatchValidationLevel().isValidated()) {
                                    PeptideHasProteinGroup peptideHasProteinGroup = new PeptideHasProteinGroup();
                                    //set peptide scores in PeptideHasProteinGroup entity
                                    peptideHasProteinGroup.setPeptideProbability(peptideScore.getPeptideProbabilityScore());
                                    peptideHasProteinGroup.setPeptidePostErrorProbability(peptideScore.getPeptideProbability());

                                    //set entity associations between PeptideHasProteinGroup and ProteinGroup entities
                                    peptideHasProteinGroup.setProteinGroup(proteinGroup);
                                    proteinGroup.getPeptideHasProteinGroups().add(peptideHasProteinGroup);

                                    //map spectrum
                                    Spectrum targetSpectrum = new Spectrum();
                                    //@todo get fragmentation type from PeptideShaker
                                    utilitiesSpectrumMapper.map((MSnSpectrum) spectrumFactory.getSpectrum(spectrumMatchKey), null, targetSpectrum);

                                    //map peptide
                                    Peptide targetPeptide = new Peptide();
                                    utilitiesPeptideMapper.map(spectrumMatch, spectrumScore, targetPeptide);

                                    //set entity associations between AnalyticalRun and Spectrum entities
                                    analyticalRun.getSpectrums().add(targetSpectrum);
                                    targetSpectrum.setAnalyticalRun(analyticalRun);

                                    //set entity associations between Spectrum and Peptide entities
                                    targetSpectrum.getPeptides().add(targetPeptide);
                                    targetPeptide.setSpectrum(targetSpectrum);

                                    //set entity associations between Peptide and PeptideHasProteinGroup entities
                                    targetPeptide.getPeptideHasProteinGroups().add(peptideHasProteinGroup);
                                    peptideHasProteinGroup.setPeptide(targetPeptide);
                                }
                            }
                        }
                    }
                }
                catch (NullPointerException e){
                    System.out.println("-------");
                }
            }
            analyticalRuns.add(analyticalRun);

            LOGGER.info("Finished mapping PeptideShaker experiment " + dataImport.getCpsParent().getExperiment().getReference());
        } catch (IOException | SQLException | ClassNotFoundException | InterruptedException | IllegalArgumentException | MzMLUnmarshallerException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new MappingException(ex.getMessage(), ex);
        }

        return new MappedData(analyticalRuns, proteinGroups);
    }

    /**
     * Map the search settings.
     *
     * @param unpackedPeptideShakerImport the UnpackedPeptideShakerImport instance
     * @param analyticalRun               the AnalyticalRun instance onto the search settings will be mapped
     * @param fastaDb                     the FastaDb instance retrieved from the database
     * @param proteinScoreType            the protein target-decoy scoring strategy
     * @param proteinThreshold            the protein score threshold
     * @return the mapped search and validation settings
     */
    private SearchAndValidationSettings mapSearchSettings(final UnpackedPeptideShakerImport unpackedPeptideShakerImport, final AnalyticalRun analyticalRun, final FastaDb fastaDb, final ScoreType proteinScoreType, final Double proteinThreshold) {
        SearchAndValidationSettings searchAndValidationSettings;

        CpsParent cpsParent = unpackedPeptideShakerImport.getCpsParent();
        String version = cpsParent.getProjectDetails().getPeptideShakerVersion();

        EnumMap<FastaDbType, FastaDb> fastaDbs = new EnumMap<>(FastaDbType.class);
        fastaDbs.put(FastaDbType.PRIMARY, fastaDb);

        searchAndValidationSettings = utilitiesSearchSettingsMapper.map(SearchEngineType.PEPTIDESHAKER, version, fastaDbs, cpsParent.getIdentificationParameters(), proteinScoreType, proteinThreshold, false);

        //set entity associations
        analyticalRun.setSearchAndValidationSettings(searchAndValidationSettings);
        searchAndValidationSettings.setAnalyticalRun(analyticalRun);

        return searchAndValidationSettings;
    }
}
