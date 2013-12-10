package com.compomics.colims.core.mapper.impl.colimsToUtilities;

import java.util.List;

import com.compomics.colims.core.exception.MappingException;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.PeptideHasProtein;
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

    @Autowired
    private static final ColimsPeptideMapper colimsPeptideMapper = new ColimsPeptideMapper();

    private static final Logger LOGGER = Logger.getLogger(ColimsProteinMapper.class);

    /**
     * Map the utilities protein related objects to colims proteins and add them
     * to the peptide.
     *
     * @param proteinMatches the utilities list of protein matches
     * @param targetPeptide the colims peptide
     * @throws MappingException
     */
    public void map(Peptide targetPeptide, List<ProteinMatch> proteinMatches) throws MappingException {
        for (PeptideHasProtein pepHasProt : targetPeptide.getPeptideHasProteins()) {
            ProteinMatch protMatch = new ProteinMatch();
            PeptideMatch pepMatch = new PeptideMatch();
            //map the peptide to utilitiespeptides
            PeptideMatch match = new PeptideMatch();
            colimsPeptideMapper.map(targetPeptide, match);
            //add the peptide to proteinmatches
            protMatch.addPeptideMatch(pepMatch.getKey());
            protMatch.setMainMatch(pepHasProt.getMainGroupProtein().getAccession());
            protMatch.addPeptideMatch(pepHasProt.getPeptide().getSequence());
            proteinMatches.add(protMatch);
        }

    }
}