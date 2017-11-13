package com.compomics.colims.repository.hibernate;

/**
 * This class represents a protein group data transfer object that holds some
 * additional information about a protein group (for a given run).
 * <p/>
 * Created by Niels Hulstaert on 14/10/15.
 */
public class ProteinGroupDTO {

    /**
     * The protein group ID.
     */
    private Long id;
    /**
     * The protein probability score.
     */
    private Double proteinProbability;
    /**
     * The protein posterior error probability score.
     */
    private Double proteinPostErrorProbability;
    /**
     * The main group protein ID.
     */
    private Long mainId;
    /**
     * The accession of the main group protein.
     */
    private String mainAccession;
    /**
     * The protein ID
     */
    private Long proteinId;
    /**
     * the sequence of the main group protein.
     */
    private String mainSequence;
    /**
     * The number of distinct peptides (distinct peptide sequence, modifications
     * not taken into account!) linked to the group.
     */
    private long distinctPeptideSequenceCount;
    /**
     * The number of spectra related with the protein group.
     */
    private long spectrumCount;

    /**
     * No-arg constructor.
     */
    public ProteinGroupDTO() {
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

    public Long getMainId() {
        return mainId;
    }

    public void setMainId(Long mainId) {
        this.mainId = mainId;
    }

    public String getMainAccession() {
        return mainAccession;
    }

    public void setMainAccession(String mainAccession) {
        this.mainAccession = mainAccession;
    }

    public Long getProteinId() {
        return proteinId;
    }

    public void setProteinId(Long proteinId) {
        this.proteinId = proteinId;
    }

    public String getMainSequence() {
        return mainSequence;
    }

    public void setMainSequence(String mainSequence) {
        this.mainSequence = mainSequence;
    }

    public long getDistinctPeptideSequenceCount() {
        return distinctPeptideSequenceCount;
    }

    public void setDistinctPeptideSequenceCount(long distinctPeptideSequenceCount) {
        this.distinctPeptideSequenceCount = distinctPeptideSequenceCount;
    }

    public long getSpectrumCount() {
        return spectrumCount;
    }

    public void setSpectrumCount(long spectrumCount) {
        this.spectrumCount = spectrumCount;
    }

    /**
     * Calculate the protein confidence based on the
     * proteinPostErrorProbability.
     *
     * @return the protein confidence
     */
    public double getProteinConfidence() {
        double confidence = 0.0;

        if (proteinPostErrorProbability != null) {
            confidence = 100.0 * (1 - proteinPostErrorProbability);
            if (confidence <= 0) {
                confidence = 0;
            }
        }

        return confidence;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ProteinGroupDTO that = (ProteinGroupDTO) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
