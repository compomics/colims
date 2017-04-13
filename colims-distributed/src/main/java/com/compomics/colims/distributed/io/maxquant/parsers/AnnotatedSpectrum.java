package com.compomics.colims.distributed.io.maxquant.parsers;

import com.compomics.colims.model.Spectrum;
import org.apache.commons.math.util.MathUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Holder class for a {@link com.compomics.colims.model.Spectrum} instance and it's annotations.
 * <p>
 * Created by niels on 4/5/17.
 */
public class AnnotatedSpectrum {

    private static final int ROUND_DECIMALS = 5;
    private static final String DELIMITER = ";";

    /**
     * The {@link Spectrum} instance.
     */
    private Spectrum spectrum;
    /**
     * The matched fragment ions, semi-colon separated.
     */
    private String ionMatches;
    /**
     * The matched fragment masses, semi-colon separated.
     */
    private String fragmentMasses;

    /**
     * Constructor.
     *
     * @param spectrum       the {@link Spectrum} instance
     * @param fragmentIons   the matched fragment ions String
     * @param fragmentMasses the matched fragment masses String
     */
    public AnnotatedSpectrum(Spectrum spectrum, String fragmentIons, String fragmentMasses) {
        this.spectrum = spectrum;
        this.ionMatches = fragmentIons;
        this.fragmentMasses = roundFragmentMasses(fragmentMasses);
    }

    public Spectrum getSpectrum() {
        return spectrum;
    }

    public String getIonMatches() {
        return ionMatches;
    }

    public String getFragmentMasses() {
        return fragmentMasses;
    }

    /**
     * This method rounds the fragment masses and concatenates them again.
     *
     * @param fragmentMasses the matched fragment masses
     * @return
     */
    private String roundFragmentMasses(String fragmentMasses) {
        return Arrays.stream(fragmentMasses.split(DELIMITER)).map(fragmentMass ->
                Double.toString(MathUtils.round(Double.valueOf(fragmentMass), ROUND_DECIMALS))
        ).collect(Collectors.joining(DELIMITER));
    }
}
