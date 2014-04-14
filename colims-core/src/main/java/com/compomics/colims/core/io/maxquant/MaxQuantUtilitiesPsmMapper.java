package com.compomics.colims.core.io.maxquant;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.MatchScore;
import com.compomics.colims.core.io.utilities_to_colims.UtilitiesProteinMapper;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.Spectrum;
import com.compomics.util.experiment.identification.PeptideAssumption;
import com.compomics.util.experiment.identification.matches.ProteinMatch;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * maps the max quant identifications of a spectrum to a colims spectrum
 *
 * @throws MappingException
 * @author Davy
 */
@Component("maxQuantUtilitiesPsmMapper")
public class MaxQuantUtilitiesPsmMapper {

    @Autowired
    private MaxQuantUtilitiesPeptideMapper maxQuantUtilitiesPeptideMapper;
    @Autowired
    private UtilitiesProteinMapper utilitiesProteinMapper;

    public void map(MSnSpectrum aParsedSpectrum, MaxQuantParser maxQuantParser, Spectrum targetSpectrum) throws MappingException {
        Peptide targetPeptide = new Peptide();
        PeptideAssumption sourcePeptide = maxQuantParser.getIdentificationForSpectrum(aParsedSpectrum);
        maxQuantUtilitiesPeptideMapper.map(sourcePeptide, targetPeptide);
        List<ProteinMatch> proteinMatches = new ArrayList<>(maxQuantParser.getProteinHitsForIdentification(sourcePeptide));
        utilitiesProteinMapper.map(proteinMatches, (MatchScore) sourcePeptide.getUrParam(new MatchScore(Double.NaN, Double.NEGATIVE_INFINITY)), targetPeptide);
        targetSpectrum.getPeptides().add(targetPeptide);
        targetPeptide.setSpectrum(targetSpectrum);
    }
}
