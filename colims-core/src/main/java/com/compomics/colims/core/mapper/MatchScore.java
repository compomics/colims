package com.compomics.colims.core.mapper;

import com.compomics.util.experiment.personalization.UrParameter;

/**
 *
 * @author Niels Hulstaert
 */
public class MatchScore implements UrParameter {

    private Double probability;
    private Double postErrorProbability;

    public MatchScore(final Double probability, final Double postErrorProbability) {
        this.probability = probability;
        this.postErrorProbability = postErrorProbability;
    }

    public Double getProbability() {
        return probability;
    }

    public void setProbability(final Double probability) {
        this.probability = probability;
    }

    public Double getPostErrorProbability() {
        return postErrorProbability;
    }

    public void setPostErrorProbability(final Double postErrorProbability) {
        this.postErrorProbability = postErrorProbability;
    }

    //
    @Override
    public String getFamilyName() {
        return "colims";
    }

    @Override
    public int getIndex() {
        return 99;
    }
}
