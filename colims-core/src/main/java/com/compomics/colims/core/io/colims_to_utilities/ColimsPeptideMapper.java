package com.compomics.colims.core.io.colims_to_utilities;

import com.compomics.colims.core.io.Mapper;
import com.compomics.colims.core.service.PeptideService;
import com.compomics.colims.core.service.ProteinAccessionService;
import com.compomics.colims.model.Peptide;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.compomics.util.experiment.identification.matches.PeptideMatch;
import com.compomics.util.experiment.identification.spectrum_assumptions.PeptideAssumption;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * This class maps the Colims Peptide instance onto a Utilities PeptideMatch instance.
 *
 * @author Niels Hulstaert
 */
@Component("colimspeptideMapper")
public class ColimsPeptideMapper implements Mapper<Peptide, PeptideAssumption> {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(ColimsPeptideMapper.class);

    @Autowired
    private ColimsModificationMapper colimsModificationMapper;
    @Autowired
    private PeptideService peptideService;
    @Autowired
    private ProteinAccessionService proteinAccessionService;

    @Override
    public void map(Peptide sourcePeptide, PeptideAssumption peptideAssumption) {
        //map peptide


        //map PTMs
        ArrayList<ModificationMatch> modificationMatches = new ArrayList<>();


        com.compomics.util.experiment.biology.Peptide assumedPeptide = new com.compomics.util.experiment.biology.Peptide(sourcePeptide.getSequence(), modificationMatches);
        assumedPeptide.setParentProteins((ArrayList) proteinAccessionService.getProteinAccessionsForPeptide(sourcePeptide));

//        targetPeptideMatch.setTheoreticPeptide(assumedPeptide);
    }

    /**
     * Map the Colims Peptide instance onto the Utilities Peptide instance.
     *
     * @param sourcePeptide
     * @return the Utilities Peptide instance
     */
    private com.compomics.util.experiment.biology.Peptide mapPeptide(Peptide sourcePeptide){
        //fetch PeptideHasModifications
        peptideService.fetchPeptideHasModifications(sourcePeptide);

        ArrayList<ModificationMatch> modificationMatches = new ArrayList<>();
        colimsModificationMapper.map(sourcePeptide.getPeptideHasModifications(), modificationMatches);

        return new com.compomics.util.experiment.biology.Peptide();
    }
}
