package com.compomics.colims.core.mapper.impl.utilitiesToColims;

import com.compomics.colims.core.component.PtmFactoryWrapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.compomics.colims.core.exception.MappingException;
import com.compomics.colims.core.service.ModificationService;
import com.compomics.colims.core.service.OlsService;
import com.compomics.colims.model.Modification;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.PeptideHasModification;
import com.compomics.colims.model.enums.ModificationType;
import com.compomics.util.experiment.biology.PTM;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.compomics.util.pride.CvTerm;
import eu.isas.peptideshaker.myparameters.PSPtmScores;
import eu.isas.peptideshaker.scoring.PtmScoring;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("utilitiesModificationMapper")
public class UtilitiesModificationMapper {

    private static final Logger LOGGER = Logger.getLogger(UtilitiesModificationMapper.class);
    private static final String UNKNOWN_UTILITIES_PTM = "unknown";
    @Autowired
    private PtmCvTermMapper ptmCvTermMapper;
    @Autowired
    private ModificationService modificationService;
    @Autowired
    private OlsService olsService;
    /**
     * Wrapper around the utilities spectrum factory
     */
    @Autowired
    private PtmFactoryWrapper ptmFactoryWrapper;
    /**
     * The map of new modifications (key: modification name, value: the
     * modification)
     */
    private Map<String, Modification> newModifications = new HashMap<>();

    /**
     * Map the utilities modification matches onto the colims peptide. The
     * utilities PTMs are matched first onto CV terms from PSI-MOD.
     *
     * @param modificationMatches the list of modification matches
     * @param ptmScores the PeptideShaker PTM scores
     * @param targetPeptide the colims target peptide
     * @throws MappingException
     */
    public void map(ArrayList<ModificationMatch> modificationMatches, PSPtmScores ptmScores, Peptide targetPeptide) throws MappingException {
        List<PeptideHasModification> peptideHasModifications = new ArrayList<>();

        //iterate over modification matches
        for (ModificationMatch modificationMatch : modificationMatches) {
            //try to find a mapped CV term
            CvTerm cvTerm = ptmCvTermMapper.getCvTerm(modificationMatch.getTheoreticPtm());

            Modification modification;
            if (cvTerm != null) {
                modification = mapModificationMatch(cvTerm);
            } else {
                modification = mapModificationMatch(modificationMatch);
            }

            //set entity relations if modification could be mapped
            if (modification != null) {
                PeptideHasModification peptideHasModification = new PeptideHasModification();

                //set location in the PeptideHasModification join table
                //substract one because the modification site in the ModificationMatch class starts from 1
                Integer location = modificationMatch.getModificationSite() - 1;
                peptideHasModification.setLocation(location);

                //set modification type
                if (modificationMatch.isVariable()) {
                    peptideHasModification.setModificationType(ModificationType.VARIABLE);

                    if (ptmScores != null && ptmScores.getPtmScoring(modificationMatch.getTheoreticPtm()) != null) {
                        String alphaLocationKeys = ptmScores.getPtmScoring(modificationMatch.getTheoreticPtm()).getBestAScoreLocations();
                        if (alphaLocationKeys != null) {
                            Double alphaScore = ptmScores.getPtmScoring(modificationMatch.getTheoreticPtm()).getAScore(alphaLocationKeys);
                            peptideHasModification.setAlphaScore(alphaScore);
                            ArrayList<Integer> locations = PtmScoring.getLocations(alphaLocationKeys);
                            if (!locations.contains(modificationMatch.getModificationSite())) {
                                LOGGER.warn("The modification site " + modificationMatch.getModificationSite() + " is not found in the PtmScoring locations (" + alphaLocationKeys + ")");
                            }
                        }
                        String deltaLocationKeys = ptmScores.getPtmScoring(modificationMatch.getTheoreticPtm()).getBestDeltaScoreLocations();
                        if (deltaLocationKeys != null) {
                            Double deltaScore = ptmScores.getPtmScoring(modificationMatch.getTheoreticPtm()).getDeltaScore(deltaLocationKeys);
                            peptideHasModification.setDeltaScore(deltaScore);
                            ArrayList<Integer> locations = PtmScoring.getLocations(deltaLocationKeys);
                            if (!locations.contains(modificationMatch.getModificationSite())) {
                                LOGGER.warn("The modification site " + modificationMatch.getModificationSite() + " is not found in the PtmScoring locations (" + deltaLocationKeys + ")");
                            }
                        }
                    }
                } else {
                    peptideHasModification.setModificationType(ModificationType.FIXED);
                }

                peptideHasModifications.add(peptideHasModification);
                //set entity relations
                peptideHasModification.setModification(modification);
                peptideHasModification.setPeptide(targetPeptide);
            } else {
                LOGGER.error("The modification match " + modificationMatch.getTheoreticPtm() + " could not be mapped.");
                throw new MappingException("The modification match " + modificationMatch.getTheoreticPtm() + " could not be mapped.");
            }
        }

        if (!peptideHasModifications.isEmpty()) {
            targetPeptide.setPeptideHasModifications(peptideHasModifications);
        }
    }

    /**
     * Map the given ModificationMatch object to a Modification instance. Return
     * null if no mapping was possible.
     *
     * @param modificationMatch the utilities ModificationMatch
     * @return the colims Modification
     */
    private Modification mapModificationMatch(ModificationMatch modificationMatch) {
        Modification modification;

        //look for the modification in the newModifications map
        modification = newModifications.get(modificationMatch.getTheoreticPtm());

        if (modification == null) {
            //the modification was not found in the newModifications map    
            //look for the modification in the database by name          
            modification = modificationService.findByName(modificationMatch.getTheoreticPtm());

            if (modification == null) {
                //the modification was not found in the database
                //look for the modification in the PSI-MOD ontology by exact name
                modification = olsService.findModifiationByExactName(modificationMatch.getTheoreticPtm());

                if (modification == null) {
                    //the modification was not found by name in the PSI-MOD ontology
                    //@todo maybe search by mass or not by 'exact' name?
                    //get modification from modification factory
                    PTM ptM = ptmFactoryWrapper.getPtmFactory().getPTM(modificationMatch.getTheoreticPtm());

                    //check if the PTM is not unknown in the PTMFactory
                    if (!ptM.getName().equals(UNKNOWN_UTILITIES_PTM)) {
                        modification = new Modification(modificationMatch.getTheoreticPtm(), modificationMatch.getTheoreticPtm());

                        //@todo check if the PTM mass is the average or the monoisotopic mass shift
                        modification.setMonoIsotopicMassShift(ptM.getMass());

                        //add to newModifications
                        newModifications.put(modification.getAccession(), modification);
                    }
                }
            }
        }

        return modification;
    }

    /**
     * Map the given CvTerm utilities object to a Modification instance. Return
     * null if no mapping was possible.
     *
     * @param cvTerm the utilities CvTerm
     * @return the colims Modification entity
     */
    private Modification mapModificationMatch(CvTerm cvTerm) {
        Modification modification;

        //look for the modification in the newModifications map
        modification = newModifications.get(cvTerm.getAccession());

        if (modification == null) {
            //the modification was not found in the newModifications map    
            //look for the modification in the database by accession          
            modification = modificationService.findByAccession(cvTerm.getAccession());

            if (modification == null) {
                //the modification was not found in the database
                //look for the modification in the PSI-MOD ontology by accession                
                modification = olsService.findModifiationByAccession(cvTerm.getAccession());

                if (modification != null) {
                    newModifications.put(modification.getAccession(), modification);
                }
            }
        }

        return modification;
    }
}
