package com.compomics.colims.core.mapper.impl.colimsToUtilities;

import com.compomics.colims.core.mapper.impl.utilitiesToColims.*;
import java.util.List;

import com.compomics.colims.core.exception.MappingException;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.Spectrum;
import com.compomics.util.experiment.identification.PeptideAssumption;
import com.compomics.util.experiment.identification.matches.SpectrumMatch;
import com.compomics.util.experiment.massspectrometry.Charge;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Kenneth Verheggen
 */
@Component("colimsPsmMapper")
public class ColimsPsmMapper {

    private static final Logger LOGGER = Logger.getLogger(UtilitiesPsmMapper.class);
    @Autowired
    private ColimsPeptideMapper colimsPeptideMapper;

    public void map(Spectrum spectrum, List<SpectrumMatch> targetSpectrumMap) throws MappingException {
        //get best assumption
        for (Peptide aPeptide : spectrum.getPeptides()) {
            com.compomics.util.experiment.biology.Peptide utilitiesPeptide = new com.compomics.util.experiment.biology.Peptide();
            colimsPeptideMapper.map(aPeptide, utilitiesPeptide);
            PeptideAssumption assumption = new PeptideAssumption(utilitiesPeptide, 0, 0, new Charge(1, spectrum.getCharge()), aPeptide.getPsmProbability());
            SpectrumMatch match = new SpectrumMatch(spectrum.getAccession(), assumption);
            targetSpectrumMap.add(match);
        }
    }
}
