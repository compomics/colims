package com.compomics.colims.core.io.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.compomics.colims.core.exception.MappingException;
import com.compomics.colims.core.service.ModificationService;
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

    @Autowired
    private ModificationService modificationService;
    /**
     * The map of new modifications (key: modification name, value: the
     * modification)
     */
    private Map<String, Modification> newModifications = new HashMap<>();;
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
        //@todo check if this is the experimental mass
        target.setExperimentalMass(source.getMass());

        //check for modifications
        if (!source.getModificationMatches().isEmpty()) {
            List<PeptideHasModification> peptideHasModifications = new ArrayList<>();

            //iterate over modification matches
            for (ModificationMatch modificationMatch : source.getModificationMatches()) {
                PeptideHasModification peptideHasModification = new PeptideHasModification();
                //look for the modification in the db
                //@todo configure hibernate cache and check performance
                Modification modification = modificationService.findByName(modificationMatch.getTheoreticPtm());
                if (modification != null) {
                }//look for the modification in the newModifications map    
                else if (newModifications.containsKey(modificationMatch.getTheoreticPtm())) {
                    modification = newModifications.get(modificationMatch.getTheoreticPtm());
                }//else it's a new modification     
                else {
                    modification = new Modification(modificationMatch.getTheoreticPtm());

                    //get modification from modification factory
                    PTM ptM = pTMFactory.getPTM(modificationMatch.getTheoreticPtm());
                    //@todo check if the PTM mass is the average of the monoisotopic
                    modification.setMonoIsotopicMass(ptM.getMass());

                    //add to newModifications
                    newModifications.put(modification.getName(), modification);
                }
                peptideHasModifications.add(peptideHasModification);
                //set entity relations
                peptideHasModification.setModification(modification);
                peptideHasModification.setPeptide(target);
            }
            target.setPeptideHasModifications(peptideHasModifications);
        }
    }
}
