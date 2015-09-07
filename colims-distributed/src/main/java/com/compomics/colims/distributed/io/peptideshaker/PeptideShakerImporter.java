package com.compomics.colims.distributed.io.peptideshaker;

import com.compomics.colims.core.io.DataImporter;
import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.distributed.io.SearchSettingsMapper;
import com.compomics.colims.distributed.io.utilities_to_colims.UtilitiesPeptideMapper;
import com.compomics.colims.distributed.io.utilities_to_colims.UtilitiesProteinMapper;
import com.compomics.colims.distributed.io.utilities_to_colims.UtilitiesSpectrumMapper;
import com.compomics.colims.model.*;
import com.compomics.colims.model.enums.SearchEngineType;
import com.compomics.util.experiment.MsExperiment;
import com.compomics.util.experiment.identification.SequenceFactory;
import com.compomics.util.experiment.identification.identifications.Ms2Identification;
import com.compomics.util.experiment.identification.matches.PeptideMatch;
import com.compomics.util.experiment.identification.matches.ProteinMatch;
import com.compomics.util.experiment.identification.matches.SpectrumMatch;
import com.compomics.util.experiment.identification.matches_iterators.PeptideMatchesIterator;
import com.compomics.util.experiment.identification.matches_iterators.ProteinMatchesIterator;
import com.compomics.util.experiment.identification.matches_iterators.PsmIterator;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.SpectrumFactory;
import com.compomics.util.experiment.personalization.UrParameter;
import eu.isas.peptideshaker.myparameters.PSParameter;
import eu.isas.peptideshaker.utils.CpsParent;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * The DataImporter class for PeptideShaker projects.
 *
 * @author Niels Hulstaert
 */
@Component("peptideShakerImporter")
public class PeptideShakerImporter implements DataImporter<UnpackedPeptideShakerImport> {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(PeptideShakerImporter.class);
    private static final String ANALYTICAL_RUN_NAME_SEPARATOR = ":";
    /**
     * The utilities to Colims search settings mapper.
     */
    @Autowired
    private SearchSettingsMapper searchSettingsMapper;
    /**
     * The Compomics Utilities to Colims spectrum mapper.
     */
    @Autowired
    private UtilitiesSpectrumMapper utilitiesSpectrumMapper;
    /**
     * The Compomics Utilities to Colims protein mapper.
     */
    @Autowired
    private UtilitiesProteinMapper utilitiesProteinMapper;
    /**
     * The Compomics Utilities to Colims peptide mapper.
     */
    @Autowired
    private UtilitiesPeptideMapper utilitiesPeptideMapper;
    /**
     * Compomics Utilities spectrum factory.
     */
    private final SpectrumFactory spectrumFactory = SpectrumFactory.getInstance();
    /**
     * Compomics Utilities sequence factory.
     */
    private final SequenceFactory sequenceFactory = SequenceFactory.getInstance();

    /**
     * Clear the mapping resources: reset the SpectrumFactory, SequenceFactory, ...
     *
     * @throws IOException  thrown in case of an IO related problem
     * @throws SQLException thrown in case of an SQL related problem
     */
    @Override
    public void clear() throws IOException, SQLException {
        spectrumFactory.clearFactory();
        sequenceFactory.clearFactory();
        //@todo Check if we need to clear the ObjectsCache because we started using the CpsParent class for accessing the .cps file
        //objectsCache = new ObjectsCache();
        //objectsCache.setAutomatedMemoryManagement(true);
        //call clear methods on child mappers
        utilitiesPeptideMapper.clear();
        utilitiesProteinMapper.clear();
    }

    @Override
    public List<AnalyticalRun> importData(UnpackedPeptideShakerImport dataImport) throws MappingException {
        //the analytical runs onto the utilities replicates will be mapped
        List<AnalyticalRun> analyticalRuns = new ArrayList<>();

        try {
            AnalyticalRun analyticalRun = new AnalyticalRun();
            //set the path of the .cps archive
            analyticalRun.setStorageLocation(dataImport.getPeptideShakerCpsArchive().getCanonicalPath());

            //first, map the search settings
            //get the CpsParent instance for accessing the .cps file
            CpsParent cpsParent = dataImport.getCpsParent();
            //get the MsExperiment instance
            MsExperiment msExperiment = cpsParent.getExperiment();

            LOGGER.info("Start mapping search settings for PeptideShaker experiment " + msExperiment.getReference());

            SearchAndValidationSettings searchAndValidationSettings = mapSearchSettings(dataImport, analyticalRun);

            LOGGER.info("Finished mapping search settings for PeptideShaker experiment " + msExperiment.getReference());

            //get the Ms2Identification instance
            Ms2Identification identification = (Ms2Identification) cpsParent.getIdentification();

            LOGGER.info("Start mapping PeptideShaker experiment " + msExperiment.getReference());

            //load the fasta file
            File fastaDbFile = new File(dataImport.getFastaDb().getFilePath());
            cpsParent.loadFastaFile(fastaDbFile, null);

            //load the spectrum files, peptide en protein matches
            cpsParent.loadSpectrumFiles(null);
            loadPeptideMatches(identification);
            loadProteinMatches(identification);

            IdentificationFile identificationFile = searchAndValidationSettings.getIdentificationFiles().get(0);

            //We don't need to iterate over the samples in the .cps file because
            //for the moment, because PeptideShaker .cps files contain only one sample.
            //We don't need to iterate over the replicates/analytical runs in the .cps file
            //for the moment, because there's only one replicate per sample.
            //instantiate new analytical run
            //@todo check if there is a more suitable candidate as accession number
            analyticalRun.setName(cpsParent.getSample().getReference() + ANALYTICAL_RUN_NAME_SEPARATOR + cpsParent.getReplicateNumber());

            ArrayList<UrParameter> parameters = new ArrayList<>(1);
            parameters.add(new PSParameter());

            //iterate over the protein matches
            ProteinMatchesIterator proteinMatchesIterator = identification.getProteinMatchesIterator(parameters, true, parameters, true, parameters, null);
            while (proteinMatchesIterator.hasNext()) {
                ProteinMatch proteinMatch = proteinMatchesIterator.next();
                String proteinMatchKey = proteinMatch.getKey();

                PSParameter proteinGroupScore = new PSParameter();
                proteinGroupScore = (PSParameter) (identification.getProteinMatchParameter(proteinMatchKey, proteinGroupScore));

                //only consider non decoy and validated proteins
                if (!ProteinMatch.isDecoy(proteinMatchKey) && proteinGroupScore.getMatchValidationLevel().isValidated()) {
                    ProteinGroup proteinGroup = new ProteinGroup();

                    //map the Utilities ProteinMatch instance onto the ProteinGroup instance
                    utilitiesProteinMapper.map(proteinMatch, proteinGroupScore, proteinGroup);

                    //iterate over the peptide matches
                    PeptideMatchesIterator peptideMatchesIterator = identification.getPeptideMatchesIterator(proteinMatch.getPeptideMatchesKeys(), parameters, false, parameters, null);
                    while (peptideMatchesIterator.hasNext()) {
                        PeptideMatch peptideMatch = peptideMatchesIterator.next();
                        String peptideMatchKey = peptideMatch.getKey();

                        PSParameter peptideScore = new PSParameter();
                        peptideScore = (PSParameter) identification.getPeptideMatchParameter(peptideMatchKey, peptideScore);

                        //iterate over the spectrum matches
                        PsmIterator psmIterator = identification.getPsmIterator(peptideMatch.getSpectrumMatches(), parameters, true, null);
                        while (psmIterator.hasNext()) {
                            SpectrumMatch spectrumMatch = psmIterator.next();
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

                                //set entity associations the IdentificationFile and Peptide entities
                                targetPeptide.setIdentificationFile(identificationFile);

                                //set entity associations between Peptide and PeptideHasProteinGroup entities
                                targetPeptide.getPeptideHasProteinGroups().add(peptideHasProteinGroup);
                                peptideHasProteinGroup.setPeptide(targetPeptide);
                            }
                        }
                    }
                }
            }

            analyticalRuns.add(analyticalRun);

            LOGGER.info("Finished mapping PeptideShaker experiment " + dataImport.getCpsParent().getExperiment().getReference());
        } catch (IOException | SQLException | ClassNotFoundException | InterruptedException | IllegalArgumentException | MzMLUnmarshallerException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new MappingException(ex.getMessage(), ex);
        }

        return analyticalRuns;
    }

    /**
     * Map the search settings.
     *
     * @param unpackedPeptideShakerImport the UnpackedPeptideShakerImport instance
     * @param analyticalRun               the AnalyticalRun instance onto the search settings will be mapped
     * @return the mapped search and validation settings
     * @throws IOException thrown in case of an I/O related problem
     */
    private SearchAndValidationSettings mapSearchSettings(final UnpackedPeptideShakerImport unpackedPeptideShakerImport, final AnalyticalRun analyticalRun) throws IOException {
        SearchAndValidationSettings searchAndValidationSettings;

        CpsParent cpsParent = unpackedPeptideShakerImport.getCpsParent();
        String version = cpsParent.getProjectDetails().getPeptideShakerVersion();

        List<File> identificationFiles = new ArrayList<>();
        identificationFiles.add(unpackedPeptideShakerImport.getPeptideShakerCpsArchive());
        searchAndValidationSettings = searchSettingsMapper.map(SearchEngineType.PEPTIDESHAKER, version, unpackedPeptideShakerImport.getFastaDb(), cpsParent.getIdentificationParameters().getSearchParameters(), identificationFiles, false);

        //set entity associations
        analyticalRun.setSearchAndValidationSettings(searchAndValidationSettings);
        searchAndValidationSettings.setAnalyticalRun(analyticalRun);

        return searchAndValidationSettings;
    }

    /**
     * Load the spectrum matches from the Ms2Identification.
     *
     * @param ms2Identification the Ms2Identification
     * @param source            the unarchived PeptideShaker project
     * @throws SQLException           thrown in case of an SQL related problem
     * @throws IOException            thrown in case of an IO related problem
     * @throws ClassNotFoundException thrown in case of a failure to load a class by it's string name.
     * @throws InterruptedException   thrown in case of an interrupted thread problem
     */
    private void loadSpectrumMatches(final Ms2Identification ms2Identification, final UnpackedPeptideShakerImport source) throws SQLException, IOException, ClassNotFoundException, InterruptedException {
        for (String spectrumFileName : ms2Identification.getSpectrumFiles()) {
            loadSpectraFromMgfFile(source.getMgfFileByName(spectrumFileName));
            ms2Identification.loadSpectrumMatches(spectrumFileName, null, false);
            ms2Identification.loadSpectrumMatchParameters(spectrumFileName, new PSParameter(), null, false);
        }
    }

    /**
     * Load the spectrum matches from the Ms2Identification.
     *
     * @param ms2Identification the Ms2Identification
     * @param source            the unarchived PeptideShaker project
     * @throws SQLException           thrown in case of an SQL related problem
     * @throws IOException            thrown in case of an IO related problem
     * @throws ClassNotFoundException thrown in case of a failure to load a class by it's string name.
     * @throws InterruptedException   thrown in case of an interrupted thread problem
     */
    @Deprecated
    private void loadSpectrumMatchesOld(final Ms2Identification ms2Identification, final UnpackedPeptideShakerImport source) throws SQLException, IOException, ClassNotFoundException, InterruptedException {
        for (String spectrumFileName : ms2Identification.getSpectrumFiles()) {
            loadSpectraFromMgfFile(source.getMgfFileByName(spectrumFileName));
            ms2Identification.loadSpectrumMatches(spectrumFileName, null, false);
            ms2Identification.loadSpectrumMatchParameters(spectrumFileName, new PSParameter(), null, false);
        }
    }

    /**
     * Load spectra from a given mfg file in the Utilities SpectrumFactory.
     *
     * @param mgfFile the mgf file
     * @throws IOException thrown in case of an SQL related problem problem
     */
    private void loadSpectraFromMgfFile(final File mgfFile) throws IOException {
        LOGGER.debug("Start importing spectra from file " + mgfFile.getName() + " into the utilities SpectrumFactory.");
        spectrumFactory.addSpectra(mgfFile, null);
        LOGGER.debug("Finish importing spectra from file " + mgfFile.getName() + " into the utilities SpectrumFactory.");
    }

    /**
     * Load the peptide matches from the Ms2Identification.
     *
     * @param ms2Identification the Ms2Identification
     * @throws SQLException           thrown in case of an IO related problem
     * @throws IOException            thrown in case of an SQL related problem
     * @throws ClassNotFoundException thrown in case of a failure to load a class by it's string name.
     * @throws InterruptedException   thrown in case of an interrupted thread problem
     */
    private void loadPeptideMatches(final Ms2Identification ms2Identification) throws SQLException, IOException, ClassNotFoundException, InterruptedException {
        ms2Identification.loadPeptideMatches(null, false);
        ms2Identification.loadPeptideMatchParameters(new PSParameter(), null, false);
    }

    /**
     * Load the protein matches from the Ms2Identification.
     *
     * @param ms2Identification the Ms2Identification
     * @throws SQLException           thrown in case of an IO related problem
     * @throws IOException            thrown in case of an SQL related problem
     * @throws ClassNotFoundException thrown in case of a failure to load a class by it's string name.
     * @throws InterruptedException   thrown in case of an interrupted thread problem
     */
    private void loadProteinMatches(final Ms2Identification ms2Identification) throws SQLException, IOException, ClassNotFoundException, InterruptedException {
        ms2Identification.loadProteinMatches(null, false);
        ms2Identification.loadProteinMatchParameters(new PSParameter(), null, false);
    }

}
