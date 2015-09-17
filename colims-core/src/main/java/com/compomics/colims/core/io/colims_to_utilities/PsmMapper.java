package com.compomics.colims.core.io.colims_to_utilities;


import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.Spectrum;
import com.compomics.util.experiment.identification.matches.PeptideMatch;
import com.compomics.util.experiment.identification.matches.SpectrumMatch;
import com.compomics.util.experiment.identification.spectrum_assumptions.PeptideAssumption;
import com.compomics.util.experiment.massspectrometry.Charge;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Kenneth Verheggen
 */
@Component("psmMapper")
public class PsmMapper {

    private static final Logger LOGGER = Logger.getLogger(PsmMapper.class);
    @Autowired
    private PeptideMapper peptideMapper;

    public void map(final Spectrum spectrum, final SpectrumMatch spectrumMatch, Peptide peptide) {
        LOGGER.debug("Mapping spectrum from " + spectrum.getTitle() + " to new list of SpectrumMatch objects");
        
        //for the moment, get the first peptide and set it as the best assumption
        PeptideMatch peptideMatch = new PeptideMatch();
        peptideMapper.map(peptide, peptideMatch);
        PeptideAssumption assumption = new PeptideAssumption(peptideMatch.getTheoreticPeptide(), 0, 0, new Charge(1, spectrum.getCharge()), peptide.getPsmProbability());
        //set best assumption
        spectrumMatch.setBestPeptideAssumption(assumption);       
    }
}
