/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.io.maxquant;

import com.compomics.colims.core.io.peptideshaker.UnpackedPsDataImport;
import com.compomics.colims.core.io.utilities_to_colims.UtilitiesSearchParametersMapper;
import com.compomics.util.experiment.MsExperiment;
import com.compomics.util.experiment.identification.SearchParameters;
import com.compomics.util.preferences.GenePreferences;
import com.compomics.util.preferences.IdFilter;
import com.compomics.util.preferences.PTMScoringPreferences;
import com.compomics.util.preferences.ProcessingPreferences;
import eu.isas.peptideshaker.myparameters.PSSettings;
import eu.isas.peptideshaker.myparameters.PeptideShakerSettings;
import org.apache.log4j.Logger;

/**
 *
 * @author Kenneth Verheggen
 */
public class PeptideShakerParameterParser {

    private static final Logger LOGGER = Logger.getLogger(UtilitiesSearchParametersMapper.class);
    /**
     * The PeptideShaker experiment settings
     */
    private PeptideShakerSettings experimentSettings;    
    
    /**
     * Parse the peptideshaker import file to utilities searchparameters.
     *
     * @param source
     * @return
     */
    public SearchParameters parseToSearchParameters(final UnpackedPsDataImport source) {
        if (source == null) {
            throw new IllegalArgumentException("The source and/or target of the mapping are null");
        }
        LOGGER.info("Start mapping PeptideShaker experiment " + source.getMsExperiment().getReference() + " on domain model Experiment class");
        //get the MsExperiment object
        MsExperiment msExperiment = source.getMsExperiment();
        //load experiment settings
        experimentSettings = loadExperimentSettings(msExperiment);
        return experimentSettings.getSearchParameters();
    }

    /**
     * Load the PeptideShaker settings.
     *
     * @param experiment
     * @return PeptideshakerSettings
     */
    public PeptideShakerSettings loadExperimentSettings(final MsExperiment experiment) {
        experimentSettings = new PeptideShakerSettings();
        
        if (experiment.getUrParam(experimentSettings) instanceof PSSettings) {
            // convert old settings files using utilities version 3.10.68 or older
            // convert the old ProcessingPreferences object
            PSSettings tempSettings = (PSSettings) experiment.getUrParam(experimentSettings);
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
            experimentSettings = (PeptideShakerSettings) experiment.getUrParam(experimentSettings);
        }

        return experimentSettings;
    }

}
