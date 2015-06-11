package com.compomics.colims.core.util;

/**
 * Convenience class for peptide location information in a protein.
 */
public class PeptidePosition {

    /**
     * The amino acid (1 character representation) preceding the peptide in the protein sequence.
     */
    private Character preAA;
    /**
     * The amino acid (1 character representation) following the peptide in the protein sequence.
     */
    private Character postAA;
    /**
     * The start position of the peptide in the protein sequence.
     */
    private Integer startPosition;
    /**
     * The end position of the peptide in the protein sequence.
     */
    private Integer endPosition;

    /**
     * No-arg constructor.
     */
    public PeptidePosition() {
    }

    public Character getPreAA() {
        return preAA;
    }

    public void setPreAA(Character preAA) {
        this.preAA = preAA;
    }

    public Character getPostAA() {
        return postAA;
    }

    public void setPostAA(Character postAA) {
        this.postAA = postAA;
    }

    public Integer getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(Integer startPosition) {
        this.startPosition = startPosition;
    }

    public Integer getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(Integer endPosition) {
        this.endPosition = endPosition;
    }
}
