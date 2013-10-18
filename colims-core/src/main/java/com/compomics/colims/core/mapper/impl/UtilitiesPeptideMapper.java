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
import com.compomics.util.experiment.biology.PTM;
import com.compomics.util.experiment.biology.PTMFactory;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Niels Hulstaert
 */
@Component("utilitiesPeptideMapper")
public class UtilitiesPeptideMapper implements Mapper<com.compomics.util.experiment.biology.Peptide, Peptide> {

    private static final String UNKNOWN_UTILITIES_PTM = "unknown";
    @Autowired
    private ModificationService modificationService;
    @Autowired
    private OlsService olsService;
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

    public UtilitiesPeptideMapper() {
    }

    @Override
    public void map(com.compomics.util.experiment.biology.Peptide source, Peptide target) throws MappingException {
        //set sequence
        target.setSequence(source.getSequence());
        //set theoretical mass
        target.setTheoreticalMass(source.getMass());
        //@todo how to get experimental mass

        //check for modifications
        if (!source.getModificationMatches().isEmpty()) {
            List<PeptideHasModification> peptideHasModifications = new ArrayList<>();

            //iterate over modification matches
            for (ModificationMatch modificationMatch : source.getModificationMatches()) {
                PeptideHasModification peptideHasModification = new PeptideHasModification();
                //look for the modification in the db
                //@todo configure hibernate cache and check performance
                Modification modification = modificationService.findByName(modificationMatch.getTheoreticPtm());
                if (modification == null) {
                    //the modification was not found in the db
                    //look for the modification in the newModifications map    
                    if (newModifications.containsKey(modificationMatch.getTheoreticPtm())) {
                        modification = newModifications.get(modificationMatch.getTheoreticPtm());
                    } else {
                        //the modification was not found in the newModification map
                        //look for the modification in the PSI-MOD ontology
                        modification = olsService.findModifiationByExactName(modificationMatch.getTheoreticPtm());

                        if (modification == null) {
                            //the modification was not found by name in the PSI-MOD ontology
                            //@todo maybe search by mass or not by exact name?
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

                if (modification != null) {
                    peptideHasModifications.add(peptideHasModification);
                    //set entity relations
                    peptideHasModification.setModification(modification);
                    peptideHasModification.setPeptide(target);
                }
            }
            target.setPeptideHasModifications(peptideHasModifications);
        }
    }
}
