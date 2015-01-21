package com.compomics.colims.core.io.peptideshaker;

import com.compomics.colims.core.io.DataImporter;
import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.SearchSettingsMapper;
import com.compomics.colims.core.io.utilities_to_colims.UtilitiesPsmMapper;
import com.compomics.colims.core.io.utilities_to_colims.UtilitiesSpectrumMapper;
import com.compomics.colims.model.*;
import com.compomics.colims.model.enums.SearchEngineType;
import com.compomics.util.experiment.MsExperiment;
import com.compomics.util.experiment.identification.SequenceFactory;
import com.compomics.util.experiment.identification.identifications.Ms2Identification;
import com.compomics.util.experiment.identification.matches.SpectrumMatch;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.SpectrumFactory;
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
     * The Compomics Utilities to Colims PSM mapper.
     */
    @Autowired
    private UtilitiesPsmMapper utilitiesPsmMapper;
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
     * @throws java.io.IOException   thrown in case of an IO related problem
     * @throws java.sql.SQLException thrown in case of an SQL related problem
     */
    @Override
    public void clear() throws IOException, SQLException {
        spectrumFactory.clearFactory();
        sequenceFactory.clearFactory();
        //@todo Check if we need to clear the ObjectsCache because we started using the CpsParent class for accessing the .cps file
        //objectsCache = new ObjectsCache();
        //objectsCache.setAutomatedMemoryManagement(true);
        utilitiesPsmMapper.clear();
    }

    @Override
    public List<AnalyticalRun> importData(UnpackedPeptideShakerImport dataImport) throws MappingException {
        //the analytical runs onto the utilities replicates will be mapped
        List<AnalyticalRun> analyticalRuns = new ArrayList<>();

        try {
            AnalyticalRun analyticalRun = new AnalyticalRun();

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

            LOGGER.info("Start mapping PeptideShaker experiment " + msExperiment.getReference() + " on domain model Experiment class");

            //load the fasta file
            File fastaDbFile = new File(dataImport.getFastaDb().getFilePath());
            cpsParent.loadFastaFile(fastaDbFile, null);

            //load the spectrum files, peptide en protein matches
            cpsParent.loadSpectrumFiles(null);
            loadPeptideMatches(identification);
            loadProteinMatches(identification);

            //init the UtilitiesPsmMapper
            IdentificationFile identificationFile = searchAndValidationSettings.getIdentificationFiles().get(0);
            utilitiesPsmMapper.init(identification, cpsParent.getIdentificationParameters(), identificationFile);

            //We don't need to iterate over the samples in the .cps file because
            //for the moment, because PeptideShaker .cps files contain only one sample.
            //We don't need to iterate over the replicates/analytical runs in the .cps file
            //for the moment, because there's only one replicate per sample.
            //instantiate new analytical run
            //@todo check if there is a more suitable candidate as accession number
            analyticalRun.setName(cpsParent.getSample().getReference() + ANALYTICAL_RUN_NAME_SEPARATOR + cpsParent.getReplicateNumber());

            //instantiate the spectrum list
            List<Spectrum> spectra = new ArrayList<>();
            //iterate over spectrum files
            for (String spectrumFileName : identification.getSpectrumFiles()) {
                //iterate over the spectrum identifications
                for (String psmKey : identification.getSpectrumIdentification(spectrumFileName)) {
                    //get spectrum by key
                    MSnSpectrum sourceSpectrum = (MSnSpectrum) spectrumFactory.getSpectrum(psmKey);

                    //instantiate the Colims spectrum entity
                    Spectrum targetSpectrum = new Spectrum();

                    //check if an identification match exists
                    boolean matchExists = identification.matchExists(psmKey);
                    if (matchExists) {
                        SpectrumMatch spectrumMatch = identification.getSpectrumMatch(psmKey);
                        PSParameter psmProbabilities = new PSParameter();
                        psmProbabilities = (PSParameter) identification.getSpectrumMatchParameter(spectrumMatch.getKey(), psmProbabilities);
                        //check if the psm has been validated
                        if (psmProbabilities.getMatchValidationLevel().isValidated()) {
                            utilitiesPsmMapper.map(spectrumMatch, targetSpectrum);
                        } else {
                            LOGGER.debug("The PSM was not validated for spectrum match " + spectrumMatch.getKey());
                        }
                    } else {
                        LOGGER.debug("No PSM was found for spectrum " + psmKey);
                    }

                    //map MSnSpectrum to model Spectrum
                    //@todo get fragmentation type from PeptideShaker
                    utilitiesSpectrumMapper.map(sourceSpectrum, null, targetSpectrum);
                    spectra.add(targetSpectrum);
                    //set entity relations
                    targetSpectrum.setAnalyticalRun(analyticalRun);
                }
            }

            analyticalRuns.add(analyticalRun);
            //set entity relations
            analyticalRun.setSpectrums(spectra);

            LOGGER.info("Finished mapping PeptideShaker experiment " + dataImport.getCpsParent().getExperiment().getReference() + " on a list of analytical runs");

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

        //set entity relations
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
            ms2Identification.loadSpectrumMatches(spectrumFileName, null);
            ms2Identification.loadSpectrumMatchParameters(spectrumFileName, new PSParameter(), null);
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
            ms2Identification.loadSpectrumMatches(spectrumFileName, null);
            ms2Identification.loadSpectrumMatchParameters(spectrumFileName, new PSParameter(), null);
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
        ms2Identification.loadPeptideMatches(null);
        ms2Identification.loadPeptideMatchParameters(new PSParameter(), null);
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
        ms2Identification.loadProteinMatches(null);
        ms2Identification.loadProteinMatchParameters(new PSParameter(), null);
    }

}
