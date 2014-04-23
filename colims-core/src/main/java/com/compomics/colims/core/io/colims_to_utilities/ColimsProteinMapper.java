package com.compomics.colims.core.io.colims_to_utilities;

import java.util.List;

import com.compomics.colims.core.io.MappingException;
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
@Component("colimsProteinMapper")
public class ColimsProteinMapper {

    private static final Logger LOGGER = Logger.getLogger(ColimsProteinMapper.class);
    @Autowired
    private ColimsPeptideMapper colimsPeptideMapper;

    /**
     * Map the utilities protein related objects to colims proteins and add them
     * to the peptide.
     *
     * @param proteinMatches the utilities list of protein matches
     * @param sourceProtein the colims protein
     * @throws MappingException
     */
    public void map(final Protein sourceProtein, final List<ProteinMatch> proteinMatches) throws MappingException {
//        LOGGER.debug("Mapping proteins from " + sourceProtein.getAccession() + " to new list of ProteinMatch objects");
//        ProteinMatch protMatch = new ProteinMatch();
//        protMatch.setMainMatch(sourceProtein.getAccession());
//        for (PeptideHasProtein pepHasProt : sourceProtein.getPeptideHasProteins()) {
//            PeptideMatch pepMatch = new PeptideMatch();
//            //map the peptide to utilitiespeptides
//            colimsPeptideMapper.map(pepHasProt.getPeptide(), pepMatch, protMatch);
//            //add the peptide to proteinmatches
//            protMatch.addPeptideMatch(pepMatch.getKey());
//        }
//        proteinMatches.add(protMatch);
    }
}
