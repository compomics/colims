package com.compomics.colims.core.io.colims_to_utilities;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.PeptideHasProteinGroup;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.compomics.util.experiment.identification.matches.PeptideMatch;
import com.compomics.util.experiment.identification.matches.ProteinMatch;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * @author Kenneth Verheggen
 */
@Component("colimsPeptideMapper2")
public class ColimsPeptideMapper2 {

    @Autowired
    private ColimsModificationMapper colimsModMapper;
    @Autowired
    private ColimsProteinMapper colimsProteinMapper;

    private static final Logger LOGGER = Logger.getLogger(ColimsPeptideMapper2.class);

    public void map(final Peptide sourcePeptide, final PeptideMatch targetPeptideMatch) throws MappingException {
        LOGGER.debug("Mapping peptides from " + sourcePeptide.getSequence() + " to new PeptideMatch object");
        //set sequence
        ArrayList<ProteinMatch> parentProteins = new ArrayList<>();
        for (PeptideHasProteinGroup peptideHasProteinGroup : sourcePeptide.getPeptideHasProteinGroups()) {
            colimsProteinMapper.map(peptideHasProteinGroup.getProteinGroup().getMainProtein(), parentProteins);
        }

        ArrayList<String> parentProteinAccessions = new ArrayList<>();
        for (ProteinMatch aMatch : parentProteins) {
            parentProteinAccessions.add(aMatch.getMainMatch());
        }
        //TODO : REVERT THE MODIFICATIONMAPPING !!!!
        ArrayList<ModificationMatch> modifications = new ArrayList<>();
//        colimsModMapper.map(sourcePeptide, modifications);
        com.compomics.util.experiment.biology.Peptide assumedPeptide = new com.compomics.util.experiment.biology.Peptide(sourcePeptide.getSequence(), modifications);
        assumedPeptide.setParentProteins(parentProteinAccessions);
        targetPeptideMatch.setTheoreticPeptide(assumedPeptide);
    }

    public void map(final Peptide sourcePeptide, final PeptideMatch targetPeptideMatch, final ProteinMatch parentProteinMatch) {
        LOGGER.debug("Mapping peptides from " + sourcePeptide.getSequence() + " to new PeptideMatch object");
        //set sequence
        ArrayList<String> parentProteinAccessions = new ArrayList<>();
        parentProteinAccessions.add(parentProteinMatch.getMainMatch());
        //TODO : REVERT THE MODIFICATIONMAPPING !!!!
        ArrayList<ModificationMatch> modifications = new ArrayList<>();
//        colimsModMapper.map(sourcePeptide, modifications);
        com.compomics.util.experiment.biology.Peptide assumedPeptide = new com.compomics.util.experiment.biology.Peptide(sourcePeptide.getSequence(), modifications);
        assumedPeptide.setParentProteins(parentProteinAccessions);
        targetPeptideMatch.setTheoreticPeptide(assumedPeptide);
    }

}
