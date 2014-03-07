package com.compomics.colims.core.io;

import java.io.File;
import java.io.Serializable;

/**
 *
 * @author Niels Hulstaert
 */
public abstract class DataImport implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The fasta file
     */
    protected File fastaFile;

    public DataImport() {
    }

    public DataImport(File fastaFile) {
        this.fastaFile = fastaFile;
    }

    public File getFastaFile() {
        return fastaFile;
    }

    public void setFastaFile(File fastaFile) {
        this.fastaFile = fastaFile;
    }        

}
