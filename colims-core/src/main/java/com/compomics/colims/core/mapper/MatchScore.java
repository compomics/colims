
package com.compomics.colims.core.mapper;

/**
 *
 * @author Niels Hulstaert
 */
public class MatchScore {
    
    private Double probability;
    private Double postErrorProbability;

    public MatchScore(Double probability, Double postErrorProbability) {
        this.probability = probability;
        this.postErrorProbability = postErrorProbability;
    }    
    
    public Double getProbability() {
        return probability;
    }

    public void setProbability(Double probability) {
        this.probability = probability;
    }

    public Double getPostErrorProbability() {
        return postErrorProbability;
    }

    public void setPostErrorProbability(Double postErrorProbability) {
        this.postErrorProbability = postErrorProbability;
    }        

}
