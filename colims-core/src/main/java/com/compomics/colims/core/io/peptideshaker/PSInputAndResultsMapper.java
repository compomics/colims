package com.compomics.colims.core.io.peptideshaker;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.utilities_to_colims.UtilitiesPsmMapper;
import com.compomics.colims.core.io.utilities_to_colims.UtilitiesSpectrumMapper;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.IdentificationFile;
import com.compomics.colims.model.SearchAndValidationSettings;
import com.compomics.colims.model.Spectrum;
import com.compomics.util.db.ObjectsCache;
import com.compomics.util.experiment.MsExperiment;
import com.compomics.util.experiment.identification.SequenceFactory;
import com.compomics.util.experiment.identification.identifications.Ms2Identification;
import com.compomics.util.experiment.identification.matches.SpectrumMatch;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.SpectrumFactory;
import eu.isas.peptideshaker.myparameters.PSParameter;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import eu.isas.peptideshaker.utils.CpsParent;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

/**
 * This class maps the search input and identification results of a PeptideShaker project to a list of analytical runs.
 *
 * @author Niels Hulstaert
 */
@Component("psInputAndResultMapper")
public class PSInputAndResultsMapper {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(PSInputAndResultsMapper.class);
    private static final String ANALYTICAL_RUN_NAME_SEPARATOR = ":";
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
     * Map the identification results to a list of analytical runs.
     *
     * @param searchAndValidationSettings the SearchAndValidationSettings
     * @param unpackedPsDataImport        the UnpackedPeptideShakerImport
     * @return the list of mapped analytical runs
     * @throws IOException               thrown in case of an IO related problem
     * @throws SQLException              thrown in case of an SQL related problem
     * @throws ClassNotFoundException    thrown in case of a failure to load a class by it's string name.
     * @throws InterruptedException      thrown in case of an interrupted thread problem
     * @throws MappingException          thrown in case of a mapping related problem
     * @throws MzMLUnmarshallerException thrown in case of a MzMl parsing problem
     */
    public List<AnalyticalRun> map(final SearchAndValidationSettings searchAndValidationSettings, final UnpackedPeptideShakerImport unpackedPsDataImport) throws IOException, SQLException, ClassNotFoundException, InterruptedException, MzMLUnmarshallerException, MappingException {
        //the analytical runs onto the utilities replicates will be mapped
        List<AnalyticalRun> analyticalRuns = new ArrayList<>();

        //get the CpsParent instance for accessing the .cps file
        CpsParent cpsParent = unpackedPsDataImport.getCpsParent();
        //get the MsExperiment instance
        MsExperiment msExperiment = cpsParent.getExperiment();
        //get the Ms2Identification instance
        Ms2Identification identification = (Ms2Identification) cpsParent.getIdentification();

        LOGGER.info("Start mapping PeptideShaker experiment " + msExperiment.getReference() + " on domain model Experiment class");

        IdentificationFile identificationFile = searchAndValidationSettings.getIdentificationFiles().get(0);

        //load the fasta file
        File fastaDbFile = new File(unpackedPsDataImport.getFastaDb().getFilePath());
        cpsParent.loadFastaFile(fastaDbFile, null);

        //load the spectrum files, peptide en protein matches
        cpsParent.loadSpectrumFiles(null);
        loadPeptideMatches(identification);
        loadProteinMatches(identification);

        //init the UtilitiesPsmMapper
        utilitiesPsmMapper.init(identification, cpsParent.getIdentificationParameters(), identificationFile);

        //We don't need to iterate over the samples in the .cps file because
        //for the moment, because PeptideShaker .cps files contain only one sample.
        //We don't need to iterate over the replicates/analytical runs in the .cps file
        //for the moment, because there's only one replicate per sample.
        //instantiate new analytical run
        AnalyticalRun analyticalRun = new AnalyticalRun();
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

        LOGGER.info("Finished mapping PeptideShaker experiment " + unpackedPsDataImport.getCpsParent().getExperiment().getReference() + " on a list of analytical runs");

        return analyticalRuns;
    }

    /**
     * Clear the mapping resources: reset the SpectrumFactory, SequenceFactory, ...
     *
     * @throws java.io.IOException   thrown in case of an IO related problem
     * @throws java.sql.SQLException thrown in case of an SQL related problem
     */
    public void clear() throws IOException, SQLException {
        spectrumFactory.clearFactory();
        sequenceFactory.clearFactory();
        //@todo Check if we need to clear the ObjectsCache because we started using the CpsParent class for accessing the .cps file
//        objectsCache = new ObjectsCache();
//        objectsCache.setAutomatedMemoryManagement(true);
        utilitiesPsmMapper.clear();
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
