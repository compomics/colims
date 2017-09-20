package com.compomics.colims.repository.hibernate;

import com.compomics.colims.model.Peptide;

/**
 * This class represents a peptide data transfer object that holds some
 * additional information about a peptide for MzTab export purposes.
 * <p/>
 * Created by Niels Hulstaert on 14/10/15.
 */
public class PeptideMzTabDTO {

    /**
     * The peptide instance.
     */
    private Peptide peptide;
    /**
     * The analytical run ID.
     */
    private long analyticalRunId;
    /**
     * The protein group ID.
     */
    private Long proteinGroupId;

    /**
     * No-arg constructor.
     */
    public PeptideMzTabDTO() {
    }

    /**
     * Constructor.
     *
     * @param peptide the Peptide instance
     * @param analyticalRunId the analytical run ID
     * @param proteinGroupId the protein group ID
     */
    public PeptideMzTabDTO(Peptide peptide, long analyticalRunId, Long proteinGroupId) {
        this.peptide = peptide;
        this.analyticalRunId = analyticalRunId;
        this.proteinGroupId = proteinGroupId;
    }

    public Peptide getPeptide() {
        return peptide;
    }

    public void setPeptide(Peptide peptide) {
        this.peptide = peptide;
    }

    public long getAnalyticalRunId() {
        return analyticalRunId;
    }

    public void setAnalyticalRunId(long analyticalRunId) {
        this.analyticalRunId = analyticalRunId;
    }

    public Long getProteinGroupId() {
        return proteinGroupId;
    }

    public void setProteinGroupId(Long proteinGroupId) {
        this.proteinGroupId = proteinGroupId;
    }
}
