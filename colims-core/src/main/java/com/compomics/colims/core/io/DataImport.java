package com.compomics.colims.core.io;

import com.compomics.colims.model.FastaDb;
import java.io.Serializable;

/**
 * This abstract class is the parent class for all data import types. It has the
 * FastaDb entity as class member, which is common regardless of the import type
 * (PeptideShaker, MaxQuant).
 *
 * @author Niels Hulstaert
 */
public abstract class DataImport implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The fasta DB entity.
     */
    protected FastaDb fastaDb;

    /**
     * No-arg constructor.
     */
    public DataImport() {
    }

    /**
     * Constructor.
     *
     * @param fastaDb the FastaDb entity
     */
    public DataImport(final FastaDb fastaDb) {
        this.fastaDb = fastaDb;
    }

    public FastaDb getFastaDb() {
        return fastaDb;
    }

    public void setFastaDb(final FastaDb fastaDb) {
        this.fastaDb = fastaDb;
    }

}
