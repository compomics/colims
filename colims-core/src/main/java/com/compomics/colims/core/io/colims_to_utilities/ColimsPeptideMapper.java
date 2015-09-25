package com.compomics.colims.core.io.colims_to_utilities;

import com.compomics.colims.core.service.PeptideService;
import com.compomics.colims.core.service.ProteinAccessionService;
import com.compomics.colims.model.Peptide;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.compomics.util.experiment.identification.spectrum_assumptions.PeptideAssumption;
import com.compomics.util.experiment.massspectrometry.Charge;
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
public class ColimsPeptideMapper {

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

    public PeptideAssumption map(Peptide sourcePeptide) {
        //map peptide
        com.compomics.util.experiment.biology.Peptide targetPeptide = mapPeptide(sourcePeptide);

        Charge charge = new Charge(1, sourcePeptide.getCharge());
        return new PeptideAssumption(targetPeptide, 0, 0, charge, sourcePeptide.getPsmProbability());
    }

    /**
     * Map the Colims Peptide instance onto the Utilities Peptide instance.
     *
     * @param sourcePeptide
     * @return the Utilities Peptide instance
     */
    private com.compomics.util.experiment.biology.Peptide mapPeptide(Peptide sourcePeptide) {
        //fetch PeptideHasModifications
        peptideService.fetchPeptideHasModifications(sourcePeptide);

        ArrayList<ModificationMatch> modificationMatches = new ArrayList<>();
        if (!sourcePeptide.getPeptideHasModifications().isEmpty()) {
            colimsModificationMapper.map(sourcePeptide.getPeptideHasModifications(), modificationMatches);
        }

        return new com.compomics.util.experiment.biology.Peptide(sourcePeptide.getSequence(), modificationMatches);
    }
}
