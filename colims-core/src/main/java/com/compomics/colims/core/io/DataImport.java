package com.compomics.colims.core.io;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Niels Hulstaert
 */
public abstract class DataImport implements Serializable {

    private static final long serialVersionUID = 1L;
    
    protected static final String FASTA_FILE_PATH = "fasta_file";

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

    /**
     * Return the DataImport instance as a Map (key: property name; value:
     * property value).
     *
     * @return
     */
    public Map<String, String> asMap() {
        Map<String, String> properties = new HashMap<>();

        properties.put(FASTA_FILE_PATH, fastaFile.getAbsolutePath());

        return properties;
    }

}
