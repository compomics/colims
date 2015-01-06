package com.compomics.colims.core.io.colims_to_utilities;

import java.util.List;

import com.compomics.colims.model.PeptideHasProtein;
import com.compomics.util.experiment.identification.matches.ProteinMatch;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 *
 * @author Kenneth Verheggen
 */
@Component("proteinMapper")
public class ProteinMapper {

    private static final Logger LOGGER = Logger.getLogger(ProteinMapper.class);

    /**
     * Map the utilities protein related objects to colims proteins and add them
     * to the peptide.
     *
     * @param peptideHasProteins
     * @param proteinMatches
     * @param peptideMatchKey
     */
    public void map(final List<PeptideHasProtein> peptideHasProteins, final List<ProteinMatch> proteinMatches, final String peptideMatchKey) {
//        for (PeptideHasProtein peptideHasProtein : peptideHasProteins) {
//            ProteinMatch proteinMatch = new ProteinMatch(peptideHasProtein.getProtein().getAccession());
//
//            //add the peptide to proteinmatches
//            proteinMatch.addPeptideMatch(peptideMatchKey);
//
//            proteinMatches.add(proteinMatch);
//        }
    }
}
