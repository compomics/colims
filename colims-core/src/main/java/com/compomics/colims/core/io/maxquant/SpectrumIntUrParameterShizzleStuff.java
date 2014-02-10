package com.compomics.colims.core.io.maxquant;

import com.compomics.util.experiment.personalization.UrParameter;

/**
 *
 * @author Davy
 */
public class SpectrumIntUrParameterShizzleStuff implements UrParameter {

    private int spectrumid;

    public int getSpectrumid() {
        return spectrumid;
    }

    public void setSpectrumid(final int spectrumid) {
        this.spectrumid = spectrumid;
    }
    
    @Override
    public String getFamilyName() {
        return "maxquantparser";
    }

    @Override
    public int getIndex() {
        return 4782389;
    }    
        
}
