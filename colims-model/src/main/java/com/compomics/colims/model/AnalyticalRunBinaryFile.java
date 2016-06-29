package com.compomics.colims.model;

import javax.persistence.*;

/**
 * This class represents an analytical run attachment in the database.
 *
 * @author Niels Hulstaert
 */
@Table(name = "analytical_run_binary_file")
@Entity
public class AnalyticalRunBinaryFile extends BinaryFile {

    private static final long serialVersionUID = -629806717593264864L;

    /**
     * The analytical run of the attachment.
     */
    @JoinColumn(name = "l_analytical_run_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private AnalyticalRun analyticalRun;

    /**
     * No-arg constructor.
     */
    public AnalyticalRunBinaryFile() {
    }

    /**
     * Constructor.
     *
     * @param content the content as a byte array
     */
    public AnalyticalRunBinaryFile(final byte[] content) {
        super(content);
    }

    public AnalyticalRun getAnalyticalRun() {
        return analyticalRun;
    }

    public void setAnalyticalRun(AnalyticalRun analyticalRun) {
        this.analyticalRun = analyticalRun;
    }
}
