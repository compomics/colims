package com.compomics.colims.core.io.utilities_to_colims;

import com.compomics.colims.core.bean.PtmFactoryWrapper;
import com.compomics.colims.core.io.ModificationMappingException;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
     * The map of cached modifications (key: modification name, value: the
     * modification)
     */
    private final Map<String, Modification> cachedModifications = new HashMap<>();

    public Map<String, Modification> getCachedModifications() {
        return cachedModifications;
    }

    /**
     * Map the utilities modification matches onto the colims peptide. The
     * utilities PTMs are matched first onto CV terms from PSI-MOD.
     *
     * @param modificationMatches the list of modification matches
     * @param ptmScores the PeptideShaker PTM scores
     * @param targetPeptide the colims target peptide
     * @throws com.compomics.colims.core.io.ModificationMappingException
     */
    public void map(ArrayList<ModificationMatch> modificationMatches, PSPtmScores ptmScores, Peptide targetPeptide) throws ModificationMappingException {
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

                    if (ptmScores != null) {
                        PtmScoring ptmScoring = ptmScores.getPtmScoring(modificationMatch.getTheoreticPtm());
                        if (ptmScoring != null) {
                            //@todo ask mark if taking the site with the highest prob is the way to go
                            //@todo ask mark if we should use the modificationMatch.getModificationSite
                            List<Integer> orderedProbablisticSites = ptmScoring.getOrderedProbabilisticSites();
                            if (!orderedProbablisticSites.isEmpty()) {
                                Double probabilisticScore = ptmScoring.getProbabilisticScore(orderedProbablisticSites.get(0));
                                peptideHasModification.setAlphaScore(probabilisticScore);
                                Set<Integer> locations = ptmScoring.getProbabilisticSites();
                                if (!locations.contains(modificationMatch.getModificationSite())) {
                                    LOGGER.warn("The modification site " + modificationMatch.getModificationSite() + " is not found in the PtmScoring locations (" + Arrays.toString(orderedProbablisticSites.toArray()) + ")");
                                }
                            }
                            List<Integer> orderedDeltaSites = ptmScoring.getOrderedDSites();
                            if (!orderedDeltaSites.isEmpty()) {
                                Double deltaScore = ptmScoring.getDeltaScore(orderedDeltaSites.get(0));
                                peptideHasModification.setDeltaScore(deltaScore);
                                Set<Integer> locations = ptmScoring.getDSites();
                                if (!locations.contains(modificationMatch.getModificationSite())) {
                                    LOGGER.warn("The modification site " + modificationMatch.getModificationSite() + " is not found in the PtmScoring locations (" + Arrays.toString(orderedDeltaSites.toArray()) + ")");
                                }
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
                throw new ModificationMappingException("The modification match " + modificationMatch.getTheoreticPtm() + " could not be mapped.");
            }
        }

        if (!peptideHasModifications.isEmpty()) {
            targetPeptide.setPeptideHasModifications(peptideHasModifications);
        }
    }

    /**
     * Clear resources after usage.
     */
    public void clear() {
        cachedModifications.clear();
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
        modification = cachedModifications.get(modificationMatch.getTheoreticPtm());

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
                        LOGGER.warn("The modification match " + modificationMatch.getTheoreticPtm() + " could not be found in the PtmFactory.");
                    }

                    modification = new Modification(modificationMatch.getTheoreticPtm());
                    //@todo check if the PTM mass is the average or the monoisotopic mass shift
                    modification.setMonoIsotopicMassShift(ptM.getMass());
                }
            }
            //add to cached modifications
            cachedModifications.put(modification.getName(), modification);
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
        modification = cachedModifications.get(cvTerm.getAccession());

        if (modification == null) {
            //the modification was not found in the cachedModifications map    
            //look for the modification in the database by accession          
            modification = modificationService.findByAccession(cvTerm.getAccession());
            //for UNIMOD mods, look for the alternative accession
            if (cvTerm.getOntology().equals("UNIMOD") && modification == null) {
                //look for the modification in the database by alternative accession
                modification = modificationService.findByAlternativeAccession(cvTerm.getAccession());
            }

            if (modification == null) {
                //the modification was not found in the database
                if (cvTerm.getOntology().equals("PSI-MOD")) {
                    //look for the modification in the PSI-MOD ontology by accession                
                    modification = olsService.findModifiationByAccession(cvTerm.getAccession());

                    if (modification != null) {
                        //add to cached modifications
                        cachedModifications.put(modification.getAccession(), modification);
                    }
                } else if (cvTerm.getOntology().equals("UNIMOD")) {
                    //look for the modification in the PSI-MOD ontology by name and UNIMOD accession                
                    modification = olsService.findModifiationByNameAndUnimodAccession(cvTerm.getName(), cvTerm.getAccession());

                    if (modification != null) {
                        //add to cached modifications
                        cachedModifications.put(cvTerm.getAccession(), modification);
                    }
                }

                if (modification == null) {
                    modification = new Modification(cvTerm.getAccession(), cvTerm.getName());

                    //@todo check if the PTM mass is the average or the monoisotopic mass shift
                    modification.setMonoIsotopicMassShift(Double.valueOf(cvTerm.getValue()));

                    //add to cached modifications
                    cachedModifications.put(modification.getAccession(), modification);
                }
            } else {
                cachedModifications.put(cvTerm.getAccession(), modification);
            }
        }

        return modification;
    }

}
