package com.compomics.colims.core.io.maxquant;

import com.compomics.util.experiment.personalization.UrParameter;

/**
 * Convenience class for shoehorning these values into a peptide assumption.
 */
public class PeptidePosition implements UrParameter {

    /**
     * The amino acid (1 character representation) preceding the peptide in the protein sequence.
     */
    private String preAA;
    /**
     * The amino acid (1 character representation) following the peptide in the protein sequence.
     */
    private String postAA;
    /**
     * The start position of the peptide in the protein sequence.
     */
    private Integer start;
    /**
     * The end position of the peptide in the protein sequence.
     */
    private Integer end;

    /**
     * No-arg constructor.
     */
    public PeptidePosition() {
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getEnd() {
        return end;
    }

    public void setEnd(Integer end) {
        this.end = end;
    }

    public String getPreAA() {
        return preAA;
    }

    public void setPreeAA(String preAA) {
        this.preAA = preAA;
    }

    public String getPostAA() {
        return postAA;
    }

    public void setPostAA(String postAA) {
        this.postAA = postAA;
    }

    @Override
    public String getFamilyName() {
        return "maxquantpeptide";
    }

    @Override
    public int getIndex() {
        return 666;
    }
}
