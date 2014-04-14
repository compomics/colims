package com.compomics.colims.core.io;

import com.compomics.colims.model.FastaDb;
import java.io.Serializable;

/**
 *
 * @author Niels Hulstaert
 */
public abstract class DataImport implements Serializable {

    private static final long serialVersionUID = 1L;       

    /**
     * The fasta DB
     */
    protected FastaDb fastaDb;

    public DataImport() {
    }

    public DataImport(FastaDb fastaDb) {
        this.fastaDb = fastaDb;
    }

    public FastaDb getFastaDb() {
        return fastaDb;
    }

    public void setFastaDb(FastaDb fastaDb) {
        this.fastaDb = fastaDb;
    }      

}
