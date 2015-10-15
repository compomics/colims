package com.compomics.colims.repository.hibernate.model;

import com.compomics.colims.model.ProteinGroup;

/**
 * Created by Niels Hulstaert on 14/10/15.
 */
public class ProteinGroupForRun {

    private Long id;
    private Double proteinProbability;
    private Double proteinPostErrorProbability;
    private String mainAccession;
    private String mainSequence;
    private long distinctPeptideCount;
    private long spectrumCount;
    private ProteinGroup proteinGroup;

    /**
     * No-arg constructor.
     */
    public ProteinGroupForRun() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getProteinProbability() {
        return proteinProbability;
    }

    public void setProteinProbability(Double proteinProbability) {
        this.proteinProbability = proteinProbability;
    }

    public Double getProteinPostErrorProbability() {
        return proteinPostErrorProbability;
    }

    public void setProteinPostErrorProbability(Double proteinPostErrorProbability) {
        this.proteinPostErrorProbability = proteinPostErrorProbability;
    }

    public String getMainAccession() {
        return mainAccession;
    }

    public void setMainAccession(String mainAccession) {
        this.mainAccession = mainAccession;
    }

    public String getMainSequence() {
        return mainSequence;
    }

    public void setMainSequence(String mainSequence) {
        this.mainSequence = mainSequence;
    }

    public long getDistinctPeptideCount() {
        return distinctPeptideCount;
    }

    public void setDistinctPeptideCount(long distinctPeptideCount) {
        this.distinctPeptideCount = distinctPeptideCount;
    }

    public long getSpectrumCount() {
        return spectrumCount;
    }

    public void setSpectrumCount(long spectrumCount) {
        this.spectrumCount = spectrumCount;
    }

    public ProteinGroup getProteinGroup() {
        return proteinGroup;
    }

    public void setProteinGroup(ProteinGroup proteinGroup) {
        this.proteinGroup = proteinGroup;
    }

    /**
     * Calculate the protein confidence based on the proteinPostErrorProbability.
     *
     * @return the protein confidence
     */
    public double getProteinConfidence() {
        double confidence = 100.0 * (1 - proteinPostErrorProbability);
        if (confidence <= 0) {
            confidence = 0;
        }

        return confidence;
    }

}
