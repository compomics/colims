package com.compomics.colims.core.mapper.impl.colimsToUtilities;

import com.compomics.colims.core.exception.MappingException;
import com.compomics.colims.model.Peptide;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.compomics.util.experiment.identification.matches.PeptideMatch;
import com.compomics.util.experiment.identification.matches.ProteinMatch;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Kenneth Verheggen
 */
@Component("colimsPeptideMapper")
public class ColimsPeptideMapper {

    @Autowired
    private static final ColimsModificationMapper colimsModMapper = new ColimsModificationMapper();
    @Autowired
    private static final ColimsProteinMapper colimsProteinMapper = new ColimsProteinMapper();

    private static final Logger LOGGER = Logger.getLogger(ColimsPeptideMapper.class);

    public void map(Peptide sourcePeptide, PeptideMatch targetPeptideMatch) throws MappingException {
          LOGGER.debug("Mapping peptides from " + sourcePeptide.getSequence() + " to new PeptideMatch object");
        //set sequence
        ArrayList<ProteinMatch> parentProteins = new ArrayList<ProteinMatch>();
        colimsProteinMapper.map(sourcePeptide, parentProteins);
        ArrayList<String> parentProteinAccessions = new ArrayList<String>();
        for (ProteinMatch aMatch : parentProteins) {
            parentProteinAccessions.add(aMatch.getMainMatch());
        }
        //TODO : REVERT THE MODIFICATIONMAPPING !!!!
        ArrayList<ModificationMatch> modifications = new ArrayList<ModificationMatch>();
        colimsModMapper.map(sourcePeptide, modifications);
        com.compomics.util.experiment.biology.Peptide assumedPeptide = new com.compomics.util.experiment.biology.Peptide(sourcePeptide.getSequence(), parentProteinAccessions, modifications);
        targetPeptideMatch.setTheoreticPeptide(assumedPeptide);
    }
}
