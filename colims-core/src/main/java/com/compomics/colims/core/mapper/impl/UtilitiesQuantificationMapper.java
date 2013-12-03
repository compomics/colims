/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.colims.core.mapper.impl;

import com.compomics.colims.core.exception.MappingException;
import com.compomics.colims.model.Quantification;
import com.compomics.colims.model.Spectrum;
import com.compomics.colims.model.enums.FragmentationType;
import com.compomics.util.experiment.quantification.quantification.QuantificationHit;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kenneth Verheggen
 */
@Component("utilitiesQuantificationMapper")
@Transactional
public class UtilitiesQuantificationMapper {

    public void map(QuantificationHit source, FragmentationType fragmentationType, Quantification target) throws MappingException {
        Spectrum colimsSpectrum = new Spectrum();
        new UtilitiesSpectrumMapper().map(source.getSpectrum(), fragmentationType, colimsSpectrum);
        target.setSpectrum(colimsSpectrum);
    }

}
