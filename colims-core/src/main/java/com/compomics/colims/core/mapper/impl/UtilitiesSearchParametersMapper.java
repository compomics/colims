package com.compomics.colims.core.mapper.impl;

import com.compomics.colims.core.mapper.Mapper;

import org.apache.log4j.Logger;

import com.compomics.colims.core.exception.MappingException;
import com.compomics.colims.core.io.peptideshaker.model.PeptideShakerImport;
import com.compomics.colims.model.FastaDb;
import com.compomics.colims.model.SearchParameterSettings;
import com.compomics.util.experiment.MsExperiment;
import com.compomics.util.experiment.identification.SearchParameters;
import com.compomics.util.preferences.GenePreferences;
import com.compomics.util.preferences.IdFilter;
import com.compomics.util.preferences.PTMScoringPreferences;
import com.compomics.util.preferences.ProcessingPreferences;
import eu.isas.peptideshaker.myparameters.PSSettings;
import eu.isas.peptideshaker.myparameters.PeptideShakerSettings;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kenneth Verheggen
 */
@Component("utilitiesSearchParametersMapper")
@Transactional
public class UtilitiesSearchParametersMapper implements Mapper<PeptideShakerImport, SearchParameterSettings> {

    private static final Logger LOGGER = Logger.getLogger(UtilitiesSearchParametersMapper.class);
    /**
     * The PeptideShaker experiment settings
     */
    private PeptideShakerSettings experimentSettings;

    @Override
    public void map(PeptideShakerImport source, SearchParameterSettings target) throws MappingException {
        if (source == null || target == null) {
            throw new IllegalArgumentException("The source and/or target of the mapping are null");
        }
        try {
            LOGGER.info("Start mapping PeptideShaker experiment " + source.getMsExperiment().getReference() + " on domain model Experiment class");
            //get the MsExperiment object
            MsExperiment msExperiment = source.getMsExperiment();
            //load experiment settings
            experimentSettings = loadExperimentSettings(msExperiment);
            mapFromPeptideShakerSettings(experimentSettings, target);
        } catch (MappingException e) {
            LOGGER.error(e);
        }
    }

    /**
     * Map the PeptideShakerSettings to the Colims object.
     *
     * @param experimentSettings
     * @param target
     * @throws com.compomics.colims.core.exception.MappingException
     */
    public void mapFromPeptideShakerSettings(PeptideShakerSettings experimentSettings, SearchParameterSettings target) throws MappingException {
        if (experimentSettings == null || target == null) {
            throw new IllegalArgumentException("The source and/or target of the mapping are null");
        }
        try {
            SearchParameters parameters = experimentSettings.getSearchParameters();
            mapFromSearchParameters(parameters, target);
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    /**
     * Map the SearchParameters to the Colims object.
     *
     * @param SearchParameters
     * @return
     */
    private void mapFromSearchParameters(SearchParameters searchParameters, SearchParameterSettings colimsSearchParametersSettings) {
        FastaDb fastaDb = new FastaDb();
        fastaDb.setFileName(searchParameters.getFastaFile().getName());
        //TODO GET THE CHECKSUM
        colimsSearchParametersSettings.setEnzyme(searchParameters.getEnzyme().getName());
        colimsSearchParametersSettings.setEvalueCutoff(searchParameters.getMaxEValue());
        colimsSearchParametersSettings.setFastaDb(fastaDb);
        colimsSearchParametersSettings.setFragMassTolerance(searchParameters.getFragmentIonAccuracy());
        //TODO this might be wrong !!!!
        colimsSearchParametersSettings.setFragMassToleranceUnit(searchParameters.getPrecursorAccuracyType());
        colimsSearchParametersSettings.setFragmentIon1Type(searchParameters.getIonSearched1());
        colimsSearchParametersSettings.setFragmentIon2Type(searchParameters.getIonSearched2());
        colimsSearchParametersSettings.setHitlistLength(searchParameters.getHitListLength());
        colimsSearchParametersSettings.setMaxMissedCleavages(searchParameters.getnMissedCleavages());
        colimsSearchParametersSettings.setPrecMassTolerance(searchParameters.getPrecursorAccuracy());
        colimsSearchParametersSettings.setPrecMassToleranceUnit(searchParameters.getPrecursorAccuracyType());
        colimsSearchParametersSettings.setPrecursorUpperCharge((searchParameters.getMaxChargeSearched().value) * searchParameters.getMaxChargeSearched().sign);
        colimsSearchParametersSettings.setPrecursorLowerCharge((searchParameters.getMinChargeSearched().value) * searchParameters.getMinChargeSearched().sign);
        //TODO search engine settings?
    }

    /**
     * Load the PeptideShaker settings.
     *
     * @param msExperiment
     * @return
     */
    public PeptideShakerSettings loadExperimentSettings(MsExperiment experiment) {
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
