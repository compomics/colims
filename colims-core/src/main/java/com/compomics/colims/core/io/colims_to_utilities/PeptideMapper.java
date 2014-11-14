package com.compomics.colims.core.io.colims_to_utilities;

import com.compomics.colims.core.service.PeptideService;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.PeptideHasProtein;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.compomics.util.experiment.identification.matches.PeptideMatch;

import java.util.ArrayList;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Kenneth Verheggen
 */
@Component("peptideMapper")
public class PeptideMapper {

    @Autowired
    private ColimsModificationMapper colimsModMapper;
    @Autowired
    private PeptideService peptideService;
    private static final Logger LOGGER = Logger.getLogger(PeptideMapper.class);

    public void map(Peptide sourcePeptide, PeptideMatch targetPeptideMatch) {
        LOGGER.debug("Mapping peptides from " + sourcePeptide.getSequence() + " to new PeptideMatch object");

        //TODO : REVERT THE MODIFICATIONMAPPING !!!!
        //map PTMs
        ArrayList<ModificationMatch> modificationMatches = new ArrayList<>();
        //fetch PeptideHasModifications
        peptideService.fetchPeptideHasModificiations(sourcePeptide);
        colimsModMapper.map(sourcePeptide, modificationMatches);

        ArrayList<String> parentProteinAccessions = new ArrayList<>();
        for (PeptideHasProtein peptideHasProtein : sourcePeptide.getPeptideHasProteins()) {
            parentProteinAccessions.add(peptideHasProtein.getProtein().getProteinAccessions().get(0).getAccession());
        }

        com.compomics.util.experiment.biology.Peptide assumedPeptide = new com.compomics.util.experiment.biology.Peptide(sourcePeptide.getSequence(), parentProteinAccessions, modificationMatches);
        targetPeptideMatch.setTheoreticPeptide(assumedPeptide);
    }
}
