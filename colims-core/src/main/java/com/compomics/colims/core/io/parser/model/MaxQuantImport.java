package com.compomics.colims.core.io.parser.model;

import java.io.File;

/**
 *
 * @author Davy
 */
public class MaxQuantImport {

    private File maxQuantFolder;
    private File fastaFileUsed;
    
    public MaxQuantImport(File aMaxQuantFolder,File aFastaFile){
        this.maxQuantFolder = aMaxQuantFolder;
        this.fastaFileUsed = aFastaFile;
    }

    public File getMaxQuantFolder() {
        return maxQuantFolder;
    }

    public void setMaxQuantFolder(File maxQuantFolder) {
        this.maxQuantFolder = maxQuantFolder;
    }

    public File getFastaFileUsed() {
        return fastaFileUsed;
    }

    public void setFastaFileUsed(File fastaFileUsed) {
        this.fastaFileUsed = fastaFileUsed;
    }
    
}
