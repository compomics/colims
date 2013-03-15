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

/**
 *
 * @author Niels Hulstaert
 */
public class PeptideMapper implements Mapper<com.compomics.util.experiment.biology.Peptide, Peptide> {

    private ModificationService modificationService;
    /**
     * The map of new modifications (key: modification name, value: the
     * modification)
     */
    private Map<String, Modification> newModifications;
    /**
     * Compomics utilities spectrum factory
     *
     * @todo think of a good way to import this .cus file from the .compomics
     * folder
     */
    private PTMFactory pTMFactory;

    public PeptideMapper() {
        newModifications = new HashMap<String, Modification>();
        pTMFactory = PTMFactory.getInstance();
    }

    public ModificationService getModificationService() {
        return modificationService;
    }

    public void setModificationService(ModificationService modificationService) {
        this.modificationService = modificationService;
    }

    @Override
    public void map(com.compomics.util.experiment.biology.Peptide source, Peptide target) throws MappingException {
        //set sequence
        target.setSequence(source.getSequence());
        //@todo check if this is the experimental mass
        target.setExperimentalMass(source.getMass());

        //check for modifications
        if (!source.getModificationMatches().isEmpty()) {
            List<PeptideHasModification> peptideHasModifications = new ArrayList<PeptideHasModification>();

            //iterate over modification matches
            for (ModificationMatch modificationMatch : source.getModificationMatches()) {
                PeptideHasModification peptideHasModification = new PeptideHasModification();
                //check if the modification could be found in the db or in the newModifications map
                //@todo configure hibernate cache and check performance
                Modification modification = modificationService.findByName(modificationMatch.getTheoreticPtm());
                if (modification != null) {
                } else if (newModifications.containsKey(modificationMatch.getTheoreticPtm())) {
                    modification = newModifications.get(modificationMatch.getTheoreticPtm());
                } else {
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
