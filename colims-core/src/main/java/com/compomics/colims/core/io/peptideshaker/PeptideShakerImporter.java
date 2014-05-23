package com.compomics.colims.core.io.peptideshaker;


import com.compomics.colims.core.io.DataImport;
import com.compomics.colims.core.io.DataImporter;
import org.apache.log4j.Logger;

import com.compomics.colims.core.io.SearchSettingsMapper;
import com.compomics.colims.model.AnalyticalRun;
import com.compomics.colims.model.QuantificationSettings;
import com.compomics.colims.model.SearchAndValidationSettings;
import com.compomics.util.experiment.MsExperiment;
import com.compomics.util.preferences.GenePreferences;
import com.compomics.util.preferences.IdFilter;
import com.compomics.util.preferences.PTMScoringPreferences;
import com.compomics.util.preferences.ProcessingPreferences;
import eu.isas.peptideshaker.myparameters.PSSettings;
import eu.isas.peptideshaker.myparameters.PeptideShakerSettings;
import java.io.File;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Niels Hulstaert
 */
@Component("peptideShakerImporter")
//@Transactional
public class PeptideShakerImporter implements DataImporter {

    private static final Logger LOGGER = Logger.getLogger(PeptideShakerImporter.class);
    @Autowired
    private SearchSettingsMapper searchSettingsMapper;
    @Autowired
    private PSInputAndResultsMapper inputAndResultsMapper;
    
    public SearchAndValidationSettings mapSearchAndValidationSettings(File peptideShakerCpsArchive, UnpackedPeptideShakerImport unpackedPsDataImport) {
        SearchAndValidationSettings searchAndValidationSettings = new SearchAndValidationSettings();

        //load experiment settings
        PeptideShakerSettings peptideShakerSettings = loadExperimentSettings(unpackedPsDataImport.getMsExperiment());
        String version = peptideShakerSettings.getProjectDetails().getPeptideShakerVersion();

        return searchAndValidationSettings;
    }    

    /**
     * Load the PeptideShaker settings.
     *
     * @param msExperiment
     */
    private PeptideShakerSettings loadExperimentSettings(MsExperiment msExperiment) {
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
                    tempPTMScoringPreferences, new GenePreferences(), new IdFilter());

        } else {
            peptideShakerSettings = (PeptideShakerSettings) msExperiment.getUrParam(peptideShakerSettings);
        }

        return peptideShakerSettings;
    }

    @Override
    public SearchAndValidationSettings importSearchSettings(DataImport dataImport) {        
        SearchAndValidationSettings searchAndValidationSettings = null;
        
//        searchSettingsMapper.
        
        return searchAndValidationSettings;
    }

    @Override
    public QuantificationSettings importQuantSettings() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<AnalyticalRun> importInputAndResults(SearchAndValidationSettings searchAndValidationSettings, QuantificationSettings quantificationSettings) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
