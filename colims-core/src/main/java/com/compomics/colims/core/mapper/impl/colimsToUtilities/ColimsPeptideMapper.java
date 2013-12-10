package com.compomics.colims.core.mapper.impl.colimsToUtilities;

import com.compomics.colims.core.mapper.impl.utilitiesToColims.*;
import com.compomics.colims.core.exception.MappingException;
import com.compomics.colims.model.Peptide;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
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

    private static final ColimsModificationMapper colimsModMapper = new ColimsModificationMapper();

    private static final ColimsProteinMapper colimsProteinMapper = new ColimsProteinMapper();

    private static final Logger LOGGER = Logger.getLogger(UtilitiesPeptideMapper.class);
    @Autowired
    private UtilitiesModificationMapper utilitiesModificationMapper;

    public void map(Peptide sourcePeptide, com.compomics.util.experiment.biology.Peptide targetPeptide) throws MappingException {
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
        targetPeptide = new com.compomics.util.experiment.biology.Peptide(sourcePeptide.getSequence(), parentProteinAccessions, modifications);

    }
}
