package com.compomics.colims.core.mapper.impl.maxQuantToColims;

import com.compomics.colims.core.exception.MappingException;
import com.compomics.colims.core.io.parser.impl.MaxQuantParser;
import com.compomics.colims.core.mapper.MatchScore;
import com.compomics.colims.core.mapper.impl.utilitiesToColims.UtilitiesProteinMapper;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.Protein;
import com.compomics.colims.model.Spectrum;
import com.compomics.util.experiment.identification.PeptideAssumption;
import com.compomics.util.experiment.identification.matches.ProteinMatch;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * maps the max quant identifications of a spectrum to a colims spectrum
 *
 * @throws MappingException
 * @author Davy
 */
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
    }
}
