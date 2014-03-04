package com.compomics.colims.core.io;

import java.io.File;

/**
 *
 * @author Niels Hulstaert
 */
public abstract class DataImport {

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
