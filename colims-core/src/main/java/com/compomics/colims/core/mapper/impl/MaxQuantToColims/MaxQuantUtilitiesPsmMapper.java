package com.compomics.colims.core.mapper.impl.MaxQuantToColims;

import com.compomics.colims.core.exception.MappingException;
import com.compomics.colims.core.io.parser.impl.MaxQuantParser;
import com.compomics.colims.model.Peptide;
import com.compomics.colims.model.Spectrum;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
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
    
    public void map(MSnSpectrum aParsedSpectrum, MaxQuantParser maxQuantParser, Spectrum targetSpectrum) throws MappingException {
        Peptide peptide = new Peptide();
        maxQuantUtilitiesPeptideMapper.map(maxQuantParser.getIdentificationForSpectrum(aParsedSpectrum), peptide);
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
