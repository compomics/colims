package com.compomics.colims.core.io.colims_to_utilities;

import com.compomics.colims.core.io.Mapper;
import com.compomics.colims.model.PeptideHasModification;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * This class maps a collection of Colims PeptideHasModification instances onto a collection of Utilities
 * ModificationMatch instances.
 *
 * @author Niels Hulstaert
 */
@Component("colimsModificationMapper")
public class ColimsModificationMapper implements Mapper<List<PeptideHasModification>, ArrayList<ModificationMatch>> {

    /**
     * Logger instance
     */
    private static final Logger LOGGER = Logger.getLogger(ColimsModificationMapper.class);

    /**
     * Map the Colims PeptideHasModification instances onto the Utilities ModificationMatch instances.
     *
     * @param peptideHasModifications the list of PeptideHasModification instances
     * @param modificationMatches     the list of ModificationMatch instances
     */
    @Override
    public void map(final List<PeptideHasModification> peptideHasModifications, ArrayList<ModificationMatch> modificationMatches) {
        peptideHasModifications.stream().forEach((peptideHasModification) -> {
            String theoreticPTM = peptideHasModification.getModification().getAccession();
            if (theoreticPTM != null) {
                int modificationSite = peptideHasModification.getLocation();
                //@todo do we need to get the modification type from the search settings here?
                ModificationMatch match = new ModificationMatch(theoreticPTM, true, modificationSite);
                modificationMatches.add(match);
            }
        });
    }
}
