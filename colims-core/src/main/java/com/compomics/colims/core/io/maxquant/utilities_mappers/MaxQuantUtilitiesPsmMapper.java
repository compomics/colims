package com.compomics.colims.core.io.maxquant.utilities_mappers;

import com.compomics.colims.core.io.MappingException;
import com.compomics.colims.core.io.MatchScore;
import com.compomics.colims.core.io.maxquant.parsers.MaxQuantParser;
import com.compomics.colims.core.io.utilities_to_colims.UtilitiesProteinMapper;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.Spectrum;
import com.compomics.util.experiment.identification.PeptideAssumption;
import com.compomics.util.experiment.identification.matches.ProteinMatch;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Maps the max quant identifications of a spectrum to a Colims spectrum.
 *
 * @author Davy
 */
@Component("maxQuantUtilitiesPsmMapper")
public class MaxQuantUtilitiesPsmMapper {

    @Autowired
    private MaxQuantUtilitiesPeptideMapper maxQuantUtilitiesPeptideMapper;
    @Autowired
    private UtilitiesProteinMapper utilitiesProteinMapper;

    public void map(Spectrum aParsedSpectrum, MaxQuantParser maxQuantParser, Spectrum targetSpectrum) throws MappingException {
        Peptide targetPeptide = maxQuantParser.getIdentificationForSpectrum(aParsedSpectrum);

        List<ProteinMatch> proteinMatches = new ArrayList<>(maxQuantParser.getProteinHitsForIdentification(sourcePeptide));
//        utilitiesProteinMapper.map(proteinMatches, (MatchScore) sourcePeptide.getUrParam(new MatchScore(Double.NaN, Double.NEGATIVE_INFINITY)), targetPeptide);
        targetSpectrum.getPeptides().add(targetPeptide);
        targetPeptide.setSpectrum(targetSpectrum);
    }

    /**
     * Clear resources.
     */
    public void clear() {
        utilitiesProteinMapper.clear();
        maxQuantUtilitiesPeptideMapper.clear();
    }
}
