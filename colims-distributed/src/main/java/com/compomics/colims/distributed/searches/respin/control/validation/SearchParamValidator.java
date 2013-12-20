/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.distributed.searches.respin.control.validation;

import com.compomics.util.experiment.biology.EnzymeFactory;
import com.compomics.util.experiment.identification.SearchParameters;
import com.compomics.util.experiment.massspectrometry.Charge;
import java.util.ArrayList;
import org.apache.log4j.Logger;

/**
 *
 * @author Kenneth
 */
public class SearchParamValidator {

    private static final Logger LOGGER = Logger.getLogger(SearchParamValidator.class);
    private static final EnzymeFactory enzymeFactory = EnzymeFactory.getInstance();

    public SearchParamValidator() {
    }

    public static boolean validate(SearchParameters searchParameters) {
        // ======================================================EVALUATE ENZYME
        if (searchParameters.getEnzyme() == null) {
            LOGGER.debug("Enzyme was null!");
            searchParameters.setEnzyme(enzymeFactory.getEnzyme("Trypsin"));
        }
        LOGGER.debug("Using enzyme : " + searchParameters.getEnzyme().getName());

// ======================================================EVALUATE FASTA

        if (searchParameters.getFastaFile() == null) {
            LOGGER.debug("Fasta was null!");
            return false;
        }
        LOGGER.debug("Using Fasta : " + searchParameters.getFastaFile().getAbsolutePath());

// ======================================================EVALUATE FRAGMENTIONACC       
        if (searchParameters.getFragmentIonAccuracy() == null) {
            LOGGER.debug("Fragment Ion Accuracy was null");
            searchParameters.setFragmentIonAccuracy(1.0);
        }
        LOGGER.debug("Using Fragment Ion Accuracy : " + searchParameters.getFragmentIonAccuracy());

// ======================================================EVALUATE PRECURSORACC   
        if (searchParameters.getPrecursorAccuracy() == null) {
            LOGGER.debug("Precursor Accuracy was null!");
            searchParameters.setPrecursorAccuracy(1.0);
        }
        LOGGER.debug("Using Precursor Accuracy : " + searchParameters.getPrecursorAccuracy());

// ======================================================EVALUATE HITLISTLENGTH          
        if (searchParameters.getHitListLength() == null) {
            LOGGER.debug("Hitlist length was null!");
            searchParameters.setHitListLength(10);
        }
        LOGGER.debug("Using Hitlist length : " + searchParameters.getHitListLength());

// ======================================================EVALUATE IONS SEARCHED  

        if (searchParameters.getIonSearched1() == null) {
            LOGGER.debug("Ion 1 was null!");
            searchParameters.setIonSearched1("");
        }
        LOGGER.debug("Using Ion 1 searched : " + searchParameters.getIonSearched1());
        if (searchParameters.getIonSearched2() == null) {
            LOGGER.debug("Ion 2 was null!");
            searchParameters.setIonSearched2("");
        }
        LOGGER.debug("Using Ion 2 searched : " + searchParameters.getIonSearched2());

// ======================================================EVALUATE E VALUE 
        if (searchParameters.getMaxEValue() == null) {
            LOGGER.debug("Max E-value was null!");
            searchParameters.setMaxEValue(100.0);
        }
        LOGGER.debug("Using Max E-value : " + searchParameters.getMaxEValue());

// ======================================================EVALUATE MAX PEPTIDE LENGTH

        if (searchParameters.getMaxPeptideLength() == null) {
            LOGGER.debug("Max peptide length was null!");
            searchParameters.setMaxPeptideLength(30);
        }
        LOGGER.debug("Using Max Peptide Length : " + searchParameters.getMaxPeptideLength());

// ====================================================EVALUATE VARIABLE MISSED CLEAVAGES     

        if (searchParameters.getnMissedCleavages() == null) {
            LOGGER.debug("Missed Cleavages was null!");
            searchParameters.setnMissedCleavages(2);
        }
//TODO GET THIS FROM PRIDE ASAP?
        searchParameters.setnMissedCleavages(2);

        LOGGER.debug("Using Missed Cleavages : " + searchParameters.getnMissedCleavages());

// ====================================================EVALUATE CHARGES    
        if (searchParameters.getMaxChargeSearched() == null) {
            LOGGER.debug("Max Charge Searched was null!");
            searchParameters.setMaxChargeSearched(new Charge(1, 4));
        }
        LOGGER.debug("Using Max Charge : " + searchParameters.getMaxChargeSearched());

        if (searchParameters.getMinChargeSearched() == null) {
            LOGGER.debug("Min Charge Searched was null!");
            searchParameters.setMinChargeSearched(new Charge(1, 1));
        }
        LOGGER.debug("Using Min Charge : " + searchParameters.getMinChargeSearched());

// ======================================================EVALUATE FIXED MODIFICATIONS

        ArrayList<String> fixModList = searchParameters.getModificationProfile().getFixedModifications();
        LOGGER.debug("Using Fixed Mod profile : ");
        for (String aMod : fixModList) {
            LOGGER.debug("Fixed Modification : " + aMod);
        }
// ====================================================EVALUATE VARIABLE MODIFICATIONS
        ArrayList<String> varModList = searchParameters.getModificationProfile().getVariableModifications();
        LOGGER.debug("Using Variable Mod profile : ");
        for (String aMod : varModList) {
            LOGGER.debug("Var Modification : " + aMod);
        }
        LOGGER.debug("Searchparameters were validated");
        return true;
    }
}
