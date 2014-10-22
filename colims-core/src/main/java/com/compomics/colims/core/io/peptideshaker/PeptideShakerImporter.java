package com.compomics.colims.core.io.peptideshaker;

import com.compomics.colims.core.io.DataImport;
import com.compomics.colims.core.io.DataImporter;
import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.SearchSettingsMapper;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.QuantificationSettings;
import com.compomics.colims.model.SearchAndValidationSettings;
import com.compomics.colims.model.enums.SearchEngineType;
import com.compomics.util.experiment.MsExperiment;
import com.compomics.util.preferences.GenePreferences;
import com.compomics.util.preferences.IdFilter;
import com.compomics.util.preferences.PTMScoringPreferences;
import com.compomics.util.preferences.ProcessingPreferences;
import com.compomics.util.preferences.SequenceMatchingPreferences;
import eu.isas.peptideshaker.myparameters.PSSettings;
import eu.isas.peptideshaker.myparameters.PeptideShakerSettings;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

/**
 * The DataImporter class for PeptideShaker projects.
 *
 * @author Niels Hulstaert
 */
@Component("peptideShakerImporter")
public class PeptideShakerImporter implements DataImporter {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(PeptideShakerImporter.class);

    /**
     * The unarchived PeptideShaker import.
     */
    private UnpackedPeptideShakerImport unpackedPeptideShakerImport;
    /**
     * The utilities to colims search settings mapper.
     */
    @Autowired
    private SearchSettingsMapper searchSettingsMapper;
    /**
     * Maps the PeptideShaker search input and results to Colims classes.
     */
    @Autowired
    private PSInputAndResultsMapper inputAndResultsMapper;

    @Override
    public void initImport(final DataImport dataImport) {
        if (dataImport instanceof UnpackedPeptideShakerImport) {
            unpackedPeptideShakerImport = (UnpackedPeptideShakerImport) dataImport;
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void clear() {
        try {
            inputAndResultsMapper.clear();
        } catch (IOException | SQLException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    @Override
    public SearchAndValidationSettings importSearchSettings() throws MappingException {
        SearchAndValidationSettings searchAndValidationSettings = null;

        try {
            //load experiment settings
            PeptideShakerSettings peptideShakerSettings = loadExperimentSettings(unpackedPeptideShakerImport.getMsExperiment());
            String version = peptideShakerSettings.getProjectDetails().getPeptideShakerVersion();

            List<File> identificationFiles = new ArrayList<>();
            identificationFiles.add(unpackedPeptideShakerImport.getPeptideShakerCpsArchive());
            searchAndValidationSettings = searchSettingsMapper.map(SearchEngineType.PEPTIDESHAKER, version, unpackedPeptideShakerImport.getFastaDb(), peptideShakerSettings.getSearchParameters(), identificationFiles, false);
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new MappingException(ex.getMessage(), ex);
        }

        return searchAndValidationSettings;
    }

    @Override
    public QuantificationSettings importQuantSettings() throws MappingException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<AnalyticalRun> importInputAndResults(final SearchAndValidationSettings searchAndValidationSettings, final QuantificationSettings quantificationSettings) throws MappingException {
        List<AnalyticalRun> runs = null;

        try {
            runs = inputAndResultsMapper.map(searchAndValidationSettings, unpackedPeptideShakerImport);
        } catch (IOException | SQLException | ClassNotFoundException | InterruptedException | IllegalArgumentException | MzMLUnmarshallerException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new MappingException(ex.getMessage(), ex);
        }

        return runs;
    }

    /**
     * Load the PeptideShaker settings from the given MsExperiment.
     *
     * @param msExperiment the MsExperiment
     * @return the PeptideShakerSettings
     */
    private PeptideShakerSettings loadExperimentSettings(final MsExperiment msExperiment) {
        PeptideShakerSettings peptideShakerSettings = new PeptideShakerSettings();

        if (msExperiment.getUrParam(peptideShakerSettings) instanceof PSSettings) {

            // convert old settings files using utilities version 3.10.68 or older
            // convert the old ProcessingPreferences object
            PSSettings tempSettings = (PSSettings) msExperiment.getUrParam(peptideShakerSettings);
            ProcessingPreferences tempProcessingPreferences = new ProcessingPreferences();
            tempProcessingPreferences.setProteinFDR(tempSettings.getProcessingPreferences().getProteinFDR());
            tempProcessingPreferences.setPeptideFDR(tempSettings.getProcessingPreferences().getPeptideFDR());
            tempProcessingPreferences.setPsmFDR(tempSettings.getProcessingPreferences().getPsmFDR());

            // convert the old PTMScoringPreferences object
            PTMScoringPreferences tempPTMScoringPreferences = new PTMScoringPreferences();
            tempPTMScoringPreferences.setaScoreCalculation(tempSettings.getPTMScoringPreferences().aScoreCalculation());
            tempPTMScoringPreferences.setaScoreNeutralLosses(tempSettings.getPTMScoringPreferences().isaScoreNeutralLosses());
            tempPTMScoringPreferences.setFlrThreshold(tempSettings.getPTMScoringPreferences().getFlrThreshold());

            peptideShakerSettings = new PeptideShakerSettings(tempSettings.getSearchParameters(), tempSettings.getAnnotationPreferences(),
                    tempSettings.getSpectrumCountingPreferences(), tempSettings.getProjectDetails(), tempSettings.getFilterPreferences(),
                    tempSettings.getDisplayPreferences(),
                    tempSettings.getMetrics(), tempProcessingPreferences, tempSettings.getIdentificationFeaturesCache(),
                    tempPTMScoringPreferences, new GenePreferences(), new IdFilter(), new SequenceMatchingPreferences());
        } else {
            peptideShakerSettings = (PeptideShakerSettings) msExperiment.getUrParam(peptideShakerSettings);
        }

        return peptideShakerSettings;
    }

}
