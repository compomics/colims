package com.compomics.colims.core.io.parser.impl;

import com.compomics.util.experiment.personalization.UrParameter;

/**
 *
 * @author Davy
 */
public class SpectrumIntUrParameterShizzleStuff implements UrParameter{
    public int spectrumid;

    @Override
    public String getFamilyName() {
        return "maxquantparser";
    }

    @Override
    public int getIndex() {
        return 4782389;
    }
}
