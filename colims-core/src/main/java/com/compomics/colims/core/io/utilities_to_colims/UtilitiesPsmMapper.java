package com.compomics.colims.core.io.utilities_to_colims;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.MatchScore;
import com.compomics.colims.model.IdentificationFile;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.Spectrum;
import com.compomics.util.experiment.identification.PeptideAssumption;
import com.compomics.util.experiment.identification.identifications.Ms2Identification;
import com.compomics.util.experiment.identification.matches.PeptideMatch;
import com.compomics.util.experiment.identification.matches.ProteinMatch;
import com.compomics.util.experiment.identification.matches.SpectrumMatch;
import com.compomics.util.preferences.IdentificationParameters;
import eu.isas.peptideshaker.myparameters.PSParameter;
import eu.isas.peptideshaker.myparameters.PSPtmScores;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class maps the Utilities PSM input to a Colims spectrum entity and related classes (Peptide, Protein, ...).
 *
 * @author Niels Hulstaert
 */
@Component("utilitiesPsmMapper")
public class UtilitiesPsmMapper {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(UtilitiesPsmMapper.class);
    /**
     * The Compomics Utilities Ms2Identification instance.
     */
    private Ms2Identification identification;
    /**
     * The Compomics Utilities identificationParameters. It contains the parameters used for protein identification.
     */
    private IdentificationParameters identificationParameters;
    /**
     * The IdentificationFile instance for setting to entities relations.
     */
    private IdentificationFile identificationFile;
    /**
     * The Compomics Utilities to Colims peptide mapper.
     */
    @Autowired
    private UtilitiesPeptideMapper utilitiesPeptideMapper;
    /**
     * The Compomics Utilities to Colims protein mapper.
     */
    @Autowired
    private UtilitiesProteinMapper utilitiesProteinMapper;

    public IdentificationParameters getIdentificationParameters() {
        return identificationParameters;
    }

    /**
     * Initialize the mapper; set the Ms2Identification, the IdentificationParameters, IdentificationFile instances and
     * the peptide and protein matches.
     *
     * @param identification           the Ms2Identification instance
     * @param identificationParameters contains the parameters used for protein identification
     * @param identificationFile       the identification file
     */
    public void init(final Ms2Identification identification, final IdentificationParameters identificationParameters, final IdentificationFile identificationFile) {
        this.identification = identification;
        this.identificationParameters = identificationParameters;
        this.identificationFile = identificationFile;
    }

    /**
     * Map the Utilities psm input to a Colims spectrum entity and related classes.
     *
     * @param spectrumMatch  the SpectrumMatch
     * @param targetSpectrum the Colims spectrum entity
     * @throws SQLException           thrown in case of an SQL related problem
     * @throws IOException            thrown in case of an IO related problem
     * @throws ClassNotFoundException thrown in case of a failure to load a class by it's string name.
     * @throws InterruptedException   thrown in case of an interrupted thread problem
     * @throws MappingException       thrown in case of a mapping related problem
     */
    public void map(final SpectrumMatch spectrumMatch, final Spectrum targetSpectrum) throws SQLException, IOException, ClassNotFoundException, InterruptedException, MappingException {
        if (spectrumMatch.getBestPeptideAssumption() != null) {
            //get best assumption
            PeptideAssumption peptideAssumption = spectrumMatch.getBestPeptideAssumption();
            com.compomics.util.experiment.biology.Peptide bestMatchingPeptide = peptideAssumption.getPeptide();
            String bestMatchingPeptideKey = bestMatchingPeptide.getMatchingKey(identificationParameters.getSequenceMatchingPreferences());

            //instantiate the Colims Peptide instance
            Peptide targetPeptide = new Peptide();

            //get psm and peptide parameters
            PSParameter psmParameter = (PSParameter) identification.getSpectrumMatchParameter(spectrumMatch.getKey(), new PSParameter());
            PSParameter peptideParameter = (PSParameter) identification.getPeptideMatchParameter(bestMatchingPeptideKey, new PSParameter());

            MatchScore psmMatchScore = new MatchScore(psmParameter.getPsmProbabilityScore(), psmParameter.getPsmProbability());
            PSPtmScores psmPtmScores = null;
            PSPtmScores peptidePtmScores = null;

            if (spectrumMatch.getUrParam(new PSPtmScores()) != null) {
                psmPtmScores = (PSPtmScores) spectrumMatch.getUrParam(new PSPtmScores());
            }

            PeptideMatch peptideMatch = identification.getPeptideMatch(bestMatchingPeptideKey);
            if (peptideMatch != null) {
                if (peptideMatch.getUrParam(new PSPtmScores()) != null) {
                    peptidePtmScores = (PSPtmScores) peptideMatch.getUrParam(new PSPtmScores());
                    if (peptidePtmScores != null) {
                        System.out.printf("test");
                    }
                }
            }

            utilitiesPeptideMapper.map(bestMatchingPeptide, psmMatchScore, psmPtmScores, peptideAssumption.getIdentificationCharge().value, targetPeptide);
            //set the relation between the IdentificationFile and Peptide entities
            targetPeptide.setIdentificationFile(identificationFile);

            //set entity relations
            targetSpectrum.getPeptides().add(targetPeptide);
            targetPeptide.setSpectrum(targetSpectrum);

            List<ProteinMatch> proteinMatches = new ArrayList<>();

//            ProteinMatch proteinMatch;
//            List<String> possibleProteins = new ArrayList();
//            for (String parentProtein : bestMatchingPeptide.getParentProteins(identificationParameters.getSequenceMatchingPreferences())) {
//                ArrayList<String> parentProteins = identification.getProteinMap().get(parentProtein);
//                if (parentProteins != null) {
//                    for (String proteinKey : parentProteins) {
//                        if (!possibleProteins.contains(proteinKey)) {
//                            try {
//                                proteinMatch = identification.getProteinMatch(proteinKey);
//                                if (proteinMatch.getPeptideMatchesKeys().contains(bestMatchingPeptideKey)) {
//                                    possibleProteins.add(proteinKey);
//                                }
//                            } catch (Exception e) {
//                                // protein deleted due to protein inference issue and not deleted from the map in versions earlier than 0.14.6
//                                System.out.println("Non-existing protein key in protein map: " + proteinKey);
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                }
//            }

            //iterate over protein keys
            //get parent proteins without remapping them
            //@todo this is the way to go for MaxQuant, but what about PeptideShaker?
            for (String proteinKey : bestMatchingPeptide.getParentProteins(identificationParameters.getSequenceMatchingPreferences())) {
                ProteinMatch proteinMatch = identification.getProteinMatch(proteinKey);
                if (proteinMatch != null) {
                    proteinMatches.add(proteinMatch);
                }
            }

            //map proteins
            MatchScore peptideMatchScore;
            if (peptideParameter != null) {
                peptideMatchScore = new MatchScore(peptideParameter.getPeptideProbabilityScore(), peptideParameter.getPeptideProbability());
            } else {
                LOGGER.info("No peptide match score found for peptide: " + targetPeptide.getSequence());
                peptideMatchScore = new MatchScore(0.0, 0.0);
            }

            utilitiesProteinMapper.map(proteinMatches, peptideMatchScore, targetPeptide);
        } else {
            LOGGER.debug("No best peptide assumption was found for spectrum match " + spectrumMatch.getKey());
        }
    }

    /**
     * Map the Utilities psm input to a Colims spectrum entity and related classes.
     *
     * @param ms2Identification  the Ms2Identification
     * @param identificationFile the identification file
     * @param spectrumMatch      the SpectrumMatch
     * @param targetSpectrum     the Colims spectrum entity
     * @throws SQLException           thrown in case of an SQL related problem
     * @throws IOException            thrown in case of an IO related problem
     * @throws ClassNotFoundException thrown in case of a failure to load a class by it's string name.
     * @throws InterruptedException   thrown in case of an interrupted thread problem
     * @throws MappingException       thrown in case of a mapping related problem
     */
    public void mapOld(final Ms2Identification ms2Identification, final IdentificationFile identificationFile, final SpectrumMatch spectrumMatch, final Spectrum targetSpectrum) throws SQLException, IOException, ClassNotFoundException, InterruptedException, MappingException {
        if (spectrumMatch.getBestPeptideAssumption() != null) {
            //get best assumption
            PeptideAssumption peptideAssumption = spectrumMatch.getBestPeptideAssumption();
            com.compomics.util.experiment.biology.Peptide sourcePeptide = peptideAssumption.getPeptide();

            //instantiate the Colims Peptide instance
            Peptide targetPeptide = new Peptide();

            PSParameter psmProbabilities = new PSParameter();
            PSParameter peptideProbabilities = new PSParameter();
//                PSParameter proteinProbabilities = new PSParameter();
            //get psm and peptide probabilities
            psmProbabilities = (PSParameter) ms2Identification.getSpectrumMatchParameter(spectrumMatch.getKey(), psmProbabilities);
            peptideProbabilities = (PSParameter) ms2Identification.getPeptideMatchParameter(sourcePeptide.getKey(), peptideProbabilities);

            MatchScore psmMatchScore = new MatchScore(psmProbabilities.getPsmProbabilityScore(), psmProbabilities.getPsmProbability());
            PSPtmScores ptmScores = null;
            if (spectrumMatch.getUrParam(new PSPtmScores()) != null) {
                ptmScores = (PSPtmScores) spectrumMatch.getUrParam(new PSPtmScores());
            }
            utilitiesPeptideMapper.map(sourcePeptide, psmMatchScore, ptmScores, peptideAssumption.getIdentificationCharge().value, targetPeptide);
            //link the IdentificationFile to the peptide
            targetPeptide.setIdentificationFile(identificationFile);

            //set entity relations
            targetSpectrum.getPeptides().add(targetPeptide);
            targetPeptide.setSpectrum(targetSpectrum);

            List<ProteinMatch> proteinMatches = new ArrayList<>();
            //iterate over protein keys
            //get parent proteins without remapping them
            //@todo this is the way to go for maxquant, but what about peptideshaker?
            for (String proteinKey : sourcePeptide.getParentProteinsNoRemapping()) {
                ProteinMatch proteinMatch = ms2Identification.getProteinMatch(proteinKey);
                if (proteinMatch != null) {
                    proteinMatches.add(proteinMatch);
                }
            }

            //map proteins
            MatchScore peptideMatchScore;
            if (peptideProbabilities != null) {
                peptideMatchScore = new MatchScore(peptideProbabilities.getPeptideProbabilityScore(), peptideProbabilities.getPeptideProbability());
            } else {
                LOGGER.info("No peptide match score found for peptide: " + targetPeptide.getSequence());
                peptideMatchScore = new MatchScore(0.0, 0.0);
            }

            utilitiesProteinMapper.map(proteinMatches, peptideMatchScore, targetPeptide);
        } else {
            LOGGER.debug("No best peptide assumption was found for spectrum match " + spectrumMatch.getKey());
        }
    }

    /**
     * Clear resources.
     */
    public void clear() {
        //set class variables to null
        identification = null;
        identificationParameters = null;
        identificationFile = null;
        //call clear methods on child mappers
        utilitiesPeptideMapper.clear();
        utilitiesProteinMapper.clear();
    }

}
