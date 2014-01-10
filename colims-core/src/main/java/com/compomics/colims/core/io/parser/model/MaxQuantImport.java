package com.compomics.colims.core.io.parser.model;

import java.io.File;

/**
 *
 * @author Davy
 */
public class MaxQuantImport {

    private File maxQuantFolder;
    private File fastaFileUsed;
    
    public MaxQuantImport(final File aMaxQuantFolder, final File aFastaFile){
        this.maxQuantFolder = aMaxQuantFolder;
        this.fastaFileUsed = aFastaFile;
    }

    public File getMaxQuantFolder() {
        return maxQuantFolder;
    }

    public void setMaxQuantFolder(final File maxQuantFolder) {
        this.maxQuantFolder = maxQuantFolder;
    }

    public File getFastaFileUsed() {
        return fastaFileUsed;
    }

    public void setFastaFileUsed(final File fastaFileUsed) {
        this.fastaFileUsed = fastaFileUsed;
    }
    
}
