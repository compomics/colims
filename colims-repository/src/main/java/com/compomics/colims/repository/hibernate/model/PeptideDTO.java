package com.compomics.colims.repository.hibernate.model;

import com.compomics.colims.model.Peptide;

/**
 * This class represents a peptide data transfer object that holds some addititianol information about a peptide.
 * <p/>
 * Created by Niels Hulstaert on 14/10/15.
 */
public class PeptideDTO {

    /**
     * The number of protein groups associated with this peptide.
     */
    private int numberOfProteinGroups;
    /**
     * The peptide probability score.
     */
    private Double peptideProbability;
    /**
     * The peptide posterior error probability score.
     */
    private Double peptidePostErrorProbability;
    /**
     * The Peptide enitity instance.
     */
    private Peptide peptide;

    /**
     * No-arg constructor.
     */
    public PeptideDTO() {
    }

    public Double getPeptideProbability() {
        return peptideProbability;
    }

    public void setPeptideProbability(Double peptideProbability) {
        this.peptideProbability = peptideProbability;
    }

    public Double getPeptidePostErrorProbability() {
        return peptidePostErrorProbability;
    }

    public void setPeptidePostErrorProbability(Double peptidePostErrorProbability) {
        this.peptidePostErrorProbability = peptidePostErrorProbability;
    }

    public Peptide getPeptide() {
        return peptide;
    }

    public void setPeptide(Peptide peptide) {
        this.peptide = peptide;
    }

    public int getNumberOfProteinGroups() {
        return numberOfProteinGroups;
    }

    public void setNumberOfProteinGroups(int numberOfProteinGroups) {
        this.numberOfProteinGroups = numberOfProteinGroups;
    }

}
