package com.compomics.colims.core.io.parser.impl;

import com.compomics.util.experiment.personalization.UrParameter;

/**
 *
 * @author Davy
 */
public class MaxQuantPtmScoring implements UrParameter {

    private double score = -1;
    private double deltaScore = -1;

    public double getScore() {
        return score;
    }

    public void setScore(final double score) {
        this.score = score;
    }

    public double getDeltaScore() {
        return deltaScore;
    }

    public void setDeltaScore(final double deltaScore) {
        this.deltaScore = deltaScore;
    }

    @Override
    public String getFamilyName() {
        return "max quant parser";
    }

    @Override
    public int getIndex() {
        return 123456789;
    }
}
