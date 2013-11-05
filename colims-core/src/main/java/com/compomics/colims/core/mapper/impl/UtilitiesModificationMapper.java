package com.compomics.colims.core.mapper.impl;

import com.compomics.colims.core.mapper.Mapper;
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
import com.compomics.colims.model.enums.ModificationScoreType;
import com.compomics.colims.model.enums.ModificationTypeEnum;
import com.compomics.util.experiment.biology.PTM;
import com.compomics.util.experiment.biology.PTMFactory;
import com.compomics.util.experiment.identification.SearchParameters;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.compomics.util.pride.CvTerm;
import com.compomics.util.pride.PrideObjectsFactory;
import com.compomics.util.pride.PtmToPrideMap;
import eu.isas.peptideshaker.myparameters.PSPtmScores;
import eu.isas.peptideshaker.scoring.PtmScoring;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
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
    private ModificationService modificationService;
    @Autowired
    private OlsService olsService;
    /**
     * The utilities PtmToPrideMap that holds mappings between utilities PTMs
     * and CV term PTMs
     */
    private PtmToPrideMap ptmToPrideMap;
    /**
     * The map of new modifications (key: modification name, value: the
     * modification)
     */
    private Map<String, Modification> newModifications = new HashMap<>();
    /**
     * Compomics utilities spectrum factory
     *
     * @todo think of a good way to import this .cus file from the .compomics
     * folder
     */
    private PTMFactory pTMFactory = PTMFactory.getInstance();

    public UtilitiesModificationMapper() throws FileNotFoundException, IOException, ClassNotFoundException {
        ptmToPrideMap = PrideObjectsFactory.getInstance().getPtmToPrideMap();
    }

    /**
     * Update the PtmToPrideMap with the PTMs found in the PeptideShaker
     * SearchParameters
     *
     * @param searchParameters the PeptideShaker SearchParameters
     */
    public void update(SearchParameters searchParameters) throws FileNotFoundException, IOException, ClassNotFoundException {
        PtmToPrideMap.loadPtmToPrideMap(searchParameters);
    }

    public void map(ArrayList<ModificationMatch> modificationMatches, PSPtmScores ptmScores, Peptide targetPeptide) throws MappingException {
        List<PeptideHasModification> peptideHasModifications = new ArrayList<>();

        //iterate over modification matches
        for (ModificationMatch modificationMatch : modificationMatches) {
            //try to find a mapped CV term in the PtmToPrideMap
            CvTerm cvTerm = ptmToPrideMap.getCVTerm(modificationMatch.getTheoreticPtm());

            Modification modification;
            if (cvTerm != null) {
                modification = mapModificationMatch(cvTerm);
            } else {
                modification = mapModificationMatch(modificationMatch);
            }

            //set entity relations if modification could be mapped
            if (modification != null) {
                PeptideHasModification peptideHasModification = new PeptideHasModification();
                //set modification type
                if (modificationMatch.isVariable()) {
                    peptideHasModification.setModificationType(ModificationTypeEnum.VARIABLE);
                } else {
                    peptideHasModification.setModificationType(ModificationTypeEnum.FIXED);
                }

                //set location in the PeptideHasModification join table
                //substract one because the modification site in the ModificationMatch class starts from 1
                Integer location = modificationMatch.getModificationSite() - 1;
                peptideHasModification.setLocation(location);

                if (ptmScores != null && ptmScores.getPtmScoring(modificationMatch.getTheoreticPtm()) != null) {
                    String locationKeys = ptmScores.getPtmScoring(modificationMatch.getTheoreticPtm()).getBestDeltaScoreLocations();
                    if (locationKeys != null) {
                        ArrayList<Integer> locations = PtmScoring.getLocations(locationKeys);
                        if (locations.contains(modificationMatch.getModificationSite())) {
                            Double deltaScore = ptmScores.getPtmScoring(modificationMatch.getTheoreticPtm()).getDeltaScore(locationKeys);
                            peptideHasModification.setScore(deltaScore);
                        }
                        else{
                            throw new IllegalStateException("The modification site " + modificationMatch.getModificationSite() + " is not found in the PtmScoring locations (" + locationKeys + ")" );
                        }
                    }
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
                    PTM ptM = pTMFactory.getPTM(modificationMatch.getTheoreticPtm());

                    //check if the PTM is not unknown in the PTMFactory
                    if (!ptM.getName().equals(UNKNOWN_UTILITIES_PTM)) {
                        modification = new Modification(modificationMatch.getTheoreticPtm());

                        //@todo check if the PTM mass is the average or the monoisotopic mass shift
                        modification.setMonoIsotopicMassShift(ptM.getMass());

                        //add to newModifications
                        newModifications.put(modification.getName(), modification);
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
        modification = newModifications.get(cvTerm.getName());

        if (modification == null) {
            //the modification was not found in the newModifications map    
            //look for the modification in the database by accession          
            modification = modificationService.findByAccession(cvTerm.getAccession());

            if (modification == null) {
                //the modification was not found in the database
                //look for the modification in the PSI-MOD ontology by accession                
                modification = olsService.findModifiationByAccession(cvTerm.getAccession());

                if (modification != null) {
                    newModifications.put(modification.getName(), modification);
                }
            }
        }

        return modification;
    }
}
