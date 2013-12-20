package com.compomics.colims.core.mapper.impl.colimsToUtilities;

import java.util.List;

import com.compomics.colims.core.exception.MappingException;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.PeptideHasProtein;
import com.compomics.colims.model.Protein;
import com.compomics.util.experiment.identification.matches.PeptideMatch;
import com.compomics.util.experiment.identification.matches.ProteinMatch;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
     * @param peptideMatch
     * @throws MappingException
     */
    public void map(List<PeptideHasProtein> peptideHasProteins, List<ProteinMatch> proteinMatches, String peptideMatchKey) {
        for (PeptideHasProtein peptideHasProtein : peptideHasProteins) {
            ProteinMatch proteinMatch = new ProteinMatch(peptideHasProtein.getProtein().getAccession());

            //add the peptide to proteinmatches
            proteinMatch.addPeptideMatch(peptideMatchKey);

            proteinMatches.add(proteinMatch);
        }

    }
}
